package behiron.reproducebylog.sample
import behiron.reproducebylog._
import behiron.reproducebylog.utils.LoanPattern.using
import scala.io.Source

object accessLogSample {
  def main(args: Array[String]): Unit = {
    val reproduceByLog = ReproduceByLog(WriteStdOutTask, AccessLogTiming)
    /* reproduceByLog.setTimeOut(1000) */
    using(Source.fromFile(args(0))) { s =>
      reproduceByLog.run(s)
    }
  }
}

object accessLogExceptionSample {
  def main(args: Array[String]): Unit = {
    val reproduceByLog = ReproduceByLog(ExceptionTestTask, AccessLogTiming)
    /* reproduceByLog.setTimeOut(1000) */
    using(Source.fromFile(args(0))) { s =>
      reproduceByLog.run(s)
    }
  }
}
