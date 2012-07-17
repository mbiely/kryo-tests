package ch.epfl.lsr.netty

import org.jboss.netty.channel.SimpleChannelHandler
import org.jboss.netty.channel.{ ChannelHandlerContext => NettyChannelHandlerContext, 
				MessageEvent => NettyMessageEvent, 
				ExceptionEvent => NettyExceptionEvent }


trait MessageReceivedHandler extends SimpleChannelHandler  { 
  type MessageEvent = NettyMessageEvent
  type ChannelHandlerContext = NettyChannelHandlerContext
  type ExceptionEvent = NettyExceptionEvent

  def messageReceived(ctx :ChannelHandlerContext, e :MessageEvent) 

  override def exceptionCaught(ctx :ChannelHandlerContext, e :ExceptionEvent) { 
    e.getCause.printStackTrace
    e.getChannel.close
  }
}
