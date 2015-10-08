import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf

class Events {
  def createContext(): StreamingContext = {
    val sparkConf = new SparkConf().set("spark.streaming.receiver.writeAheadLog.enable", "true")
                                   .setAppName("Events")

    val ssc = new StreamingContext(sparkConf, Seconds(1))
    
    val messages = KafkaUtils.createStream(ssc, "localhost", "Events", Map("events" -> 1)).map(_._2)
    
    ssc.checkpoint("/tmp")

    /* for each mini-batch in the stream*/
    messages.foreachRDD({ rdd =>
      /* for each worker's local portion */
      rdd.foreachPartition({ partition =>
        val r = new scala.util.Random().nextInt(100)
        if (r % 2 == 0) {
          throw new Exception()
        }
        /* for each event in the local partition*/
        partition.foreach(println)
      })
    })
    
    ssc
  }

  def main(args: Array[String]) = {
    val ssc = StreamingContext.getOrCreate("/tmp", createContext)
    ssc.start()
    ssc.awaitTermination()
  }
}
