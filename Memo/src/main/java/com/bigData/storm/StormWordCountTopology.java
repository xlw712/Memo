package com.bigData.storm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IBasicBolt;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *类描述： 
 *@Description: TODO
 *@author xLw    
 *@since JDK 1.7
 *@date 2016年9月3日 下午6:32:33
 */
public class StormWordCountTopology {
	Logger logger = LoggerFactory.getLogger(StormWordCountTopology.class);

	public static void main(String[] args)
			throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
		// 创建一个拓扑
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("word-reader", new WordReadSpout());
		// shuffleGrouping数据流组,当Spout,emit时,如果有多个Bolt指定componentId为Sopout,那么Spout会将tuple随机发送
		builder.setBolt("word-split", new WordSplitBold()).shuffleGrouping("word-reader");
		// fieldGrouping数据流组,当有Sout或者Bolt,emit时,可以指定接收哪个Spout或者Bolt的Field字段,可以配置多个,字段由declareOutputFields方法配置,数据流组详细说明在InputDeclarer类中
		builder.setBolt("word-counter", new WordCounterBold()).fieldsGrouping("word-split", new Fields("line"));
		StormTopology stormTopology = builder.createTopology();	
		Config conf = new Config();
		conf.put("resourcePath", "E:\\1.txt");
		conf.setNumWorkers(20);
		if (args != null && args.length > 0) {
			conf.setNumWorkers(1);
			StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
		} else {
			// 创建一个本地集群用于测试
			LocalCluster cluster = new LocalCluster();
			// 一个spout
			// task中处于pending状态的最大的tuples数量.该配置应用于单个task,而不是整个spouts或topology.
			conf.setMaxSpoutPending(5000);
			cluster.submitTopology("FristTopology", conf, stormTopology);
			cluster.killTopology("FristTopology");
			cluster.shutdown();
		}
	}

	/**
	 * 
	 *	类描述： 测试Spout
	 *	@Description: TODO
	 *	@author xLw    
	 *	@since JDK 1.7
	 *	@date 2016年9月3日 下午10:52:34
	 */
	private static class WordReadSpout implements IRichSpout {
		private FileReader fileReader;
		private SpoutOutputCollector collector;
		private boolean completed = false;

		/** 
		 * 这是第一个方法，里面接收了三个参数，
		 * 第一个是创建Topology时的配置 
		 * 第二个是所有的Topology数据
		 * 第三个是用来把Spout的数据发射给bolt 
		 */
		@Override
		public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
			try {
				// 获取创建Topology时指定的要读取的文件路径
				this.fileReader = new FileReader(conf.get("resourcePath").toString());
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Error reading file [" + conf.get("wordFile") + "]");
			}
			this.collector = collector;
		}

		/** 
		 * 这是Spout最主要的方法，在这里我们读取文本文件，并把它的每一行发射出去（给bolt） 
		 * 这个方法会不断被调用，为了降低它对CPU的消耗，当任务完成时让它sleep一下 
		 */
		@Override
		public void nextTuple() {
			if (completed) {
				try {
					Thread.sleep(30);
					completed = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return;
			}
			String str;
			// Open the reader
			BufferedReader reader = new BufferedReader(fileReader);
			try {
				// Read all lines
				while ((str = reader.readLine()) != null) {
					/** 
					 * 发射每一行，Values是一个ArrayList的实现 
					 * Values的格式要与Declare中定义的个数一样
					 */
					this.collector.emit(new Values(str), str);
					System.out.println(this.getClass().getName() + ":发射出了" + str);
				}
			} catch (Exception e) {
				throw new RuntimeException("Error reading tuple", e);
			} finally {
				completed = true;
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			/**
			 * Uses default stream id.
			 */
			declarer.declare(new Fields("line"));
		}

		@Override
		public void close() {
			System.out.println("close");
		}

		@Override
		public void activate() {
			System.out.println("activate");
		}

		@Override
		public void deactivate() {
			System.out.println("deactivate");
		}

		@Override
		public void ack(Object msgId) {
			System.out.println("ack");
		}

		@Override
		public void fail(Object msgId) {
			System.out.println("fail");
		}

		/**
		 * 该方法在setSpout时调用
		 */
		@Override
		public Map<String, Object> getComponentConfiguration() {
			System.out.println("getComponent");
			return null;
		}

	}

	/**
	 * 拆分
	 *类描述： 
	 *@Description: TODO
	 *@author xLw    
	 *@since JDK 1.7
	 *@date 2016年9月4日 上午12:36:01
	 */
	private static class WordSplitBold implements IBasicBolt {

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("line"));
		}

		@Override
		public void prepare(Map stormConf, TopologyContext context) {
			System.out.println(this.getClass().getName() + ":prepare方法执行");
		}

		@Override
		public Map<String, Object> getComponentConfiguration() {
			System.out.println("getComponent");
			return null;
		}

		/**
		 * 这是bolt中最重要的方法，每当接收到一个tuple时，此方法便被调用 
		 * 这个方法的作用就是把文本文件中的每一行切分成一个个单词，并把这些单词发射出去（给下一个bolt处理） 
		 */
		@Override
		public void execute(Tuple tuple, BasicOutputCollector collector) {
			String sentence = (String) tuple.getValue(0);
			String[] words = sentence.split(" ");
			for (String word : words) {
				word = word.trim();
				if (!word.isEmpty()) {
					word = word.toLowerCase();
					List list = new ArrayList<>();
					list.add(word);
					collector.emit(list);
					System.out.println(this.getClass().getName() + ": 发射了" + list.toString());
				}
			}
		}

		@Override
		public void cleanup() {
		}
	}

	private static class WordCounterBold implements IBasicBolt {
		Integer id;
		String name;
		Map<String, Integer> counters;
		private OutputCollector collector;

		@Override
		public void prepare(Map stormConf, TopologyContext context) {
			this.counters = new HashMap<String, Integer>();
			this.name = context.getThisComponentId();
			this.id = context.getThisTaskId();
		}

		@Override
		public void execute(Tuple input, BasicOutputCollector collector) {
			String str = input.getString(0);
			if (!counters.containsKey(str)) {
				counters.put(str, 1);
			} else {
				Integer c = counters.get(str) + 1;
				counters.put(str, c);
			}
		}

		@Override
		public void cleanup() {
			System.out.println("-- Word Counter [" + name + "-" + id + "] --");
			for (Map.Entry<String, Integer> entry : counters.entrySet()) {
				System.out.println(entry.getKey() + ": " + entry.getValue());
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("line"));
		}

		@Override
		public Map<String, Object> getComponentConfiguration() {
			return null;
		}
	}
}
