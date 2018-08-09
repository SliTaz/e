package com.zbensoft.e.payment.api.quartz.job.statistics;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.common.config.SystemConfigKey;

/**
 * 
 * 
 * 1. 早上 9:30 ：0 30 9 * * ?<br/>
 * 2. 中午15:00 ：0 0 15 * * ?<br/>
 * 3. 晚上午19:00 ：0 0 17 * * ?<br/>
 * 
 * 
 * @author xieqiang
 *
 */
public class OnLineUserCountJob implements Job {

//	private static final Logger log = LoggerFactory.getLogger(OnLineUserCountJob.class);

	private static String key = OnLineUserCountJob.class.getName();

	private CloseableHttpClient httpClient = SpringBeanUtil.getBean(CloseableHttpClient.class);
	private RequestConfig config = SpringBeanUtil.getBean(RequestConfig.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		String JOB_ONLINE_USER_COUNT_EMAIL_SUBJECT = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_ONLINE_USER_COUNT_EMAIL_SUBJECT);
		String JOB_ONLINE_USER_COUNT_EMAIL_TO = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_ONLINE_USER_COUNT_EMAIL_TO);
		String JOB_ONLINE_USER_COUNT_EMAIL_BCC = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_ONLINE_USER_COUNT_EMAIL_BCC);
		String JOB_ONLINE_USER_COUNT_API = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_ONLINE_USER_COUNT_API);
		String JOB_ONLINE_USER_COUNT_SERVICE_IP = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_ONLINE_USER_COUNT_SERVICE_IP);

		long count = 0;
		String[] ips = JOB_ONLINE_USER_COUNT_SERVICE_IP.split(",");
		if (ips != null && ips.length > 0) {
			for (String ip : ips) {
				if (StringUtils.isNotEmpty(ip)) {
					count += getOnlineUsercount(JOB_ONLINE_USER_COUNT_API.replaceAll("#ip#", ip));
				}
			}
		}
		String poroxyStatus = getProxyStatus("http://10.11.1.3:8090/Nginxstatus");
		poroxyStatus = poroxyStatus.replaceAll("server accepts handled requests", MailUtil.NEW_LINE + "server accepts handled requests" + MailUtil.NEW_LINE);
		poroxyStatus = poroxyStatus.replaceAll("Reading", MailUtil.NEW_LINE + "Reading");
		poroxyStatus = poroxyStatus.replaceAll("Writing", MailUtil.NEW_LINE + "Writing");
		poroxyStatus = poroxyStatus.replaceAll("Waiting", MailUtil.NEW_LINE + "Waiting");

		String poroxyStatus2 = poroxyStatus.replaceAll("Active connections", "Conexiones activas");
		poroxyStatus2 = poroxyStatus2.replaceAll("server accepts handled requests", "servidor acepta manejado peticiones");
		poroxyStatus2 = poroxyStatus2.replaceAll("Reading", "Leyendo");
		poroxyStatus2 = poroxyStatus2.replaceAll("Writing", "Escritura");
		poroxyStatus2 = poroxyStatus2.replaceAll("Waiting", "Esperando");

		StringBuffer sb = new StringBuffer();
		sb.append("Hola a todos!").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
		sb.append("La cantidad de usuarios en línea ahora es: <font color=\"red\">").append(count).append("</font>").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
		sb.append(poroxyStatus2).append("</font>").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);

		sb.append(poroxyStatus).append("</font>").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);

		try {
			MailUtil.sendEmail(MailUtil.EPAY_FILE_NAME, JOB_ONLINE_USER_COUNT_EMAIL_SUBJECT, JOB_ONLINE_USER_COUNT_EMAIL_TO, null, JOB_ONLINE_USER_COUNT_EMAIL_BCC, sb.toString());
		} catch (Exception e) {
			TASK_LOG.ERROR(String.format("%s sendEmail error", key), e);
		}

		TASK_LOG.INFO(String.format("%s %s", key, sb.toString()));
		TASK_LOG.INFO(String.format("%s End", key));
	}

	private String getProxyStatus(String url) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(config);

		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
					return responseStr;
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
		} finally {
		}
		return "";
	}

	private long getOnlineUsercount(String url) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(config);

		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
					if (responseStr != null && responseStr.contains("body")) {
						JSONObject jsonObjectResponse = new JSONObject();
						jsonObjectResponse = (JSONObject) jsonObjectResponse.parse(responseStr);
						return jsonObjectResponse.getLongValue("body");
					}
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
		} finally {
		}
		return 0;
	}

}
