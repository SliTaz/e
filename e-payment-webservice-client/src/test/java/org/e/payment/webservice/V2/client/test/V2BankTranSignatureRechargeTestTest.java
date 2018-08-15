package org.e.payment.webservice.V2.client.test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.apache.commons.codec.digest.DigestUtils;
import org.e.payment.webservice.client.config.cxf.CxfSignatureConfV2;
import org.e.payment.webservice.client.vo.V2.bankTran.BankPaymentInfoV2;
import org.e.payment.webservice.client.vo.V2.bankTran.BankRechargeReqBodyV2;
import org.e.payment.webservice.client.vo.V2.bankTran.BankTranServiceV2;
import org.e.payment.webservice.client.vo.V2.bankTran.RechargeRequestV2;
import org.e.payment.webservice.client.vo.V2.bankTran.RechargeResponseV2;
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
@ContextConfiguration(classes = CxfSignatureConfV2.class)
@SpringBootTest
public class V2BankTranSignatureRechargeTestTest {

	@Autowired
	private BankTranServiceV2 bankTranServiceV2;

	@Test
	public void recharge() {
		RechargeRequestV2 rechargeRequest = new RechargeRequestV2();
		rechargeRequest.setInterfaceVersion("2.0");
		rechargeRequest.setBankId("0190");
		rechargeRequest.setRefNo(System.currentTimeMillis() + "");
		BankRechargeReqBodyV2 bankRechargeReqBody = new BankRechargeReqBodyV2();
		BankPaymentInfoV2 bankPaymentInfo = new BankPaymentInfoV2();
		bankPaymentInfo.setAmount("100");
		bankPaymentInfo.setPaymentTime(
				DateUtil.convertDateToString(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_SEVENTEEN));
		bankPaymentInfo.setVid("V00000001");
		bankPaymentInfo.setPatrimonyCardCode("1");
		bankRechargeReqBody.setBankPaymentInfo(bankPaymentInfo);
		rechargeRequest.setBankRechargeReqBody(bankRechargeReqBody);

		String xml = JaxbUtil.beanToXml3(bankRechargeReqBody);
		rechargeRequest.setSigMsg(DigestUtils.sha256Hex(xml));

		RechargeResponseV2 result = bankTranServiceV2.recharge(rechargeRequest);
		System.out.println("=========V2===pro========refNo=" + rechargeRequest.getRefNo() + "====================="
				+ result.getBankRechargeRespBody().getResultCode());
		assertEquals(result.getBankRechargeRespBody().getResultCode(), SOAPResultCode.OK.getCode());
	}
}
