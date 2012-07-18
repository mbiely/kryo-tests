package ch.epfl.lsr.testing.kryonet

import ch.epfl.lsr.testing.common.Message

import com.esotericsoftware.kryonet.{ Server => KryoServer, Listener => KryoListener, Connection => KryoConnection }


object Echoer extends KryoListener { 
  override def received (c :KryoConnection, o :AnyRef) { 
    val m = o.asInstanceOf[Message]
    
    c.sendTCP(m)
  }
}


object ServerMain extends Settings { 
  
  def main(args :Array[String]) {  
    val server = new KryoServer()
    server bind port
    
    initEndPoint(server)
    server addListener Echoer
    
    server.start
  }

}


