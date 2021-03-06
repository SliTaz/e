package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.FraultIpAddr;

public interface FraultIpAddrService {
	  /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_ip_addr
     *
     * @mbg.generated Tue Jun 27 15:31:59 CST 2017
     */
    int deleteByPrimaryKey(FraultIpAddr fraultIpAddrId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_ip_addr
     *
     * @mbg.generated Tue Jun 27 15:31:59 CST 2017
     */
    int insert(FraultIpAddr record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_ip_addr
     *
     * @mbg.generated Tue Jun 27 15:31:59 CST 2017
     */
    int insertSelective(FraultIpAddr record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_ip_addr
     *
     * @mbg.generated Tue Jun 27 15:31:59 CST 2017
     */
    FraultIpAddr selectByPrimaryKey(FraultIpAddr fraultIpAddrId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_ip_addr
     *
     * @mbg.generated Tue Jun 27 15:31:59 CST 2017
     */
    int updateByPrimaryKeySelective(FraultIpAddr record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_ip_addr
     *
     * @mbg.generated Tue Jun 27 15:31:59 CST 2017
     */
    int updateByPrimaryKey(FraultIpAddr record);
    int deleteAll();
	int count(FraultIpAddr addr);
	List<FraultIpAddr> selectPage(FraultIpAddr record);
}
