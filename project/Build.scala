import sbt._ 
import Keys._

import sbtassembly.Plugin._
import sbtassembly.Plugin.{ AssemblyKeys => Ass }
import sbtg5k.G5kPlugin._
import sbtg5k.G5kPlugin.{g5kKeys => G5}
import sbtCluster.ClusterPlugin._
import sbtCluster.ClusterPlugin.{ClusterKeys => CK }


object KryonetTestBuild extends AkkaBuild { 
  def akkaModules = List("actor")

  lazy val typesafe = "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
  lazy val typesafeSnapshot = "Typesafe Snapshots Repository" at "http://repo.typesafe.com/typesafe/snapshots/"

  def computeSettings = { 
    Defaults.defaultSettings ++ super.settings ++ baseAssemblySettings ++ 
    Seq[Setting[_]](
       libraryDependencies ++= Seq(
	 //"com.romix.akka" % "akka-kryo-serialization" % "0.1-SNAPSHOT"
	 "org.apache.commons" % "commons-math" % "2.2",
	 "com.esotericsoftware.kryo" % "kryo" % "2.18-SNAPSHOT",
	 "io.netty" % "netty" % "3.5.2.Final"
       ),
       resolvers ++= Seq(
	 typesafe,
	 typesafeSnapshot
       )
     ) ++ PrivateSettings()
  }

  lazy val netty = Project(id = "kryo-netty-test", 
			     base = file("."),
			     settings = computeSettings
			    )

  lazy val akka = Project(id = "kryo-akka-test", 
			     base = file("."),
			     settings = computeSettings ++ Seq(addAkkaDependencies("remote")) 
			    )

}
