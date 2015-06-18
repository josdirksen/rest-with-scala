package org.restwithscala.chapter2.routes

import com.twitter.finagle.httpx.Request
import com.twitter.finagle.{Filter, SimpleFilter, Service, Httpx}
import com.twitter.util.Future
import io.finch.{HttpRequest, HttpResponse}
import io.finch.request.RequestReader
import io.finch.route._
import io.finch.request._
import io.finch.response._
import org.restwithscala.common.model.{Person, Task}
import io.finch.{Endpoint => _, _}

/**
 * When implementing a service using a specific framework it is probably most interesting to
 * show how this is done using specific constructs. So in the case of Finch we'll use the
 * following:
 *
 * 1. Use matchers for URL patterns and Verbs
 * 2. Use extractors to access path segemnts
 * 3. Combine these together using / combinators
 *
 */
object FinchRoutes extends App {

  // define a matcher
  val matchVerb : Matcher = Get

  // define matcher on path name and add another path element
  val matchPath : Matcher = "hello"
  val fullPath = matchPath / "world"

  // additional path matcher
  val matchSingle: Matcher = *
  val matchMultiplePathElement: Matcher = **

  // extractors from path
  val intExtractor = long("id");

//  val handleExceptions = new SimpleFilter[Request, HttpResponse] {
//    def apply(req: Request, service: Service[Request, HttpResponse]): Future[HttpResponse] =
//      handleExceptions
//  }

  // A Finagle filter that authorizes a request: performs conversion `HttpRequest` => `AuthRequest`.
  val authorize = new Filter[HttpRequest, HttpResponse, HttpRequest, HttpResponse] {
    def apply(req: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = {
      println(req)
      service(req)
    }
  }
  // run the server, with the path.
//  Httpx.serve(":8080", (matchVerb / fullPath / intExtractor /> "Hello, Finch!").toService)

  // :+: is needed since we use a case class that extends service
  // if we used a service we could have used |


  val api = (
    (matchVerb / fullPath / intExtractor /> PostUserTicket) :+:
      (matchVerb / "test" / intExtractor /> PostUserTicket2)
    ).toService



  val server = Httpx.serve(":8080", authorize ! api)

  println("Press <enter> to exit.")
  Console.in.read.toChar




  server.close()


}




// A REST service that add a ticket to a given user `userId`.
case class PostUserTicket(userId: Long) extends Service[Request, String] {
  println(userId)
  def apply(req: Request): Future[String] = Future {userId toString}
}

case class PostUserTicket3(userId: Long) extends Service[Request, String] {
  println(userId)
  def apply(req: Request): Future[String] = Future {userId toString}
}

case class Blaat(t: String, t2: String)

case class PostUserTicket2(userId: Long) extends Service[Request, HttpResponse] {

  val doSomethingWithRequest: RequestReader[Task] = {
    ( RequestReader.value(userId) ::
      param("title") ::
      param("content") ::
      RequestReader.value(None:Option[Person])
      ).as[Task]
    }


  val doSomethingElseWithRequest: RequestReader[String] =
    for {
      foo <- param("test2")
    } yield foo


//  def apply(req: Request): Future[String] = doSomethingWithRequest(req)
  def apply(req: Request): Future[HttpResponse] = {
    val res = doSomethingWithRequest(req) handle {
      case _ => Task(10l, "empty", "empty", None)
    }

    for {
      blaat <- res
    } yield Ok(blaat.title)
  }

}

