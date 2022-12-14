package consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SimpleConsumer {
	private final static Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);
	private final static String TOPIC_NAME = "test";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092";
	private final static String GROUP_ID = "test-group";

	public static void main(String[] args) {
		Properties configs = new Properties();
		configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
		configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true); // auto commit option (default = true)
		configs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 60000);

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(configs);
		// consumer.subscribe(Arrays.asList(TOPIC_NAME));
		consumer.subscribe(Arrays.asList(TOPIC_NAME), new RebalanceListener()); // rebalance listener 등록

		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
			Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

			for (ConsumerRecord<String, String> record : records) {
				logger.info("record:{}", record);

				// 레코드 단위 커밋
//				currentOffset.put(
//					new TopicPartition(record.topic(), record.partition()),
//					new OffsetAndMetadata(record.offset() + 1, null));
//				consumer.commitSync(currentOffset);
			}
//			consumer.commitSync(); // 동기 한번에 여러개 묶어서 커밋
//			consumer.commitAsync(); // 비동기
//			consumer.commitAsync(new OffsetCommitCallback() { // 비동기 커밋 실패시 처리 가능
//				@Override public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
//					if (exception != null) {
//						System.err.println("Commit failed");
//						logger.error("Commit failed for offsets {}", offsets, exception);
//					}
//					else
//						System.err.println("Commit succeeded");
//				}
//			});
		}
	}
}
