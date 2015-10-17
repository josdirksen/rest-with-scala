package org.restwithscala.chapter3.steps

import org.restwithscala.common.model.{Note, Status, Task}
import org.restwithscala.common.service.TaskService
import unfiltered.directives.Directives
import unfiltered.netty._
import unfiltered.request.Params.Extract
import unfiltered.request._
import unfiltered.response._
import Directives._

import scala.concurrent.{ExecutionContext, Future}

/**
 * In this step we'll focus on parameter and body extraction.
 * - Extract a task based on the provided parameters
 */
object UnfilteredChapter7 extends App {

  val MediaType = "application/vnd.restwithscala.task+json"

  implicit def ec = ExecutionContext.Implicits.global

  @io.netty.channel.ChannelHandler.Sharable
  object api extends future.Plan with ServerErrorResponse {

    def executionContext = ec

    def intent =  {
      case GET(Path("/tasks")) => Future {ResponseString(s"Get all tasks" )}
      case GET(Path(Seg("tasks" :: id :: Nil))) => Future {ResponseString(s"Get a single task with id: $id" )}
      case DELETE(Path(Seg("tasks" :: id :: Nil))) => Future {ResponseString(s"Delete an existing task with id $id")}

      case req @ Path("/tasks") => (req, req) match {
        case (req @ POST(_), (RequestContentType(MediaType))) =>
          handleCreateTask(req).map(Ok ~> ResponseHeader("content-type", Set(MediaType)) ~> ResponseString(_))
        case (PUT(_),_) => handleCreateTask(req).map(Ok ~> ResponseHeader("content-type", Set(MediaType)) ~> ResponseString(_))
        case _ => Future {Pass}
      }
      case _ => Future{Pass}
    }
  }

  @io.netty.channel.ChannelHandler.Sharable
  object fallback extends future.Plan with ServerErrorResponse {
    def executionContext = ec
    def intent =  {
      case _ => Future {NotImplemented ~> ResponseString("Function is not implemented")}
    }
  }

  def handleCreateTask(req: HttpRequest[Any]): Future[String] = {
    val task = requestToTask(TaskService.nextTaskId(), req)
    val insertedTask = task map(TaskService.insert(_).map(_.toString))

    insertedTask.getOrElse(Future{"Error inserting"})
  }

  def paramExtractor(param: String): Extract[String] = {
     new Extract[String]( param, Params.first ~> Params.nonempty ~> Params.trimmed)
  }

  def requestToTask(id: Long, req: HttpRequest[Any]): Option[Task] = {
    val title = paramExtractor("title")

    req match {
      case Params(title(param)) => Some(Task(
        id,
        param,
        Body.string(req),
        None,
        List.empty[Note],
      Status("")))

      case _ => None
    }
  }

  unfiltered.netty.Server.http(8080).handler(api).handler(fallback).run
  dispatch.Http.shutdown()
}
