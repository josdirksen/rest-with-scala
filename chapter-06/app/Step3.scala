package controllers

import org.restwithscala.common.model.{Note, Task, Person, Status => MStatus}
import org.restwithscala.common.service.TaskService
import play.api.data.validation.ValidationError
import play.api.http.Writeable
import play.api.mvc._
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

import javax.inject._

import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc.Results._
import play.api.routing.Router

class ErrorHandler @Inject() (env: Environment,
                              config: Configuration,
                              sourceMapper: OptionalSourceMapper,
                              router: Provider[Router]
                              ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onDevServerError(request: RequestHeader, exception: UsefulException) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }

  override def onProdServerError(request: RequestHeader, exception: UsefulException) = {
    Future.successful(
      InternalServerError("A server error occurred: " + exception.getMessage)
    )
  }
}

/**
 * - Action composition: Adding a logging action that wraps the actions.
 * - Perhaps content negotiation using Accepts extractor
 * - working with JSON
 * - custom error handler
 */
object Step3 extends Controller {

  // we use format, since our read and writes are symmetrical
  // this doesn't work for one parameter case classes
  implicit def notesFormat: Format[Note] = (
    (JsPath \ "id").format[Long] and
    (JsPath \ "content").format[String])(Note.apply, unlift(Note.unapply))

  implicit def statusReads: Reads[MStatus] =
    ((JsPath \ "status").read(
      minLength[String](3) andKeep
        filter(ValidationError("Status must be either New, In Progress or Closed"))
          ((b: String) => List("New", "In Progress", "Closed").contains(b))
    ))
    .map(MStatus(_))
  implicit def statusWrites: Writes[MStatus] = ((JsPath \ "status").write[String]).contramap((_.status))

  implicit def personReads: Reads[Person] = ((JsPath \ "name").read[String]).map(Person(_))
  implicit def personWrites: Writes[Person] = ((JsPath \ "name").write[String]).contramap((_.name))

  implicit def taskReads: Reads[Task] =  (
      (JsPath \ "id").read[Long] and
        (JsPath \ "title").read(minLength[String](3) andKeep maxLength[String](10)) and
        (JsPath \ "content").read[String] and
        (JsPath \ "assignedTo").readNullable[Person] and
        (JsPath \ "notes").read[List[Note]] and
        (JsPath \ "status").read[MStatus])(Task.apply _)

 implicit def taskWrites: Writes[Task] =  (
    (JsPath \ "id").write[Long] and
      (JsPath \ "title").write[String] and
      (JsPath \ "content").write[String] and
      (JsPath \ "assignedTo").writeNullable[Person] and
      (JsPath \ "notes").write[List[Note]] and
      (JsPath \ "status").write[MStatus])(unlift(Task.unapply))

    // use these if you want to have default serialization and deserialization.
    // doesn't allow easy validation however.
//  implicit val fmtNote = Json.format[Note]
//  implicit val fmtPerson = Json.format[Person]
//  implicit val fmtMStatus = Json.format[MStatus]
//  implicit val fmtTask = Json.format[Task]

  // simple implicit to convert our tasks to a simple string for now
  implicit def wTask: Writeable[Task] = Writeable(_.toString.getBytes, Some("application/txt"))
  implicit def wListTask: Writeable[List[Task]] = Writeable(b => Json.prettyPrint(Json.toJson(b)).getBytes, Some("application/json"))

  def createTask = Action.async { request =>
    val body: Option[JsResult[Task]] = request.body.asJson.map(_.validate[Task])
//    val body: Option[Task] = request.body.asJson.map(_.validate[Task])

    // option defines whether we have a json body or not.
    body match {
      case Some(task) =>
        // jsResult defines whether we have failures.
        task match {
          case JsSuccess(task, _) => TaskService.insert(task).map(b => Ok(Json.toJson(b)))
          case JsError(errors) => Future{BadRequest(errors.mkString("\n"))}
        }
      case None => Future{BadRequest("Body can't be parsed to JSON")}
    }
  }

  def getTasks = Action.async {
    TaskService.all.map(
      Ok(_)
        .withCookies(new Cookie("play","cookie"))
        .withHeaders(("header1" -> "header1value"))) // specialization if you want to add a date.
  }

  def getTask(id: Long) = Action.async {
    val task = TaskService.select(id);
    task.map({
      case Some(task) => Ok(task)
      case None => NotFound("")
    })
  }

  def deleteTask(id: Long) = Action.async {
    val task = TaskService.delete(id);

    // assume this task does something unexpected and throws an exception.
    throw new IllegalArgumentException("Unexpected argument");

    task.map({
      case Some(task) => Ok(task)
      case None => NotFound("")
    })
  }

  def updateTask(id: Long, title: String, person: Option[String], status: String) = Action.async { request =>
    val body: Option[String] = request.body.asText

    val updatedTask = TaskService.update(id, Task(
      id,
      title,
      body.getOrElse("No body provided"),
      person.map(Person(_)),
      List[Note](),
      MStatus(status)))

    updatedTask.map({
      case Some(task) => Ok(task)
      case None => NotFound("")
    })
  }

  def notImplemented(path: String) = Action {
    NotFound(s"Specified route not found: $path")
  }
}