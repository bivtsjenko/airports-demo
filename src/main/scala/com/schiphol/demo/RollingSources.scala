package com.schiphol.demo

import org.apache.log4j.Logger
import org.apache.spark.sql.functions.{split, _}
import org.apache.spark.sql.{DataFrame, SQLContext}
import org.apache.spark.{SparkConf, SparkContext}


object RollingSources {

  val logger: Logger = Logger.getLogger(classOf[Nothing].getName)

  def main(args: Array[String]): Unit = {

    val conf: SparkConf = new SparkConf().setAppName("RollingSources").setMaster("local[*]")
    implicit val sc: SparkContext = SparkContext.getOrCreate(conf)


    implicit val sqlContext: SQLContext = new SQLContext(sc)

    /* Set parameters */

    // Input
    val inputFile: String = "src/main/resources/routes.dat"

    // Output
    val writeToDisk: Boolean = true


    val fileDf = readCSV(inputFile)
    val top10Overview: DataFrame = top10Airports(input = fileDf)
    top10Overview.show()

    logger.info("Top10Overview" + top10Overview)


    //Write to filesystem


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
      .option("ignoreTrailingWhiteSpace", value = true)
      .load(filePath)

  }

  /**
   * Top 10 airports used as source.
   * Load Dataframe
   *
   * @return dataframe with top 10 source airports
   */

  def top10Airports(input: DataFrame): DataFrame = {

    //Groupby all distinct strings in column 2 and 4
    val columSeperatedDf = input.withColumn("temp", split(col("_c0"), "\\,")).select(
      (0 until 10).map(i => col("temp").getItem(i).as(s"col$i")): _*
    )
    val top10col2 = columSeperatedDf.groupBy("col2").count().sort(col("count").desc)
    val renamedAirportCol2 = top10col2.withColumnRenamed("col2", "airport")
    val renamedCountCol2 = renamedAirportCol2.withColumnRenamed("count", "oneway")

    val top10col4 = columSeperatedDf.groupBy("col4").count().sort(col("count").desc)
    val renamedAirportCol4 = top10col4.withColumnRenamed("col4", "airport")
    val renamedCountCol4 = renamedAirportCol4.withColumnRenamed("count", "otherway")

    //Give total amount of all routes by joining the two columns
    val joinedColumns = renamedCountCol2.join(renamedCountCol4, Seq("airport"))

    //Add one and otherway routes as a total
    val summedRoutes = joinedColumns.withColumn("totalRoutes", col("oneway") + col("otherway")).limit(10)


    summedRoutes


  }


}
