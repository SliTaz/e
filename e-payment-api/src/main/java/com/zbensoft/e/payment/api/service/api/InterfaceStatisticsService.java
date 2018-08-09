package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.InterfaceStatistics;
import com.zbensoft.e.payment.db.domain.InterfaceStatisticsKey;
import com.zbensoft.e.payment.db.domain.PayApp;

public interface InterfaceStatisticsService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table interface_statistics
     *
     * @mbg.generated Wed Aug 02 08:36:19 VET 2017
     */
    int deleteByPrimaryKey(InterfaceStatisticsKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table interface_statistics
     *
     * @mbg.generated Wed Aug 02 08:36:19 VET 2017
     */
    int insert(InterfaceStatistics record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table interface_statistics
     *
     * @mbg.generated Wed Aug 02 08:36:19 VET 2017
     */
    int insertSelective(InterfaceStatistics record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table interface_statistics
     *
     * @mbg.generated Wed Aug 02 08:36:19 VET 2017
     */
    InterfaceStatistics selectByPrimaryKey(InterfaceStatisticsKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table interface_statistics
     *
     * @mbg.generated Wed Aug 02 08:36:19 VET 2017
     */
    int updateByPrimaryKeySelective(InterfaceStatistics record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table interface_statistics
     *
     * @mbg.generated Wed Aug 02 08:36:19 VET 2017
     */
    int updateByPrimaryKey(InterfaceStatistics record);
    
    
	List<InterfaceStatistics> selectPage(InterfaceStatistics record);


	int count(InterfaceStatistics interfaceStatistics);

	int countDay(InterfaceStatistics interfaceStatistics, String type);

	List<InterfaceStatistics> selectPageDay(InterfaceStatistics record, String type);
}