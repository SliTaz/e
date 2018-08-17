package org.e.payment.core.pay.bookKeeping.impl.charge;

import java.util.Calendar;
import java.util.Date;

import org.e.payment.core.pay.bookKeeping.CaptionAccount;
import org.e.payment.core.pay.bookKeeping.impl.AbsBookKeepingProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.log.BOOKKEEPING_LOG;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.db.domain.Bookkeepking;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public class BookKeepingChargeReconciliationProcessImpl extends AbsBookKeepingProcess {

	private static final Logger log = LoggerFactory.getLogger(BookKeepingChargeReconciliationProcessImpl.class);

	private TradeInfoService tradeInfoService = SpringBeanUtil.getBean(TradeInfoService.class);

	@Override
	public boolean processBookKeeping(TradeInfo tradeInfo) {
		if (tradeInfo != null) {
			try {
				TradeInfo upDateTradeInfo = new TradeInfo();
				upDateTradeInfo.setTradeSeq(tradeInfo.getTradeSeq());
				upDateTradeInfo.setStatus(MessageDef.TRADE_STATUS.SUCC);
				upDateTradeInfo.setEndTime(new Date());
				upDateTradeInfo.setIsClose(MessageDef.TRADE_CLOSE.CLOSE);
				tradeInfoService.updateByPrimaryKeySelective(upDateTradeInfo);
			} catch (Exception e) {
				log.error("", e);
				BOOKKEEPING_LOG.ERROR("Tradeinfo Update exception", e);
				return false;
			}
			
			try {
				Bookkeepking bookkeepking = new Bookkeepking();
				// 提现
				if (MessageDef.TRADE_TYPE.CHARGE == tradeInfo.getType()) {
						doBookkeepkingBankCharge(tradeInfo, bookkeepking);
				} else {
					BOOKKEEPING_LOG.INFO(String.format("BookKeepingErrorHandlingProcessImpl not support trade type %s", tradeInfo.toString()));
					return false;
				}
				bookkeepking.setTradeSeq(tradeInfo.getTradeSeq());
				bookkeepking.setBookkeepkingSeq(IDGenerate.generateCommTwo(IDGenerate.BOOKKEEPKING));
				bookkeepking.setCreateTime(Calendar.getInstance().getTime());
				try {
					insertBookkeeping(bookkeepking);
					BOOKKEEPING_LOG.INFO(String.format("BookKeepingProcessImpl succ %s", bookkeepking.toString()));
				} catch (Exception e) {
					MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "BookKeepingErrorHandlingProcessImpl insertBookkeeping", JSONObject.toJSONString(bookkeepking)));
					log.error(String.format("BookKeepingProcessImpl TO DB exception %s", bookkeepking.toString()), e);

					BOOKKEEPING_LOG.INFO(String.format("BookKeepingProcessImpl TO DB exception %s", bookkeepking.toString()));
					BOOKKEEPING_LOG.ERROR(String.format("BookKeepingProcessImpl TO DB exception %s", bookkeepking.toString()), e);
				}
			} catch (Exception e) {
				log.error(String.format("BookKeepingProcessImpl exception %s", tradeInfo.toString()), e);
				BOOKKEEPING_LOG.INFO(String.format("BookKeepingProcessImpl exception %s", tradeInfo.toString()));
				BOOKKEEPING_LOG.ERROR(String.format("BookKeepingProcessImpl exception %s", tradeInfo.toString()), e);
			}
			
			
			return true;
		}
		
		
		return false;
	}

	private void doBookkeepkingBankCharge(TradeInfo tradeInfo, Bookkeepking bookkeepking) {

		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			
			bookkeepking.setLoanCaption(CaptionAccount.C_11002_CHARGE);
			bookkeepking.setLoanAmount(tradeInfo.getPaySumAmount());

		}
		if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			//提现,支出不会走银行卡
		}

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			//提现,借入不会余额
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
			bookkeepking.setBorrowAmount(tradeInfo.getRecvSumAmount());
			
		}

	
		
	}
}
