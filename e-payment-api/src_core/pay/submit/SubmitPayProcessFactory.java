package org.e.payment.core.pay.submit;

import org.e.payment.core.pay.ProcessType;
import org.e.payment.core.pay.submit.impl.GetPayGateWayPayProcess;
import org.e.payment.core.pay.submit.impl.errorHandling.bankrecharge.ErrorHandlingBankRechargeRechargePayProcess;
import org.e.payment.core.pay.submit.impl.errorHandling.bankrecharge.ErrorHandlingBankRechargeRefundPayProcess;
import org.e.payment.core.pay.submit.impl.errorHandling.charge.ErrorHandlingChargeBankErrorProcess;
import org.e.payment.core.pay.submit.impl.errorHandling.charge.ErrorHandlingChargeEpayErrorProcess;
import org.e.payment.core.pay.submit.impl.errorHandling.charge.ErrorHandlingChargeRefundPayProcess;
import org.e.payment.core.pay.submit.impl.reverse.ConsumptionReservseProcess;
import org.e.payment.core.pay.submit.impl.submit.allBank.AllBankSubmitChargePayProcess;
import org.e.payment.core.pay.submit.impl.submit.allBank.AllBankSubmitConsumptionAppPayProcess;
import org.e.payment.core.pay.submit.impl.submit.allBank.AllBankSubmitRechargeAppPayProcess;
import org.e.payment.core.pay.submit.impl.submit.allBank.AllBankSubmitRechargePayProcess;
import org.e.payment.core.pay.submit.impl.webservice.BankTranWebserviceRechargePayProcess;
import org.e.payment.core.pay.submit.impl.webservice.BankTranWebserviceReversePayProcess;

public class SubmitPayProcessFactory {

	private static SubmitPayProcessFactory instance = null;


	private SubmitPayProcessFactory() {

	}

	public static SubmitPayProcessFactory getInstance() {
		if (instance == null) {
			instance = new SubmitPayProcessFactory();
		}
		return instance;
	}

	public SubmitPayProcess get(int processType) {
		switch (processType) {
		case ProcessType.RECHARGE:
			return new AllBankSubmitRechargePayProcess();
		case ProcessType.CHARGE:
			return new AllBankSubmitChargePayProcess();
		case ProcessType.RECHARGE_APP:
			return new AllBankSubmitRechargeAppPayProcess();
		case ProcessType.CONSUMPTION_APP:
			return new AllBankSubmitConsumptionAppPayProcess();
		case ProcessType.REFUND:
			return new ConsumptionReservseProcess();
			
			
			
		case ProcessType.GATEWAY:
			return new GetPayGateWayPayProcess();
			
			
			
		case ProcessType.WEBSERVICE_BANK_RECHARGE:
			return new BankTranWebserviceRechargePayProcess();
		case ProcessType.WEBSERVICE_BANK_REVERSE:
			return new BankTranWebserviceReversePayProcess();
			
			
			
			
		case ProcessType.ERROR_HANDLING_BANK_RECHARGE_RECHARGE:
			return new ErrorHandlingBankRechargeRechargePayProcess();
		case ProcessType.ERROR_HANDLING_BANK_RECHARGE_REFUND:
			return new ErrorHandlingBankRechargeRefundPayProcess();
		case ProcessType.ERROR_HANDLING_CHARGE_REFUND:
			return new ErrorHandlingChargeRefundPayProcess();
		case ProcessType.ERROR_HANDLING_CHARGE_BANK_MISS:
			return new ErrorHandlingChargeBankErrorProcess();
		case ProcessType.ERROR_HANDLING_CHARGE_EPAY_MISS:
			return new ErrorHandlingChargeEpayErrorProcess();
		default:
			break;
		}
		return null;
	}
}
