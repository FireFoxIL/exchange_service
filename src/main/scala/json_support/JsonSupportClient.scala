package json_support

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import json_support.models.{JsonExchangeResponse, Rate}
import spray.json._

trait ClientProtocol extends DefaultJsonProtocol {
  implicit object JsonExchangeResponseFormat extends RootJsonFormat[JsonExchangeResponse] {
    def write(e: JsonExchangeResponse) = ???

    def read(value: JsValue) = value.asJsObject.getFields("success", "base", "rates") match {
      case Seq(JsBoolean(success), JsString(base), JsObject(rates)) =>
        JsonExchangeResponse(success, base, rates.map {
          case (b, JsNumber(v)) => Rate(b, v.toDouble)
        }.toList)
      case Seq(_, _, _) => throw DeserializationException("Cannot deserialize")
    }
  }

}

object JsonSupportClient extends SprayJsonSupport with ClientProtocol {
  implicit val jsonExchangeResponseFormat = JsonExchangeResponseFormat
}