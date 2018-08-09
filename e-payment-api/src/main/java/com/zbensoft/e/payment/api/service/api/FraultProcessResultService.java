package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.FraultProcessResult;

public interface FraultProcessResultService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int deleteByPrimaryKey(FraultProcessResult key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int insert(FraultProcessResult record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int insertSelective(FraultProcessResult record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    FraultProcessResult selectByPrimaryKey(FraultProcessResult key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int updateByPrimaryKeySelective(FraultProcessResult record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int updateByPrimaryKey(FraultProcessResult record);

	List<FraultProcessResult> selectPage(FraultProcessResult record);

	void deleteAll();

	int count(FraultProcessResult consumerCoupon);

}