package com.bigData.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

/**
 * Hight-Level-Api 单线程版本
 * 类描述：
 * 
 * @Description: TODO
 * @author xLw
 * @since JDK 1.7
 * @date 2016年7月17日 上午11:35:20
 */
public class Hight_Level_KafkaClientsConsumerDemo {
	public static void main(String[] args) {
		Properties props = new Properties();
		// 默认为largest,既最新的,必须要加smallest,如果要读旧数据
		props.put("auto .offset.reset", "smallest");
		props.put("zookeeper.connect", "192.168.1.11:2181");
		props.put("group.id", "pv");
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		ConsumerConfig conf = new ConsumerConfig(props);
		// 创建一个消费者连接
		ConsumerConnector consumer = Consumer.createJavaConsumerConnector(conf);
		String topic = "testTopic";
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		KafkaStream<byte[], byte[]> stream = streams.get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		while (it.hasNext()) {
			System.out.println("message: " + new String(it.next().message()));
		}
		if (consumer != null)
			consumer.shutdown(); // 其实执行不到，因为上面的hasNext会block
	}
}
