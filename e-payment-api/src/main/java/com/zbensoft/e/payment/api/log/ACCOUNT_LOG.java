package com.zbensoft.e.payment.api.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class ACCOUNT_LOG {

	private static Logger log = LoggerFactory.getLogger(ACCOUNT_LOG.class);

	public static void DEBUG(String msg) {
		log.debug(msg);
	}

	//
	// public static void INFO(String msg) {
	// log.info(msg);
	// }

	public static void WARN(String msg) {
		log.warn(msg);
	}

	public static void ERROR(String msg) {
		log.error(msg);
	}

	public static void ERROR(String msg, Exception e) {
		log.error(msg, e);
	}

	public static void INFO(String userId, Long recvAmount, Long start, Long end, Object ob) {
		try {
			if (ob == null) {
				log.info(String.format("%s : %s + (%s) = %s, now=%s,info=%s", userId, start, recvAmount, end, DoubleUtil.format(DoubleUtil.redisLongToDouble(end)), ""));
			} else {
				if (ob instanceof TradeInfo) {
					log.info(String.format("%s : %s + (%s) = %s, now=%s, info=%s", userId, start, recvAmount, end, DoubleUtil.format(DoubleUtil.redisLongToDouble(end)), "TradeSeq=" + ((TradeInfo) ob).getTradeSeq()));
				} else if (ob instanceof String) {
					log.info(String.format("%s : %s + (%s) = %s, now=%s, info=%s", userId, start, recvAmount, end, DoubleUtil.format(DoubleUtil.redisLongToDouble(end)), ob));
				} else {
					log.info(String.format("%s : %s + (%s) = %s, now=%s, info=%s", userId, start, recvAmount, end, DoubleUtil.format(DoubleUtil.redisLongToDouble(end)), JSONObject.toJSONString(ob)));
				}
			}
			// if (ob == null) {
			// log.info(String.format("%s : %s + (%s) = %s, %s,info=%s", userId, DoubleUtil.format(start), DoubleUtil.format(recvAmount), DoubleUtil.format(end), end, ""));
			// } else {
			// if (ob instanceof TradeInfo) {
			// log.info(String.format("%s : %s + (%s) = %s, %s, info=%s", userId, DoubleUtil.format(start), DoubleUtil.format(recvAmount), DoubleUtil.format(end), end, "TradeSeq="
			// + ((TradeInfo) ob).getTradeSeq()));
			// } else if (ob instanceof String) {
			// log.info(String.format("%s : %s + (%s) = %s, %s, info=%s", userId, DoubleUtil.format(start), DoubleUtil.format(recvAmount), DoubleUtil.format(end), end, ob));
			// } else {
			// log.info(String.format("%s : %s + (%s) = %s, %s, info=%s", userId, DoubleUtil.format(start), DoubleUtil.format(recvAmount), DoubleUtil.format(end), end,
			// JSONObject.toJSONString(ob)));
			// }
			// }
		} catch (Exception e) {
			log.error("", e);
		}
	}
}
