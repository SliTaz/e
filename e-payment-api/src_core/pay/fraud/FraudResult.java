package org.e.payment.core.pay.fraud;

public enum FraudResult {

	SUCC(1, "SUCC", "SUCC", "SUCC"), //
	WARNING(99, "WARNING", "WARNING", "WARNING Error"), //
	ERROR(99, "ERROR", "ERROR", "ERROR");//

	int code;
	String key;
	String reason;
	String reason_es;

	private FraudResult(int code, String key, String reason, String reason_es) {
		this.code = code;
		this.key = key;
		this.reason = reason;
		this.reason_es = reason_es;
	}

	public String getReason() {
		return reason;
	}

	public String getReason_es() {
		return reason_es;
	}

	public int getCode() {
		return code;
	}

	public String getKey() {
		return key;
	}

	public String toString() {
		return String.valueOf(code);
	}

	public static FraudResult getResponseStatusCode(int value) {
		for (FraudResult status : FraudResult.values()) {
			if (status.getCode() == value) {
				return status;
			}
		}
		return ERROR;
	}
}
