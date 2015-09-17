package controllers

import org.restwithscala.common.model.{Note, Task, Person, Status => MStatus}
import org.restwithscala.common.service.TaskService
import play.api.http.Writeable
import play.api.mvc._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

/**
 * In this step we use the following:
 *
 *   - manipulate the result:
 *    - Set content-type using as. v
 *    - Set custom headers v
 *    - Set cookies v
 *
 *   - Connect service v
 *   - Use async v
 *   - Use default serialization v
 *
 */
object Step2 extends Controller {

  // simple implicit to convert our tasks to a simple string for now
  implicit def wTask: Writeable[Task] = Writeable(_.toString.getBytes, Some("application/text"))
  implicit def wListTask: Writeable[List[Task]] = Writeable(_.mkString("\n").getBytes, Some("application/text"))

  def createTask(title: String, person: Option[String], status: String) = Action.async { request =>
    val body: Option[String] = request.body.asText

    println(request.id);


    val createdTask = TaskService.insert(Task(
      -1,
      title,
      body.getOrElse("No body provided"),
      person.map(Person(_)),
      List[Note](),
      MStatus(status)))

    createdTask.map(Ok(_))
  }

  def getTasks = Action.async {
    TaskService.all.map(
      Ok(_)
        .as("application/text")
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