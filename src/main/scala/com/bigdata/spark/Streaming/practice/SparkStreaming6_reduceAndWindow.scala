package com.bigdata.spark.Streaming.practice

import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, ReceiverInputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}


object SparkStreaming6_reduceAndWindow {

  def main(args: Array[String]): Unit = {

//    SparkStreaming最少要两个核
    val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("Streaming")

    val ssc = new StreamingContext(sparkConf, Seconds(3))

    ssc.sparkContext.setCheckpointDir("cp")   //里计算的中间结果需要报错到检查点的位置中。所以需要设定检查点路径。

//    从socket拿数据
    val ds: ReceiverInputDStream[String] = ssc.socketTextStream("localhost",9999)

//    TODO 窗口
    val ints = Array(1,2,3,4,5,6,7,8)

    val wordToOneDS: DStream[(String, Int)] = ds.map(num => ("key",num.toInt))

      //reduceByKeyAndWindow用于重复数据的范围比较大的场合，这样可以优化效率
      val result: DStream[(String, Int)] = wordToOneDS.reduceByKeyAndWindow(
          (x, y) => {
              println(s"x=${x}, y = ${y}")
              x + y
          },
          (a, b) => {
              println(s"x=${a}, y = ${b}")
              a - b
          },
          Seconds(9)
      )
      result.foreachRDD(
          rdd => rdd.foreach(println)
      )





    result.print()



//    TODO 启动采集器
    ssc.start()

//    TODO 等待采集器结束
    ssc.awaitTermination()






  }
}
