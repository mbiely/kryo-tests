package ch.epfl.lsr.netty.akkabridge

import ch.epfl.lsr.common.{ Actors, Message }

import ch.epfl.lsr.netty._
import ch.epfl.lsr.netty.bootstrapping._
import ch.epfl.lsr.netty.kryo._

import org.jboss.netty.channel._

import akka.util.duration._
import akka.util.Timeout 
import akka.pattern.ask
import akka.actor._

import java.net.InetSocketAddress

object implicits { 
  implicit val timeout = Timeout(5 seconds)
}

class ActorForwardingHandler(actor :ActorRef) extends MessageReceivedHandler { 
  import implicits._

  override def messageReceived(ctx :ChannelHandlerContext, e :MessageEvent)  { 
    // uses implicit timeout
    val f = actor ? e.getMessage 
    f.onComplete { 
      case Right(answer) => 
	e.getChannel.write(answer)
      //case Left (e :AskTimeoutException) =>
      case _ => ()
    }
  }
}


object ServerMain { 
  def main(args :Array[String])  {
    val actor = Actors.echoActor
    
    val bootstrap = NIOSocketServer.bootstrap("child.tcpNoDelay" -> true, "child.keepAlive" -> true) { 
      Channels.pipeline(
	new KryoEncoder(),
	new KryoDecoder(),
	new ActorForwardingHandler(actor)
      )
    }
    bootstrap.bind(new InetSocketAddress(8080))
  }
}
