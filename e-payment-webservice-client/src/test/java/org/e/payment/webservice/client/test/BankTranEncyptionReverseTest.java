package org.e.payment.webservice.client.test;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.digest.DigestUtils;
import org.e.payment.webservice.client.config.cxf.CxfEncryptionConf;
import org.e.payment.webservice.client.vo.bankTran.BankTranService;
import org.e.payment.webservice.client.vo.bankTran.ReverseReqBody;
import org.e.payment.webservice.client.vo.bankTran.ReverseRequest;
import org.e.payment.webservice.client.vo.bankTran.ReverseResponse2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zbensoft.e.payment.common.cxf.SOAPResultCode;
import com.zbensoft.e.payment.common.util.JaxbUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CxfEncryptionConf.class)
@SpringBootTest
public class BankTranEncyptionReverseTest {

	@Autowired
	private BankTranService bankTranService;

	@Test
	public void recharge() {
		ReverseRequest reverseRequest = new ReverseRequest();
		reverseRequest.setInterfaceVersion("1.0");
		reverseRequest.setBankId("1");
		reverseRequest.setRefNo(System.currentTimeMillis() + "");
		ReverseReqBody reverseReqBody = new ReverseReqBody();
		reverseReqBody.setReverseRefNo("1501209097770");
		reverseRequest.setReverseReqBody(reverseReqBody);

		String xml = JaxbUtil.beanToXml3(reverseReqBody);
		reverseRequest.setSigMsg(DigestUtils.sha256Hex(xml));

		ReverseResponse2 result = bankTranService.reverse(reverseRequest);
		System.out.println("=====================" + result.getReverseRespBody().getResultCode());
		assertEquals(result.getReverseRespBody().getResultCode(), SOAPResultCode.OK.getCode());
	}
}
