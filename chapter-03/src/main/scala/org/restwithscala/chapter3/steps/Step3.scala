package org.restwithscala.chapter3.steps

import org.restwithscala.common.model._
import org.restwithscala.common.service.TaskService
import unfiltered.directives.{Directive => UDirective, ResponseJoiner, data}
import unfiltered.request._
import unfiltered.response._
import unfiltered.netty._
import scala.concurrent.{ExecutionContext}
import scala.concurrent.Future
import scalaz._
import scalaz.std.scalaFuture._

/**
 * In this step we'll focus on parameter and body extraction.
 * - Extract a task based on the provided parameters
 */
object Step3 extends App {

  /**
   * Object holds all the implicit conversions used by Unfiltered to
   * process the incoming requests.
   */
  object Conversions {

    case class BadParam(msg: String) extends ResponseJoiner(msg)(
      msgs =>
        BadRequest ~> ResponseString(msgs.mkString("","\n","\n"))
    )

    implicit def requiredJoin[T] = data.Requiring[T].fail(name =>
      BadParam(name + " is missing")
    )

    implicit val toStringInterpreter = data.as.String

    implicit val implyIntValue =
      data.as.String ~> data.as.Int.fail { (k,v) =>
        BadRequest ~> ResponseString(
          s"'$v' is not a valid int for $k"
        )
      }

    val allowedStatus = Seq("new", "done", "progress")
    val inStatus = data.Conditional[String](allowedStatus contains (_)).fail(
      (k, v) => BadParam(s" value not allowed: $v, should be one of ${allowedStatus.mkString(",")} ")
    )
  }


  // setup the name so we can stop the server
  Thread.currentThread().setName("swj");
  // used by the futures
  implicit def ec = ExecutionContext.Implicits.global

  // This plan contains the complete API. Works asynchronously
  // directives by default don't work with futures. Using the d2
  // directives, we can wrap the existing directives and use the
  // async plan.
  @io.netty.channel.ChannelHandler.Sharable
  object api extends async.Plan with ServerErrorResponse {

    // Import the required d2 diretives so we can work
    // with Futures and directives together. We also bring
    // the implicit directive conversions into scope.
    val D = d2.Directives[Future]
    import D._
    import D.ops._
    import Conversions._

    // maps the requests so that we can use directives with the
    // async intent. In this case we pass on the complete request
    // to the partial function
    val MappedAsyncIntent = d2.Async.Mapping[Any, HttpRequest[Any]] {
      case req: HttpRequest[Any] => req
    }

    // d2 provides a function to convert standard unfiltered
    // directives to d2 directives. This implicit conversion
    // makes using this easier by adding a toD2 function to
    // the standard directives.
    implicit class toD2[T, L, R](s: UDirective[T, L, R]) {
      def toD2 = fromUnfilteredDirective(s)
    }

    // our plan requires an execution context,
    def executionContext = ec
    def intent = MappedAsyncIntent {
      case Path("/search") => handleSearchCombinedError
//      case Path("/search") => handleSearchSingleError
    }

    def handleSearchSingleError = for {
      status <- inStatus.named("status").toD2
      text1 <- data.as.Required[String].named("text").toD2
      tasks <- TaskService.search(status.get,Some(text1)).successValue
    } yield {
        Ok ~> ResponseString(tasks.toString())
    }

    def handleSearchCombinedError = for {
      p <- ((inStatus.named("status")) &
            (data.as.Required[String] named "text")
           ).toD2
      tasks <- TaskService.search(p._1.get, Some(p._2)).successValue
    } yield {
        Ok ~> ResponseString(tasks.toString())
      }
  }

  unfiltered.netty.Server.http(8080).handler(api).run
  dispatch.Http.shutdown()
}
