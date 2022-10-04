import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SimpleProducer {
	private final static Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
	private final static String TOPIC_NAME = "test";
	private final static String BOOTSTRAP_SERVERS = "localhost:9092";

	public static void main(String[] args) {
		Properties configs = new Properties();
		configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configs.put(ProducerConfig.ACKS_CONFIG, "0");
		// acks = 0 : leader partition에 전송만하고 제대로 전송됐는지는 확인하지 않음
		// 따라서, 전송 응답 데이터를 확인하면 topic-parition#@-1 출력 : offeset은 -1일 수 없음

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(configs);

		// message value 만 전송
		String messageValue = "testMessage";
		ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, messageValue);
		producer.send(record);
		logger.info("{}", record);

		// message key와 value 전송
		ProducerRecord<String, String> record2 = new ProducerRecord<>(TOPIC_NAME, "k1", "Pangyo");
		producer.send(record2);
		ProducerRecord<String, String> record3 = new ProducerRecord<>(TOPIC_NAME, "k2", "Busan");
		producer.send(record3);

		/* partition 번호 지정
		int partition = 0;
		ProducerRecord<String, String> record4 = new ProducerRecord<>(TOPIC_NAME, partition, "k1", "Pangyo");
		producer.send(record4);
		 */

		// producer로 보낸 데이터의 결과를 동기적으로 가져오기 (어떤 topic - 몇번 partition @ offset)
		try {
			RecordMetadata metadata = producer.send(record2).get();
			logger.info(metadata.toString());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			producer.flush();
			producer.close();
		}

//		producer.flush();
//		producer.close();

		/* custom partitioner
		Properties configs2 = new Properties();
		configs2.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		configs2.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configs2.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		configs2.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class);
		KafkaProducer<String, String> producer2 = new KafkaProducer<String, String>(configs2);
		ProducerRecord<String, String> p2record = new ProducerRecord<>(TOPIC_NAME, "k1", "Pangyo");
		producer.send(p2record);

		producer2.flush();
		producer2.close();
		 */
	}
}
