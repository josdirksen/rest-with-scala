package org.restwithscala.chapter5.steps

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCodes, HttpMethods}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.MethodRejection
import akka.stream.ActorMaterializer

/**
 * First step. This step just shows a number of simple directives. We show the following standard things here:
 *
 *
 * Complete the request by returning the value of requestContext.complete(...)
 * Reject the request by returning the value of requestContext.reject(...) (see Rejections)
 * Fail the request by returning the value of requestContext.fail(...) or by just throwing an exception (see Exception Handling)
 * Do any kind of asynchronous processing and instantly return a Future[RouteResult] to be eventually completed later
 */
object AkkaHttpDSLStep1 extends App {

  // used to run the actors
  implicit val system = ActorSystem("my-system")
  // materialises underlying flow defintion into a set of actors
  implicit val materializer = ActorMaterializer()

  val route =
    // handle the /tasks part of the request
    path("tasks") {
      get {
        complete { "Return all the tasks" }
      } ~
      post {
        complete { s"Create a new task" }
      } // any othe request is also rejected.
    } ~ { // we handle the "/tasks/id separately"
      path("tasks" / IntNumber) {
        task => {
          entity(as[String]) { body => {
            put { complete { s"Update an existing task with id: $task and body: $body" } }
          } ~
            get { complete { s"Get an existing task with id : $task and body: $body" } }
          } ~ {
            // We can manually add this rejection.
            reject(MethodRejection(HttpMethods.GET), MethodRejection(HttpMethods.PUT))
          }
        }
      }
    }


  // start the server
  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  // wait for the user to stop the server
  println("Press <enter> to exit.")
  Console.in.read.toChar

  // gracefully shutdown the server
  import system.dispatcher
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.shutdown())
}