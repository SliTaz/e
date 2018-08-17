package org.e.payment.core.pay.bankProcess;

import java.util.concurrent.ConcurrentHashMap;

import org.e.payment.core.pay.BankInterfaceType;
import org.e.payment.core.pay.bankProcess.impl.allBank.AllBankChargeProcess;
import org.e.payment.core.pay.bankProcess.impl.allBank.AllBankRechargeProcess;
import org.e.payment.core.pay.bankProcess.impl.allBank.AllBankReverseProcess;
import org.e.payment.core.pay.bookKeeping.BookKeepingType;
import org.e.payment.core.pay.bookKeeping.impl.bankrecharge.BookKeepingBankRechargeErrorHandlingProcessImpl;
import org.e.payment.core.pay.bookKeeping.impl.bankrecharge.BookKeepingBankRechargeReconciliationProcessImpl;
import org.e.payment.core.pay.bookKeeping.impl.submit.BookKeepingSubmitProcessImpl;

public class BankProcessFactory {

	private static BankProcessFactory instance = null;

	private static ConcurrentHashMap<String, BankProcess> map = new ConcurrentHashMap<>();

	private BankProcessFactory() {

	}

	public static BankProcessFactory getInstance() {
		if (instance == null) {
			instance = new BankProcessFactory();
			// map.put(MessageDef.TRADE_TYPE.RECHARGE, new RechargeTradeProcess());
			// map.put(MessageDef.TRADE_TYPE.WITHDRAW_CASH, new WithdrawCashTradeProcess());
			// map.put(MessageDef.TRADE_TYPE.TRANSFER, new TransferTradeProcess());
			// map.put(MessageDef.TRADE_TYPE.CONSUMPTION, new ConsumptionTradeProcess());
			// map.put(MessageDef.TRADE_TYPE.TRANSFER_BANK, new TransferBankTradeProcess());

			map.put("" + BankInterfaceType.RECHARGE, new AllBankRechargeProcess());
			map.put("" + BankInterfaceType.CHARGE, new AllBankChargeProcess());
			map.put("" + BankInterfaceType.REVERSE, new AllBankReverseProcess());
		}
		return instance;
	}

	private BankProcess get(String payBankId, int recharge) {
		return null;
	}

	public BankProcess get(int recharge) {
		switch (recharge) {
		case BankInterfaceType.RECHARGE:
			return new AllBankRechargeProcess();
		case BankInterfaceType.CHARGE:
			return new AllBankChargeProcess();
		case BankInterfaceType.REVERSE:
			return new AllBankReverseProcess();
		default:
			break;
		}
		return null;
	}
}
