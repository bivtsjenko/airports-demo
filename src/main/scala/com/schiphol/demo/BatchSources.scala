package com.schiphol.demo

import org.apache.log4j.Logger
import org.apache.spark.sql.functions.{split, _}
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.apache.spark.{SparkConf, SparkContext}


object BatchSources {

  val logger: Logger = Logger.getLogger(classOf[Nothing].getName)

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setAppName("RollingSources").setMaster("local[*]")
    implicit val sc: SparkContext = SparkContext.getOrCreate(conf)


    implicit val sqlContext: SQLContext = new SQLContext(sc)

    /* Set parameters */

    // Input
    val inputPath: String = "src/main/resources/input/routes.dat"
    val outputPath: String = "src/main/resources/top10/"


    // Output
    val writeToDisk: Boolean = true

    // Read source file
    val fileDf = readCSV(inputPath)

    // Top 10 airports used as source airport
    val top10Overview: DataFrame = top10Airports(input = fileDf)


    // Write to filesystem
    if (writeToDisk) {
      top10Overview.write.format("csv").mode(SaveMode.Overwrite).save(outputPath)
    }

  }

  /**
   * Description:
   * Load a CSV file into a DataFrame.
   */
  def readCSV(filePath: String)(implicit sqlContext: SQLContext): DataFrame = {
    sqlContext.read.format("csv")
      .option("header", "false")
      .option("inferSchema", "false")
      .option("delimiter", "\u0001")

      .load(filePath)

  }

  /**
   * Top 10 airports used as source.
   * Load Dataframe
   *
   * @return Dataframe with top 10 source airports
   */

  def top10Airports(input: DataFrame): DataFrame = {

    //Groupby all distinct strings in column 2 and 4
    val columSeperatedDf = input.withColumn("temp", split(col("_c0"), "\\,")).select(
      (0 until 10).map(i => col("temp").getItem(i).as(s"col$i")): _*
    )

    //Grouping and renaming in col2
    val top10col2 = columSeperatedDf.groupBy("col2").count().sort(col("count").desc)
    val renamedAirportCol2 = top10col2.withColumnRenamed("col2", "airport")
    val renamedCountCol2 = renamedAirportCol2.withColumnRenamed("count", "routes")


    renamedCountCol2.limit(10)


  }


}
