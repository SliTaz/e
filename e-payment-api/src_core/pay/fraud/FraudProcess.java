package org.e.payment.core.pay.fraud;

import java.util.Map;

public interface FraudProcess {

	public FraudResult process(Map<String, String> param);
}
