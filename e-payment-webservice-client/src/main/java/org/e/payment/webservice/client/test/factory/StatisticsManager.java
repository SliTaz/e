package org.e.payment.webservice.client.test.factory;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsManager {

	private static final Logger log = LoggerFactory.getLogger(StatisticsManager.class);

	private static StatisticsManager instance = new StatisticsManager();

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	private static AtomicLong s = new AtomicLong(0);
	private static long st = System.currentTimeMillis();

	public static StatisticsManager getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new StatisticsManager();
				}
			}
		}
		return instance;
	}

	public void add() {
		synchronized (objectLock) {
			long tt = System.currentTimeMillis();
			if ((tt - st) > (1000 * 10)) {
				log.info("===" + tt + "=======" + s.get());
				st = tt;
				s.set(0l);
			}
			s.getAndAdd(1l);
		}
	}

}
