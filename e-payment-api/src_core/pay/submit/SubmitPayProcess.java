package org.e.payment.core.pay.submit;

import com.zbensoft.e.payment.api.common.ResponseRestEntity;

public interface SubmitPayProcess {

	public ResponseRestEntity<?> process(Object request);
	
}
