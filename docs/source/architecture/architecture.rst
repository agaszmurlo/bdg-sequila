Architecture
===============


Algorithm
##########
SeQuiLa's range joins are based on IntervalTree algorithm. 

Genomic interval intersections can be expressed as follows:

.. code-block:: SQL

	SELECT s2.targetId,count(*)
	FROM reads s1 JOIN targets s2
	ON s1.chr=s2.chr
	AND s1.end>=s2.start
	AND s1.start<=s2.end
	GROUP BY targetId;


SeQuiLa package at its core introduces a new rule based optimizer (RBO) that chooses most efficient join strategy based on
input data statistics computed in the runtime. The first step of the algorithm is to obtain value of `spark.biodatageeks.rangejoin.maxBroadcastSize` parameter. It can set explicite by the end user or computed as a fraction of the Apache Spark Driver memory.
In the next step table row counts are computed and based on that table with the fewer rows is selected for constructing interval forest. This is the default approach - it can be overridden by setting
`spark.biodatageeks.rangejoin.useJoinOrder` to `true`. In this scenario no row counts are computed and the right join table is used for creating interval forest. Such an strategy can be useful in situation when it is known upfront which table should be used for creating a broadcast structure. The final step of the optimization procedure is to estimate the row size and the size of the whole table.
If this number is lower than the max broadcast size parameter computed above then complete table rows are put in the interval tree broadcast structure otherwise only intervals identifiers are used.

.. image:: rbo.*
	:scale: 90


Ecosystem
##########


Integration with R
####################

Integration with JDBC
#######################

Running on YARN
################

Existing apps
################

Ad-hoc analysis
#################