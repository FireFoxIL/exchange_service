import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import json_support.models._

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}


object WebServer extends Directives with JsonSupport {
  val apiKey = "3fc26ec70625604b4175f29d8fbfaa0b"

//  def requestExchange(req: ItemRequest): Future[HttpResponse] = Http().singleRequest(
//    HttpRequest(method = HttpMethods.GET, uri = Uri.from(
//      scheme = "http",
//      host = "data.fixer.io",
//      path = "/api/latest"
//    ).withQuery(Query(
//      "access_key" -> apiKey,
//      "base" -> req.currencyFrom,
//      "symbols" -> req.currencyTo
//    )))
//  )
//
//  def request(req: ItemRequest): Option[ItemResponse] = {
//    val future = requestExchange(req)
//    future.onComplete{
//      case Success(res) => println(res)
//      case Failure(_) => println("something went wrong")
//    }
//    Some(ItemResponse(
//      "USD", "EUR", 1.0, 2.0
//    ))
//  }

  def main(args: Array[String]): Unit = {

    implicit val system = ActorSystem("system")
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route =
      pathSingleSlash {
        get {
          val future = Http().singleRequest(HttpRequest(method = HttpMethods.GET, uri = Uri.from(
            scheme = "http",
            host = "data.fixer.io",
            path = "/api/latest"
          ).withQuery(Query(
            "access_key" -> apiKey,
            "base" -> "USD",
            "symbols" -> "EUR"
          ))))

          val next = future flatMap {
            res => {
              Unmarshal(res.entity).to[JsonExchangeResponse]
            }
          }

          onComplete(future) {
            case Success(res) => complete(res.entity.toString)
            case Failure(_) => complete("something went wrong")
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 9000)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}