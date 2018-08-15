package org.e.payment.webservice.client.test.thread;

import java.util.Calendar;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.e.payment.webservice.client.common.SpringBeanUtil;
import org.e.payment.webservice.client.test.conf.TestConf;
import org.e.payment.webservice.client.test.conf.TestIDGenerate;
import org.e.payment.webservice.client.test.conf.TestManager;
import org.e.payment.webservice.client.test.control.TestController;
import org.e.payment.webservice.client.test.factory.StatisticsManager;
import org.e.payment.webservice.client.vo.bankTran.BankPaymentInfo;
import org.e.payment.webservice.client.vo.bankTran.BankRechargeReqBody;
import org.e.payment.webservice.client.vo.bankTran.RechargeRequest;
import org.e.payment.webservice.client.vo.bankTran.RechargeResponse2;
import org.e.payment.webservice.client.vo.httpclient.BankTranWebServiceRechargeRequest;
import org.e.payment.webservice.client.vo.httpclient.BankTranWebServiceRechargeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.common.mutliThread.MultiThread;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.JaxbUtil;

public class TestThread extends MultiThread {

	private static final Logger log = LoggerFactory.getLogger(TestThread.class);

	private CloseableHttpClient httpClient = SpringBeanUtil.getBean(CloseableHttpClient.class);
	private RequestConfig config = SpringBeanUtil.getBean(RequestConfig.class);

	private Environment env = SpringBeanUtil.getBean(Environment.class);
	private String userName = env.getProperty("httpClient.bankTran.userName");// "4rfv^YHN";
	private String password = env.getProperty("httpClient.bankTran.password");// "*IK<>LO()P:?";
	private String rechargeApi = env.getProperty("httpClient.bankTran.rechargeApi");// "http://localhost:8080/webservice/bankTran/recharge";
	private String reverseApi = env.getProperty("httpClient.bankTran.reverseApi");// "http://localhost:8080/webservice/bankTran/reverse";
	private String loginApi = env.getProperty("httpClient.loginApi");

	public TestThread(String name) {
		super(name);
	}

	@Override
	public boolean process() {
		TestConf conf = TestManager.getInstance().getConf();
		int cont = conf.getCountForOneProcess();
		for (int i = 0; i < cont; i++) {
			if (conf.getTestType() == 0) {
				doWebservice(conf);
			} else if (conf.getTestType() == 1) {
				doHttpClient(conf);
			} else if (conf.getTestType() == 2) {
				doHttpClientLogin(conf);
			}
		}
		return false;
	}

	private void doHttpClientLogin(TestConf conf) {
		String bankId = conf.getBankIdList().get(new Random().nextInt(conf.getBankIdList().size()));
		String vid = conf.getVidList().get(new Random().nextInt(conf.getVidList().size()));
		// 请求参数
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"").append("userName").append("\"").append(":").append("\"").append("10_").append(vid).append("\"")
				.append(",");
		sb.append("\"").append("password").append("\"").append(":").append("\"").append("123456").append("\"");
		sb.append("}");

		//////////////// httpclient start/////////////////
		long hs = System.currentTimeMillis();
		HttpPost httppost = new HttpPost(loginApi);
		httppost.setConfig(config);

		try {
//			log.info(String.format("loginApi send httpclient %s,%s", loginApi, sb.toString()));
			StringEntity stringEntity = new StringEntity(sb.toString(), "utf-8");// 解决中文乱码问题
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");
			httppost.setEntity(stringEntity);
			CloseableHttpResponse response = httpClient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
//					log.info(String.format("loginApi httpclient get response from httpClient %s", responseStr));
					if (responseStr != null && responseStr.contains("body")) {
						JSONObject jsonObjectResponse = new JSONObject();
						jsonObjectResponse = (JSONObject) jsonObjectResponse.parse(responseStr);
						String statusCode = jsonObjectResponse.get("statusCode").toString();
						if ("OK".equals(statusCode)) {
							// log.info(String.format("usetime=%s,Recharge
							// httpclient get response %s",
							// (System.currentTimeMillis() - hs), statusCode));
							StatisticsManager.getInstance().add();
						} else {
							log.info(String.format("usetime=%s,Recharge httpclient get response is null",
									(System.currentTimeMillis() - hs)));
						}
					} else {
						log.info(String.format("usetime=%s,Recharge httpclient get response error",
								(System.currentTimeMillis() - hs)));
					}
				} else {
					log.info(String.format("usetime=%s,Recharge httpclient get response error2",
							(System.currentTimeMillis() - hs)));
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			log.info(String.format("usetime=%s,Recharge httpclient not connect", (System.currentTimeMillis() - hs)));
			log.error(String.format("usetime=%s,Recharge httpclient not connect", (System.currentTimeMillis() - hs)),
					e);
		} finally {
		}
	}

	private void doHttpClient(TestConf conf) {
		String bankId = conf.getBankIdList().get(new Random().nextInt(conf.getBankIdList().size()));
		String vid = conf.getVidList().get(new Random().nextInt(conf.getVidList().size()));
		// 请求参数
		BankTranWebServiceRechargeRequest bankTranWebServiceRechargeRequest = new BankTranWebServiceRechargeRequest();
		bankTranWebServiceRechargeRequest.setUserName(userName);
		bankTranWebServiceRechargeRequest.setPassword(password);

		bankTranWebServiceRechargeRequest.setInterfaceVersion("1.0");
		bankTranWebServiceRechargeRequest.setIpAddress("");
		bankTranWebServiceRechargeRequest.setBankId(bankId);
		bankTranWebServiceRechargeRequest.setRefNo(TestIDGenerate.generateCommOne(TestIDGenerate.TEST_ID));
		Double amount = Double.valueOf((new Random().nextInt(10000) + 1) + "");
		bankTranWebServiceRechargeRequest.setAmount(amount);
		bankTranWebServiceRechargeRequest.setVid(vid);
		bankTranWebServiceRechargeRequest.setPaymentTime(Calendar.getInstance().getTime());

		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(bankTranWebServiceRechargeRequest);

		//////////////// httpclient start/////////////////
		long hs = System.currentTimeMillis();
		HttpPost httppost = new HttpPost(rechargeApi);
		httppost.setConfig(config);

		try {
			log.info(String.format("Recharge send httpclient %s,%s", rechargeApi, jsonParam.toString()));
			StringEntity stringEntity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
			stringEntity.setContentEncoding("UTF-8");
			stringEntity.setContentType("application/json");
			httppost.setEntity(stringEntity);
			CloseableHttpResponse response = httpClient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
					log.info(String.format("Reverse httpclient get response from httpClient %s", responseStr));
					if (responseStr != null && responseStr.contains("body")) {
						JSONObject jsonObjectResponse = new JSONObject();
						jsonObjectResponse = (JSONObject) jsonObjectResponse.parse(responseStr);
						BankTranWebServiceRechargeResponse bankTranWebServiceRechargeResponse = JSONObject.toJavaObject(
								jsonObjectResponse.getJSONObject("body"), BankTranWebServiceRechargeResponse.class);
						if (bankTranWebServiceRechargeResponse != null) {
							log.info(String.format("usetime=%s,Recharge httpclient get response  %s",
									(System.currentTimeMillis() - hs),
									JSONObject.toJSON(bankTranWebServiceRechargeResponse)));
						} else {
							log.info(String.format("usetime=%s,Recharge httpclient get response is null",
									(System.currentTimeMillis() - hs)));
						}
					} else {
						log.info(String.format("usetime=%s,Recharge httpclient get response error",
								(System.currentTimeMillis() - hs)));
					}
				} else {
					log.info(String.format("usetime=%s,Recharge httpclient get response error2",
							(System.currentTimeMillis() - hs)));
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			log.info(String.format("usetime=%s,Recharge httpclient not connect", (System.currentTimeMillis() - hs)));
			log.error(String.format("usetime=%s,Recharge httpclient not connect", (System.currentTimeMillis() - hs)),
					e);
		} finally {
		}
	}

	private void doWebservice(TestConf conf) {
		String bankId = conf.getBankIdList().get(new Random().nextInt(conf.getBankIdList().size()));
		String vid = conf.getVidList().get(new Random().nextInt(conf.getVidList().size()));
		RechargeRequest rechargeRequest = new RechargeRequest();
		rechargeRequest.setInterfaceVersion("1.0");
		rechargeRequest.setBankId(bankId);
		rechargeRequest.setRefNo(TestIDGenerate.generateCommOne(TestIDGenerate.TEST_ID));
		BankRechargeReqBody bankRechargeReqBody = new BankRechargeReqBody();
		BankPaymentInfo bankPaymentInfo = new BankPaymentInfo();
		bankPaymentInfo.setAmount((new Random().nextInt(10000) + 1) + "");
		bankPaymentInfo.setPaymentTime(
				DateUtil.convertDateToString(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_SEVENTEEN));
		bankPaymentInfo.setVid(vid);
		bankRechargeReqBody.setBankPaymentInfo(bankPaymentInfo);
		rechargeRequest.setBankRechargeReqBody(bankRechargeReqBody);

		String xml = JaxbUtil.beanToXml3(bankRechargeReqBody);
		rechargeRequest.setSigMsg(DigestUtils.sha256Hex(xml));
		long s = System.currentTimeMillis();
		RechargeResponse2 result = TestController.service.recharge(rechargeRequest);
		log.info(String.format("usetime=%s,result=%s", (System.currentTimeMillis() - s),
				result == null ? "" : result.getBankRechargeRespBody().getResultCode()));

	}

}
