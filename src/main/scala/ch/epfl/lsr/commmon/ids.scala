package ch.epfl.lsr.common

object IdProvider { 
  lazy val ai = new java.util.concurrent.atomic.AtomicInteger
  def nextId = ai.incrementAndGet
}

trait HasId { 
  val id :Int = IdProvider.nextId
  lazy val getIdentifier = id.toString
}

