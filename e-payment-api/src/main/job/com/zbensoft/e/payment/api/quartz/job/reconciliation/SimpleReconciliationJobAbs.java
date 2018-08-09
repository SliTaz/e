package com.zbensoft.e.payment.api.quartz.job.reconciliation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.RabbitmqDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.SFTPUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.BOOKKEEPING_LOG;
import com.zbensoft.e.payment.api.log.ERROR_HANDLING_LOG;
import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.service.api.BankTradeInfoService;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankService;
import com.zbensoft.e.payment.api.service.api.ErrorHandlingBankTradeInfoService;
import com.zbensoft.e.payment.api.service.api.ReconciliationBankService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.api.vo.errorHandling.ErrorHandlingVo;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.BankTradeInfo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBank;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankTradeInfo;
import com.zbensoft.e.payment.db.domain.ReconciliationBank;
import com.zbensoft.e.payment.db.domain.ReconciliationBankKey;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * @author xieqiang
 *
 */
public abstract class SimpleReconciliationJobAbs implements Job {

	private static final Logger log = LoggerFactory.getLogger(SimpleReconciliationJobAbs.class);

	protected String key = "SimpleReconciliationJobAbs";

	// private Environment env = SpringBeanUtil.getBean(Environment.class);

	private ErrorHandlingBankService errorHandlingBankService = SpringBeanUtil.getBean(ErrorHandlingBankService.class);
	private ErrorHandlingBankTradeInfoService errorHandlingBankTradeInfoService = SpringBeanUtil.getBean(ErrorHandlingBankTradeInfoService.class);
	private BankTradeInfoService bankTradeInfoService = SpringBeanUtil.getBean(BankTradeInfoService.class);
	protected ReconciliationBankService reconciliationBankService = SpringBeanUtil.getBean(ReconciliationBankService.class);
	private TradeInfoService tradeInfoService = SpringBeanUtil.getBean(TradeInfoService.class);
	protected AmqpTemplate rabbitTemplate = SpringBeanUtil.getBean(AmqpTemplate.class);

	// private Date date = Calendar.getInstance().getTime();
	protected Date date = DateUtil.convertStringToDate("2017-08-09", "yyyy-MM-dd");
	protected String bankId = "0163";
	protected String filePath = "E:\\项目\\2017-02-CLAP\\银行接口\\reconciliation\\";
	protected String bankFilePath = "E:\\项目\\2017-02-CLAP\\银行接口\\reconciliation\\";

	protected Date yesterday = DateUtils.addDays(date, -1);
	protected ReconciliationBank reconciliationBank;
	protected long timeWindow = 0;// 时间窗口
	protected String yesterdayStr = DateUtil.convertDateToString(yesterday, "yyyyMMdd");
	protected String fileName = bankId + "_" + yesterdayStr + ".txt";

	protected String redisKey_bank = "recon_bank_" + bankId;
	protected String redisKey_epay = "recon_epay_" + bankId;
	protected String sftpPath = "/upload";

	protected String sftpUserName;
	protected String sftpIpAddress;
	protected int sftpPort;
	protected String sftpPassword;
	protected String sftpKeyFile;

	protected long allRecord = 0;
	protected long bankNotRecord = 0;
	protected long epayNotRecord = 0;
	protected long moneyNotSameRecord = 0;

	protected void executeJob() throws JobExecutionException {
		long s = System.currentTimeMillis();
		try {
			RECONCILIATION_LOG.INFO(String.format("%s Start", key));
			initdata();
			RECONCILIATION_LOG.INFO(String.format("%s date = %s,bankId = %s,filePath = %s,bankFilePath = %s,timeWindow = %s,yesterday = %s,filename=%s,redisKey_bank = %s,redisKey_epay = %s", key, date, bankId, filePath,
					bankFilePath, timeWindow, yesterdayStr, fileName, redisKey_bank, redisKey_epay));
			reconciliationBank = getReconciliationBank();
			if (reconciliationBank == null) {
				RECONCILIATION_LOG.INFO(String.format("%s getReconciliationBank is null", key));
				throw new JobExecutionException(String.format("%s getReconciliationBank is null", key));
			}
			boolean isSucc = true;
			if (MessageDef.RECONCILIATION_BANK_STATUS.UN_START == reconciliationBank.getStatus()) {
				isSucc = copyFile();
				if (!isSucc) {
					File bankFile = new File(bankFilePath + fileName);
					if (bankFile.exists()) {
						RECONCILIATION_LOG.INFO(String.format("%s ftp/copyFile is fail,may be not exist.", key));
						throw new JobExecutionException(String.format("%s ftp/copyFile is fail,may be not exist.", key));
					} else {
						isSucc = checkNoTradeInfo();
						if (isSucc) {
							reconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.END);// 对账结束
							reconciliationBank.setEndTime(Calendar.getInstance().getTime());
							reconciliationBankService.updateByPrimaryKey(reconciliationBank);
							MessageAlarmFactory.getInstance().add(String.format("%s,usetime=%s,succ no recharge,bankId=%s,date=%s", key, (System.currentTimeMillis() - s), bankId, yesterdayStr));
							return;
						}
					}
				} else {
					reconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.FTP_DOWNLOAD);// 已下载文件
					reconciliationBankService.updateByPrimaryKey(reconciliationBank);
				}
			}
			if (MessageDef.RECONCILIATION_BANK_STATUS.FTP_DOWNLOAD == reconciliationBank.getStatus()) {
				isSucc = fileToDB();
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s fileToDB is error", key));
					throw new JobExecutionException(String.format("%s fileToDB is error", key));
				} else {
					reconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.BANK_TO_DB);// 银行已入库
					reconciliationBankService.updateByPrimaryKey(reconciliationBank);
				}
			}
			if (MessageDef.RECONCILIATION_BANK_STATUS.BANK_TO_DB == reconciliationBank.getStatus()) {
				isSucc = tradeInfoToDB();
				if (!isSucc) {
					RECONCILIATION_LOG.INFO(String.format("%s fileToDB is error", key));
					throw new JobExecutionException(String.format("%s fileToDB is error", key));
				} else {
					reconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.EPAY_TO_DB);// 交易已入库
					reconciliationBankService.updateByPrimaryKey(reconciliationBank);
				}
			}

			if (MessageDef.RECONCILIATION_BANK_STATUS.EPAY_TO_DB == reconciliationBank.getStatus()) {
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
					reconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.END);// 对账结束
					reconciliationBank.setEndTime(Calendar.getInstance().getTime());
					reconciliationBankService.updateByPrimaryKey(reconciliationBank);
				}

				doSame();
				MessageAlarmFactory.getInstance().add(String.format("%s,usetime=%s,succ,bankId=%s,date=%s: allRecord=%s,bankNotRecord=%s,epayNotRecord=%s,moneyNotSameRecord=%s", key, (System.currentTimeMillis() - s),
						bankId, yesterdayStr, allRecord, bankNotRecord, epayNotRecord, moneyNotSameRecord));
			}
		} catch (Exception e) {
			log.error(String.format("%s diffEpay is error", key), e);
			RECONCILIATION_LOG.ERROR(String.format("%s diffEpay is error", key), e);
			MessageAlarmFactory.getInstance().add(String.format("%s,usetime=%s,error,bankId=%s,date=%s,info:%s", key, (System.currentTimeMillis() - s), bankId, yesterdayStr, e.getMessage()));
		}
	}

	/**
	 * 校验是否有数据，true：没有数据
	 * 
	 * @return
	 * @throws JobExecutionException
	 */
	private boolean checkNoTradeInfo() throws JobExecutionException {
		RedisUtil.delete_key(redisKey_epay);// redisTemplate.delete(redisKey_epay);
		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setCreateTimeStartSer(DateUtil.convertDateToString(yesterday, "yyyy-MM-dd 00:00:00"));
		tradeInfo.setCreateTimeEndSer(DateUtil.convertDateToString(yesterday, "yyyy-MM-dd 23:59:59"));
		tradeInfo.setPayBankId(bankId);
		tradeInfo.setType(MessageDef.TRADE_TYPE.BANK_RECHARGE);
		int start = 0;
		int length = 1;
		try {
			int pageNum = PageHelperUtil.getPageNum(start + "", length + "");
			int pageSize = PageHelperUtil.getPageSize(start + "", length + "");
			PageHelper.startPage(pageNum, pageSize);
			List<TradeInfo> tradeInfoList = tradeInfoService.selectPage(tradeInfo);
			if (tradeInfoList != null && tradeInfoList.size() > 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			log.error(String.format("%s checkTradeInfo is error", key), e);
			RECONCILIATION_LOG.ERROR(String.format("%s checkTradeInfo is error", key), e);
			throw new JobExecutionException(String.format("%s checkTradeInfo is error:%s", key, e.getMessage()));
		}
	}

	protected void doSame() {
		Set<String> sets = RedisUtil.intersect(redisKey_bank, redisKey_epay);// redisTemplate.opsForSet().intersect(redisKey_bank, redisKey_epay);
		if (sets != null && sets.size() > 0) {
			for (Iterator<String> iterator = sets.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				String[] values = value.split("_");
				String orderNo = values[0];
				// String amount = values[1];
				TradeInfo tradeInfoTmp = new TradeInfo();
				tradeInfoTmp.setMerchantOrderNo(orderNo);
				tradeInfoTmp.setCreateTimeStartSer(DateUtil.convertDateToString(yesterday, "yyyy-MM-dd 00:00:00"));
				tradeInfoTmp.setCreateTimeEndSer(DateUtil.convertDateToString(yesterday, "yyyy-MM-dd 23:59:59"));
				// tradeInfoTmp.setCreateTime(yesterday);
				TradeInfo tradeInfo = null;
				List<TradeInfo> list = tradeInfoService.selectbyOrderNoInDay(tradeInfoTmp);
				if (list != null && list.size() == 1) {
					tradeInfo = list.get(0);
				}
				if (tradeInfo.getErrorCode() == null) {// 没有处理过的数据
					try {
						rabbitTemplate.convertAndSend(RabbitmqDef.RECONCILIATION.EXCHANGE, null, tradeInfo);
						RECONCILIATION_LOG.INFO(String.format("%s send to bookkeeping %s ", key, tradeInfo.toString()));
						BOOKKEEPING_LOG.INFO(String.format("%s send info =%s", key, tradeInfo.toString()));
					} catch (Exception e) {
						log.error(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						RECONCILIATION_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						BOOKKEEPING_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
						MessageAlarmFactory.getInstance().add(String.format("%s,doSame sendToRabbitmq error,bankId=%s,date=%s,info =%s", key, bankId, yesterdayStr, tradeInfo.toString()));
					}
				}
			}
		}

	}

	protected boolean deleteDiff() throws JobExecutionException {
		try {
			ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
			errorHandlingBank.setBankId(bankId);
			errorHandlingBank.setReconciliationTime(yesterdayStr);
			errorHandlingBankService.deleteByBankIdAndTime(errorHandlingBank);
			ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo = new ErrorHandlingBankTradeInfo();
			errorHandlingBankTradeInfo.setBankId(bankId);
			errorHandlingBankTradeInfo.setReconciliationTime(yesterdayStr);
			errorHandlingBankTradeInfoService.deleteByBankIdAndTime(errorHandlingBankTradeInfo);
		} catch (Exception e) {
			log.error(String.format("%s diffEpay is error", key), e);
			RECONCILIATION_LOG.ERROR(String.format("%s diffEpay is error", key), e);
			throw new JobExecutionException(String.format("%s diffEpay is error:%s ", key, e.getMessage()));
		}
		return true;
	}

	protected boolean diffEpay() throws JobExecutionException {
		Set<String> sets = RedisUtil.difference(redisKey_epay, redisKey_bank);// redisTemplate.opsForSet().difference(redisKey_epay, redisKey_bank);
		if (sets != null && sets.size() > 0) {
			for (Iterator<String> iterator = sets.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				if (!doDiff(value, true)) {
					RECONCILIATION_LOG.INFO(String.format("%s doDiff is error resfNo=%s", key, value));
					return false;
				}
			}
		}
		return true;
	}

	protected boolean diffBank() throws JobExecutionException {
		Set<String> sets = RedisUtil.difference(redisKey_bank, redisKey_epay);// redisTemplate.opsForSet().difference(redisKey_bank, redisKey_epay);
		if (sets != null && sets.size() > 0) {
			for (Iterator<String> iterator = sets.iterator(); iterator.hasNext();) {
				String value = (String) iterator.next();
				if (!doDiff(value, false)) {
					RECONCILIATION_LOG.INFO(String.format("%s doDiff is error resfNo=%s", key, value));
					return false;
				}
			}
		}
		return true;
	}

	private boolean doDiff(String value, boolean isEpay) throws JobExecutionException {
		int ERROR_HANDLING_BANK_RECHARGE_AUTO = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.ERROR_HANDLING_BANK_RECHARGE_AUTO);
		try {
			boolean insertFlag = true;
			String[] values = value.split("_");
			String orderNo = values[0];
			// String amount = values[1];
			BankTradeInfo bankTradeInfo = bankTradeInfoService.selectByPrimaryKey(bankId, orderNo);
			TradeInfo tradeInfoTmp = new TradeInfo();
			tradeInfoTmp.setMerchantOrderNo(orderNo);

			tradeInfoTmp.setCreateTimeStartSer(DateUtil.convertDateToString(yesterday, "yyyy-MM-dd 00:00:00"));
			tradeInfoTmp.setCreateTimeEndSer(DateUtil.convertDateToString(yesterday, "yyyy-MM-dd 23:59:59"));

			// tradeInfoTmp.setCreateTime(yesterday);
			TradeInfo tradeInfo = null;
			List<TradeInfo> list = tradeInfoService.selectbyOrderNoInDay(tradeInfoTmp);
			if (list != null && list.size() == 1) {
				tradeInfo = list.get(0);
			}
			// 银行没有
			if (bankTradeInfo == null) {
				if (tradeInfo == null) {
					RECONCILIATION_LOG.INFO(String.format("%s bank and epay both null keyValue=%s", key, value));
				} else {
					if (MessageDef.TRADE_HAVE_REFUND.YES == tradeInfo.getHaveRefund()) {
						RECONCILIATION_LOG.INFO(String.format("%s haveRefund , not error handling %s", key, tradeInfo));
						ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
						errorHandlingBank.setBankId(bankId);
						errorHandlingBank.setReconciliationTime(yesterdayStr);
						errorHandlingBank.setTradeSeq(tradeInfo.getTradeSeq());
						errorHandlingBank.setRefNo(tradeInfo.getMerchantOrderNo());
						errorHandlingBank.setReason(MessageDef.ERROR_HANDLING_REASON.REVERSE);
						errorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.NOT_NEED_HANDLING);
						errorHandlingBankService.insert(errorHandlingBank);
					} else if (!StringUtils.isEmpty(tradeInfo.getParentTradeSeq())) {
						RECONCILIATION_LOG.INFO(String.format("%s parentTradeSeq not null,is a reverse,not error handling %s", key, tradeInfo));
						ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
						errorHandlingBank.setBankId(bankId);
						errorHandlingBank.setReconciliationTime(yesterdayStr);
						errorHandlingBank.setTradeSeq(tradeInfo.getTradeSeq());
						errorHandlingBank.setRefNo(tradeInfo.getMerchantOrderNo());
						errorHandlingBank.setReason(MessageDef.ERROR_HANDLING_REASON.REVERSE);
						errorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.NOT_NEED_HANDLING);
						errorHandlingBankService.insert(errorHandlingBank);
					} else {
						ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
						errorHandlingBank.setBankId(bankId);
						errorHandlingBank.setReconciliationTime(yesterdayStr);
						errorHandlingBank.setTradeSeq(tradeInfo.getTradeSeq());
						errorHandlingBank.setRefNo(tradeInfo.getMerchantOrderNo());
						errorHandlingBank.setReason(MessageDef.ERROR_HANDLING_REASON.BANK_NO_INFO);
						errorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.UNHANDLING);
						errorHandlingBankService.insert(errorHandlingBank);
						ErrorHandlingVo errorHandlingVo = new ErrorHandlingVo();
						errorHandlingVo.setTradeInfo(tradeInfo);
						errorHandlingVo.setBankId(bankId);
						errorHandlingVo.setReconciliationTime(yesterdayStr);
						bankNotRecord++;
						if (ERROR_HANDLING_BANK_RECHARGE_AUTO == 1) {
							try {
								rabbitTemplate.convertAndSend(RabbitmqDef.ERROR_HANDLING.EXCHANGE, null, errorHandlingVo);
								RECONCILIATION_LOG.INFO(String.format("%s send to ERROR_HANDLING %s ", key, errorHandlingVo.toString()));
								ERROR_HANDLING_LOG.INFO(String.format("%s send info =%s", key, errorHandlingVo.toString()));
							} catch (Exception e) {
								log.error(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
								RECONCILIATION_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
								ERROR_HANDLING_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, tradeInfo.toString()), e);
								MessageAlarmFactory.getInstance().add(String.format("%s,doDiff sendToRabbitmq error,bankId=%s,date=%s,info =%s", key, bankId, yesterdayStr, tradeInfo.toString()));
							}
						}
					}
				}
			} else {
				if (tradeInfo == null) {
					ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
					errorHandlingBank.setBankId(bankId);
					errorHandlingBank.setReconciliationTime(yesterdayStr);
					errorHandlingBank.setTradeSeq(null);
					errorHandlingBank.setRefNo(bankTradeInfo.getRefNo());
					errorHandlingBank.setReason(MessageDef.ERROR_HANDLING_REASON.EPAY_NO_INFO);
					errorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.UNHANDLING);
					errorHandlingBankService.insert(errorHandlingBank);

					ErrorHandlingVo errorHandlingVo = new ErrorHandlingVo();
					errorHandlingVo.setBankTradeInfo(bankTradeInfo);
					errorHandlingVo.setBankId(bankId);
					errorHandlingVo.setReconciliationTime(yesterdayStr);
					epayNotRecord++;
					if (ERROR_HANDLING_BANK_RECHARGE_AUTO == 1) {
						try {
							rabbitTemplate.convertAndSend(RabbitmqDef.ERROR_HANDLING.EXCHANGE, null, errorHandlingVo);
							RECONCILIATION_LOG.INFO(String.format("%s send to ERROR_HANDLING %s", key, errorHandlingVo.toString()));
							ERROR_HANDLING_LOG.INFO(String.format("%s send info =%s", key, errorHandlingVo.toString()));
						} catch (Exception e) {
							log.error(String.format("%s sendToRabbitmq error info =%s", key, errorHandlingVo.toString()), e);
							RECONCILIATION_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, errorHandlingVo.toString()), e);
							ERROR_HANDLING_LOG.ERROR(String.format("%s sendToRabbitmq error info =%s", key, errorHandlingVo.toString()), e);
							MessageAlarmFactory.getInstance().add(String.format("%s,doDiff sendToRabbitmq error,bankId=%s,date=%s,info =%s", key, bankId, yesterdayStr, errorHandlingVo.toString()));
						}
					}

				} else {
					// 重复只插入一条
					if (isEpay) {
						ErrorHandlingBank errorHandlingBank = new ErrorHandlingBank();
						errorHandlingBank.setBankId(bankId);
						errorHandlingBank.setReconciliationTime(yesterdayStr);
						errorHandlingBank.setTradeSeq(tradeInfo.getTradeSeq());
						errorHandlingBank.setRefNo(tradeInfo.getMerchantOrderNo());
						errorHandlingBank.setReason(MessageDef.ERROR_HANDLING_REASON.AMOUNT_NOT_MATCH);
						errorHandlingBank.setHanndlingResult(MessageDef.ERROR_HANDLING_RESULT.UNHANDLING);
						errorHandlingBankService.insert(errorHandlingBank);
						moneyNotSameRecord++;
					} else {
						insertFlag = false;
					}
				}
				if (insertFlag) {
					errorHandlingBankTradeInfoService.insert(new ErrorHandlingBankTradeInfo(bankTradeInfo, bankId, yesterdayStr));
				}
			}
		} catch (Exception e) {
			log.error(String.format("%s doDiff error", key), e);
			RECONCILIATION_LOG.ERROR(String.format("%s doDiff error", key), e);
			throw new JobExecutionException(String.format("%s doDiff is error:%s", key, e.getMessage()));
		}
		return true;
	}

	protected boolean tradeInfoToDB() throws JobExecutionException {
		RedisUtil.delete_key(redisKey_epay);// redisTemplate.delete(redisKey_epay);
		TradeInfo tradeInfo = new TradeInfo();
		tradeInfo.setCreateTimeStartSer(DateUtil.convertDateToString(yesterday, "yyyy-MM-dd 00:00:00"));
		tradeInfo.setCreateTimeEndSer(DateUtil.convertDateToString(yesterday, "yyyy-MM-dd 23:59:59"));
		tradeInfo.setPayBankId(bankId);
		tradeInfo.setType(MessageDef.TRADE_TYPE.BANK_RECHARGE);
		int JOB_RECONCLIATION_BATCH_TO_DB_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_RECONCLIATION_BATCH_TO_DB_COUNT);
		int start = 0;
		int length = JOB_RECONCLIATION_BATCH_TO_DB_COUNT;
		try {
			while (true) {
				int pageNum = PageHelperUtil.getPageNum(start + "", length + "");
				int pageSize = PageHelperUtil.getPageSize(start + "", length + "");
				PageHelper.startPage(pageNum, pageSize);
				List<TradeInfo> tradeInfoList = tradeInfoService.selectPage(tradeInfo);
				if (tradeInfoList != null && tradeInfoList.size() == JOB_RECONCLIATION_BATCH_TO_DB_COUNT) {
					addRedisEpayPay(tradeInfoList);
					start += JOB_RECONCLIATION_BATCH_TO_DB_COUNT;
					length = JOB_RECONCLIATION_BATCH_TO_DB_COUNT;
				} else {
					addRedisEpayPay(tradeInfoList);
					break;
				}
			}
		} catch (Exception e) {
			log.error(String.format("%s tradeInfoToDB is error", key), e);
			RECONCILIATION_LOG.ERROR(String.format("%s tradeInfoToDB is error", key), e);
			throw new JobExecutionException(String.format("%s tradeInfoToDB is error:%s ", key, e.getMessage()));
		}
		return true;
	}

	protected ReconciliationBank getReconciliationBank() {
		ReconciliationBankKey reconciliationBankKey = new ReconciliationBankKey();
		reconciliationBankKey.setBankId(bankId);
		reconciliationBankKey.setReconciliationTime(yesterdayStr);

		ReconciliationBank reconciliationBank = reconciliationBankService.selectByPrimaryKey(reconciliationBankKey);
		if (reconciliationBank == null) {
			reconciliationBank = new ReconciliationBank();
			reconciliationBank.setBankId(reconciliationBankKey.getBankId());
			reconciliationBank.setReconciliationTime(reconciliationBankKey.getReconciliationTime());
			reconciliationBank.setStatus(MessageDef.RECONCILIATION_BANK_STATUS.UN_START);
			reconciliationBank.setCreateTime(Calendar.getInstance().getTime());
			int insertInt = reconciliationBankService.insert(reconciliationBank);
			if (insertInt != 1) {
				RECONCILIATION_LOG.INFO(String.format("%s insert ReconciliationBank return 0", key));
				return null;
			}
		}
		return reconciliationBank;
	}

	/**
	 * 读文件，将数据入库
	 * 
	 * @return
	 * @throws JobExecutionException
	 */
	protected boolean fileToDB() throws JobExecutionException {
		int JOB_RECONCLIATION_BATCH_TO_DB_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_RECONCLIATION_BATCH_TO_DB_COUNT);
		bankTradeInfoService.deleteAll(bankId);
		RedisUtil.delete_key(redisKey_bank);// redisTemplate.delete(redisKey_bank);
		File file = new File(filePath + fileName);
		if (!file.exists()) {
			RECONCILIATION_LOG.INFO(String.format("%s %s not exist", key, filePath + fileName));
			throw new JobExecutionException(String.format("%s fileToDB file(%s) not exist ", key, filePath + fileName));
			// return false;
		}
		BufferedReader reader = null;
		int line = 0;
		List<BankTradeInfo> bankTradeInfoList = new ArrayList<>();
		ReconciliationTestBankJobHeader reconciliationTestBankJobHeader = new ReconciliationTestBankJobHeader();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				if (line == 0) {
					boolean isDecode = reconciliationTestBankJobHeader.decode(tempString);
					if (!isDecode) {
						return false;
					}
					if (!reconciliationTestBankJobHeader.validate()) {
						return false;
					}
				} else {
					ReconciliationTestBankJobRecord reconciliationTestBankJobRecord = new ReconciliationTestBankJobRecord(bankId);
					boolean isDecode = reconciliationTestBankJobRecord.decode(tempString);
					if (!isDecode) {
						return false;
					}
					if (!reconciliationTestBankJobRecord.validate()) {
						return false;
					}
					bankTradeInfoList.add(reconciliationTestBankJobRecord.getBankTradeInfo());
					if (bankTradeInfoList.size() >= JOB_RECONCLIATION_BATCH_TO_DB_COUNT) {
						addRedisBank(bankTradeInfoList);
						bankTradeInfoService.insertBatch(bankTradeInfoList);
						bankTradeInfoList.clear();
					}
				}
				// 显示行号
				line++;
			}
			if (bankTradeInfoList.size() > 0) {
				allRecord = bankTradeInfoList.size();
				addRedisBank(bankTradeInfoList);
				bankTradeInfoService.insertBatch(bankTradeInfoList);
				bankTradeInfoList.clear();
			}
			reader.close();
		} catch (Exception e) {
			log.error(String.format("%s %s read exception", key, filePath + fileName), e);
			RECONCILIATION_LOG.ERROR(String.format("%s %s read exception", key, filePath + fileName), e);
			throw new JobExecutionException(String.format("%s fileToDB file(%s) read exception:%s", key, filePath + fileName, e.getMessage()));
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		if ((line - 1) != Integer.valueOf(reconciliationTestBankJobHeader.getRecordsNumber())) {
			RECONCILIATION_LOG.INFO(String.format("%s header's recordsNumber not match record's number", key, filePath + fileName));
			throw new JobExecutionException(String.format("%s fileToDB file(%s)  header's recordsNumber not match record's number", key, filePath + fileName));
		}
		return true;
	}

	private void addRedisEpayPay(List<TradeInfo> tradeInfoList) {
		RedisUtil.addRedisEpayPay(redisKey_epay, tradeInfoList);

	}

	private void addRedisBank(List<BankTradeInfo> bankTradeInfoList) {
		RedisUtil.addRedisBank(redisKey_bank, bankTradeInfoList);

	}

	protected boolean ftp() {
		try {
			if (StringUtils.isNotEmpty(sftpUserName)) {
				SFTPUtil sftp = new SFTPUtil(sftpUserName, sftpIpAddress, sftpPort, sftpKeyFile);
				sftp.login();
				sftp.download(sftpPath, fileName, filePath + fileName);
				sftp.logout();
			}
		} catch (Exception e) {
			log.error("", e);
			return false;
		}
		return true;
	}

	private boolean copyFile() throws JobExecutionException {
		try {
			File fileFolder = new File(filePath);
			if (!fileFolder.exists()) {
				fileFolder.mkdirs();
			}
			File fileFolderBank = new File(bankFilePath);
			if (!fileFolderBank.exists()) {
				fileFolderBank.mkdirs();
			}
			File bankFile = new File(bankFilePath + fileName);
			if (bankFile.exists()) {
				FileUtils.copyFile(bankFile, new File(filePath + fileName));
				return true;
			}
		} catch (Exception e) {
			log.error("", e);
			throw new JobExecutionException(String.format("%s copyFile is error:%s", key, e.getMessage()));
		}
		return false;
	}

	protected abstract void initdata();

	class ReconciliationTestBankJobHeader {
		private String reconciliationDate;
		private String bankId;
		private String recordsNumber;
		private String totalRechargeAmount;
		private String generateTime;

		public boolean decode(String tempString) throws JobExecutionException {
			if (StringUtils.isEmpty(tempString)) {
				RECONCILIATION_LOG.INFO(String.format("%s read head is empty", key));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) read head is empty", key, filePath + fileName));
			}
			if (tempString.length() != 43) {
				RECONCILIATION_LOG.INFO(String.format("%s read head length not 43,%s", key, tempString));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) read head length not 43,%s", key, filePath + fileName, tempString));
			}
			int index = 0;
			reconciliationDate = tempString.substring(index, 8).trim();
			index += 8;
			bankId = tempString.substring(index, index + 4).trim();
			index += 4;
			recordsNumber = tempString.substring(index, index + 8).trim();
			index += 8;
			totalRechargeAmount = tempString.substring(index, index + 17).trim();
			index += 17;
			generateTime = tempString.substring(index, index + 6).trim();
			return true;
		}

		public boolean validate() throws JobExecutionException {
			if (StringUtils.isEmpty(reconciliationDate)) {
				RECONCILIATION_LOG.INFO(String.format("%s reconciliationDate=%s must not empty", key, reconciliationDate));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) reconciliationDate=%s must not empty ", key, filePath + fileName, reconciliationDate));
			}
			if (reconciliationDate.length() != 8) {
				RECONCILIATION_LOG.INFO(String.format("%s reconciliationDate=%s len must 8", key, reconciliationDate));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) reconciliationDate=%s len must 8 ", key, filePath + fileName, reconciliationDate));
			}
			if (StringUtils.isEmpty(bankId)) {
				RECONCILIATION_LOG.INFO(String.format("%s bankId=%s must not empty", key, bankId));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) bankId=%s must not empty ", key, filePath + fileName, bankId));
			}
			if (StringUtils.isEmpty(recordsNumber)) {
				RECONCILIATION_LOG.INFO(String.format("%s recordsNumber=%s must not empty", key, recordsNumber));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) recordsNumber=%s must not empty ", key, filePath + fileName, recordsNumber));
			}
			if (!StringUtils.isNumeric(recordsNumber)) {
				RECONCILIATION_LOG.INFO(String.format("%s recordsNumber=%s must numeric", key, recordsNumber));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) recordsNumber=%s must numeric", key, filePath + fileName, recordsNumber));
			}
			if (StringUtils.isEmpty(generateTime)) {
				RECONCILIATION_LOG.INFO(String.format("%s generateTime=%s must not empty", key, generateTime));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) generateTime=%s must not empty", key, filePath + fileName, generateTime));
			}
			if (generateTime.length() != 6) {
				RECONCILIATION_LOG.INFO(String.format("%s generateTime=%s len must 6", key, generateTime));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) generateTime=%s len must 6", key, filePath + fileName, generateTime));
			}

			return true;
		}

		public String getReconciliationDate() {
			return reconciliationDate;
		}

		public void setReconciliationDate(String reconciliationDate) {
			this.reconciliationDate = reconciliationDate;
		}

		public String getBankId() {
			return bankId;
		}

		public void setBankId(String bankId) {
			this.bankId = bankId;
		}

		public String getRecordsNumber() {
			return recordsNumber;
		}

		public void setRecordsNumber(String recordsNumber) {
			this.recordsNumber = recordsNumber;
		}

		public String getTotalRechargeAmount() {
			return totalRechargeAmount;
		}

		public void setTotalRechargeAmount(String totalRechargeAmount) {
			this.totalRechargeAmount = totalRechargeAmount;
		}

		public String getGenerateTime() {
			return generateTime;
		}

		public void setGenerateTime(String generateTime) {
			this.generateTime = generateTime;
		}

	}

	class ReconciliationTestBankJobRecord {
		BankTradeInfo bankTradeInfo = new BankTradeInfo();

		public ReconciliationTestBankJobRecord(String bankId) {
			bankTradeInfo.setBankId(bankId);
		}

		public boolean decode(String tempString) throws JobExecutionException {
			if (StringUtils.isEmpty(tempString)) {
				RECONCILIATION_LOG.INFO(String.format("%s read record is empty", key));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) read head is empty", key, filePath + fileName));
			}
			if (tempString.length() != 82) {
				RECONCILIATION_LOG.INFO(String.format("%s read record length not 82,%s", key, tempString));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) read head length not 43,%s", key, filePath + fileName, tempString));
			}
			int index = 0;
			bankTradeInfo.setRefNo(tempString.substring(index, index + 32).trim());
			index += 32;
			bankTradeInfo.setVid(tempString.substring(index, index + 12).trim());
			index += 12;
			bankTradeInfo.setTransactionDate(tempString.substring(index, index + 14).trim());
			index += 14;
			bankTradeInfo.setCurrencyType(tempString.substring(index, index + 3).trim());
			index += 3;
			bankTradeInfo.setTransactionAmount(tempString.substring(index, index + 17).trim());
			index += 17;
			bankTradeInfo.setTransactionType(tempString.substring(index, index + 2).trim());
			index += 2;
			bankTradeInfo.setChannelType(tempString.substring(index, index + 2).trim());
			return true;
		}

		public boolean validate() throws JobExecutionException {
			if (StringUtils.isEmpty(bankTradeInfo.getRefNo())) {
				RECONCILIATION_LOG.INFO(String.format("%s refNo=%s must not empty", key, bankTradeInfo.getRefNo()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) refNo=%s must not empty", key, filePath + fileName, bankTradeInfo.getRefNo()));
			}
			if (StringUtils.isEmpty(bankTradeInfo.getVid())) {
				RECONCILIATION_LOG.INFO(String.format("%s vid=%s must not empty", key, bankTradeInfo.getVid()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) vid=%s must not empty", key, filePath + fileName, bankTradeInfo.getVid()));
			}
			if (StringUtils.isEmpty(bankTradeInfo.getTransactionDate())) {
				RECONCILIATION_LOG.INFO(String.format("%s transactionDate=%s must not empty", key, bankTradeInfo.getTransactionDate()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) transactionDate=%s must not empty", key, filePath + fileName, bankTradeInfo.getTransactionDate()));
			}
			if (bankTradeInfo.getTransactionDate().length() != 14) {
				RECONCILIATION_LOG.INFO(String.format("%s transactionDate=%s len must 14", key, bankTradeInfo.getTransactionDate()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) transactionDate=%s len must 14", key, filePath + fileName, bankTradeInfo.getTransactionDate()));
			}
			if (StringUtils.isEmpty(bankTradeInfo.getCurrencyType())) {
				RECONCILIATION_LOG.INFO(String.format("%s currencyType=%s must not empty", key, bankTradeInfo.getCurrencyType()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) currencyType=%s must not empty", key, filePath + fileName, bankTradeInfo.getCurrencyType()));
			}
			if (StringUtils.isEmpty(bankTradeInfo.getTransactionAmount())) {
				RECONCILIATION_LOG.INFO(String.format("%s transactionAmount=%s must not empty", key, bankTradeInfo.getTransactionAmount()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) transactionAmount=%s must not empty", key, filePath + fileName, bankTradeInfo.getTransactionAmount()));
			}
			if (!StringUtils.isNumeric(bankTradeInfo.getTransactionAmount())) {
				RECONCILIATION_LOG.INFO(String.format("%s transactionAmount=%s must numeric", key, bankTradeInfo.getTransactionAmount()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) transactionAmount=%s must numeric", key, filePath + fileName, bankTradeInfo.getTransactionAmount()));
			}
			Double mulD = Double.valueOf(bankTradeInfo.getTransactionAmount());
			String amount = mulD.toString();
			if (amount.contains(".")) {
				amount = amount.substring(0, amount.indexOf("."));
			}
			bankTradeInfo.setTransactionAmount(amount);
			if (StringUtils.isEmpty(bankTradeInfo.getTransactionType())) {
				RECONCILIATION_LOG.INFO(String.format("%s transactionType=%s must not empty", key, bankTradeInfo.getTransactionType()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) transactionType=%s must not empty", key, filePath + fileName, bankTradeInfo.getTransactionType()));
			}
			if (!"01".equals(bankTradeInfo.getTransactionType()) && !"02".equals(bankTradeInfo.getTransactionType())) {
				RECONCILIATION_LOG.INFO(String.format("%s transactionType=%s must 01 or 02", key, bankTradeInfo.getTransactionType()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) transactionType=%s must 01 or 02", key, filePath + fileName, bankTradeInfo.getTransactionType()));
			}
			if (StringUtils.isEmpty(bankTradeInfo.getChannelType())) {
				RECONCILIATION_LOG.INFO(String.format("%s channelType=%s must not empty", key, bankTradeInfo.getChannelType()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) channelType=%s must not empty", key, filePath + fileName, bankTradeInfo.getChannelType()));
			}
			if (!"01".equals(bankTradeInfo.getChannelType()) && !"02".equals(bankTradeInfo.getChannelType()) && !"03".equals(bankTradeInfo.getChannelType()) && !"04".equals(bankTradeInfo.getChannelType())) {
				RECONCILIATION_LOG.INFO(String.format("%s channelType=%s must 01, 02, 03 or 04", key, bankTradeInfo.getChannelType()));
				throw new JobExecutionException(String.format("%s fileToDB file(%s) channelType=%s must 01, 02, 03 or 04", key, filePath + fileName, bankTradeInfo.getChannelType()));
			}
			return true;
		}

		public BankTradeInfo getBankTradeInfo() {
			return bankTradeInfo;
		}

		public void setBankTradeInfo(BankTradeInfo bankTradeInfo) {
			this.bankTradeInfo = bankTradeInfo;
		}

	}

}
