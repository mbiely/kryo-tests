package ch.epfl.lsr.common


trait DelaySerializable extends Serializable

case class Message(clientId :Int, seq :Int) extends DelaySerializable { 
  def inc = Message(clientId, seq+1)
}

