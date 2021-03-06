package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ShellPc;

public interface ShellPcService {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int deleteByPrimaryKey(String pcCode);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int insert(ShellPc record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int insertSelective(ShellPc record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	ShellPc selectByPrimaryKey(String pcCode);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int updateByPrimaryKeySelective(ShellPc record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int updateByPrimaryKey(ShellPc record);

	List<ShellPc> selectPage(ShellPc record);


	int count(ShellPc shellPc);
}