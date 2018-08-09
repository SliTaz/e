package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.AlarmType;

public interface AlarmTypeService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int deleteByPrimaryKey(String alarmTypeCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insert(AlarmType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int insertSelective(AlarmType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    AlarmType selectByPrimaryKey(String alarmTypeCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKeySelective(AlarmType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table pay_rule
     *
     * @mbg.generated Thu May 25 10:46:10 CST 2017
     */
    int updateByPrimaryKey(AlarmType record);
    
    List<AlarmType> selectPage(AlarmType record);

	void deleteAll();

	int count(AlarmType alarmType);

	List<AlarmType> selectAll();
}