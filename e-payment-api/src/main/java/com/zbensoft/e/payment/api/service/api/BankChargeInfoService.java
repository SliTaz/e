package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.BankChargeInfo;

public interface BankChargeInfoService {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table bank_charge_info_0102
	 * @mbg.generated  Sat Sep 09 21:25:22 VET 2017
	 */
	int deleteByPrimaryKey(String refNo);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table bank_charge_info_0102
	 * @mbg.generated  Sat Sep 09 21:25:22 VET 2017
	 */
	int insert(BankChargeInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table bank_charge_info_0102
	 * @mbg.generated  Sat Sep 09 21:25:22 VET 2017
	 */
	int insertSelective(BankChargeInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table bank_charge_info_0102
	 * @mbg.generated  Sat Sep 09 21:25:22 VET 2017
	 */
	BankChargeInfo selectByPrimaryKey(String refNo);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table bank_charge_info_0102
	 * @mbg.generated  Sat Sep 09 21:25:22 VET 2017
	 */
	int updateByPrimaryKeySelective(BankChargeInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table bank_charge_info_0102
	 * @mbg.generated  Sat Sep 09 21:25:22 VET 2017
	 */
	int updateByPrimaryKey(BankChargeInfo record);

	void deleteAll(String bankId);

	void deleteAll();

	int insertBatch(List<BankChargeInfo> bankChargeInfoList);
	List<BankChargeInfo> selectPage(BankChargeInfo record);
	int count(BankChargeInfo bankChargeInfo);
}