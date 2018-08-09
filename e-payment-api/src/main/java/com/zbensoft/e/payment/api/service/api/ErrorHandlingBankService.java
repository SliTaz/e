package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ErrorHandlingBank;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankKey;
import com.zbensoft.e.payment.db.domain.ReconciliationBank;

public interface ErrorHandlingBankService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank
     *
     * @mbg.generated Sun Jul 30 18:47:05 VET 2017
     */
    int deleteByPrimaryKey(ErrorHandlingBankKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank
     *
     * @mbg.generated Sun Jul 30 18:47:05 VET 2017
     */
    int insert(ErrorHandlingBank record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank
     *
     * @mbg.generated Sun Jul 30 18:47:05 VET 2017
     */
    int insertSelective(ErrorHandlingBank record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank
     *
     * @mbg.generated Sun Jul 30 18:47:05 VET 2017
     */
    ErrorHandlingBank selectByPrimaryKey(ErrorHandlingBankKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank
     *
     * @mbg.generated Sun Jul 30 18:47:05 VET 2017
     */
    int updateByPrimaryKeySelective(ErrorHandlingBank record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank
     *
     * @mbg.generated Sun Jul 30 18:47:05 VET 2017
     */
    int updateByPrimaryKey(ErrorHandlingBank record);

	void deleteByBankIdAndTime(ErrorHandlingBank errorHandlingBank);
	List<ErrorHandlingBank> selectPage(ErrorHandlingBank record);



	int count(ErrorHandlingBank errorHandlingBank);
}