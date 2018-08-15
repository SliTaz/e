package org.e.payment.webservice.client.pro;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.apache.commons.codec.digest.DigestUtils;
import org.e.payment.webservice.client.config.cxf.CxfSignatureConfPro;
import org.e.payment.webservice.client.vo.bankTran.BankPaymentInfo;
import org.e.payment.webservice.client.vo.bankTran.BankRechargeReqBody;
import org.e.payment.webservice.client.vo.bankTran.BankTranService;
import org.e.payment.webservice.client.vo.bankTran.RechargeRequest;
import org.e.payment.webservice.client.vo.bankTran.RechargeResponse2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zbensoft.e.payment.common.cxf.SOAPResultCode;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.JaxbUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CxfSignatureConfPro.class)
@SpringBootTest
public class BankTranSignatureRechargeProTest {

	@Autowired
	private BankTranService bankTranService;

	@Test
	public void recharge() {
		RechargeRequest rechargeRequest = new RechargeRequest();
		rechargeRequest.setInterfaceVersion("1.0");
		rechargeRequest.setBankId("0190");
		rechargeRequest.setRefNo(System.currentTimeMillis() + "");
		BankRechargeReqBody bankRechargeReqBody = new BankRechargeReqBody();
		BankPaymentInfo bankPaymentInfo = new BankPaymentInfo();
		bankPaymentInfo.setAmount("100");
		bankPaymentInfo.setPaymentTime(
				DateUtil.convertDateToString(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_SEVENTEEN));
		bankPaymentInfo.setVid("V00000190");
		bankRechargeReqBody.setBankPaymentInfo(bankPaymentInfo);
		rechargeRequest.setBankRechargeReqBody(bankRechargeReqBody);

		String xml = JaxbUtil.beanToXml3(bankRechargeReqBody);
		rechargeRequest.setSigMsg(DigestUtils.sha256Hex(xml));

		RechargeResponse2 result = bankTranService.recharge(rechargeRequest);
		System.out.println("====================refNo=" +rechargeRequest.getRefNo() +"====================="+ result.getBankRechargeRespBody().getResultCode());
		assertEquals(result.getBankRechargeRespBody().getResultCode(), SOAPResultCode.OK.getCode());
	}
}
