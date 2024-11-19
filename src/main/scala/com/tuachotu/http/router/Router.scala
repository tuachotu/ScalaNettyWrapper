package com.tuachotu.http.router

import io.netty.handler.codec.http._

object Router {
  def route(req: FullHttpRequest): FullHttpResponse = {
    (req.method(), req.uri()) match {
      case (HttpMethod.GET, "/") => handleRoot()
      case (HttpMethod.GET, "/hello") => handleHello()
      // Add more routes as needed
      case _ => handleNotFound()
    }
  }

  private def handleRoot(): FullHttpResponse = {
    val content = "Welcome to the Scala 3 Netty Server!"
    new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1,
      HttpResponseStatus.OK,
      io.netty.buffer.Unpooled.copiedBuffer(content.getBytes())
    )
  }

  private def handleHello(): FullHttpResponse = {
    val content = "Hello, World11!"
    new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1,
      HttpResponseStatus.OK,
      io.netty.buffer.Unpooled.copiedBuffer(content.getBytes())
    )
  }

  private def handleNotFound(): FullHttpResponse = {
    val content = "404 Not Found"
    new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1,
      HttpResponseStatus.NOT_FOUND,
      io.netty.buffer.Unpooled.copiedBuffer(content.getBytes())
    )
  }
}