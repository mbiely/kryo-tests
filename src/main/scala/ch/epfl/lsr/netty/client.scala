package ch.epfl.lsr.netty

import ch.epfl.lsr.common._
import ch.epfl.lsr.netty.kryo._
import ch.epfl.lsr.netty.bootstrapping._

import org.jboss.netty.bootstrap.Bootstrap
import org.jboss.netty.channel._



class MessageEchoClient() extends MessageReceivedHandler with SimpleSummaryStats with HasId { 
  val discardFor = 30
  val collectFor = 60

  override def messageReceived(ctx :ChannelHandlerContext, e :MessageEvent) { 
    val m = e.getMessage.asInstanceOf[Message]

    if(m.clientId != id)
      throw new Exception("received wrong answer")

    recordEvent
    
    e.getChannel.write(m.inc)
  }
  
  override def channelConnected(ctx :ChannelHandlerContext, e :ChannelStateEvent) { 
    e.getChannel.write(new Message(id,0))
  }
}


import ImplicitSocketAddresses._

object ClientMain { 
  def main(args :Array[String])  {

    var numClients = 30
    var host = "localhost"
    if(args.size>1) { 
      numClients = args(0).toInt
      host = args(1)
    }

    val bootstrap = NIOSocketCient.bootstrap("child.tcpNoDelay" -> true, "child.keepAlive" -> true) { 
      pipeline(
	new KryoEncoder(),
	new KryoDecoder(),
	new MessageEchoClient()
      )
    }
    (1 to numClients) foreach { _ => bootstrap.connect(host::8080) }
  }
}
