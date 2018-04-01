import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

/**
  * WebClient to perform test requests to the WebServer
  *
  * @author Ivan Lyagaev
  */
object TestClient {

  // Initialization of the actor system
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  /**
    * Sends request to the WebServer
    *
    * @param  host  Hostname
    * @param  port  Port
    * @param  json  Json String for request body
    */
  def requestExchangeRates(host: String, port: Int, json: String): Future[HttpResponse] = Http().singleRequest(
    HttpRequest(method = HttpMethods.POST, uri = Uri.from(
      scheme = "http",
      host = host,
      port = port,
    )).withEntity(HttpEntity(ContentType(MediaTypes.`application/json`), json))
  )

}
