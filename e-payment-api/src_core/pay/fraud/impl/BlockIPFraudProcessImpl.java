package org.e.payment.core.pay.fraud.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.e.payment.core.pay.fraud.FraudProcess;
import org.e.payment.core.pay.fraud.FraudResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockIPFraudProcessImpl implements FraudProcess {

	private static final Logger log = LoggerFactory.getLogger(BlockIPFraudProcessImpl.class);

	@Override
	public FraudResult process(Map<String, String> param) {
		if (param == null) {
			return FraudResult.SUCC;
		}

		String ipAddress = param.get("ip_address");
		if (StringUtils.isEmpty(ipAddress)) {
			return FraudResult.SUCC;
		}

		try {

			InetAddress.getByName(ipAddress).getAddress();
		} catch (UnknownHostException e) {
			log.error("", e);
		}

		return FraudResult.SUCC;
	}

}
