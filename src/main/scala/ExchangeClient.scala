import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import json_support.models.{ItemRequest, ItemResponse, JsonExchangeResponse, Rate}

import scala.concurrent.Future

object ExchangeClient {

  // Implicit converter for Json Response
  import json_support.JsonSupportClient._

  val apiKey = "3fc26ec70625604b4175f29d8fbfaa0b"

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  def requestExchange(req: ItemRequest): Future[HttpResponse] = Http().singleRequest(
    HttpRequest(method = HttpMethods.GET, uri = Uri.from(
      scheme = "http",
      host = "data.fixer.io",
      path = "/api/latest"
    ).withQuery(Query(
      "access_key" -> apiKey,
      "symbols" -> (req.currencyFrom + "," + req.currencyTo)
    )))
  )

  def requestAndParse(req: ItemRequest): Future[JsonExchangeResponse] =
    requestExchange(req) flatMap { res => Unmarshal(res.entity).to[JsonExchangeResponse] }
}
