#Chapter4. Working with Key/Value
- - -
This chapter covers how to work with RDDs of key/value pairs calling pair RDDs.
Key와 Value로 짝지어진 RDD를 다루는 방법을 소개할 것이며 해당 RDD를 페어 RDDs(pair RDDs)라고 부를 것이다.
- - -
## Creating Pair RDDs
>==Scala== : for the functions on keyed data to be available, return *tuples*.
>An implicit conversion on RDDs of tuples exists to provide the additional key/value functions.
>키를 가지는 데이터를 위한 함수들을 위해 튜플을 리턴해야 하고, 묵시적인 변환또한 존재한다.
<pre><code> val pairs=lines.map(x=>(x.split(" ")(0),x))
</code></pre>
>>tuples: the tuples-here is the key list- cannot be changed
>>tuples : 여기서는 key list를 의미하는데 절대 바뀌어서는 안된다.

## ==Transformation== on Pair RDDs
>==Transformation==: Create new RDDs from a previous one.
>존재하는 RDDs에서 새로운 RDDs를 만들어 내는 것을 말한다.  

Pair RDDs are allowed to use transformationos by passing the functions that operate on tuples.
Please refer to page 49 in the book for inner function
Pair RDDs are also still RDDs (of Python tuples),and thus support the same functions as RDDs.
If you are uncomfortable to working with pairs, Spark provides the ==`mapValue(func)`== function, which is the same as `map{case(x,y):(x,func(y))}`
**Example**
<pre><code>pairs.filter{case(key,value)=> value.length<20}
//Create Dataset filtering out length of value under 20 
//value의 길이가 20미만인 경우만 걸러내어 Dataset을 만든다.</code></pre>

Pari RDDs는 tuple을 처리할 수 있는 함수를 전달하여 transformation을 사용해야 한다. 내장 함수는 책 62페이지를 참조하기를 바란다. pair RDDs는 여전히 일반적인 RDD이므로 스칼라/파이썬 tuple 객체를 가진 RDD에서 지원하는 함수를 그대로 지원한다. 각 키에 대해 쌍으로 작업하는 것이 불편할 경우, 이를 위해 제공하는 함수, ==`mapValue(func)`== 이 있으며 이는 `map{case(x,y):(x,func(y))}`로 대체 가능하다.

----
## Aggregations / 집합연산
1. **reduceByKey()** is quite similar to `reduce()`.
That runs several parallel reduce operations, one for each key in the dataset, where each operation combines values that have the same key. It returns a new RDD consisting of each key and the reduced value for that key.
**reduceByKey()**는 `reduce()`와 유사한데, RDD의 값을 병렬로 연산하여 새로운 RDD를 리턴, 동일한 키가 여러 개 있으면, 해당 value들의 합을 구하여 그 값을 value로 한다. 아래는 예시이다.
> Tabel 1.
| K | V |
|---|----|
|panda|4|
|panda|3|

 Table 1 will be changed like below.
 > Table 2.
 >|K|V|
 >|---|---|
 >|panda|7|

2. **foldByKey()** is quite similar to `fold()`;both use a *ZERO* value of the same type of the data in our RDD and combination function. The provided *ZERO* value for foldByKey() should have no impact when added with your combination function to another element. In other words, result is same with `reduceByKey()`
 **foldByKey()**도 `fold()`와 매우 유사한데 RDD의 데이터타입과 동일한 zero value와 값이 함께 병합되는 함수를 필요로 하는데 값에 변경이 생기면 안된다. 즉, `reduceByKey()`의 결과와 일치해야 한다. 
 
 [What is difference? - fold() VS reduce()](http://stackoverflow.com/questions/29150202/pyspark-fold-method-output)
3. **combineByKey()** is the most general functions.![combineByKey-1](https://github.com/NPLAB-KIT/Resources/blob/master/combineByKey-1.PNG)
![combineByKey-2](https://github.com/NPLAB-KIT/Resources/blob/master/combineByKey-2.PNG)








