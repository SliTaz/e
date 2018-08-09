package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.BalanceStatistics;

public interface BalanceStatisticsService {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table bank_info
	 *
	 * @mbg.generated Thu May 25 09:28:45 CST 2017
	 */
	int deleteByPrimaryKey(String time);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table bank_info
	 *
	 * @mbg.generated Thu May 25 09:28:45 CST 2017
	 */
	int insert(BalanceStatistics record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table bank_info
	 *
	 * @mbg.generated Thu May 25 09:28:45 CST 2017
	 */
	int insertSelective(BalanceStatistics record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table bank_info
	 *
	 * @mbg.generated Thu May 25 09:28:45 CST 2017
	 */
	BalanceStatistics selectByPrimaryKey(String time);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table bank_info
	 *
	 * @mbg.generated Thu May 25 09:28:45 CST 2017
	 */
	int updateByPrimaryKeySelective(BalanceStatistics record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table bank_info
	 *
	 * @mbg.generated Thu May 25 09:28:45 CST 2017
	 */
	int updateByPrimaryKey(BalanceStatistics record);

	List<BalanceStatistics> selectPage(BalanceStatistics record);

	void deleteAll();
	
	int count(BalanceStatistics bankInfo);
}