package behiron.reproducebylog.utils

object LoanPattern {
  def using[A <: {def close(): Unit}](s: A)(f: A => Any): Any = {
    try {
      f(s)
    } finally {
      s.close()
    }
  }
}
