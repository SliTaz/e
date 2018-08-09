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
import com.zbensoft.e.payment.api.service.api.ConsumerTradeService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MonthBillConsumptionDetailService;
import com.zbensoft.e.payment.api.service.api.MonthBillService;
import com.zbensoft.e.payment.api.service.api.MonthBillTransferDetailService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.common.util.DoubleUtil;
import com.zbensoft.e.payment.db.domain.ConsumerTradeKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MonthBill;
import com.zbensoft.e.payment.db.domain.MonthBillConsumptionDetail;
import com.zbensoft.e.payment.db.domain.MonthBillTransferDetail;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 买家月账单，每月1号早上6点执行一次
 * 
 * 0 0 6 1 * ?
 * 
 * @author xieqiang
 *
 */
public class BillMonthlyBuyerJob implements Job {
	
	private static final Logger log = LoggerFactory.getLogger(BillMonthlyBuyerJob.class);

	private static String key = "BillMonthlyBuyerJob";

	private ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);
	private ConsumerTradeService consumerTradeService = SpringBeanUtil.getBean(ConsumerTradeService.class);
	private TradeInfoService tradeInfoService = SpringBeanUtil.getBean(TradeInfoService.class);

	private MonthBillService monthBillService = SpringBeanUtil.getBean(MonthBillService.class);
	private MonthBillConsumptionDetailService monthBillConsumptionDetailService = SpringBeanUtil.getBean(MonthBillConsumptionDetailService.class);
	private MonthBillTransferDetailService monthBillTransferDetailService = SpringBeanUtil.getBean(MonthBillTransferDetailService.class);

	private Date now = null;
	private Date lastMonthDay = null;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		TASK_LOG.INFO(String.format("%s Start", key));
		now = Calendar.getInstance().getTime();
		lastMonthDay = DateUtils.addDays(now, -Integer.valueOf(DateUtil.convertDateToString(now, "dd")));
		String billDate = DateUtil.convertDateToString(lastMonthDay, "yyyy-MM");
		monthBillService.deleteByBillDate(billDate);
		monthBillConsumptionDetailService.deleteByBillDate(billDate);
		monthBillTransferDetailService.deleteByBillDate(billDate);

		int JOB_BILL_MONTHLY_BUYER_GET_USER_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_BILL_MONTHLY_BUYER_GET_USER_COUNT);
		int start = 0;
		int length = JOB_BILL_MONTHLY_BUYER_GET_USER_COUNT;
		try {
			while (true) {
				try {
					TASK_LOG.INFO(String.format("%s process %s - %s ", key, start, start + length - 1));
					int pageNum = PageHelperUtil.getPageNum(start + "", length + "");
					int pageSize = PageHelperUtil.getPageSize(start + "", length + "");
					PageHelper.startPage(pageNum, pageSize, false);
					List<ConsumerUser> consumerUserList = consumerUserService.selectPage(new ConsumerUser());
					if (consumerUserList != null && consumerUserList.size() == JOB_BILL_MONTHLY_BUYER_GET_USER_COUNT) {
						daliyBill(consumerUserList);
						start += JOB_BILL_MONTHLY_BUYER_GET_USER_COUNT;

					} else {
						daliyBill(consumerUserList);
						break;
					}
				} catch (Exception e) {
					log.error(String.format("%s is error", key), e);
					TASK_LOG.ERROR(String.format("%s is error", key), e);
				}
				JOB_BILL_MONTHLY_BUYER_GET_USER_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_BILL_MONTHLY_BUYER_GET_USER_COUNT);
				length = JOB_BILL_MONTHLY_BUYER_GET_USER_COUNT;
			}
		} catch (Exception e) {
			log.error(String.format("%s is error", key), e);
			TASK_LOG.ERROR(String.format("%s is error", key), e);
		}
		TASK_LOG.INFO(String.format("%s end", key));
	}

	private void daliyBill(List<ConsumerUser> consumerUserList) {
		for (ConsumerUser consumerUser : consumerUserList) {
			try {
				ConsumerTradeKey consumerTrade = new ConsumerTradeKey();
				consumerTrade.setCreateTimeStartSer(DateUtil.convertDateToString(lastMonthDay, "yyyy-MM-01") + " 00:00:00");
				consumerTrade.setCreateTimeEndSer(DateUtil.convertDateToString(lastMonthDay) + " 23:59:59");
				consumerTrade.setUserId(consumerUser.getUserId());
				PageHelper.startPage(1, 100000);
				List<ConsumerTradeKey> consumerTradeList = consumerTradeService.selectPage(consumerTrade);
				Double recharge = 0d;
				Double charge = 0d;
				Double consumption = 0d;
				Double bankRecharge = 0d;
				if (consumerTradeList != null && consumerTradeList.size() > 0) {
					for (ConsumerTradeKey consumerTrade2 : consumerTradeList) {
						TradeInfo tradeInfo = tradeInfoService.selectByPrimaryKey(consumerTrade2.getTradeSeq());
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
				MonthBill monthBill = new MonthBill();
				monthBill.setUserId(consumerUser.getUserId());
				monthBill.setBillDate(DateUtil.convertDateToString(lastMonthDay, "yyyy-MM"));
				monthBill.setBorrow(DoubleUtil.add(recharge, bankRecharge));
				monthBill.setLoan(DoubleUtil.add(charge, consumption));
				monthBill.setCreateTime(Calendar.getInstance().getTime());
				monthBillService.insert(monthBill);
				if (consumption > 0) {
					MonthBillConsumptionDetail monthBillConsumptionDetail = new MonthBillConsumptionDetail();
					monthBillConsumptionDetail.setUserId(monthBill.getUserId());
					monthBillConsumptionDetail.setBillDate(monthBill.getBillDate());
					monthBillConsumptionDetail.setBillConsumptionTypeId(MessageDef.TRADE_TYPE.CONSUMPTION + "");
					monthBillConsumptionDetail.setBorrow(0d);
					monthBillConsumptionDetail.setLoan(consumption);
					monthBillConsumptionDetailService.insert(monthBillConsumptionDetail);
				}
				if (recharge > 0) {
					MonthBillTransferDetail monthBillTransferDetailRECHARGE = new MonthBillTransferDetail();
					monthBillTransferDetailRECHARGE.setUserId(monthBill.getUserId());
					monthBillTransferDetailRECHARGE.setBillDate(monthBill.getBillDate());
					monthBillTransferDetailRECHARGE.setTransferTypeId(MessageDef.TRADE_TYPE.RECHARGE + "");
					monthBillTransferDetailRECHARGE.setBorrow(recharge);
					monthBillTransferDetailRECHARGE.setLoan(0d);
					monthBillTransferDetailService.insert(monthBillTransferDetailRECHARGE);
				}
				if (bankRecharge > 0) {
					MonthBillTransferDetail monthBillTransferDetailBANK_RECHARGE = new MonthBillTransferDetail();
					monthBillTransferDetailBANK_RECHARGE.setUserId(monthBill.getUserId());
					monthBillTransferDetailBANK_RECHARGE.setBillDate(monthBill.getBillDate());
					monthBillTransferDetailBANK_RECHARGE.setTransferTypeId(MessageDef.TRADE_TYPE.BANK_RECHARGE + "");
					monthBillTransferDetailBANK_RECHARGE.setBorrow(bankRecharge);
					monthBillTransferDetailBANK_RECHARGE.setLoan(0d);
					monthBillTransferDetailService.insert(monthBillTransferDetailBANK_RECHARGE);
				}
				if (charge > 0) {
					MonthBillTransferDetail monthBillTransferDetailCHARGE = new MonthBillTransferDetail();
					monthBillTransferDetailCHARGE.setUserId(monthBill.getUserId());
					monthBillTransferDetailCHARGE.setBillDate(monthBill.getBillDate());
					monthBillTransferDetailCHARGE.setTransferTypeId(MessageDef.TRADE_TYPE.CHARGE + "");
					monthBillTransferDetailCHARGE.setBorrow(0d);
					monthBillTransferDetailCHARGE.setLoan(charge);
					monthBillTransferDetailService.insert(monthBillTransferDetailCHARGE);
				}
			} catch (Exception e) {
				log.error(String.format("%s is error %s", key, JSONObject.toJSON(consumerUser)), e);
				TASK_LOG.ERROR(String.format("%s is error %s", key, JSONObject.toJSON(consumerUser)), e);
			}
		}

	}

}
