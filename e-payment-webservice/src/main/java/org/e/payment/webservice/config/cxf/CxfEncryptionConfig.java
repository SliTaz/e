package org.e.payment.webservice.config.cxf;

import static org.apache.wss4j.common.ConfigurationConstants.ACTION;
import static org.apache.wss4j.common.ConfigurationConstants.DEC_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.ENCRYPTION_USER;
import static org.apache.wss4j.common.ConfigurationConstants.ENC_PROP_FILE;
import static org.apache.wss4j.common.ConfigurationConstants.MUST_UNDERSTAND;
import static org.apache.wss4j.common.ConfigurationConstants.PW_CALLBACK_CLASS;

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
import org.e.payment.webservice.bankTran.service.impl.BankTranServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.google.common.collect.Maps;

public class CxfEncryptionConfig {

	@Value("${cxf.contextPath}")
	private String contextPath;
	@Value("${cxf.bankTran.endpointUrl}")
	private String bankTranEndpointUrl;

	
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
        endpoint.getInInterceptors().add(wss4JInInterceptor());
        endpoint.getInInterceptors().add(loggingInInterceptor());
        endpoint.getOutInterceptors().add(loggingOutInterceptor());
        endpoint.getOutInterceptors().add(wss4JOutInterceptor());
		return endpoint;
	}

    private WSS4JInInterceptor wss4JInInterceptor() {
        Map<String, Object> securityProperties = Maps.newHashMap();
        securityProperties.put(ACTION, "Encrypt");
        securityProperties.put(ENCRYPTION_USER, "epayment-webservice-serverkey");
        securityProperties.put(DEC_PROP_FILE, "server_encrypt.properties");
        securityProperties.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
        return new WSS4JInInterceptor(securityProperties);
    }

    private WSS4JOutInterceptor wss4JOutInterceptor() {
        Map<String, Object> securityProperties = Maps.newHashMap();
        securityProperties.put(ACTION, "Encrypt");
        securityProperties.put(ENC_PROP_FILE, "server_encrypt.properties");
        securityProperties.put(ENCRYPTION_USER, "epayment-webservice-clientcert");
        securityProperties.put(MUST_UNDERSTAND, "true");
        securityProperties.put(PW_CALLBACK_CLASS, CertificatePasswordHandler.class.getName());
        return new WSS4JOutInterceptor(securityProperties);
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
