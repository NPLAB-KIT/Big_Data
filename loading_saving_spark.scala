//Some Documentation of spark 1.6 version based on hadoop 2.7 version
//Some documentation of "Learning spark Book" (Holden Karau, Andy Konwinski 2015) chapter 5     
//Every source codes were experienced, it runs above version. 
//Developed by  KIT  Nplab:
//Loading data common file formats: (txt, json, ).  In Scala 

import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SQLContext
import sqlContext.implicits
val test=sc.textFile("d://test/test1.txt") 
 test.count()  
 test.collect().foreach(println)  

// Check console panel  http://localhost:4040/jobs/ (maybe port differently in other 
//  system) )
//  Saving Text file :
  
 test.saveAsTextFile("d://test/new")

// Check again console panel  http://localhost:4040/jobs/ (maybe port differently in 
// other 

// whole file loading example
val test= sc.wholeTextFiles("d://test/ ") 
 test.count()  
val result = test.mapValues{y => val nums = y.split(" ").map(x => x.toDouble) 
nums.sum / nums.size.toDouble 

 }
// whole file … loading (example 2 

val test= sc.wholeTextFiles("d://test/") 
test.count()
val files = test.map { case (filename, content) => filename}
def doSomething(file: String) = { 
println (file); 
val logData = sc.textFile(file); 
val numAs = logData.filter(line => line.contains("a")).count(); 
println("Lines with a: %s".format(numAs)); 
} 
files.collect.foreach( filename => { doSomething(filename) 
}) 

//Saving whole loading file 
files. saveAsTextFile("d://test/new")   

// loading json  

val jsonTest = sqlContext.read.format("json").load("d://test/json/jsonfile1.json")
jsonTest.show





// loading json
val jsonTest = sqlContext.read.format("json").load("d://test/json/jsonfile1.json")
val tableValueTest = sc.parallelize(
"""{"name":"Shohrukh","age":"25"}""" :: Nil)
val jsonTest = sqlContext.read.json(tableValueTest)	
jsonTest.show 
jsonTest.printSchema


// saving json.

import org.apache.spark.sql.SaveMode
jsonTest.write.format("json").mode(SaveMode.Append).save("d://test/new1")









