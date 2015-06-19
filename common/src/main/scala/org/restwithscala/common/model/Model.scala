package org.restwithscala.common.model

case class Task(id: Long, title: String, content: String, assignedTo: Option[Person], notes: List[Note], status: Status)

case class Note(id: Long, content: String)

case class Status(status: String)

case class Project(id: Long, name: String, tasks: List[Task], members: List[Person])

case class Person(name: String)

case class SearchParams(status: Option[String], text: Option[String])