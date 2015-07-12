package org.restwithscala.chapter3.steps

import unfiltered.request._
import unfiltered.response._

/**
 * - Explain pass and onpass
 * - Explain segment matching
 * - Explain path matching
 * - Explain composition of intents
 */
object Step1 extends App {

  // explicitly set the thread name. If not the server can't be stopped easily
  // when started from an IDE
  Thread.currentThread().setName("swj");

  object api extends unfiltered.filter.Plan {
    def intent = taskApi.onPass(fallback)

    def taskApi = unfiltered.filter.Intent {
      case GET(Path("/tasks")) => ResponseString(s"Get all tasks" )
      case GET(Path(Seg("tasks" :: id :: Nil))) => ResponseString(s"Get a single task with id: $id" )
      case DELETE(Path(Seg("tasks" :: id :: Nil))) => ResponseString(s"Delete an existing task with id $id")

      case req @ Path("/tasks") => req match {
        case POST(_) => ResponseString(s"Create a new " +
          s"task with body ${Body.string(req)}")
        case PUT(_) => ResponseString(s"Update a new task with" +
          s" body ${Body.string(req)}")
        case _ => Pass
      }

      case _ => Pass
    }

    def fallback = unfiltered.filter.Intent {
      case _ => NotImplemented ~> ResponseString("Function is not implemented")
    }
  }
  

  unfiltered.jetty.Server.http(8080).plan(api).run()

  println("Press <enter> to exit.")
  Console.in.read.toChar

}
