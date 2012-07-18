package ch.epfl.lsr.testing.common

object IdProvider { 
  lazy val ai = new java.util.concurrent.atomic.AtomicInteger
  def nextId = ai.incrementAndGet
}

trait HasId { 
  val id :Int = IdProvider.nextId
  lazy val getIdentifier = "%6d" format id
}

