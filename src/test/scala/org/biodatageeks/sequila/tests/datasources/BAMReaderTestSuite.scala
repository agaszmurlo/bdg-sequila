package org.biodatageeks.sequila.tests.datasources

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, SharedSparkContext}
import org.apache.spark.sql.SequilaSession
import org.biodatageeks.sequila.utils.{Columns, SequilaRegister}
import org.scalatest.{BeforeAndAfter, FunSuite}

class BAMReaderTestSuite extends FunSuite with DataFrameSuiteBase with BeforeAndAfter with SharedSparkContext{

  val bamPath = getClass.getResource("/NA12878.slice.bam").getPath
  //val bamPath = "/Users/marek/data/NA12878.chrom20.ILLUMINA.bwa.CEU.low_coverage.20121211.bam"

  val tableNameBAM = "reads"

  before{
    spark.sql(s"DROP TABLE IF EXISTS ${tableNameBAM}")
    spark.sql(
      s"""
         |CREATE TABLE ${tableNameBAM}
         |USING org.biodatageeks.sequila.datasources.BAM.BAMDataSource
         |OPTIONS(path "${bamPath}")
         |
      """.stripMargin)
  }

  test("Test point query predicate pushdown"){
    val ss = new SequilaSession(spark)
    SequilaRegister.register(ss)
    ss.sqlContext.setConf("spark.biodatageeks.bam.predicatePushdown","false")
//    val query =  """
//                   |SELECT * FROM reads WHERE contigName='20' AND start=59993
//                 """.stripMargin
    val query =  s"""
                       |SELECT * FROM reads WHERE ${Columns.CONTIG}='chr1' AND ${Columns.START}=20138
                     """.stripMargin
    val withoutPPDF = ss.sql(query).collect()

    ss.sqlContext.setConf("spark.biodatageeks.bam.predicatePushdown","true")
    val withPPDF = ss.sql(query)
    assertDataFrameEquals(ss.createDataFrame(ss.sparkContext.parallelize(withoutPPDF),withPPDF.schema),withPPDF)
    spark.time {
      ss.sqlContext.setConf("spark.biodatageeks.bam.predicatePushdown", "false")
      ss.sql(query).count
    }

  }

  test("Test interval query predicate pushdown"){
    val ss = new SequilaSession(spark)
    SequilaRegister.register(ss)
    ss.sqlContext.setConf("spark.biodatageeks.bam.predicatePushdown","false")
    val query =  s"""
                   |SELECT * FROM reads WHERE ${Columns.CONTIG}='chr1' AND ${Columns.START} >= 1996 AND ${Columns.END} <= 2071
                 """.stripMargin
    val withoutPPDF = ss.sql(query).collect()

    ss.sqlContext.setConf("spark.biodatageeks.bam.predicatePushdown","true")
    val withPPDF = ss.sql(query)
    assertDataFrameEquals(ss.createDataFrame(ss.sparkContext.parallelize(withoutPPDF),withPPDF.schema),withPPDF)
    spark.time {
      ss.sqlContext.setConf("spark.biodatageeks.bam.predicatePushdown", "false")
      ss.sql(query).show(50)
    }

  }
}
