package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ConsumerBlackNumber;

public interface ConsumerBlackNumberService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int deleteByPrimaryKey(String userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insert(ConsumerBlackNumber record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insertSelective(ConsumerBlackNumber record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    ConsumerBlackNumber selectByPrimaryKey(String userId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKeySelective(ConsumerBlackNumber record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKey(ConsumerBlackNumber record);
    
    List<ConsumerBlackNumber> selectPage(ConsumerBlackNumber record);

	void deleteAll();

	int count(ConsumerBlackNumber consumerBlackNumber);
	boolean isExist(ConsumerBlackNumber consumerBlackNumber);
}