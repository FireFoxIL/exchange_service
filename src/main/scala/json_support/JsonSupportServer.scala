package json_support

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

import json_support.models.{ItemRequest, ItemResponse, JsonRequest, JsonResponse}

/**
  * Adds implicit formats for automatic json parsing
  */
object JsonSupportServer extends SprayJsonSupport with DefaultJsonProtocol {
  // Format for ItemRequest class
  implicit val itemReqFormat = jsonFormat3(ItemRequest)

  // Format for ItemResponse class
  implicit val itemRespFormat = jsonFormat4(ItemResponse)

  // Format for JsonRequest class
  implicit val jsonReqFormat = jsonFormat1(JsonRequest)

  // Format for JsonRespose class
  implicit val jsonRespFormat = jsonFormat3(JsonResponse)
}
