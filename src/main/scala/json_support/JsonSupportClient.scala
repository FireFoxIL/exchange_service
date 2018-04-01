package json_support

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import json_support.models.{Currency, JsonExchangeResponse}
import spray.json._

/**
  * Implementing custom protocol for automatic json parsing
  */
trait ClientProtocol extends DefaultJsonProtocol {
  implicit object JsonExchangeResponseFormat extends RootJsonFormat[JsonExchangeResponse] {

    /**
      * Converts JsonExchangeResponse to json. No need to implement
      *
      * @param e  object to convert
      */
    def write(e: JsonExchangeResponse) = ???

    /**
      * Converts json to JsonExchangeResponse
      *
      * @param  value value (json) to convert
      */
    def read(value: JsValue) = value.asJsObject.getFields("success", "base", "rates") match {
      case Seq(JsBoolean(success), JsString(base), JsObject(rates)) =>
        JsonExchangeResponse(success, base, rates.map {
          case (b, JsNumber(v)) => Currency(b, v.toDouble)
          case _ => throw DeserializationException("Couldn't deserialize")
        }.toList)
      case Seq(JsBoolean(success), _, _) => JsonExchangeResponse(success, "", Nil)
      case _ => throw DeserializationException("Couldn't deserialize")
    }
  }

}

/**
  * Adds implicit formats for automatic json parsing
  */
object JsonSupportClient extends SprayJsonSupport with ClientProtocol