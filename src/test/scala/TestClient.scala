import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object TestClient {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private val json =
    """
      |{
      |  "data": [
      |    {
      |      "currencyFrom" : "RUB",
      |      "currencyTo" : "USD",
      |      "valueFrom" : 15.65
      |    },
      |    {
      |      "currencyFrom" : "RUB",
      |      "currencyTo" : "EUR",
      |      "valueFrom" : 20.0
      |    }
      |  ]
      |}
    """.stripMargin

  def requestExchangeRates(host: String, port: Int): Future[HttpResponse] = Http().singleRequest(
    HttpRequest(method = HttpMethods.POST, uri = Uri.from(
      scheme = "http",
      host = host,
      port = port,
    )).withEntity(HttpEntity(ContentType(MediaTypes.`application/json`), json))
  )

}
