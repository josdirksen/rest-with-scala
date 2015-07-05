package org.restwithscala.common.service

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import org.restwithscala.common.model.{Status, Note, Task}

import scala.collection.JavaConverters._
import scala.concurrent._
import ExecutionContext.Implicits.global

import scala.concurrent.Future

// Simple service which can store and retrieve tasks.
object TaskService {

  private val idGenerator = IdGenerator()

  // An underlying map.
  private val map = new ConcurrentHashMap[Long, Task]().asScala

  def nextTaskId(): Long = idGenerator.next
  def delete(id: Long): Future[Option[Task]] = Future { map.remove(id) }
  def select(id: Long): Future[Option[Task]] = Future { map.get(id) }
  def all: Future[List[Task]] = Future {map.values.toList}
  def search(status: String, containsText: Option[String]) : Future[List[Task]] = {

    Future {
    map.filter {case(id, task) => status.equals(task.status.status)}
       .filter {case(id, task) => containsText.map(task.content.contains(_)) getOrElse(true)}
       .values.toList
    }
  }

  def insert(u: Task): Future[Task] = Future {
    val taskWithId = u.copy(id = nextTaskId())
    map += (taskWithId.id -> taskWithId)
    taskWithId
  }

  def update(id: Long, u: Task): Future[Option[Task]] = Future {
    if (map.contains(id)) {
      map.replace(id, u)
      Some(u)
    } else None
  }

  case class IdGenerator() {
    private val id = new AtomicLong
    def next: Long = id.incrementAndGet
  }
}