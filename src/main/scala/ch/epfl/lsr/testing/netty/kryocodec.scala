package ch.epfl.lsr.testing.netty.kryo

import com.esotericsoftware.kryo.{ Kryo, Registration, Serializer }
import com.esotericsoftware.kryo.io.{ Input, Output }
import com.esotericsoftware.kryo.util.{ DefaultClassResolver, MapReferenceResolver }

import org.jboss.netty.handler.codec.frame.{ LengthFieldPrepender, LengthFieldBasedFrameDecoder }
import org.jboss.netty.buffer.{ ChannelBuffer, ChannelBuffers, ChannelBufferInputStream, ChannelBufferOutputStream }

import org.jboss.netty.channel._

import scala.collection.mutable.Queue

case class ClassIdMapping(val clazzName :String, val id :Int) { 
  def registerWith(kryo :Kryo) = { 
    //System.err.println("registering "+clazzName)
    val clazz = Class forName clazzName
    kryo.register(new Registration(clazz, kryo.getDefaultSerializer(clazz), id))
  }
}

class ClassResolver(encoder :KryoEncoder) extends DefaultClassResolver { 
  var nextId = 1000

  override def registerImplicit(clazz :Class[_]) = { 
    val id = nextId
    nextId += 1
    
    val mapping = ClassIdMapping(clazz.getName, id)

    if(encoder != null)
      encoder sendMapping mapping

    mapping registerWith kryo
  }
}


object KryoFactory  { 
  import ch.epfl.lsr.testing.common.Message

  def getKryo(encoder :KryoEncoder = null) = { 

    val kryo = new Kryo(new ClassResolver(encoder), new MapReferenceResolver)
    kryo.setInstantiatorStrategy(new org.objenesis.strategy.StdInstantiatorStrategy());
    kryo setRegistrationRequired false
    kryo setReferences false

    kryo.register(classOf[ClassIdMapping],100)
    // TODO automagic registration. see akka-kryo-serialization
    //kryo.register(classOf[Message], 1111)

    kryo
  }
}


class KryoDecoder extends LengthFieldBasedFrameDecoder(1048576,0,4,0,4) { 
  val kryo = KryoFactory.getKryo(null)

  // to avoid copying as in ObjectDecoder
  override def extractFrame(buffer :ChannelBuffer, index :Int, length :Int) = buffer.slice(index,length)

  override def decode(ctx :ChannelHandlerContext, channel :Channel, buffer :ChannelBuffer) = { 
    val frame = super.decode(ctx, channel, buffer).asInstanceOf[ChannelBuffer]
    if(frame == null) { 
      null
    } else { 
      val is = new ChannelBufferInputStream(frame)
      val msg = kryo.readClassAndObject(new Input(is))

      if(msg.isInstanceOf[ClassIdMapping]) { 
	msg.asInstanceOf[ClassIdMapping].registerWith(kryo)
	// TODO: does this work to discard the data ?
	null 
      } else { 
	msg
      }
    }
  }
}

class KryoEncoder extends LengthFieldPrepender(4) { 
  val kryo = KryoFactory.getKryo(this)
  var pendingRegistrations :Queue[ClassIdMapping] = Queue.empty

  def sendMapping(registration :ClassIdMapping) { 
    pendingRegistrations += registration
  }

  
  override def encode(ctx :ChannelHandlerContext, channel :Channel, msg :Object) = { 
    def encode2buffer(msg :Object) = { 
      val os = new ChannelBufferOutputStream(ChannelBuffers.dynamicBuffer)
      val output = new Output(os)
      kryo.writeClassAndObject(output, msg)
      output.flush
      output.close
      super.encode(ctx, channel, os.buffer).asInstanceOf[ChannelBuffer]
    }

    var mappingsBuffer = ChannelBuffers.EMPTY_BUFFER    
    val msgBuffer = encode2buffer(msg)

    if(pendingRegistrations.nonEmpty) { 
      pendingRegistrations.dequeueAll { mapping => 
	mappingsBuffer = ChannelBuffers.wrappedBuffer(mappingsBuffer, encode2buffer(mapping))
	true
      }
    } 
    ChannelBuffers.wrappedBuffer(mappingsBuffer,msgBuffer)
  }
}
