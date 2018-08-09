package com.zbensoft.e.payment.api.quartz.job.charge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.mail.search.HeaderTerm;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.RabbitmqDef;
import com.zbensoft.e.payment.api.common.SFTPUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.BOOKKEEPING_LOG;
import com.zbensoft.e.payment.api.log.ERROR_HANDLING_LOG;
import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.service.api.BankChargeInfoService;
import com.zbensoft.e.payment.api.service.api.ChargeErrorHandlingBankService;
import com.zbensoft.e.payment.api.service.api.ChargeReconciliationBankService;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankChargeInfoService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeAdjustment;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeBatch;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeDocument;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeHeader;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeRecord;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeTotal;
import com.zbensoft.e.payment.api.vo.errorHandling.ErrorHandlingChargeVo;
import com.zbensoft.e.payment.api.vo.errorHandling.ErrorHandlingVo;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.BankChargeInfo;
import com.zbensoft.e.payment.db.domain.ChargeErrorHandlingBank;
import com.zbensoft.e.payment.db.domain.ChargeReconciliationBank;
import com.zbensoft.e.payment.db.domain.ChargeReconciliationBankKey;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankChargeInfo;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * @author wangchenyang
 *
 */
public abstract class SimpleChargeReconciliationJobAbs implements Job {
	
	private static final Logger log = LoggerFactory.getLogger(SimpleChargeReconciliationJobAbs.class);


	protected String key = "SimpleChargeReconciliationJobAbs";

	private Environment env = SpringBeanUtil.getBean(Environment.class);

	
	
	private ChargeErrorHandlingBankService chargeErrorHandlingBankService= SpringBeanUtil.getBean(ChargeErrorHandlingBankService.class);
	private ErrorHandlingBankChargeInfoService errorHandlingBankChargeInfoService= SpringBeanUtil.getBean(ErrorHandlingBankChargeInfoService.class);
	private BankChargeInfoService bankChargeInfoService = SpringBeanUtil.getBean(BankChargeInfoService.class);
	protected ChargeReconciliationBankService chargeReconciliationBankService = SpringBeanUtil.getBean(ChargeReconciliationBankService.class);
	
	private TradeInfoService tradeInfoService = SpringBeanUtil.getBean(TradeInfoService.class);
	private RedisTemplate redisTemplate = SpringBeanUtil.getBean("redisTemplate", RedisTemplate.class);
	protected AmqpTemplate rabbitTemplate = SpringBeanUtil.getBean(AmqpTemplate.class);
	
	String JOB_CHARGE_RECONCILIATION_FILE_PREFIX=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_RECONCILIATION_FILE_PREFIX);
	String JOB_CHARGE_RECONCILIATION_FILE_DATEFORMATE=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_RECONCILIATION_FILE_DATEFORMATE);
//	int JOB_CHARGE_RECONCILIATION_FILE_TRADE_DAY_BEFORE=SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CHARGE_RECONCILIATION_FILE_TRADE_DAY_BEFORE);
	
	String JOB_CHARGE_RECONCILIATION_BANK_FILE_PATH=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_RECONCILIATION_BANK_FILE_PATH);
	String JOB_CHARGE_RECONCILIATION_COPY_FILE_PATH=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_RECONCILIATION_COPY_FILE_PATH);
	String JOB_CHARGE_DOCUMENT_IDENTIFIER_REGISTRY=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_DOCUMENT_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_AJUSTE_IDENTIFIER_REGISTRY=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_AJUSTE_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_HEADER_IDENTIFIER_REGISTRY=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_HEADER_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_TOTAL_IDENTIFIER_REGISTRY=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_TOTAL_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_RECORD_IDENTIFIER_REGISTRY= SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_RECORD_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_BATH_IDENTIFIER_REGISTRY= SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_BATH_IDENTIFIER_REGISTRY);
	
	
	String JOB_CHARGE_FILE_PREFIX=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_FILE_PREFIX);
	String JOB_CHARGE_FILE_DATEFORMATE=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_FILE_DATEFORMATE);
	int JOB_CHARGE_FILE_TRADE_DAY_BEFORE=SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CHARGE_FILE_TRADE_DAY_BEFORE);
	String JOB_CHARGE_HEADER_NUMBER_NEGOTIATION=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_HEADER_NUMBER_NEGOTIATION);
	String JOB_CHARGE_RIF_OF_CANTV=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_RIF_OF_CANTV);
	String JOB_CHARGE_PAYER_NAME_OF_CANTV=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_PAYER_NAME_OF_CANTV);
	String JOB_CHARGE_BANK_0102_ACCOUNT_OF_CANTV=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_BANK_0102_ACCOUNT_OF_CANTV);
	
	protected ChargeReconciliationBank chargeReconciliationBank;

	// private Date date = Calendar.getInstance().getTime();
	protected Date date = DateUtil.convertStringToDate("2017-08-09", "yyyy-MM-dd");
	protected String bankId = "0102";
	protected String filePath = JOB_CHARGE_RECONCILIATION_COPY_FILE_PATH;
	protected String bankFilePath = JOB_CHARGE_RECONCILIATION_BANK_FILE_PATH;
	
	

	protected Date yesterday = DateUtils.addDays(date, -1);

	protected long timeWindow = 0;// 时间窗口
	protected String yesterdayStr = DateUtil.convertDateToString(yesterday, "yyyyMMdd");

	protected String redisKey_bank = "charge_recon_bank_" + bankId;
	protected String redisKey_epay = "charge_recon_epay_" + bankId;
	protected String sftpPath = "/upload";

	protected String sftpUserName;
	protected String sftpIpAddress;
	protected int sftpPort;
	protected String sftpPassword;
	protected String sftpKeyFile;
	protected String destFilePathName;
	protected String destFileName;
	protected List<String> destFilePaths=new ArrayList<>();
	protected List<BankChargeHeader> headerList=new ArrayList<>();
	protected BankChargeHeader bankChargeHeaderTmp = new BankChargeHeader();
	/**
	 * 数据初始化
	 */
	protected abstract void initdata();

	/**
	 * 执行任务
	 * @throws JobExecutionException
	 */
	protected void executeJob() throws JobExecutionException {
		try {
	
			RECONCILIATION_LOG.INFO(String.format("%s Start", key));
			initdata();
			RECONCILIATION_LOG.INFO(String.format("%s date = %s,bankId = %s,filePath = %s,bankFilePath = %s,timeWindow = %s,yesterday = %s,filename=%s,redisKey_bank = %s,redisKey_epay = %s", key, date, bankId, filePath,
					bankFilePath, timeWindow, yesterdayStr, destFilePathName, redisKey_bank, redisKey_epay));
			chargeReconciliationBank = getChargeReconciliationBank();
			if (chargeReconciliationBank == null) {
				RECONCILIATION_LOG.INFO(String.format("%s getChargeReconciliationBank is null", key));
				throw new JobExecutionException(String.format("%s getChargeReconciliationBank is null", key));
			}
			boolean isSucc = true;
			//顺序执行任务
			if (0 == chargeReconciliationBank.getStatus()) {
				isSucc = filesCheck();//检查文件是否下载
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s ftp/copyFile is fail", key));
					throw new JobExecutionException(String.format("%s ftp/copyFile is fail", key));
				} else {
					chargeReconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.FTP_DOWNLOAD);// 已下载文件
					chargeReconciliationBankService.updateByPrimaryKey(chargeReconciliationBank);
				}
			}
			if (1 == chargeReconciliationBank.getStatus()) {
				isSucc = fileToDB();
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s fileToDB is error", key));
					throw new JobExecutionException(String.format("%s fileToDB is error", key));
				} else {
					chargeReconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.BANK_TO_DB);// 银行已入库
					chargeReconciliationBankService.updateByPrimaryKey(chargeReconciliationBank);
				}
			}
			if (2 == chargeReconciliationBank.getStatus()) {
				isSucc = tradeInfoToDB();
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s fileToDB is error", key));
					throw new JobExecutionException(String.format("%s fileToDB is error", key));
				} else {
					chargeReconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.EPAY_TO_DB);// 交易已入库
					chargeReconciliationBankService.updateByPrimaryKey(chargeReconciliationBank);
				}
			}
	
			if (3 == chargeReconciliationBank.getStatus()) {
				isSucc = deleteDiff();
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s deleteDiff is error", key));
					throw new JobExecutionException(String.format("%s deleteDiff is error", key));
				}
	
				isSucc = diffBank();
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s diffBank is error", key));
					throw new JobExecutionException(String.format("%s diffBank is error", key));
				}
	
				isSucc = diffEpay();
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s diffEpay is error", key));
					throw new JobExecutionException(String.format("%s diffEpay is error", key));
				} else {
					chargeReconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.END);// 对账结束
					chargeReconciliationBank.setEndTime(Calendar.getInstance().getTime());
					chargeReconciliationBankService.updateByPrimaryKey(chargeReconciliationBank);
				}
	
				doSame();
	
			}
		} catch (Exception e) {
			log.error(String.format("%s diffEpay is error", key), e);
			RECONCILIATION_LOG.ERROR(String.format("%s diffEpay is error", key), e);
		}
	}

	protected void doSame() {
		Set<String> sets = redisTemplate.opsForSet().intersect(redisKey_bank, redisKey_epay);
		if (sets != null && sets.size() > 0) {
			for (Iterator iterator = sets.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				String[] values = value.split("_");
				String orderNo = values[0];
				// String amount = values[1];
				TradeInfo tradeInfoTmp = new TradeInfo();
				tradeInfoTmp.setMerchantOrderNo(orderNo);
				tradeInfoTmp.setCreateTimeStartSer(DateUtil.getDayStrTenTime(bankChargeHeaderTmp.getReferenceNumber(),DateUtil.DATE_FORMAT_THREE, true, DateUtil.DATE_FORMAT_FIVE));
				tradeInfoTmp.setCreateTimeEndSer(DateUtil.getDayStrTenTime(bankChargeHeaderTmp.getReferenceNumber(),DateUtil.DATE_FORMAT_THREE, false, DateUtil.DATE_FORMAT_FIVE));
				TradeInfo tradeInfo = null;
				List<TradeInfo> list = tradeInfoService.selectbyOrderNoInDay(tradeInfoTmp);
				if (list != null && list.size() == 1) {
					tradeInfo = list.get(0);
				}
				
				if (tradeInfo.getErrorCode() == null) {// 没有处理过的数据
					try {
						rabbitTemplate.convertAndSend(RabbitmqDef.CHARGE_RECONCILIATION.EXCHANGE, null, tradeInfo);
						RECONCILIATION_LOG.INFO(String.format("%s send to bookkeeping %s ", key, tradeInfo.toString()));
						BOOKKEEPING_LOG.INFO(String.format("%s send info =%s", key, tradeInfo.toString()));
					} catch (Exception e) {
						log.error(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						// TODO:ALARM
						RECONCILIATION_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						BOOKKEEPING_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
					}
				}
			}
		}

	}

	/**
	 * 删除昨天该银行对账错误文件
	 * 
	 * @return
	 */
	protected boolean deleteDiff() {
		try {
			ChargeErrorHandlingBank chargeErrorHandlingBank = new ChargeErrorHandlingBank();
			chargeErrorHandlingBank.setBankId(bankId);
			chargeErrorHandlingBank.setReconciliationTime(yesterdayStr);
			chargeErrorHandlingBankService.deleteByBankIdAndTime(chargeErrorHandlingBank);

			ErrorHandlingBankChargeInfo errorHandlingBankChargeInfo = new ErrorHandlingBankChargeInfo();
			errorHandlingBankChargeInfo.setBankId(bankId);
			errorHandlingBankChargeInfo.setReconciliationTime(yesterdayStr);
			errorHandlingBankChargeInfoService.deleteByBankIdAndTime(errorHandlingBankChargeInfo);
		} catch (Exception e) {
			log.error(String.format("%s diffEpay is error", key), e);
			RECONCILIATION_LOG.ERROR(String.format("%s diffEpay is error", key), e);
		}
		return true;
	}

	protected boolean diffEpay() {
		Set<String> sets = redisTemplate.opsForSet().difference(redisKey_epay, redisKey_bank);
		if (sets != null && sets.size() > 0) {
			for (Iterator iterator = sets.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				if (!doDiff(value, true)) {
					RECONCILIATION_LOG.INFO(String.format("%s doDiff is error resfNo=%s", key, value));
					return false;
				}
			}
		}
		return true;
	}

	protected boolean diffBank() {
		Set<String> sets = redisTemplate.opsForSet().difference(redisKey_bank, redisKey_epay);
		if (sets != null && sets.size() > 0) {
			for (Iterator iterator = sets.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				if (!doDiff(value, false)) {
					RECONCILIATION_LOG.INFO(String.format("%s doDiff is error resfNo=%s", key, value));
					return false;
				}
			}
		}
		return true;
	}

	private boolean doDiff(String value, boolean isEpay) {
		try {
			boolean insertFlag = true;
			String[] values = value.split("_");
			String orderNo = values[0];
			String amount = values[1];
			BankChargeInfo bankChargeInfo = bankChargeInfoService.selectByPrimaryKey(orderNo);
			TradeInfo tradeInfoTmp = new TradeInfo();
			tradeInfoTmp.setMerchantOrderNo(orderNo);
			tradeInfoTmp.setCreateTimeStartSer(DateUtil.getDayStrTenTime(bankChargeHeaderTmp.getReferenceNumber(),DateUtil.DATE_FORMAT_THREE, true, DateUtil.DATE_FORMAT_FIVE));
			tradeInfoTmp.setCreateTimeEndSer(DateUtil.getDayStrTenTime(bankChargeHeaderTmp.getReferenceNumber(),DateUtil.DATE_FORMAT_THREE, false, DateUtil.DATE_FORMAT_FIVE));
			TradeInfo tradeInfo = null;
			List<TradeInfo> list = tradeInfoService.selectbyOrderNoInDay(tradeInfoTmp);
			if (list != null && list.size() == 1) {
				tradeInfo = list.get(0);
			}
			// 银行没有
			if (bankChargeInfo == null) {
				if (tradeInfo == null) {
					RECONCILIATION_LOG.INFO(String.format("%s bank and epay both null keyValue=%s", key, value));
				} else {
						ChargeErrorHandlingBank chargeErrorHandlingBank = new ChargeErrorHandlingBank();
						chargeErrorHandlingBank.setBankId(bankId);
						chargeErrorHandlingBank.setReconciliationTime(bankChargeHeaderTmp.getReferenceNumber());
						chargeErrorHandlingBank.setTradeSeq(tradeInfo.getTradeSeq());
						chargeErrorHandlingBank.setRefNo(tradeInfo.getMerchantOrderNo());
						chargeErrorHandlingBank.setReason(MessageDef.CHARGE_ERROR_HANDLING_REASON.BANK_NO_INFO);
						chargeErrorHandlingBank.setHanndlingResult(MessageDef.CHARGE_ERROR_HANDLING_RESULT.UNHANDLING);
						chargeErrorHandlingBankService.insert(chargeErrorHandlingBank);
						
						ErrorHandlingChargeVo errorHandlingChargeVo = new ErrorHandlingChargeVo();
						errorHandlingChargeVo.setTradeInfo(tradeInfo);
						errorHandlingChargeVo.setBankId(bankId);
						errorHandlingChargeVo.setReconciliationTime(bankChargeHeaderTmp.getReferenceNumber());
						try {
							rabbitTemplate.convertAndSend(RabbitmqDef.CHARGE_ERROR_HANDLING.EXCHANGE, null, errorHandlingChargeVo);
							RECONCILIATION_LOG.INFO(String.format("%s send to ERROR_HANDLING %s ", key, errorHandlingChargeVo.toString()));
							ERROR_HANDLING_LOG.INFO(String.format("%s send info =%s", key, errorHandlingChargeVo.toString()));
						} catch (Exception e) {
							log.error(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
							// TODO:ALARM
							RECONCILIATION_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
							ERROR_HANDLING_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						}
				}
			} else {
				if (tradeInfo == null) {
					ChargeErrorHandlingBank chargeErrorHandlingBank = new ChargeErrorHandlingBank();
					chargeErrorHandlingBank.setBankId(bankId);
					chargeErrorHandlingBank.setReconciliationTime(bankChargeHeaderTmp.getReferenceNumber());
					chargeErrorHandlingBank.setTradeSeq(null);
					chargeErrorHandlingBank.setRefNo(bankChargeInfo.getRefNo());
					chargeErrorHandlingBank.setReason(MessageDef.CHARGE_ERROR_HANDLING_REASON.EPAY_NO_INFO);
					chargeErrorHandlingBank.setHanndlingResult(MessageDef.CHARGE_ERROR_HANDLING_RESULT.UNHANDLING);
					chargeErrorHandlingBankService.insert(chargeErrorHandlingBank);
					
					ErrorHandlingChargeVo errorHandlingChargeVo = new ErrorHandlingChargeVo();
					errorHandlingChargeVo.setBankChargeInfo(bankChargeInfo);
					errorHandlingChargeVo.setBankId(bankId);
					errorHandlingChargeVo.setReconciliationTime(bankChargeHeaderTmp.getReferenceNumber());
					try {
						rabbitTemplate.convertAndSend(RabbitmqDef.CHARGE_ERROR_HANDLING.EXCHANGE, null, errorHandlingChargeVo);
						RECONCILIATION_LOG.INFO(String.format("%s send to ERROR_HANDLING %s", key, errorHandlingChargeVo.toString()));
						ERROR_HANDLING_LOG.INFO(String.format("%s send info =%s", key, errorHandlingChargeVo.toString()));
					} catch (Exception e) {
						log.error(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						// TODO:ALARM
						RECONCILIATION_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						ERROR_HANDLING_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
					}

				} else {
					// 重复只插入一条
					if (isEpay) {
						ChargeErrorHandlingBank chargeErrorHandlingBank = new ChargeErrorHandlingBank();
						chargeErrorHandlingBank.setBankId(bankId);
						chargeErrorHandlingBank.setReconciliationTime(bankChargeHeaderTmp.getReferenceNumber());
						chargeErrorHandlingBank.setTradeSeq(tradeInfo.getTradeSeq());
						chargeErrorHandlingBank.setRefNo(tradeInfo.getMerchantOrderNo());
						chargeErrorHandlingBank.setReason(MessageDef.CHARGE_ERROR_HANDLING_REASON.RESULT_NOT_MATCH);
						chargeErrorHandlingBank.setHanndlingResult(MessageDef.CHARGE_ERROR_HANDLING_RESULT.UNHANDLING);
						chargeErrorHandlingBankService.insert(chargeErrorHandlingBank);
						
						
						ErrorHandlingChargeVo errorHandlingChargeVo = new ErrorHandlingChargeVo();
						errorHandlingChargeVo.setTradeInfo(tradeInfo);
						errorHandlingChargeVo.setBankChargeInfo(bankChargeInfo);
						errorHandlingChargeVo.setBankId(bankId);
						errorHandlingChargeVo.setReconciliationTime(bankChargeHeaderTmp.getReferenceNumber());
						try {
							rabbitTemplate.convertAndSend(RabbitmqDef.CHARGE_ERROR_HANDLING.EXCHANGE, null, errorHandlingChargeVo);
							RECONCILIATION_LOG.INFO(String.format("%s send to ERROR_HANDLING %s", key, errorHandlingChargeVo.toString()));
							ERROR_HANDLING_LOG.INFO(String.format("%s send info =%s", key, errorHandlingChargeVo.toString()));
						} catch (Exception e) {
							log.error(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
							// TODO:ALARM
							RECONCILIATION_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
							ERROR_HANDLING_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						}
					} else {
						insertFlag = false;
					}
				}
				if (insertFlag) {
					
					ErrorHandlingBankChargeInfo errorHandlingBankChargeInfo=new ErrorHandlingBankChargeInfo(bankChargeInfo, bankId, bankChargeHeaderTmp.getReferenceNumber());
					errorHandlingBankChargeInfoService.insert(errorHandlingBankChargeInfo);
				}
			}
		} catch (Exception e) {
			log.error(String.format("%s doDiff error", key), e);
			RECONCILIATION_LOG.ERROR(String.format("%s doDiff error", key), e);
			return false;
		}
		return true;
	}

	protected boolean tradeInfoToDB() {
		redisTemplate.delete(redisKey_epay);
		if (bankChargeHeaderTmp != null) {
			if (bankChargeHeaderTmp.getDateOfPaymen() != null) {
				TradeInfo tradeInfo = new TradeInfo();
				int JOB_CHARGE_FILE_WRITE_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CHARGE_FILE_WRITE_ONETIME);
				List<TradeInfo> getDbTradeList = new ArrayList<>();
				TradeInfo tradeInfoSer = new TradeInfo();
				tradeInfoSer.setType(MessageDef.TRADE_TYPE.CHARGE);// type未提现
				tradeInfoSer.setStatus(MessageDef.TRADE_STATUS.PROCESSING);// 状态为进行中
				tradeInfoSer.setCreateTimeStartSer(DateUtil.getDayStrTenTime(bankChargeHeaderTmp.getReferenceNumber(),DateUtil.DATE_FORMAT_THREE, true, DateUtil.DATE_FORMAT_FIVE));
				tradeInfoSer.setCreateTimeEndSer(DateUtil.getDayStrTenTime(bankChargeHeaderTmp.getReferenceNumber(),DateUtil.DATE_FORMAT_THREE, false, DateUtil.DATE_FORMAT_FIVE));
				int start = 0;
				try {
					while (true) {
						int pageNum = PageHelperUtil.getPageNum(String.valueOf(start),
								String.valueOf(JOB_CHARGE_FILE_WRITE_ONETIME));
						int pageSize = PageHelperUtil.getPageSize(String.valueOf(start),
								String.valueOf(JOB_CHARGE_FILE_WRITE_ONETIME));
						PageHelper.startPage(pageNum, pageSize);
						List<TradeInfo> tradeInfoList = tradeInfoService.selectByDayLmit(tradeInfoSer);

						if (tradeInfoList != null && tradeInfoList.size() == JOB_CHARGE_FILE_WRITE_ONETIME) {
							addRedisEpayPay(tradeInfoList);
							start += JOB_CHARGE_FILE_WRITE_ONETIME;
						} else {
							addRedisEpayPay(tradeInfoList);
							break;
						}
					}
				} catch (Exception e) {
					log.error(String.format("%s tradeInfoToDB is error", key), e);
					RECONCILIATION_LOG.ERROR(String.format("%s tradeInfoToDB is error", key), e);
				}
			} else {
				return false;
			}
		}else{
			return false;
		}
		return true;
	}

	/**
	 * 获取对账银行
	 * @return
	 */
	protected ChargeReconciliationBank getChargeReconciliationBank() {
		ChargeReconciliationBankKey chargeReconciliationBankKey = new ChargeReconciliationBankKey();
		chargeReconciliationBankKey.setBankId(bankId);
		chargeReconciliationBankKey.setReconciliationTime(bankChargeHeaderTmp.getReferenceNumber());
		if(bankChargeHeaderTmp!=null&&bankChargeHeaderTmp!=null){
			chargeReconciliationBankKey.setReconciliationTime(bankChargeHeaderTmp.getReferenceNumber());//从文件头文件中获取时间
		}
		ChargeReconciliationBank chargeReconciliationBank = chargeReconciliationBankService.selectByPrimaryKey(chargeReconciliationBankKey);
		if (chargeReconciliationBank == null) {
			chargeReconciliationBank = new ChargeReconciliationBank();
			chargeReconciliationBank.setBankId(chargeReconciliationBankKey.getBankId());
			chargeReconciliationBank.setReconciliationTime(chargeReconciliationBankKey.getReconciliationTime());
			chargeReconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.UN_START);
			chargeReconciliationBank.setCreateTime(Calendar.getInstance().getTime());
			int insertInt = chargeReconciliationBankService.insert(chargeReconciliationBank);
			if (insertInt != 1) {
				RECONCILIATION_LOG.INFO(String.format("%s insert ChargeReconciliationBank return 0", key));
				return null;
			}
		}
		return chargeReconciliationBank;
	}

	protected boolean fileToDB() {
		int JOB_CHARGE_RECONCLIATION_BATCH_TO_DB_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CHARGE_RECONCLIATION_BATCH_TO_DB_COUNT);
//		bankChargeInfoService.deleteAll(bankId);//银行分表处理
		bankChargeInfoService.deleteAll();
		redisTemplate.delete(redisKey_bank);
		if (destFilePathName != null && destFilePathName.length() > 0) {

				File file = new File(destFilePathName);
				if (!file.exists()) {
					RECONCILIATION_LOG.INFO(String.format("%s %s not exist", key, destFilePathName));
					return false;
				}
				destFileName=file.getName();
				BufferedReader reader = null;
				int line = 0;
				int batchNo = 0;
				int recordNo = 0;
				List<BankChargeInfo> bankChargeInfoList = new ArrayList<>();

				BankChargeHeader bankChargeHeader = new BankChargeHeader();
				List<BankChargeBatch> bankChargeBatchList = new ArrayList<>();

				BankChargeDocument bankChargeDocument = new BankChargeDocument();
				BankChargeAdjustment bankChargeAdjustment = new BankChargeAdjustment();
				BankChargeTotal bankChargeTotal = new BankChargeTotal();

				try {
					reader = new BufferedReader(new FileReader(file));
					String tempString = null;
					// 一次读入一行，直到读入null为文件结束
					while ((tempString = reader.readLine()) != null) {
						int length = tempString.length();
						if (tempString.startsWith(JOB_CHARGE_HEADER_IDENTIFIER_REGISTRY)) {// 文件头
							boolean isDecode = bankChargeHeader.decode(tempString, key);// 解析头
							if (!isDecode) {
								return false;
							}
							if (!bankChargeHeader.validate(key)) {
								return false;
							}
						} else if (tempString.startsWith(JOB_CHARGE_BATH_IDENTIFIER_REGISTRY)) {// 批次信息
							BankChargeBatch bankChargeBatch = new BankChargeBatch();
							boolean isDecode = bankChargeBatch.decode(tempString, key);
							if (!isDecode) {
								return false;
							}
							if (!bankChargeBatch.validate(key)) {
								return false;
							}
							bankChargeBatchList.add(bankChargeBatch);
							batchNo++;
						} else if (tempString.startsWith(JOB_CHARGE_RECORD_IDENTIFIER_REGISTRY)) {// 充值对账记录
							BankChargeRecord bankChargeRecord = new BankChargeRecord();
							boolean isDecode = bankChargeRecord.decode(tempString, key);
							if (!isDecode) {
								return false;
							}
							if (!bankChargeRecord.validate(key)) {
								return false;
							}
							BankChargeInfo bankChargeInfo = bankChargeRecord.setBankChargeInfo();
							if(bankChargeRecord.getAmount()!=null&&bankChargeRecord.getAmount().length()>0){
								bankChargeInfo.setChargeAmount(stringToAmount(bankChargeRecord.getAmount()));
							}
							if (bankChargeBatchList != null && bankChargeBatchList.get(batchNo - 1) != null) {
								bankChargeInfo.setChargeDate(bankChargeBatchList.get(batchNo - 1).getValueDate());
								bankChargeInfo.setCurrencyType(bankChargeBatchList.get(batchNo - 1).getCurrency());
							}
							bankChargeInfoList.add(bankChargeInfo);
							if (bankChargeInfoList.size() >= JOB_CHARGE_RECONCLIATION_BATCH_TO_DB_COUNT) {
								addRedisBank(bankChargeInfoList);
								bankChargeInfoService.insertBatch(bankChargeInfoList);
								bankChargeInfoList.clear();
							}
							recordNo++;

						} else if (tempString.startsWith(JOB_CHARGE_DOCUMENT_IDENTIFIER_REGISTRY)) {// DOCUMNET
							boolean isDecode = bankChargeDocument.decode(tempString, key);
							if (!isDecode) {
								return false;
							}
							if (!bankChargeDocument.validate(key)) {
								return false;
							}
						} else if (tempString.startsWith(JOB_CHARGE_AJUSTE_IDENTIFIER_REGISTRY)) {// AJUSTE
							boolean isDecode = bankChargeAdjustment.decode(tempString, key);
							if (!isDecode) {
								return false;
							}
							if (!bankChargeAdjustment.validate(key)) {
								return false;
							}
						} else {// Total
							boolean isDecode = bankChargeTotal.decode(tempString, key);
							if (!isDecode) {
								return false;
							}
							if (!bankChargeTotal.validate(key)) {
								return false;
							}

						}

						line++;
					}
					if (bankChargeInfoList.size() > 0) {
						addRedisBank(bankChargeInfoList);
						bankChargeInfoService.insertBatch(bankChargeInfoList);
						bankChargeInfoList.clear();
					}
					reader.close();
				} catch (Exception e) {
					log.error(String.format("%s %s read exception", key, destFilePathName), e);
					RECONCILIATION_LOG.ERROR(String.format("%s %s read exception", key, destFilePathName), e);
					return false;
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e1) {
							log.error("", e1);
						}
					}
				}

				if (bankChargeTotal.getTotalDebitRecords() != null
						&& batchNo  != Integer.valueOf(bankChargeTotal.getTotalDebitRecords())) {
					RECONCILIATION_LOG.INFO(String.format("%s %s Total's DebitRecords not match batch's number", key,
							destFilePathName));
					return false;
				}
				if (bankChargeTotal.getTotalDebitRecords() != null
						&& recordNo != Integer.valueOf(bankChargeTotal.getTotalCreditsRecords())) {
					RECONCILIATION_LOG.INFO(String.format("%s %s Total's CreditsRecords not match record's number", key,
							destFilePathName));
					return false;
				}
		}
		return true;
	}

	private void addRedisEpayPay(List<TradeInfo> tradeInfoList) {
		List<Object> pipelinedResults = redisTemplate.executePipelined(new SessionCallback<Object>() {
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				for (TradeInfo tradeInfo : tradeInfoList) {
					String statusStr=null;
					if(tradeInfo.getStatus()!=null){
						statusStr=String.valueOf(tradeInfo.getStatus());
					}
					String tmp=tradeInfo.getMerchantOrderNo()+ "_" + statusStr;
					operations.opsForSet().add(redisKey_epay, tradeInfo.getMerchantOrderNo()+ "_" + statusStr);
				}
				return null;
			}
		});
	}

	private void addRedisBank(List<BankChargeInfo> bankChargeInfoList) {
		List<Object> pipelinedResults = redisTemplate.executePipelined(new SessionCallback<Object>() {
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				for (BankChargeInfo bankChargeInfo : bankChargeInfoList) {
					String tmp=bankChargeInfo.getRefNo()+ "_" + resultToInt(bankChargeInfo.getChargeResult(),3);
					operations.opsForSet().add(redisKey_bank, bankChargeInfo.getRefNo()+ "_" + resultToInt(bankChargeInfo.getChargeResult(),3));
				}
				return null;
			}
		});
	}

	protected String resultToInt(String chargeResult, int location) {
		if (chargeResult != null) {
			switch (location) {
			case 1:
				if (chargeResult.equals(MessageDef.CHARGE_HEADER_RESULT.SUCCESS)) {
					return "1";
				} else {
					return chargeResult;
				}
			case 2:
				if (chargeResult.equals(MessageDef.CHARGE_DEBITO_RESULT.SUCCESS)) {
					return "1";
				} else {
					return chargeResult;
				}
			case 3:
				if (chargeResult.equals(MessageDef.CHARGE_CREDITO_RESULT.SUCCESS)) {
					return "1";
				} else {
					return chargeResult;
				}
			default:
				return chargeResult;
			}
		}
		return "99";
	}

	protected boolean ftp() {
		try {
			if (StringUtils.isNotEmpty(sftpUserName)) {
				SFTPUtil sftp = new SFTPUtil(sftpUserName, sftpIpAddress, sftpPort, sftpKeyFile);
				sftp.login();
				sftp.download(sftpPath, destFileName, destFilePathName);
				sftp.logout();
			}
		} catch (Exception e) {
			log.error("", e);
			return false;
		}
		return true;
	}

	
	/**
	 * 检查
	 * @return
	 */
	private boolean filesCheck(){
		
		File destFile = new File(destFilePathName);  
        //判断该文件或目录是否存在，不存在时在控制台输出提醒  
        if (!destFile.exists()) {  
        	RECONCILIATION_LOG.ERROR("the file:"+ bankFilePath +" not exit.");  
            return false;  
        }  
        //判断是不是一个文件
        if (!destFile.isFile()) {  
            if (destFile.isDirectory()) {  
            	RECONCILIATION_LOG.ERROR("the path:"+ bankFilePath +" is a Directory");   
            }  
            return false;  
        }
		return true;  
	}
	
	
	/**
	 * 数据库交易记录转ClapTrad记录
	 * @param tradeInfo
	 * @return
	 */
	private static String stringToAmount(String amountStr) {
		String totalAmountStr ="";
		totalAmountStr = amountStr.replace(",", ".");
		BigDecimal bigD=new BigDecimal(totalAmountStr);
		
		DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置 
		
		return decimalFormat.format(bigD);
	}
	
	
	public static void main(String[] args) {
		System.out.println(stringToAmount("000000000001923,00"));
		
	}

}
