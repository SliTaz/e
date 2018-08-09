package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.MerchantActiveReport;
import com.zbensoft.e.payment.db.domain.MerchantUser;

public interface MerchantActiveReportService {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table merchant_active_report
	 * @mbg.generated  Fri Dec 01 13:07:35 CST 2017
	 */
	int deleteByPrimaryKey(String statisticsTime);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table merchant_active_report
	 * @mbg.generated  Fri Dec 01 13:07:35 CST 2017
	 */
	int insert(MerchantActiveReport record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table merchant_active_report
	 * @mbg.generated  Fri Dec 01 13:07:35 CST 2017
	 */
	int insertSelective(MerchantActiveReport record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table merchant_active_report
	 * @mbg.generated  Fri Dec 01 13:07:35 CST 2017
	 */
	MerchantActiveReport selectByPrimaryKey(String statisticsTime);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table merchant_active_report
	 * @mbg.generated  Fri Dec 01 13:07:35 CST 2017
	 */
	int updateByPrimaryKeySelective(MerchantActiveReport record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table merchant_active_report
	 * @mbg.generated  Fri Dec 01 13:07:35 CST 2017
	 */
	int updateByPrimaryKey(MerchantActiveReport record);
	
	List<MerchantActiveReport> selectPage(MerchantActiveReport record);

	int deleteAll();

	int count(MerchantActiveReport merchantActiveReport);
}