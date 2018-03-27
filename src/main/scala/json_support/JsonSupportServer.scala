package json_support

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

import json_support.models.{ItemRequest, ItemResponse, JsonRequest, JsonResponse}

object JsonSupportServer extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemReqFormat = jsonFormat3(ItemRequest)
  implicit val itemRespFormat = jsonFormat4(ItemResponse)
  implicit val jsonReqFormat = jsonFormat1(JsonRequest)
  implicit val jsonRespFormat = jsonFormat3(JsonResponse)

  def produceItemResponse(req: ItemRequest, valueTo: Double) = ItemResponse(
    currencyFrom = req.currencyFrom,
    currencyTo = req.currencyTo,
    valueFrom = req.valueFrom,
    valueTo = valueTo,
  )
}
