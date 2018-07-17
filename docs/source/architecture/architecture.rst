Architecture
===============

Overview
#########

The core concept that we have implemented in SeQuiLa is substituting the join strategy chosen by Spark by default to more efficient one, based on broadcasting interval tree data structure from driver nodes to worker nodes.

The main idea is as follows. Let's assume we have genomic intervals (with three genomic coordinates: `chromosome`, `start position`, `end position` and additional annotations) stored in table s1 (smaller) and table s2 (bigger).

.. figure:: structure.*
    :scale: 80

    Dataset s1 and s2. Both storing genomic intervals with necessary genomic coordinates and optional annotations

Our goal is to efficiently perform query as shown below:

.. code-block:: SQL

    SELECT s2.targetId,count(*)
    FROM reads s1 JOIN targets s2
    ON s1.chr=s2.chr
    AND s1.end>=s2.start
    AND s1.start<=s2.end
    GROUP BY targetId;


Let's additionally presume that we have a cluster with one Spark driver, three worker nodes and that the tables are partitioned between worker nodes.


.. figure:: broadcasting.*
	:scale: 80

	Broadcasting interval forest to worker nodes.

When interval query is performed, all table A partitions are sent to the driver node in which interval forest is built (for each chromosome a separate interval tree is being created).  The forest is subsequently sent back to worker nodes in which efficient interval operations based on interval trees are performed. 


Interval Tree
##############
At it's core SeQuiLa's range joins are based on IntervalTree data structure. 

An interval tree is a tree data structure to hold intervals. It is a augmented, balanced red-black tree with low endpoint as node key and additional max value of any endpoint stored in subtree. 
Each node contains following fields: parent, left subtree, right subtree, color, low endpoint, high endpoint and max endpoint of subtree. 
It can be proved that this structure allows for correct interval insertion, deletion and search in O(lg n) time ([CLR]_)

.. figure:: inttree.*
	:scale: 65

	An interval tree. On the top: A set of 10 intervals, shown sorted bottom to top by left endpoint. On  the bottom the interval tree that represents them. An inorder tree walk of the tree lists the nodes in sorted order by left endpoint. [CLR]_

Our implementation of IntervalTree is based on explanations in [CLR]_ although it is extended in the following way:

* data structure allows storing non-unique intervals 
* data structure allows storing additional interval attributes if 






Rule Based Optimizer
####################

SeQuiLa package introduces a new rule based optimizer (RBO) that chooses most efficient join strategy based on
input data statistics computed in the runtime. The first step of the algorithm is to obtain value of `spark.biodatageeks.rangejoin.maxBroadcastSize` parameter. It can set explicite by the end user or computed as a fraction of the Apache Spark Driver memory.
In the next step table row counts are computed and based on that table with the fewer rows is selected for constructing interval forest. This is the default approach - it can be overridden by setting
`spark.biodatageeks.rangejoin.useJoinOrder` to `true`. In this scenario no row counts are computed and the right join table is used for creating interval forest. Such an strategy can be useful in situation when it is known upfront which table should be used for creating a broadcast structure. The final step of the optimization procedure is to estimate the row size and the size of the whole table.
If this number is lower than the max broadcast size parameter computed above then complete table rows are put in the interval tree broadcast structure otherwise only intervals identifiers are used.

.. image:: rbo.*
	:scale: 90


.. [CLR] Cormen, Thomas H.; Leiserson, Charles E., Rivest, Ronald L. (1990). Introduction to Algorithms (1st ed.). MIT Press and McGraw-Hill. ISBN 0-262-03141-8


Ecosystem
##########


