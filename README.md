# DataMiningCharmDcharm

# General info

University evaluation project in validation and verification of programming systems

# Description

Application allows mining frequent closed itemsets from logs_BCS37_20181103.csv file using Charm/dCharm 
algorithms provided by SPMF. Sequential pattern mining framework (SPMF) is an open-source software and data
mining library written in Java, specialized in pattern mining (the discovery of patterns in data). It is
imported in the source code (/src/ca).

Frequent patterns are patterns which appear frequently within a dataset. A frequent itemset is one which 
is made up of one of these patterns, which is why frequent pattern mining is often alternately referred 
to as frequent itemset mining.

* Charm is an algorithm for discovering frequent closed itemsets in a transaction database.
* dCharm is a variation of the Charm algorithm. It has the same output and input as Charm.

# Details

The input of Charm/dCharm algorithms is a transaction database which is a set of transactions. 
Each transaction is a set of items. The transaction database objects should load the data from
special text file. For this reason CsvConverter is implemented to transform the cvs file to
appropriate for Charm/Dcharm input txt file and to convert the result from the algorithm
to more user-friendly output.

The input file format used by CHARM is defined as follows. It is a text file. An item is 
represented by a positive integer. A transaction is a line in the text file. In each line 
(transaction), items are separated by a single space. It is assumed that all items within a same 
transaction (line) are sorted according to a total order (e.g. ascending order) and that no item 
can appear twice within the same line. 

The output file format is defined as follows. It is a text file, where each line represents a 
frequent closed itemset. On each line, the items of the itemset are first listed. Each item is 
represented by an integer and it is followed by a single space. After, all the items, the keyword 
"#SUP:" appears, which is followed by an integer indicating the support of the itemset, expressed 
as a number of transactions

[More details:](https://www.philippe-fournier-viger.com/spmf/Charm_dCharm.php#:~:text=The%20Charm%20algorithm%20is%20an,for%20frequent%20closed%20itemset%20mining.)

# Run

Launching Charm or dCharm was done from "MainTestCharm_bitset_saveToFile.java" and 
"MainTestDCharm_bitset_saveToFile.java" in the package ca.pfv.SPMF.tests.

# Testing

The test cases and results are described in details in TestPlan.docx and TestReportCombined.doc.

### Note

JUnit library is used in this project for unit testing. More specifically junit-4.13-beta-2.jar and hamcrest-core-1.3.jar;
