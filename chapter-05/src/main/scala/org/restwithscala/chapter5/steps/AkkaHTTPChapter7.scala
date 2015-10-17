package org.restwithscala.chapter5.steps

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ResponseEntity
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling._
import akka.http.scaladsl.model.HttpHeader.ParsingResult
import akka.http.scaladsl.model.MediaType.Encoding
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.MediaTypes
import akka.http.scaladsl.model.headers.{`Content-Type`, HttpCookie}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.util.FastFuture
import akka.stream.{Materializer, ActorMaterializer}
import akka.util.ByteString
import org.restwithscala.common.model._
import org.restwithscala.common.service.TaskService
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.unmarshalling._

import scala.util.{Failure, Success}


//object customConversion {
//  val `application/vnd.restwithscala.task+json` = MediaType.custom("application/vnd.restwithscala.task+json", MediaType.Encoding.Fixed(HttpCharsets.`UTF-8`))

//  implicit def pp[T]: FromRequestUnmarshaller[]


//  Unmarshaller.strict[HttpRequest, Task] { req =>
//    read[Task]()
//  }
//    Unmarshaller[HelloFoo](`application/vnd.ww.v1.foo+json`) {
//      case HttpEntity.NonEmpty(contentType, data) =>
//        read[HelloFoo](data.asString)
//    }

//    Unmarshaller[HttpRequest, T]
//implicit def messageUnmarshallerFromEntityUnmarshaller[T](implicit um: FromEntityUnmarshaller[T]): FromMessageUnmarshaller[T] =
//  Unmarshaller { implicit ec ⇒ request ⇒ um(request.entity) }


//  trait AkkaJSONProtocol2 extends DefaultJsonProtocol {
//    implicit val statusFormat = jsonFormat1(Status.apply)
//    implicit val noteFormat = jsonFormat2(Note.apply)
//    implicit val personFormat = jsonFormat1(Person.apply)
//    implicit val taskFormat = jsonFormat6(Task.apply)
//  }
//
//
//
//
//  implicit def ss[T](implicit reader: RootJsonReader[T], mat: Materializer): FromEntityUnmarshaller[T] =
//    bllat.map(jsonReader[T].read)
//  implicit def bllat(implicit mat: Materializer): FromEntityUnmarshaller[JsValue] =
//    Unmarshaller.byteStringUnmarshaller.forContentTypes(`application/vnd.restwithscala.task+json`).mapWithCharset { (data, charset) ⇒
//      val input =
//        if (charset == HttpCharsets.`UTF-8`) ParserInput(data.toArray)
//        else ParserInput(data.decodeString(charset.nioCharset.name)) // FIXME: identify charset by instance, not by name!
//      JsonParser(input)
//    }
//}

/**
 * Add JSON
 */
object AkkaHttpChapter7 extends App with AkkaJSONProtocol {

  val CustomContentType = MediaType.custom("application/vnd.restwithscala.task+json",Encoding.Fixed(HttpCharsets.`UTF-8`))

  def mapEntity(entity: ResponseEntity): ResponseEntity = entity match {
    case HttpEntity.Strict(contentType, data) =>
      HttpEntity.Strict(CustomContentType, data)
    case _ => throw new IllegalStateException("Unexpected entity type")
  }

  import system.dispatcher

  // used to run the actors
  implicit val system = ActorSystem("my-system")
  // materialises underlying flow defintion into a set of actors
  implicit val materializer = ActorMaterializer()

  val sampleHeader: HttpHeader = (HttpHeader.parse("helloheader", "hellovalue") match {
    case ParsingResult.Ok(header, _) => Some(header)
    case ParsingResult.Error(_) => None
  }).get

  // if defined implicitly it will be used for the complete route.
  //  implicit def customRejectionHandler =
  val customRejectionHandler =
    RejectionHandler.newBuilder()
      .handle {
      case ValidationRejection(cause, exception) =>
        complete(HttpResponse(StatusCodes.BadRequest, entity = s"Validation failed: $cause"))

    }.result()

//   implicit def customExceptionHandler = ExceptionHandler {
  val customExceptionHandler = ExceptionHandler {
    case _: IllegalArgumentException =>
      // you can easily access any request parameter here using extractors.
      extractUri { uri =>
        complete(HttpResponse(StatusCodes.InternalServerError, entity = s"The function on $uri isn't implemented yet"))
      }
  }


  val route =
  // handle the /tasks part of the request
    path("search") {
      handleExceptions(customExceptionHandler) {
        failWith(new IllegalArgumentException("Search call not implemented"))
      }
    } ~
      path("tasks") {
        get {
          complete {
            // intellij might show this as an error, this, however, works.
            TaskService.all
          }
        } ~
            post {
                (entity(as[String]) & (extractRequest)) { (ent, request) =>
                request.entity.contentType() match {
                  case ContentType(MediaType("application/vnd.restwithscala.task+json"), _) =>
                    mapRequest({ req => req.copy(entity = HttpEntity.apply(MediaTypes.`application/json`, ent)) }) {
                      (entity(as[Task])) {
                        task => {
                          mapResponseEntity(mapEntity) {
                            complete {
                              TaskService.insert(task)
                            }
                          }
                        }
                      }
                    }
                  case _ => complete(StatusCodes.BadRequest, "Unsupported mediatype")
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
              (setCookie(HttpCookie(name = "hello", value = "world")) &
                respondWithHeader(sampleHeader)
                ) {
                onComplete(TaskService.select(task)) {
                  case Success(Some(value)) => complete(value)
                  case Success(None) => complete(StatusCodes.NotFound, "No tasks found")
                  case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
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