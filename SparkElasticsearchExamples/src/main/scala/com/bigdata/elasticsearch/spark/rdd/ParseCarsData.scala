package com.bigdata.elasticsearch.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}



object ParseCarsData {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("Parsing cars data")
    val sc = new SparkContext(sparkConf)

    val rdd1 = sc.textFile("/home/ashok/Desktop/Data/Cars.csv")
    val header = rdd1.take(1)(0)
    val rdd2 = rdd1.filter(line => !line.equals(header))
    val finalRdd = rdd2.map(x => parseLine(x))
    finalRdd.saveAsTextFile("/home/ashok/Desktop/Data/ParsedCars")
  }

  def parseLine(line: String): OldCar = {
    val record = line.split(",")
    val mileage = convertToInt(record(2))
    val manufactureYear = convertToInt(record(3))
    val engineDisplacement = convertToInt(record(4))
    val enginePower = convertToInt(record(5))
    val priceEUR = convertToDouble(record(15))

    OldCar(record(0), record(1), mileage, manufactureYear, engineDisplacement, enginePower, record(6), record(7),
      record(8), record(9), record(10), record(11), record(12), record(13), record(14), priceEUR)

  }

  def convertToInt(str: String): Int = {
    if(str.equals("") || str.length == 0) 0 else str.toInt
  }

  def convertToDouble(str: String): Double = {
    if(str.equals("") || str.length == 0) 0 else str.toDouble
  }

}
