#Chapter4. Working with Key/Value
- - -
This chapter covers how to work with RDDs of key/value pairs calling pair RDDs.
Key와 Value로 짝지어진 RDD를 다루는 방법을 소개할 것이며 해당 RDD를 페어 RDDs(pair RDDs)라고 부를 것이다.
- - -
## Creating Pair RDDs
>Scala : for the functions on keyed data to be available, return *tuples*.
>An implicit conversion on RDDs of tuples exists to provide the additional key/value functions.
>키를 가지는 데이터를 위한 함수들을 위해 튜플을 리턴해야 하고, 묵시적인 변환또한 존재한다.
<pre><code> val pairs=lines.map(x=>(x.split(" ")(0),x))
</code></pre>
>>tuples: the tuples-here is the key list- cannot be changed
>>tuples : 여기서는 key list를 의미하는데 절대 바뀌어서는 안된다.

## Transformation on Pair RDDs
>Transformation: Create new RDDs from a previous one.
>존재하는 RDDs에서 새로운 RDDs를 만들어 내는 것을 말한다.  

Pair RDDs are allowed to use transformationos by passing the functions that operate on tuples.
Please refer to page 49 in the book for built-in function
Pair RDDs are also still RDDs (of Python tuples),and thus support the same functions as RDDs.
If you are uncomfortable to working with pairs, Spark provides the `mapValue(func)` function, which is the same as `map{case(x,y):(x,func(y))}`
**Example**
<pre><code>pairs.filter{case(key,value)=> value.length<20}
//Create Dataset filtering out length of value under 20 
//value의 길이가 20미만인 경우만 걸러내어 Dataset을 만든다.</code></pre>

Pari RDDs는 tuple을 처리할 수 있는 함수를 전달하여 transformation을 사용해야 한다. 내장 함수는 책 62페이지를 참조하기를 바란다. pair RDDs는 여전히 일반적인 RDD이므로 스칼라/파이썬 tuple 객체를 가진 RDD에서 지원하는 함수를 그대로 지원한다. 각 키에 대해 쌍으로 작업하는 것이 불편할 경우, 이를 위해 제공하는 함수, ==`mapValue(func)`== 이 있으며 이는 `map{case(x,y):(x,func(y))}`로 대체 가능하다.

----
### Aggregations / 집합연산
#### reduceByKey()
**reduceByKey()** is quite similar to `reduce()`.
That runs several parallel reduce operations, one for each key in the dataset, where each operation combines values that have the same key. It returns a new RDD consisting of each key and the reduced value for that key.
**reduceByKey()**는 `reduce()`와 유사한데, RDD의 값을 병렬로 연산하여 새로운 RDD를 리턴, 동일한 키가 여러 개 있으면, 해당 value들의 합을 구하여 그 값을 value로 한다. 아래는 예시이다.
> Tabel 1.
| K | V |
|:-----:|:------:|
|panda|4|
|panda|3|

 Table 1 will be changed like below.
 > Table 2.
 >|K|V|
 >|:-----:|:------:|
 >|panda|7|

#### foldByKey()
**foldByKey()** is quite similar to `fold()`;both use a *ZERO* value of the same type of the data in our RDD and combination function. The provided *ZERO* value for `foldByKey()` should have no impact when added with your combination function to another element. In other words, result is same with `reduceByKey()`
 **foldByKey()**도 `fold()`와 매우 유사한데 RDD의 데이터타입과 동일한 *ZERO* value와 값이 함께 병합되는 함수를 필요로 하는데 값에 변경이 생기면 안된다. 즉, `reduceByKey()`의 결과와 일치해야 한다. 
 
 [What is difference? - fold() VS reduce()](http://stackoverflow.com/questions/29150202/pyspark-fold-method-output)
 
####combineByKey()
**combineByKey()** is the most general functions.
**Example : Per-key average using combineByKey() in Scala **
<pre><code>val result = input.combineByKey(
	(v) => (v, 1), // 1
	(acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1), // 2
	(acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2) // 3
	).map{ case (key, value) => (key, value._1 / value._2.toFloat) } // 4
	result.collectAsMap().map(println(_))</code></pre>
 First is called _createCombiner()_. 'combineByKey()' uses a function-createCombiner(), It create the initial value for the accumulator on that key if it is new. It’s important to note that this happens the first time a key is found in each partition, rather than only the first time the key is found in the RDD.
 Second and Third are called _mergeValue()_. If it is value we have seen before while processing that partition, it will instead use _mergeValue()_.
 Fourth is called _mergeCombiners()_. Since each partition is processed independently, we can have multiple accumulators for the same key. When we are merging the results from each partition, if two or more partitions have an accumulator for the same key we merge the accumulators
using the user-supplied mergeCombiners() function.  

  **combineByKey()**는 가장 일반적으로 쓰이는 함수이다. 코드에서 1번은  _createCombiner()_로, 새로운 데이터라면 그 함수를 써서 해당 키에 대한 accumulator의 초기값을 만든다. 첫 키가 나올 때까 아니라 각 파티션에서 처음 나오는 키마다 실행한다.
  2번째와 3번째는 _mergeValue()_로, 파티션을 처리하는 도중 출현한 적이 있는 값이라면 이 함수를 사용하여 해당 키에 대한 accumulator의 현재 값과 새로운 값에 적용해서 합친다.
  4번째는 mergeCombiners()로 각 파티션으로부터 결과를 최종적으로 합칠 때 둘 이상의 파티션이 동일 키에 대한 어큐뮬레이터를 가지고 있다면 이 어큐뮬레이터들은 마찬가지로 해당 함수를 써서 합쳐지게 된다.
![combineByKey-1](https://github.com/NPLAB-KIT/Resources/blob/master/combineByKey-1.PNG)
![combineByKey-2](https://github.com/NPLAB-KIT/Resources/blob/master/combineByKey-2.PNG)

4. **Tuning the level of parallelism**
 Every RDD has a fixed number of partitions that determine the degree of parallelism to use when executing operations on the RDD. Spark will always try to infer a sensible default value based on the size of your cluster, but in some cases you will want to tune the level of parallelism for better performance.  
 모든 RDD는 고정된 개수의 파티션이 존재하기 때문에 연산이 RDD에서 처리될 때 작업수준을 결정한다. 스파크는 클러스터의 사이즈에 맞는 적절한 파티션 개수를 찾는 방식으로 동작하나, 더 나은 퍼포먼스를 내기 위해 병렬화의 수준을 직접 정해주어야 한다.
 **Example in spark**
 <pre><code>val data = Seq(("a", 3), ("b", 4), ("a", 1))
	sc.parallelize(data).reduceByKey((x, y) => x + y) // Default parallelism,기본병렬화 수준 사용
	sc.parallelize(data).reduceByKey((x, y) => x + y) // Custom parallelism, 병렬화 수준 지정</code></pre>
    
### Grouping Data
####groupByKey()
----
Use in case of Data using key that wanted value.  
데이터가 이미 원하는 값을 키로 쓰고 있을 경우에 사용
[K , V] => return [K, Iterable[ V ]]
####groupBy()
----
If you want to use a different condition besides equality on the current key or works on unpaired data, Uses this.
It takes a function that it applies to every element in the source RDD and uses the result to determine the key.
쌍을 이루지 않았거나 현재 키와 관계되지 않은 다른 조건을 써서 데이터를 그룹화 하고자 할 때 사용
이 함수는 원본 RDD의 모든 데이터에 적용하는 함수를 인자로 받아 그 결과를 키로 사용한다.
####cogroup()
----
You can group data sharing the same key from multiple RDDs. `cogroup()` gives us the power to group data from multiple RDDs.
여러 RDDs에 동일 키를 공유해 데이터를 그룹화할 수 있다. 다중 RDD에 대한 데이터 그룹화가 가능하다.
rdd1:[K,v] , rdd2: [K,W] => return [K,(Iterable[V],Iterable[W])]
###Join
>“Join” is a database term for combining fields from two tables using common values.
>"Join"은 원래 두 테이블에서 공통으로 존재하는 값들을 써서 필드끼리 연결하는 작업을 가리키는 데이터베이스 용어이다.

####Inner Join
Only keys that are present in both pair RDDs are output.
양쪽 RDD에 모두 존재하는 key만이 결과로 쓰인다.
![inner Join](https://github.com/NPLAB-KIT/Resources/blob/master/innerJoin.PNG)
####leftOuterJoin(other) / rightOuterJoin(other)
Join pair RDDs together by key, where one of the pair RDDs can be missing the key.
With leftOuterJoin() the resulting pair RDD has entries for each key in the source RDD. The value associated with each key in the result is a tuple of the value from the source RDD and an Option for the value from the other pair RDD.
rightOuterJoin() is except the key must be present in the other RDD and the tuple has an option for the source.
한쪽이 key가 없는 경우라도 pairRDD들을 join해 준다.
leftOuterJoin()을 쓰면 결과 RDD는 원본 RDD에 있는 각 키의 엔트리들을 가진다. 각 키에 대한 값들 중에서 원본 RDD에 대한 값들은 튜플로 표시되며, 다른 쪽 pair RDDs의 값들은 Option으로 표시된다.
rightOuterJoin()은 키가 다른쪽 RDD에 존재해야 하며 원본 RDD에 Option으로 표시된다.
![leftOuterJoin](https://github.com/NPLAB-KIT/Resources/blob/master/leftOuterJoin.PNG)
![rightOuterJoin](https://github.com/NPLAB-KIT/Resources/blob/master/rightOuterJoin.PNG)

###Sorting Data
We can sort an RDD with key/value pairs provided that there is an ordering defined on the key. it defaults to true. But If necessary, we can provide our own comparison function
키에 대해 순서가 정의되어 있다면 정렬이 가능하며, 기본값 true로 설정되어 있다. 그러나 필요하다면 별도의 비교함수를 제작할 수 있다.
<pre><code>val input: RDD[(Int, Venue)] = ...
implicit val sortIntegersByString = new Ordering[Int] {
override def compare(a: Int, b: Int) = a.toString.compare(b.toString)
}
rdd.sortByKey()</code></pre>

##Actions Available on Pair RDDs
Please refer to page 60 in the book for built-in function
책 75페이지를 참고해 주시길 바랍니다.
