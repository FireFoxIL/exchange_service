/**
  * Main method is defined here
  * */
object Launcher {

  /**
    * Entry point
    *
    * @param  args  command line arguments
    */
  def main(args: Array[String]): Unit = {
    ExchangeWebServer.start("localhost", 9000)
  }
}
