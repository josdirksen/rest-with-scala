package org.restwithscala

import argonaut.{EncodeJson, DecodeJson}
import com.twitter.util
import org.restwithscala.common.model.{Status, Person, Note, Task}

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Failure, Success, Try}
import argonaut._, Argonaut._

/**
 * Set of helper functions to deal with twitter/scala futures.
 *
 * Based on: http://stackoverflow.com/questions/30317473/convert-scala-future-to-twitter-future
 */
package object chapter2 {

  implicit def scalaToTwitterTry[T](t: Try[T]): util.Try[T] = t match {
    case Success(r) => util.Return(r)
    case Failure(ex) => util.Throw(ex)
  }

  implicit def scalaToTwitterFuture[T](f: Future[T])(implicit ec: ExecutionContext): util.Future[T] = {
    val promise = util.Promise[T]()
    f.onComplete(promise update _)
    promise
  }

  implicit def personDecoding: DecodeJson[Person] =
    jdecode1L(Person.apply)("name")

  implicit def personEncoding: EncodeJson[Person] =
    jencode1L((u: Person) => (u.name))("name")

  implicit def statusDecoding: DecodeJson[Status] =
    jdecode1L(Status.apply)("status")

  implicit def statusEncoding: EncodeJson[Status] =
    jencode1L((u: Status) => (u.status))("status")

  implicit def noteDecoding: DecodeJson[Note] =
    jdecode2L(Note.apply)("id", "content")

  implicit def noteEncoding: EncodeJson[Note] =
    jencode2L((u: Note) => (u.id, u.content)
  )("id", "content")

  implicit def taskDecoding: DecodeJson[Task] =
    jdecode6L(Task.apply)("id", "title", "content", "assignedTo", "notes", "status")

  implicit def taskEncoding: EncodeJson[Task] = jencode6L(
    (u: Task) => (u.id, u.title, u.content, u.assignedTo, u.notes, u.status)
  )("id", "title", "content", "assignedTo", "notes", "status" )
}
