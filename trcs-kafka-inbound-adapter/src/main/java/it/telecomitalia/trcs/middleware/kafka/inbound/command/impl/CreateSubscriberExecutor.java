package it.telecomitalia.trcs.middleware.kafka.inbound.command.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.telecomitalia.soa.soap.soapheader.HeaderType;
import it.telecomitalia.soa.trcs.gateway.AccessoryType;
import it.telecomitalia.soa.trcs.gateway.BasketGroupedType;
import it.telecomitalia.soa.trcs.gateway.InstallSubscriberXIbData;
import it.telecomitalia.soa.trcs.gateway.InstallSubscriberXRequest;
import it.telecomitalia.soa.trcs.gateway.InstallSubscriberXResponse;
import it.telecomitalia.soa.trcs.gateway.MandatoryOfferType;
import it.telecomitalia.soa.trcs.gateway.MandatorySubscriptionType;
import it.telecomitalia.soa.trcs.gateway.OfferType;
import it.telecomitalia.soa.trcs.gateway.ResourceType;
import it.telecomitalia.soa.trcs.gateway.RestoreSubscriberXIbData;
import it.telecomitalia.soa.trcs.gateway.RestoreSubscriberXRequest;
import it.telecomitalia.soa.trcs.gateway.RestoreSubscriberXResponse;
import it.telecomitalia.soa.trcs.gateway.SaleOperationXIbData;
import it.telecomitalia.soa.trcs.gateway.SaleOperationXRequest;
import it.telecomitalia.soa.trcs.gateway.SaleOperationXResponse;
import it.telecomitalia.soa.trcs.gateway.SubscriptionType;
import it.telecomitalia.soa.trcs.gateway.infobus.commons.InfobusMessage;
import it.telecomitalia.trcs.middleware.kafka.inbound.builder.HeaderTypeBuilder;
import it.telecomitalia.trcs.middleware.kafka.inbound.command.ExecutorSynchronousFailed;
import it.telecomitalia.trcs.middleware.kafka.inbound.command.TrcsInboundExecutorException;
import it.telecomitalia.trcs.middleware.kafka.inbound.command.impl.type.CreateType;
import it.telecomitalia.trcs.middleware.kafka.inbound.command.impl.type.InfoType;
import it.telecomitalia.trcs.middleware.kafka.inbound.command.impl.util.UtilModernization;
import it.telecomitalia.trcs.middleware.kafka.inbound.config.ResponseTargets;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.CreateSubscriberRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.CreateSubscriberResponseBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.TrcsKafkaEventType;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.TrcsKafkaHeader;
import it.telecomitalia.trcs.middleware.kafka.inbound.logging.HydraLogBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.logging.HydraLogThreadLocal;
import it.telecomitalia.trcs.middleware.ws.client.OpscProvisioningClient;


public class CreateSubscriberExecutor extends AbstractExecutor{

	private final Logger logger = LoggerFactory.getLogger(CreateSubscriberExecutor.class);


	public CreateSubscriberExecutor(OpscProvisioningClient client, ResponseTargets responseTargets) {
		super(client, responseTargets);
	}


	@Override
	public void execute(Map<String, Object> headers, String payload) throws ExecutorSynchronousFailed{

		ObjectMapper objectMapper = new ObjectMapper();

		try {
			logger.debug("Request [{}]", payload);

			// Converte il JSON in POJO
			CreateSubscriberRequestBean request = objectMapper.readValue(payload, CreateSubscriberRequestBean.class);
			
			HydraLogThreadLocal.getLogBean().getEvent().setPayload(request);
			// Effettua il mapping con l'header SOAP
			HeaderType headerType = new HeaderTypeBuilder(headers).build();
			
					
			switch (Enum.valueOf(CreateType.class,request.getCreateType())) {
			case Restore: 
				switch (Enum.valueOf(InfoType.class,request.getInfo())) {
				case Normal:
				case NumPortSubscr:
				case MnpSubscr:
				case ExpSubscr:
					if (!request.getBaseOffer().startsWith("OB6")) {
						logger.info("call RestoreSubscriber(CreateSub)");
						// 1
						callRestore(objectMapper,request,headers,headerType);
					}
					else {
						logger.info("call RestoreSubscriberX"); 
						// 2
						callRestoreX(objectMapper,request,headers,headerType);

					}
					break;
				}
			case Migrate:
				logger.info("call MigrateSubscriber"); 
				// 3
				callMigrate(objectMapper,request,headers,headerType);
				break;
			case Install:
				if(request.isFullMnp()) {
					logger.info("call InstallSubscriberX");
					// 4
					callInstallX(objectMapper,request,headers,headerType);
					
				}
				else {
					logger.info("call SaleOperationX");
					// 5
					callSaleOperationX(objectMapper,request,headers,headerType);
				
				}
			default:
				return ;
			}

		} catch (ExecutorSynchronousFailed e) {
			throw e;
		}catch (Exception e) {
			logger.error("CreateSubscriber calling error", e);
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw new TrcsInboundExecutorException(e);
		}

	}

	// 1
	private void callRestore(ObjectMapper objectMapper,CreateSubscriberRequestBean request,Map<String, Object> headers,HeaderType headerType) throws ExecutorSynchronousFailed,Exception {


		InfobusMessage response = callWebServiceRestoreSubscriber(request, headers, headerType);
		logger.info("RestoreSubsciber result=[{}]", response.getIbRetCode());

		if ("1".equals(response.getIbRetCode())) {
			//TODO: Scrivere Log di Success
			HydraLogThreadLocal.getLogBean().setResult(HydraLogBean.Result.success);

		} else {
			//TODO: Gestire Errore di invocazione inviando risposta KO su Kafka
			//TODO: Inserire Logging
			CreateSubscriberResponseBean responsePayload = this.createRestoreMigrateResponsePayload(headers, request, response);
			
			throw new ExecutorSynchronousFailed(
					this.getReponseTargets().getResponseTarget(TrcsKafkaEventType.createSubscriberResponse),
					TrcsKafkaHeader.createResponseKafkaHeader(headers, TrcsKafkaEventType.createSubscriberResponse),
					responsePayload,
					objectMapper.writeValueAsString(responsePayload),
					request.getPhoneNumber()
					);

		} 

	}
	
	/////// Inizio Restore
	// 1.1
	private InfobusMessage callWebServiceRestoreSubscriber(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) {
		// Effettua il mapping con il body SOAP
		JAXBElement<InfobusMessage> wsRequest = this.createWebServiceRestoreRequest(request, headers, headerType);
		InfobusMessage response = this.getOpscClient().restoreSubscriber(headerType, wsRequest);
		return response;
	}

	
    // 1.2
	private CreateSubscriberResponseBean createRestoreMigrateResponsePayload(Map<String, Object> headers, CreateSubscriberRequestBean request,  InfobusMessage response) {

		CreateSubscriberResponseBean result = new CreateSubscriberResponseBean(headers.get(TrcsKafkaHeader.sourceSystem.name()).toString(),
				request.getPhoneNumber(),
				KafkaErrorCodes.decodeFromOpsc(response.getIbRetCode()),
				KafkaErrorCodes.messageFromOpsc(response.getIbRetCode()),
				request.getCreateType(),
				request.getIccid(),
				request.getImsi(),
				null
				);

		result.setSubsystemErrorCode(response.getIbRetCode());
     	return result;

	}

	
	// 1.3
	private JAXBElement<InfobusMessage>	 createWebServiceRestoreRequest(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) {
		
		InfobusMessage message =  new InfobusMessage();
		message.setIbAppDep1("0");
		message.setIbAppDep2("0");
		message.setIbRetCode("1");
		message.setIbIdSrvc("RESTOREG");
		message.setIbData(new InfobusMessage.IbData());
		// Create buffer flat for ibdata
		StringBuffer strBuf =  new StringBuffer("   channel");
		String operType = new String();
		
		switch (Enum.valueOf(InfoType.class,request.getInfo())) {
		  case Normal:
			  operType="0";
			  break;
		  case NumPortSubscr:
			  operType="1";
			  break;
		  case MnpSubscr:
			  operType="2";
			  break;
		  case ExpSubscr:
			  operType="4";
			  break;
			
		}
		strBuf.append(operType);
		
		strBuf.append(StringUtils.leftPad(request.getPhoneNumber(),12));
		// iccid
		strBuf.append(StringUtils.leftPad(request.getIccid()!=null ? request.getIccid():"",20));
		//imsi
		strBuf.append(StringUtils.leftPad(request.getImsi()!=null ? request.getImsi():"",16));
		// pin puk pin2 puk2 category
		
		strBuf.append(StringUtils.leftPad("",30));
		// debit
		strBuf.append(StringUtils.leftPad((request.getCredit()==null ? "0.000000" : request.getCredit().toString()),16));
		// tariff
		strBuf.append(StringUtils.leftPad(request.getBaseOffer().substring(2),5));
		// language
		strBuf.append(StringUtils.leftPad(request.getLanguageId(),2));
		// aststate
		strBuf.append("A");
		// serviceid
		strBuf.append(StringUtils.leftPad("",5));
		// setInstalldate
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		
		strBuf.append(StringUtils.leftPad((request.getInstallationDate()!=null ? formatter.format(request.getInstallationDate()) : formatter.format(new Date())),14));
		//setActivedate
		strBuf.append(StringUtils.leftPad((request.getActivationDate()!=null ? formatter.format(request.getActivationDate()) : ""),14));
		//setDeactivated
		strBuf.append(StringUtils.leftPad((request.getDeactivationDate()!=null ? formatter.format(request.getDeactivationDate()) : ""),14));
		//setDeactivedate
		strBuf.append(StringUtils.leftPad((request.getDeactivationDate()!=null ? formatter.format(request.getDeactivationDate()) : ""),14));
		//setExpirydate
		strBuf.append(StringUtils.leftPad((request.getExpirationDate()!=null ? formatter.format(request.getExpirationDate()) : ""),14));
		// setExpiredreason
		strBuf.append(StringUtils.leftPad(request.getExpiredReason()!=null ? request.getExpiredReason() :"",1));
		// setInformedmessageid
		strBuf.append(StringUtils.leftPad("",1));
		//  setNumreloads
		strBuf.append(StringUtils.leftPad(request.getNumReloads()!=null ? request.getNumReloads():"",5));
		// setNumchangetariff 
		strBuf.append(StringUtils.leftPad(request.getNumBaseOfferChanges()!=null ? request.getNumBaseOfferChanges():"",5));
		//setChangetariffdate 
		strBuf.append(StringUtils.leftPad((request.getLastBaseOfferChange()!=null ? formatter.format(request.getLastBaseOfferChange()) : ""),14));
		//setLastcalldate
		strBuf.append(StringUtils.leftPad((request.getLastCreditUpdate()!=null ? formatter.format(request.getLastCreditUpdate()) : ""),14));
		// setTypeofcard
		strBuf.append(StringUtils.leftPad(request.getTypeOfCard()!=null ? request.getTypeOfCard():"",2));
		// setCardphase 
		strBuf.append(StringUtils.leftPad(request.getPhase()!=null ? request.getPhase():"",2));
		//setAccessorycode
		//setDistrictid
		//setNumdistricts 
		//setChangedistrictdate
		strBuf.append(StringUtils.leftPad("",31));
		// setR_system
		strBuf.append("3");
		// setNumbilledsms setNumfreesms setAccessoryedate
		strBuf.append(StringUtils.leftPad("",24));
		// setDebitco
		strBuf.append(StringUtils.leftPad("0.000000",16));
		// setBonus1
		strBuf.append(StringUtils.leftPad((request.getBonus1()==null ? "0.000000":request.getBonus1().toString()),16));
		// setBonus2
		strBuf.append(StringUtils.leftPad("0.000000",10));
		// setNumfreeseconds
		strBuf.append(StringUtils.leftPad("",5));
		// setCardfeatures
		strBuf.append(StringUtils.leftPad(request.getFeatures()!=null ? request.getFeatures():"",8));
		//setServcardedate2 
		strBuf.append(StringUtils.leftPad("",14));
		// setServices
		long fullMnp = request.isFullMnp() ? 67108864 : 0;
		long restoredFromExpired = request.isRestoredFromExpired() ? 2097152 :0;
		Long  X = fullMnp + restoredFromExpired;
		strBuf.append((StringUtils.leftPad(X.toString(),10)));
		
		// setServcardedate5 
		// setServcardedate1
		// setServcardedate3
		//setServcardedate4 
		strBuf.append(StringUtils.leftPad("",56));
		//setActivetime
		strBuf.append(StringUtils.leftPad(request.getActiveTime()!=null? request.getActiveTime():"",3));
        //setDeactivetime	setRetbonus setTypeofbonus setTypeoflist 
		//setRetbonusedate setServcard_edate6	setAccessorycode_edate1	setAccessorycode_edate2 setPointsbasket1edate  setPointsbasket2edate setPointsbasket3edate	setPointsbasket1 setPointsbasket2 setPointsbasket3 setPointsbasket4edate setPointsbasket5edate
		strBuf.append(StringUtils.leftPad("",189));
	    
		message.getIbData().setIbLenData(strBuf.toString().length());
		message.getIbData().setValue(strBuf.toString());
			
		message.setIbData(message.getIbData());
		it.telecomitalia.soa.trcs.gateway.ObjectFactory obj = new it.telecomitalia.soa.trcs.gateway.ObjectFactory();
		JAXBElement<InfobusMessage> result = obj.createRestoreSubscriberRequest(message);
	
		return result;
	}
	

	/// fine Restore
	

	
    /////// Inizio RestoreX
	// 2
	private void callRestoreX(ObjectMapper objectMapper, CreateSubscriberRequestBean request,Map<String, Object> headers,HeaderType headerType) throws ExecutorSynchronousFailed,Exception {

		RestoreSubscriberXResponse response = callWebServiceRestoreSubscriberX(request, headers, headerType);
		logger.info("RestoreSubsciberX result=[{}]", response.getIbRetCode());

		if ("1".equals(response.getIbRetCode())) {
			//TODO: Scrivere Log di Success
			HydraLogThreadLocal.getLogBean().setResult(HydraLogBean.Result.success);

		} else {
			//TODO: Gestire Errore di invocazione inviando risposta KO su Kafka
			//TODO: Inserire Logging
			CreateSubscriberResponseBean responsePayload = this.createResponsePayloadRestoreX(headers, request, response);
			
			throw new ExecutorSynchronousFailed(
					this.getReponseTargets().getResponseTarget(TrcsKafkaEventType.createSubscriberResponse),
					TrcsKafkaHeader.createResponseKafkaHeader(headers, TrcsKafkaEventType.createSubscriberResponse),
					responsePayload,
					objectMapper.writeValueAsString(responsePayload),
					request.getPhoneNumber()
					);

		} 


	}
	
    // 2.1
	private RestoreSubscriberXResponse callWebServiceRestoreSubscriberX(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) throws  DatatypeConfigurationException {
		// Effettua il mapping con il body SOAP
		RestoreSubscriberXRequest wsRequest = this.createWebServiceRestoreXRequest(request, headers, headerType);
		// Invoca il servizio di cambio numero di GW
		RestoreSubscriberXResponse response = this.getOpscClient().restoreSubscriberX(headerType, wsRequest);
		return response;
	}
	
	// 2.2
	private CreateSubscriberResponseBean createResponsePayloadRestoreX(Map<String, Object> headers, CreateSubscriberRequestBean request,  RestoreSubscriberXResponse response) {

	
		CreateSubscriberResponseBean result = new CreateSubscriberResponseBean(headers.get(TrcsKafkaHeader.sourceSystem.name()).toString(),
				request.getPhoneNumber(),
				KafkaErrorCodes.decodeFromOpsc(response.getIbRetCode()),
				KafkaErrorCodes.messageFromOpsc(response.getIbRetCode()),
				request.getCreateType(),
				request.getIccid(),
				request.getImsi(),
				null
				);

		result.setSubsystemErrorCode(response.getIbRetCode());
     	return result;
 

	}
	
	// 2.3
	private RestoreSubscriberXRequest createWebServiceRestoreXRequest(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) throws  DatatypeConfigurationException {

		RestoreSubscriberXRequest wsRequest = new RestoreSubscriberXRequest();

		wsRequest.setIbRetCode("1");
		wsRequest.setIbAppDep1("0");
		wsRequest.setIbAppDep2("0");
		wsRequest.setIbIdSrvc("RESTORE");
		wsRequest.setIbData(new RestoreSubscriberXRequest.IbData());
		wsRequest.getIbData().setIbLenData(0);

		RestoreSubscriberXIbData payload = new RestoreSubscriberXIbData();
		wsRequest.getIbData().setRequest(payload);

		payload.setRequestType("TwoStep");

		payload.setTransaction(new RestoreSubscriberXIbData.Transaction());

		payload.getTransaction().setTID(headerType.getTransactionID());
		payload.getTransaction().setSubsystem(String.valueOf(headers.get(TrcsKafkaHeader.channel.name())));
		payload.getTransaction().setService(wsRequest.getIbIdSrvc());
		payload.getTransaction().setIDSystem(headerType.getSourceSystem());
		payload.getTransaction().setRetCode("1");

		payload.setClientKeys(new RestoreSubscriberXIbData.ClientKeys());
		payload.getClientKeys().setMSISDN(request.getPhoneNumber());

		payload.setOperation(new RestoreSubscriberXIbData.Operation());
		payload.getOperation().setOperationType("RestoreSubscriber");
		payload.getOperation().setInfo(request.getInfo());
		
		
		RestoreSubscriberXIbData.Operation.RestoreSubscriber restoreSubscriber = new RestoreSubscriberXIbData.Operation.RestoreSubscriber();
		
		// Client
		RestoreSubscriberXIbData.Operation.RestoreSubscriber.Client client = new RestoreSubscriberXIbData.Operation.RestoreSubscriber.Client();
				
		client.setReason("A");
		client.setLanguageId(request.getLanguageId());
		client.setTypeOfCard(request.getTypeOfCard());
		client.setNumReloads(request.getNumReloads() == null ? "0" : request.getNumReloads());
		client.setNumTariffs(request.getNumBaseOfferChanges() == null ? "0" : request.getNumBaseOfferChanges());
		client.setLastChangeTariff(request.getLastBaseOfferChange() == null ? "" : request.getLastBaseOfferChange().toString());
		
		client.setInstallationDate(UtilModernization.localDateTime2XMLGregorianCalendar(request.getInstallationDate()));
		if (request.getActivationDate()!=null)
			client.setActivationDate(UtilModernization.localDateTime2XMLGregorianCalendar(request.getActivationDate()));
		client.setServiceProviderId("WRAPPER");
		if (request.getLastCreditUpdate()!=null)
			client.setLastEventDate((UtilModernization.localDateTime2XMLGregorianCalendar(request.getLastCreditUpdate())));
	
		client.setAccountType(request.isFullMnp() ? new BigInteger("3") : new BigInteger("1"));
		
		RestoreSubscriberXIbData.Operation.RestoreSubscriber.Client.GSM gsm = new RestoreSubscriberXIbData.Operation.RestoreSubscriber.Client.GSM(); 
		gsm.setCardFeature(request.getFeatures());
		gsm.setCardPhase(request.getPhase());
		gsm.setICCID(request.getIccid());
		gsm.setIMSI(request.getImsi());
		client.setGSM(gsm);
		
		restoreSubscriber.setClient(client);
		
		// Subscription
		MandatorySubscriptionType mandatorySubscriptionType = new MandatorySubscriptionType();
		mandatorySubscriptionType.setName("WRAPPER");
		restoreSubscriber.getSubscription().add(mandatorySubscriptionType);
		
		MandatoryOfferType mandatoryOfferType = new MandatoryOfferType();
		mandatoryOfferType.setName(request.getBaseOffer());
		mandatorySubscriptionType.getOffer().add(mandatoryOfferType);
		
		AccessoryType accessoryType = new AccessoryType();
		accessoryType.setName("WRAPPER");
		mandatorySubscriptionType.getOffer().get(0).getAccessory().add(accessoryType);
		
		BasketGroupedType basketGroupedType = new BasketGroupedType();
		basketGroupedType.setName("DEBIT");
		basketGroupedType.setValue(request.getCredit() == null ? BigDecimal.ZERO  : request.getCredit());
		mandatorySubscriptionType.getOffer().get(0).getAccessory().get(0).getBasket().add(basketGroupedType);
		
		ResourceType resourceType = new ResourceType();
		resourceType.setName("RSRIF");
		resourceType.setState(request.isRifService() ? "1" : "2");
		mandatorySubscriptionType.getOffer().get(0).getAccessory().get(0).getResource().add(resourceType);


		payload.getOperation().setRestoreSubscriber(restoreSubscriber);
		
		
		return wsRequest;
	}
	
	///// fine RestoreX
	
	///// Inizio Migrate 
	// 3
	private void callMigrate(ObjectMapper objectMapper,CreateSubscriberRequestBean request,Map<String, Object> headers,HeaderType headerType) throws ExecutorSynchronousFailed,Exception {


		InfobusMessage response = callWebServiceMigrateSubscriber(request, headers, headerType);
		logger.info("MigrateSubsciber result=[{}]", response.getIbRetCode());

		if ("1".equals(response.getIbRetCode())) {
			//TODO: Scrivere Log di Success
			HydraLogThreadLocal.getLogBean().setResult(HydraLogBean.Result.success);

		} else {
			//TODO: Gestire Errore di invocazione inviando risposta KO su Kafka
			//TODO: Inserire Logging
			CreateSubscriberResponseBean responsePayload = this.createRestoreMigrateResponsePayload(headers, request, response);
			
			throw new ExecutorSynchronousFailed(
					this.getReponseTargets().getResponseTarget(TrcsKafkaEventType.createSubscriberResponse),
					TrcsKafkaHeader.createResponseKafkaHeader(headers, TrcsKafkaEventType.createSubscriberResponse),
					responsePayload,
					objectMapper.writeValueAsString(responsePayload),
					request.getPhoneNumber()
					);

		} 

	}
	
	// 3.1
	private InfobusMessage callWebServiceMigrateSubscriber(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) {
		// Effettua il mapping con il body SOAP
		JAXBElement<InfobusMessage>  wsRequest = this.createWebServiceMigrateRequest(request, headers, headerType);
		// Invoca il servizio di cambio numero di GW
		InfobusMessage response = this.getOpscClient().migrateSubscriber(headerType, wsRequest);
		return response;
	}

	// 3.2 createRestoreMigrateResponsePayload in comune a migrate e restore
	// 3.3
	private JAXBElement<InfobusMessage> createWebServiceMigrateRequest(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) {

		InfobusMessage message =  new InfobusMessage();
		message.setIbAppDep1("0");
		message.setIbAppDep2("0");
		message.setIbRetCode("1");
		message.setIbIdSrvc("MIGRPREPG");
		message.setIbData(new InfobusMessage.IbData());
		
		// Create buffer flat for ibdata
		StringBuffer strBuf =  new StringBuffer("   channel");
		// msisdn
		strBuf.append(StringUtils.leftPad(request.getPhoneNumber(),20));
		// iccid
		strBuf.append(StringUtils.leftPad(request.getIccid(),20));
		// imsi
		strBuf.append(StringUtils.leftPad(request.getImsi(),16));
		//pin puk pin2 puk2
		strBuf.append(StringUtils.leftPad("",24));
		// category
		strBuf.append(request.isRifService() ? "RSRIF" : StringUtils.leftPad("",6));
		// debit
		BigDecimal valueDebit = BigDecimal.ZERO;
		if (request.getBaseOffer()!=null && request.getBaseOffer().trim().length()>0) {
			if(request.getBaseOffer().equals("OB601") || request.getBaseOffer().equals("OB63"))
			  valueDebit =  request.getCompanyCredit();
		    else
		      valueDebit =  request.getCredit();
		}
		strBuf.append(StringUtils.leftPad(valueDebit.toString(),10));
		strBuf.append(StringUtils.leftPad(request.getBaseOffer() != null ? request.getBaseOffer().substring(2):"",5));
		// languageid
		strBuf.append(StringUtils.leftPad(request.getLanguageId(),2));
		// district
		strBuf.append(StringUtils.leftPad("",4));
		// asstate
		strBuf.append("A");
		// cardfeatures
		strBuf.append(StringUtils.leftPad(request.getFeatures(),8));
		// dn_options
		strBuf.append("0");
		// mnpflag
		strBuf.append(request.isFullMnp()  ? "1" :"0");
		//SLIDE_FLAG
		strBuf.append("0");
		// VirtualMSISDN
		strBuf.append(StringUtils.leftPad("",20));	
		// toc
		strBuf.append(StringUtils.leftPad(request.getTypeOfCard(),2));
		
		message.getIbData().setIbLenData(strBuf.toString().length());
		message.getIbData().setValue(strBuf.toString());
			
		message.setIbData(message.getIbData());
		it.telecomitalia.soa.trcs.gateway.ObjectFactory obj = new it.telecomitalia.soa.trcs.gateway.ObjectFactory();
		JAXBElement<InfobusMessage> result = obj.createDeleteSubscriberRequest(message);
	
		return result;
		
	}


	
	/////// Inizio InstallSubscriberX
	// 4
	private void callInstallX(ObjectMapper objectMapper, CreateSubscriberRequestBean request,Map<String, Object> headers,HeaderType headerType) throws ExecutorSynchronousFailed,Exception {

		InstallSubscriberXResponse response = callWebServiceInstallSubscriberX(request, headers, headerType);
		logger.info("InstallSubsciberX result=[{}]", response.getIbRetCode());

		if ("1".equals(response.getIbRetCode())) {
			//TODO: Scrivere Log di Success
			HydraLogThreadLocal.getLogBean().setResult(HydraLogBean.Result.success);

		} else {
			//TODO: Gestire Errore di invocazione inviando risposta KO su Kafka
			//TODO: Inserire Logging
			CreateSubscriberResponseBean responsePayload = this.createResponsePayloadInstallX(headers, request, response);

			throw new ExecutorSynchronousFailed(
					this.getReponseTargets().getResponseTarget(TrcsKafkaEventType.createSubscriberResponse),
					TrcsKafkaHeader.createResponseKafkaHeader(headers, TrcsKafkaEventType.createSubscriberResponse),
					responsePayload,
					objectMapper.writeValueAsString(responsePayload),
					request.getPhoneNumber()
					);

		} 


	}

	// 4.1
	private InstallSubscriberXResponse callWebServiceInstallSubscriberX(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) throws  DatatypeConfigurationException {
		// Effettua il mapping con il body SOAP
		InstallSubscriberXRequest wsRequest = this.createWebServiceInstallXRequest(request, headers, headerType);
		// Invoca il servizio di cambio numero di GW
		InstallSubscriberXResponse response = this.getOpscClient().installSubscriberX(headerType, wsRequest);
		return response;
	}

	// 4.2
	private CreateSubscriberResponseBean createResponsePayloadInstallX(Map<String, Object> headers, CreateSubscriberRequestBean request,  InstallSubscriberXResponse response) {


		CreateSubscriberResponseBean result = new CreateSubscriberResponseBean(headers.get(TrcsKafkaHeader.sourceSystem.name()).toString(),
				request.getPhoneNumber(),
				KafkaErrorCodes.decodeFromOpsc(response.getIbRetCode()),
				KafkaErrorCodes.messageFromOpsc(response.getIbRetCode()),
				request.getCreateType(),
				request.getIccid(),
				request.getImsi(),
				null
				);

		result.setSubsystemErrorCode(response.getIbRetCode());
		return result;


	}

	// 4.3
	private InstallSubscriberXRequest createWebServiceInstallXRequest(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) throws  DatatypeConfigurationException {

		InstallSubscriberXRequest wsRequest = new InstallSubscriberXRequest();

		wsRequest.setIbRetCode("1");
		wsRequest.setIbAppDep1("0");
		wsRequest.setIbAppDep2("0");
		wsRequest.setIbIdSrvc("OLOMNP");
		wsRequest.setIbData(new InstallSubscriberXRequest.IbData());
		wsRequest.getIbData().setIbLenData(0);

		InstallSubscriberXIbData payload = new InstallSubscriberXIbData();
		wsRequest.getIbData().setRequest(payload);

		payload.setRequestType("TwoStep");

		payload.setTransaction(new InstallSubscriberXIbData.Transaction());

		payload.getTransaction().setTID(headerType.getTransactionID());
		payload.getTransaction().setSubsystem(String.valueOf(headers.get(TrcsKafkaHeader.channel.name())));
		payload.getTransaction().setService(wsRequest.getIbIdSrvc());
		payload.getTransaction().setIDSystem(headerType.getSourceSystem());
		payload.getTransaction().setRetCode("1");

		payload.setClientKeys(new InstallSubscriberXIbData.ClientKeys());
		payload.getClientKeys().setMSISDN(request.getPhoneNumber());

		payload.setOperation(new InstallSubscriberXIbData.Operation());
		payload.getOperation().setOperationType("InstallSubscriber");
		payload.getOperation().setInfo(request.getInfo());


		InstallSubscriberXIbData.Operation.InstallSubscriber installSubscriber = new InstallSubscriberXIbData.Operation.InstallSubscriber();
		installSubscriber.setInstallType("Mnp");
		
		// ClientMnp
		InstallSubscriberXIbData.Operation.InstallSubscriber.ClientMnp clientMnp = new InstallSubscriberXIbData.Operation.InstallSubscriber.ClientMnp();

		clientMnp.setReason("A");
		clientMnp.setLanguageId(request.getLanguageId());
		clientMnp.setTypeOfCard(request.getTypeOfCard());
		
        // GSM
		InstallSubscriberXIbData.Operation.InstallSubscriber.ClientMnp.GSM gsm = new InstallSubscriberXIbData.Operation.InstallSubscriber.ClientMnp.GSM(); 
		
		gsm.setCardFeature(request.getFeatures());
		gsm.setCardPhase(request.getPhase());
		gsm.setICCID(request.getIccid());
		gsm.setIMSI(request.getImsi());
		clientMnp.setGSM(gsm);
       
		// 
		installSubscriber.setClientMnp(clientMnp);

		InstallSubscriberXIbData.Operation.InstallSubscriber.Mnp mnp = new InstallSubscriberXIbData.Operation.InstallSubscriber.Mnp();
		
		SubscriptionType subscriptionType = new SubscriptionType();
		subscriptionType.setName("WRAPPER");
		
		mnp.getSubscription().add(subscriptionType);
		
		OfferType offerType = new OfferType();
		offerType.setName(request.getBaseOffer());
		subscriptionType.getOffer().add(offerType);
		
		AccessoryType  accessoryType = new AccessoryType();
		accessoryType.setName("WRAPPER");
		offerType.getAccessory().add(accessoryType);
		
		BasketGroupedType basketGroupedType  = new BasketGroupedType();
		basketGroupedType.setName("DEBIT");
		basketGroupedType.setValue(request.getCredit()!=null? request.getCredit():BigDecimal.ZERO);

		accessoryType.getBasket().add(basketGroupedType);
		
		ResourceType resourceType = new ResourceType();
		resourceType.setName("RSRIF");
		resourceType.setState(request.isRifService() ? "1" :"2");
		accessoryType.getResource().add(resourceType);
		
		mnp.getSubscription().add(subscriptionType);
		
		installSubscriber.setMnp(mnp);
		
		payload.getOperation().setInstallSubscriber(installSubscriber);


		return wsRequest;
	}

	///// fine InstallSubscriberX
	
	

	/////// Inizio SaleOperationX
	// 5
	private void callSaleOperationX(ObjectMapper objectMapper, CreateSubscriberRequestBean request,Map<String, Object> headers,HeaderType headerType) throws ExecutorSynchronousFailed,Exception {

		SaleOperationXResponse response = callWebServiceSaleOperationX(request, headers, headerType);
		logger.info("SaleOperationX result=[{}]", response.getIbRetCode());

		if ("1".equals(response.getIbRetCode())) {
			//TODO: Scrivere Log di Success
			HydraLogThreadLocal.getLogBean().setResult(HydraLogBean.Result.success);

		} else {
			//TODO: Gestire Errore di invocazione inviando risposta KO su Kafka
			//TODO: Inserire Logging
			CreateSubscriberResponseBean responsePayload = this.createResponsePayloadSaleOperationX(headers, request, response);

			throw new ExecutorSynchronousFailed(
					this.getReponseTargets().getResponseTarget(TrcsKafkaEventType.createSubscriberResponse),
					TrcsKafkaHeader.createResponseKafkaHeader(headers, TrcsKafkaEventType.createSubscriberResponse),
					responsePayload,
					objectMapper.writeValueAsString(responsePayload),
					request.getPhoneNumber()
					);

		} 


	}

	// 5.1
	private SaleOperationXResponse callWebServiceSaleOperationX(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) throws  DatatypeConfigurationException {
		// Effettua il mapping con il body SOAP
		SaleOperationXRequest wsRequest = this.createWebServiceSaleOperationXRequest(request, headers, headerType);
		// Invoca il servizio di cambio numero di GW
		SaleOperationXResponse response = this.getOpscClient().saleOperationX(headerType, wsRequest);
		return response;
	}

	// 5.2
	private CreateSubscriberResponseBean createResponsePayloadSaleOperationX(Map<String, Object> headers, CreateSubscriberRequestBean request,  SaleOperationXResponse response) {


		CreateSubscriberResponseBean result = new CreateSubscriberResponseBean(headers.get(TrcsKafkaHeader.sourceSystem.name()).toString(),
				request.getPhoneNumber(),
				KafkaErrorCodes.decodeFromOpsc(response.getIbRetCode()),
				KafkaErrorCodes.messageFromOpsc(response.getIbRetCode()),
				request.getCreateType(),
				request.getIccid(),
				request.getImsi(),
				null
				);

		result.setSubsystemErrorCode(response.getIbRetCode());
		return result;


	}

	// 5.3
	private SaleOperationXRequest createWebServiceSaleOperationXRequest(CreateSubscriberRequestBean request, Map<String, Object> headers,
			HeaderType headerType) throws  DatatypeConfigurationException {

		SaleOperationXRequest wsRequest = new SaleOperationXRequest();

		wsRequest.setIbRetCode("1");
		wsRequest.setIbAppDep1("0");
		wsRequest.setIbAppDep2("0");
		wsRequest.setIbIdSrvc("vendita");
		wsRequest.setIbData(new SaleOperationXRequest.IbData());
		wsRequest.getIbData().setIbLenData(0);

		SaleOperationXIbData payload = new SaleOperationXIbData();
		wsRequest.getIbData().setRequest(payload);

		payload.setRequestType("TwoStep");

		payload.setTransaction(new SaleOperationXIbData.Transaction());

		payload.getTransaction().setTID(headerType.getTransactionID());
		payload.getTransaction().setSubsystem(String.valueOf(headers.get(TrcsKafkaHeader.channel.name())));
		payload.getTransaction().setService(wsRequest.getIbIdSrvc());
		payload.getTransaction().setIDSystem(headerType.getSourceSystem());
		payload.getTransaction().setRetCode("1");

		payload.setClientKeys(new SaleOperationXIbData.ClientKeys());
		payload.getClientKeys().setMSISDN(request.getPhoneNumber());

		payload.setOperation(new SaleOperationXIbData.Operation());
		payload.getOperation().setOperationType("SaleOperation");
		payload.getOperation().setInfo(request.getInfo());

		SaleOperationXIbData.Operation.SaleOperation saleOperation = new SaleOperationXIbData.Operation.SaleOperation();
		
		saleOperation.setSaleOpType("PCNInstOp");
		SaleOperationXIbData.Operation.SaleOperation.Client client = new SaleOperationXIbData.Operation.SaleOperation.Client();
		
		client.setReason("A");
		client.setLanguageId(request.getLanguageId());
		client.setTypeOfCard(request.getTypeOfCard());
		
		SaleOperationXIbData.Operation.SaleOperation.Client.GSM gsm = new SaleOperationXIbData.Operation.SaleOperation.Client.GSM();
		
		gsm.setCardFeature(request.getFeatures());
		gsm.setCardPhase(request.getPhase());
		gsm.setICCID(request.getIccid());
		gsm.setIMSI(request.getImsi());
				
		client.setGSM(gsm);
		
		saleOperation.setClient(client);
		
		SaleOperationXIbData.Operation.SaleOperation.PCNSaleOp  pCNSaleOp = new SaleOperationXIbData.Operation.SaleOperation.PCNSaleOp();
		
		SubscriptionType subscriptionType = new SubscriptionType();
		subscriptionType.setName("WRAPPER");
		pCNSaleOp.setSubscription(subscriptionType);
		
		OfferType offerType = new OfferType();
		offerType.setName(request.getBaseOffer());
		subscriptionType.getOffer().add(offerType);
		
		
		AccessoryType  accessoryType = new AccessoryType();
		accessoryType.setName("WRAPPER");
		offerType.getAccessory().add(accessoryType);
		
		BasketGroupedType basketGroupedType  = new BasketGroupedType();
		basketGroupedType.setName("DEBIT");
		basketGroupedType.setValue(request.getCredit()!=null? request.getCredit():BigDecimal.ZERO);

		accessoryType.getBasket().add(basketGroupedType);
		
		ResourceType resourceType = new ResourceType();
		resourceType.setName("RSRIF");
		resourceType.setState(request.isRifService() ? "1" :"2");
		accessoryType.getResource().add(resourceType);
		
		saleOperation.setPCNSaleOp(pCNSaleOp);
		
		payload.getOperation().setSaleOperation(saleOperation);
		;
		return wsRequest;
	}

	///// fine SaleOperationX
	
	

}
