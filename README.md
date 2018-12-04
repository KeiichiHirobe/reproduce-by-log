reproduce-by-log
================
Reproduce a log or request from a past log at the same timeing.
Useful for Fluentd Test.

Requirements
------------

* Java 8 or higher
* [sbt](https://www.scala-sbt.org/1.x/docs/Installing-sbt-on-Linux.html)


Setup
-----


```bash
$ sbt
[info] Loading settings for project reproduce-by-log-build from assembly.sbt ...
[info] Loading project definition from ...
....
[info] sbt server started at ....

# compile and run
sbt:reproducebylog> run sample.log
[info] Updating ...
[info] Done updating.
...
[info] Done compiling.
[warn] Multiple main classes detected.  Run 'show discoveredMainClasses' to see the list

Multiple main classes detected, select one to run:

 [1] behiron.reproducebylog.sample.accessLogExceptionSample
 [2] behiron.reproducebylog.sample.accessLogSample


# choose main class
Enter number: [info] Packaging reproducebylog_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.

2

# successfly running
[info] Running behiron.reproducebylog.sample.accessLogSample sample.log
10.0.2.2 - - [16/Nov/2018:14:21:01 +0900] "GET /example HTTP/1.1" 404 -
10.0.2.2 - - [16/Nov/2018:14:21:02 +0900] "GET /example HTTP/1.1" 404 -
10.0.2.2 - - [16/Nov/2018:14:21:05 +0900] "POST /example HTTP/1.1" 404 -
...

```

Create a fat JAR
----------------

Main class is defined by `mainClass in assembly` in build.sbt
```bash
sbt:reproducebylog> assembly
[warn] Multiple main classes detected.  Run 'show discoveredMainClasses' to see the list
[info] Strategy 'discard' was applied to a file (Run the task at debug level to see details)
[info] Packaging target/scala-2.12/reproducebylog.jar ...
[info] Done packaging.
[success] Total time: 4 s, completed 2018/12/04 12:50:40
```

Usage
-----

### Reproduce apache accesslog
Very useful if you want to test Fluentd collecting accesss log because you can test anywhere


```bash
$ cat sample.log 
10.0.2.2 - - [16/Nov/2018:14:21:01 +0900] "GET /example HTTP/1.1" 404 -
10.0.2.2 - - [16/Nov/2018:14:21:02 +0900] "GET /example HTTP/1.1" 404 -
10.0.2.2 - - [16/Nov/2018:14:21:05 +0900] "POST /example HTTP/1.1" 404 -
10.0.2.2 - - [16/Nov/2018:14:21:06 +0900] "POST /example HTTP/1.1" 500 19990
10.0.2.2 - - [16/Nov/2018:14:21:11 +0900] "POST /example HTTP/1.1" 500 9511
10.0.2.2 - - [16/Nov/2018:14:21:12 +0900] "POST /example HTTP/1.1" 500 19990
10.0.2.2 - - [INVALIDLOGFORMAT/Nov/2018:14:21:12 +0900] "POST /example HTTP/1.1" 500 19990
10.0.2.2 - - [16/Nov/2018:14:21:13 +0900] "POST /example HTTP/1.1" 500 19990

# records are printed to STDOUT intermittently
$ java -jar target/scala-2.12/reproducebylog.jar sample.log 2>stderr.log
10.0.2.2 - - [16/Nov/2018:14:21:01 +0900] "GET /example HTTP/1.1" 404 -
10.0.2.2 - - [16/Nov/2018:14:21:02 +0900] "GET /example HTTP/1.1" 404 -
10.0.2.2 - - [16/Nov/2018:14:21:05 +0900] "POST /example HTTP/1.1" 404 -
10.0.2.2 - - [16/Nov/2018:14:21:06 +0900] "POST /example HTTP/1.1" 500 19990
10.0.2.2 - - [16/Nov/2018:14:21:11 +0900] "POST /example HTTP/1.1" 500 9511
10.0.2.2 - - [16/Nov/2018:14:21:12 +0900] "POST /example HTTP/1.1" 500 19990
10.0.2.2 - - [16/Nov/2018:14:21:13 +0900] "POST /example HTTP/1.1" 500 19990

# invalid records,exception records,and timeout records are printed to STDERR (you can overwrite these behavior) 
$ cat stderr.log 
invalid record: [7] 10.0.2.2 - - [INVALIDLOGFORMAT/Nov/2018:14:21:12 +0900] "POST /example HTTP/1.1" 500 19990
```

### Customize

#### customize timing
Extend TaskTiming trait if you want to use logs which format is different from accesslog's format
```scala
  /* defined in TaskTiming trait */

  def getRawEpoch(record: String, lineNum: Int): Option[Long]
```


#### customize task
Extend Task trait if you want to do another tasks using logs.
Simulating response is a good example.
```scala
  /* defined in Task trait */

  def work(record: String, lineNum: Int): Unit
```
