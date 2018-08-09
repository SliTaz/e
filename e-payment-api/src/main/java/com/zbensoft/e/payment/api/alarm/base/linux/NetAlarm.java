package com.zbensoft.e.payment.api.alarm.base.linux;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.alarm.Alarm;
import com.zbensoft.e.payment.api.alarm.util.AlarmUtil;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

public class NetAlarm extends Alarm {
	
	private static final Logger log = LoggerFactory.getLogger(NetAlarm.class);


	private int alarm_net = 0;
	private int alarm_net_band_width = 1000;// 网口带宽,Mbps

	@Override
	public void initSetup() {
		alarm_net = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_NET);
		alarm_net_band_width = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ALARM_NET_BAND_WIDTH);

	}

	@Override
	public boolean haveAlarm() {
		try {
			float value = getNet();
			if (value > alarm_net) {
				int alarmtTimeSec = 0;
				if (isFirstCount) {
					alarmtTimeSec = message.getAlarmTimeSec();
					if (startTime == 0) {
						startTime = System.currentTimeMillis();
					}
				} else {
					alarmtTimeSec = message.getAlarmFrequencyTimeSec();
				}
				if (System.currentTimeMillis() - startTime > alarmtTimeSec * 1000) {
					startTime = System.currentTimeMillis();
					isFirstCount = false;
					message.setAlarm(true);
					StringBuffer sb = new StringBuffer();
					sb.append(AlarmUtil.getIPInfo());
					sb.append("net out max num:").append(alarm_net).append(MailUtil.NEW_LINE);
					sb.append("Please check the server!").append(MailUtil.NEW_LINE);
					message.setContent(sb.toString());
					return true;
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
		message.setAlarm(false);
		message.setContent(null);
		return false;
	}

	private float getNet() {
		float netUsage = 0.0f;
		Process pro1, pro2;
		Runtime r = Runtime.getRuntime();
		try {
			String command = "cat /proc/net/dev";
			// 第一次采集流量数据
			long startTime = System.currentTimeMillis();
			pro1 = r.exec(command);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			String line = null;
			long inSize1 = 0, outSize1 = 0;
			while ((line = in1.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("eth0")) {
					String[] temp = line.split("\\s+");
					inSize1 = Long.parseLong(temp[0].substring(5)); // Receive bytes,单位为Byte
					outSize1 = Long.parseLong(temp[8]); // Transmit bytes,单位为Byte
					break;
				}
			}
			in1.close();
			pro1.destroy();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				log.error("", e);
			}
			// 第二次采集流量数据
			long endTime = System.currentTimeMillis();
			pro2 = r.exec(command);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(pro2.getInputStream()));
			long inSize2 = 0, outSize2 = 0;
			while ((line = in2.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("eth0")) {
					String[] temp = line.split("\\s+");
					inSize2 = Long.parseLong(temp[0].substring(5));
					outSize2 = Long.parseLong(temp[8]);
					break;
				}
			}
			if (inSize1 != 0 && outSize1 != 0 && inSize2 != 0 && outSize2 != 0) {
				float interval = (float) (endTime - startTime) / 1000;
				// 网口传输速度,单位为bps
				float curRate = (float) (inSize2 - inSize1 + outSize2 - outSize1) * 8 / (1000000 * interval);
				netUsage = curRate / alarm_net_band_width;
			}
			in2.close();
			pro2.destroy();
		} catch (Exception e) {
			log.error("", e);
		}

		return netUsage;
	}

}
