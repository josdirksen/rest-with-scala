package org.restwithscala.common.model

case class Task(id: Long, title: String, content: String, assignedTo: Option[Person])

case class Note(id: Long, noteFor: Task)

case class Project(id: Long, name: String, tasks: List[Task], members: List[Person])

case class Person(name: String)