package org.e.payment.core.pay.bookKeeping;

public interface BookKeepingType {
	/** 交易 */
	public static final int TRADE = 1;
	/** 对账 */
	public static final int BANK_RECHARGE_RECONCILIATION = 2;
	/** 差错处理 */
	public static final int BANK_RECHARGE_ERROR_HANDLING = 3;

	/** 提现对账 */
	public static final int CHARGE_RECONCILIATION = 4;
	/** 提现差错处理 */
	public static final int CHARGE_ERROR_HANDLING = 5;
}
