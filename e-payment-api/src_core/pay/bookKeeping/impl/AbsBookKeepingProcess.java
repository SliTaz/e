package org.e.payment.core.pay.bookKeeping.impl;

import org.e.payment.core.pay.bookKeeping.BookKeepingProcess;

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.BookkeepkingService;
import com.zbensoft.e.payment.db.domain.Bookkeepking;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public abstract class AbsBookKeepingProcess implements BookKeepingProcess {

	private BookkeepkingService bookkeepkingService = SpringBeanUtil.getBean(BookkeepkingService.class);

	@Override
	public boolean process(TradeInfo tradeInfo) {
		return processBookKeeping(tradeInfo);
	}

	public abstract boolean processBookKeeping(TradeInfo tradeInfo);

	protected void insertBookkeeping(Bookkeepking bookkeepking) {
		bookkeepkingService.insert(bookkeepking);
		// 统计利润
		RedisUtil.increment_BI_PROFIT_STATEMENT(bookkeepking);

	}

	protected boolean isGetWayTypeAccountAmount(int getwayType) {
		switch (getwayType) {
		// case MessageDef.TRADE_PAY_GETWAY_TYPE.CONSUMPTION:
		// return true;
		case MessageDef.TRADE_PAY_GETWAY_TYPE.ACCOUNT_NUMBER:
			return true;
		default:
			break;
		}
		return false;
	}

	protected boolean isGetWayTypeBankTran(int getwayType) {
		switch (getwayType) {
		case MessageDef.TRADE_PAY_GETWAY_TYPE.BANK_RECHARGE:
			return true;
		case MessageDef.TRADE_PAY_GETWAY_TYPE.BANK_REVERSE:
			return true;
		case MessageDef.TRADE_PAY_GETWAY_TYPE.EPAY_RECHARGE:
			return true;
		case MessageDef.TRADE_PAY_GETWAY_TYPE.EPAY_CHARGE:
			return true;
		default:
			break;
		}
		return false;
	}

}
