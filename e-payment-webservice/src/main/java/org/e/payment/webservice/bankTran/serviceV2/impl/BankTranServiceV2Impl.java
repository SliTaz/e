package org.e.payment.webservice.bankTran.serviceV2.impl;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.e.payment.webservice.bankTran.apivo.BankTranWebServiceRechargeRequest;
import org.e.payment.webservice.bankTran.apivo.BankTranWebServiceRechargeResponse;
import org.e.payment.webservice.bankTran.serviceV2.BankTranServiceV2;
import org.e.payment.webservice.bankTran.serviceV2.BankTranServiceV2Abs;
import org.e.payment.webservice.bankTran.serviceV2.vo.BankRechargeRespBodyV2;
import org.e.payment.webservice.bankTran.serviceV2.vo.RechargeRequestV2;
import org.e.payment.webservice.bankTran.serviceV2.vo.RechargeResponseV2;
import org.e.payment.webservice.common.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.common.cxf.SOAPResultCode;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;

@WebService(targetNamespace = "http://201.249.156.12/webservice", endpointInterface = "org.e.payment.webservice.bankTran.serviceV2.BankTranServiceV2")
// @BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class BankTranServiceV2Impl extends BankTranServiceV2Abs implements BankTranServiceV2 {
	private static final Logger log = LoggerFactory.getLogger(BankTranServiceV2Impl.class);

	@Resource
	WebServiceContext wsContext;

	private Environment env = SpringBeanUtil.getBean(Environment.class);

	private String rechargeApi = env.getProperty("httpClient.bankTran.rechargeApi");// "http://localhost:8080/webservice/bankTran/recharge";

	private String userName = env.getProperty("httpClient.bankTran.userName");// "4rfv^YHN";
	private String password = env.getProperty("httpClient.bankTran.password");// "*IK<>LO()P:?";

	private CloseableHttpClient httpClient = SpringBeanUtil.getBean(CloseableHttpClient.class);
	private RequestConfig config = SpringBeanUtil.getBean(RequestConfig.class);

	@Override
	public RechargeResponseV2 Recharge(RechargeRequestV2 rechargeRequest) {
		log.info(String.format("Recharge recv %s", JSONObject.toJSON(rechargeRequest)));
		long s = System.currentTimeMillis();
		String ipAddress = getIpAddress(wsContext);

		RechargeResponseV2 rechargeResponse = new RechargeResponseV2();
		BankRechargeRespBodyV2 bankRechargeRespBody = new BankRechargeRespBodyV2();
		rechargeResponse.setBankRechargeRespBody(bankRechargeRespBody);
		rechargeResponse.setInterfaceVersion(rechargeRequest.getInterfaceVersion());
		rechargeResponse.setRefNo(rechargeRequest.getRefNo());

		SOAPResultCode result = validateRechargeRequest(rechargeRequest);
		if (result != SOAPResultCode.OK) {
			bankRechargeRespBody.setResultCode(result.getCode());
			rechargeResponse.setSigMsg(getSigMsg(bankRechargeRespBody));
			log.info(String.format("usetime=%s,Recharge validateRechargeRequest resultCode=%s,resultMsg=%s", (System.currentTimeMillis() - s), result.getCode(), result.getReason()));
			return rechargeResponse;
		}

		// 请求参数
		BankTranWebServiceRechargeRequest bankTranWebServiceRechargeRequest = new BankTranWebServiceRechargeRequest();
		bankTranWebServiceRechargeRequest.setUserName(userName);
		bankTranWebServiceRechargeRequest.setPassword(password);

		bankTranWebServiceRechargeRequest.setInterfaceVersion(rechargeRequest.getInterfaceVersion());
		bankTranWebServiceRechargeRequest.setIpAddress(ipAddress);
		bankTranWebServiceRechargeRequest.setBankId(rechargeRequest.getBankId());
		bankTranWebServiceRechargeRequest.setRefNo(rechargeRequest.getRefNo());
		Double amount = DoubleUtil.divide(Double.valueOf(rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo().getAmount()), 100d);
		bankTranWebServiceRechargeRequest.setAmount(amount);
		bankTranWebServiceRechargeRequest.setVid(rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo().getVid());
		bankTranWebServiceRechargeRequest.setPaymentTime(DateUtil.convertStringToDate(rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo().getPaymentTime(), DateUtil.DATE_FORMAT_SEVENTEEN));
		bankTranWebServiceRechargeRequest.setPatrimonyCardCode(rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo().getPatrimonyCardCode());

		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(bankTranWebServiceRechargeRequest);

		//////////////// httpclient start/////////////////
		long hs = System.currentTimeMillis();
		HttpPost httppost = new HttpPost(rechargeApi);
		httppost.setConfig(config);

		try {
			// log.info(String.format("Recharge send httpclient %s,%s", rechargeApi, jsonParam.toString()));
			StringEntity stringEntity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");
			httppost.setEntity(stringEntity);
			CloseableHttpResponse response = httpClient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
					log.info(String.format("Recharge httpclient get response from httpClient %s", responseStr));
					if (responseStr != null && responseStr.contains("body")) {
						JSONObject jsonObjectResponse = new JSONObject();
						jsonObjectResponse = (JSONObject) jsonObjectResponse.parse(responseStr);
						BankTranWebServiceRechargeResponse bankTranWebServiceRechargeResponse = JSONObject.toJavaObject(jsonObjectResponse.getJSONObject("body"), BankTranWebServiceRechargeResponse.class);
						if (bankTranWebServiceRechargeResponse != null) {
							bankRechargeRespBody.setResultCode(bankTranWebServiceRechargeResponse.getResultCode().getCode());
							rechargeResponse.setSigMsg(getSigMsg(bankRechargeRespBody));
							log.info(String.format("usetime=%s,httpclientusetime=%s,Recharge httpclient get response  %s", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs),
									JSONObject.toJSON(bankTranWebServiceRechargeResponse)));
							return rechargeResponse;
						} else {
							bankRechargeRespBody.setResultCode(SOAPResultCode.SERVICE_RESPONSE_IS_EMPTY.getCode());
							rechargeResponse.setSigMsg(getSigMsg(bankRechargeRespBody));
							log.info(String.format("usetime=%s,httpclientusetime=%s,Recharge httpclient get response is null", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)));
							return rechargeResponse;
						}
					} else {
						bankRechargeRespBody.setResultCode(SOAPResultCode.SERVICE_RESPONSE_ERROR.getCode());
						rechargeResponse.setSigMsg(getSigMsg(bankRechargeRespBody));
						log.info(String.format("usetime=%s,httpclientusetime=%s,Recharge httpclient get response error", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)));
						return rechargeResponse;
					}
				} else {
					bankRechargeRespBody.setResultCode(SOAPResultCode.SERVICE_RESPONSE_ERROR.getCode());
					rechargeResponse.setSigMsg(getSigMsg(bankRechargeRespBody));
					log.info(String.format("usetime=%s,httpclientusetime=%s,Recharge httpclient get response error2", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)));
					return rechargeResponse;
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			bankRechargeRespBody.setResultCode(SOAPResultCode.SERVICE_CAN_NOT_CONNECT.getCode());
			rechargeResponse.setSigMsg(getSigMsg(bankRechargeRespBody));
			log.info(String.format("usetime=%s,httpclientusetime=%s,Recharge httpclient not connect", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)));
			log.error(String.format("usetime=%s,httpclientusetime=%s,Recharge httpclient not connect", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)), e);
			return rechargeResponse;
		} finally {
			// 关闭连接,释放资源
			// try {
			// httpClient.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}

		//////////////// httpclient end/////////////////
	}

	private SOAPResultCode validateRechargeRequest(RechargeRequestV2 rechargeRequest) {
		if (rechargeRequest == null) {
			return SOAPResultCode.REQUEST_IS_NULL;
		}
		SOAPResultCode soapResultCode = SOAPResultCode.OK;
		soapResultCode = validateInterfaceVersion(rechargeRequest.getInterfaceVersion());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}
		soapResultCode = validateBankId(rechargeRequest.getBankId());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}
		soapResultCode = validateInterfaceRefNo(rechargeRequest.getRefNo());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		if (rechargeRequest.getBankRechargeReqBody() == null) {
			return SOAPResultCode.BANK_RECHARGE_REQ_BODY_IS_NULL;
		}

		if (rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo() == null) {
			return SOAPResultCode.BANK_PAYMENT_INFO_IS_NULL;
		}

		soapResultCode = validateDateTime(rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo().getPaymentTime(), DateUtil.DATE_FORMAT_SEVENTEEN);
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}
		soapResultCode = validateAmount(rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo().getAmount());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		soapResultCode = validateVID(rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo().getVid());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}
		soapResultCode = validatePatrimonyCardCode(rechargeRequest.getBankRechargeReqBody().getBankPaymentInfo().getPatrimonyCardCode());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		soapResultCode = validateSigMsg(rechargeRequest.getBankRechargeReqBody(), rechargeRequest.getSigMsg());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		return soapResultCode;
	}

}
