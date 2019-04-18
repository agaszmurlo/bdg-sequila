package org.biodatageeks.apps

import htsjdk.samtools.ValidationStringency
import org.apache.hadoop.io.LongWritable
import org.apache.spark.sql.SparkSession
import org.biodatageeks.rangejoins.IntervalTree.IntervalTreeJoinStrategyOptim
import org.rogach.scallop.ScallopConf
import org.seqdoop.hadoop_bam.{BAMInputFormat, SAMRecordWritable}
import org.seqdoop.hadoop_bam.util.SAMHeaderReader

import org.apache.spark.sql.SequilaSession
import org.biodatageeks.utils.{SequilaRegister, UDFRegister,BDGInternalParams}





object DepthOfCoverage {

  case class Region(contigName:String, start:Int, end:Int)

  class RunConf(args:Array[String]) extends ScallopConf(args){

    val output = opt[String](required = true)
    val readsFile = trailArg[String](required = true)
    val format = opt[String](required = true)
    verify()
  }


  def main(args: Array[String]): Unit = {
    val runConf = new RunConf(args)
    val spark = SparkSession
      .builder()
      .appName("SeQuiLa-DoC")
      .getOrCreate()


    spark
      .sparkContext
      .setLogLevel("WARN")

    spark
      .sparkContext
      .hadoopConfiguration.set(SAMHeaderReader.VALIDATION_STRINGENCY_PROPERTY, ValidationStringency.SILENT.toString)


    val sample = spark
      .sparkContext.newAPIHadoopFile[LongWritable, SAMRecordWritable, BAMInputFormat](runConf.readsFile())
      .map(_._2.get).first().getReadGroup().getSample

    val alignments = spark
      .sparkContext.newAPIHadoopFile[LongWritable, SAMRecordWritable, BAMInputFormat](runConf.readsFile())
      .map(_._2.get)
      .map(r => Region(r.getContig, r.getStart, r.getEnd))
    val ss = SequilaSession(spark)
    SequilaRegister.register(ss)

    val readsTable = ss.sqlContext.createDataFrame(alignments)
    readsTable.createOrReplaceTempView("reads")


    println(s">>>> Analyzed $sample")




    val query = "SELECT * FROM bdg_coverage('reads', '%s', '%s')".format(sample, runConf.format)


    ss.sql(query)
      .orderBy("chr")
      .coalesce(1)
      .write
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(runConf.output())
  }

}
