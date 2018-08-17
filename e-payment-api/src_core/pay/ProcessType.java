package org.e.payment.core.pay;

public interface ProcessType {
	/** 1：充值 **/
	public static int RECHARGE = 1;
	/** 2：提现 **/
	public static int CHARGE = 2;
	/** 3：转账 **/
	public static int TRANSFER = 3;
	/** 4：转账到银行卡 **/
	public static int TRANSFER_BANK = 4;
	/** 5：充值 **/
	public static int RECHARGE_APP = 5;
	/** 6：消费（收款） **/
	public static int CONSUMPTION_APP = 6;
	/** 7：商家（退款） **/
	public static int REFUND = 7;


	/** 20：GateWay **/
	public static int GATEWAY = 100;

	/** 21：银行充值-webservice **/
	public static int WEBSERVICE_BANK_RECHARGE = 201;
	/** 22：银行取消-webservice **/
	public static int WEBSERVICE_BANK_REVERSE = 202;

	/** 91：银行充值，差错处理，补账单，充值 **/
	public static int ERROR_HANDLING_BANK_RECHARGE_RECHARGE = 301;
	/** 92：银行充值，差错处理，银行没有信息，充值失败，扣款 **/
	public static int ERROR_HANDLING_BANK_RECHARGE_REFUND = 302;

	/** 93：提现，差错处理，银行返回错误，提现失败，返款 **/
	public static int ERROR_HANDLING_CHARGE_REFUND = 321;
	
	/** 322：提现，差错处理，银行没有记录，手动处理 **/
	public static int ERROR_HANDLING_CHARGE_BANK_MISS = 322;
	/** 323：提现，差错处理，Epay没有记录，手动处理 **/
	public static int ERROR_HANDLING_CHARGE_EPAY_MISS = 323;

}
