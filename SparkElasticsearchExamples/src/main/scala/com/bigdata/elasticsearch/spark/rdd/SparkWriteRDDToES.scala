package com.bigdata.elasticsearch.spark.rdd

import org.apache.spark.sql.{SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}
import org.elasticsearch.spark.sql._

object SparkWriteRDDToES {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf()
      .setMaster("local")
      .setAppName("Write RDD To Elasticsearch")

    val sparkContext = new SparkContext(sparkConf)
    sparkContext.setLogLevel("ERROR")
    val sqlContext = new SQLContext(sparkContext)

    import sqlContext.implicits._

    val rdd = sparkContext.textFile("/home/ashok/Desktop/Data/Cars_Test.csv")
    val header = rdd.take(1)(0)
    val columns = header.split(",")
    val rdd2 = rdd.filter(x => !x.equals(header))
    val rdd3 = rdd2.map(x => x.split(","))
    val rdd4 = rdd3.map(x => (x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8), x(9), x(10), x(11), x(12), x(13), x(14), x(15)))
    val df1 = rdd4.toDF(columns: _*)

    df1.show

    df1
      .write
      .format("org.elasticsearch.spark.sql")
      .option("es.nodes", "localhost")
      .option("es.port", "9200")
      .option("spark.es.nodes.wan.only", "true")
      .mode(SaveMode.Append)
      .save("cars2/old_cars")
  }
}
