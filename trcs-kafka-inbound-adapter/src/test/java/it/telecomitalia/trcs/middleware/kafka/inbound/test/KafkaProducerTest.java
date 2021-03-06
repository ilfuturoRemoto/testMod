package it.telecomitalia.trcs.middleware.kafka.inbound.test;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.telecomitalia.trcs.middleware.kafka.inbound.KafkaProducer;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.ActivateSubscriberRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.BlockUnblockRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.ChangeCardRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.ChangeNumberRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.ChangeSubscriberRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.CreateSubscriberRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.DeleteSubscriberRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.ReloadSubscriberRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.TrcsKafkaEventType;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.TrcsKafkaHeader;



@SpringBootTest
@DirtiesContext
public class KafkaProducerTest {
	private final Logger logger = LoggerFactory.getLogger(KafkaProducerTest.class);
    @Autowired
    private KafkaProducer producer;

    @Value("${test.topic}")
    private String topic;
    
   
    
    @Test
    public void sendMessageChangeNumber() throws Exception {
    	logger.debug("sendMessageChangeNumber");
    	String phoneNumber="9898989980";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.changeNumberRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	//headers.put(TrcsKafkaHeader.transactionID.name(), "CN.16");
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "DBSS-COM");
    	headers.put(TrcsKafkaHeader.messageID.name(),  UUID.randomUUID().toString());
    	
    	
    	ChangeNumberRequestBean bean = new ChangeNumberRequestBean();
    	
    	bean.setPhoneNumber(phoneNumber);
    	bean.setBaseOffer("OB001");
    	bean.setPhoneNumberOLO("3393214321");
    	bean.setTypeOfCard("TOC-01");
    	bean.setTypeOfCustomer("CUST-001");
    	bean.setInfo("Info");
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
   // @Test
    public void sendMessageDeleteSubscriberX() throws Exception {
    	logger.debug("sendMessageDeleteSubscriberX");
    	String phoneNumber="3391231299";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.deleteSubscriberRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "JunitTest");
    	
    	DeleteSubscriberRequestBean bean = new DeleteSubscriberRequestBean();
    	
    	bean.setPhoneNumber(phoneNumber);
    	bean.setDeleteType("MnpMvno");
    	bean.setDiscountRecover(false);
    	bean.setReason("setReason");
    	bean.setInfo("setInfo");
    	bean.setPhoneNumberMnp("3391231234");
    	bean.setTypeOfCard("setTypeOfCard");
    	
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
   //@Test
    public void sendMessageDeleteSubscriber() throws Exception {
    	logger.debug("sendMessageDeleteSubscriber");
    	String phoneNumber="3391231200";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.deleteSubscriberRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "JunitTest");
    	
    	DeleteSubscriberRequestBean bean = new DeleteSubscriberRequestBean();
    	
    	bean.setPhoneNumber(phoneNumber);
    	bean.setDeleteType("MnpMvno");
    	bean.setDiscountRecover(true);
    	bean.setReason("A");
    	bean.setInfo("setInfo");
    	bean.setPhoneNumberMnp("1234567890");
    	bean.setTypeOfCard("AA");
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
    
   // @Test
    public void sendMessageChangeCard() throws Exception {
    	logger.debug("sendMessageChangeCard");
    	String phoneNumber="3391231999";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.changeCardRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "JunitTest");
    	
    	ChangeCardRequestBean bean = new ChangeCardRequestBean();
    	
    	bean.setPhase("setPhase");
    	bean.setFeatures("setFeatures");
    	bean.setIccidNew("8939019876543210321");
    	bean.setIccidOld("8939011234567891234");
    	bean.setImsiOld("222013395947770");
    	bean.setImsiNew("222023395947200");
    	bean.setPhoneNumber(phoneNumber);
    	
    	
    	bean.setInfo("setInfo");
    	
    	bean.setTypeOfCard("ChNoBlockState");
    	bean.setUnblockSubscriber(false);
    	
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
    
    //@Test
    public void sendMessageSetBlockUnblock() throws Exception {
    	logger.debug("sendMessageSetBlockUnblock");
    	String phoneNumber="3391239898";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.blockUnblockRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "DBSS-COM");
    	
    	BlockUnblockRequestBean bean = new BlockUnblockRequestBean();
    	
    	bean.setOldReason("Q");
    	bean.setReason("A");
       	bean.setPhoneNumber(phoneNumber);
    	bean.setInfo("setInfo");
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
    
    // @Test
    public void sendMessageRestoreDelSubscriber() throws Exception {
    	logger.debug("sendMessageRestoreDelSubscriber");
    	String phoneNumber="3391231999";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.deleteSubscriberRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "JunitTest");
    	
    	DeleteSubscriberRequestBean bean = new DeleteSubscriberRequestBean();
    	
    	bean.setPhoneNumber(phoneNumber);
    	bean.setDeleteType("MnpDeactivationOnDeletedSubscriber");
    	bean.setDiscountRecover(true);
    	bean.setReason("A");
    	bean.setInfo("setInfo");
    	bean.setPhoneNumberMnp("setPhoneNumberMnp");
    	bean.setTypeOfCard("setTypeOfCard");
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
    
    //@Test
    public void sendMessageRestoreCreateSubscriber() throws Exception {
    	logger.debug("sendMessageRestoreCreateSubscriber");
    	String phoneNumber="3391231999";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.createSubscriberRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "JunitTest");
    	
    	CreateSubscriberRequestBean bean = new CreateSubscriberRequestBean();
    	bean.setIccid("333333333333");
    	bean.setImsi("2222222222");
    	bean.setCreateType("Restore");
    	bean.setInfo("Normal");
    	bean.setBaseOffer("OX6665");
    	bean.setPhoneNumber(phoneNumber);
    	bean.setLanguageId("1");
//    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//    	String dt = "20171222104757";
//    	Date date1=formatter.parse(dt); 
    	LocalDateTime dt =  LocalDateTime.of(2021,04,14,9,27,26);
       	bean.setActivationDate(dt);
       	bean.setDeactivationDate(dt);

       	bean.setTypeOfCard("AA");
       	bean.setFullMnp(true);
       	bean.setRestoredFromExpired(true);
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
  //@Test
    public void sendMessageRestoreChangeSubscriber() throws Exception {
    	logger.debug("sendMessageRestoreChangeSubscriber");
    	String phoneNumber="3391231999";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.createSubscriberRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "JunitTest");
    	
    	ChangeSubscriberRequestBean bean = new ChangeSubscriberRequestBean();
    	
    	bean.setInfo("Normal");
    	bean.setBaseOffer("OX6665");
    	bean.setPhoneNumber(phoneNumber);
    	bean.setMigrationType("PP2PCN");
       	bean.setTypeOfCard("AA");
        bean.setRifService(false);
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
  //@Test
    public void sendMessageRestoreActivateSubscriber() throws Exception {
    	logger.debug("sendMessageRestoreActivateSubscriber");
    	String phoneNumber="3391231999";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.createSubscriberRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "JunitTest");
    	
    	ActivateSubscriberRequestBean bean = new ActivateSubscriberRequestBean();
    	
    	bean.setInfo("Normal");
    	bean.setBaseOffer("OX6665");
    	bean.setPhoneNumber(phoneNumber);
    	bean.setLanguageId("1");
    	bean.setCompanyCredit(BigDecimal.TEN);
    	bean.setCredit(BigDecimal.ONE);
    	bean.setRifService(false);
    	bean.setTypeOfCard("AA");
    	
    //	LocalDateTime dt =  LocalDateTime.of(2021,04,14,9,27,26);
       	
       	bean.setTypeOfCard("AA");
       
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
    
    
   // @Test
    public void sendMessageCombine() throws Exception {
    	logger.debug("sendMessageCombine");
    	String phoneNumber="3391231999";
    	
    	HashMap<String, String> headers = new HashMap<>();
    	
    	headers.put(TrcsKafkaHeader.eventType.name(), TrcsKafkaEventType.reloadSubscriberRequest.value());
    	headers.put(TrcsKafkaHeader.transactionID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.businessID.name(), UUID.randomUUID().toString());
    	headers.put(TrcsKafkaHeader.sourceSystem.name(), "JunitTest");
    	
    	ReloadSubscriberRequestBean bean = new ReloadSubscriberRequestBean();
    	bean.setAbiCode("Abi");
    	bean.setActiveTime("32");
    	bean.setCabCode("Cab");
    	bean.setClientOperationType("A");
    	bean.setDispatcherOperationType("B");
    	//LocalDate localDate =  LocalDate.of(2021,10,10);
    	
    	//logger.info("localDate " + localDate.toString());
    	//bean.setPayDate(localDate);
    	bean.setPayDate("2021-10-12");
    	bean.setPhoneNumber(phoneNumber);
    	bean.setReloadServiceId("C");
    	bean.setReloadValue(BigDecimal.ONE);
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	ByteArrayOutputStream out = new ByteArrayOutputStream();
    	
    	objectMapper.writeValue(out, bean);
        producer.send(topic, out.toString(), phoneNumber, headers);        
    }
}