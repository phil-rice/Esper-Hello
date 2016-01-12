package org.validoc.esperHello

import com.espertech.esper.client._
import com.espertech.esper.client.EPAdministrator
import com.espertech.esper.client.UpdateListener
import com.espertech.esper.client.EPListenable
import com.espertech.esper.client.EPServiceProvider
import java.util.Date
import scala.beans.BeanProperty
import scala.util.Random

//https://coffeeonesugar.wordpress.com/2009/07/21/getting-started-with-esper-in-5-minutes/
case class Tick(@BeanProperty symbol: String, @BeanProperty price: Double, @BeanProperty timestamp: Date) {
  def this(symbol: String, price: Double, timestamp: Long) = this(symbol, price, new Date(timestamp))
}

object EsperHello {

  val generator = new Random();

  def generateRandomTick(cepRT: EPRuntime) {
    val price = generator.nextInt(10).toDouble;
    val timeStamp = System.currentTimeMillis();
    val symbol = "AAPL";
    val tick = new Tick(symbol, price, timeStamp);
    System.out.println("Sending tick:" + tick);
    cepRT.sendEvent(tick);
  }

  object EPStatementPimp {
    implicit def pimp(s: EPStatement) = new EPStatementPimp(s)
  }

  class EPStatementPimp(statement: EPStatement) {
    def update(fn: (Array[EventBean], Array[EventBean]) => Unit) {
      statement.addListener(new UpdateListener() {
        def update(newData: Array[EventBean], oldData: Array[EventBean]) = fn(newData, oldData)
      })
    }
    def update(fn: Array[EventBean] => Unit) {
      statement.addListener(new UpdateListener() {
        def update(newData: Array[EventBean], oldData: Array[EventBean]) = fn(newData)
      })

    }

  }

  def main(args: Array[String]) {

    //The Configuration is meant only as an initialization-time object.
    val cepConfig = new Configuration();
    // We register Ticks as objects the engine will have to handle
    cepConfig.addEventType("StockTick", classOf[Tick]);

    // We setup the engine
    val cep = EPServiceProviderManager.getProvider("myFirstCEPEngine", cepConfig);
    val cepRT: EPRuntime = cep.getEPRuntime();

    val cepAdm = cep.getEPAdministrator();
    val cepStatement = cepAdm.createEPL("select * from StockTick(symbol='AAPL').win:length(2) having avg(price) > 6.0");
    import EPStatementPimp._

    cepStatement.update { newData => System.out.println("Event received: " + newData(0).getUnderlying()) }
//
//    cepStatement.addListener(new UpdateListener() {
//      def update(newData: Array[EventBean], oldData: Array[EventBean]) = {
//        System.out.println("Event received: " + newData(0).getUnderlying());
//      }
//    })

    while (true) {
      for (_ <- (0 to 15))
        generateRandomTick(cepRT)
      Thread.sleep(1000)
    }
  }
}
