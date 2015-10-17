package org.restwithscala.chapter4.steps

import javax.servlet.ServletContext

import _root_.akka.actor.{ActorSystem, Props}
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.commands.{FieldDescriptor, Field, ParamsOnlyCommand}
import org.scalatra.json._
import org.restwithscala.chapter4.JettyLauncher
import org.restwithscala.common.model.{Note, SearchParams, Status, Task}
import org.restwithscala.common.service.TaskService
import org.scalatra._
import org.scalatra.validation.Validators.PredicateValidator
import org.slf4j.LoggerFactory
import scalaz._
import Scalaz._
import scala.util.control.Exception._

import org.scalatra.validation._

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success, Try}

// the Scalatra command handlers
import org.scalatra.commands._

// our own Command classes
import commands._

import SearchCommands._


// run this example, by specifying the name of the bootstrap to use
object ScalatraRunnerChapter7 extends App {
  JettyLauncher.launch("org.restwithscala.chapter4.steps.ScalatraBootstrapChapter7")
}

class ScalatraBootstrapChapter7 extends LifeCycle {

  val system = ActorSystem()

  override def init(context: ServletContext) {
    context.mount(new ScalatraChapter7(system), "/*")
  }

  override def destroy(context: ServletContext) {
    system.shutdown()
  }
}

/**
 * The servlet containing the route to execute
 */
class ScalatraChapter7(system: ActorSystem) extends ScalatraServlet
                  with FutureSupport
                  with JacksonJsonSupport with CommandSupport {

  val MediaType = "application/vnd.restwithscala.task+json"

  override type CommandType = SearchTasksCommand

  protected implicit val jsonFormats: Formats = DefaultFormats

  protected implicit def executor: ExecutionContext = system.dispatcher
  val Log = LoggerFactory.getLogger(this.getClass)

  // check the content type.
  before("/tasks") {
    (request.getMethod, request.contentType) match {
      case ("POST", Some(MediaType)) => // do nothing
      case ("POST", _) => halt(status = 400, reason = "Unsupported Mimetype")
      case (_,_) => // do nothing, since it isn't a post
    }
  }

  after("/task") {
    request.getMethod match {
      case "POST" => response.setContentType(MediaType)
      case _ => // do nothing, since it isn't a post
    }
  }

  // after the first check has passed convert it to json, if not it doesn' work.
  before("/*") {
    contentType = formats("json")
    Log.info(s"Processing request for: ${params("splat")}")
  }

  after("""^\/tasks\/(.*)""".r) {
    Log.info(s"Processed request for tasks: ${params("captures")}")
  }

  notFound {
    "Route not implemented"
  }



  post("/tasks") {
    new AsyncResult() { // we use a asyncresult snce we access the parameters
      override val is  = {
        // convert provided request parameters to a task and store it
        TaskService.insert(parsedBody.extract[Task])
      }
    }
  }

  get("/tasks") {
      TaskService.all // we use json4s serialization to json.
  }

  get("/tasks/:id") {
    // we use asyncresult, so that we can access properties from the
    // request in our Future.
    new AsyncResult() {
      override val is = Future {
        Try { params("id").toInt } match {
          case Success(id) => s"Get task with id: $id "
          case Failure(e) => BadRequest(reason = s"Can't parse id: ${e.getMessage}")
        }
      }
    }
  }

  get("/tasks/search") {
    new AsyncResult() {
      override val is = (command[SearchTasksCommand] >> (TaskServiceWrapper.WrapSearch(_))).fold (
        errors => halt(400, errors),
        tasks => tasks
      )
    }
  }

  delete("/tasks/:id") {
    new AsyncResult() {
      override val is = Try { params("id").toLong } match {
        case Success(id) => TaskService.delete(id)
        case Failure(e) => Future{BadRequest(reason = s"Can't parse id: ${e.getMessage}")}
      }
    }
  }

  put("tasks/:id") {
    new AsyncResult() {
      override val is =
        Try {
          params("id").toLong
        } match {
          case Success(id) => {
            TaskService.update(id, Task(
              id,
              params.getOrElse("title", halt(status = 400, reason = "Title is required")),
              request.body,
              None,
              List.empty,
              Status(params.getOrElse("status", "new"))))
          }
          case Failure(e) => Future {BadRequest(reason = s"Can't parse id: ${e.getMessage}")}
        }
    }
  }
}