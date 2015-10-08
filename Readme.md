Setup:

Assumes you already have java, scala, and sbt installed

1. Get and extract zookeeper:
```
curl -O http://apache.claz.org/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
tar -xzf zookeeper-3.4.6.tar.gz
```

2. Get and extract kafka:
```
curl -O http://download.nextag.com/apache/kafka/0.8.2.2/kafka_2.10-0.8.2.2.tgz
tar -xzf kafka_2.10-0.8.2.2.tgz
```

3. Get and extract spark: 
```
curl -O http://d3kbcqa49mib13.cloudfront.net/spark-1.5.1-bin-hadoop2.6.tgz`
tar -xzf spark-1.5.1-bin-hadoop2.6.tgz
```

4. Compile application: 
```
sbt assembly
```

5. Configure and start zookeeper:
```
cp zookeeper-3.4.6/conf/zoo_sample.cfg zookeeper-3.4.6/conf/zoo.cfg 
./zookeeper-3.4.6/bin/zkServer.sh start
```

6. Start the kafka server:
```
./kafka_2.10-0.8.2.2/bin/kafka-server-start.sh -daemon server.properties
```

7. Create the topic:
```
./kafka_2.10-0.8.2.2/bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic events --replication-factor 1 --partitions 1
```

8. In another terminal, start the kafka producer:
```
./kafka_2.10-0.8.2.2/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic events
```

9. Run application:
```
./spark-1.5.1-bin-hadoop2.6/bin/spark-submit --class Events target/scala-2.11/events-assembly-1.0.jar 2> spark.out
```

Now if you type a letter and hit enter on terminal running the producer, it should show up in the terminal running the application.

Try typing a bunch of letters, every single one should get through regardless of the spark application failing--which should occur approximately 50% of the time.

