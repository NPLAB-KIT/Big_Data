----
# Working with Key/Value Pairs<br/>
## Motivation -Pair RDDs
1. act on each key in parallel : 각 키에 대하여 병렬로 처리 가능
2. regroup data across the network : 네트워크상에서 데이터를 재그룹핑 해줌

## Creating Pair RDDs
###How to Create Key/Value RDD
- Python <br/>
    pairs=lines.map(lambda x: (x.split(" ")[0],x))

- Scala <br/>
    val pairs=lines.map(x=>(x.split(" ")(0),x))

- Java  <br/>
    PairFunction<String, String, String> keyData=
    new PairFunction<String, String, String> call(String x) {
      return new Tuple2(x.split(" ")[0],x);
    }
    JavaPairRDD<String, String> pairs=lines.mapToPair(keyData);  
    
Creating pair RDD => call ````Sparkcontext.parallelize()````<br/>
## Transformations on Pair RDDs
Pair RDDs are allowed to use all the transformation avaiilable to standard RDDs.
Pair RDDs contain tuples so, Need to pass functions that operate on tuples rather than on individual elements.

|Function name | Purpose | Example | Result |
|:-------------|:--------:|:--------:|------: |
|reduceByKey(func)|Combine values with the same key|rdd.reduceByKey((x, y) => x+y)|{(1,2),(3,10)}|
|groupByKey()|Group values with the same key|rdd.groupByKey()|{(1,[2]),(3,[4,6])}|
|combineByKey(createCominer,mergeValue,mergeCombiners,partitioner)

