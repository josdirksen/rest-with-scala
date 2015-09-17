package org.restwithscala.chapter5.steps

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.MethodRejection
import akka.stream.ActorMaterializer
import org.restwithscala.common.model.{Person, Status, Task}
import org.restwithscala.common.service.TaskService

import scala.util.{Failure, Success}

/**
 * First step. This step just shows a number of simple directives. We show the following standard things here:
 *
 *
 * Complete the request by returning the value of requestContext.complete(...)
 * Reject the request by returning the value of requestContext.reject(...) (see Rejections)
 * Fail the request by returning the value of requestContext.fail(...) or by just throwing an exception (see Exception Handling)
 * Do any kind of asynchronous processing and instantly return a Future[RouteResult] to be eventually completed later
 */
object AkkaHttpDSLStep2 extends App {

  import system.dispatcher
  // used to run the actors
  implicit val system = ActorSystem("my-system")
  // materialises underlying flow defintion into a set of actors
  implicit val materializer = ActorMaterializer()

  // This implicit is used to convert the Task to a HttpEntity so
  // that akka-http can return it.
  implicit val StringMarshaller: ToEntityMarshaller[Task] =
    Marshaller.opaque { s ⇒ HttpEntity(ContentType(`text/plain`), s.toString) }

  val sampleHeader: HttpHeader = (HttpHeader.parse("helloheader","hellovalue") match {
    case ParsingResult.Ok(header, _) => Some(header)
    case ParsingResult.Error(_) => None
  }).get


  val route =
  // handle the /tasks part of the request
    path("tasks") {
      get {
        complete {
          // our taskservice returns a Future[List[String]], we map
          // this to a single Future[String] instance that can returned
          // automatically by akka-http
          TaskService.all.map(_.foldLeft("")((z, b) => z + b.toString + "\n"))
        }
      } ~
        ((post) & (parameters("title", "person".?, "status" ? "new"))) { (title, assignedTo, status) => {
          (entity(as[String])) { body => {
            complete {
              val createdTask = TaskService.insert(Task(-1, title, body, assignedTo.map(Person(_)), List.empty, Status(status)))
              createdTask.map(_.toString)
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
            ( setCookie(HttpCookie(name = "hello", value = "world")) &
              respondWithHeader(sampleHeader)
              ) {
              onComplete(TaskService.select(task)) {
                case Success(Some(value)) => complete(value)
                case Success(None) => complete(StatusCodes.NotFound, "No tasks found")
                case Failure(ex)  => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
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