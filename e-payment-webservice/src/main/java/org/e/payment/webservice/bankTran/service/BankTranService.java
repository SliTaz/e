package org.e.payment.webservice.bankTran.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.cxf.annotations.WSDLDocumentation;
import org.e.payment.webservice.bankTran.service.vo.RechargeRequest;
import org.e.payment.webservice.bankTran.service.vo.RechargeResponse;
import org.e.payment.webservice.bankTran.service.vo.ReverseRequest;
import org.e.payment.webservice.bankTran.service.vo.ReverseResponse;

@WebService(name = "BankTranService", targetNamespace = "http://201.249.156.12/webservice")
@WSDLDocumentation(placement = WSDLDocumentation.Placement.TOP, value = "BankTranService")
public interface BankTranService {
	@WebMethod
	RechargeResponse Recharge(@WebParam(name = "RechargeRequest") RechargeRequest rechargeRequest);
	@WebMethod
	ReverseResponse Reverse(@WebParam(name = "ReverseRequest") ReverseRequest reverseRequest);
}
