package ch.epfl.lsr.netty.kryo

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{ Input, Output }

import org.jboss.netty.handler.codec.frame.{ LengthFieldPrepender, LengthFieldBasedFrameDecoder }
import org.jboss.netty.buffer.{ ChannelBuffer, ChannelBuffers, ChannelBufferInputStream, ChannelBufferOutputStream }

import org.jboss.netty.channel._


object KryoFactory  { 
  import ch.epfl.lsr.common.Message

  def getKryo = { 
    val kryo = new Kryo()
    kryo.setInstantiatorStrategy(new org.objenesis.strategy.StdInstantiatorStrategy());

    // TODO automagic registration. see akka-kryo-serialization
    kryo.register(classOf[Message], 1111)

    kryo
  }
}


class KryoDecoder extends LengthFieldBasedFrameDecoder(1048576,0,4,0,4) { 
  val kryo = KryoFactory.getKryo

  // to avoid copying as in ObjectDecoder
  override def extractFrame(buffer :ChannelBuffer, index :Int, length :Int) = buffer.slice(index,length)

  // TODO cache input object ? or reimplement Input to access buffer?
  override def decode(ctx :ChannelHandlerContext, channel :Channel, buffer :ChannelBuffer) = { 
    val frame = super.decode(ctx, channel, buffer).asInstanceOf[ChannelBuffer]
    if(frame == null) { 
      null
    } else { 
      val is = new ChannelBufferInputStream(frame)
      val msg = kryo.readClassAndObject(new Input(is))
      msg
    }
  }
}

class KryoEncoder extends LengthFieldPrepender(4) { 
  val kryo = KryoFactory.getKryo

  //TODO cache output ? or reimplement to access buffer?
  override def encode(ctx :ChannelHandlerContext, channel :Channel, msg :Object) = { 
    val os = new ChannelBufferOutputStream(ChannelBuffers.dynamicBuffer())
    val output = new Output(os)
    kryo.writeClassAndObject(output, msg)
    output.flush
    output.close
    val s = super.encode(ctx, channel, os.buffer)
    s
  }
}
