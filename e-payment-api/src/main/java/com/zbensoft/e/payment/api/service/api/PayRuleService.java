package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.PayRule;

public interface PayRuleService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int deleteByPrimaryKey(String payRuleId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insert(PayRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insertSelective(PayRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    PayRule selectByPrimaryKey(String payRuleId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKeySelective(PayRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKey(PayRule record);
    
    List<PayRule> selectPage(PayRule record);

	void deleteAll();

	int count(PayRule payRule);
}