import scala.collection.immutable.Map
import kafka.serializer.StringDecoder
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.apache.spark.SparkConf

object Events {
  def createContext(): StreamingContext = {
    val sparkConf = new SparkConf()//.set("spark.streaming.receiver.writeAheadLog.enable", "true")
      .set("spark.task.maxFailures", "200")
      .setAppName("Events")

    val ssc = new StreamingContext(sparkConf, Seconds(1))
    
    val params = Map(
      "metadata.broker.list" -> "localhost:9092",
      "auto.offset.reset" -> "smallest"
    )

    val topics = Set("events")

    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, params, topics).map(_._2)    

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
    
    ssc.checkpoint("/tmp")
    ssc
  }

  def main(args: Array[String]) = {
    val ssc = StreamingContext.getOrCreate("/tmp", createContext)
    ssc.start()
    ssc.awaitTermination()
  }
}
