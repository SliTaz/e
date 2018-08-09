package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.db.domain.SysUser;

public interface SysUserService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated Wed May 24 13:36:02 CST 2017
     */
    int deleteByPrimaryKey(String userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated Wed May 24 13:36:02 CST 2017
     */
    int insert(SysUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated Wed May 24 13:36:02 CST 2017
     */
    int insertSelective(SysUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated Wed May 24 13:36:02 CST 2017
     */
    SysUser selectByPrimaryKey(String userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated Wed May 24 13:36:02 CST 2017
     */
    int updateByPrimaryKeySelective(SysUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sys_user
     *
     * @mbg.generated Wed May 24 13:36:02 CST 2017
     */
    int updateByPrimaryKey(SysUser record);

    
    List<SysUser> selectPage(SysUser record);
	boolean isUserExist(SysUser user);

	void deleteAll();

	SysUser selectByUserName(String userName);

	JwtAuthenticationResponse login(String username, String password) throws Exception;

	JwtAuthenticationResponse refresh(String token);

	SysUser register(SysUser addedUser);

	int count(SysUser user);

	void logout(String token);
}