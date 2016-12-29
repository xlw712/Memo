package com.bigData.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * Hight-Level-Api 多线程版本 类描述：
 * 
 * @Description: TODO
 * @author xLw
 * @since JDK 1.7
 * @date 2016年7月17日 上午11:32:54
 */
public class Hight_Level_M_KafkaClientsConsumerDemo {
	private ConsumerConnector consumer;
	private String topic;
	private ExecutorService executor;

	public Hight_Level_M_KafkaClientsConsumerDemo(String a_zookeeper, String a_groupId, String a_topic) {
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector( // 创建Connector，注意下面对conf的配置
				createConsumerConfig(a_zookeeper, a_groupId));
		this.topic = a_topic;
	}

	public void shutdown() {
		if (consumer != null)
			consumer.shutdown();
		if (executor != null)
			executor.shutdown();
	}

	public void run(int a_numThreads) { // 创建并发的consumers
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(a_numThreads)); // 描述读取哪个topic，需要几个线程读
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap); // 创建Streams
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic); // 每个线程对应于一个KafkaStream

		// now launch all the threads
		//
		executor = Executors.newFixedThreadPool(a_numThreads);

		// now create an object to consume the messages
		//
		int threadNumber = 0;
		for (final KafkaStream stream : streams) {
			executor.submit(new ConsumerMsgTask(stream, threadNumber)); // 启动consumer
																		// thread
			threadNumber++;
		}
	}

	private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}

	public class ConsumerMsgTask implements Runnable {
		private KafkaStream m_stream;
		private int m_threadNumber;

		public ConsumerMsgTask(KafkaStream stream, int threadNumber) {
			m_threadNumber = threadNumber;
			m_stream = stream;
		}

		public void run() {
			ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
			while (it.hasNext())
				System.out.println("Thread " + m_threadNumber + ": " + new String(it.next().message()));
			System.out.println("Shutting down Thread: " + m_threadNumber);
		}
	}

	public static void main(String[] args) {
		args[0] = "192.168.1.11:2181";
		args[1] = "g1";
		args[2] = "testTopic";
		String zooKeeper = args[0];
		String groupId = args[1];
		String topic = args[2];
		int threads = Integer.parseInt(args[3]);

		Hight_Level_M_KafkaClientsConsumerDemo example = new Hight_Level_M_KafkaClientsConsumerDemo(zooKeeper, groupId,
				topic);
		example.run(threads);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException ie) {

		}
		example.shutdown();
	}
}
