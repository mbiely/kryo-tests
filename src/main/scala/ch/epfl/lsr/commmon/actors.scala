package ch.epfl.lsr.common

import akka.actor._

class EchoActor extends Actor { 
  def receive = { 
    case msg :Message => 
      sender ! msg
  }
}


object Actors {
  lazy val echoActor = ActorSystem("Server").actorOf(Props[EchoActor])
}
