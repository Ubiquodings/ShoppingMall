전체 구조



![스트림즈테스트구조](https://github.com/Ubiquodings/ShoppingMall/blob/master/%ED%9A%8C%EC%9D%98%EB%A1%9D/%EA%B2%B0%EA%B3%BC/%EC%8A%A4%ED%8A%B8%EB%A6%BC%EC%A6%88%ED%85%8C%EC%8A%A4%ED%8A%B8%EA%B5%AC%EC%A1%B0.PNG)



화면

![스트림즈예제_UI화면]([D:%5Cdownloads%5C%EC%8A%A4%ED%8A%B8%EB%A6%BC%EC%A6%88%EC%98%88%EC%A0%9C_UI%ED%99%94%EB%A9%B4.PNG](https://github.com/Ubiquodings/ShoppingMall/blob/master/%ED%9A%8C%EC%9D%98%EB%A1%9D/%EA%B2%B0%EA%B3%BC/%EC%8A%A4%ED%8A%B8%EB%A6%BC%EC%A6%88%EC%98%88%EC%A0%9C_UI%ED%99%94%EB%A9%B4.PNG))

type: click, shoplist 에 따라 count 집계하는 테스트입니다.





## 서버1 (SB1)

1. #### build.gradle

   ```cmd
   implementation 'org.springframework.boot:spring-boot-starter-web'
   implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
   implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'
   
   compile group: 'org.elasticsearch', name: 'elasticsearch', version: '6.8.5'
   
   compile group: 'com.google.guava', name: 'guava', version: '25.1-jre'
   compileOnly 'org.projectlombok:lombok'
   annotationProcessor 'org.projectlombok:lombok'
   
   runtimeOnly 'com.h2database:h2:1.4.199'
   
   testImplementation('org.springframework.boot:spring-boot-starter-test') {
   exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
   }
   testImplementation 'org.springframework.kafka:spring-kafka-test'
   implementation 'org.springframework.kafka:spring-kafka'
   
   compile('org.springframework.boot:spring-boot-starter-mustache')
   
   ```

   

2. #### 설정 파일 (application-kafka.yml)

   ```yaml
   spring:
     kafka:
       bootstrap-servers: ${KAFKA-URI}:${PORT}
   
       properties:
         ssl.endpoint.identification.algorithm: https
         sasl.mechanism: PLAIN
         request.timeout.ms: 20000
         security.protocol: SASL_SSL
         retry.backoff.ms: 500
         sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username="${USERNAME}" password="${PASSWORD}";
   
       producer:
         bootstrap-servers: ${KAFKA-URI}:${PORT}
         key-serializer: org.apache.kafka.common.serialization.StringSerializer
         value-serializer: org.apache.kafka.common.serialization.StringSerializer
         properties:
           acks: all
           retries: 10
           retry.backoff.ms: 1000
   
       admin:
         properties:
           bootstrap-servers: ${KAFKA-URI}:${PORT}
   
       consumer:
         bootstrap-servers: ${KAFKA-URI}:${PORT}
         key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
         value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
         group-id: library-events-listener-group
   
   
     elasticsearch:
       rest:
         password: ${PASSWORD}
         username: ${USERNAME}
         uris: ${ES-URI}:${PORT}
         read-timeout: 10s
   
   ```

   

3. #### 화면 컨트롤러 (IndexController.java)

   ```java
   private final ElasticsearchRestTemplate elasticsearchRestTemplate;
   
   
   @GetMapping("/")
   public String index(Model model) {
   
       UserAction shopListAction = elasticsearchRestTemplate.queryForObject(
           GetQuery.getById("{\"userName\":\"sisi\",\"actionType\":\"shoplist\"}"),
           UserAction.class);
   
       if (shopListAction == null)
           model.addAttribute("shopCount", 0);
       else
           model.addAttribute("shopCount", shopListAction.getCount());
   
   
       UserAction clickAction = elasticsearchRestTemplate.queryForObject(
           GetQuery.getById("{\"userName\":\"sisi\",\"actionType\":\"click\"}"),
           UserAction.class);
   
       if (clickAction == null)
           model.addAttribute("clickCount", 0);
       else
           model.addAttribute("clickCount", clickAction.getCount());
   
       return "index";
   }
   
   ```

4. #### 화면 (index.mustache)

   ```html
   <div class="col-md-12">
       <div class="row">
           <button type="button" class="btn btn-primary col-md-2" id="btn-click">click</button>
           <button type="button" class="btn btn-primary col-md-2" id="btn-shoplist">shoplist</button>
       </div>
   
       <div class="row">
           대시보드 <br>
   
           <!-- 집계 -->
           click: {{#clickCount}}{{clickCount}}{{/clickCount}} {{^clickCount}}0{{/clickCount}}<br>
           shoplist: {{#shopCount}}{{shopCount}}{{/shopCount}} {{^shopCount}}0{{/shopCount}}<br>
       </div>
   </div>
   
   ```

   

5. #### js 파일: ajax (index.js)

   ```javascript
   var index = {
       init: function () {
           var _this = this;
           // click 버튼을 클릭하면
           $('#btn-click').on('click', function () {
               var data = {
                   userName: "sisi", // 하드코딩
                   actionType: "click"
               };
               $.ajax({
                   type: 'POST',
                   url: '/v1/ubicshop',
                   dataType: 'json',
                   contentType: 'application/json; charset=utf-8',
                   data: JSON.stringify(data)
               });
           });
   
           // shoplist 버튼을 클릭하면
           $('#btn-shoplist').on('click', function () {
               var data = {
                   userName: "sisi",
                   actionType: "shoplist"
               };
               $.ajax({
                   type: 'POST',
                   url: '/v1/ubicshop',
                   dataType: 'json',
                   contentType: 'application/json; charset=utf-8',
                   data: JSON.stringify(data)
               });
           });
       }
   };
   index.init();
   ```

   

6. #### rest 컨트롤러 (KafkaAPIController.java)

   ```java
   private final UbicUserActionProducer ubicUserActionProducer;
   
   @PostMapping("/v1/ubicshop")
   public ResponseEntity<KafkaProducerRequestDto> postUbicUserActionEvent(@RequestBody KafkaProducerRequestDto kafkaProducerRequestDto) throws JsonProcessingException {
   
       ubicUserActionProducer.sendToKafka(kafkaProducerRequestDto);
   
       return ResponseEntity.status(HttpStatus.CREATED).body(kafkaProducerRequestDto);
   }
   
   ```

7. #### 카프카 프로듀서 (UbicUserActionProducer.java)

   ```java
   @Slf4j
   @RequiredArgsConstructor
   @Component
   public class UbicUserActionProducer {
   
       private final KafkaTemplate<String,String> kafkaTemplate;
       String topic = "ubic-shop-test"; // topic name
       private final ObjectMapper objectMapper;
   
       public ListenableFuture<SendResult<String,String>> sendToKafka(KafkaProducerRequestDto kafkaProducerRequestDto) throws JsonProcessingException {
   
           String key = kafkaProducerRequestDto.getUserName();
           String value = objectMapper.writeValueAsString(kafkaProducerRequestDto);
   
           ProducerRecord<String,String> producerRecord = buildProducerRecord(key, value, topic);
   
           ListenableFuture<SendResult<String,String>> listenableFuture =  kafkaTemplate.send(producerRecord);
           listenableFuture.addCallback(new ListenableFutureCallback<>() {
               @Override
               public void onFailure(Throwable ex) {
                   handleFailure(key, value, ex);
               }
   
               @Override
               public void onSuccess(SendResult<String, String> result) {
                   handleSuccess(key, value, result);
               }
           });
           return listenableFuture;
       }
   
       private ProducerRecord<String, String> buildProducerRecord(String key, String value, String topic) {
           List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));
           return new ProducerRecord<>(topic, null, key, value, recordHeaders);
       }
   
       private void handleFailure(String key, String value, Throwable ex) {
           log.error("Error Sending the Message and the exception is {}", ex.getMessage());
           try {
               throw ex;
           } catch (Throwable throwable) {
               log.error("Error in OnFailure: {}", throwable.getMessage());
           }
       }
   
       private void handleSuccess(String key, String value, SendResult<String, String> result) {
           log.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
       }
   }
   
   ```

8. #### 카프카 컨슈머 (UbicUserActionConsumer.java)

   ```java
   private final ElasticsearchRestTemplate elasticsearchRestTemplate;
   
   @KafkaListener(topics = {"ubic-stream-test-re"}, containerFactory = "kafkaListenerContainerFactory")
   public void onUserActionListener(/*ConsumerRecords<String,Long>*/ConsumerRecord<String, Long> consumerRecord) throws JsonProcessingException {
   
       log.info("ubic-stream-test-re :: StreamsConsumerRecord : {}-{} ", consumerRecord.key(), consumerRecord.value());
   
       // 엘라스틱 서치에 저장
       UserAction userAction = UserAction.builder()
           .id(consumerRecord.key()) // id
           .actionType(consumerRecord.key())
           .count(consumerRecord.value())
           .build();
   
   
       // 문서 추가
       IndexQuery indexQuery = new IndexQueryBuilder()
           .withId(userAction.getActionType()) // _id
           .withObject(userAction)
           .build();
   
       elasticsearchRestTemplate.index(indexQuery);
       log.info("create doc ok.");
   
   }
   
   ```

9. #### 엘라스틱서치 도메인

   ```java
   @Getter @ToString @Builder
   @AllArgsConstructor @NoArgsConstructor
   @Document(indexName="user_action", type="_doc")
   public class UserAction {
   
       @Id
       private String id; // data 채우면서 id 삽입하는 과정이 있으므로 멤버로 필요는 없을듯!
   
       private String actionType;
       private Long count;
   }
   
   ```



## 서버2 (SB2)

1. #### build.gradle

   > A quick way to bootstrap a new project for Kafka Streams binder is to use [Spring Initializr](https://start.spring.io/) and then select "Cloud Stream~~s~~" and "Spring for Kafka Streams"

   ```python
   implementation 'org.apache.kafka:kafka-streams'
   implementation 'org.springframework.cloud:spring-cloud-stream'
   implementation 'org.springframework.cloud:spring-cloud-stream-binder-kafka-streams'
   testImplementation('org.springframework.boot:spring-boot-starter-test') {
       exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
   }
   testImplementation 'org.springframework.cloud:spring-cloud-stream-test-support'
   
   compileOnly 'org.projectlombok:lombok'
   annotationProcessor 'org.projectlombok:lombok'
   
   ```

   

2. #### .yml 자동설정 파일

   ```yaml
   spring.cloud:
     function.definition: teststream;testActionPeriod # method names
     stream:
       kafka.streams:
         binder:
           replicationFactor: 3
           brokers: ${KAFKA-URI}:${PORT}
   
           configuration:
             ssl.endpoint.identification.algorithm: https
             sasl.mechanism: PLAIN
             request.timeout.ms: 20000
             security.protocol: SASL_SSL
             retry.backoff.ms: 500
             sasl.jaas.config: org.apache.kafka.common.security.plain.PlainLoginModule required username="${USERNAME}" password="${PASSWORD}";
   
             default:
               key.serde: org.apache.kafka.common.serialization.Serdes$StringSerde
               value.serde: org.apache.kafka.common.serialization.Serdes$LongSerde
   
       bindings: # topic names
         teststream-in-0.destination: ubic-shop-test
         teststream-out-0.destination: ubic-stream-test-re
   
         testActionPeriod-in-0.destination: ubic-shop-action-period-test
         testActionPeriod-out-0.destination: ubic-shop-action-period-test
   
   ```

   

3. #### 스트림즈 코드

   ```java
   @Bean
   ObjectMapper objectMapper() {
       return new ObjectMapper();
   }
   
   @Autowired
   ObjectMapper objectMapper;
   
   @Bean
   public Function<KStream<String, String>, KStream<String, Long>> teststream() {
   
       return input -> input.peek((key, value) -> {
           KafkaProducerRequestDto requestDto = null;
           try {
               requestDto = objectMapper.readValue(value, KafkaProducerRequestDto.class);
           } catch (JsonProcessingException e) {
               e.printStackTrace();
           }
           System.out.println("key=" + key + ",value=" + requestDto); // 출력하지 말라고 공식문서 ?
       })
   
           .groupBy((key, word) -> word)
           .count(Materialized.as("counts-store"))
           .toStream();
   }
   
   ```

   > `peek` is helpful for use cases such as logging or tracking metrics or for debugging and troubleshooting.
   >
   > **Note on processing guarantees:** Any side effects of an action (such as writing to external systems) are not trackable by Kafka, which means they will typically not benefit from Kafka’s processing guarantees.
   >
   > .peek 메소드는 스트림을 그대로 반환합니다.
   >
   > 제가 사용한 것처럼 로깅 하거나, 추적해서 디버깅/트러블슈팅 할 때 유용하다고 합니다. (저도 스트림즈 함수가 잘 동작하나 확인해보려고 사용했어요)
   >
   > 하지만 외부 시스템에 쓰는 작업은 카프카 처리 성능의 이점을 누리기 어렵다는 것 같아요.

4. #### dto

   ```java
   @ToString @Getter
   @NoArgsConstructor @AllArgsConstructor
   public class KafkaProducerRequestDto {
       String userName;
       String actionType;
   }
   
   ```



키바나 화면

![스트림즈예제_키바나결과](https://github.com/Ubiquodings/ShoppingMall/blob/master/%ED%9A%8C%EC%9D%98%EB%A1%9D/%EA%B2%B0%EA%B3%BC/%EC%8A%A4%ED%8A%B8%EB%A6%BC%EC%A6%88%EC%98%88%EC%A0%9C_%ED%82%A4%EB%B0%94%EB%82%98%EA%B2%B0%EA%B3%BC.PNG)



## 정리

- #### Spring Data ElasticSearch

  - Spring Data ElasticSearch 에서 `RestHighLevelClient` 를 한단계 더 감싸 추상화 한 `ElasticsearchRestTemplate`를 제공해줍니다.

  ```java
  // 쓰기
  UserAction userAction = UserAction.builder()
      .id(consumerRecord.key()) // id
      .actionType(consumerRecord.key())
      .count(consumerRecord.value())
      .build();
  
  
  // 문서 추가
  IndexQuery indexQuery = new IndexQueryBuilder()
      .withId(userAction.getActionType()) // _id
      .withObject(userAction)
      .build();
  
  elasticsearchRestTemplate.index(indexQuery);
  
  // 읽기 by id
  UserAction shopListAction = elasticsearchRestTemplate.queryForObject(
      GetQuery.getById(${DOC_ID}),
      UserAction.class);
  // 인덱스 이름은 elasticsearchRestTemplate 내부에서 
  // 도메인 클래스(UserAction) @Document 어노테이션의 indexName 속성에 접근합니다.
  
  // 인덱스 생성
  elasticsearchRestTemplate.createIndex(UserAction.class);
  // 내부적으로 해당 이름의 인덱스가 존재하는지 확인합니다.
  ```

  



## 이슈

- 스프링부트 2.1.x 버전에서는 `ElasticsearchRestTemplate` 을 지원하지 않아서
  - 2.2.x 으로 버전업 하기도 했습니다.
- 엘라스틱 서치 인덱스를 먼저 생성하지 않으면 스프링 부트 내장 서버가 실행되지 않는다
  - 내부적으로 get-indexes 를 수행하는 모양

```python
Error creating bean with name 'userActionRepository': Invocation of init method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.data.elasticsearch.repository.support.SimpleElasticsearchRepository]: Constructor threw exception; nested exception is org.springframework.data.elasticsearch.ElasticsearchException: Error while for indexExists request: org.elasticsearch.action.admin.indices.get.GetIndexRequest@73c6ae15
```



- 엘라스틱 서치에 데이터가 없을 경우 데이터를 가져오는 로직에서 null exception 이 발생하므로 주의해야 한다

```java
UserAction shopListAction = template.queryForObject(
    GetQuery.getById("{\"userName\":\"sisi\",\"actionType\":\"shoplist\"}"),
    UserAction.class);

```

