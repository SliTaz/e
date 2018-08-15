package org.e.payment.webservice.client.config.cxf;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Created by Mateusz Dalgiewicz on 15.04.2017.
 */
public class CertificatePasswordHandlerPro implements CallbackHandler {
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        if (pc.getIdentifier().equals("epayment-webservice-clientkey")) {
            pc.setPassword("webservice911epay");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-treasure")) {
            pc.setPassword("webservicetreasure119");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-bicentennial")) {
            pc.setPassword("webservicebicentennial119");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-banfanb")) {
            pc.setPassword("webservicebanfanb119");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-agricultural")) {
            pc.setPassword("webserviceagricultural119");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-venezuela")) {
            pc.setPassword("webservicevenezuela119");
        }
    }
}
