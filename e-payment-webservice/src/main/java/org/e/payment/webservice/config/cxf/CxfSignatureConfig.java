package org.e.payment.webservice.config.cxf;

import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_CLASS;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ConfigurationConstants;
import org.e.payment.webservice.bankTran.service.impl.BankTranServiceImpl;
import org.e.payment.webservice.bankTran.serviceV2.impl.BankTranServiceV2Impl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfSignatureConfig {

	@Value("${cxf.contextPath}")
	private String contextPath;
	@Value("${cxf.bankTran.endpointUrl}")
	private String bankTranEndpointUrl;
	@Value("${cxf.bankTran.endpointUrl.V2}")
	private String bankTranEndpointUrlV2;

	@Bean
	public ServletRegistrationBean dispatcherServlet() {
		return new ServletRegistrationBean(new CXFServlet(), contextPath);
	}

	@Bean(name = Bus.DEFAULT_BUS_ID)
	public SpringBus springBus() {
		return new SpringBus();
	}

	@Bean
	public Endpoint endpoint() {
		EndpointImpl endpoint = new EndpointImpl(springBus(), new BankTranServiceImpl());
		endpoint.publish(bankTranEndpointUrl);
		endpoint.getInInterceptors().add(wss4jIn());
		// endpoint.getInInterceptors().add(loggingInInterceptor());
		// endpoint.getOutInterceptors().add(loggingOutInterceptor());
		endpoint.getOutInterceptors().add(wss4jOut());
		return endpoint;
	}

	@Bean
	public Endpoint endpointV2() {
		EndpointImpl endpoint = new EndpointImpl(springBus(), new BankTranServiceV2Impl());
		endpoint.publish(bankTranEndpointUrlV2);
		endpoint.getInInterceptors().add(wss4jIn());
		// endpoint.getInInterceptors().add(loggingInInterceptor());
		// endpoint.getOutInterceptors().add(loggingOutInterceptor());
		endpoint.getOutInterceptors().add(wss4jOut());
		return endpoint;
	}

	public WSS4JOutInterceptor wss4jOut() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ConfigurationConstants.ACTION, ConfigurationConstants.SIGNATURE + " " + ConfigurationConstants.TIMESTAMP);
		properties.put(ConfigurationConstants.SIG_PROP_FILE, "server_signing.properties");
		properties.put(ConfigurationConstants.SIGNATURE_USER, "epayment-webservice-serverkey");
		properties.put(ConfigurationConstants.SIG_KEY_ID, "DirectReference");
		properties.put(ConfigurationConstants.SIGNATURE_PARTS, "{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
		properties.put(ConfigurationConstants.SIG_ALGO, "http://www.w3.org/2000/09/xmldsig#rsa-sha1");
		properties.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
		WSS4JOutInterceptor interceptor = new WSS4JOutInterceptor(properties);
		return interceptor;
	}

	public WSS4JInInterceptor wss4jIn() {
		Map<String, Object> properties = new HashMap<>();
		properties.put(ConfigurationConstants.ACTION, ConfigurationConstants.SIGNATURE + " " + ConfigurationConstants.TIMESTAMP);
		properties.put(ConfigurationConstants.SIG_PROP_FILE, "server_signing.properties");
		properties.put(ConfigurationConstants.SIGNATURE_USER, "epayment-webservice-clientcert");
		properties.put(ConfigurationConstants.SIG_KEY_ID, "DirectReference");
		properties.put(ConfigurationConstants.SIGNATURE_PARTS, "{Element}{http://schemas.xmlsoap.org/soap/envelope/}Body");
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
