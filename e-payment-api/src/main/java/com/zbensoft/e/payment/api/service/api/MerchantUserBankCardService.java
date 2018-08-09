package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;

public interface MerchantUserBankCardService {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int deleteByPrimaryKey(String bankBindId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int insert(MerchantUserBankCard record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int insertSelective(MerchantUserBankCard record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	MerchantUserBankCard selectByPrimaryKey(String bankBindId);
	MerchantUserBankCard selectByCardNo(String cardNo);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int updateByPrimaryKeySelective(MerchantUserBankCard record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table reconciliation_bath
	 *
	 * @mbg.generated Thu May 25 16:26:59 CST 2017
	 */
	int updateByPrimaryKey(MerchantUserBankCard record);

	List<MerchantUserBankCard> selectPage(MerchantUserBankCard record);

	void deleteAll();

	int count(MerchantUserBankCard merchantUserBankCard);

	List<MerchantUserBankCard> selectByUserId(String userId);

	MerchantUserBankCard selectByUserIdBankCard(MerchantUserBankCard merchantUserBankCardSer);

	MerchantUserBankCard selectByUserIdCardNo(MerchantUserBankCard merchantUserBankCard);

	void updateBySelective(MerchantUserBankCard merchantUserBankCardUpdate);

	MerchantUserBankCard selectByClapStoreCardNo(MerchantUserBankCard  merchantUserBankCard );



}