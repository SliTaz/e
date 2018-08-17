package org.e.payment.core.pay.bookKeeping;

public interface CaptionAccount {
	/** buyer账户存款 */
	public static final String C_201_DEPOSIT_BUYER = "201";
	/** seller账户存款 */
	public static final String C_202_DEPOSIT_SELLER = "202";

	/** 待清算Buyer充值 */
	public static final String C_40101_NOT_CLEAR_RECHARGE_BUYER = "40101";
	/** 待清算Seller充值 */
	public static final String C_40102_NOT_CLEAR_RECHARGE_SELLER = "40102";
	/** 待清算Buyer提现 */
	public static final String C_40201_NOT_CLEAR_CHARGE_BUYER = "40201";
	/** 待清算Seller提现 */
	public static final String C_40202_NOT_CLEAR_CHARGE_SELLER = "40202";
	/** 待清算Buyer银行充值 */
	public static final String C_40301_NOT_CLEAR_RECHARGE_BUYER = "40301";
	/** 待清算Seller银行充值 */
	public static final String C_40302_NOT_CLEAR_RECHARGE_SELLER = "40302";
	/** 待清算Buyer银行取消 */
	public static final String C_40401_NOT_CLEAR_REVERSE_BUYER = "40401";
	/** 待清算Seller银行取消 */
	public static final String C_40402_NOT_CLEAR_REVERSE_SELLER = "40402";

	/** 收款专用 */
	public static final String C_11001_RECHARGE = "11001";
	/** test bank one收款 */
	public static final String C_1100101_RECHARGE_TEST_BANK_ONE = "1100101";
	

	/** 付款专用 */
	public static final String C_11002_CHARGE = "11002";
	/** test bank one付款 */
	public static final String C_1100201_CHARGE_TEST_BANK_ONE = "1100201";

}
