package org.e.payment.core.pay.bookKeeping.impl.bankrecharge;

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
 * 对账成功记录的记账
 * 
 * @author xieqiang
 *
 */
public class BookKeepingBankRechargeReconciliationProcessImpl extends AbsBookKeepingProcess {

	private static final Logger log = LoggerFactory.getLogger(BookKeepingBankRechargeReconciliationProcessImpl.class);

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
			} else if (MessageDef.TRADE_TYPE.BANK_RECHARGE == tradeInfo.getType()) {
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
				MessageAlarmFactory.getInstance().add(String.format("%s error,info:%s", "BookKeepingReconciliationProcessImpl insertBookkeeping", JSONObject.toJSONString(bookkeepking)));
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
	 * 
	 * -100 -100
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
			bookkeepking.setLoanCaption(getLoanCaption(tradeInfo.getPayBankId()));
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
			// 充值收款不会未快捷支付
		}
		bookkeepking.setBorrowAmount(tradeInfo.getPaySumAmount());

	}

	private void doBookkeepkingConsumption(TradeInfo tradeInfo, Bookkeepking bookkeepking) {
		// 没有消费类型会走到这里

	}

	/**
	 * 
	 * -95 +100 ,和充值相反
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
			// 支付不会有快捷支付
		}
		bookkeepking.setLoanAmount(tradeInfo.getRecvSumAmount());// -付款，付方扣款

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			// 收款不会有用户余额
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			bookkeepking.setBorrowCaption(getBorrowCaption(tradeInfo.getRecvBankId()));
		}
		bookkeepking.setBorrowAmount(tradeInfo.getPaySumAmount());// +收款，银行返款

	}

	/**
	 * 
	 * -95 +95
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
			// 支付不会为快捷支付
		}
		bookkeepking.setLoanAmount(tradeInfo.getRecvSumAmount());

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			bookkeepking.setBorrowCaption(getBorrowCaption(tradeInfo.getRecvBankId()));
		}
		bookkeepking.setBorrowAmount(tradeInfo.getRecvSumAmount());

	}

	/**
	 * 
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
			bookkeepking.setLoanCaption(CaptionAccount.C_1100101_RECHARGE_TEST_BANK_ONE);
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
			// 充值收款不会未快捷支付
		}
		bookkeepking.setBorrowAmount(tradeInfo.getRecvSumAmount());

	}

	private String getLoanCaption(String payBankId) {
		if ("1".equals(payBankId)) {
			return CaptionAccount.C_1100101_RECHARGE_TEST_BANK_ONE;
		}
		return CaptionAccount.C_11001_RECHARGE;
	}

	private String getBorrowCaption(String recvBankId) {
		if ("1".equals(recvBankId)) {
			return CaptionAccount.C_1100201_CHARGE_TEST_BANK_ONE;
		}

		return CaptionAccount.C_11002_CHARGE;
	}

}
