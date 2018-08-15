package org.e.payment.webservice.client.config.cxf;

import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_CLASS;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ConfigurationConstants;
import org.e.payment.webservice.client.vo.bankTran.BankTranService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * v1版本，证书是测试环境
 * @author xieqiang
 *
 */
@Configuration
public class CxfSignatureConf {
	private static final Logger log = LoggerFactory.getLogger(CxfSignatureConf.class.getName());
	@Value("${service.url}")
	private String serviceUrl;
	@Value("${sig.prop.file}")
	private String SIG_PROP_FILE;
	@Value("${signature.user}")
	private String SIGNATURE_USER;
	

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
//		endpoint.getInInterceptors().add(loggingInInterceptor());
		endpoint.getInInterceptors().add(wss4jIn());
//		endpoint.getOutInterceptors().add(loggingOutInterceptor());
		endpoint.getOutInterceptors().add(wss4jOut());
	}

	public WSS4JOutInterceptor wss4jOut() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ConfigurationConstants.ACTION,
				ConfigurationConstants.SIGNATURE + " " + ConfigurationConstants.TIMESTAMP);
//		properties.put(ConfigurationConstants.SIG_PROP_FILE, "client_signing.properties");
		properties.put(ConfigurationConstants.SIG_PROP_FILE, SIG_PROP_FILE);
		properties.put(ConfigurationConstants.SIGNATURE_USER, SIGNATURE_USER);
		properties.put(ConfigurationConstants.SIG_KEY_ID, "DirectReference");
		properties.put(ConfigurationConstants.SIGNATURE_PARTS,
				"{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
		properties.put(ConfigurationConstants.SIG_ALGO, "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
		properties.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
		WSS4JOutInterceptor interceptor = new WSS4JOutInterceptor(properties);
		return interceptor;
	}

	public WSS4JInInterceptor wss4jIn() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ConfigurationConstants.ACTION,
				ConfigurationConstants.SIGNATURE + " " + ConfigurationConstants.TIMESTAMP);
//		properties.put(ConfigurationConstants.SIG_PROP_FILE, "client_signing.properties");
		properties.put(ConfigurationConstants.SIG_PROP_FILE, SIG_PROP_FILE);
		properties.put(ConfigurationConstants.SIGNATURE_USER, "epayment-webservice-servercert");
		properties.put(ConfigurationConstants.SIG_KEY_ID, "DirectReference");
		properties.put(ConfigurationConstants.SIGNATURE_PARTS,
				"{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
		properties.put(ConfigurationConstants.SIG_ALGO, "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
		properties.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
		WSS4JInInterceptor interceptor = new WSS4JInInterceptor(properties);
		return interceptor;
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
