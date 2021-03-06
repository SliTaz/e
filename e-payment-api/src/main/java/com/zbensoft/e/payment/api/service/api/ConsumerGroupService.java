package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.ConsumerGroup;
import com.zbensoft.e.payment.db.domain.ConsumerUser;

public interface ConsumerGroupService {

	/**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table consumer_group
     *
     * @mbg.generated Mon Jun 19 14:13:26 GMT+08:00 2017
     */
    int deleteByPrimaryKey(String consumerGroupId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table consumer_group
     *
     * @mbg.generated Mon Jun 19 14:13:26 GMT+08:00 2017
     */
    int insert(ConsumerGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table consumer_group
     *
     * @mbg.generated Mon Jun 19 14:13:26 GMT+08:00 2017
     */
    int insertSelective(ConsumerGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table consumer_group
     *
     * @mbg.generated Mon Jun 19 14:13:26 GMT+08:00 2017
     */
    ConsumerGroup selectByPrimaryKey(String consumerGroupId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table consumer_group
     *
     * @mbg.generated Mon Jun 19 14:13:26 GMT+08:00 2017
     */
    int updateByPrimaryKeySelective(ConsumerGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table consumer_group
     *
     * @mbg.generated Mon Jun 19 14:13:26 GMT+08:00 2017
     */
    int updateByPrimaryKey(ConsumerGroup record);

	int count(ConsumerGroup consumerGroup);

	List<ConsumerGroup> selectPage(ConsumerGroup consumerGroup);

	boolean isNameExist(ConsumerGroup consumerGroup);

	void deleteAll();

	int saveOrUpdate(List<String> list,String consumerGroupId,boolean deleteFlagAll);

	void statusEnable(String consumerGroupId);

	void statusDisable(String consumerGroupId);
}