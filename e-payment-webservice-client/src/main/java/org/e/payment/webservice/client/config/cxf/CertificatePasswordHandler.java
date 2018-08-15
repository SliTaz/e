package org.e.payment.webservice.client.config.cxf;

import org.apache.wss4j.common.ext.WSPasswordCallback;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import java.io.IOException;

/**
 * Created by Mateusz Dalgiewicz on 15.04.2017.
 */
public class CertificatePasswordHandler implements CallbackHandler {
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
        if (pc.getIdentifier().equals("epayment-webservice-clientkey")) {
            pc.setPassword("webservice1116Zben");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-treasure")) {
            pc.setPassword("webservicetreasure");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-bicentennial")) {
            pc.setPassword("webservicebicentennial");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-banfanb")) {
            pc.setPassword("webservicebanfanb");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-agricultural")) {
            pc.setPassword("webserviceagricultural");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-venezuela")) {
            pc.setPassword("webservicevenezuela");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-100bank")) {
            pc.setPassword("webservice100bank");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-BanescoBank")) {
            pc.setPassword("webserviceBanescoBank");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-ExteriorBank")) {
            pc.setPassword("webserviceExteriorBank");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-MercantilBank")) {
            pc.setPassword("webserviceMercantilBank");
        }else if (pc.getIdentifier().equals("epayment-webservice-clientkey-BanCrecer")) {
            pc.setPassword("webserviceBanCrecer");
        }
    }
}
