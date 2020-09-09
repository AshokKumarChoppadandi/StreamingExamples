package com.bigdata.spark.streaming.utils

import java.util.Date

import org.apache.hadoop.fs.DF
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.DD
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.catalyst.plans.logical.Distinct
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._

//import org.training.spark.practice.Sales
object aadharCsvReader {
  def main(args: Array[String]): Unit = {
    //1.	Describe the schema.
    val conf = new SparkConf().setMaster("local").setAppName("aadharCsvReader")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    val schema = StructType(Array(
      StructField("tDate", StringType, true),
      StructField("Register", StringType, true),
      StructField("Private_Agency", StringType, true),
      StructField("State", StringType, true),
      StructField("District", StringType, true),
      StructField("Sub_District", StringType, true),
      StructField("Pincode", IntegerType, true),
      StructField("Gender", StringType, true),
      StructField("Age", IntegerType, true),
      StructField("Aadhar_Genereted", IntegerType, true),
      StructField("Rejected", IntegerType, true),
      StructField("Mobile_Number", IntegerType, true),
      StructField("Email_Id", IntegerType, true)))
    val aadharSrcDF = sqlContext.read.format("csv").option("header", "true").option("inferSchema", "true").schema(schema)
      .load("/home/cloudera/Desktop/aadhaar_data_small.csv")
    aadharSrcDF.show()
    aadharSrcDF.printSchema()
    //SQL WAY
    aadharSrcDF.registerTempTable("test")
    val aadharClnDataDF1 = sqlContext.sql("select TO_DATE(cast(from_unixtime(unix_timestamp(tDate,'yyyyMMdd'),'yyyy-MM-dd') as Date)) as Actual_Date,Register,Private_Agency,State,District,Sub_District,Pincode,Gender,Age,Aadhar_Genereted,Rejected,Mobile_Number,Email_Id from test")
    aadharClnDataDF1.show()

    //DSL
    val dslaadharClnDataDF2 = aadharClnDataDF1.select("Actual_Date","Register","Private_Agency","State","District","Sub_District","Pincode","Gender","Age","Aadhar_Genereted","Rejected","Mobile_Number","Mobile_Number")
    dslaadharClnDataDF2.show()
    val maxdateDF = dslaadharClnDataDF2.select(max("Actual_Date")).as("Max_date")
    maxdateDF.show()
    val mindateDF = dslaadharClnDataDF2.select(min("Actual_Date")).as("Min_date")
    mindateDF.show()


    /*csvDF1.registerTempTable("test")
    val csvDF2 = csvDF.selectExpr("tDate  as Actual_Date")
    csvDF2.show()*/


    //2.	Find the count and names of registrars in the table.

    /*val countDF = csvDF.groupBy("Register").count()
    countDF.show()
    countDF.printSchema()*/

    //3.	Find the number of states, districts in each state and sub-districts in each district.

    /*val groupDF = csvDF.groupBy("State", "Distinct", "Sub_Distinct").count()
    groupDF.show()
    groupDF.printSchema()*/


    //4.	Find the number of males and females in each state from the table.

    /* val  groupDF1 = csvDF.groupBy("State","Gender").count()
    groupDF1.show()
s    groupDF1.printSchema()*/


    //5.	Find out the names of private agencies for each state
    /*val fifthDF = csvDF.groupBy("Private_Agency", "State").count()
    fifthDF.show()
    fifthDF.printSchema()*/


    //1.	Find top 3 states generating most number of Aadhaar cards?

    //csvDF.registerTempTable("test")
    /*val windowDF = sqlContext.sql("select State from test group by State limit 2")
    windowDF.show()
    windowDF.printSchema()
  }
}*/

    //2.	Find top 3 private agencies generating the most number of Aadhar cards?


    /* val windowDF1 = sqlContext.sql("select Private_Agency from test group by Private_Agency limit 2")
    windowDF1.show()
    windowDF1.printSchema()
  }
}*/

    //3.	Find the number of unique pincodes in the data?
    //csvDF.registerTempTable("test")
    /*val DF = sqlContext.sql("select distinct Pincode from test")
    DF.show()
    DF.printSchema()

  }
}*/

    // 4.	Find the number of Aadhaar registrations rejected in Uttar Pradesh and Maharashtra?
    /*val DF = csvDF.groupBy("Uttar Pradesh","Maharashtra").sum("Rejected")
    DF.show()
    DF.printSchema()not work

*/
    // 4.	Find top 3 districts where enrolment numbers are maximum?

    /*val topDistrictDF = sqlContext.sql("select count(Register) as Reg_cnt , District from test group by District order by Reg_cnt desc limit 3")

    topDistrictDF.show()
  }
}*/
    //5.	Find the no. of Aadhaar cards generated in each state?
    /*val generateDF = sqlContext.sql("select count(Aadhar_Genereted) as generate_cnt,State from test group by State order by generate_cnt limit 1")
    generateDF.show()
  }
}
or
val generateDF1 = sqlContext.sql("select max(Aadhar_Genereted) as generate_cnt,State from test group by State order by generate_cnt desc ")
generateDF1.show()
}
}
or
val topDF = sqlContext.sql("select State from test limit 3").agg(max("AAadhar_Genereted"))
      //col("State").agg(max("Aadhar_Genereted")).take(3))
*/
    //val disDF = sqlContext.sql("select count(*) as cnt Register from test group by Register order by cnt desc limit 2")
    //val DF = csvDF.orderBy(col("State").desc).limit(3).show()


    /*val topDF = csvDF.groupBy("State").agg(sum("Aadhar_Genereted").as("Aadhar_Genereted_count")).limit(3)
    topDF.show()
    topDF.printSchema()*/

    //val byDepName = Window.partitionBy("State").orderBy(desc("Aadhar_Genereted").as(""))


    // Checkpoint 5
    //1•	The top 3 states where the percentage of Aadhaar cards being generated for males is the highest.

    //val malesDF = csvDF.where("Gender like 'M'").groupBy("State").agg(sum("Aadhar_Genereted")).limit(3)
    //malesDF.printSchema()
    //malesDF.show()

    //2•	In each of these 3 states, identify the top 3 districts where the percentage of Aadhaar cards being rejected for females is the highest.
    /*val distDF = csvDF.where("Gender like 'F'").groupBy("State","Distinct").agg(sum("Rejected"))
    distDF.printSchema()
    distDF.show()*/
    //3•	The top 3 states where the percentage of Aadhaar cards being generated for females is the highest
    // val FemaleDF = csvDF.where("Gender like 'F'").groupBy("State").agg(sum("Aadhar_Genereted")).limit(3)
    //FemaleDF.printSchema()
    //FemaleDF.show()
    //4•	In each of these 3 states, identify the top 3 districts where the percentage of Aadhaar cards being rejected for males is the highest.
    //val distDF1 = csvDF.where("Gender like 'M'").groupBy("State","Distinct").agg(sum("Rejected"))
    //distDF1.printSchema()
    //distDF1.show()
    /*}

}*/

    case class Aaadhar(tDate:String,Register:String,Private_Agency:String,State:String,District:String)
    //val textFileRDD = sc.textFile("/home/cloudera/Desktop/aadhaar_data.csv")
    //val salesRDD = textFileRDD.filter(rec => rec != textFileRDD.first())
    //val salesRDD2 = textFileRDD.filter(rec => !rec.startsWith("Date"))
    /*def toAaadhar(x: String) = {
      val words = x.split(",")
      val tDate = words(0).toString
      val Register = words(1).toString
      val Private_Agency = words(2).toString
      val State = words(3).toString
      val District = words(4).toString

    }
    val e = Aaadhar(tDate,Register,Private_Agency,State,District)

    val schemaRDD = salesRDD.map(rec => {
      val colArr = rec.split(",")
      (colArr(0).toString,colArr(1).toString,colArr(2).toString,colArr(3).toString,colArr(4).toString)
    })
    val salesDF = sqlContext.createDataFrame(schemaRDD)

    //import sqlContext.implicits._

    //val salesDF2 = schemaRDD.toDF

    salesDF.show()
    salesDF.printSchema()*/
  }
}