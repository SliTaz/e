package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.PayGetwayType;

public interface PayGetwayTypeService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_getway_type
     *
     * @mbg.generated Tue Oct 24 10:08:36 CST 2017
     */
    int deleteByPrimaryKey(String payGatewayTypeId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_getway_type
     *
     * @mbg.generated Tue Oct 24 10:08:36 CST 2017
     */
    int insert(PayGetwayType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_getway_type
     *
     * @mbg.generated Tue Oct 24 10:08:36 CST 2017
     */
    int insertSelective(PayGetwayType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_getway_type
     *
     * @mbg.generated Tue Oct 24 10:08:36 CST 2017
     */
    PayGetwayType selectByPrimaryKey(String payGatewayTypeId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_getway_type
     *
     * @mbg.generated Tue Oct 24 10:08:36 CST 2017
     */
    int updateByPrimaryKeySelective(PayGetwayType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_getway_type
     *
     * @mbg.generated Tue Oct 24 10:08:36 CST 2017
     */
    int updateByPrimaryKey(PayGetwayType record);
    
    List<PayGetwayType> selectPage(PayGetwayType record);

	boolean isPayGetwayTypeExist(PayGetwayType payGetwayType);

	int count(PayGetwayType payGetwayType);
}