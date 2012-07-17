package ch.epfl.lsr.common

import java.net.InetSocketAddress


object ImplicitSocketAddresses { 
  class StringAsSocketAddress(s :String) { 
    def asSocketAddress = { 
      val a = s split ":"
      new InetSocketAddress(a(0),a(1).toInt)
    }
  }

  class IntAsPortPart(port :Int) { 
    def ::(s :String) = new InetSocketAddress(s, port)
  }
  

  implicit def Tuple2InetAddress(addr :(String,Int)) :InetSocketAddress = { 
    new InetSocketAddress(addr._1, addr._2)
  }
  implicit def port2InetAddress(port :Int) :InetSocketAddress = new InetSocketAddress(port)
  implicit def String2AsSocketAddress(s :String) : StringAsSocketAddress = new StringAsSocketAddress(s)
  implicit def port2PortPart(port :Int) = new IntAsPortPart(port)
}
