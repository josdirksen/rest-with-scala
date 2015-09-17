package org.restwithscala.chapter5.steps

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import org.restwithscala.common.model.{SearchParams, Person, Status, Task}
import org.restwithscala.common.service.TaskService

import scala.util.{Failure, Success}

/**
 * Final step, make sure that there are some validation exceptions and rejections to show how to process
 * these.
 */
object AkkaHttpDSLStep3 extends App {

  import system.dispatcher

  // used to run the actors
  implicit val system = ActorSystem("my-system")
  // materialises underlying flow defintion into a set of actors
  implicit val materializer = ActorMaterializer()

  // This implicit is used to convert the Task to a HttpEntity so
  // that akka-http can return it.
  implicit val StringMarshaller: ToEntityMarshaller[Task] =
    Marshaller.opaque { s ⇒ HttpEntity(ContentType(`text/plain`), s.toString) }

  implicit val ListMarshaller: ToEntityMarshaller[List[Task]] =
    Marshaller.opaque { s ⇒ HttpEntity(ContentType(`text/plain`), s.toString) }

  val sampleHeader: HttpHeader = (HttpHeader.parse("helloheader", "hellovalue") match {
    case ParsingResult.Ok(header, _) => Some(header)
    case ParsingResult.Error(_) => None
  }).get

  // if defined implicitly it will be used for the complete route.
  //  implicit def customRejectionHandler =
  val customRejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
      case ValidationRejection(cause, exception) =>
        complete(HttpResponse(StatusCodes.BadRequest, entity = s"Validation failed: $cause"))

    }.result()

//   implicit def customExceptionHandler = ExceptionHandler {
  val customExceptionHandler = ExceptionHandler {
    case _: IllegalArgumentException =>
      // you can easily access any request parameter here using extractors.
      extractUri { uri =>
        complete(HttpResponse(StatusCodes.InternalServerError, entity = s"The function on $uri isn't implemented yet"))
      }
  }

  val route =
  // handle the /tasks part of the request
    path("search") {
      handleExceptions(customExceptionHandler) {
        failWith(new IllegalArgumentException("Search call not implemented"))
      }
    } ~
      path("tasks") {
        get {
          complete {
            // our taskservice returns a Future[List[String]], we map
            // this to a single Future[String] instance that can returned
            // automatically by akka-http
            TaskService.all.map(_.foldLeft("")((z, b) => z + b.toString + "\n"))
          }
        } ~
          handleRejections(customRejectionHandler) {
            ((post) & (parameters("title", "person".?, "status" ? "new"))) { (title, assignedTo, status) => {
              (entity(as[String])) { body => {
                // before we complete the request, we can also return a rejection. We'll do this if the input doesn't validate
                // correctly. Another good place to do this would be in the case class itself. The validate will result in a
                // validationrejection.
                (validate(title.length > 10, "Title must be longer than 10 characters") &
                  validate(List("new", "done", "in progress").contains(status), "Status must be either 'new', 'done' or 'in progress'") &
                  validate(body.length > 0, "Title must be longer than 10 characters")
                  ) {
                  complete {
                    TaskService.insert(Task(-1, title, body, assignedTo.map(Person(_)), List.empty, Status(status)))
                  }
                }
              }
              }
            }
            }
          }
      } ~ {
      // we handle the "/tasks/id separately"
      path("tasks" / LongNumber) {
        task => {
          entity(as[String]) { body => {
            put {
              complete {
                s"Update an existing task with id: $task and body"
              }
            }
          }
          } ~
            get {
              (setCookie(HttpCookie(name = "hello", value = "world")) &
                respondWithHeader(sampleHeader)
                ) {
                onComplete(TaskService.select(task)) {
                  case Success(Some(value)) => complete(value)
                  case Success(None) => complete(StatusCodes.NotFound, "No tasks found")
                  case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
                }
              }
            }
        } ~ {
          // We can manually add this rejection.
          reject(MethodRejection(HttpMethods.GET), MethodRejection(HttpMethods.PUT))
        }
      }
    }


  // start the server
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  // wait for the user to stop the server
  println("Press <enter> to exit.")
  Console.in.read.toChar

  // gracefully shutdown the server
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.shutdown())
}


//
//
//notFound { "Route not implemented" }
//
//post("/tasks") { s"create a new task with body ${request.body}"}
//get("/tasks") { "Get all the tasks" }
//get("/tasks/:id") {
//Try { params("id").toInt }  match {
//case Success(id) => s"Get task with id: ${params("id")} "
//case Failure(e) => BadRequest(reason = "Can't parse id")
//}
//}
//delete("/tasks/:id") { s"Delete task with id: ${params("id")} "}
//put("/tasks/:id") { s"Update an existing task " +
//s"with id:  ${params("id")} " +
//s"and body ${request.body}"}