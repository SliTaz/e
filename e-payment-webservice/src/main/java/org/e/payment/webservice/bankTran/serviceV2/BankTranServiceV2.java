package org.e.payment.webservice.bankTran.serviceV2;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.cxf.annotations.WSDLDocumentation;
import org.e.payment.webservice.bankTran.serviceV2.vo.RechargeRequestV2;
import org.e.payment.webservice.bankTran.serviceV2.vo.RechargeResponseV2;

@WebService(name = "BankTranServiceV2", targetNamespace = "http://201.249.156.12/webservice")
@WSDLDocumentation(placement = WSDLDocumentation.Placement.TOP, value = "BankTranServiceV2")
public interface BankTranServiceV2 {
	@WebMethod
	RechargeResponseV2 Recharge(@WebParam(name = "RechargeRequest") RechargeRequestV2 rechargeRequest);
}
