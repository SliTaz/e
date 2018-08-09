package com.zbensoft.e.payment.api.common;

/**
 * 
 * 
 * @author xieqiang
 *
 */
public interface RedisDef {

	/**
	 * 分割符
	 * 
	 * @author xieqiang
	 *
	 */
	public interface DELIMITER {
		public static String UNDERLINE = "_";
	}

	/**
	 * 登录
	 * 
	 * @author xieqiang
	 *
	 */
	public interface LOGIN {
		public static String TOKEN = "T";
		public static String ERROR_PASSWORD_COUNT = "EPC";
		public static String ERROR_PAY_PASSWORD_COUNT = "EPPC";

	}

	/**
	 * 二维码
	 * 
	 * @author xieqiang
	 *
	 */
	public interface QR_CODE {
		public static String QR_CODE = "QC";
	}

	/**
	 * 订单号
	 * 
	 * @author xieqiang
	 *
	 */
	public interface TRADE_SUMBIT {
		public static String ORDER_NO = "ON";
		public static String ORDER_NO_CHARGE = "ONC";// 提现使用接口
	}

	/**
	 * 余额
	 * 
	 * @author xieqiang
	 *
	 */
	public interface ACCOUNT_AMOUNT {
		public static String ACCOUNT_AMOUNT = "A";
	}

	/**
	 * 统计前缀
	 * 
	 * @author xieqiang
	 *
	 */
	public interface STATISTICS_PRE {
		public static String SUCC = "S_";
		public static String DB = "D_";
	}

	/**
	 * 统计
	 * 
	 * @author xieqiang
	 *
	 */
	public interface STATISTICS {
		public static String BANK_RECHARGE = "ST_BRA";
		public static String BANK_REVERSE = "ST_BRB";
		public static String CONSUMPTION = "ST_CA";
		public static String RECHARGE = "ST_R";
		public static String CHARGE = "ST_CB";
	}

	/**
	 * 计数
	 * 
	 * @author xieqiang
	 *
	 */
	public interface COUNT {
		public static String COUNT_BUYER = "CB";
		public static String COUNT_QUEST = "CQA";
		public static String COUNT_QUEST_SUCC = "CQS";
		public static String COUNT_DOWNLOAD_APP = "CDA";
	}

	/**
	 * 每天限制数
	 * 
	 * @author xieqiang
	 *
	 */
	public interface STATISTICS_LIMIT_DAY {
		public static String BANK_RECHARGE = "LDST_BRA";
		public static String BANK_REVERSE = "LDST_BRB";
		public static String CONSUMPTION = "LDST_CA";
		public static String RECHARGE = "LDST_R";
		public static String CHARGE = "LDST_CB";
	}

	/**
	 * 每月限制数
	 * 
	 * @author xieqiang
	 *
	 */
	public interface STATISTICS_LIMIT_MONTH {
		public static String MONTHLY_BANK_RECHARGE = "LMST_BRA";
		public static String MONTHLY_BANK_REVERSE = "LMST_BRB";
		public static String MONTHLY_CONSUMPTION = "LMST_CA";
		public static String MONTHLY_RECHARGE = "LMST_R";
		public static String MONTHLY_CHARGE = "LMST_CB";
	}

	/**
	 * bi
	 * 
	 * @author xieqiang
	 *
	 */
	public interface BI {
		public static String PROFIT_STATEMENT = "BI_RS";

	}

	/**
	 * 邮箱
	 * 
	 * @author xieqiang
	 *
	 */
	public interface EMAIL {
		public static String VC = "VC";
	}
	

	/**
	 * 验证码用户key
	 * 
	 * @author wagnchenyang
	 *
	 */
	public interface VERIFICATION_CODE {
		public static String IMG_VERIFI_CODE = "IVC";
		
	}
}
