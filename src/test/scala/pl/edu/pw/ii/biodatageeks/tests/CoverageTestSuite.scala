package pl.edu.pw.ii.biodatageeks.tests

import java.io.{OutputStreamWriter, PrintWriter}

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, SharedSparkContext}
import org.apache.spark.sql.{SequilaSession, SparkSession}
import org.bdgenomics.utils.instrumentation.{Metrics, MetricsListener, RecordedMetrics}
import org.biodatageeks.preprocessing.coverage.CoverageStrategy
import org.biodatageeks.utils.{BDGInternalParams, SequilaRegister}
import org.scalatest.{BeforeAndAfter, FunSuite}

class CoverageTestSuite extends FunSuite with DataFrameSuiteBase with BeforeAndAfter with SharedSparkContext{

    val bamPath = getClass.getResource("/NA12878.slice.bam").getPath
    val bamMultiPath = getClass.getResource("/multichrom/NA12878.multichrom.bam").getPath
    val adamPath = getClass.getResource("/NA12878.slice.adam").getPath
    val metricsListener = new MetricsListener(new RecordedMetrics())
    val writer = new PrintWriter(new OutputStreamWriter(System.out))
    val cramPath = getClass.getResource("/test.cram").getPath
    val refPath = getClass.getResource("/phix-illumina.fa").getPath
    val tableNameBAM = "reads"
    val tableNameMultiBAM = "readsMulti"
    val tableNameADAM = "readsADAM"
    val tableNameCRAM = "readsCRAM"
    before{

      Metrics.initialize(sc)
      sc.addSparkListener(metricsListener)
      System.setSecurityManager(null)
      spark.sql(s"DROP TABLE IF EXISTS ${tableNameBAM}")
      spark.sql(
        s"""
           |CREATE TABLE ${tableNameBAM}
           |USING org.biodatageeks.datasources.BAM.BAMDataSource
           |OPTIONS(path "${bamPath}")
           |
      """.stripMargin)

      spark.sql(s"DROP TABLE IF EXISTS ${tableNameMultiBAM}")
      spark.sql(
        s"""
           |CREATE TABLE ${tableNameMultiBAM}
           |USING org.biodatageeks.datasources.BAM.BAMDataSource
           |OPTIONS(path "${bamMultiPath}")
           |
      """.stripMargin)

      spark.sql(s"DROP TABLE IF EXISTS ${tableNameCRAM}")
      spark.sql(
        s"""
           |CREATE TABLE ${tableNameCRAM}
           |USING org.biodatageeks.datasources.BAM.CRAMDataSource
           |OPTIONS(path "${cramPath}", refPath "${refPath}")
           |
      """.stripMargin)

      spark.sql(s"DROP TABLE IF EXISTS ${tableNameADAM}")
      spark.sql(
        s"""
           |CREATE TABLE ${tableNameADAM}
           |USING org.biodatageeks.datasources.ADAM.ADAMDataSource
           |OPTIONS(path "${adamPath}")
           |
      """.stripMargin)

    }


  test("BAM - bdg_coverage - windows"){

    spark.sqlContext.setConf(BDGInternalParams.InputSplitSize,"100000")

    val session: SparkSession = SequilaSession(spark)
    SequilaRegister.register(session)

    val windowLength = 100
    val bdg = session.sql(s"SELECT * FROM bdg_coverage('${tableNameBAM}','NA12878', '', '${windowLength}')")
    //bdg.show(5)

    bdg.where("start >= 3100 AND start <= 3300").show()



//    assert (bdg.count == 300)
//    assert (bdg.first().getInt(1) % windowLength == 0)
//    assert (bdg.first().getInt(2) % windowLength == windowLength - 1)
//    assert(bdg.where("start == 23500").first().getFloat(3)==0.52.toFloat)
//    assert(bdg.where("start == 2700").first().getFloat(3)==4.65.toFloat)
//    assert(bdg.where("start == 25100").first().getFloat(3)==0.0.toFloat)

  }
//
//  test("BAM - bdg_coverage - blocks - allPositions"){
//    val session: SparkSession = SequilaSession(spark)
//    SequilaRegister.register(session)
//    session.experimental.extraStrategies = new CoverageStrategy(session) :: Nil
//
//
//    session.sqlContext.setConf(BDGInternalParams.ShowAllPositions,"true")
//    val bdg = session.sql(s"SELECT * FROM bdg_coverage('${tableNameMultiBAM}','NA12878', 'blocks')")
//    bdg.show(5)
//
//    assert(bdg.count() == 12865)
//    assert(bdg.first().get(1) == 1) // first position should be one
//    assert(bdg.where("start == 257").first().getShort(3) == 2)
//
//  }
//
//  test("BAM - bdg_coverage - blocks notAllPositions"){
//    val session: SparkSession = SequilaSession(spark)
//    SequilaRegister.register(session)
//
//    session.sqlContext.setConf(BDGInternalParams.ShowAllPositions,"false")
//    val bdg = session.sql(s"SELECT *  FROM bdg_coverage('${tableNameMultiBAM}','NA12878', 'blocks')")
//    bdg.show(5)
//
//    assert(bdg.count() == 12861)
//    assert(bdg.first().get(1) != 1) // first position should not be one
//    assert(bdg.where("start == 35").first().getShort(3) == 2)
//
//  }
//
//  test("BAM - bdg_coverage - bases - notAllPositions"){
//    val session: SparkSession = SequilaSession(spark)
//    SequilaRegister.register(session)
//
//    session.sqlContext.setConf(BDGInternalParams.ShowAllPositions,"false")
//    val bdg = session.sql(s"SELECT contigName, start, end, coverage FROM bdg_coverage('${tableNameMultiBAM}','NA12878', 'bases')")
//    bdg.show(5)
//
//    assert(bdg.count() == 26598)
//    assert(bdg.first().get(1) != 1) // first position should not be one
//    assert(bdg.where("start == 88").first().getShort(3) == 7)
//  }
//
//  test("CRAM - bdg_coverage - show"){
//    val session: SparkSession = SequilaSession(spark)
//    SequilaRegister.register(session)
//
//    val bdg = session.sql(s"SELECT * FROM bdg_coverage('${tableNameCRAM}','test', 'blocks') ")
//    bdg.show(5)
//
//    assert(bdg.count() == 49)
//    assert(bdg.where("start == 107").first().getShort(3) == 459)
//  }
//
//  test("BAM - bdg_coverage - wrong param, Exception should be thrown") {
//      val session: SparkSession = SequilaSession(spark)
//      SequilaRegister.register(session)
//
//      assertThrows[Exception](
//        session.sql(s"SELECT * FROM bdg_coverage('${tableNameMultiBAM}','NA12878', 'blaaaaaah')").show())
//
//    }


//    test("Repartitioning"){
//      spark.sqlContext.setConf(BDGInternalParams.InputSplitSize,"100000")
//      val  ss = SequilaSession(spark)
//      SequilaRegister.register(ss)
//
//
//      val a = ss.sql(
//        s"""
//          |SELECT * FROM ${tableNameBAM}
//        """.stripMargin)
//      println(s"""Partitions number: ${a
//        .rdd
//        .partitions.length}""" )
//
//    }

  after{

    Metrics.print(writer, Some(metricsListener.metrics.sparkMetrics.stageTimes))
    writer.flush()
    Metrics.stopRecording()
    //writer.

  }

}
