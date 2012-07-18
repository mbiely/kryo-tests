package ch.epfl.lsr.testing.kryonet

import ch.epfl.lsr.testing.common.Message
import com.esotericsoftware.kryonet.{ EndPoint }

trait Settings { 
  val port = 8080
  val kryotimeout = 5000

  def initEndPoint(e :EndPoint) { 
    e.getKryo.setInstantiatorStrategy(new org.objenesis.strategy.StdInstantiatorStrategy());
    e.getKryo.register(classOf[Message],100)
  }

}

