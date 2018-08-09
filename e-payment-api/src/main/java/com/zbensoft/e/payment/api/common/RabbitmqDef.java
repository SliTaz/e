package com.zbensoft.e.payment.api.common;

public interface RabbitmqDef {

	// public interface TRADE {
	// public static String EXCHANGE = "epayment.Trade";
	// public static String QUEUE = "epayment.TradeQueue";
	// public static String ROUTEKEY = "epayment.TradeRoutekey";
	//
	// }

	public interface TRADE {
		public static String EXCHANGE = "epayment.trade";
		public static String BOOKKEEPING_QUEUE = "epayment.trade.bookkeepingQueue";
		public static String CDR_QUEUE = "epayment.trade.CDRQueue";
	}

	public interface RECONCILIATION {
		public static String EXCHANGE = "epayment.reconciliation";
		public static String BOOKKEEPING_QUEUE = "epayment.reconciliation.BookkeepingQueue";
	}

	public interface ERROR_HANDLING {
		public static String EXCHANGE = "epayment.errorhandling";
		public static String ERRORHANDLING_QUEUE = "epayment.errorhandling.errorhandlingQueue";
	}
	
	public interface CHARGE_RECONCILIATION {
		public static String EXCHANGE = "epayment.charge.reconciliation";
		public static String BOOKKEEPING_QUEUE = "epayment.charge.reconciliation.BookkeepingQueue";
	}

	public interface CHARGE_ERROR_HANDLING {
		public static String EXCHANGE = "epayment.charge.errorhandling";
		public static String ERRORHANDLING_QUEUE = "epayment.charge.errorhandling.errorhandlingQueue";
	}
	
	public interface CHARGE_ERROR_HANDLING_BOOKKEEPING {
		public static String EXCHANGE = "epayment.charge.errorhandlingbookKeeping";
		public static String BOOKKEEPING_QUEUE = "epayment.charge.errorhandlingbookKeeping.bookkeepingQueue";
		public static String CDR_QUEUE = "epayment.charge.errorhandlingbookKeeping.CDRQueue";
	}

	public interface ERROR_HANDLING_BOOKKEEPING {
		public static String EXCHANGE = "epayment.errorhandlingbookKeeping";
		public static String BOOKKEEPING_QUEUE = "epayment.errorhandlingbookKeeping.bookkeepingQueue";
		public static String CDR_QUEUE = "epayment.errorhandlingbookKeeping.CDRQueue";
	}
}
