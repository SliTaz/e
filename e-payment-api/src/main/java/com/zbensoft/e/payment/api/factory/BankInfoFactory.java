package com.zbensoft.e.payment.api.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.BankInfoService;
import com.zbensoft.e.payment.db.domain.BankInfo;

/**
 * 
 * @author xieqiang
 *
 */
public class BankInfoFactory {
	private static Logger log = Logger.getLogger(BankInfoFactory.class);
	private BankInfoService bankInfoService = SpringBeanUtil.getBean(BankInfoService.class);

	private List<BankInfo> list = new ArrayList<>();

	private static BankInfoFactory instance = new BankInfoFactory();

	private static final Object uniqueLock = new Object();
	private static final Object objectLock = new Object();
	private BankInfo bankInfo = new BankInfo();

	public static BankInfoFactory getInstance() {
		if (null == instance) {
			synchronized (uniqueLock) {
				if (null == instance) {
					instance = new BankInfoFactory();
				}
			}
		}
		return instance;
	}

	public void loadConfig() {
		synchronized (objectLock) {
			try {
				bankInfo.setDeleteFlag(0);
				PageHelper.startPage(1, 100000);
				list = bankInfoService.selectPage(bankInfo);
			} catch (Exception e) {
				log.error("", e);
			}
		}
	}

	public List<BankInfo> get() {
		synchronized (objectLock) {
			return list;
		}
	}

}
