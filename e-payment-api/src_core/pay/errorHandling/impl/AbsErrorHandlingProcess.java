package org.e.payment.core.pay.errorHandling.impl;

import org.e.payment.core.pay.errorHandling.ErrorHandlingProcess;

public abstract class AbsErrorHandlingProcess implements ErrorHandlingProcess {

	@Override
	public boolean process(Object ob) {
		return processErrorHandling(ob);
	}


	public abstract boolean processErrorHandling(Object ob);

}
