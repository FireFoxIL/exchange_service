import akka.actor.ActorSystem
import org.scalatest._
import akka.http.scaladsl.model.{ContentType, HttpEntity, MediaTypes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import json_support.models.{ItemRequest, JsonExchangeResponse, JsonResponse}

/**
  * Collects tests for WebServer
  */
class AppTest extends AsyncFunSuite {

  // Initialization of the actor system
  implicit val system = ActorSystem("server")
  implicit val materializer = ActorMaterializer()

  test("Parsing response json string from exchange API") {
    import json_support.JsonSupportClient._
    val string =
      """
        |{
        |    "success": true,
        |    "timestamp": 1519296206,
        |    "base": "EUR",
        |    "date": "2018-03-27",
        |    "rates": {
        |        "AUD": 1.566015,
        |        "CAD": 1.560132,
        |        "CHF": 1.154727,
        |        "CNY": 7.827874,
        |        "GBP": 0.882047,
        |        "JPY": 132.360679,
        |        "USD": 1.23396
        |    }
        |}
      """.stripMargin

    Unmarshal(HttpEntity(ContentType(MediaTypes.`application/json`), string)).to[JsonExchangeResponse] map {
      res => assert(res.success)
    }
  }

  test("Request to exchange API") {
    ExchangeClient.requestExchange(ItemRequest("USD", "EUR", 1.0)) map {
      res => assert(res.status.isSuccess())
    }
  }

  test("Parsing response from exchange API") {
    ExchangeClient.requestAndParse(ItemRequest("USD", "EUR", 1.0)) map {
      res => println(res.rates); assert(res.success)
    }
  }

  test("Request to server") {
    import json_support.JsonSupportServer._

    val json =
      """
        |{
        |  "data": [
        |    {
        |      "currencyFrom" : "USD",
        |      "currencyTo" : "RUB",
        |      "valueFrom" : 15.65
        |    },
        |    {
        |      "currencyFrom" : "EUR",
        |      "currencyTo" : "RUB",
        |      "valueFrom" : 20.0
        |    },
        |    {
        |      "currencyFrom" : "CZK",
        |      "currencyTo" : "RUB",
        |      "valueFrom" : 10.0
        |    },
        |    {
        |      "currencyFrom" : "UAH",
        |      "currencyTo" : "RUB",
        |      "valueFrom" : 25.0
        |    }
        |  ]
        |}
      """.stripMargin

    ExchangeWebServer.start("localhost", 9000)


    TestClient.requestExchangeRates("localhost", 9000, json) flatMap {
      res => Unmarshal(res.entity).to[JsonResponse]
    } map {
      res => assert(res.errorCode == 0)
    }
  }

  test("Request to server with bad input") {
    import json_support.JsonSupportServer._

    val json = ""

    ExchangeWebServer.start("localhost", 9000)


    TestClient.requestExchangeRates("localhost", 9000, json) flatMap {
      res => Unmarshal(res.entity).to[JsonResponse]
    } map {
      res => assert(res.errorCode == 4)
    }
  }
}
