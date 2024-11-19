package com.tuachotu.http.core

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelFuture, ChannelInitializer, ChannelOption, EventLoopGroup}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http._
import com.tuachotu.util.LoggerUtil
import com.tuachotu.util.LoggerUtil.Logger
import scala.jdk.CollectionConverters._

import com.tuachotu.http.handlers.HttpHandler

object HttpServer:
  implicit private val logger: Logger = LoggerUtil.getLogger(classOf[HttpServer.type])
  def start(port: Int = 2107): Unit = // TODO : Move port to config
    // This Group handles incoming connection request, just one thread for that
    // Accepts incoming connections and assigns them to a worker.
    val bossGroup: EventLoopGroup = new NioEventLoopGroup(1)
    // TODO: Find out what is the default number 
    // TODO: Find out what can be improved in this config
    // Handles the actual I/O operations (reading, writing, etc.) for the connected channels.
    val workerGroup: EventLoopGroup = new NioEventLoopGroup()

    try
      val bootstrap = new ServerBootstrap()
      bootstrap.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])  // Parent channel (ServerSocketChannel)
        .childHandler(new HttpHandler()) // child channel will pass the req to handler
        // We are starting with 128, but how many connection we will allow waiting?
        // if we are getting high traffic, changing this number may help
        // If we are slow in processing req, this number may be big
        // TODO: move it to config
        .option(ChannelOption.SO_BACKLOG, Integer.valueOf(128))
        .childOption(ChannelOption.SO_KEEPALIVE, java.lang.Boolean.TRUE)

      val f: ChannelFuture = bootstrap.bind(port).sync()
      if f.isSuccess then
        val jsonMessage = Map("port" -> port)
        LoggerUtil.info("Server started", "port", 2107)
        sys.addShutdownHook {
          shutdown()
        }
        f.channel().closeFuture().sync()
      else
        println(s"Failed to bind to port $port")
    catch
      case e: Exception => println(s"An error occurred: ${e.getMessage}")
    finally
      if bossGroup != null && workerGroup != null then
        // shut down Boss and Worker Group gracefully!
        workerGroup.shutdownGracefully()
        bossGroup.shutdownGracefully()


    def shutdown(): Unit =
      if workerGroup != null then
        workerGroup.shutdownGracefully()
      if bossGroup != null then
        bossGroup.shutdownGracefully()
      println("Server shutdown complete.")