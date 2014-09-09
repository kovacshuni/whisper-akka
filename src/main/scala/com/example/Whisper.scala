package com.example

import java.util.concurrent.{TimeUnit, Executors}

import akka.actor.{ActorRef, Actor}

import scala.concurrent.{ExecutionContext, Await, Promise}
import scala.concurrent.duration._

object Whisper {

  private val executor = Executors.newSingleThreadExecutor()
  implicit private val ec = ExecutionContext.fromExecutorService(executor)

  def main(args: Array[String]): Unit = {
    val nWhisperers = args(0).toLong
    val firstWhisperer = Promise[Int]()
    var latestWhisperer = firstWhisperer
    for (i <- nWhisperers to 1 by -1) {
      val whisperer = Promise[Int]()
      latestWhisperer.future onSuccess {
        case n: Int => whisperer.success(n+1)
      }
      latestWhisperer = whisperer
    }
    println("Starting...")
    val timeA = System.currentTimeMillis()

    firstWhisperer.success(0)
    println(Await.result(latestWhisperer.future, 3.0 seconds))

    println("Ended in " + (System.currentTimeMillis() - timeA) + " ms.")
    ec.shutdown()
    ec.awaitTermination(3, TimeUnit.SECONDS)
    executor.shutdown()
    executor.awaitTermination(3, TimeUnit.SECONDS)
  }
}

class Whisperer(private val nextWhisperer: ActorRef) extends Actor {
  override def receive = {
    case n: Int ⇒ nextWhisperer ! n+1
  }
}

class LastWhisperer(gameOver: Promise[Boolean]) extends Whisperer(null) {
  override def receive = {
    case n: Int ⇒
      println(n+1)
      gameOver.success(true)
  }
}
