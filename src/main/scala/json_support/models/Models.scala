package json_support.models

// Server
/**
  * Represents data for one request to Exchange API
  *
  * @param currencyFrom Name of the currency from which you want to convert
  * @param currencyTo Name of the currency to which you want to convert
  * @param valueFrom  Value you want to convert
  */
case class ItemRequest(currencyFrom: String, currencyTo: String, valueFrom: Double) {
  /**
    * Converts to ItemResponse
    *
    * @param valueTo Calculated value (after conversion)
    */
  def produceItemResponse(valueTo: Double) =
    ItemResponse(currencyFrom, currencyTo, valueFrom, valueTo)
}

/**
  * Represents data for one response from Exchange API
  *
  * @param currencyFrom Name of the currency from which you want to convert
  * @param currencyTo Name of the currency to which you want to convert
  * @param valueFrom  Value you want to convert
  * @param valueTo Calculated value (after conversion)
  */
case class ItemResponse(currencyFrom: String, currencyTo: String, valueFrom: Double, valueTo: Double)

/**
  * Represents data from request to WebServer
  *
  * @param  data  List of rates
  */
case class JsonRequest(data: List[ItemRequest])

/**
  * Represents data of WebServer response
  *
  * @param  data  List of rates
  * @param  errorCode Error code (specified in MyServerException)
  * @param  errorMessage  Error message
  */
case class JsonResponse(data: List[ItemResponse], errorCode: Int, errorMessage: String)

// Client
/**
  * Represents currency
  *
  * @param  name  Name of the currency
  * @param  value Proportional coefficient relative to some currency
  */
case class Currency(name: String, value: Double)

/**
  * Represents data of Exchange API response
  *
  * @param  success Response status
  * @param  base  Name of the currency which all rates values are relative to
  * @param  rates List of rates
  */
case class JsonExchangeResponse(success: Boolean, base: String, rates: List[Currency]) {
  /**
    * Finds currency with given name from list of currencies
    *
    * @param  name  Name of the currency
    */
  def findCurrency(name: String):Option[Currency] = {
    rates.find(_.name == name)
  }
}

