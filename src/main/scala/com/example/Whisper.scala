package com.example

import akka.actor.{ActorSystem, Props, ActorRef, Actor}

import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._

object Whisper {

  private val NWhisperers = 1000000
  private val system = ActorSystem("my-actor-system")

  def main(args: Array[String]): Unit = {
    val gameOver = Promise[Boolean]()
    val lastWhisperer = system.actorOf(Props(classOf[LastWhisperer], gameOver), name = "last-whisperer")
    var latestWhisperer: ActorRef = lastWhisperer
    for (i <- NWhisperers to 1 by -1) {
      val newWhisperer = system.actorOf(Props(classOf[Whisperer], latestWhisperer), name = s"whisperer-$i")
      latestWhisperer = newWhisperer
    }
    println("Starting...")
    val timeA = System.currentTimeMillis()

    latestWhisperer ! 0
    Await.ready(gameOver.future, 3.0 seconds)

    println("Ended in " + (System.currentTimeMillis() - timeA) + " ms.")
    system.shutdown()
    system.awaitTermination(3.0 second)
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
