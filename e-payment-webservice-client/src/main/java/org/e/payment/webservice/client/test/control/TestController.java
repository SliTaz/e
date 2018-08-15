package org.e.payment.webservice.client.test.control;

import org.e.payment.webservice.client.test.conf.TestConf;
import org.e.payment.webservice.client.test.conf.TestManager;
import org.e.payment.webservice.client.vo.bankTran.BankTranService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/test")
@RestController
public class TestController {

	public static BankTranService service;
	@Autowired
	private BankTranService bankTranService;

	@RequestMapping(value = "/start", method = RequestMethod.GET)
	public ResponseEntity<String> start() {
		service = bankTranService;
		TestManager.getInstance().start();
		return new ResponseEntity<String>("start succ", HttpStatus.OK);
	}

	@RequestMapping(value = "/stop", method = RequestMethod.GET)
	public ResponseEntity<String> stop() {
		TestManager.getInstance().stop();
		return new ResponseEntity<String>("stop succ", HttpStatus.OK);
	}

	@RequestMapping(value = "/conf", method = RequestMethod.GET)
	public ResponseEntity<TestConf> conf(@RequestParam String threadCount, @RequestParam String maxThreadCount,
			@RequestParam String sleepTimeMS, @RequestParam String countForOneProcess, @RequestParam String bankId,
			@RequestParam String vid, @RequestParam(required = false) String testType) {

		int threadCountInt = Integer.valueOf(threadCount);
		int maxThreadCountInt = Integer.valueOf(maxThreadCount);
		int sleepTimeMSInt = Integer.valueOf(sleepTimeMS);
		int countForOneProcessInt = Integer.valueOf(countForOneProcess);
		int testTypeInt = 0;
		if (testType != null) {
			testTypeInt = Integer.valueOf(testType);
		}
		TestManager.getInstance().setConf(threadCountInt, maxThreadCountInt, sleepTimeMSInt, countForOneProcessInt,
				testTypeInt);

		String[] bankIdArr = bankId.split(",");
		for (String bankIdStr : bankIdArr) {
			TestManager.getInstance().addBank(bankIdStr);
		}
		String[] vidArr = vid.split(",");
		for (String vidStr : vidArr) {
			TestManager.getInstance().addVid(vidStr);
		}
		return new ResponseEntity<TestConf>(TestManager.getInstance().getConf(), HttpStatus.OK);
	}

	@RequestMapping(value = "/viewconf", method = RequestMethod.GET)
	public ResponseEntity<TestConf> conf() {
		return new ResponseEntity<TestConf>(TestManager.getInstance().getConf(), HttpStatus.OK);
	}
}