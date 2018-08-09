package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.PayApp;

public interface PayAppService {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int deleteByPrimaryKey(String payAppId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int insert(PayApp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int insertSelective(PayApp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	PayApp selectByPrimaryKey(String payAppId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int updateByPrimaryKeySelective(PayApp record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int updateByPrimaryKey(PayApp record);

	List<PayApp> selectPage(PayApp record);

	void deleteAll();

	int count(PayApp payApp);
}