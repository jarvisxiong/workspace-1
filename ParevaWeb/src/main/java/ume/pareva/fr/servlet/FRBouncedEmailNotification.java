package ume.pareva.fr.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.dao.SESNotificationDao;
import ume.pareva.fr.util.FRUtil;
import ume.pareva.pojo.SESNotification;
import ume.pareva.pojo.SESNotification.Recipient;
import ume.pareva.pojo.SNSBouncedNotification;
import ume.pareva.pojo.SNSBouncedRecipients;
import ume.pareva.pojo.SNSDeliveryNotification;
import ume.pareva.pojo.SNSSubscriptionConfirmation;
import ume.pareva.pojo.SNSSubscriptionConfirmationResponse;
import ume.pareva.sdk.Misc;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/FRBouncedEmailNotification")
public class FRBouncedEmailNotification extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger( FRBouncedEmailNotification.class.getName());

	@Autowired
	FRUtil frUtil;
	
	@Autowired
	SESNotificationDao sesNotificationDao;
	
	public FRBouncedEmailNotification() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String messageType=request.getHeader("x-amz-sns-message-type");
		logger.info("Amazon Message Type as read by Header (x-amz-sns-message-type): "+messageType);
		String topicArn=request.getHeader("x-amz-sns-topic-arn");
		if(messageType.equals("SubscriptionConfirmation")){
			String requestBody=readRequestBody(request);
			SNSSubscriptionConfirmation snsSubscriptionConfirmation = mapJsonToPojo(requestBody,SNSSubscriptionConfirmation.class);
			if(snsSubscriptionConfirmation!=null){
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
				HttpEntity<SNSSubscriptionConfirmationResponse> entity = new HttpEntity<SNSSubscriptionConfirmationResponse>(headers);
				ResponseEntity<SNSSubscriptionConfirmationResponse> snsSubscriptionConfirmationResponse=frUtil.getRestTemplate().exchange(snsSubscriptionConfirmation.getSubscriptionUrl(),HttpMethod.GET,entity,SNSSubscriptionConfirmationResponse.class);
				logger.info("SubscriptionArn: "+snsSubscriptionConfirmationResponse.getBody().getSubscriptionConfirmResult().getSubscriptionArn());
			}

		}else if(messageType.equals("Notification") && topicArn.contains("SESbounces")){
			String requestBody=readRequestBody(request);
			requestBody=requestBody.replace("\"{", "{");
			requestBody=requestBody.replace("}\"", "}");
			requestBody=requestBody.replace("\\", "");			
			SNSBouncedNotification snsBouncedNotification= mapJsonToPojo(requestBody,SNSBouncedNotification.class);
			String sourceEmail=snsBouncedNotification.getMessage().getMail().getSource();
			SESNotification sesNotification=new SESNotification();
			sesNotification.setaUnique(Misc.generateUniqueId());
			sesNotification.setaType(snsBouncedNotification.getMessage().getNotificationType());
			sesNotification.setaSourceEmail(snsBouncedNotification.getMessage().getMail().getSource());
			sesNotification.setaDomainName(sourceEmail.substring(sourceEmail.indexOf('@')+1));
			sesNotification.setaCreated(new Date());
			List<Recipient> recipients=new ArrayList<Recipient>();
			for(SNSBouncedRecipients snsBouncedRecipient:snsBouncedNotification.getMessage().getBounce().getBouncedRecipients()){
				SESNotification.Recipient recipient=sesNotification.new Recipient();
				recipient.setaAction(snsBouncedRecipient.getAction());
				recipient.setaDestinationEmail(snsBouncedRecipient.getEmailAddress());
				recipient.setaDiagnosticCode(snsBouncedRecipient.getDiagnosticCode());
				recipient.setaStatus(snsBouncedRecipient.getStatus());
				recipients.add(recipient);
			}
			sesNotification.setaRecipients(recipients);
			sesNotificationDao.saveSESNotification(sesNotification);
			System.out.println(snsBouncedNotification.toString());
			
		}else if(messageType.equals("Notification") && topicArn.contains("SESdelivrep")){
			String requestBody=readRequestBody(request);
			requestBody=requestBody.replace("\"{", "{");
			requestBody=requestBody.replace("}\"", "}");
			requestBody=requestBody.replace("\\", "");	
			SNSDeliveryNotification snsDeliveryNotification= mapJsonToPojo(requestBody,SNSDeliveryNotification.class);
			String sourceEmail=snsDeliveryNotification.getMessage().getMail().getSource();
			SESNotification sesNotification=new SESNotification();
			sesNotification.setaUnique(Misc.generateUniqueId());
			sesNotification.setaType(snsDeliveryNotification.getMessage().getNotificationType());
			sesNotification.setaSourceEmail(snsDeliveryNotification.getMessage().getMail().getSource());
			sesNotification.setaDomainName(sourceEmail.substring(sourceEmail.indexOf('@')+1));
			sesNotification.setaCreated(new Date());
			List<Recipient> recipients=new ArrayList<Recipient>();
			for(String destinationEmail:snsDeliveryNotification.getMessage().getDelivery().getRecipients()){
				SESNotification.Recipient recipient=sesNotification.new Recipient();
				recipient.setaAction("");
				recipient.setaDestinationEmail(destinationEmail);
				recipient.setaDiagnosticCode("");
				recipient.setaStatus("");
				recipients.add(recipient);
			}
			sesNotification.setaRecipients(recipients);
			sesNotificationDao.saveSESNotification(sesNotification);
			System.out.println(snsDeliveryNotification.toString());
		}

	}

	public String readRequestBody(HttpServletRequest request){
		StringBuffer requestBody = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				requestBody.append(line);
		} catch (Exception e){ 
			logger.error("Error Reading Request Body");
			e.printStackTrace();
		}
		logger.info("AMAZON Json String: "+requestBody.toString());
		return requestBody.toString();
	}
	
	public <T> T mapJsonToPojo(String requestBody,Class<T> type){
		ObjectMapper mapper = new ObjectMapper();
		T mappedClass=null;
		try {
			mappedClass =  mapper.readValue(requestBody,type);
		} catch (IOException e) {
			logger.error("Error Mapping Json String to POJO");
			e.printStackTrace();
		}
		return mappedClass;
	}
	
	
}
