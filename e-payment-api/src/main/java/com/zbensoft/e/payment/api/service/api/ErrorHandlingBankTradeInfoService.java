package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ErrorHandlingBankTradeInfo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankTradeInfoKey;

public interface ErrorHandlingBankTradeInfoService {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table error_handling_bank_trade_info
	 * @mbg.generated  Sun Jul 30 20:35:56 VET 2017
	 */
	int deleteByPrimaryKey(ErrorHandlingBankTradeInfoKey key);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table error_handling_bank_trade_info
	 * @mbg.generated  Sun Jul 30 20:35:56 VET 2017
	 */
	int insert(ErrorHandlingBankTradeInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table error_handling_bank_trade_info
	 * @mbg.generated  Sun Jul 30 20:35:56 VET 2017
	 */
	int insertSelective(ErrorHandlingBankTradeInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table error_handling_bank_trade_info
	 * @mbg.generated  Sun Jul 30 20:35:56 VET 2017
	 */
	ErrorHandlingBankTradeInfo selectByPrimaryKey(ErrorHandlingBankTradeInfoKey key);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table error_handling_bank_trade_info
	 * @mbg.generated  Sun Jul 30 20:35:56 VET 2017
	 */
	int updateByPrimaryKeySelective(ErrorHandlingBankTradeInfo record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table error_handling_bank_trade_info
	 * @mbg.generated  Sun Jul 30 20:35:56 VET 2017
	 */
	int updateByPrimaryKey(ErrorHandlingBankTradeInfo record);

	void deleteByBankIdAndTime(ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo);
	
    List<ErrorHandlingBankTradeInfo> selectPage(ErrorHandlingBankTradeInfo record);

	int count(ErrorHandlingBankTradeInfo errorHandlingBankTradeInfo);
}