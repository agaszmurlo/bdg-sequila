.. bdg-spark-granges documentation master file, created by
   sphinx-quickstart on Fri Mar  9 22:03:23 2018.
   You can adapt this file completely to your liking, but it should at least
   contain the root `toctree` directive.

SeQuiLa documentation
======================

SeQuiLa is an ANSI-SQL compliant solution for efficient genomic intervals querying and processing.  

* SeQuiLa is fast:

   - genome-size analysis in several minutes 
   - 22x speedup against Spark default processing
   - 100% accuracy in functional tests against GRanges

* SeQuiLa is elastic:

   - growing catalogue of utility functions and operations including: `featureCounts`, `countOverlaps` and `coverage`
   - exposed parameters for further performance optimizations 
   - integration with third-party tools through SparkSQL JDBC driver 
   - can be used natively in R using SparkR tool 
   - possibility to use SeQuiLa as command line tool without any exposure to Scala/Spark/Hadoop


* SeQuiLa is scalable:

   - implemented in Scala in Apache Spark 2.2 environment 
   - can be run on single computer (locally) or Hadoop cluster using YARN



.. toctree::
   :numbered:
   :maxdepth: 2
   
   architecture/architecture
   function/function
   quickstart/quickstart
   usecases/usecases
   benchmarking/benchmarking
..   citation/citation



