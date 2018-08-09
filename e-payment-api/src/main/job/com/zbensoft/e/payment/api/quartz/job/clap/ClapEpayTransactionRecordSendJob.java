package com.zbensoft.e.payment.api.quartz.job.clap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.SFTPUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.api.vo.clap.ClapTrade;
import com.zbensoft.e.payment.api.vo.clap.ClapTradeHeader;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 交易数据上传，每天凌晨1点执行。
 * 
 * 0 0 1 * * ?
 * 
 * @author Wang Chenyang
 *
 */
public class ClapEpayTransactionRecordSendJob implements Job {

	private static final Logger log = LoggerFactory.getLogger(ClapEpayTransactionRecordSendJob.class);

	private static String key = "ClapEpayTransactionRecordSendJob";
	private ClapTradeHeader clapTradeHeader = new ClapTradeHeader();
	TradeInfoService tradeInfoService = SpringBeanUtil.getBean(TradeInfoService.class);
	ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);
	MerchantUserService merchantUserService = SpringBeanUtil.getBean(MerchantUserService.class);
	MerchantEmployeeService merchantEmployeeService = SpringBeanUtil.getBean(MerchantEmployeeService.class);
	String JOB_CLAP_UPNLOAD_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_UPNLOAD_FILE_PATH);
	String JOB_CLAP_TRADE_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_TRADE_FILE_PREFIX);
	String JOB_CLAP_TRADE_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_TRADE_FILE_DATEFORMATE);

	String JOB_CLAP_SFTP_USER_NAME = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_USER_NAME);
	String JOB_CLAP_SFTP_IP_ADDRESS = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_IP_ADDRESS);
	int JOB_CLAP_SFTP_PORT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_SFTP_PORT);
	String JOB_CLAP_SFTP_PASSWORD = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SFTP_PASSWORD);
	String JOB_CLAP_TRADE_FILE_SFTP_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_TRADE_FILE_SFTP_PATH);

	int JOB_CLAP_SFTP_TRADE_DAY_BEFORE = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_SFTP_TRADE_DAY_BEFORE);

	String fileName = JOB_CLAP_TRADE_FILE_PREFIX + DateUtil.convertDateToString(DateUtil.addDate(new Date(), JOB_CLAP_SFTP_TRADE_DAY_BEFORE, 3), JOB_CLAP_TRADE_FILE_DATEFORMATE) + ".txt";

	

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		boolean isSucc = true;

		isSucc = getHeader();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s getHeader fail", key));
			throw new JobExecutionException(String.format("%s getHeader is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s getHeader Success", key));
		}
		isSucc = wirteFile();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s wirteFile fail", key));
			throw new JobExecutionException(String.format("%s wirteFile is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s wirteFile Success", key));
		}
		isSucc = ftp();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s ftp upload fail", key));
			throw new JobExecutionException(String.format("%s ftp is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s ftp upload Success", key));
		}

		TASK_LOG.INFO(String.format("%s End", key));
	}

	private boolean wirteFile() {

		BufferedWriter bufferedWriter = null;
		try {
			File file = new File(JOB_CLAP_UPNLOAD_FILE_PATH + fileName);// 指定要写入的文件
			if (!file.getParentFile().exists() && file.getParentFile().isDirectory()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {// 如果文件不存在则创建
				file.createNewFile();
			}
			// 获取该文件的缓冲输出流
			bufferedWriter = new BufferedWriter(new FileWriter(file));

			if (clapTradeHeader != null) {
				bufferedWriter.write(clapTradeHeader.toString());
				bufferedWriter.newLine();// 表示换行
			}
			int start = 0;
			int JOB_CLAP_TRADE_FILE_WRITE_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_TRADE_FILE_WRITE_ONETIME);
			List<TradeInfo> getDbTradeList = new ArrayList<>();
			TradeInfo tradeInfoSer = new TradeInfo();
			tradeInfoSer.setType(MessageDef.TRADE_TYPE.CONSUMPTION);
			tradeInfoSer.setStatus(MessageDef.TRADE_STATUS.SUCC);
			tradeInfoSer.setCreateTimeStartSer(DateUtil.getDayStartEndTime(JOB_CLAP_SFTP_TRADE_DAY_BEFORE, true, DateUtil.DATE_FORMAT_FIVE));
			tradeInfoSer.setCreateTimeEndSer(DateUtil.getDayStartEndTime(JOB_CLAP_SFTP_TRADE_DAY_BEFORE, false, DateUtil.DATE_FORMAT_FIVE));
			while (true) {

				int pageNum = PageHelperUtil.getPageNum(String.valueOf(start), String.valueOf(JOB_CLAP_TRADE_FILE_WRITE_ONETIME));
				int pageSize = PageHelperUtil.getPageSize(String.valueOf(start), String.valueOf(JOB_CLAP_TRADE_FILE_WRITE_ONETIME));
				PageHelper.startPage(pageNum, pageSize);
				getDbTradeList = tradeInfoService.selectByDayLmit(tradeInfoSer);
				if (getDbTradeList != null&&getDbTradeList.size()>0) {
					for (TradeInfo tradeInfo : getDbTradeList) {
						ClapTrade clapTrade = getClapTrade(tradeInfo);
						if (clapTrade != null) {
							bufferedWriter.write(clapTrade.toString());
							bufferedWriter.newLine();// 表示换行
						} else {
							TASK_LOG.INFO(String.format("%s getDbTradeList tradeInfo to clapTrade failed, clapTrade is null. tradeSeq=%s", key, tradeInfo.getTradeSeq()));
						}

					}
					start += getDbTradeList.size();
				}
				if (getDbTradeList == null || getDbTradeList.size() != JOB_CLAP_TRADE_FILE_WRITE_ONETIME) {
					if(start==0){
						TASK_LOG.INFO(String.format("%s getDbTradeList is empty, no data today", key));
					}
					
					return true;
					
				}
				
			}

		} catch (Exception e) {
			log.error(String.format("%s wirteFile Exception", key), e);
			TASK_LOG.ERROR(String.format("%s wirteFile Exception", key), e);
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				log.error(String.format("%s flush buffereWriter Exception", key), e);
				TASK_LOG.ERROR(String.format("%s flush buffereWriter Exception", key), e);
			}
		}

		return false;
	}

	/**
	 * 数据库交易记录转ClapTrad记录
	 * 
	 * @param tradeInfo
	 * @return
	 */
	private ClapTrade getClapTrade(TradeInfo tradeInfo) {
		ClapTrade clapTrade = new ClapTrade(tradeInfo);
		if (tradeInfo != null && tradeInfo.getTradeSeq() != null) {
			try {
				if (tradeInfo.getPayUserId() != null) {// 付款人信息
					ConsumerUserClap consumerUserClap = consumerUserClapService.selectByUser(tradeInfo.getPayUserId());
					if (consumerUserClap != null && consumerUserClap.getClapNo() != null && consumerUserClap.getIdNumber() != null) {
						clapTrade.setBuyerVID(consumerUserClap.getIdNumber());
						clapTrade.setPatrimonyCardId(consumerUserClap.getClapNo());
					}
					if (tradeInfo.getRecvEmployeeUserId() != null) {// 收款人信息，员工
						MerchantEmployee merchantEmployee = merchantEmployeeService.selectByPrimaryKey(tradeInfo.getRecvEmployeeUserId());
						if (merchantEmployee != null && merchantEmployee.getUserId() != null && merchantEmployee.getIdNumber() != null) {
							clapTrade.setSellerVID(merchantEmployee.getIdNumber());
							MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(merchantEmployee.getUserId());
							if (merchantUser != null && merchantUser.getClapStoreNo() != null) {
								clapTrade.setClapStoreNo(merchantUser.getClapStoreNo());
							}
						}
					} else if (tradeInfo.getRecvUserId() != null) {// 收款人信息，商户
						MerchantUser merchantUser = merchantUserService.selectByPrimaryKey(tradeInfo.getRecvUserId());
						if (merchantUser != null && merchantUser.getClapStoreNo() != null && merchantUser.getIdNumber() != null) {
							clapTrade.setSellerVID(merchantUser.getIdNumber());
							clapTrade.setClapStoreNo(merchantUser.getClapStoreNo());
						}
					}
					if (tradeInfo.getPayAmount() != null && tradeInfo.getPayAmount() > 0) {// 金额信息
						clapTrade.setAmount(amountToString(tradeInfo.getPayAmount(), MessageDef.CLAP_AMOUNT.LENGTH, MessageDef.CLAP_AMOUNT.DECIMAL));
					}

				}
			} catch (Exception e) {
				log.error(String.format("%s getClapTrade Exception, TradeSeq = %s", key, tradeInfo.getTradeSeq()), e);
				TASK_LOG.ERROR(String.format("%s getClapTrade Exception, TradeSeq = %s", key, tradeInfo.getTradeSeq()), e);
			}
		}

		return clapTrade;
	}

	/**
	 * 获取文件头信息
	 * 
	 * @return
	 */
	private boolean getHeader() {
		try {
			clapTradeHeader.setGenerateFileDate(DateUtil.convertDateToString(new Date(), DateUtil.DATE_FORMAT_FIVE));
			TradeInfo tradeInfoSer = new TradeInfo();
			tradeInfoSer.setType(MessageDef.TRADE_TYPE.CONSUMPTION);
			tradeInfoSer.setStatus(MessageDef.TRADE_STATUS.SUCC);
			tradeInfoSer.setCreateTimeStartSer(DateUtil.getDayStartEndTime(JOB_CLAP_SFTP_TRADE_DAY_BEFORE, true, DateUtil.DATE_FORMAT_FIVE));
			tradeInfoSer.setCreateTimeEndSer(DateUtil.getDayStartEndTime(JOB_CLAP_SFTP_TRADE_DAY_BEFORE, false, DateUtil.DATE_FORMAT_FIVE));
			int recordsCount = tradeInfoService.countByDay(tradeInfoSer);
			Double totalAmount = tradeInfoService.sumByDay(tradeInfoSer);
			clapTradeHeader.setTotalPackages(String.valueOf(recordsCount));
			String TotalAmountStr = amountToString(totalAmount, MessageDef.CLAP_AMOUNT.LENGTH, MessageDef.CLAP_AMOUNT.DECIMAL);
			clapTradeHeader.setTotalAmount(TotalAmountStr);
			return true;
		} catch (Exception e) {
			log.error(String.format("%s getHeader Exception", key), e);
			TASK_LOG.ERROR(String.format("%s getHeader Exception", key), e);
		}

		return false;
	}

	/**
	 * 金额格式规整
	 * 
	 * @param totalAmount
	 * @param length
	 * @param decimal
	 * @return
	 */
	private static String amountToString(Double totalAmount, int length, int decimal) {
		String totalAmountStr = "000";
		if (totalAmount != null && totalAmount >= 0) {
			DecimalFormat decimalFormat = new DecimalFormat("###0.00");// 格式化设置
			totalAmountStr = decimalFormat.format(totalAmount);
			totalAmountStr = totalAmountStr.replace(".", "");
			if (totalAmountStr.length() <= length) {
				totalAmountStr = "00000000000000000000000000".substring(0, (length - totalAmountStr.length())) + totalAmountStr;
			}
		} else {
			totalAmountStr = "00000000000000000000000000".substring(0, length);
		}
		return totalAmountStr;
	}

	/**
	 * ftp上传操作
	 * 
	 * @return
	 */
	private boolean ftp() {
		try {
			if (StringUtils.isNotEmpty(JOB_CLAP_SFTP_USER_NAME)) {
				SFTPUtil sftp = new SFTPUtil(JOB_CLAP_SFTP_USER_NAME, JOB_CLAP_SFTP_PASSWORD, JOB_CLAP_SFTP_IP_ADDRESS, JOB_CLAP_SFTP_PORT);
				sftp.login();
				sftp.upload(JOB_CLAP_TRADE_FILE_SFTP_PATH, JOB_CLAP_UPNLOAD_FILE_PATH + fileName);
				sftp.logout();
			}
		} catch (Exception e) {
			log.error(String.format("%s ftp Exception", key), e);
			TASK_LOG.ERROR(String.format("%s ftp Exception", key), e);
			return false;
		}
		return true;

	}

	public static void main(String[] args) {
		System.out.println(isEchoNum("113111"));
		System.out.println(isContinuously("122456"));
	}

	
    /**
     * 判断是否为重复密码  111111，222222
     * @param pwd
     * @return
     */
    public static  boolean isEchoNum(String pwd){
        if(pwd==null||pwd.isEmpty()){
            return false;
        }
        char[] elements=pwd.toCharArray();
        for(char e:elements){
            if(e!=elements[0]){
                return false;
            }
        }
        return true;
    }
    /**
     * 判断是否为连续密码  123456，23456
     * @param pwd
     * @return
     */
    public static   boolean isContinuously(String pwd){
        if(pwd==null||pwd.isEmpty()){
            return false;
        }
        char[] elements=pwd.toCharArray();
        for (int i = 1; i <elements.length ; i++) {
            if(elements[i]-elements[i-1]!=1){
                return false;
            }
        }
        return true;
    }
}
