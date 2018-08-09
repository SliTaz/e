package com.zbensoft.e.payment.api.log;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDR_LOG {

	private static Logger log = LoggerFactory.getLogger(CDR_LOG.class);

	public static void DEBUG(String msg) {
		log.debug(msg);
	}

	public static void INFO(String msg) {
		log.info(msg);
	}

	public static void WARN(String msg) {
		log.warn(msg);
	}

	public static void ERROR(String msg) {
		log.error(msg);
	}

	public static void main(String[] args) {
		while (true) {
			CDR_LOG.INFO("" + new Random().nextInt(100000));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
