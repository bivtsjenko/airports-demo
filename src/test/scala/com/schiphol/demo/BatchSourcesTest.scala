package com.schiphol.demo

import java.nio.file.{Files, Paths}

import com.schiphol.demo.BatchSources.readCSV
import org.apache.log4j.Logger
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.FunSuite

class BatchSourcesTest extends FunSuite {

  val logger: Logger = Logger.getLogger(classOf[Nothing].getName)
  val conf: SparkConf = new SparkConf().setAppName("RollingSources").setMaster("local[*]")
  implicit val sc: SparkContext = SparkContext.getOrCreate(conf)
  implicit val sqlContext: SQLContext = new SQLContext(sc)


  test("testTop10Airports") {

    val inputPath: String = "src/main/resources/input/routes.dat"
    val fileDf = readCSV(inputPath)


    readCSV(inputPath)


    //assert output file exists
    val fileExists = Files.exists(Paths.get("src/main/resources/output/top10airports.csv"))

    assert(fileExists === true)

    //Should be 10 rows
    val countRows = BatchSources.top10Airports(fileDf).count()

    assert(countRows === 10)

  }

}
