package behiron.reproducebylog
import scala.io.Source
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import java.util.concurrent.TimeoutException


case class ReproduceByLog(task: Task, taskTiming: TaskTiming) {

  private var firstLineEpoch:Long = 0
  private var startEpoch:Long = 0
  private var timeOutMilliSecond:Long = 10000

  def run(source: Source) : Unit = {
    var lineNum:Int = 1
    for (record <- source.getLines) {
      taskTiming.getRawEpoch(record, lineNum) match {
        case Some(epoch) => {
          if (firstLineEpoch == 0) {
            firstLineEpoch = epoch
            startEpoch = taskTiming.currentEpoch()
          }
          applyTask(
            taskTiming.taskEpoch(startEpoch, firstLineEpoch, epoch),
            record,
            lineNum
          )
        }
        case None => invalidLog(record, lineNum)
      }
      lineNum += 1
    }
    /* wait 2 * timeOutMilliSecond just in case */
    Thread.sleep(2 * timeOutMilliSecond)
  }

  def setTimeOut(milliSecond: Long) = {
    timeOutMilliSecond = milliSecond
  }

  protected def applyTask(epoch: Long, record: String, lineNum: Int): Unit = {
    val currentEpoch = taskTiming.currentEpoch()
    if (currentEpoch < epoch) Thread.sleep((epoch - currentEpoch) * 1000)
    val futureTask: Future[Unit] = Future {
      task.work(record, lineNum)
    }
    /**
     * Codes below never cancel thread of futureTask even if timeout occures, just call timeOutLog, and call exceptionLog only if futureTask fails before timeout occures.
     */

    Future {
      try {
        Await.result(futureTask, Duration(timeOutMilliSecond, MILLISECONDS))
      } catch {
        case e: TimeoutException => timeOutLog(record, lineNum)
        case e  => exceptionLog(record, lineNum, e)
      }
    }
  }

  protected def invalidLog(record: String, lineNum: Int) : Unit = {
    System.err.println(
      s"invalid record: [${lineNum}]${record}"
    )
  }

  protected def timeOutLog(record: String, lineNum: Int) : Unit = {
    System.err.println(
      s"timeout record: [${lineNum}]${record}"
    )
  }

  protected def exceptionLog(record: String, lineNum: Int, e: Throwable) : Unit = {
    System.err.println(
      s"exception record: [${lineNum}]${record} throws ${e.getMessage}"
    )
  }
}
