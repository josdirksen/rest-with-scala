package org.restwithscala.chapter4.steps

import javax.servlet.ServletContext

import akka.actor.ActorSystem
import org.restwithscala.chapter4.JettyLauncher
import org.restwithscala.common.model.{Status, Task}
import org.restwithscala.common.service.TaskService
import org.scalatra._
import org.slf4j.LoggerFactory

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success, Try}


// run this example, by specifying the name of the bootstrap to use
object ScalatraRunnerStep2 extends App {
  JettyLauncher.launch("org.restwithscala.chapter4.steps.ScalatraBootstrapStep2")
}

class ScalatraBootstrapStep2 extends LifeCycle {

  val system = ActorSystem()

  override def init(context: ServletContext) {
    context.mount(new ScalatraStep2(system), "/*")
  }

  override def destroy(context: ServletContext) {
    system.shutdown()
  }
}

/**
 * The servlet containing the route to execute
 */
class ScalatraStep2(system: ActorSystem) extends ScalatraServlet with FutureSupport {

  protected implicit def executor: ExecutionContext = system.dispatcher
  val Log = LoggerFactory.getLogger(this.getClass)

  before("/*") {
    Log.info(s"Processing request for: ${params("splat")}")
  }

  after(""""^\/tasks\/(.*)"""".r) {
    Log.info(s"Processed request for tasks: ${params("captures")}")
  }

  notFound {
    "Route not implemented"
  }

  post("/tasks") {
    new AsyncResult() { // we use a asyncresult snce we access the parameters
      override val is  = {
        // convert provided request parameters to a task and store it
        val createdTask = TaskService.insert(Task(
          -1,
          params.getOrElse("title", halt(status = 400, reason="Title is required")),
          request.body,
          None,
          List.empty,
          Status(params.getOrElse("status", "new"))))

        // the result is a Future[Task], map this to a string
        createdTask.map(_.toString)
      }
    }
  }

  get("/tasks") {
      TaskService.all.map(_.toString) // directly return future since we don't access request
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

  delete("/tasks/:id") {
    new AsyncResult() {
      override val is = Try { params("id").toLong } match {
        case Success(id) => TaskService.delete(id).map(_.toString)
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


