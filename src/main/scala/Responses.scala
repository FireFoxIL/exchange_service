import ExchangeWebServer.complete
import akka.http.scaladsl.server.{Directives, StandardRoute}
import json_support.models.{ItemResponse, JsonResponse}
import org.apache.logging.log4j.scala.Logging

/**
  * Defines responses for different scenarios
  */
object Responses extends Logging {

  import json_support.JsonSupportServer._

  /**
    * Error code '0' - No errors
    */
  def ok(successful: List[ItemResponse]):StandardRoute = {
    logger.info(s"[0] No errors")
    complete(JsonResponse(successful, 0, "No errors"))
  }

  // Error responses

  /**
    * Error code '1' - Undefined
    */
  def undefined():StandardRoute = {
    logger.error(s"[1] Something went wrong")
    complete(JsonResponse(Nil, 1, "Something went wrong"))
  }

  /**
    * Error code '2' - Some requests failed
    */
  def someFailed(successful: List[ItemResponse], errors: String):StandardRoute = {
    logger.error(s"[2] Some requests failed [$errors]")
    complete(JsonResponse(successful, 2, s"Some requests failed [$errors]"))
  }

  /**
    * Error code '3' - All requests failed
    */
  def allFailed(errors: String): StandardRoute = {
    logger.error(s"[3] All requests failed [$errors]")
    complete(JsonResponse(Nil, 3, s"All requests failed [$errors]"))
  }


  /**
    * Error code '4' - Bad input
    */
  def badInput():StandardRoute = {
    logger.error(s"[4] Bad input from user")
    complete(JsonResponse(Nil, 4, "Bad input"))
  }
}
