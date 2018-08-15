package org.e.payment.webservice.client.config.cxf;

import static org.apache.wss4j.common.ConfigurationConstants.ACTION;
import static org.apache.wss4j.common.ConfigurationConstants.DEC_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.ENCRYPTION_USER;
import static org.apache.wss4j.common.ConfigurationConstants.ENC_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.MUST_UNDERSTAND;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_CLASS;

import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.e.payment.webservice.client.vo.bankTran.BankTranService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Maps;

public class CxfEncryptionConf {
	private static final Logger log = LoggerFactory.getLogger(CxfEncryptionConf.class.getName());
	@Value("${service.url}")
	private String serviceUrl;

	@Bean
	public BankTranService efacturaConsultasClient() {
		JaxWsProxyFactoryBean jaxWsProxyFactory = new JaxWsProxyFactoryBean();
		jaxWsProxyFactory.setServiceClass(BankTranService.class);
		jaxWsProxyFactory.setAddress(serviceUrl);
		log.info("Consumiendo servicio de " + serviceUrl);
		BankTranService bankTranService = (BankTranService) jaxWsProxyFactory.create();
		Client client = ClientProxy.getClient(bankTranService);
		configureEndpoint(client.getEndpoint());
		return bankTranService;
	}

	private void configureEndpoint(Endpoint endpoint) {
		endpoint.getInInterceptors().add(loggingInInterceptor());
		endpoint.getInInterceptors().add(wss4JInInterceptor());
		endpoint.getOutInterceptors().add(loggingOutInterceptor());
		endpoint.getOutInterceptors().add(wss4JOutInterceptor());
	}

	private WSS4JOutInterceptor wss4JOutInterceptor() {
		Map<String, Object> securityProperties = Maps.newHashMap();
		securityProperties.put(ACTION, "Encrypt");
		securityProperties.put(ENC_PROP_FILE, "client_encrypt.properties");
		securityProperties.put(ENCRYPTION_USER, "epayment-webservice-servercert");
		securityProperties.put(MUST_UNDERSTAND, "true");
		securityProperties.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
		return new WSS4JOutInterceptor(securityProperties);
	}

	private WSS4JInInterceptor wss4JInInterceptor() {
		Map<String, Object> securityProperties = Maps.newHashMap();
		securityProperties.put(ACTION, "Encrypt");
		securityProperties.put(ENCRYPTION_USER, "epayment-webservice-clientkey");
		securityProperties.put(DEC_PROP_FILE, "client_encrypt.properties");
		securityProperties.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
		return new WSS4JInInterceptor(securityProperties);
	}

	private LoggingInInterceptor loggingInInterceptor() {
		LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor();
		loggingInInterceptor.setPrettyLogging(true);
		return loggingInInterceptor;
	}

	private LoggingOutInterceptor loggingOutInterceptor() {
		LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor();
		loggingOutInterceptor.setPrettyLogging(true);
		return loggingOutInterceptor;
	}

}
