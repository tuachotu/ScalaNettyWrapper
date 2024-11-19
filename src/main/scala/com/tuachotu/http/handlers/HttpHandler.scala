package com.tuachotu.http.handlers

import io.netty.channel.{ChannelHandlerContext, ChannelInitializer, SimpleChannelInboundHandler}
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http._
import com.tuachotu.http.router.Router

class HttpHandler extends ChannelInitializer[SocketChannel]:
  override def initChannel(ch: SocketChannel): Unit =
    ch.pipeline().addLast(new HttpServerCodec())
    ch.pipeline().addLast(new HttpObjectAggregator(512 * 1024)) // MAX size allowed in HTTP method
    ch.pipeline().addLast(new SimpleChannelInboundHandler[FullHttpRequest]() {
      override def channelRead0(ctx: ChannelHandlerContext, req: FullHttpRequest): Unit =
        val response = Router.route(req)
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain")
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes().toString)
        ctx.writeAndFlush(response)
    })