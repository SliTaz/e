package com.zbensoft.e.payment.api.control;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.common.cxf.SOAPResultCode;
import com.zbensoft.e.payment.common.cxf.SOAPResultCodeHelp;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/SOAPResultCode")
@RestController
public class SOAPResultCodeController {

	
	private static final Logger log = LoggerFactory.getLogger(SOAPResultCodeController.class);

	@ApiOperation(value = "Query soapResultCodeMapper，Support paging", notes = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseRestEntity<List<SOAPResultCodeHelp>> selectPage() {

		List<SOAPResultCodeHelp> list = new ArrayList<SOAPResultCodeHelp>();

		try {
			list = SOAPResultCode.getList(Class.forName("com.zbensoft.e.payment.common.cxf.SOAPResultCode"), null);
		} catch (ClassNotFoundException e) {
			log.error("",e);
		}

		if (list == null || list.isEmpty()) {
			return new ResponseRestEntity<List<SOAPResultCodeHelp>>(new ArrayList<SOAPResultCodeHelp>(), HttpRestStatus.NOT_FOUND);
		}

		return new ResponseRestEntity<List<SOAPResultCodeHelp>>(list, HttpRestStatus.OK);
	}

}
