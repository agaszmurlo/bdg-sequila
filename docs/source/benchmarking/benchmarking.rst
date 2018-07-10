
Benchmarking
=============


Performance tests description
#############################
In order to evaluate our range join strategy we ran a number of tests using both one-node and a Hadoop cluster
installation. In that way we were able to analyze both vertical (by means of adding computing resources such as CPU/RAM to one machine)
as well as horizontal (by means of adding resources on multiple machines) scalability.

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


Datasets
########


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