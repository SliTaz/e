package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ProfitStatement;

public interface ProfitStatementService {
	/**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table profit_statement
     *
     * @mbg.generated Thu Aug 10 09:54:22 CST 2017
     */
    int deleteByPrimaryKey(String statisticsTime);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table profit_statement
     *
     * @mbg.generated Thu Aug 10 09:54:22 CST 2017
     */
    int insert(ProfitStatement record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table profit_statement
     *
     * @mbg.generated Thu Aug 10 09:54:22 CST 2017
     */
    int insertSelective(ProfitStatement record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table profit_statement
     *
     * @mbg.generated Thu Aug 10 09:54:22 CST 2017
     */
    ProfitStatement selectByPrimaryKey(String statisticsTime);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table profit_statement
     *
     * @mbg.generated Thu Aug 10 09:54:22 CST 2017
     */
    int updateByPrimaryKeySelective(ProfitStatement record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table profit_statement
     *
     * @mbg.generated Thu Aug 10 09:54:22 CST 2017
     */
    int updateByPrimaryKey(ProfitStatement record);
    int count(ProfitStatement profitStatement);
   	List<ProfitStatement> selectPage(ProfitStatement record);
}