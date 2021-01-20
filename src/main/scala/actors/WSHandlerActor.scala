package actors

import actors.WSHandlerActor.{Disconnected, DisconnectedWithFailure, EstablishConnection, Tic}
import akka.actor.{Actor, ActorRef, Props}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

object WSHandlerActor {
  def props = Props(new WSHandlerActor)

  case class EstablishConnection(wsRef: ActorRef)
  case object Tic
  case object Disconnected
  case object DisconnectedWithFailure
}

class WSHandlerActor extends Actor {
  implicit val ex: ExecutionContext = context.dispatcher
  context.system.scheduler.scheduleAtFixedRate(5.seconds, 2.seconds, self, Tic)

  override def receive: Receive = {
    case EstablishConnection(wsRef) => context.become(connected(wsRef))
  }

  def connected(wsRef: ActorRef): Receive = {
    case msg: String => wsRef ! s"Echo: $msg"
    case Tic => wsRef ! "Tic"
  }

  override def aroundReceive(receive: Receive, msg: Any): Unit = msg match {
    case Disconnected =>
      println("Closing Actor")
      context.stop(self)
    case DisconnectedWithFailure =>
      println("Disconnected with failure")
      context.stop(self)
    case _ => super.aroundReceive(receive, msg)
  }
}

