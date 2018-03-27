package json_support.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

case class ItemRequest(currencyFrom: String, currencyTo: String, valueFrom: Double)
case class ItemResponse(currencyFrom: String, currencyTo: String, valueFrom: Double, valueTo: Double)
case class JsonRequest(data: List[ItemRequest])
case class JsonResponse(data: List[ItemResponse], errorCode: Int, errorMessage: String)
case class JsonExchangeResponse(success: Boolean, timestamp: Int, base: String, date: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemReqFormat = jsonFormat3(ItemRequest)
  implicit val itemRespFormat = jsonFormat4(ItemResponse)
  implicit val jsonReqFormat = jsonFormat1(JsonRequest)
  implicit val jsonRespFormat = jsonFormat3(JsonResponse)
  implicit val jsonExchangeResponse = jsonFormat4(JsonExchangeResponse)

  def produceItemResponse(req: ItemRequest, valueTo: Double) = ItemResponse(
    currencyFrom = req.currencyFrom,
    currencyTo = req.currencyTo,
    valueFrom = req.valueFrom,
    valueTo = valueTo,
  )
}