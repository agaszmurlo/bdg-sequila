
Benchmarking
=============


Performance tests description
#############################
In order to evaluate our range join strategy we ran a number of tests using both one-node and a Hadoop cluster
installation. In that way we were able to analyze both vertical (by means of adding computing resources such as CPU/RAM to one machine)
as well as horizontal (by means of adding resources on multiple machines) scalability.

The main idea of the test was to compare performance of SeQuiLa with other tools like featureCounts and genAp that can be used
to compute the number of reads intersecting predefined genomic intervals. It is by no means one of the most commonly used operations
in both DNA and RNA-seq data processing, most notably in gene differential expression and CNV-calling.
featureCounts performance results have been treated as a baseline. In order to show the difference between the naive approach using the
default range join strategy available in Spark SQL and SeQuiLa interval-tree one, we have included it in the single-node test.


Test environment setup
######################

Infrastructure
**************

Our tests have been run on a 6-node Hadoop cluster:

======  =============== =========   ===
Role    Number of nodes CPU cores   RAM
======  =============== =========   ===
EN              1           24      64
NN/RM           1           24      64
DN/NM           4           24      64
======  =============== =========   ===

EN - edge node where only Application masters and Spark Drivers were launched in case of cluster tests.
In case of single node tests (Apache Spark local mode) all computations were performed on the edge node.

NN - HDFS NameNode

RM - YARN ResourceManager

DN - HDFS DataNode

NM - YARN NodeManager


Software
********
All tests have been run using the following software components:

=============   =======
Software        Version
=============   =======
CDH             5.12
Apache Hadoop   2.6.0
Apache Spark    2.2.1
Oracle JDK      1.8
Scala           2.11.8
=============   =======


Datasets
########
Two NGS datasets have been used in all the tests.
WES (whole exome sequencing) and WGS (whole genome sequencing) datasets have been used for vertical and horizontal scalability
evaluation respectively. Both of them came from sequencing of NA 12878 sample that is widely used in many benchmarks.
The table below presents basic datasets information:

=========   ======  =========    ==========
Test name   Format  Size [GB]    Row count
=========   ======  =========    ==========
WES-SN      BAM     17           161544693
WES-SN      ADAM    14           161544693
WES-SN      BED     0.0045       193557
WGS-CL      BAM     273          2617420313
WGS-CL      ADAM    215          2617420313
WGS-CL      BED     0.0016       68191
=========   ======  =========    ==========

WES-SN - tests performed on a single node using WES dataset

WGS-CL - tests performed on a cluster using WGS dataset

Test procedure
##############

.. code-block:: sql

    SELECT targets.contigName,targets.start,targets.end,count(*) FROM reads JOIN targets
         ON (targets.contigName=reads.contigName
         AND
         CAST(reads.end AS INTEGER)>=CAST(targets.start AS INTEGER)
         AND
         CAST(reads.start AS INTEGER)<=CAST(targets.end AS INTEGER)
         )
         GROUP BY targets.contigName,targets.start,targets.end

Results
#######


Local mode
**********

.. image:: local.*


Hadoop cluster
**************

.. image:: cluster.*

Discussion
##########