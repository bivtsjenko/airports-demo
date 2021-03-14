package com.schiphol.demo

import org.apache.log4j.Logger
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SQLContext, SparkSession}
import org.apache.spark.sql.streaming.OutputMode.Update
import org.apache.spark.sql.streaming.StreamingQuery

object RollingSourcesStream {

  val logger: Logger = Logger.getLogger(classOf[Nothing].getName)

  def main(args: Array[String]): Unit = {

    val spark: SparkSession = SparkSession
      .builder
      .appName("RollingSourcesSteaming")
      .config("spark.master", "local")

      .getOrCreate()




    /* Set parameters */

    // Input
    val directory: String = "src/main/resources/input"
    val checkpointLocation: String = "src/main/resources/tmp/checkpoint"
    val inputPath: String = "src/main/resources/routes.dat"
    val outputPath: String = "src/main/resources/top10stream/"

    // Output
    val writeToDisk: Boolean = true

    // Read source file


    val fileDf = readCsvStream(directory, spark.sqlContext)

    //    fileDf
    //
    //      .writeStream
    //      .option("checkpointLocation", checkpointLocation )
    //      .start(directory)
    //
    //
    //    logger.info(s"Logging in user ${fileDf.count()}")


  }

  /**
   * Description:
   * Load a CSV file into a DataFrame.
   */
  def readCsvStream(inputDir: String, sc: SQLContext): Unit = {

    val userSchema = new StructType().add("Airline", "string").add("AirlineID", "string").add("SourceAirport", "string").add("SourceAirportId", "string").add("DestAirport", "string").add("destAirId", "string").add("CodeShare", "string").add("Stops", "integer").add("Equipment", "string")
    case class Order(id: Long, zipCode: String)

    val csvDF: Unit = sc
      .readStream
      .format("csv")
      .option("header", "false")
      .option("delimiter", ",")
      .schema(userSchema)
      .load(inputDir)

      .writeStream
      .format("console")
      .outputMode(Update) // <-- update output mode
      .start.awaitTermination()




  }


}
