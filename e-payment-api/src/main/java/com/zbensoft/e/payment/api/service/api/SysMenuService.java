package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.SysMenu;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuParam;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuResponse;

public interface SysMenuService {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_menu
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int deleteByPrimaryKey(String menuId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_menu
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int insert(SysMenu record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_menu
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int insertSelective(SysMenu record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_menu
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	SysMenu selectByPrimaryKey(String menuId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_menu
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int updateByPrimaryKeySelective(SysMenu record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table sys_menu
	 * 
	 * @mbg.generated Thu May 25 15:26:15 CST 2017
	 */
	int updateByPrimaryKey(SysMenu record);

	List<SysMenu> selectPage(SysMenu record);


	void deleteAll();

	int count(SysMenu menu);
	List<SysMenuUserMenuResponse> getUserMenus(SysMenuUserMenuParam sysMenuUserMenuParam, String weburl);
	SysMenu selectByMenuName(String name);
	boolean isRoleExist(SysMenu menu);

	List<SysMenu> getRoleResources(String id);

	List<SysMenu> findAll();

	void saveRoleRescours(String roleId, List<String> list);

	SysMenu findTopMenu(String keyWord);
}