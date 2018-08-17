package org.e.payment.core.pay.bankProcess;

public enum BankProcessErrorCode {
	SUCC(0, "SUCC"),
	PAY_USER_TYPE_ERROR(101, "PAY_USER_TYPE_ERROR"),
	PAY_USER_PAY_ERROR(102, "PAY_USER_PAY_ERROR"),
	PAY_NO_PAY_GATEWAY_ERROR(103, "PAY_NO_PAY_GATEWAY_ERROR"),
	PAY_RECHARGE_FORM_MERCHANT_NOT_SUPPERT_RECHARGE_MERCHANT(104, "PAY_RECHARGE_FORM_MERCHANT_NOT_SUPPERT_RECHARGE_MERCHANT"),
	ACCOUNT_AMOUNT_NOT_ENOUGH(105, "ACCOUNT_AMOUNT_NOT_ENOUGH"),

	// end
	UNKNOWN(9999, "unknown error");

	int value;
	String reasonPhrase;

	private BankProcessErrorCode(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		return String.valueOf(value);
	}

	public static BankProcessErrorCode getResponseStatusCode(int value) {
		for (BankProcessErrorCode status : BankProcessErrorCode.values()) {
			if (status.getValue() == value) {
				return status;
			}
		}
		return UNKNOWN;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

}
