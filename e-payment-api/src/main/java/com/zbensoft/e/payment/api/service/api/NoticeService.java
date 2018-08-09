package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.Notice;

public interface NoticeService {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int deleteByPrimaryKey(String noticeId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int insert(Notice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int insertSelective(Notice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	Notice selectByPrimaryKey(String noticeId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int updateByPrimaryKeySelective(Notice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int updateByPrimaryKey(Notice record);

	List<Notice> selectPage(Notice record);

	void deleteAll();

	int count(Notice notice);

	List<Notice> selectNewestRecords(Notice notice);
}