package com.zbensoft.e.payment.api.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.RECONCILIATION_LOG;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.TradeInfo;

/**
 * 测试
 * 
 * 0 10 0/2 * * ?
 * 
 * @author xieqiang
 *
 */
public class DeleteHisJob implements Job {
	Logger logger = LoggerFactory.getLogger(getClass());
	private TradeInfoService tradeInfoService = SpringBeanUtil.getBean(TradeInfoService.class);
	int JOB_DELETE_TABLES=SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_DELETE_TABLES);
	int JOB_DELETE_TRADE_INFO_DAY_BEFORE=SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_DELETE_TRADE_INFO_DAY_BEFORE);
	int JOB_DELETE_TRADE_INFO_ONE_TIME_NUM=SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_DELETE_TRADE_INFO_ONE_TIME_NUM);
	
	private static String key = "DeleteHisJob";
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		TASK_LOG.INFO(String.format("%s Start", key));
		boolean isSucc=true;
		long start =System.currentTimeMillis();
		
		isSucc=deleteTradeInfo();
		if (!isSucc) {
			//TODO Alarm 
			RECONCILIATION_LOG.INFO(String.format("%s deleteTradeInfo is fail", key));
		}
		
		
		
		System.out.println("use time :" +(System.currentTimeMillis()-start));
		
		
		TASK_LOG.INFO(String.format("%s End", key));
	}
	
	
	
	private boolean deleteTradeInfo() {
		try {
			while (true) {
				TradeInfo tradeInfo = new TradeInfo();
				String dayStr = DateUtil.getDayStartEndTime(JOB_DELETE_TRADE_INFO_DAY_BEFORE, false,
						DateUtil.DATE_FORMAT_FIVE);
				tradeInfo.setCreateTimeEndSer(dayStr);
				tradeInfo.setDeleteLimit(JOB_DELETE_TRADE_INFO_ONE_TIME_NUM);
				int result = tradeInfoService.limiteDelete(tradeInfo);
				if (result == 0) {
					return true;
				}
			}
			
		} catch (Exception e) {
			TASK_LOG.ERROR("Delete TradeInfo Exception",e);
			return false;
		}
		
	}
}
