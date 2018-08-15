package org.e.payment.webservice.config.cxf;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.e.payment.webservice.bankTran.service.impl.BankTranServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

public class CxfConfig {

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
//		endpoint.getInInterceptors().add(loggingInInterceptor());
//		endpoint.getOutInterceptors().add(loggingOutInterceptor());
		return endpoint;
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
