import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import json_support.models._
import org.apache.logging.log4j.scala.Logging

import scala.concurrent.Future

/**
  * WebClient to perform HTTP requests to Exchange API
  *
  * Exchange API: https://fixer.io/
  */
object ExchangeClient extends Logging {

  // Implicit converter for Json Exchange Response
  import json_support.JsonSupportClient._

  /**
    * ApiKey to perform requests
    */
  val apiKey = "3fc26ec70625604b4175f29d8fbfaa0b"

  // Initialization of the actor system
  implicit val system = ActorSystem("exchange-client")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  /**
    * Sends request to Exchange API
    *
    * @param  req Needed data to perform request
    */
  def requestExchange(req: ItemRequest): Future[HttpResponse] = {
    logger.info(s"Sending request [$req] to Exchange API")
    Http().singleRequest(
      HttpRequest(method = HttpMethods.GET, uri = Uri.from(
        scheme = "http",
        host = "data.fixer.io",
        path = "/api/latest"
      ).withQuery(Query(
        // Defining query parameters
        "access_key" -> apiKey,
        "symbols" -> (req.currencyFrom + "," + req.currencyTo)
      )))
    ).recover {
      // Extracting Timeout Exception and deleting all other messages
      case ex: RequestTimeoutException =>
        logger.error(s"Timeout error for request [$req]")
        throw MyServerException("Timeout")
      case ex =>
        logger.error(s"${ex.getMessage} for request [$req]")
        throw MyServerException()
    }
  }

  /**
    * Sends request to Exchange API and parses its body
    *
    * @param  req Needed data to perform request
    */
  def requestAndParse(req: ItemRequest): Future[JsonExchangeResponse] =
    requestExchange(req) flatMap { res =>
      logger.info(s"Parsing response for request [$req]")
      Unmarshal(res.entity).to[JsonExchangeResponse].recover {
        case _ =>
          logger.error(s"Deserialization error for request [$req]")
          throw MyServerException()
      }
    }
}
