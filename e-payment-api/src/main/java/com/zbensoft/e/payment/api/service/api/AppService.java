package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.App;

public interface AppService {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int deleteByPrimaryKey(String appId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int insert(App record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int insertSelective(App record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	App selectByPrimaryKey(String appId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int updateByPrimaryKeySelective(App record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int updateByPrimaryKey(App record);

	List<App> selectPage(App record);

	boolean isAppExist(App app);

	void deleteAll();

	int count(App app);
}