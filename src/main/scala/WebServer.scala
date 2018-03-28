import java.rmi.ServerException

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import json_support.models._
import json_support.JsonSupportServer._

import scala.concurrent.Future
import scala.util.{Failure, Success}


object WebServer extends Directives {
  
  implicit val system = ActorSystem("server")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  private def find(list: List[Rate], base: String): Double = {
    list.filter(_.base == base).head.value
  }

  private val route =
    pathSingleSlash {
      post {
        entity(as[JsonRequest]) { req =>
          val list = for (item <- req.data) yield {
            ExchangeClient.requestAndParse(item) map { res =>
              if (!res.success)
                throw new ServerException("Bad reply from api")
              val result = find(res.rates, item.currencyFrom) / find(res.rates, item.currencyTo) * item.valueFrom
              item.produceItemResponse(result)
            }
          }
          onComplete(Future.sequence(list)) {
            case Success(res) => complete(JsonResponse(res, 0, "No errors"))
            case Failure(ex) => complete(JsonResponse(Nil, 1, ex.getMessage))
          }
        }
      }
    }

  def start(host: String, port: Int): Unit = {
    Http().bindAndHandle(route, host, port) onComplete {
      _ => println(s"Server online at http://$host:$port/")
    }
  }
}