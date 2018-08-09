package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.FraultInfo;

public interface FraultInfoService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int deleteByPrimaryKey(String fraultInfoId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insert(FraultInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insertSelective(FraultInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    FraultInfo selectByPrimaryKey(String fraultInfoId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKeySelective(FraultInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKey(FraultInfo record);
    
    List<FraultInfo> selectPage(FraultInfo record);

	void deleteAll();

	int count(FraultInfo fraultInfo);
}