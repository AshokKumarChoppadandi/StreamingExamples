name := "SparkElasticsearchExamples"

version := "0.1"

scalaVersion := "2.11.8"

// https://mvnrepository.com/artifact/org.elasticsearch/elasticsearch-spark-20
libraryDependencies += "org.elasticsearch" %% "elasticsearch-spark-20" % "7.3.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "2.4.0"

// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.0"