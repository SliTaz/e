package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.SysRoleUserKey;
import com.zbensoft.e.payment.db.domain.SysUsersAppKey;

public interface SysRoleUserService {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_users_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int deleteByPrimaryKey(SysRoleUserKey key);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_users_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int insert(SysRoleUserKey record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_users_app
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int insertSelective(SysRoleUserKey record);

	List<SysRoleUserKey> selectPage(SysRoleUserKey record);

	void deleteAll();

	SysRoleUserKey selectByPrimaryKey(SysRoleUserKey key);

	int updateByPrimaryKey(SysRoleUserKey record);

	int updateByPrimaryKeySelective(SysRoleUserKey record);

	int count(SysRoleUserKey sysRoleUser);

	List<SysRoleUserKey> selectByUserId(String userId);

	

}