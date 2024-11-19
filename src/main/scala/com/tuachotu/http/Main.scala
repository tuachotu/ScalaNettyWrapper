package com.tuachotu.http

import com.tuachotu.http.core.HttpServer

@main def run(): Unit =
  println("Starting HomePro API server...")
  HttpServer.start()