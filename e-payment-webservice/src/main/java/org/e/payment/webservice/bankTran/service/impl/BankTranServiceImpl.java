package org.e.payment.webservice.bankTran.service.impl;

import java.io.IOException;

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
import org.e.payment.webservice.bankTran.apivo.BankTranWebServiceReverseRequest;
import org.e.payment.webservice.bankTran.apivo.BankTranWebServiceReverseResponse;
import org.e.payment.webservice.bankTran.service.BankTranService;
import org.e.payment.webservice.bankTran.service.BankTranServiceAbs;
import org.e.payment.webservice.bankTran.service.vo.BankRechargeRespBody;
import org.e.payment.webservice.bankTran.service.vo.RechargeRequest;
import org.e.payment.webservice.bankTran.service.vo.RechargeResponse;
import org.e.payment.webservice.bankTran.service.vo.ReverseRequest;
import org.e.payment.webservice.bankTran.service.vo.ReverseRespBody;
import org.e.payment.webservice.bankTran.service.vo.ReverseResponse;
import org.e.payment.webservice.common.SpringBeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.common.cxf.SOAPResultCode;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;

@WebService(targetNamespace = "http://201.249.156.12/webservice", endpointInterface = "org.e.payment.webservice.bankTran.service.BankTranService")
// @BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class BankTranServiceImpl extends BankTranServiceAbs implements BankTranService {
	private static final Logger log = LoggerFactory.getLogger(BankTranServiceImpl.class);

	@Resource
	WebServiceContext wsContext;

	private Environment env = SpringBeanUtil.getBean(Environment.class);

	private String rechargeApi = env.getProperty("httpClient.bankTran.rechargeApi");// "http://localhost:8080/webservice/bankTran/recharge";
	private String reverseApi = env.getProperty("httpClient.bankTran.reverseApi");// "http://localhost:8080/webservice/bankTran/reverse";

	private String userName = env.getProperty("httpClient.bankTran.userName");// "4rfv^YHN";
	private String password = env.getProperty("httpClient.bankTran.password");// "*IK<>LO()P:?";

	private CloseableHttpClient httpClient = SpringBeanUtil.getBean(CloseableHttpClient.class);
	private RequestConfig config = SpringBeanUtil.getBean(RequestConfig.class);

	@Override
	public RechargeResponse Recharge(RechargeRequest rechargeRequest) {
		log.info(String.format("Recharge recv %s", JSONObject.toJSON(rechargeRequest)));
		long s = System.currentTimeMillis();
		String ipAddress = getIpAddress(wsContext);

		RechargeResponse rechargeResponse = new RechargeResponse();
		BankRechargeRespBody bankRechargeRespBody = new BankRechargeRespBody();
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

	@Override
	public ReverseResponse Reverse(ReverseRequest reverseRequest) {
		log.info(String.format("Reverse recv %s", JSONObject.toJSON(reverseRequest)));
		long s = System.currentTimeMillis();
		String ipAddress = getIpAddress(wsContext);

		ReverseResponse reverseResponse = new ReverseResponse();
		ReverseRespBody reverseRespBody = new ReverseRespBody();
		reverseResponse.setReverseRespBody(reverseRespBody);
		reverseResponse.setInterfaceVersion(reverseRequest.getInterfaceVersion());
		reverseResponse.setRefNo(reverseRequest.getRefNo());

		SOAPResultCode result = validateReverseRequest(reverseRequest);
		if (result != SOAPResultCode.OK) {
			reverseRespBody.setResultCode(result.getCode());
			reverseResponse.setSigMsg(getSigMsg(reverseRespBody));
			log.info(String.format("usetime=%s,Reverse validateRechargeRequest resultCode=%s,resultMsg=%s", (System.currentTimeMillis() - s), result.getCode(), result.getReason()));
			return reverseResponse;
		}
		// 请求参数
		BankTranWebServiceReverseRequest bankTranWebServiceReverseRequest = new BankTranWebServiceReverseRequest();
		bankTranWebServiceReverseRequest.setUserName(userName);
		bankTranWebServiceReverseRequest.setPassword(password);

		bankTranWebServiceReverseRequest.setInterfaceVersion(reverseRequest.getInterfaceVersion());
		bankTranWebServiceReverseRequest.setBankId(reverseRequest.getBankId());
		bankTranWebServiceReverseRequest.setIpAddress(ipAddress);
		bankTranWebServiceReverseRequest.setRefNo(reverseRequest.getRefNo());
		bankTranWebServiceReverseRequest.setReverseRefNo(reverseRequest.getReverseReqBody().getReverseRefNo());

		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(bankTranWebServiceReverseRequest);

		//////////////// httpclient start/////////////////
		long hs = System.currentTimeMillis();
		HttpPost httppost = new HttpPost(reverseApi);
		httppost.setConfig(config);

		try {
			// log.info(String.format("Reverse send httpclient %s,%s", reverseApi, jsonParam.toString()));
			StringEntity stringEntity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");
			httppost.setEntity(stringEntity);
			CloseableHttpResponse response = httpClient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
					log.info(String.format("usetime=%s,httpclientusetime=%s,Reverse httpclient get response from httpClient %s", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs), responseStr));
					if (responseStr != null && responseStr.contains("body")) {
						JSONObject jsonObjectResponse = new JSONObject();
						jsonObjectResponse = (JSONObject) jsonObjectResponse.parse(responseStr);
						BankTranWebServiceReverseResponse bankTranWebServiceReverseResponse = JSONObject.toJavaObject(jsonObjectResponse.getJSONObject("body"), BankTranWebServiceReverseResponse.class);
						if (bankTranWebServiceReverseResponse != null) {
							reverseRespBody.setResultCode(bankTranWebServiceReverseResponse.getResultCode().getCode());
							reverseResponse.setSigMsg(getSigMsg(reverseRespBody));
							log.info(String.format("usetime=%s,httpclientusetime=%s,Reverse httpclient get response from httpClient %s", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs),
									JSONObject.toJSON(bankTranWebServiceReverseResponse)));
							return reverseResponse;
						} else {
							reverseRespBody.setResultCode(SOAPResultCode.SERVICE_RESPONSE_IS_EMPTY.getCode());
							reverseResponse.setSigMsg(getSigMsg(reverseRespBody));
							log.info(String.format("usetime=%s,httpclientusetime=%s,Reverse httpclient get response is null", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)));
							return reverseResponse;
						}
					} else {
						reverseRespBody.setResultCode(SOAPResultCode.SERVICE_RESPONSE_ERROR.getCode());
						reverseResponse.setSigMsg(getSigMsg(reverseRespBody));
						log.info(String.format("usetime=%s,httpclientusetime=%s,Reverse httpclient get response error", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)));
						return reverseResponse;
					}
				} else {
					reverseRespBody.setResultCode(SOAPResultCode.SERVICE_RESPONSE_ERROR.getCode());
					reverseResponse.setSigMsg(getSigMsg(reverseRespBody));
					log.info(String.format("usetime=%s,httpclientusetime=%s,Reverse httpclient get response error2", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)));
					return reverseResponse;
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			reverseRespBody.setResultCode(SOAPResultCode.SERVICE_CAN_NOT_CONNECT.getCode());
			reverseResponse.setSigMsg(getSigMsg(reverseRespBody));
			log.info(String.format("usetime=%s,httpclientusetime=%s,Reverse httpclient not connect", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)));
			log.error(String.format("usetime=%s,httpclientusetime=%s,Recharge httpclient not connect", (System.currentTimeMillis() - s), (System.currentTimeMillis() - hs)), e);
			return reverseResponse;
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

	private SOAPResultCode validateRechargeRequest(RechargeRequest rechargeRequest) {
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

		soapResultCode = validateSigMsg(rechargeRequest.getBankRechargeReqBody(), rechargeRequest.getSigMsg());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		return soapResultCode;
	}

	private SOAPResultCode validateReverseRequest(ReverseRequest reverseRequest) {

		if (reverseRequest == null) {
			return SOAPResultCode.REQUEST_IS_NULL;
		}
		SOAPResultCode soapResultCode = SOAPResultCode.OK;
		soapResultCode = validateInterfaceVersion(reverseRequest.getInterfaceVersion());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		soapResultCode = validateInterfaceRefNo(reverseRequest.getRefNo());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		soapResultCode = validateBankId(reverseRequest.getBankId());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		if (reverseRequest.getReverseReqBody() == null) {
			return SOAPResultCode.REVERSE_REQ_BODY_IS_NULL;
		}

		soapResultCode = validateInterfaceRefNo(reverseRequest.getReverseReqBody().getReverseRefNo());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		soapResultCode = validateSigMsg(reverseRequest.getReverseReqBody(), reverseRequest.getSigMsg());
		if (soapResultCode != SOAPResultCode.OK) {
			return soapResultCode;
		}

		return soapResultCode;

	}

}
