package ch.epfl.lsr.netty

import ch.epfl.lsr.netty.kryo._
import ch.epfl.lsr.netty.bootstrapping._

import ch.epfl.lsr.common.ImplicitSocketAddresses._

class ObjectEchoHandler[T] extends MessageReceivedHandler { 

  override def messageReceived(ctx :ChannelHandlerContext, e :MessageEvent) { 
    val o :T = e.getMessage.asInstanceOf[T] // make sure it is T
    e.getChannel.write(o)
  }

}

object ServerMain { 
  import ch.epfl.lsr.common.Message

  def main(args :Array[String])  {
    val bootstrap = NIOSocketServer.bootstrap("child.tcpNoDelay" -> true, "child.keepAlive" -> true) { 
      pipeline(
	new KryoEncoder(),
	new KryoDecoder(),
	new ObjectEchoHandler[Message]()
      )
    }
    bootstrap.bind(8080)
  }
}
