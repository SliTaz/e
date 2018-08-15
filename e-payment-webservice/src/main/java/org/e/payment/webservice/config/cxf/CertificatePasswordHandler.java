package org.e.payment.webservice.config.cxf;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.e.payment.webservice.common.SpringBeanUtil;
import org.springframework.core.env.Environment;

/**
 * Created by Mateusz Dalgiewicz on 15.04.2017.
 */
public class CertificatePasswordHandler implements CallbackHandler {
	
	private Environment env = SpringBeanUtil.getBean(Environment.class);

	private String bankTranServerPassord = env.getProperty("cxf.bankTran.serverPassord");
	
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        if (pc.getIdentifier().equals("epayment-webservice-serverkey")) {
            pc.setPassword(bankTranServerPassord);

			// if (pc.getIdentifier().equals("epayment-webservice-server")) {
			// pc.setPassword("webservice1116Zben");
        }
    }
}
