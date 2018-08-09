package com.zbensoft.e.payment.api.quartz.job.clap;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.ConsumerCouponService;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyCouponService;
import com.zbensoft.e.payment.api.service.api.CouponService;
import com.zbensoft.e.payment.db.domain.ConsumerCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.domain.Coupon;
/**
 * 过期券更新，每天凌晨1点执行。
 * 
 * 0 0 1 * * ?
 * 
 * @author xieqiang
 *
 */
public class CouponExpiredUpdateJob implements Job {
	
	private static final Logger log = LoggerFactory.getLogger(CouponExpiredUpdateJob.class);

	private static String key = "ExpiredCouponUpdateJob";
	private ConsumerCouponService consumerCouponService = SpringBeanUtil.getBean(ConsumerCouponService.class);
	private CouponService couponService = SpringBeanUtil.getBean(CouponService.class);
	private ConsumerFamilyCouponService consumerFamilyCouponService =SpringBeanUtil.getBean(ConsumerFamilyCouponService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//selectExpiredCoupon
		TASK_LOG.INFO(String.format("%s Start", key));
		Coupon couponSer=new Coupon();
		PageHelper.startPage(1, 100000);
		List<Coupon> expiredCouponList = couponService.selectExpiredCoupon(couponSer);
		if(expiredCouponList!=null&&expiredCouponList.size()>0){
			long successCount=0l;
			for (Coupon expCoupon : expiredCouponList) {
				try {
					if (expCoupon.getGetLimit() != null) {
						if (MessageDef.GET_LIMIT.CONSUMER == expCoupon.getGetLimit()) {
							ConsumerCoupon consumerCoupon = new ConsumerCoupon();
							consumerCoupon.setCouponId(expCoupon.getCouponId());
							consumerCoupon.setStatus(MessageDef.COUPON_STATUS.UNUSE);
							consumerCouponService.updateByStatus(consumerCoupon);
						} else {
							ConsumerFamilyCoupon consumerFamilyCoupon = new ConsumerFamilyCoupon();
							consumerFamilyCoupon.setCouponId(expCoupon.getCouponId());
							consumerFamilyCoupon.setStatus(MessageDef.COUPON_STATUS.UNUSE);
							consumerFamilyCouponService.updateByStatus(consumerFamilyCoupon);
						}
					}
					successCount++;
				} catch (Exception e) {
					log.error(String.format("%s ExpiredCouponUpdate fail", key), e);
					TASK_LOG.INFO(String.format("%s ExpiredCouponUpdate fail", key));
					TASK_LOG.ERROR(String.format("%s ExpiredCouponUpdate fail", key), e);
				}
				TASK_LOG.INFO(String.format("%s ExpiredCouponUpdate finish %d/%d", key,successCount,expiredCouponList.size()));
			}
			
		}
		TASK_LOG.INFO(String.format("%s End", key));
		
	}
}
