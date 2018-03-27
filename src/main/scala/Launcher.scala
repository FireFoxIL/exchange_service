object Launcher {
  def main(args: Array[String]): Unit = {
    WebServer.start("localhost", 9000)
  }
}
