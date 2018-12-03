package behiron.reproducebylog
import java.time.ZonedDateTime
import java.time.ZoneId

trait TaskTiming {
  /* if invalid record, return None */
  def getRawEpoch(record: String, lineNum: Int): Option[Long]
  /* you may override this if you want customize timing */
  def taskEpoch(startEpoch: Long, firstLineEpoch: Long, lineEpoch: Long): Long = startEpoch + (lineEpoch - firstLineEpoch)
  def currentEpoch(): Long = ZonedDateTime.now().toEpochSecond
}

object AccessLogTiming extends TaskTiming {
  val month3letter: Map[String, Int] = Map(
    "Jan" -> 1,
    "Feb" -> 2,
    "Mar" -> 3,
    "Apr" -> 4,
    "May" -> 5,
    "Jun" -> 6,
    "Jul" -> 7,
    "Aug" -> 8,
    "Sep" -> 9,
    "Oct" -> 10,
    "Nov" -> 11,
    "Dec" -> 12
  )

  /* example:  10.0.2.2 - - [16/Nov/2018:14:43:30 +0900] "POST /api/hoge HTTP/1.1" 500 19990 */
  val date = (
    """\[(\d{2})/(""" +
    month3letter.keys.mkString("|")  + 
    """)/(\d{4}):(\d{2}):(\d{2}):(\d{2}) (\+|-)(\d{4})\]"""
  ).r

  def getRawEpoch(record: String, lineNum: Int): Option[Long] = {
    for (m <- date.findFirstMatchIn(record)) yield {
      val year = m.group(3).toInt
      val month = month3letter(m.group(2))
      val dayOfMonth  = m.group(1).toInt
      val hour = m.group(4).toInt
      val minute = m.group(5).toInt
      val second = m.group(6).toInt
      (ZonedDateTime.of(
        year,
        month,
        dayOfMonth,
        hour,
        minute,
        second,
        0,
        ZoneId.systemDefault()
      )).toEpochSecond
    }
  }
}
