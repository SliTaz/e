package com.zbensoft.e.payment.api.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.env.Environment;

public class IDGenerate {

	private static Environment env = SpringBeanUtil.getBean(Environment.class);
	private static String SERVER_CODE = env.getProperty("server.code");

	/** 消费用户编号 **/
	private static AtomicLong CONSUMER_USER_ID = new AtomicLong(1);
	/** 商户户编号 **/
	private static AtomicLong MERCHANT_USER_ID = new AtomicLong(1);
	/** 政府用户编号 **/
	private static AtomicLong GOV_USER_ID = new AtomicLong(1);
	/** 系统用户编号 **/
	private static AtomicLong SYS_USER_ID = new AtomicLong(1);
	/** 交易流水 **/
	public static AtomicLong TRAD_SEQ = new AtomicLong(1);

	/** 消费券 **/
	public static AtomicLong CONSUMER_COUPON = new AtomicLong(1);

	/** 管理用户登录历史 **/
	public static AtomicLong SYS_LOGIN_HIS = new AtomicLong(1);

	/** 管理用户角色 **/
	public static AtomicLong SYS_USR_ROLE = new AtomicLong(1);

	/** 消费用户登录历史 **/
	public static AtomicLong CONSUMER_LOGIN_HIS = new AtomicLong(1);

	/** 消费用户角色 **/
	public static AtomicLong CONSUMER__USR_ROLE = new AtomicLong(1);

	/** 消费用户角色 **/
	public static AtomicLong CLAP_USE_STORE = new AtomicLong(1);

	/** 消费用户银行卡 **/
	public static AtomicLong CONSUMER_USER_BANK_CARD = new AtomicLong(1);

	/** 券 **/
	public static AtomicLong COUPON = new AtomicLong(1);

	/** 券购买 **/
	public static AtomicLong COUPON_BUY = new AtomicLong(1);

	/** 商户用户角色 **/
	public static AtomicLong MERCHANT_ROLE = new AtomicLong(1);

	/** 商户登录历史 **/
	public static AtomicLong MERCHANT_USER_LOGIN_HIS = new AtomicLong(1);

	/** 商户银行卡 **/
	public static AtomicLong MERCHANT_USER_BANK_CARD = new AtomicLong(1);

	/** 政府用户角色 **/
	public static AtomicLong GOV_ROLE = new AtomicLong(1);

	/** 政府登录历史 **/
	public static AtomicLong GOV_USER_LOGIN_HIS = new AtomicLong(1);

	/** 日账单 **/
	public static AtomicLong DAILY_BILL = new AtomicLong(1);
	/** 日账单消费明细 **/
	public static AtomicLong DAILY_BILL_CONSUMPTION_DETAIL = new AtomicLong(1);

	/** 日账单转账明细 **/
	public static AtomicLong DAILY_BILL_TRANSFER_DETAIL = new AtomicLong(1);

	/** 月账单 **/
	public static AtomicLong MONTH_BILL = new AtomicLong(1);

	/** 月账单消费明细 **/
	public static AtomicLong MONTH_BILL_CONSUMPTION_DETAIL = new AtomicLong(1);

	/** 月账单转账明细 **/
	public static AtomicLong MONTH_BILL_TRANSFER_DETAIL = new AtomicLong(1);

	/** 会计记账 **/
	public static AtomicLong BOOKKEEPKING = new AtomicLong(1);

	/** 支付应用 **/
	public static AtomicLong PAY_APP = new AtomicLong(1);

	/** 支付划价 **/
	public static AtomicLong PAY_CALC_PRICE = new AtomicLong(1);

	/** 支付规则 **/
	public static AtomicLong PAY_RULE = new AtomicLong(1);

	/** 对账批次 **/
	public static AtomicLong RECONCILIATION_BATCH = new AtomicLong(1);

	/** 差错处理 **/
	public static AtomicLong ERROR_HANDLING = new AtomicLong(1);

	/** 通知 **/
	public static AtomicLong NOTICE = new AtomicLong(1);

	/** app **/
	public static AtomicLong APP = new AtomicLong(1);

	/** 任务类型 **/
	public static AtomicLong TASK_TYPE = new AtomicLong(1);

	/** 任务 **/
	public static AtomicLong TASK = new AtomicLong(1);

	/** 任务 **/
	public static AtomicLong TASK_INSTANCE = new AtomicLong(1);

	/** 风险日志 **/
	public static AtomicLong FRAULT_INFO = new AtomicLong(1);

	/** 告警信息 **/
	public static AtomicLong ALARM_INFO = new AtomicLong(1);

	/** 消费用户家庭 **/
	public static AtomicLong CONSUMER_FAMILY = new AtomicLong(1);

	/** 消费用户家庭券 **/
	public static AtomicLong CONSUMER_FAMILY_COUPON = new AtomicLong(1);

	/** 风险模型 **/
	public static AtomicLong FRAULT_MODEL = new AtomicLong(1);

	/** 风险处理 **/
	public static AtomicLong FRAULT_PROCESS = new AtomicLong(1);

	/** 风险处理日志 **/
	public static AtomicLong FRAULT_PROCESS_INFO = new AtomicLong(1);

	/** 风险处理结果 **/
	public static AtomicLong FRAULT_PROCESS_RESULT = new AtomicLong(1);

	/** 系统日志 **/
	public static AtomicLong SYS_LOG = new AtomicLong(1);
	/** 机器编号**/
	public static AtomicLong SHELL_PC = new AtomicLong(1);

	/** 可疑IP地址 **/
	public static AtomicLong FRAULT_IP_ADDR = new AtomicLong(1);

	/** 可疑银行卡号 **/
	public static AtomicLong FRAULT_BANK_CARD = new AtomicLong(1);

	/** 可疑身份证号 **/
	public static AtomicLong FRAULT_ID_NUMBER = new AtomicLong(1);

	/** 可疑手机号码 **/
	public static AtomicLong FRAULT_PHONE_NUMBER = new AtomicLong(1);

	/** 可疑用户 **/
	public static AtomicLong FRAULT_CONSUMER = new AtomicLong(1);

	/** 可疑商户 **/
	public static AtomicLong FRAULT_MERCHANT = new AtomicLong(1);

	/** 消费用户黑名单 **/
	public static AtomicLong CONSUMER_BLACK_NUMBER = new AtomicLong(1);

	/** 消费用户黑名单历史 **/
	public static AtomicLong CONSUMER_BLACK_NUMBER_HIS = new AtomicLong(1);
	/** 商户部门 **/
	public static AtomicLong MERCHANT_DEPARTMENT = new AtomicLong(1);
	/** 商户职务 **/
	public static AtomicLong MERCHANT_POSITION = new AtomicLong(1);

	/** 用户家庭组 **/
	public static AtomicLong CONSUMER_FAMILY_GROUP = new AtomicLong(1);

	/** 商户员工登录历史 **/
	public static AtomicLong MERCHANT_EMPLOYEE_LOGIN_HIS = new AtomicLong(1);

	/** 商户黑名单历史 **/
	public static AtomicLong MERCHANT_BLACK_NUMBER_HIS = new AtomicLong(1);
	////////////////////////////////// 以下是共同方法/////////////////////////////////////////

	/** 消费用户编号 **/
	public static String generateCONSUMER_USER_ID() {
		return generateUSER(CONSUMER_USER_ID, "10");
	}

	/** 商户户编号 **/
	public static String generateMERCHANT_USER_ID() {
		return generateUSER(MERCHANT_USER_ID, "20");
	}

	/** 政府用户编号 **/
	public static String generateGOV_USER_ID() {
		return generateUSER(GOV_USER_ID, "80");
	}

	/** 系统用户编号 **/
	public static String generateSYS_USER_ID() {
		return generateUSER(SYS_USER_ID, "90");
	}

	/** 通用规则1编号 seq长度6 总长 20 **/
	public static String generateCommOne(AtomicLong SEQ) {
		return generateComm(SEQ, 6);
	}

	/** 通用规则2编号 seq长度10 24 **/
	public static String generateCommTwo(AtomicLong SEQ) {
		return generateComm(SEQ, 10);
	}

	private static String generateUSER(AtomicLong SEQ, String type) {
		return type + generateCommTwo(SEQ);
	}

	private static String generateComm(AtomicLong SEQ, int len) {
		String timeStr = getTimeString();
		String xulie = String.valueOf(SEQ.addAndGet(1));
		if (len == 10) {
			if (SEQ.get() > 99999999l) {
				SEQ.set(1);
				xulie = String.valueOf(SEQ.get());
			}
		} else if (len == 6) {
			if (SEQ.get() > 9999l) {
				SEQ.set(1);
				xulie = String.valueOf(SEQ.get());
			}
		}
		String id = timeStr + "000000000000".substring(0, len - 2 - xulie.length()) + xulie + SERVER_CODE;
		return id;
	}

	private static String getTimeString() {
		Date d = Calendar.getInstance().getTime();
		long time = d.getTime();
		String timePattren = "yyyyMMddHHmmss";
		Date date = new Date(time);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timePattren);
		return simpleDateFormat.format(date);
	}
}
