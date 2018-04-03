import akka.actor.ActorSystem
import akka.http.javadsl.server.{MalformedRequestContentRejection, RequestEntityExpectedRejection}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import json_support.models._
import Responses._
import akka.http.scaladsl.server.RouteResult.{Complete, Rejected}
import org.apache.logging.log4j.scala.Logging
import AroundDirectives._

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * WebServer that can convert value from one currency to another
  */
object ExchangeWebServer extends Directives with Logging {

  // Implicit formats for automatic json parsing
  import json_support.JsonSupportServer._

  // Initialization of the actor system
  implicit val system = ActorSystem("server")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  /**
    * Gets currency coefficient from Exchange API response for given name
    *
    * @param  response Response from Exhange API
    * @param  name     name of currency to search
    * @throws MyServerException if there is no value for given name
    */
  private def getCurrencyCoefficient(response: JsonExchangeResponse, name: String): Double = {
    response.findCurrency(name) match {
      case Some(rate) => rate.value
      case None => throw MyServerException()
    }
  }

  /**
    * Rejection Handler
    */
  implicit def rejectionHandler: RejectionHandler = RejectionHandler.newBuilder()
    .handle {
      case _:RequestEntityExpectedRejection => badInput()
      case _:UnsupportedRequestContentTypeRejection => badInput()
      case _:MalformedRequestContentRejection => badInput()
    }
    .result()

  /**
    * Logs time needed for request completion
    */
  def timeRequest(ctx: RequestContext): Try[RouteResult] => Unit = {
    val start = System.currentTimeMillis()

    {
      case Success(Complete(_)) =>
        val d = System.currentTimeMillis() - start
        logger.info(s"Request [${ctx.request.headers}] took - $d ms")
      case Success(Rejected(_)) =>
      case Failure(_)           =>
    }
  }


  /**
    * Route tree
    */
  private val route =
    aroundRequest(timeRequest) {
      pathSingleSlash {
        post {
          entity(as[JsonRequest]) { req =>
            // Executing requests to Exchange API in parallel mode
            val list = for (item <- req.data) yield {
              ExchangeClient.requestAndParse(item) map { resp =>

                // Checking response for success
                if (!resp.success) {
                  logger.error(s"Bad reply from Exchange API for request [$item]")
                  throw MyServerException("Bad reply from api")
                }

                // Calculating result value for given
                val result = getCurrencyCoefficient(resp, item.currencyTo) /
                  getCurrencyCoefficient(resp, item.currencyFrom) * item.valueFrom

                // Converting request item into response item
                item.produceItemResponse(result)
              }
            }

            // Converting List[Future[ItemResponse]] to List[Future[Try[ItemResponse]]]
            val convertedList = list.map(_.map(Success(_)).recover({ case e => Failure(e) }))

            onComplete(Future.sequence(convertedList)) {
              case Success(res) =>
                if (res.forall(_.isSuccess))
                  ok(res.map(_.get))
                else if (res.forall(_.isFailure))
                  allFailed(res.map(_.failed.get.getMessage)
                    .distinct.mkString(", "))
                else
                  someFailed(res.filter(_.isSuccess).map(_.get),
                    res.filter(_.isFailure).map(_.failed.get.getMessage)
                      .distinct.mkString(", "))
              case Failure(_) => undefined()
            }
          }
        }
      }
    }

  /**
    * Server state. Initially it is not running
    */
  private var serverState: Option[Future[Http.ServerBinding]] = None

  /**
    * Starts server on give host and port
    *
    * @param  host Hostname (e.g. localhost)
    * @param  port Port
    */
  def start(host: String, port: Int): Unit = {
    if (serverState.isEmpty) {
      serverState = Some(Http().bindAndHandle(route, host, port))
      serverState.get onComplete {
        case Success(_) =>
          logger.info(s"Server online at http://$host:$port/")
        case Failure(e) =>
          logger.error("Server failed to start", e)
      }
    } else {
      logger.info("Tried to start a server while it is already running")
    }
  }
}
