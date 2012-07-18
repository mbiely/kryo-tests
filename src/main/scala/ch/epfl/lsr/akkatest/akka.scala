package ch.epfl.lsr.testing.akkatest
	
import akka.actor.{ ActorRef, Props, Actor, ActorSystem }
import com.typesafe.config.ConfigFactory

import ch.epfl.lsr.testing.common._

object Config { 
  def serverPort = serverConfig.getInt("akka.remote.netty.port") 

  def serverConfig = config.getConfig("server")
  def clientConfig = config.getConfig("client")


  private lazy val config = ConfigFactory.load()
}

object ServerMain { 
  def main(args: Array[String]) {
    val system = ActorSystem("Server", Config.serverConfig)
    val actor = system.actorOf(Props[EchoActor], "Echo")

    //println(Config.clientConfig.root.render)

    println(actor)
  }
}


class ClientActor extends Actor with HasId with SimpleSummaryStats {
  val discardFor :Int = 30
  val collectFor :Int = 30

  def receive = {
    case actor: ActorRef => 
      actor ! Message(id,0)
    case m : Message => 
      if(m.clientId != id)
	throw new Exception("received wrong answer")

      recordEvent 

      sender ! m.inc
  }
}

object ClientMain {
   def main(args: Array[String]) {

     var numClients = 30
     var host = "127.0.0.1"
     if(args.size>1) { 
       numClients = args(0).toInt
       host = java.net.InetAddress.getByName(args(1)).getHostAddress
     }
     

     

     val system = ActorSystem("Client", Config.clientConfig)
     //println(Config.clientConfig.root.render)
     
     val remoteActor = system.actorFor("akka://Server@"+host+":"+Config.serverPort+"/user/Echo")
// system.actorOf(Props[EchoActor], "Echo")
     
     println(remoteActor)

     val clients = for( i <- 1 to numClients) yield system.actorOf(Props[ClientActor], name="Client"+i)
     
     clients foreach { a => a ! remoteActor }
   }
}



