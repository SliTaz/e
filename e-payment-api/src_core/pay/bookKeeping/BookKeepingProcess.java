package org.e.payment.core.pay.bookKeeping;

import com.zbensoft.e.payment.db.domain.TradeInfo;

public interface BookKeepingProcess {

	public boolean process(TradeInfo tradeInfo);
}
