package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ConsumerTradeKey;
import com.zbensoft.e.payment.db.domain.SysUsersAppKey;

public interface ConsumerTradeService {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_users_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int deleteByPrimaryKey(ConsumerTradeKey key);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_users_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int insert(ConsumerTradeKey record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_users_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int insertSelective(ConsumerTradeKey record);

	List<ConsumerTradeKey> selectPage(ConsumerTradeKey record);

	void deleteAll();

	ConsumerTradeKey selectByPrimaryKey(ConsumerTradeKey key);

	int updateByPrimaryKey(ConsumerTradeKey record);

	int updateByPrimaryKeySelective(ConsumerTradeKey record);

	int count(ConsumerTradeKey consumeTrade);

	List<ConsumerTradeKey> selectByUserId(String userId);

	int countTwo(ConsumerTradeKey consumeTrade);

	List<ConsumerTradeKey> selectPageTwo(ConsumerTradeKey record);

	

}