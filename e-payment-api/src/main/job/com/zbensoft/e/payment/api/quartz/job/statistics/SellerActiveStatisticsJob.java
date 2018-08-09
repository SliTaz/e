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
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.MerchantActiveReportService;
import com.zbensoft.e.payment.api.service.api.MerchantActiveStatisticsService;
import com.zbensoft.e.payment.api.service.api.MerchantUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.MerchantActiveReport;
import com.zbensoft.e.payment.db.domain.MerchantActiveStatistics;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserLoginHis;

/**
 * 用户激活统计任务 0 5 0 * * ?
 * 
 * @author Wang Chenyang
 *
 */
public class SellerActiveStatisticsJob implements Job {
	Logger logger = LoggerFactory.getLogger(getClass());
	private static String key = "SellerActiveStatisticsJob";
	private MerchantActiveStatisticsService merchantActiveStatisticsService = SpringBeanUtil.getBean(MerchantActiveStatisticsService.class);
	private MerchantActiveReportService merchantActiveReportService = SpringBeanUtil.getBean(MerchantActiveReportService.class);
	private MerchantUserService merchantUserService = SpringBeanUtil.getBean(MerchantUserService.class);
	private MerchantUserLoginHisService merchantUserLoginHisService = SpringBeanUtil.getBean(MerchantUserLoginHisService.class);

	String REPORT_SELLER_ACTIVE_STAT_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_FILE_PATH);
	String REPORT_SELLER_ACTIVE_STAT_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_FILE_PREFIX);
	String REPORT_SELLER_ACTIVE_STAT_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_FILE_DATEFORMATE);
	int REPORT_SELLER_ACTIVE_STAT_FILE_DAY_OF_MONTH = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_FILE_DAY_OF_MONTH);
	String REPORT_SELLER_ACTIVE_STAT_EMAIL_SUBJECT = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_EMAIL_SUBJECT);
	String REPORT_SELLER_ACTIVE_STAT_EMAIL_TO = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_EMAIL_TO);
	String REPORT_SELLER_ACTIVE_STAT_EMAIL_BCC = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_EMAIL_BCC);
	int JOB_REPORT_SELLER_ACTIVE_STAT_EMAIL_ENABLE = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_EMAIL_ENABLE);

	int totalUserNum = 0;
	int totalActiveUserNum = 0;
	int todayActiveUserNum = 0;
	int todayUserLoginNum = 0;
	int todayUserLoginTimes = 0;
	String fileName = REPORT_SELLER_ACTIVE_STAT_FILE_PREFIX + DateUtil.convertDateToString(DateUtil.addDate(new Date(), 0, 3), REPORT_SELLER_ACTIVE_STAT_FILE_DATEFORMATE) + ".txt";

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

		if (JOB_REPORT_SELLER_ACTIVE_STAT_EMAIL_ENABLE == 1) {
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
		}
		isSucc = reportToDB();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s reportToDB is error", key));
			MessageAlarmFactory.getInstance().add(String.format("%s reportToDB is error", key));
		}
		return true;
	}

	private boolean doSendEmail() {
		StringBuffer sb = new StringBuffer();
		sb.append("Hola a todos!").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
		sb.append("Estadistica del vendedor").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);
		sb.append("La cantidad total de vendedores ahora es: <font color=\"red\">").append(totalUserNum).append("</font>").append(MailUtil.NEW_LINE);
		sb.append("La cantidad de vendedores activos totales ahora es: ").append(totalActiveUserNum).append("</font>").append(MailUtil.NEW_LINE);
		sb.append("La cantidad de vendedores activos hoy en día es: ").append(todayActiveUserNum).append("</font>").append(MailUtil.NEW_LINE);
		sb.append("El número de inicio de sesión actual del vendedor es: ").append(todayUserLoginNum).append("</font>").append(MailUtil.NEW_LINE);
		sb.append("Los tiempos de inicio de sesión del vendedor actual son: ").append(todayUserLoginTimes).append("</font>").append(MailUtil.NEW_LINE).append(MailUtil.NEW_LINE);

		String englishStr = sb.toString();
		englishStr = englishStr.replace("Hola a todos!", "");
		englishStr = englishStr.replace("Estadistica del vendedor", "Seller Statistics");
		englishStr = englishStr.replace("La cantidad total de vendedores ahora es:", "The number of total sellers now is:");
		englishStr = englishStr.replace("La cantidad de vendedores activos totales ahora es:", "The number of total active sellers is now:");
		englishStr = englishStr.replace("La cantidad de vendedores activos hoy en día es:", "The number of active sellers today is:");
		englishStr = englishStr.replace("El número de inicio de sesión actual del vendedor es:", "The today sellers login number is:");
		englishStr = englishStr.replace("Los tiempos de inicio de sesión del vendedor actual son:", "The today sellers login times are:");
		try {
			MailUtil.sendEmail(MailUtil.EPAY_FILE_NAME, REPORT_SELLER_ACTIVE_STAT_EMAIL_SUBJECT, REPORT_SELLER_ACTIVE_STAT_EMAIL_TO, null, REPORT_SELLER_ACTIVE_STAT_EMAIL_BCC, sb.toString() + englishStr);
		} catch (Exception e) {
			TASK_LOG.ERROR(String.format("%s sendEmail error", key), e);
			return false;
		}
		return true;
	}

	private boolean reportToDB() {
		try {
			if (DateUtil.isDayOfMonth(REPORT_SELLER_ACTIVE_STAT_FILE_DAY_OF_MONTH)) {
				MerchantActiveReport report = new MerchantActiveReport();
				report.setStatisticsTime(DateUtil.convertDateToString(DateUtil.addDate(new Date(), -1, 2), DateUtil.DATE_FORMAT_TWENTY_THREE));
				report.setTotalActiveUserNum((long) totalActiveUserNum);
				if (writeFile()) {
					report.setFileAddress(fileName);
					merchantActiveReportService.insert(report);
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
			File filePath = new File(REPORT_SELLER_ACTIVE_STAT_FILE_PATH);
			File file = new File(REPORT_SELLER_ACTIVE_STAT_FILE_PATH + fileName);// 指定要写入的文件
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
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(REPORT_SELLER_ACTIVE_STAT_FILE_PATH + fileName), StandardCharsets.UTF_8));

			int start = 0;
			int REPORT_SELLER_ACTIVE_STAT_FILE_WRITE_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_REPORT_SELLER_ACTIVE_STAT_FILE_WRITE_ONETIME);
			List<MerchantUser> getDbMerchantUserList = new ArrayList<>();
			MerchantUser merchantUserSer = new MerchantUser();
			merchantUserSer.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT);
			while (true) {

				int pageNum = PageHelperUtil.getPageNum(String.valueOf(start), String.valueOf(REPORT_SELLER_ACTIVE_STAT_FILE_WRITE_ONETIME));
				int pageSize = PageHelperUtil.getPageSize(String.valueOf(start), String.valueOf(REPORT_SELLER_ACTIVE_STAT_FILE_WRITE_ONETIME));
				PageHelper.startPage(pageNum, pageSize);
				getDbMerchantUserList = merchantUserService.selectPage(merchantUserSer);
				if (getDbMerchantUserList != null) {

					for (MerchantUser merchantUser : getDbMerchantUserList) {
						if (merchantUser.getReportString() != null) {
							bufferedWriter.write(merchantUser.getReportString());
							bufferedWriter.newLine();// 表示换行
						} else {
							TASK_LOG.INFO(String.format("%s get merchantUser failed, 'idNumber' is null, UesrId= %s", key, merchantUser.getUserId()));
						}

					}
				}
				start += REPORT_SELLER_ACTIVE_STAT_FILE_WRITE_ONETIME;

				if (getDbMerchantUserList == null || getDbMerchantUserList.size() != REPORT_SELLER_ACTIVE_STAT_FILE_WRITE_ONETIME) {
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
			MerchantActiveStatistics newStatistics = new MerchantActiveStatistics();
			newStatistics.setStatisticsTime(DateUtil.convertDateToString(DateUtil.addDate(new Date(), -1, 3), DateUtil.DATE_FORMAT_ONE));// 主键日期为昨天日期
			newStatistics.setTotalUserNum((long) totalUserNum);
			newStatistics.setTotalActiveUserNum((long) totalActiveUserNum);
			newStatistics.setActiveUserNum((long) todayActiveUserNum);
			newStatistics.setLoginUserNum((long) todayUserLoginNum);
			newStatistics.setLoginUserTimes((long) todayUserLoginTimes);
			merchantActiveStatisticsService.insert(newStatistics);
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}
	}

	private boolean countTodayLoginUserTimes() {
		try {
			MerchantUserLoginHis consumerUserLoginHisSer = new MerchantUserLoginHis();
			consumerUserLoginHisSer.setLoginTimeStart(DateUtil.getDayStartEndTime(-1, true, DateUtil.DATE_FORMAT_FIVE));
			consumerUserLoginHisSer.setLoginTimeEnd(DateUtil.getDayStartEndTime(-1, false, DateUtil.DATE_FORMAT_FIVE));
			todayUserLoginTimes = merchantUserLoginHisService.count(consumerUserLoginHisSer);
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}
	}

	private boolean countTodayUserLogin() {
		try {
			MerchantUserLoginHis consumerUserLoginHisSer = new MerchantUserLoginHis();
			consumerUserLoginHisSer.setLoginTimeStart(DateUtil.getDayStartEndTime(-1, true, DateUtil.DATE_FORMAT_FIVE));
			consumerUserLoginHisSer.setLoginTimeEnd(DateUtil.getDayStartEndTime(-1, false, DateUtil.DATE_FORMAT_FIVE));
			todayUserLoginNum = merchantUserLoginHisService.countDistin(consumerUserLoginHisSer);
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}

	}

	private boolean countTodayActiveUser() {
		try {
			MerchantActiveStatistics yesterDaySstatisticsResult = merchantActiveStatisticsService.selectByPrimaryKey(getYesterday());
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
			MerchantUser consumerUserSer = new MerchantUser();
			consumerUserSer.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT);
			totalActiveUserNum = merchantUserService.count(consumerUserSer);
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
			totalUserNum = merchantUserService.count(new MerchantUser());
			if (totalUserNum > 0) {
				TASK_LOG.INFO(String.format("%s countUser = %s", key, totalUserNum));
			}
			return true;
		} catch (Exception e) {
			TASK_LOG.ERROR("", e);
			logger.error("", e);
			return false;
		}
	}

	public String getReportString(MerchantUser merchantUser) {
		StringBuffer sb = new StringBuffer();
		if (merchantUser.getIdNumber() != null) {

			sb.append(getRel(merchantUser.getIdNumber())).append(";");
			sb.append(getRel(merchantUser.getUserName())).append(";");
			sb.append(getRel(merchantUser.getClapStoreNo())).append(";");

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
