package com.zbensoft.e.payment.api.quartz.job.statistics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.alarm.util.MailUtil;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.ConsumerUserActiveReportService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserActiveStatisticsService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserActiveReport;
import com.zbensoft.e.payment.db.domain.ConsumerUserActiveStatistics;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.ConsumerUserLoginHis;

/**
 * 用户激活统计任务 0 5 0 * * ?
 * 
 * @author Wang Chenyang
 *
 */
public class BuyerActiveStatisticsJob implements Job {
	Logger logger = LoggerFactory.getLogger(getClass());
	private static String key = "BuyerActiveStatisticsJob";
	private ConsumerUserActiveStatisticsService consumerUserActiveStatisticsService = SpringBeanUtil.getBean(ConsumerUserActiveStatisticsService.class);
	private ConsumerUserActiveReportService consumerUserActiveReportService = SpringBeanUtil.getBean(ConsumerUserActiveReportService.class);
	private ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);
	private ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);
	private ConsumerUserLoginHisService consumerUserLoginHisService = SpringBeanUtil.getBean(ConsumerUserLoginHisService.class);

	String REPORT_BUYER_ACTIVE_STAT_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_FILE_PATH);
	String REPORT_BUYER_ACTIVE_STAT_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_FILE_PREFIX);
	String REPORT_BUYER_ACTIVE_STAT_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_FILE_DATEFORMATE);
	int REPORT_BUYER_ACTIVE_STAT_FILE_DAY_OF_MONTH = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_FILE_DAY_OF_MONTH);

	String REPORT_BUYER_ACTIVE_STAT_EMAIL_SUBJECT = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_EMAIL_SUBJECT);
	String REPORT_BUYER_ACTIVE_STAT_EMAIL_TO = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_EMAIL_TO);
	String REPORT_BUYER_ACTIVE_STAT_EMAIL_BCC = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_EMAIL_BCC);
	int JOB_REPORT_BUYER_ACTIVE_STAT_EMAIL_ENABLE = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_EMAIL_ENABLE);

	int totalUserNum = 0;
	int totalActiveUserNum = 0;
	int todayActiveUserNum = 0;
	int todayUserLoginNum = 0;
	int todayUserLoginTimes = 0;
	String fileName = REPORT_BUYER_ACTIVE_STAT_FILE_PREFIX + DateUtil.convertDateToString(DateUtil.addDate(new Date(), 0, 3), REPORT_BUYER_ACTIVE_STAT_FILE_DATEFORMATE) + ".txt";

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		long startTime = System.currentTimeMillis();
		boolean isSucc = false;
		isSucc = doStatistics();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s doStatistics is error", key));
			throw new JobExecutionException(String.format("%s doStatistics is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s doStatistics is Success", key));
		}
		if (JOB_REPORT_BUYER_ACTIVE_STAT_EMAIL_ENABLE == 1) {
			isSucc = doSendEmail();
			if (!isSucc) {
				TASK_LOG.INFO(String.format("%s doSendEmail is error", key));
				throw new JobExecutionException(String.format("%s doSendEmail is fail", key));
			} else {
				TASK_LOG.INFO(String.format("%s doSendEmail is Success", key));
			}
		}
		long useTime = (System.currentTimeMillis() - startTime) / 1000;
		TASK_LOG.INFO(String.format("%s End -------------------- in %d s", key, useTime));
	}

	private boolean doStatistics() {
		boolean isSucc = false;
		isSucc = countUser();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s countUser is error", key));
			MessageAlarmFactory.getInstance().add(String.format("%s countUser is error", key));
		}
		isSucc = countActiveUser();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s countActiveUser is error", key));
			MessageAlarmFactory.getInstance().add(String.format("%s countActiveUser is error", key));
		}
		isSucc = countTodayActiveUser();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s countTodayActiveUser is error", key));
			MessageAlarmFactory.getInstance().add(String.format("%s countTodayActiveUser is error", key));
		}
		isSucc = countTodayUserLogin();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s countTodayUserLogin is error", key));
			MessageAlarmFactory.getInstance().add(String.format("%s countTodayUserLogin is error", key));
		}
		isSucc = countTodayLoginUserTimes();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s countTodayLoginUserTimes is error", key));
			MessageAlarmFactory.getInstance().add(String.format("%s countTodayLoginUserTimes is error", key));
		}
		isSucc = statiticsToDB();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s statiticsToDB is error", key));
			MessageAlarmFactory.getInstance().add(String.format("%s statiticsToDB is error", key));
		}

		isSucc = reportToDB();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s reportToDB is error", key));
			MessageAlarmFactory.getInstance().add(String.format("%s reportToDB is error", key));
		}

		return true;
	}

	private boolean doSendEmail() {
		// La cantidad de usuarios totales ahora es: 114
		// La cantidad de usuarios activos totales ahora es: 12
		// El número de usuarios activos de hoy es: 0
		// El número de inicio de sesión de usuarios durante el dia es: 0
		// El tiempos del inicio de sesión de usuario durante el dia es: 0

		StringBuffer sb = new StringBuffer();
		sb.append("Hola a todos!").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
		sb.append("Estadistica del comprador").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
		sb.append("La cantidad de usuarios totales ahora es: <font color=\"red\">").append(totalUserNum).append("</font>").append(MailUtil.NEW_LINE);
		sb.append("La cantidad de usuarios activos totales ahora es: ").append(totalActiveUserNum).append("</font>").append(MailUtil.NEW_LINE);
		sb.append("El número de usuarios activos de hoy es: ").append(todayActiveUserNum).append("</font>").append(MailUtil.NEW_LINE);
		sb.append("El número de inicio de sesión de usuarios durante el dia es: ").append(todayUserLoginNum).append("</font>").append(MailUtil.NEW_LINE);
		sb.append("El tiempos del inicio de sesión de usuario durante el dia es: ").append(todayUserLoginTimes).append("</font>").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);

		String englishStr = sb.toString();
		englishStr = englishStr.replace("Hola a todos!", "");
		englishStr = englishStr.replace("Estadistica del comprador", "Buyer Statistics");
		englishStr = englishStr.replace("La cantidad de usuarios totales ahora es:", "The number of total users now is:");
		englishStr = englishStr.replace("La cantidad de usuarios activos totales ahora es:", "The number of total active users is now:");
		englishStr = englishStr.replace("El número de usuarios activos de hoy es:", "The number of active users today is:");
		englishStr = englishStr.replace("El número de inicio de sesión de usuarios durante el dia es:", "The today user login number is:");
		englishStr = englishStr.replace("El tiempos del inicio de sesión de usuario durante el dia es:", "The today user login times are:");

		try {
			MailUtil.sendEmail(MailUtil.EPAY_FILE_NAME, REPORT_BUYER_ACTIVE_STAT_EMAIL_SUBJECT, REPORT_BUYER_ACTIVE_STAT_EMAIL_TO, null, REPORT_BUYER_ACTIVE_STAT_EMAIL_BCC, sb.toString() + englishStr);
		} catch (Exception e) {
			TASK_LOG.ERROR(String.format("%s sendEmail error", key), e);
			return false;
		}
		return true;
	}

	private boolean reportToDB() {
		try {
			if (DateUtil.isDayOfMonth(REPORT_BUYER_ACTIVE_STAT_FILE_DAY_OF_MONTH)) {
				ConsumerUserActiveReport report = new ConsumerUserActiveReport();
				report.setStatisticsTime(DateUtil.convertDateToString(DateUtil.addDate(new Date(), -1, 2), DateUtil.DATE_FORMAT_TWENTY_THREE));
				report.setTotalActiveUserNum((long) totalActiveUserNum);
				if (writeFile()) {
					report.setFileAddress(fileName);
					consumerUserActiveReportService.insert(report);
				}
			}
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}

	}

	private boolean writeFile() {

		BufferedWriter bufferedWriter = null;
		try {
			File filePath = new File(REPORT_BUYER_ACTIVE_STAT_FILE_PATH);
			File file = new File(REPORT_BUYER_ACTIVE_STAT_FILE_PATH + fileName);// 指定要写入的文件
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			if (!file.exists()) {// 如果文件不存在则创建
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}

			// 获取该文件的缓冲输出流
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(REPORT_BUYER_ACTIVE_STAT_FILE_PATH + fileName), StandardCharsets.UTF_8));

			int start = 0;
			int REPORT_BUYER_ACTIVE_STAT_FILE_WRITE_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_REPORT_BUYER_ACTIVE_STAT_FILE_WRITE_ONETIME);
			List<ConsumerUser> getDbConsumerUserList = new ArrayList<>();
			ConsumerUser consumerUserSer = new ConsumerUser();
			consumerUserSer.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT);
			while (true) {
				int pageNum = PageHelperUtil.getPageNum(String.valueOf(start), String.valueOf(REPORT_BUYER_ACTIVE_STAT_FILE_WRITE_ONETIME));
				int pageSize = PageHelperUtil.getPageSize(String.valueOf(start), String.valueOf(REPORT_BUYER_ACTIVE_STAT_FILE_WRITE_ONETIME));
				PageHelper.startPage(pageNum, pageSize);
				getDbConsumerUserList = consumerUserService.selectPage(consumerUserSer);
				if (getDbConsumerUserList != null) {
					for (ConsumerUser consumerUser : getDbConsumerUserList) {
						ConsumerUserClap consumerUserClapResult = consumerUserClapService.selectByUser(consumerUser.getUserId());
						if (consumerUserClapResult != null) {
							bufferedWriter.write(getReportString(consumerUserClapResult));
							bufferedWriter.newLine();// 表示换行
						} else {
							TASK_LOG.INFO(String.format("%s ####### Can't find clap info of this user: %s", key, consumerUser.getUserId()));
						}

					}
					start += getDbConsumerUserList.size();
				}

				if (getDbConsumerUserList == null || getDbConsumerUserList.size() != REPORT_BUYER_ACTIVE_STAT_FILE_WRITE_ONETIME) {
					return true;
				}
			}

		} catch (Exception e) {
			logger.error(String.format("%s wirteFile Exception", key), e);
			TASK_LOG.ERROR(String.format("%s wirteFile Exception", key), e);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				logger.error(String.format("%s flush buffereWriter Exception", key), e);
				TASK_LOG.ERROR(String.format("%s flush buffereWriter Exception", key), e);
			}
		}

		return false;
	}

	private boolean statiticsToDB() {
		try {
			ConsumerUserActiveStatistics newStatistics = new ConsumerUserActiveStatistics();
			newStatistics.setStatisticsTime(DateUtil.convertDateToString(DateUtil.addDate(new Date(), -1, 3), DateUtil.DATE_FORMAT_ONE));// 主键日期为昨天日期
			newStatistics.setTotalUserNum((long) totalUserNum);
			newStatistics.setTotalActiveUserNum((long) totalActiveUserNum);
			newStatistics.setActiveUserNum((long) todayActiveUserNum);
			newStatistics.setLoginUserNum((long) todayUserLoginNum);
			newStatistics.setLoginUserTimes((long) todayUserLoginTimes);
			consumerUserActiveStatisticsService.insert(newStatistics);
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}
	}

	private boolean countTodayLoginUserTimes() {
		try {
			ConsumerUserLoginHis consumerUserLoginHisSer = new ConsumerUserLoginHis();
			consumerUserLoginHisSer.setLoginTimeStart(DateUtil.getDayStartEndTime(-1, true, DateUtil.DATE_FORMAT_FIVE));
			consumerUserLoginHisSer.setLoginTimeEnd(DateUtil.getDayStartEndTime(-1, false, DateUtil.DATE_FORMAT_FIVE));
			todayUserLoginTimes = consumerUserLoginHisService.count(consumerUserLoginHisSer);
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}
	}

	private boolean countTodayUserLogin() {
		try {
			ConsumerUserLoginHis consumerUserLoginHisSer = new ConsumerUserLoginHis();
			consumerUserLoginHisSer.setLoginTimeStart(DateUtil.getDayStartEndTime(-1, true, DateUtil.DATE_FORMAT_FIVE));
			consumerUserLoginHisSer.setLoginTimeEnd(DateUtil.getDayStartEndTime(-1, false, DateUtil.DATE_FORMAT_FIVE));
			todayUserLoginNum = consumerUserLoginHisService.countDistin(consumerUserLoginHisSer);
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}

	}

	private boolean countTodayActiveUser() {
		try {
			ConsumerUserActiveStatistics yesterDaySstatisticsResult = consumerUserActiveStatisticsService.selectByPrimaryKey(getYesterday());
			if (yesterDaySstatisticsResult != null && yesterDaySstatisticsResult.getTotalActiveUserNum() != null) {
				todayActiveUserNum = totalActiveUserNum - yesterDaySstatisticsResult.getTotalActiveUserNum().intValue();
			}
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}

	}

	private String getYesterday() {
		Date yesterdayDate = DateUtil.addDate(new Date(), -2, 3);
		return DateUtil.convertDateToString(yesterdayDate, DateUtil.DATE_FORMAT_ONE);
	}

	private boolean countActiveUser() {
		try {
			ConsumerUser consumerUserSer = new ConsumerUser();
			consumerUserSer.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT);
			totalActiveUserNum = consumerUserService.count(consumerUserSer);
			if (totalUserNum > 0) {
				TASK_LOG.INFO(String.format("%s countActiveUser = %s", key, totalUserNum));
			}
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}

	}

	private boolean countUser() {
		try {
			totalUserNum = consumerUserService.count(new ConsumerUser());
			if (totalUserNum > 0) {
				RedisUtil.set_COUNT_BUYER(totalUserNum);
				TASK_LOG.INFO(String.format("%s CountBuyer = %s", key, totalUserNum));
			}
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}
	}

	public String getReportString(ConsumerUserClap consumerUserClap) {
		StringBuffer sb = new StringBuffer();
		if (consumerUserClap.getIdNumber() != null) {
			sb.append(getRel(consumerUserClap.getIdNumber())).append(";");
			sb.append(getRel(consumerUserClap.getName1())).append(";");
			sb.append(getRel(consumerUserClap.getName2())).append(";");
			sb.append(getRel(consumerUserClap.getLastName1())).append(";");
			sb.append(getRel(consumerUserClap.getLastName2())).append(";");
			if (consumerUserClap.getSex() != null) {
				if (consumerUserClap.getSex() == 1) {
					sb.append("f").append(";");
				} else {
					sb.append("m").append(";");
				}
			} else {
				sb.append(";");
			}
			if (consumerUserClap.getDatebirth() != null) {
				sb.append(DateUtil.convertDateToString(consumerUserClap.getDatebirth(), DateUtil.DATE_FORMAT_TWENTY_TWO)).append(";");
			} else {
				sb.append(";");
			}
			sb.append(getRel(consumerUserClap.getFamilyId())).append(";");
			sb.append(getRel(consumerUserClap.getClapNo())).append(";");
			sb.append(getRel(consumerUserClap.getClapSeqNo())).append(";");
			sb.append(getRel(consumerUserClap.getClapStoreNo())).append(";");
			return sb.toString();
		}
		return "";
	}

	private Object getRel(String value) {
		if (value != null) {
			return value;
		}
		return "";
	}

}
