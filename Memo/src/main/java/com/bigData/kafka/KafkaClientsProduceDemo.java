package com.bigData.kafka;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaClientsProduceDemo {
	public static void main(String[] args) {
		// 设置配置属性
		Map mconfig = new HashMap();
		// 指定分区处理类。默认kafka.producer.DefaultPartitioner，表通过key哈希到对应分区
		mconfig.put("partitioner.class", "com.meituan.mafka.client.producer.CustomizePartitioner");
		// 指定是否压缩，默认0表示不压缩，1表示用gzip压缩，2表示用snappy压缩。压缩后消息中会有头来指明消息压缩类型，故在消费者端消息解压是透明的无需指定。
		mconfig.put("compression.codec", "none");
		mconfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.11:9092");
		mconfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		mconfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
				"org.apache.kafka.common.serialization.StringSerializer");
		/*
		 * Ack机制 默认为0 0:producer不会等待broker发送ack. 1:当leader接收到消息之后发送ack.
		 * 2:当所有的follower都同步消息成功后发送ack.
		 */
		mconfig.put("request.required.acks", "1");
		/*
		 * 在向producer发送ack之前,broker允许等待的最大时间 如果超时,broker将会向producer发送一个error
		 * ACK.意味着上一次消息因为某种原因未能成功(比如follower未能同步成功) request.timeout.ms=10000
		 * 
		 */
		mconfig.put("request.timeout.ms", "6000");
		/*
		 * 同步还是异步发送消息, 默认“sync”表同步，"async"表异步。异步可以提高发送吞吐量, #
		 * 也意味着消息将会在本地buffer中,并适时批量发送，但是也可能导致丢失未发送过去的消息
		 */
		mconfig.put("producer.type", "async");
		/**
		 * ############## 异步发送 (以下四个异步参数可选) #################### #
		 * 在async模式下,当message被缓存的时间超过此值后,将会批量发送给broker,默认为5000ms #
		 * 此值和batch.num.messages协同工作. queue.buffering.max.ms = 5000 #
		 * 在async模式下,producer端允许buffer的最大消息量 #
		 * 无论如何,producer都无法尽快的将消息发送给broker,从而导致消息在producer端大量沉积 #
		 * 此时,如果消息的条数达到阀值,将会导致producer端阻塞或者消息被抛弃，默认为10000
		 * queue.buffering.max.messages=20000 # 如果是异步，指定每次批量发送数据量，默认为200
		 * batch.num.messages=500 #
		 * 当消息在producer端沉积的条数达到"queue.buffering.max.meesages"后 #
		 * 阻塞一定时间后,队列仍然没有enqueue(producer仍然没有发送出任何消息) #
		 * 此时producer可以继续阻塞或者将消息抛弃,此timeout值用于控制"阻塞"的时间 # -1: 无阻塞超时限制,消息不会被抛弃 #
		 * 0:立即清空队列,消息被抛弃 queue.enqueue.timeout.ms=-1
		 * 
		 * ################ end ###############
		 * 
		 */
		/*
		 * # 当producer接收到error ACK,或者没有接收到ACK时,允许消息重发的次数 #
		 * 因为broker并没有完整的机制来避免消息重复,所以当网络异常时(比如ACK丢失) #
		 * 有可能导致broker接收到重复的消息,默认值为3. message.send.max.retries=3
		 */
		mconfig.put("message.send.max.retries", "3");
		/*
		 * producer刷新topic metada的时间间隔,producer需要知道partition
		 * leader的位置,以及当前topic的情况 #
		 * 因此producer需要一个机制来获取最新的metadata,当producer遇到特定错误时,将会立即刷新 #
		 * (比如topic失效,partition丢失,leader失效等),此外也可以通过此参数来配置额外的刷新机制，默认值600000
		 */
		mconfig.put("topic.metadata.refresh.interval.ms", "6000");
		// 创建producer
		Producer<String, String> producer = new KafkaProducer(mconfig);
		// 产生并发送消息
		long start = System.currentTimeMillis();
		long runtime = new Date().getTime();
		// 如果topic不存在，则会自动创建，默认replication-factor为1，partitions为0
		ProducerRecord<String, String> data = new ProducerRecord<String, String>("testTopic", "我叫key", "我叫msg");
		producer.send(data);
		System.out.println("耗时:" + (System.currentTimeMillis() - start));
		// 关闭producer
		producer.close();
	}
}
