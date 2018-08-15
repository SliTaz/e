package org.e.payment.webservice.bankTran.serviceV2;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.zbensoft.e.payment.common.cxf.SOAPResultCode;
import com.zbensoft.e.payment.common.util.JaxbUtil;

public abstract class BankTranServiceV2Abs {

	protected String getIpAddress(WebServiceContext wsContext2) {
		MessageContext mc = wsContext2.getMessageContext();
		HttpServletRequest req = (HttpServletRequest) mc.get(MessageContext.SERVLET_REQUEST);
		return req.getRemoteAddr();
	}

	protected String getSigMsg(Object body) {
		String xml = JaxbUtil.beanToXml3(body);
		return DigestUtils.sha256Hex(xml);
	}

	protected SOAPResultCode validateInterfaceRefNo(String refNo) {
		if (StringUtils.isEmpty(refNo)) {
			return SOAPResultCode.REFERENCE_NUMBER_IS_EMPTY;
		}
		return SOAPResultCode.OK;
	}

	protected SOAPResultCode validateInterfaceVersion(String interfaceVersion) {
		if (StringUtils.isEmpty(interfaceVersion)) {
			return SOAPResultCode.VERSION_NUMBER_IS_EMPTY;
		}
		if (!interfaceVersion.equals("2.0")) {
			return SOAPResultCode.VERSION_NUMBER_NOT_MATCH;
		}
		return SOAPResultCode.OK;
	}

	protected SOAPResultCode validateBankId(String bankId) {
		if (StringUtils.isEmpty(bankId)) {
			return SOAPResultCode.BANK_ID_NOTEMPTY;
		}
		if (bankId.length() > 4) {
			return SOAPResultCode.BANK_ID_LENGTH;
		}
		return SOAPResultCode.OK;
	}

	protected SOAPResultCode validateSigMsg(Object object, String sigMsg) {
		if (StringUtils.isEmpty(sigMsg)) {
			return SOAPResultCode.SIGMSG_IS_EMPTY;
		}
		String xml = JaxbUtil.beanToXml3(object);
		if (!DigestUtils.sha256Hex(xml).equalsIgnoreCase(sigMsg)) {
			return SOAPResultCode.SIGMSG_IS_ERROR;
		}

		return SOAPResultCode.OK;
	}

	protected SOAPResultCode validateVID(String vid) {
		if (StringUtils.isEmpty(vid)) {
			return SOAPResultCode.VID_IS_EMPTY;
		}
		// if (!vid.startsWith("V")) {
		// return SOAPResultCode.VID_FORMATE_INCORRECT;
		// }
		// if (vid.length() != 9) {
		// return SOAPResultCode.VID_LENGTH_INCORRECT;
		// }
		return SOAPResultCode.OK;
	}

	protected SOAPResultCode validatePatrimonyCardCode(String patrimonyCardCode) {
		if (StringUtils.isEmpty(patrimonyCardCode)) {
			return SOAPResultCode.PATRIMONY_CARD_CODE_NOTEMPTY;
		}
		if (patrimonyCardCode.length() > 15) {
			return SOAPResultCode.PATRIMONY_CARD_CODE_LENGTH;
		}
		return SOAPResultCode.OK;
	}

	protected SOAPResultCode validateAmount(String amount) {
		if (StringUtils.isEmpty(amount)) {
			return SOAPResultCode.AMOUNT_IS_EMPTY;
		}
		if (!StringUtils.isNumeric(amount)) {
			return SOAPResultCode.AMOUNT_FORMATE_INCORRECT;
		}

		return SOAPResultCode.OK;

	}

	protected SOAPResultCode validateDateTime(String dateTime, String Formate) {
		if (StringUtils.isEmpty(dateTime)) {
			return SOAPResultCode.REFERENCE_NUMBER_IS_EMPTY;
		}
		SimpleDateFormat format = new SimpleDateFormat(Formate);
		try {
			format.parse(dateTime);
		} catch (ParseException e) {
			return SOAPResultCode.PAYMENT_TIME_FORMATE_INCORRECT;
		}

		return SOAPResultCode.OK;
	}
}
