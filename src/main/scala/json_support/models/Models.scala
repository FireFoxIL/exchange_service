package json_support.models

// Server
case class ItemRequest(currencyFrom: String, currencyTo: String, valueFrom: Double)
case class ItemResponse(currencyFrom: String, currencyTo: String, valueFrom: Double, valueTo: Double)
case class JsonRequest(data: List[ItemRequest])
case class JsonResponse(data: List[ItemResponse], errorCode: Int, errorMessage: String)

// Client
case class Rate(base: String, value: Double)
case class JsonExchangeResponse(success: Boolean, base: String, rates: List[Rate])

