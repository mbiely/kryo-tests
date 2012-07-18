package ch.epfl.lsr.testing.common

import org.apache.commons.math.stat.descriptive.SummaryStatistics

trait SimpleSummaryStats { 
  def getIdentifier :String
  val discardFor :Int
  val collectFor :Int

  private var printedReport = false
  private val stats = new SummaryStatistics
  private val simStart :Long = System.nanoTime()
  private var firstRequestTS: Long = -1
  private var lastRequestTS: Long = -1
  private lazy val discardUntil = simStart + discardFor.toLong*1000l*1000l*1000l
  private lazy val reportAfter = discardUntil +collectFor.toLong*1000l*1000l*1000l

  def recordEvent { 
    val now = System.nanoTime()
    if (now > discardUntil) { 
      if (firstRequestTS == -1) {
        firstRequestTS = now;
      } else { 
        val duration = ((now - lastRequestTS))/1000/1000;
        stats.addValue(duration)
      }
      lastRequestTS = now;
    } 
    if (now > reportAfter) { 
      if(!printedReport) { 
	printedReport = true
	println(report)
	}
    }
  }

  def report = { 
    val duration = (lastRequestTS - firstRequestTS)/1000.0/1000/1000;
    val thrpt = stats.getN() / duration;  //"CLIENT "+clientID+" finishing time "+ System.nanoTime()+" last request "+lastRequestTS+" \n"+
    val str = "%s %6d %6.2f %6.2f %6.2f %6.2f %6.2f".format(getIdentifier, stats.getN(), thrpt, stats.getMean(), stats.getStandardDeviation(), stats.getMin(), stats.getMax())
    str
  }
}
