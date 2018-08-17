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
 * 对账差错记录的差错处理 记账
 * 
 * @author xieqiang
 *
 */
public class BookKeepingBankRechargeErrorHandlingProcessImpl extends AbsBookKeepingProcess {

	private static final Logger log = LoggerFactory.getLogger(BookKeepingBankRechargeErrorHandlingProcessImpl.class);

	@Override
	public boolean processBookKeeping(TradeInfo tradeInfo) {
		try {

			Bookkeepking bookkeepking = new Bookkeepking();

			// 只支持银行充值
			if (MessageDef.TRADE_TYPE.BANK_RECHARGE == tradeInfo.getType()) {
				doBookkeepkingBankReCharge(tradeInfo, bookkeepking);
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

	/**
	 * <pre>
	 * 对账逻辑
	 * succ:-100,+100
	 * fail:-95 +100  相反，这样正好低调利润
	 * </pre>
	 * 
	 * @param tradeInfo
	 * @param bookkeepking
	 */
	private void doBookkeepkingBankReCharge(TradeInfo tradeInfo, Bookkeepking bookkeepking) {
		if (isGetWayTypeAccountAmount(tradeInfo.getPayGetwayType())) {
			// 充值付款不会走账户余额
		}
		if (isGetWayTypeBankTran(tradeInfo.getPayGetwayType())) {
			if (tradeInfo.getStatus() == MessageDef.TRADE_STATUS.SUCC) {
				bookkeepking.setLoanCaption(getLoanCaption(tradeInfo.getPayBankId()));
				bookkeepking.setLoanAmount(tradeInfo.getPaySumAmount());
			} else {
				if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
					bookkeepking.setLoanCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
				} else {
					bookkeepking.setLoanCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
				}
				bookkeepking.setLoanAmount(tradeInfo.getRecvSumAmount());
			}
		}

		if (isGetWayTypeAccountAmount(tradeInfo.getRecvGetwayType())) {
			if (tradeInfo.getRecvUserId().startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
				bookkeepking.setBorrowCaption(CaptionAccount.C_201_DEPOSIT_BUYER);
			} else {
				bookkeepking.setBorrowCaption(CaptionAccount.C_202_DEPOSIT_SELLER);
			}
			if (tradeInfo.getStatus() == MessageDef.TRADE_STATUS.SUCC) {
				bookkeepking.setBorrowAmount(tradeInfo.getPaySumAmount());
			} else {
				bookkeepking.setBorrowAmount(tradeInfo.getPaySumAmount());
			}
		}
		if (isGetWayTypeBankTran(tradeInfo.getRecvGetwayType())) {
			// 充值收款不会未快捷支付
		}

	}

	private String getLoanCaption(String payBankId) {
		if ("1".equals(payBankId)) {
			return CaptionAccount.C_1100101_RECHARGE_TEST_BANK_ONE;
		}
		return CaptionAccount.C_11001_RECHARGE;
	}

}
