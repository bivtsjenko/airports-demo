package com.schiphol.demo

import org.apache.log4j.Logger
import org.apache.spark.sql.functions.{col, _}
import org.apache.spark.sql.streaming.DataStreamWriter
import org.apache.spark.sql.streaming.OutputMode.Update
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SparkSession}

object RollingSourcesStream {

  val logger: Logger = Logger.getLogger(classOf[Nothing].getName)

  def rollingSources(): Unit = {

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

    // Transform CSV file into streamed dataframe
    val streamedRoutes: DataFrame = readCsv(directory, spark.sqlContext, routesSchema)

    // Read streamedRoutes Dataframe and run streamed window aggregration over it.
    rollingWindows(streamedRoutes)


  }

  /**
   * Description:
   * Load a CSV file transform into a streamed DataFrame.
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
   * Top 10 airports within a window of 100 minutes.
   * Load Inputdir, Sqlcontext, Schema
   *
   */
  def rollingWindows(csvDf: DataFrame): Unit = {

    val windowDuration = s"100 minutes"
    val slideDuration = s"3 seconds"

    //Groupby windows on the AirlineId with the count on SourceAirport.
    //Since there is no timestamp column, AirlineId is converted into a timestamp field.
    val windowedAirlines: DataFrame = csvDf
      .groupBy(window(from_unixtime(col("AirlineID"), "yyyy-MM-dd HH:mm:ss"), windowDuration, slideDuration),
        col("SourceAirport"))
      .count()

    //Query write stream
    val query: DataStreamWriter[Row] = windowedAirlines.writeStream
      .format("console")
      .outputMode(Update)

    //Run stream
    query.start().awaitTermination()


  }


}
