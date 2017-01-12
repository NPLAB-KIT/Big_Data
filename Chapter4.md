# Working with Key/Value Pairs
## Motivation -Pair RDDs
1. act on each key in parallel : 각 키에 대하여 병렬로 처리 가능
2. regroup data across the network : 네트워크상에서 데이터를 재그룹핑 해줌

## Creating Pair RDDs
### How to Create Key/Value RDD
 -Python
<pre><code>pairs=lines.map(lambda x: (x.split(" ")[0],x))</code></pre>
 -Scala
<pre><code>val pairs=lines.map(x=>(x.split(" ")(0),x))</code></pre>
 -Java
<pre><code>
 PairFunction<String, String, String> keyData =
 new PairFunction<String, String, String> call(String x){
  return new Tuple2(x.split(" ")[0],x);
 }
 JavaPairRDD<String, String> pairs=lines.mapToPair(keyData);
</code></pre>

Creating pair RDD => call ````Sparkcontext.parallelize()````

## Transformations on Pair RDDs
Pair RDDs are allowed to use all the transformation avaiilable to standard RDDs.Pair RDDs contain tuples so, Need to pass functions that operate on tuples rather than on individual elements.  
페어 RDD는 기본 rdd에서 가능한 모든 트랜스포메이션을 사용할 수 있으나 페어 rdd는 튜플을 가짐브로 개별 데이터를 다루는 함수 대신 튜플을 처리하는 함수를 전달해야 한다.
image 넣기 
If you want filter on second element in Scala, write this.
<pre><code>pairs.filter{case (key,value) => value.length <20 }</code></pre>
If you want to access only value part of pair RDDs and experience inconvenience of that,
Spark provieds the **mapValues(func)** function

##Aggregations
`reduceByKey()` : runs several parallel reduce operations, one for each key in the dataset, where each operation combines values that have the same key.  
`foldByKey()` : runs several parallel reduce operations, one for each key in the dataset, where each operation combines values that have the same key.

Example in scala
~~~~
rdd.mapValues(x=>(x,1)).reduceByKey((x,y)=>(x._1+y._1,x._2+y._2))
~~~~
> `reduceByKey()` and `foldByKey()` will automatically perform combinig locally on each machine before computing global totals for each key.

`combineByKey()`: is the most general of the per-key aggreation functions, Most of the other per-key combiners are implemented usgin it.   








