package org.e.payment.webservice.client.config.cxf;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.e.payment.webservice.client.vo.bankTran.BankTranService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class CxfConf {
	@Value("${service.url}")
	private String serviceUrl;

	@Bean
	public BankTranService efacturaConsultasClient() {
		JaxWsProxyFactoryBean jaxWsProxyFactory = new JaxWsProxyFactoryBean();
		jaxWsProxyFactory.setServiceClass(BankTranService.class);
		jaxWsProxyFactory.setAddress(serviceUrl);
		BankTranService bankTranService = (BankTranService) jaxWsProxyFactory.create();
		Client client = ClientProxy.getClient(bankTranService);
		configureEndpoint(client.getEndpoint());
		return bankTranService;
	}

	private void configureEndpoint(Endpoint endpoint) {
//		endpoint.getInInterceptors().add(loggingInInterceptor());
//		endpoint.getOutInterceptors().add(loggingOutInterceptor());
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
