package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.db.domain.GovUser;

public interface GovUserService {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
	int deleteByPrimaryKey(String userId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
	int insert(GovUser record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
	int insertSelective(GovUser record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
	GovUser selectByPrimaryKey(String userId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
	int updateByPrimaryKeySelective(GovUser record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
	int updateByPrimaryKey(GovUser record);

	List<GovUser> selectPage(GovUser record);

	boolean isGovUserExist(GovUser govUser);

	void deleteAll();

	GovUser selectByUserName(String userName);

	



	int count(GovUser govUser);

	JwtAuthenticationResponse login(String userName, String password) throws Exception;
}