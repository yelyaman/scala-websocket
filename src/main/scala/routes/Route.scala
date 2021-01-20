package routes

import actors.WSHandlerActor
import actors.WSHandlerActor.{Disconnected, DisconnectedWithFailure, EstablishConnection}
import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives.{handleWebSocketMessages, path}
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, OverflowStrategy}

class Route(implicit materializer: Materializer, system: ActorSystem) {
  val websocketRoute: server.Route =
    path("greeter") {
      handleWebSocketMessages(handler)
    }

  def handler: Flow[Message, Message, Any] = {
    val handlerActor = system.actorOf(WSHandlerActor.props)
    val sink: Sink[Message, NotUsed] = Flow[Message].collect {
      case msg: TextMessage => msg.textStream
    }.mapAsync(1)(in => in.runFold("")((left, right) => left + right))
      .to(Sink.actorRef(handlerActor, Disconnected, onFailure))

    val source = Source
      .actorRef[String](PartialFunction.empty, PartialFunction.empty, 10, OverflowStrategy.dropTail)
      .mapMaterializedValue(wsRef => handlerActor ! EstablishConnection(wsRef))
      .map(out => TextMessage.apply(out))
    Flow.fromSinkAndSource(sink, source)
  }

  def onFailure(err: Throwable): Any = {
    println(err.getMessage)
    DisconnectedWithFailure
  }
}
