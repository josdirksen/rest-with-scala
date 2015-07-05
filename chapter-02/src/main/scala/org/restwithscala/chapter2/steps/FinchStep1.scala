package org.restwithscala.chapter2.steps

import com.twitter.finagle.Httpx
import io.finch.request._
import io.finch.route._
import io.finch.{Endpoint => _}

/**
 * This first step for finch shows how the routers for the REST endpoints
 * are configured. In this example you'll learn about matchers, extractors and
 * how to combine routes together.
 *
 * We support the following routes:
 * GET  /tasks Get a list of all the tasks
 * GET /tasks/id: Get a single task
 * POST /tasks: Create a new task
 * PUT  /tasks/id Update an existing id
 * DELETE /tasks/:id Remove an existing task
 * POST /tasks/:id/owner Add owner to task
 * GET /tasks/:id/owner return the owner of the task
 * POST /tasks/:id/status update the status of a task
 * GET /tasks/:id/status get current status of the task
 * GET /tasks/:id/notes Get all notes for a task
 * POST /tasks/:id/notes Add a note to a task
 * GET /tasks/:id/notes/:id get a single note
 * DELETE /tasks/:id/notes/:id delete a single note
 *
 * 1. Use matchers for URL patterns and Verbs
 * 2. Use extractors to access path segemnts
 * 3. Combine these together using / combinators
 * 4. Combine routes together with the | combinator
 */
object FinchStep1 extends App {

  val matchPath : Matcher = "hello"

  // handle a single post using a RequestReader
  val taskCreateAPI = Post / "tasks" /> (
    for {
      bodyContent <- body
    } yield s"created task with: $bodyContent")

  // use matchers and extractors to determine which route to call
  val taskAPI =
     Get / "tasks" /> "Get a list of all the tasks" |
     Get / "tasks" / long /> ( id => s"Get a single task with id: $id" ) |
     Put / "tasks" / long /> ( id => s"Update an existing task with id  $id to " ) |
     Delete / "tasks" / long /> ( id => s"Delete an existing task with $id" ) |
     Post / "tasks" / long / "owner" /> ( id => s"Set the owner of an existing task with $id " ) |
     Get / "tasks" / long / "owner" /> ( id => s"Get the owner of task with $id" ) |
     Post / "tasks" / long / "status" /> ( id => s"Set the status of taskk with $id to " ) |
     Get / "tasks" / long / "status" /> ( id => s"Get the status of an existing task with $id" ) |
     Get / "tasks" / long / "notes" /> ( id => s"Get all notes for task with id: $id" ) |
     Post / "tasks"/ long /> ( id => s"Create a new note for task with id $id to " ) |
     Put / "tasks" / long / "notes" / long /> ( (taskId, noteId) => s"Update an existing note with noteid:  $noteId with " ) |
     Get / "tasks" / long / "notes" / long /> ( (taskId, noteId) => s"Get note with id:$noteId for task with id: $taskId" ) |
     Delete / "tasks" / long / "notes" / long /> ( (taskId, noteId) => s"Delete an existing note with $noteId" )

  // simple server, which combines the two routes and creates a service.
  val server = Httpx.serve(":8080", (taskAPI :+: taskCreateAPI).toService )

  println("Press <enter> to exit.")
  Console.in.read.toChar

  server.close()
}