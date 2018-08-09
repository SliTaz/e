package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ConsumerFamily;

public interface ConsumerFamilyService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int deleteByPrimaryKey(String familyId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insert(ConsumerFamily record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insertSelective(ConsumerFamily record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    ConsumerFamily selectByPrimaryKey(String familyId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKeySelective(ConsumerFamily record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKey(ConsumerFamily record);
    
    List<ConsumerFamily> selectPage(ConsumerFamily record);

	boolean isConsumerFamilyExist(ConsumerFamily consumerFamily);
	
	void deleteAll();

	int count(ConsumerFamily consumerFamily);



}