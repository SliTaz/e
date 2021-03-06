package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;

import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.api.vo.buyerRegister.RegisterRequest;
import com.zbensoft.e.payment.api.vo.buyerRegister.RegisterResponse;
import com.zbensoft.e.payment.api.vo.clap.ClapBuyer;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

public interface ConsumerUserService {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
//	@PreAuthorize("hasRole('CONSUMER_EDIT')")
	int deleteByPrimaryKey(String userId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
//	@PreAuthorize("hasRole('CONSUMER_EDIT')")
	int insert(ConsumerUser record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
//	@PreAuthorize("hasRole('CONSUMER_EDIT')")
	int insertSelective(ConsumerUser record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
//	@PreAuthorize("hasRole('CONSUMER_QUERY') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	ConsumerUser selectByPrimaryKey(String userId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
//	@PreAuthorize("hasRole('CONSUMER_EDIT') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	int updateByPrimaryKeySelective(ConsumerUser record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table gov_user
	 *
	 * @mbg.generated Wed Jun 07 15:37:39 CST 2017
	 */
//	@PreAuthorize("hasRole('CONSUMER_EDIT') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	int updateByPrimaryKey(ConsumerUser record);

//	@PreAuthorize("hasRole('CONSUMER_QUERY') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	List<ConsumerUser> selectPage(ConsumerUser record);

//	@PreAuthorize("hasRole('CONSUMER_QUERY') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	boolean isConsumerUserExist(ConsumerUser consumerUser);

	ConsumerUser selectByUserName(String userName);

//	@PreAuthorize("hasRole('CONSUMER_QUERY') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	int count(ConsumerUser consumerUser);

	JwtAuthenticationResponse login(String userName, String password) throws Exception;

	/**
	 * 
	 * @param userName
	 * @return
	 */
//	@PreAuthorize("hasRole('CONSUMER_QUERY') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	Double queryConsumerBalance(String userName);

//	@PreAuthorize("hasRole('CONSUMER_EDIT') or hasRole('CONSUMER') or hasRole('MERCHANT')")
	int updateAmountByPrimaryKey(ConsumerUser consumerUser);
	
	
	int activateByTime(ConsumerUser record);

	Double selectSumBalance();
	
	void updateUserTransactional(ConsumerUser consumerUserNew, ConsumerUserClap consumerUserClapNew,
			ConsumerRoleUserKey consumerRoleUserKeyNew, ClapBuyer buyer);

	void insertUserTransactional(ConsumerUser consumerUserNew, ConsumerUserClap consumerUserClapNew,
			ConsumerRoleUserKey consumerRoleUserKeyNew, ClapBuyer buyer);

	void insertList( List<ConsumerUser> consumerUserList,
			List<ConsumerUserClap> consumerUserClapList, List<ConsumerFamily> consumerFamilyList,
			List<ConsumerRoleUserKey> consumerRoleUserKeyList);

	void updateList(List<ConsumerUser> consumerUserList);

	ResponseRestEntity<RegisterResponse> registerSucc(RegisterRequest registerRequest, CloseableHttpClient httpClient, RequestConfig config, String URL_REGISTER_REGISTER_UPDATE_WALLET) throws Exception;
}