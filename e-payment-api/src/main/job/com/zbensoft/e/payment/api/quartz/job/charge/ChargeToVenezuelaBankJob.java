package com.zbensoft.e.payment.api.quartz.job.charge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.e.payment.core.pay.BankID;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserBankCardService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeService;
import com.zbensoft.e.payment.api.service.api.MerchantUserBankCardService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeBatch;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeHeader;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeRecord;
import com.zbensoft.e.payment.api.vo.bankCharge.BankChargeTotal;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.BankInfo;
import com.zbensoft.e.payment.db.domain.ConsumerUserBankCard;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 交易数据上传，每天早上10点执行。
 * 
 * 0 0 10 * * ?
 * 
 * @author Wang Chenyang
 *
 */
public class ChargeToVenezuelaBankJob  implements Job{
	
	private static final Logger log = LoggerFactory.getLogger(ChargeToVenezuelaBankJob.class);

	private static String key = "ChargeToVenezuelaBankJob";
	TradeInfoService tradeInfoService=SpringBeanUtil.getBean(TradeInfoService.class);
	ConsumerUserClapService consumerUserClapService =SpringBeanUtil.getBean(ConsumerUserClapService.class);
	MerchantUserService merchantUserService=SpringBeanUtil.getBean(MerchantUserService.class);
	MerchantEmployeeService merchantEmployeeService=SpringBeanUtil.getBean(MerchantEmployeeService.class);
	MerchantUserBankCardService merchantUserBankCardService=SpringBeanUtil.getBean(MerchantUserBankCardService.class);
	BankInfoService bankInfoService =SpringBeanUtil.getBean(BankInfoService.class);
	ConsumerUserBankCardService consumerUserBankCardService=SpringBeanUtil.getBean(ConsumerUserBankCardService.class);
	
	
	String JOB_CHARGE_FILE_PREFIX=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_FILE_PREFIX);
	String JOB_CHARGE_FILE_DATEFORMATE=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_FILE_DATEFORMATE);
	String JOB_CHARGE_FILE_PATH=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_FILE_PATH);
	int JOB_CHARGE_FILE_TRADE_DAY_BEFORE=SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CHARGE_FILE_TRADE_DAY_BEFORE);
	String JOB_CHARGE_HEADER_IDENTIFIER_REGISTRY=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_HEADER_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_HEADER_NUMBER_NEGOTIATION=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_HEADER_NUMBER_NEGOTIATION);
	String JOB_CHARGE_RIF_OF_CANTV=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_RIF_OF_CANTV);
	String JOB_CHARGE_TOTAL_IDENTIFIER_REGISTRY=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_TOTAL_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_RECORD_IDENTIFIER_REGISTRY= SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_RECORD_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_BATH_IDENTIFIER_REGISTRY= SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_BATH_IDENTIFIER_REGISTRY);
	String JOB_CHARGE_PAYER_NAME_OF_CANTV=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_PAYER_NAME_OF_CANTV);
	String JOB_CHARGE_BANK_0102_ACCOUNT_OF_CANTV=SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CHARGE_BANK_0102_ACCOUNT_OF_CANTV);
	
	
	private BankChargeTotal bankChargeTotal=new BankChargeTotal();
	private String dayStr=DateUtil.convertDateToString(DateUtil.addDate(new Date(), JOB_CHARGE_FILE_TRADE_DAY_BEFORE, 3), JOB_CHARGE_FILE_DATEFORMATE);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		boolean isSucc = true;
		
		
		isSucc=getTotal();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s getTotal fail", key));
			throw new JobExecutionException(String.format("%s getTotal is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s getTotal Success", key));
		}
		
		isSucc=wirteFile();
		if (!isSucc) {
			
			TASK_LOG.INFO(String.format("%s wirteFile fail", key));
			throw new JobExecutionException(String.format("%s wirteFile is fail", key));
		} else {
			TASK_LOG.INFO(String.format("%s wirteFile Success", key));
		}
		
		
		TASK_LOG.INFO(String.format("%s End", key));
	}
	/**
	 * 获取文件头
	 * @return
	 */
	private boolean getHeader(BankChargeHeader bankChargeHeader) {
		bankChargeHeader.setIdentifierRegistry( addSpase(JOB_CHARGE_HEADER_IDENTIFIER_REGISTRY, 8));
		bankChargeHeader.setReferenceNumber(DateUtil.convertDateToString(new Date(), DateUtil.DATE_FORMAT_THREE));
		if(JOB_CHARGE_HEADER_NUMBER_NEGOTIATION.length()!=8){
			TASK_LOG.INFO(String.format("%s getHeader is error, 'JOB_CHARGE_HEADER_NUMBER_NEGOTIATION' length not enough, now is %s", key,JOB_CHARGE_HEADER_NUMBER_NEGOTIATION));
			return false;
		}
		bankChargeHeader.setNumberNegotiation(JOB_CHARGE_HEADER_NUMBER_NEGOTIATION);
		if(JOB_CHARGE_RIF_OF_CANTV.length()!=10){
			TASK_LOG.INFO(String.format("%s getHeader is error, 'JOB_CHARGE_RIF_OF_CANTV' length not enough, now is %s", key,JOB_CHARGE_RIF_OF_CANTV));
			return false;
		}
		bankChargeHeader.setRif(JOB_CHARGE_RIF_OF_CANTV);
		bankChargeHeader.setDateOfPaymen(DateUtil.convertDateToString(DateUtil.addDate(new Date(), JOB_CHARGE_FILE_TRADE_DAY_BEFORE, 3), DateUtil.DATE_FORMAT_TWENTY_TWO));
		bankChargeHeader.setDateOfShipment(DateUtil.convertDateToString(new Date(), DateUtil.DATE_FORMAT_TWENTY_TWO));
		return true;
		
	}
	/**
	 * 获取当天记录的总和
	 * @return
	 */
	private boolean getTotal() {
		try {
			bankChargeTotal.setIdentifierRegistry(JOB_CHARGE_TOTAL_IDENTIFIER_REGISTRY);
			TradeInfo tradeInfoSer = new TradeInfo();
			tradeInfoSer.setType(MessageDef.TRADE_TYPE.CHARGE);// type未提现
			tradeInfoSer.setStatus(MessageDef.TRADE_STATUS.PROCESSING);// 状态为进行中
			tradeInfoSer.setCreateTimeStartSer(DateUtil.getDayStrTenTime(dayStr, DateUtil.DATE_FORMAT_THREE, true, DateUtil.DATE_FORMAT_FIVE));
			tradeInfoSer.setCreateTimeEndSer(DateUtil.getDayStrTenTime(dayStr, DateUtil.DATE_FORMAT_THREE, false, DateUtil.DATE_FORMAT_FIVE));
			Double totalAmount = tradeInfoService.sumByDay(tradeInfoSer);
			if (totalAmount != null) {
				bankChargeTotal.setTotalBatchAmount(amountToString(new BigDecimal(totalAmount), 18, 2));
			}else{
				bankChargeTotal.setTotalBatchAmount(amountToString(BigDecimal.ZERO, 18, 2));
			}
			return true;
		} catch (Exception e) {
			log.error(String.format("%s getDbTradeList is empty, no data today", key),e);
			TASK_LOG.INFO(String.format("%s getDbTradeList is empty, no data today", key));
		}
		return false;
	}
	/**
	 * 写文件方法
	 * @return
	 */
	private boolean wirteFile() {
		String fileName = JOB_CHARGE_FILE_PREFIX+ dayStr+".txt";
		BufferedWriter bufferedWriter=null;
		 try {
			File file = new File(JOB_CHARGE_FILE_PATH+fileName);// 指定要写入的文件  
			if(!file.getParentFile().exists()&&file.getParentFile().isDirectory()){
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {// 如果文件不存在则创建
			     file.createNewFile();
			 }
			    // 获取该文件的缓冲输出流
			bufferedWriter = new BufferedWriter(new FileWriter(file));
			BankChargeHeader header= new BankChargeHeader();
			if(!getHeader(header)){//获取头信息，文件头长度不对，允许生成文件
				return false;
			}
			bufferedWriter.write(header.toString());//写入头
	        bufferedWriter.newLine();// 表示换行
			int start=0;
			int batchCount=0;
			int JOB_CHARGE_FILE_WRITE_ONETIME=SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CHARGE_FILE_WRITE_ONETIME);
			List<TradeInfo> getDbTradeList=new ArrayList<>();
			TradeInfo tradeInfoSer=new TradeInfo();
			tradeInfoSer.setType(MessageDef.TRADE_TYPE.CHARGE);//type未提现
			tradeInfoSer.setStatus(MessageDef.TRADE_STATUS.PROCESSING);//状态为进行中
			tradeInfoSer.setCreateTimeStartSer(DateUtil.getDayStrTenTime(dayStr, DateUtil.DATE_FORMAT_THREE, true, DateUtil.DATE_FORMAT_FIVE));
			tradeInfoSer.setCreateTimeEndSer(DateUtil.getDayStrTenTime(dayStr,DateUtil.DATE_FORMAT_THREE, false, DateUtil.DATE_FORMAT_FIVE));
			while(true){
				
				int pageNum = PageHelperUtil.getPageNum(String.valueOf(start), String.valueOf(JOB_CHARGE_FILE_WRITE_ONETIME));
				int pageSize = PageHelperUtil.getPageSize(String.valueOf(start), String.valueOf(JOB_CHARGE_FILE_WRITE_ONETIME));
				PageHelper.startPage(pageNum, pageSize);
				getDbTradeList=tradeInfoService.selectByDayLmit(tradeInfoSer);
				//2017-12-11 逻辑修改 WangChenyang
				if (getDbTradeList != null&&getDbTradeList.size()>0){
					boolean isSucc=true;
					isSucc=writeBankBatchRecord(bufferedWriter,getDbTradeList);
					if (!isSucc) {
						TASK_LOG.INFO(String.format("%s wirteFile fail", key));
						throw new JobExecutionException(String.format("%s wirteFile is fail", key));
					} else {
						TASK_LOG.INFO(String.format("%s wirteFile Success", key));
					}
					
					start += getDbTradeList.size();
					batchCount++;
				}
				
				if (getDbTradeList == null || getDbTradeList.size() != JOB_CHARGE_FILE_WRITE_ONETIME) {
					if (batchCount != 0) {
						bankChargeTotal.setTotalDebitRecords(addLeftZero(batchCount, 5));
						bankChargeTotal.setTotalCreditsRecords(addLeftZero(start + getDbTradeList.size(), 5));
						if (bankChargeTotal != null && bankChargeTotal.getTotalBatchAmount() != null) {
							bufferedWriter.write(bankChargeTotal.toString() + "\n");
						}
						
					} else {
						TASK_LOG.INFO(String.format("%s getDbTradeList is empty, no data today", key));
						String alarmMsg=String.format("%s getDbTradeList is empty, no Charge data today!", key);
						MessageAlarmFactory.getInstance().add(alarmMsg);//增加告警
					}
					return true;
				}
				
		
			}
			
			
		} catch (Exception e) {
			log.error(String.format("%s wirteFile Exception", key), e);
			TASK_LOG.ERROR(String.format("%s wirteFile Exception", key), e);
			return false;
		} finally {
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				log.error(String.format("%s flush buffereWriter Exception", key), e);
				TASK_LOG.ERROR(String.format("%s flush buffereWriter Exception", key), e);
			}
		}
		
	}
	
	/**
	 * 写入交易记录和批次记录
	 * @param bufferedWriter
	 * @param getDbTradeList
	 * @return
	 */
	private boolean writeBankBatchRecord(BufferedWriter bufferedWriter, List<TradeInfo> getDbTradeList) {
		if(getDbTradeList!=null&&getDbTradeList.size()>0){
			try {
				BankChargeBatch bankChargeBatch=new BankChargeBatch();
				List<BankChargeRecord> bankChargeRecordList=new ArrayList<>();
				BigDecimal batchAmount=new BigDecimal(0d);
				for (TradeInfo tradeInfo : getDbTradeList) {
					if(tradeInfo.getRecvSumAmount()!=null){
						batchAmount=batchAmount.add(new BigDecimal(tradeInfo.getRecvSumAmount()));
					}
					BankChargeRecord bankChargeRecord=new BankChargeRecord();
					bankChargeRecord.setIdentifierRegistry(addSpase(JOB_CHARGE_RECORD_IDENTIFIER_REGISTRY, 8));
					if(tradeInfo.getMerchantOrderNo().length()!=8){
						TASK_LOG.ERROR(String.format("%s setReferenceNumber fail, length is not 8, now is %s", key,tradeInfo.getMerchantOrderNo()));
						continue;
					}
					bankChargeRecord.setReferenceNumber(tradeInfo.getMerchantOrderNo());
					boolean isSucc=true;
					isSucc=setBankCardInfo(tradeInfo,bankChargeRecord);
					if(!isSucc){
						TASK_LOG.ERROR(String.format("%s setBankCardInfo fail ", key));
						continue;
					}
					
					bankChargeRecord.setAmount(amountToString(new BigDecimal(tradeInfo.getRecvSumAmount()), MessageDef.BANK_AMOUNT.LENGTH,MessageDef.BANK_AMOUNT.DECIMAL));
					bankChargeRecord.setDurationOfCheck(addSpase("",3));
					bankChargeRecord.setAgencyBanking(addSpase("",4));
					bankChargeRecord.seteMail(addSpase("",50));
					bankChargeRecordList.add(bankChargeRecord);
				}
				//填写批次信息
				bankChargeBatch.setIdentifierRegistry(addSpase(JOB_CHARGE_BATH_IDENTIFIER_REGISTRY,8));
				bankChargeBatch.setReferenceNumber(generateBatchId(BATCH_ID, 8));
				bankChargeBatch.setRif(JOB_CHARGE_RIF_OF_CANTV);
				bankChargeBatch.setPayerName(addSpase(JOB_CHARGE_PAYER_NAME_OF_CANTV,35));
				bankChargeBatch.setValueDate(DateUtil.convertDateToString(DateUtil.addDate(new Date(), JOB_CHARGE_FILE_TRADE_DAY_BEFORE, 3), DateUtil.DATE_FORMAT_TWENTY_TWO));
				bankChargeBatch.setAccountType(MessageDef.BANK_ACCOUNT_TYPE.CURRENT_ACCOUNT);
				bankChargeBatch.setAccountNumber(JOB_CHARGE_BANK_0102_ACCOUNT_OF_CANTV);
				bankChargeBatch.setAmount(amountToString(batchAmount, MessageDef.BANK_AMOUNT.LENGTH,MessageDef.BANK_AMOUNT.DECIMAL));
				bankChargeBatch.setCurrency(MessageDef.CHARGE_FIXED_VALUE.BATCH_CURRENCY);
				bankChargeBatch.setPaymentType(MessageDef.CHARGE_FIXED_VALUE.BATCH_TYPE_OF_PAYMENT);
				
				
				bufferedWriter.write(bankChargeBatch.toString());
				bufferedWriter.newLine();
				if(bankChargeRecordList!=null){
					for (BankChargeRecord record : bankChargeRecordList) {
						bufferedWriter.write(record.toString());
						bufferedWriter.newLine();
					}
				}
				return true;
			} catch (Exception e) {
				log.error(String.format("%s writeBankBatchRecord Exception", key), e);
				TASK_LOG.ERROR(String.format("%s writeBankBatchRecord Exception", key), e);
			}
			
		}
		
		return false;
	}

	/**
	 * 填写银行卡信息
	 * @param tradeInfo
	 * @param bankChargeRecord
	 * @return
	 */
	private boolean setBankCardInfo(TradeInfo tradeInfo, BankChargeRecord bankChargeRecord) {
		
		if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.MERCHANT_STRING)) {//商户提现
			MerchantUserBankCard merchantUserBankCardSer = new MerchantUserBankCard();
			merchantUserBankCardSer.setDeleteFlag(0);
			merchantUserBankCardSer.setBankId(tradeInfo.getRecvBankId());
			merchantUserBankCardSer.setCardNo(tradeInfo.getRecvBankCardNo());
			merchantUserBankCardSer.setUserId(tradeInfo.getPayUserId());
			MerchantUserBankCard bindCard = merchantUserBankCardService.selectByUserIdBankCard(merchantUserBankCardSer);
			if (bindCard != null && bindCard.getIdNumber() != null) {
				bankChargeRecord.setRif_ci(getVIDForBank(bindCard.getIdNumber()));
				bankChargeRecord.setName(addSpase(bindCard.getHolerName(), 30));
				bankChargeRecord.setAccountType(MessageDef.BANK_ACCOUNT_TYPE.CURRENT_ACCOUNT);
				bankChargeRecord.setAccountNumber(addSpase(bindCard.getCardNo(), 20));
				boolean isSucc = true;
				isSucc = setPaymentTypeAndBankCode(bankChargeRecord, bindCard.getBankId());
				if (!isSucc) {
					TASK_LOG.INFO(String.format("%s setPaymentTypeAndBankCode fail, %s", key,tradeInfo.toString()));
					return false;
				}
				return true;
			}
		}else if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)){//用户提现

			ConsumerUserBankCard consumerUserBankCardSer = new ConsumerUserBankCard();
			consumerUserBankCardSer.setDeleteFlag(0);
			consumerUserBankCardSer.setBankId(tradeInfo.getRecvBankId());
			consumerUserBankCardSer.setCardNo(tradeInfo.getRecvBankCardNo());
			consumerUserBankCardSer.setUserId(tradeInfo.getPayUserId());
			ConsumerUserBankCard bindCard = consumerUserBankCardService.selectByUserIdBankCard(consumerUserBankCardSer);
			if (bindCard != null && bindCard.getIdNumber() != null) {
				bankChargeRecord.setRif_ci(getVIDForBank(bindCard.getIdNumber()));
				bankChargeRecord.setName(addSpase(bindCard.getHolerName(), 30));
				bankChargeRecord.setAccountType(MessageDef.BANK_ACCOUNT_TYPE.CURRENT_ACCOUNT);
				bankChargeRecord.setAccountNumber(bindCard.getCardNo());
				boolean isSucc = true;
				isSucc = setPaymentTypeAndBankCode(bankChargeRecord, bindCard.getBankId());
				if (!isSucc) {
					TASK_LOG.INFO(String.format("%s setPaymentTypeAndBankCode fail", key));
					return false;
				}
				return true;
			}
		
		}
		return false;
	}
	/**
	 * 银行用VID格式规整
	 * @param idNumber
	 * @return
	 */
	private  String getVIDForBank(String idNumber) {
		String resultVid=idNumber;
		   if(idNumber!=null&&idNumber.length()<10){
			   String lettter=idNumber.substring(0, 1);
			   String number=idNumber.substring(1);
			   resultVid = lettter+"00000".substring(0,(9-number.length())) + number;
		   }
		return resultVid;
	}
	
	/**
	 * 右补空格
	 * @param files
	 * @param length
	 * @return
	 */
	private  String addSpase(String fiels, int length) {
		String resultFiles=fiels;
		if(fiels!=null){
			if(fiels.length()<=length){
				for (int i = 0; i < (length-fiels.length()); i++) {
					resultFiles+=" ";
				}
			}else{
				resultFiles=fiels.substring(0,length);
			}
		}else{
			for (int i = 0; i < length; i++) {
				resultFiles+=" ";
			}
		}
		return resultFiles;
	}
	
	/**
	 * 左补0
	 * @param number
	 * @param length
	 * @return
	 */
	private  String addLeftZero(int number, int length) {
		String result=String.valueOf(number);
			if(result.length()<=length){
				result ="00000".substring(0,(5-result.length())) + result;
			}
		return result;
	}
	
	
	/**
	 * 获取 paymentType和BankCode
	 * @param bankChargeRecord
	 * @param bankId
	 * @return
	 */
	private boolean setPaymentTypeAndBankCode(BankChargeRecord bankChargeRecord, String bankId) {
		if (bankId != null ) {
			try {
				BankInfo bankInfo = bankInfoService.selectByBankId(bankId);
				if (bankInfo != null && bankInfo.getCode() != null) {
					bankChargeRecord.setBankCode(addSpase(bankInfo.getCode(),12));
					bankChargeRecord.setPaymentType(MessageDef.BANK_PAYMENT_TYPE.OTHER_BANK);
					if (BankID.BANCO_DE_VENEZUELA.equals(bankId)) {
						bankChargeRecord.setPaymentType(MessageDef.BANK_PAYMENT_TYPE.SAME_BANK);
					}
					return true;
				}
			} catch (Exception e) {
				log.error(String.format("%s selectByBankId Exception", key), e);
				TASK_LOG.ERROR(String.format("%s selectByBankId Exception", key), e);
				return false;
			}
		}
		return false;
	}
	/**
	 * 数据库交易记录转ClapTrad记录
	 * @param tradeInfo
	 * @return
	 */
	private static String amountToString(BigDecimal batchAmount,int length,int decimal) {
		String totalAmountStr ="";
		if(batchAmount==null){
			batchAmount=BigDecimal.ZERO;
		}
		
		DecimalFormat decimalFormat = new DecimalFormat("###0.00");//格式化设置 
		totalAmountStr = decimalFormat.format(batchAmount);
		totalAmountStr = totalAmountStr.replace(".", ",");
		if (totalAmountStr.length() <= length) {
			totalAmountStr = "00000000000000000000000000".substring(0,(length-totalAmountStr.length())) + totalAmountStr;
		}else{
			return null;
		}
		return totalAmountStr;
	}
	
	/** batch番号**/
	public static AtomicLong BATCH_ID = new AtomicLong(1);
	/**
	 * 获取batch的番号
	 * @param SEQ
	 * @param len
	 * @return
	 */
	private static String generateBatchId(AtomicLong SEQ, int len) {
		String xulie = String.valueOf(SEQ.getAndAdd(1));
		String id = "000000000000".substring(0, len - xulie.length()) + xulie;
		 if (len == 8) {
			if (SEQ.get() > 99999999l) {
				SEQ.set(1);
			}
		}
		return id;
	}
	
//	public static void main(String[] args) {
//		System.out.println(System.currentTimeMillis());
//		try {
//			Thread.sleep(200);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(System.currentTimeMillis());
//		
//	}
}
