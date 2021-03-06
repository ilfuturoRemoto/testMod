package it.telecomitalia.trcs.middleware.kafka.inbound.command.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.telecomitalia.soa.soap.soapheader.HeaderType;
import it.telecomitalia.soa.trcs.gateway.SetSubscriberStatusXIbData;
import it.telecomitalia.soa.trcs.gateway.SetSubscriberStatusXIbData.Transaction;
import it.telecomitalia.soa.trcs.gateway.SetSubscriberStatusXRequest;
import it.telecomitalia.soa.trcs.gateway.SetSubscriberStatusXResponse;
import it.telecomitalia.trcs.middleware.kafka.inbound.builder.HeaderTypeBuilder;
import it.telecomitalia.trcs.middleware.kafka.inbound.command.ExecutorSynchronousFailed;
import it.telecomitalia.trcs.middleware.kafka.inbound.command.TrcsInboundExecutorException;
import it.telecomitalia.trcs.middleware.kafka.inbound.config.ResponseTargets;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.BlockUnblockRequestBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.BlockUnblockResponseBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.TrcsKafkaEventType;
import it.telecomitalia.trcs.middleware.kafka.inbound.dto.TrcsKafkaHeader;
import it.telecomitalia.trcs.middleware.kafka.inbound.logging.HydraLogBean;
import it.telecomitalia.trcs.middleware.kafka.inbound.logging.HydraLogThreadLocal;
import it.telecomitalia.trcs.middleware.ws.client.OpscProvisioningClient;

public class BlockUnblockExecutor extends AbstractExecutor {

	private final Logger logger = LoggerFactory.getLogger(BlockUnblockExecutor.class);


	public BlockUnblockExecutor(OpscProvisioningClient client, ResponseTargets responseTargets) {
		super(client, responseTargets);
	}

	
	@Override
	public void execute(Map<String, Object> headers, String payload) throws ExecutorSynchronousFailed{
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			logger.debug("Request [{}]", payload);
			
			// Converte il JSON in POJO
			BlockUnblockRequestBean request = objectMapper.readValue(payload, BlockUnblockRequestBean.class);
			
			HydraLogThreadLocal.getLogBean().getEvent().setPayload(request);
			// Effettua il mapping con l'header SOAP
			HeaderType headerType = new HeaderTypeBuilder(headers).build();
			
			// Effettua il mapping con il body SOAP
			SetSubscriberStatusXRequest wsRequest = this.createWebServiceRequest(request, headers, headerType);

			
			// Invoca il servizio di cambio numero di GW
			SetSubscriberStatusXResponse response = this.getOpscClient().setSubscriberStatusX(headerType, wsRequest);

			logger.info("SetSubscriberStatusX result=[{}]", response.getIbRetCode());
			
			if ("1".equals(response.getIbRetCode())) {
				//TODO: Scrivere Log di Success
				HydraLogThreadLocal.getLogBean().setResult(HydraLogBean.Result.success);

			} else {
				//TODO: Gestire Errore di invocazione inviando risposta KO su Kafka
				//TODO: Inserire Logging
				BlockUnblockResponseBean responsePayload = this.createResponsePayload(headers, request, response);
				
				throw new ExecutorSynchronousFailed(
						this.getReponseTargets().getResponseTarget(TrcsKafkaEventType.blockUnblockResponse),
						TrcsKafkaHeader.createResponseKafkaHeader(headers, TrcsKafkaEventType.blockUnblockResponse),
						responsePayload,
						objectMapper.writeValueAsString(responsePayload),
						request.getPhoneNumber()
					);
			}
			
		} catch (ExecutorSynchronousFailed e) {
			throw e;
		} catch (Exception e) {
			logger.error("SetSubscriberStatusX calling error.", e);
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw new TrcsInboundExecutorException(e);
		}

	}
	
    private BlockUnblockResponseBean createResponsePayload(Map<String, Object> headers, BlockUnblockRequestBean request,  SetSubscriberStatusXResponse response) {
		BlockUnblockResponseBean result = new BlockUnblockResponseBean(headers.get(TrcsKafkaHeader.sourceSystem.name()).toString(),
				                                                       request.getPhoneNumber(),
				                                                       KafkaErrorCodes.decodeFromOpsc(response.getIbRetCode()),
				                                                       KafkaErrorCodes.messageFromOpsc(response.getIbRetCode()),
				                                                       request.getReason(),request.getOldReason());
		result.setSubsystemErrorCode(response.getIbRetCode());
		return result;
	}


	private SetSubscriberStatusXRequest createWebServiceRequest(BlockUnblockRequestBean request, Map<String, Object> headers,
			HeaderType headerType) {

		SetSubscriberStatusXRequest wsRequest = new SetSubscriberStatusXRequest();
		
		wsRequest.setIbRetCode("1");
		wsRequest.setIbAppDep1("0");
		wsRequest.setIbAppDep2("0");
		wsRequest.setIbIdSrvc("CARDMGMT");
		wsRequest.setIbData(new SetSubscriberStatusXRequest.IbData());
		wsRequest.getIbData().setIbLenData(0);
		
		SetSubscriberStatusXIbData payload = new SetSubscriberStatusXIbData();
		wsRequest.getIbData().setRequest(payload);
		
		payload.setRequestType("TwoStep");
		
		payload.setTransaction(new Transaction());
		payload.getTransaction().setTID(headerType.getTransactionID());
		payload.getTransaction().setSubsystem(String.valueOf(headers.get(TrcsKafkaHeader.channel.name())));
		payload.getTransaction().setService(wsRequest.getIbIdSrvc());
		payload.getTransaction().setIDSystem(headerType.getSourceSystem());
		
		
		payload.setClientKeys(new SetSubscriberStatusXIbData.ClientKeys());
		payload.getClientKeys().setMSISDN(request.getPhoneNumber());
		
		payload.setOperation(new SetSubscriberStatusXIbData.Operation());
		payload.getOperation().setOperationType("ASTMgr");
		payload.getOperation().setInfo(request.getInfo());
		
		
		
		SetSubscriberStatusXIbData.Operation.ASTMgr aSTMgr = new SetSubscriberStatusXIbData.Operation.ASTMgr();
		SetSubscriberStatusXIbData.Operation.ASTMgr.Client client = new SetSubscriberStatusXIbData.Operation.ASTMgr.Client();
		client.setReason(request.getReason());
		aSTMgr.setClient(client);
		aSTMgr.setOperationType(BlockUnblockExecutor.calcolateValue(request.getReason(),request.getOldReason()));
		payload.getOperation().setASTMgr(aSTMgr);
		
		return wsRequest;
	}
	
	private static String calcolateValue(String reason,String oldReason) {
		
		/*- Se reason ?? uguale a "A" e oldReason ?? uguale a "Q", inserire "UnBlockingPayDelay".
		- Se reason ?? uguale a "A" e oldReason ?? diversa da "Q" o non presente, inserire "UnBlocking".
		- Se reason ?? diversa da "A" e oldReason ?? uguale a "Q", inserire "BlockingPayDelay".
		- Se reason ?? diversa da "A" e oldReason ?? diversa da "Q" o non ?? presente, inserire "Blocking".*/
		if(reason.equals("A") && "Q".equals(oldReason))
			return "UnBlockingPayDelay";
		else if(reason.equals("A") && !"Q".equals(oldReason))
			return "UnBlocking";
		else if(!reason.equals("A") && "Q".equals(oldReason))
			return "BlockingPayDelay";
		else if(!reason.equals("A") && !"Q".equals(oldReason))
			return "Blocking";
		else
			return "";
		
	}

}
