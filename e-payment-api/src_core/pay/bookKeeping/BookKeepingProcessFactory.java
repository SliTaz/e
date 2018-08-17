package org.e.payment.core.pay.bookKeeping;

import org.e.payment.core.pay.bookKeeping.impl.bankrecharge.BookKeepingBankRechargeErrorHandlingProcessImpl;
import org.e.payment.core.pay.bookKeeping.impl.bankrecharge.BookKeepingBankRechargeReconciliationProcessImpl;
import org.e.payment.core.pay.bookKeeping.impl.charge.BookKeepingChargeReconciliationProcessImpl;
import org.e.payment.core.pay.bookKeeping.impl.charge.BookKeepingChargeErrorHandlingProcessImpl;
import org.e.payment.core.pay.bookKeeping.impl.submit.BookKeepingSubmitProcessImpl;

public class BookKeepingProcessFactory {

	private static BookKeepingProcessFactory instance = null;

	private BookKeepingProcessFactory() {

	}

	public static BookKeepingProcessFactory getInstance() {
		if (instance == null) {
			instance = new BookKeepingProcessFactory();
		}
		return instance;
	}

	public BookKeepingProcess get(int bookKeepingType) {
		switch (bookKeepingType) {
		case BookKeepingType.TRADE:
			return new BookKeepingSubmitProcessImpl();
		case BookKeepingType.BANK_RECHARGE_RECONCILIATION:
			return new BookKeepingBankRechargeReconciliationProcessImpl();
		case BookKeepingType.BANK_RECHARGE_ERROR_HANDLING:
			return new BookKeepingBankRechargeErrorHandlingProcessImpl();
		case BookKeepingType.CHARGE_RECONCILIATION:
			return new BookKeepingChargeReconciliationProcessImpl();
		case BookKeepingType.CHARGE_ERROR_HANDLING:
			return new BookKeepingChargeErrorHandlingProcessImpl();
		default:
			break;
		}
		return null;
	}
}
