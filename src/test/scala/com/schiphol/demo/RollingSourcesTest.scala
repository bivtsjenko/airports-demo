package com.schiphol.demo

import com.schiphol.demo.RollingSources.readCSV
import org.apache.log4j.Logger
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FunSuite

class RollingSourcesTest extends FunSuite {

  val logger: Logger = Logger.getLogger(classOf[Nothing].getName)
  val conf: SparkConf = new SparkConf().setAppName("RollingSources").setMaster("local[*]")
  implicit val sc: SparkContext = SparkContext.getOrCreate(conf)
  implicit val sqlContext: SQLContext = new SQLContext(sc)


  test("testTop10Airports") {
    val inputFile: String = "src/test/resources/routes.dat"
    val fileDf = readCSV(inputFile)
    val countRows = RollingSources.top10Airports(fileDf).count()

    assert(countRows === 10)
  }

}
