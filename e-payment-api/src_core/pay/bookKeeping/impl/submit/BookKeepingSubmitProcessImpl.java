package org.e.payment.core.pay.bookKeeping.impl.submit;

import java.util.Calendar;

import org.e.payment.core.pay.bookKeeping.CaptionAccount;
import org.e.payment.core.pay.bookKeeping.impl.AbsBookKeepingProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.alarm.alarm.factory.MessageAlarmFactory;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.log.BOOKKEEPING_LOG;
import com.zbensoft.e.payment.db.domain.Bookkeepking;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 用户交易记账
 * 
 * @author xieqiang
 *
 */
public class BookKeepingSubmitProcessImpl extends AbsBookKeepingProcess {

	private static final Logger log = LoggerFactory.getLogger(BookKeepingSubmitProcessImpl.class);

	@Override
	public boolean processBookKeeping(TradeInfo tradeInfo) {
		try {

			Bookkeepking bookkeepking = new Bookkeepking();
			if (MessageDef.TRADE_TYPE.RECHARGE == tradeInfo.getType()) {
				doBookkeepkingRecharge(tradeInfo, bookkeepking);
			} else if (MessageDef.TRADE_TYPE.CHARGE == tradeInfo.getType()) {
				doBookkeepkingCharge(tradeInfo, bookkeepking);
			} else if (MessageDef.TRADE_TYPE.CONSUMPTION == tradeInfo.getType()) {
				doBookkeepkingConsumption(tradeInfo, bookkeepking);
			}  else if (MessageDef.TRADE_TYPE.REFUND == tradeInfo.getType()) {
				doBookkeepkingConsumptionRefund(tradeInfo, bookkeepking);
			}else if (MessageDef.TRADE_TYPE.BANK_RECHARGE == tradeInfo.getType()) {
				doBookkeepkingBankReCharge(tradeInfo, bookkeepking);
			} else if (MessageDef.TRADE_TYPE.BANK_REVERSE == tradeInfo.getType()) {
				doBookkeepkingBankReverse(tradeInfo, bookkeepking);
			} else {
				BOOKKEEPING_LOG.INFO(String.format("BookKeepingProcessImpl not support trade type %s", tradeInfo.toString()));
				return false;
			}
			bookkeepking.setTradeSeq(tradeInfo.getTradeSeq());
			bookkeepking.setBookkeepkingSeq(IDGenerate.generateCommTwo(IDGenerate.BOOKKEEPKING));
			bookkeepking.setCreateTime(Calendar.getInstance().getTime());
			try {
				insertBookkeeping(bookkeepking);
				BOOKKEEPING_LOG.INFO(String.format("BookKeepingProcessImpl succ %s", bookkeepking.toString()));
			} catch (Exception e) {
				MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "BookKeepingProcessImpl insertBookkeeping", JSONObject.toJSONString(bookkeepking)));
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

	

	/**
	 * -100 +95
	 * 
	 * @param tradeInfo
	 * @param bookkeepking
	 */
	private void doBookkeepkingBankReCharge(TradeInfo tradeInfo, Bookkeepking bookkeepking) {
		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_40301_NOT_CLEAR_RECHARGE_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_40302_NOT_CLEAR_RECHARGE_SELLER);
			}
		}
		bookkeepking.setLoanAmount(tradeInfo.getPaySumAmount());// 银行充值，未结算

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_40301_NOT_CLEAR_RECHARGE_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_40302_NOT_CLEAR_RECHARGE_SELLER);
			}
		}
		bookkeepking.setBorrowAmount(tradeInfo.getRecvSumAmount());// +存入账户

	}

	/**
	 * 
	 * -105 +95
	 * 
	 * @param tradeInfo
	 * @param bookkeepking
	 */
	private void doBookkeepkingConsumption(TradeInfo tradeInfo, Bookkeepking bookkeepking) {
		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			// 没有快捷支付
		}
		bookkeepking.setLoanAmount(tradeInfo.getPaySumAmount());// -付款

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			// 没有快捷支付
		}
		bookkeepking.setBorrowAmount(tradeInfo.getRecvSumAmount());// +存款

	}
	
	

	/**
	 * -95 +105 ,和交易相反
	 * 
	 * @param tradeInfo
	 * @param bookkeepking
	 */
	private void doBookkeepkingConsumptionRefund(TradeInfo tradeInfo, Bookkeepking bookkeepking) {
		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			// 没有快捷支付
		}
		bookkeepking.setLoanAmount(tradeInfo.getPaySumAmount());// -付款

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			// 没有快捷支付
		}
		bookkeepking.setBorrowAmount(tradeInfo.getRecvSumAmount());// +存款
		
	}
	

	/**
	 * 
	 * -95 +105 ,和充值相反
	 * 
	 * @param tradeInfo
	 * @param bookkeepking
	 */
	private void doBookkeepkingBankReverse(TradeInfo tradeInfo, Bookkeepking bookkeepking) {
		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_40401_NOT_CLEAR_REVERSE_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_40402_NOT_CLEAR_REVERSE_SELLER);
			}
		}
		bookkeepking.setLoanAmount(tradeInfo.getRecvSumAmount());// -付款，付方扣款

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_40401_NOT_CLEAR_REVERSE_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_40402_NOT_CLEAR_REVERSE_SELLER);
			}
		}
		bookkeepking.setBorrowAmount(tradeInfo.getPaySumAmount());// +收款，银行返款

	}

	/**
	 * 
	 * -105 +95
	 * 
	 * @param tradeInfo
	 * @param bookkeepking
	 */
	private void doBookkeepkingCharge(TradeInfo tradeInfo, Bookkeepking bookkeepking) {
		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_40201_NOT_CLEAR_CHARGE_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_40202_NOT_CLEAR_CHARGE_SELLER);
			}
		}
		bookkeepking.setLoanAmount(tradeInfo.getPaySumAmount());

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_40201_NOT_CLEAR_CHARGE_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_40202_NOT_CLEAR_CHARGE_SELLER);
			}
		}
		bookkeepking.setBorrowAmount(tradeInfo.getRecvSumAmount());

	}

	/**
	 * -100,+95
	 * 
	 * @param tradeInfo
	 * @param bookkeepking
	 */
	private void doBookkeepkingRecharge(TradeInfo tradeInfo, Bookkeepking bookkeepking) {

		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getPayUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setLoanCaption(CaptionAccount.C_40101_NOT_CLEAR_RECHARGE_BUYER);
			} else {
				bookkeepking.setLoanCaption(CaptionAccount.C_40102_NOT_CLEAR_RECHARGE_SELLER);
			}
		}
		bookkeepking.setLoanAmount(tradeInfo.getPaySumAmount());

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_40101_NOT_CLEAR_RECHARGE_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_40102_NOT_CLEAR_RECHARGE_SELLER);
			}
		}
		bookkeepking.setBorrowAmount(tradeInfo.getRecvSumAmount());

	}

}
