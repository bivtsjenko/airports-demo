package com.schiphol.demo

import org.apache.log4j.Logger
import org.apache.spark.sql.functions.{col, _}
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.streaming.OutputMode.Update
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SparkSession}

object RollingSourcesStream {

  val logger: Logger = Logger.getLogger(classOf[Nothing].getName)

  def rollingSources(args: Array[String]): Unit = {

    val spark: SparkSession = SparkSession
      .builder
      .appName("RollingSourcesSteaming")
      .config("spark.master", "local")
      .getOrCreate()


    /* Set parameters */

    // Input
    val directory: String = "src/main/resources/input"

    // Schema routes
    val routesSchema: StructType = new StructType()
      .add("Airline", "string")
      .add("AirlineID", "integer")
      .add("SourceAirport", "string")
      .add("SourceAirportId", "string")
      .add("DestAirport", "string")
      .add("destAirId", "string")
      .add("CodeShare", "string")
      .add("Stops", "integer")
      .add("Equipment", "string")

    // Transform csv file into streamed dataframe
    val streamedRoutes = readCsv(directory, spark.sqlContext, routesSchema)
    rollingWindows(streamedRoutes)


  }

  /**
   * Description:
   * Load a CSV file into a streamed DataFrame.
   */
  def readCsv(inputDir: String, sc: SQLContext, schema: StructType): DataFrame = {
    val csvDF: DataFrame = sc
      .readStream
      .format("csv")
      .option("header", "false")
      .option("delimiter", ",")
      .schema(schema)
      .load(inputDir)
    csvDF
  }

  /**
   * Top 10 airports for every window of 1 sec.
   * Load Inputdir, Sqlcontext, schema
   *
   * @return Stream of windows
   */
  def rollingWindows(csvDf: DataFrame): Unit = {

    val windowDuration = s"1 seconds"
    val slideDuration = s"1 seconds"

    //groupby windows on the AirlineId with the count on SourceAirport
    val windowedAirlines: DataFrame = csvDf
      .groupBy(window(from_unixtime(col("AirlineID"), "yyyy-MM-dd HH:mm:ss"), windowDuration, slideDuration),
        col("SourceAirport"))
      .count()

    //query write stream
    val query: DataStreamWriter[Row] = windowedAirlines.writeStream
      .format("console")
      .outputMode(Update)

    query.start.awaitTermination()


  }


}
