package org.e.payment.webservice.client.test.conf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class TestIDGenerate {

	public static AtomicLong TEST_ID = new AtomicLong(1);
	////////////////////////////////// 以下是共同方法/////////////////////////////////////////

	/** 通用规则1编号 seq长度6 总长 20 **/
	public static String generateCommOne(AtomicLong SEQ) {
		return generateComm(SEQ, 6);
	}

	/** 通用规则2编号 seq长度10 24 **/
	public static String generateCommTwo(AtomicLong SEQ) {
		return generateComm(SEQ, 10);
	}

	private static String generateComm(AtomicLong SEQ, int len) {
		String timeStr = getTimeString();
		String xulie = String.valueOf(SEQ.addAndGet(1));
		if (len == 10) {
			if (SEQ.get() > 9999999999l) {
				SEQ.set(1);
				xulie = String.valueOf(SEQ.get());
			}
		} else if (len == 6) {
			if (SEQ.get() > 999999l) {
				SEQ.set(1);
				xulie = String.valueOf(SEQ.get());
			}
		}
		String id = timeStr + "000000000000".substring(0, len - xulie.length()) + xulie;
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
