package com.zbensoft.e.payment.tools.pay;

/**
 * 
 * @author xieqiang
 *
 */
public enum HttpRestStatus {
	OK(200, "OK"),


	// 1000
	// login start
	LOGIN_UNKONW_ERROR(1000, "LOGIN_USER_PASSWORD_ERROR_COUNT"), //
	LOGIN_USER_NAME_NOTEMPTY(1001, "LOGIN_USER_NAME_NOTEMPTY"), //
	LOGIN_PASSWORD_NOTEMPTY(1002, "LOGIN_PASSWORD_NOTEMPTY"), //
	LOGIN_USER_NAME_PASSWORD_ERROR(1003, "LOGIN_USER_NAME_PASSWORD_ERROR"), //
	LOGIN_TYPE_NOTEMPTY(1004, "LOGIN_TYPE_NOTEMPTY"), //
	LOGIN_TYPE_ERROR(1005, "LOGIN_TYPE_ERROR"), //
	LOGIN_USER_DISABLE(1006, "LOGIN_USER_DISABLE"), //
	LOGIN_USER_LOCKED(1007, "LOGIN_USER_LOCKED"), //
	LOGIN_USER_NOTFUND(1008, "LOGIN_USER_NOTFUND"), //
	LOGIN_USER_PASSWORD_ERROR_COUNT(1009, "LOGIN_USER_PASSWORD_ERROR_COUNT"), //
	// login end

	NOTEMPTY(1010, "NotEmpty"), // 非空
	LENGTH(1011, "Length"), // 长度
	PATTERN(1012, "Pattern"), // 正则表达式:hibernate validator的正则表达式只能针对String类型
	RANGE(1013, "Range"), // 范围。针对非字符串
	TYPE(1014, "TYPE"), // 范围。针对非字符串

	NOT_EXIST(1101, "NOT_EXIST"), // 不存在
	EXIST(1102, "EXIST"), // 存在

	QRCODE_ERROR(2000, "QRCODE_ERROR"), //
	QRCODE_EXPRIE(2001, "QRCODE_EXPRIE"), //
	IMAGE_UPLOAD_FAILED_ERROR(2002, "IMAGE_UPLOAD_FAILED_ERROR"), //
	TASK_CRON_ERROR(2003, "TASK_CRON_ERROR"), //
	TASK_JOB_CLASS_NOT_EXIST(2004, "TASK_JOB_CLASS_NOT_EXIST"), //
	CONSUMER_ACTIVATE_TIME_NOTEMPTY(2005, "CONSUMER_ACTIVATE_TIME_NOTEMPTY"), //
	STARTTIME_OVER_ENDTIME_ERROR(2006, "STARTTIME_OVER_ENDTIME_ERROR"), //
	PATRIMONY_CARD_IS_EXPIRED(2007, "PATRIMONY_CARD_IS_EXPIRED"), //
	CONSUMER_NOTIN_MERCHANT_STORE(2008, "CONSUMER_NOTIN_MERCHANT_STORE"), //
	
	
	//自定义code校验start
	testCode(3000, "testCode"), //
	EMPLOYEE_CONFLICT(3001, "EMPLOYEE_CONFLICT"), //
	MERCHANT_NOT_FOUND(3002, "MERCHANT_NOT_FOUND"), //
	CONSUMER_NOT_FOUND(3003, "CONSUMER_NOT_FOUND"), //
	USERID_NOT_FOUND(3004, "USERID_NOT_FOUND"), //
	PASSWORD_SAME(3005, "PASSWORD_SAME"), //
	PASSWORD_NOT_SAME(3006, "PASSWORD_NOT_SAME"), //
	PASSWORD_NOT_NULL(3007, "PASSWORD_NOT_NULL"), //
	PASSWORD_NOT_USER(3008, "PASSWORD_NOT_USER"), //
	PASSWORD_OLD_ERROR(3009, "PASSWORD_OLD_ERROR"), //
	PASSWORD_USER_ENABLE(3010, "PASSWORD_USER_ENABLE"), //
	PASSWORD_LOGIN_FORMAT_ERROR(3011, "PASSWORD_LOGIN_FORMAT_ERROR"), //
	PASSWORD_PAY_FORMAT_ERROR(3012, "PASSWORD_PAY_FORMAT_ERROR"), //
	EMAIL_FORMAT_ERROR(3013, "EMAIL_FORMAT_ERROR"), //
	EMAIL_SEND_ERROR(3014, "EMAIL_SEND_ERROR"), //
	EMAIL_VALIDATE_CODE_ERROR(3015, "EMAIL_VALIDATE_CODE_ERROR"), //
	EMAIL_NOT_MATH_ERROR(3016, "EMAIL_NOT_MATH_ERROR"), //
	PASSWORD_ERROR(3017, "PASSWORD_ERROR"), //
	
	
	//自定义code校验end
	
	
	// pay start
	PAY_TRADE_TYPE_NOT_EXIST(9001, "PAY_TRADE_TYPE_NOT_EXIST"), // 交易类型不存在
	PAY_PAY_AMOUNT_NOTEMPTY(9011, "PAY_PAY_AMOUNT_NOTEMPTY"), // 金额不能为空
	PAY_PAY_AMOUNT_LESS_THAN_TWO_DECIMAL(9012, "PAY_PAY_AMOUNT_LESS_THAN_TWO_DECIMAL"), // 金额小数最大只能2为
	PAY_PAY_AMOUNT_MUST_DOUBLE(9013, "PAY_PAY_AMOUNT_MUST_DOUBLE"), // 金额只能是double
	PAY_PAY_AMOUNT_GREATER_THEN_ZERO(9014, "PAY_PAY_AMOUNT_GREATER_THEN_ZERO"), // 金额只能大于0
	PAY_PAYAPPID_NOTEMPTY(9021, "PAY_PAYAPPID_NOTEMPTY"), //
	PAY_PAYAPPID_NOTEXIST(9022, "PAY_PAYAPPID_NOTEXIST"), //
	PAY_PAYAPPID_NOTENABLE(9023, "PAY_PAYAPPID_NOTENABLE"), //
	PAY_ORDERNO_NOTEMPTY(9031, "PAY_ORDERNO_NOTEMPTY"), // 订单号不能为空
	PAY_ORDERNO_LENGTH(9032, "PAY_ORDERNO_NOTEMPTY"), //
	PAY_CONSUMPTIONNAME_NOTEMPTY(9041, "PAY_CONSUMPTIONNAME_NOTEMPTY"), // 消费名称不能为空
	PAY_CONSUMPTIONNAME_LENGTH(9042, "PAY_CONSUMPTIONNAME_NOTEMPTY"), //
	PAY_CALLBACK_URL_NOTEMPTY(9051, "PAY_CALLBACK_URL_NOTEMPTY"), // 回调地址不能为空
	PAY_PAY_USER_ID_NOTEMPTY(9061, "PAY_PAY_USER_ID_NOTEMPTY"), // 支付用户编号不能为空
	PAY_PAY_USER_NAME_NOTEMPTY(9062, "PAY_PAY_USER_NAME_NOTEMPTY"), // 支付用户名称不能为空
	PAY_PAY_USER_NOEXIST(9063, "PAY_PAY_USER_NOEXIST"), //
	PAY_PAY_USER_DISABLE(9064, "PAY_PAY_USER_DISABLE"), //
	PAY_PAY_USER_LOCKED(9065, "PAY_PAY_USER_LOCKED"), //
	PAY_PAY_USER_NOTACTIVE(9066, "PAY_PAY_USER_NOTACTIVE"), //
	PAY_PAY_USER_DEFAULT_LOGIN_PASSWORD(9067, "PAY_PAY_USER_DEFAULT_LOGIN_PASSWORD"), //
	PAY_PAY_USER_DEFAULT_PAY_PASSWORD(9068, "PAY_PAY_USER_DEFAULT_PAY_PASSWORD"), //
	PAY_RECV_USER_ID_NOTEMPTY(9071, "PAY_RECV_USER_ID_NOTEMPTY"), // 收款用户编号不能为空
	PAY_RECV_USER_NAME_NOTEMPTY(9072, "PAY_RECV_USER_NAME_NOTEMPTY"), // 收款用户名称不能为空
	PAY_PAY_USER_NAME_RECV_USER_NAME_NOTSAME(9073, "PAY_PAY_USER_NAME_RECV_USER_NAME_NOTSAME"), // 支付用户名称收款用户名称不能相同
	PAY_RECV_USER_NOEXIST(9074, "PAY_RECV_USER_NOEXIST"), //
	PAY_RECV_USER_DISABLE(9075, "PAY_RECV_USER_DISABLE"), //
	PAY_RECV_USER_LOCKED(9076, "PAY_RECV_USER_LOCKED"), //
	PAY_RECV_USER_NOTACTIVE(9077, "PAY_RECV_USER_NOTACTIVE"), //
	PAY_RECV_USER_DEFAULT_LOGIN_PASSWORD(9078, "PAY_RECV_USER_DEFAULT_LOGIN_PASSWORD"), //
	PAY_RECV_USER_DEFAULT_PAY_PASSWORD(9079, "PAY_RECV_USER_DEFAULT_PAY_PASSWORD"), //
	PAY_CALLBACK_URL_LENGTH(9081, "PAY_CALLBACK_URL_LENGTH"), //
	PAY_APP_ID_NOTEMPTY(9091, "PAY_APP_ID_NOTEMPTY"), //
	PAY_APP_ID_NOEXIST(9092, "PAY_APP_ID_NOEXIST"), //
	PAY_APP_ID_NO_GATEWAY(9092, "PAY_APP_ID_NO_GATEWAY"), //
	PAY_ERROR(9100, "PAY_ERROR"), //
	PAY_TRADE_NOTEXIST(9111, "PAY_TRADE_NOTEXIST"), //
	PAY_GATEWAY_NOTEXIST(9121, "PAY_GATEWAY_NOTEXIST"), //
	PAY_USER_BIND_BANK_NOTEXIST(9131, "PAY_USER_BIND_BANK_NOTEXIST"), //
	PAY_COUPON_ID_NOT_EMPTY(9141, "PAY_COUPON_ID_NOT_EMPTY"), //
	PAY_COUPON_NOT_EXIST(9142, "PAY_COUPON_NOT_EXIST"), //
	PAY_COUPON_USER_FAMILY_NOT_EMPTY(9143, "PAY_COUPON_TYPE_NOT_EMPTY"), //
	PAY_COUPON_USER_NOT_EMPTY(9144, "PAY_COUPON_USER_NOT_EMPTY"), ///
	PAY_COUPON_FAMILY_NOT_EMPTY(9145, "PAY_COUPON_FAMILY_NOT_EMPTY"), //
	PAY_REQUEST_NOT_EMPTY(9150, "PAY_REQUEST_NOT_EMPTY"), ///
	PAY_REQUEST_TYPE_ERROR(9151, "PAY_REQUEST_TYPE_ERROR"), //
	PAY_PAY_FEE_NOTEMPTY(9152, "PAY_PAY_FEE_NOTEMPTY"), // 金额不能为空
	PAY_PAY_FEE_LESS_THAN_TWO_DECIMAL(9153, "PAY_PAY_FEE_LESS_THAN_TWO_DECIMAL"), // 金额小数最大只能2为
	PAY_PAY_FEE_MUST_DOUBLE(9154, "PAY_PAY_FEE_MUST_DOUBLE"), // 金额只能是double
	PAY_PAY_FEE_GREATER_THEN_OR_EQUEL_ZERO(9155, "PAY_PAY_FEE_GREATER_THEN_OR_EQUEL_ZERO"), // 金额只能大于等于0
	PAY_RECV_AMOUNT_NOTEMPTY(9156, "PAY_RECV_AMOUNT_NOTEMPTY"), //
	PAY_RECV_AMOUNT_LESS_THAN_TWO_DECIMAL(9157, "PAY_RECV_AMOUNT_LESS_THAN_TWO_DECIMAL"), //
	PAY_RECV_AMOUNT_MUST_DOUBLE(9158, "PAY_RECV_AMOUNT_MUST_DOUBLE"), //
	PAY_RECV_AMOUNT_GREATER_THEN_ZERO(9159, "PAY_RECV_AMOUNT_GREATER_THEN_ZERO"), //
	PAY_RECV_FEE_NOTEMPTY(9160, "PAY_RECV_FEE_NOTEMPTY"), //
	PAY_RECV_FEE_LESS_THAN_TWO_DECIMAL(9161, "PAY_RECV_FEE_LESS_THAN_TWO_DECIMAL"), //
	PAY_RECV_FEE_MUST_DOUBLE(9162, "PAY_RECV_FEE_MUST_DOUBLE"), //
	PAY_RECV_FEE_GREATER_THEN_OR_EQUEL_ZERO(9163, "PAY_RECV_FEE_GREATER_THEN_OR_EQUEL_ZERO"), //
	PAY_BANK_CARD_NOTEMPTY(9164, "PAY_BANK_CARD_NOTEMPTY"), //
	PAY_BANK_USERNAME_NOTEMPTY(9165, "PAY_BANK_USERNAME_NOTEMPTY"), //
	PAY_PHONENUMBER_NOTEMPTY(9166, "PAY_PHONENUMBER_NOTEMPTY"), //
	PAY_MERCHANT_EMPLOYEE_NOT_EXIST(9167, "PAY_MERCHANT_EMPLOYEE_NOT_EXIST"), //
	PAY_USER_NAME_NOT_EXIST(9168, "PAY_USER_NAME_NOT_EXIST"), //
	PAY_USER_NAME_NOTEMPTY(9169, "PAY_USER_NAME_NOTEMPTY"), //
	PAY_PAY_PASSWORD_NOTEMPTY(9170, "PAY_PAY_PASSWORD_NOTEMPTY"), //
	PAY_PAY_PASSWORD_ERROR(9171, "PAY_PAY_PASSWORD_ERROR"), //
	PAY_PAY_CONSUMER_NOTIN_MERCHANT_STORE(9172, "PAY_PAY_CONSUMER_NOTIN_MERCHANT_STORE"), //
	PAY_RECV_USERNAME_NOT_UNIQUE(9173, "PAY_PAY_RECV_USERNAME_NOT_UNIQUE"), //
	PAY_ORDER_NO_SAME_SUBMIT(9174, "PAY_ORDER_NO_SAME_SUBMIT"), //
	PAY_ACCOUNT_AMOUNT_NOT_ENOUGH(9175, "PAY_ACCOUNT_AMOUNT_NOT_ENOUGH"), //
	PAY_PAY_GATEWAY_NOTEXIST(9176, "PAY_PAY_GATEWAY_NOTEXIST"), //
	PAY_CALL_BANK_INTERFACE_FAIL(9177, "PAY_CALL_BANK_INTERFACE_FAIL"), //
	PAY_NOT_SUPPORT_TYPE(9178, "PAY_NOT_SUPPORT_TYPE"), //
	PAY_CALL_BANK_INTERFACE_NOT_SUPPORT(9179, "PAY_CALL_BANK_INTERFACE_NOT_SUPPORT"), //
	PAY_WEBSERVICE_VID_NOT_EMPTY(9180, "PAY_WEBSERVICE_VID_NOT_EMPTY"), //
	PAY_WEBSERVICE_VID_NOT_EXIST(9181, "PAY_WEBSERVICE_VID_NOT_EXIST"), //
	PAY_WEBSERVICE_PAYMENTTIME_NOT_EMPTY(9182, "PAY_WEBSERVICE_PAYMENTTIME_NOT_EMPTY"), //
	PAY_WEBSERVICE_REVERSE_REFNO_NOT_EMPTY(9183, "PAY_WEBSERVICE_REVERSE_REFNO_NOT_EMPTY"), //
	PAY_WEBSERVICE_REVERSE_REFNO_SELECT_ERROR(9184, "PAY_WEBSERVICE_REVERSE_REFNO_SELECT_ERROR"), //
	PAY_WEBSERVICE_REVERSE_REFNO_NOT_EXIST(9185, "PAY_WEBSERVICE_REVERSE_REFNO_NOT_EXIST"), //
	PAY_UPDATE_ERROR(9186, "PAY_UPDATE_ERROR"), //
	PAY_WEBSERVICE_REVERSE_REFNO_ISREVERSEED(9187, "PAY_WEBSERVICE_REVERSE_REFNO_ISREVERSEED"), //
	PAY_WEBSERVICE_REVERSE_REFNO_ONLY_BANK_RECHARGE(9188, "PAY_WEBSERVICE_REVERSE_REFNO_ONLY_BANK_RECHARGE"), //
	PAY_WEBSERVICE_BANKID_NOTEMPTY(9189, "PAY_WEBSERVICE_BANKID_NOTEMPTY"), //
	PAY_FRAUD_WARNING(9190, "PAY_FRAUD_WARNING"), //
	PAY_FRAUD_ERROR(9191, "PAY_FRAUD_ERROR"), //
	PAY_COUPON_EXPIRED(9192, "PAY_COUPON_EXPIRED"), //
	PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE(9193, "PAY_LIMIT_DAY_AMOUNT_BANK_RECHARGE"), //
	PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE(9194, "PAY_LIMIT_MONTH_AMOUNT_BANK_RECHARGE"), //
	PAY_ORDER_NO_CHECK_ERROR(9195, "PAY_ORDER_NO_CHECK_ERROR"), //
	PAY_STATISTICS_EROOR(9196, "PAY_STATISTICS_EROOR"), //
	PAY_PAY_PASSWORD_ERROR5(9197, "PAY_PAY_PASSWORD_ERROR5"), //
	PAY_PAY_PASSWORD_ERROR4(9198, "PAY_PAY_PASSWORD_ERROR4"), //
	PAY_PAY_PASSWORD_ERROR3(9199, "PAY_PAY_PASSWORD_ERROR3"), //
	PAY_PAY_PASSWORD_ERROR2(9200, "PAY_PAY_PASSWORD_ERROR2"), //
	PAY_PAY_PASSWORD_ERROR1(9201, "PAY_PAY_PASSWORD_ERROR1"), //
	PAY_PAY_PASSWORD_ERROR0(9202, "PAY_PAY_PASSWORD_ERROR0"), //
	PASSWORD_ERROR5(9203, "PASSWORD_ERROR5"), //
	PASSWORD_ERROR4(9204, "PASSWORD_ERROR4"), //
	PASSWORD_ERROR3(9205, "PASSWORD_ERROR3"), //
	PASSWORD_ERROR2(9206, "PASSWORD_ERROR2"), //
	PASSWORD_ERROR1(9207, "PASSWORD_ERROR1"), //
	PASSWORD_ERROR0(9208, "PASSWORD_ERROR0"), //
	PAY_PAY_PASSWORD_ERROR_COUNT(9209, "PAY_PAY_PASSWORD_ERROR_COUNT"), //
	PAY_COUPON_AMOUNT_NOT_MATCH(9210, "PAY_COUPON_AMOUNT_NOT_MATCH"), //
	PAY_CLAP_CARD_CODE_NOT_MATCH(9211, "PAY_CLAP_CARD_CODE_NOT_MATCH"), //
	// pay end

	UNKNOWN(9999, "unknown error");

	int value;
	String reasonPhrase;

	private HttpRestStatus(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	public int getValue() {
		return value;
	}

	public String toString() {
		return String.valueOf(value);
	}

	public static HttpRestStatus getResponseStatusCode(int value) {
		for (HttpRestStatus status : HttpRestStatus.values()) {
			if (status.getValue() == value) {
				return status;
			}
		}
		return UNKNOWN;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}
	
	
	public static final HttpRestStatus valueOfKey(int value) throws IllegalArgumentException {
		for (HttpRestStatus httpRestStatus : values()) {
			if (httpRestStatus.value == value) {
				return httpRestStatus;
			}
		}
		throw new IllegalArgumentException("No enum const BindType with command id " + value);
	}

}
