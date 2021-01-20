import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import routes.Route

object Boot extends App {
  implicit val system: ActorSystem = ActorSystem("websocket")
  implicit val materializer: Materializer =Materializer(system)

  val routes = new Route

  Http().bindAndHandle(routes.websocketRoute, "localhost", 8080)
}
