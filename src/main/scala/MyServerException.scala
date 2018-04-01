/**
  * Custom Server Exception
  *
  * @param  message Text message
  * @param  cause   Cause
  */
final case class MyServerException(message: String = "Undefined error",
                                   private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
