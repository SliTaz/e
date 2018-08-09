package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ErrorHandlingBankChargeInfo;
import com.zbensoft.e.payment.db.domain.ErrorHandlingBankChargeInfoKey;

public interface ErrorHandlingBankChargeInfoService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank_charge_info
     *
     * @mbg.generated Sat Sep 09 21:25:22 VET 2017
     */
    int deleteByPrimaryKey(ErrorHandlingBankChargeInfoKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank_charge_info
     *
     * @mbg.generated Sat Sep 09 21:25:22 VET 2017
     */
    int insert(ErrorHandlingBankChargeInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank_charge_info
     *
     * @mbg.generated Sat Sep 09 21:25:22 VET 2017
     */
    int insertSelective(ErrorHandlingBankChargeInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank_charge_info
     *
     * @mbg.generated Sat Sep 09 21:25:22 VET 2017
     */
    ErrorHandlingBankChargeInfo selectByPrimaryKey(ErrorHandlingBankChargeInfoKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank_charge_info
     *
     * @mbg.generated Sat Sep 09 21:25:22 VET 2017
     */
    int updateByPrimaryKeySelective(ErrorHandlingBankChargeInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table error_handling_bank_charge_info
     *
     * @mbg.generated Sat Sep 09 21:25:22 VET 2017
     */
    int updateByPrimaryKey(ErrorHandlingBankChargeInfo record);

	void deleteByBankIdAndTime(ErrorHandlingBankChargeInfo errorHandlingBankChargeInfo);
	List<ErrorHandlingBankChargeInfo> selectPage(ErrorHandlingBankChargeInfo record);
	int count(ErrorHandlingBankChargeInfo errorHandlingBankChargeInfo);
}