package org.e.payment.core.pay.errorHandling;

import org.e.payment.core.pay.errorHandling.impl.bankrecharge.ErrorHandlingBankRechargeProcessImpl;
import org.e.payment.core.pay.errorHandling.impl.charge.ErrorHandlingChargeProcessImpl;

public class ErrorHandlingProcessFactory {

	private static ErrorHandlingProcessFactory instance = null;

	private ErrorHandlingProcessFactory() {

	}

	public static ErrorHandlingProcessFactory getInstance() {
		if (instance == null) {
			instance = new ErrorHandlingProcessFactory();
		}
		return instance;
	}

	public ErrorHandlingProcess get(int bookKeepingType) {
		switch (bookKeepingType) {
		case ErrorHandlingType.BANK_RECHARGE:
			return new ErrorHandlingBankRechargeProcessImpl();
		case ErrorHandlingType.CHARGE:
			return new ErrorHandlingChargeProcessImpl();
		default:
			break;
		}
		return null;

	}

}
