package ch.epfl.lsr.testing.kryonet

import ch.epfl.lsr.testing.common.{ Message, SimpleSummaryStats }

import com.esotericsoftware.kryonet.{ Client => KryoClient, Listener => KryoListener, Connection => KryoConnection }

class ClientListener(clientId: Int) extends KryoListener with SimpleSummaryStats { 
  val collectFor = 30
  val discardFor = 30

  lazy val getIdentifier = "%6d" format clientId

  override def received (c :KryoConnection, o :AnyRef) { 
    val m = o.asInstanceOf[Message]
    
    recordEvent
 
    c.sendTCP(m.inc)
  }
}

class Client(id :Int) extends Settings { 
  val listener = new ClientListener(id)
  
  val started = new java.util.concurrent.CountDownLatch(1)
  var updated = false

  val client : KryoClient = { 
    val c = new KryoClient { 
      override def update(t :Int) = { 
        super.update(t)
        if(!updated) { 
          started.countDown
	  updated = true
        }
      }
    }
    c addListener listener
    initEndPoint(c)
    c
  }

  def connect(host :String, port :Int) { 
    started.await
    client.connect(kryotimeout, host, port)
    client sendTCP Message(id,1)
  }

  def start = client.start
}

object ClientMain extends Settings { 

  def main(args :Array[String]) {  
    var numClients = 30
    var host = "localhost"

    if(args.size>1) { 
      numClients = args(0).toInt
      host = args(1)
    }

    val clients = for(i <- 1 to numClients) yield new Client(i)

    clients.foreach { _.start }
    clients.foreach { _.connect(host,port) }
  }
}


