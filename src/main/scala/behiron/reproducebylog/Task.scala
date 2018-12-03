package behiron.reproducebylog

trait Task {
 /**
  * Caller assign a new thread for work method.
  * Exception thrown by work method are handled only if before timeOutMilliSecond.
  * Caller never abort thread of work method. So, you should implement timeout logic by yourself at work method if you want to abort thread when timeout occurs.
  */
  def work(record: String, lineNum: Int): Unit
}

object WriteStdOutTask extends Task {
  def work(record: String, lineNum: Int): Unit =  println(record)
}

object ExceptionTestTask extends Task {
  def work(record: String, lineNum: Int): Unit = {
    throw new RuntimeException("Exception test")
  }
}
