package com.zbensoft.e.payment.api.quartz.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.DailyBillConsumptionDetailService;
import com.zbensoft.e.payment.api.service.api.DailyBillService;
import com.zbensoft.e.payment.api.service.api.DailyBillTransferDetailService;
import com.zbensoft.e.payment.api.service.api.MerchantTradeService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.DailyBill;
import com.zbensoft.e.payment.db.domain.DailyBillConsumptionDetail;
import com.zbensoft.e.payment.db.domain.DailyBillTransferDetail;
import com.zbensoft.e.payment.db.domain.MerchantTrade;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 卖家日账单，每天6点执行一次
 * 
 * 0 0 6 * * ?
 * 
 * @author xieqiang
 *
 */
public class BillDailySellerJob implements Job {
	
	private static final Logger log = LoggerFactory.getLogger(BillDailySellerJob.class);

	private static String key = "BillDaliySellerJob";

	private MerchantUserService merchantUserService = SpringBeanUtil.getBean(MerchantUserService.class);
	private MerchantTradeService merchantTradeService = SpringBeanUtil.getBean(MerchantTradeService.class);
	private TradeInfoService tradeInfoService = SpringBeanUtil.getBean(TradeInfoService.class);

	private DailyBillService dailyBillService = SpringBeanUtil.getBean(DailyBillService.class);
	private DailyBillConsumptionDetailService dailyBillConsumptionDetailService = SpringBeanUtil.getBean(DailyBillConsumptionDetailService.class);
	private DailyBillTransferDetailService dailyBillTransferDetailService = SpringBeanUtil.getBean(DailyBillTransferDetailService.class);

	private Date now = null;
	private Date yesterday = null;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));

		now = Calendar.getInstance().getTime();
		yesterday = DateUtils.addDays(now, -1);
		String billDate = DateUtil.convertDateToString(yesterday, "yyyy-MM-dd");
		dailyBillService.deleteByBillDate(billDate);
		dailyBillConsumptionDetailService.deleteByBillDate(billDate);
		dailyBillTransferDetailService.deleteByBillDate(billDate);

		int JOB_BILL_DALIY_SELLER_GET_USER_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_BILL_DALIY_SELLER_GET_USER_COUNT);
		int start = 0;
		int length = JOB_BILL_DALIY_SELLER_GET_USER_COUNT;
		try {
			while (true) {
				try {
					TASK_LOG.INFO(String.format("%s process %s - %s ", key, start, start + length - 1));
					int pageNum = PageHelperUtil.getPageNum(start + "", length + "");
					int pageSize = PageHelperUtil.getPageSize(start + "", length + "");
					PageHelper.startPage(pageNum, pageSize);
					List<MerchantUser> merchantUserList = merchantUserService.selectPage(new MerchantUser());
					if (merchantUserList != null && merchantUserList.size() == JOB_BILL_DALIY_SELLER_GET_USER_COUNT) {

						daliyBill(merchantUserList);

						start += JOB_BILL_DALIY_SELLER_GET_USER_COUNT;
						length = JOB_BILL_DALIY_SELLER_GET_USER_COUNT;
					} else {
						daliyBill(merchantUserList);
						break;
					}
				} catch (Exception e) {
					log.error(String.format("%s is error", key), e);
					TASK_LOG.ERROR(String.format("%s is error", key), e);
				}
			}
		} catch (Exception e) {
			log.error(String.format("%s is error", key), e);
			TASK_LOG.ERROR(String.format("%s is error", key), e);
		}
		TASK_LOG.INFO(String.format("%s end", key));
	}

	private void daliyBill(List<MerchantUser> merchantUserList) {
		for (MerchantUser merchantUser : merchantUserList) {
			try {

				MerchantTrade merchantTrade = new MerchantTrade();
				merchantTrade.setCreateTimeStartSer(DateUtil.convertDateToString(yesterday) + " 00:00:00");
				merchantTrade.setCreateTimeEndSer(DateUtil.convertDateToString(yesterday) + " 23:59:59");
				merchantTrade.setUserId(merchantUser.getUserId());
				List<MerchantTrade> merchantTradeList = merchantTradeService.selectPage(merchantTrade);
				Double recharge = 0d;
				Double charge = 0d;
				Double consumption = 0d;
				Double bankRecharge = 0d;
				if (merchantTradeList != null && merchantTradeList.size() > 0) {
					for (MerchantTrade merchantTrade2 : merchantTradeList) {
						TradeInfo tradeInfo = tradeInfoService.selectByPrimaryKey(merchantTrade2.getTradeSeq());
						if (tradeInfo != null && tradeInfo.getStatus() == MessageDef.TRADE_STATUS.SUCC) {
							switch (tradeInfo.getType()) {
							case MessageDef.TRADE_TYPE.RECHARGE:
								recharge = DoubleUtil.add(recharge, tradeInfo.getRecvSumAmount());
								break;
							case MessageDef.TRADE_TYPE.CHARGE:
								charge = DoubleUtil.add(charge, tradeInfo.getPaySumAmount());
								break;
							case MessageDef.TRADE_TYPE.CONSUMPTION:
								consumption = DoubleUtil.add(consumption, tradeInfo.getRecvSumAmount());
								break;
							case MessageDef.TRADE_TYPE.BANK_RECHARGE:
								bankRecharge = DoubleUtil.add(bankRecharge, tradeInfo.getRecvSumAmount());
								break;
							default:
								break;
							}
						}
					}
				}
				DailyBill dailyBill = new DailyBill();
				dailyBill.setUserId(merchantUser.getUserId());
				dailyBill.setBillDate(DateUtil.convertDateToString(yesterday));
				dailyBill.setBorrow(DoubleUtil.add(consumption, DoubleUtil.add(recharge, bankRecharge)));
				dailyBill.setLoan(charge);
				dailyBill.setCreateTime(Calendar.getInstance().getTime());
				dailyBillService.insert(dailyBill);
				if (consumption > 0) {
					DailyBillConsumptionDetail dailyBillConsumptionDetail = new DailyBillConsumptionDetail();
					dailyBillConsumptionDetail.setUserId(dailyBill.getUserId());
					dailyBillConsumptionDetail.setBillDate(dailyBill.getBillDate());
					dailyBillConsumptionDetail.setBillConsumptionTypeId(MessageDef.TRADE_TYPE.CONSUMPTION + "");
					dailyBillConsumptionDetail.setBorrow(consumption);
					dailyBillConsumptionDetail.setLoan(0d);
					dailyBillConsumptionDetailService.insert(dailyBillConsumptionDetail);
				}
				if (recharge > 0) {
					DailyBillTransferDetail dailyBillTransferDetailRECHARGE = new DailyBillTransferDetail();
					dailyBillTransferDetailRECHARGE.setUserId(dailyBill.getUserId());
					dailyBillTransferDetailRECHARGE.setBillDate(dailyBill.getBillDate());
					dailyBillTransferDetailRECHARGE.setTransferTypeId(MessageDef.TRADE_TYPE.RECHARGE + "");
					dailyBillTransferDetailRECHARGE.setBorrow(recharge);
					dailyBillTransferDetailRECHARGE.setLoan(0d);
					dailyBillTransferDetailService.insert(dailyBillTransferDetailRECHARGE);
				}
				if (bankRecharge > 0) {
					DailyBillTransferDetail dailyBillTransferDetailBANK_RECHARGE = new DailyBillTransferDetail();
					dailyBillTransferDetailBANK_RECHARGE.setUserId(dailyBill.getUserId());
					dailyBillTransferDetailBANK_RECHARGE.setBillDate(dailyBill.getBillDate());
					dailyBillTransferDetailBANK_RECHARGE.setTransferTypeId(MessageDef.TRADE_TYPE.BANK_RECHARGE + "");
					dailyBillTransferDetailBANK_RECHARGE.setBorrow(bankRecharge);
					dailyBillTransferDetailBANK_RECHARGE.setLoan(0d);
					dailyBillTransferDetailService.insert(dailyBillTransferDetailBANK_RECHARGE);
				}
				if (charge > 0) {
					DailyBillTransferDetail dailyBillTransferDetailCHARGE = new DailyBillTransferDetail();
					dailyBillTransferDetailCHARGE.setUserId(dailyBill.getUserId());
					dailyBillTransferDetailCHARGE.setBillDate(dailyBill.getBillDate());
					dailyBillTransferDetailCHARGE.setTransferTypeId(MessageDef.TRADE_TYPE.CHARGE + "");
					dailyBillTransferDetailCHARGE.setBorrow(0d);
					dailyBillTransferDetailCHARGE.setLoan(charge);
					dailyBillTransferDetailService.insert(dailyBillTransferDetailCHARGE);
				}
			} catch (Exception e) {
				log.error(String.format("%s is error %s", key, JSONObject.toJSON(merchantUser)), e);
				TASK_LOG.ERROR(String.format("%s is error %s", key, JSONObject.toJSON(merchantUser)), e);
			}
		}

	}

}
