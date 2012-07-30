package ch.epfl.lsr.testing.common

import collection.immutable.List

// needed for akka-kryo-serializer
trait DelaySerializable extends Serializable

case class Message(clientId :Int, seq :Int, stuff:List[Any] = List.empty) extends DelaySerializable { 
  def inc = Message(clientId, seq+1, stuff)
}

