package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.FraultIdNumber;

public interface FraultIdNumberService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_id_number
     *
     * @mbg.generated Wed Jun 28 09:29:32 CST 2017
     */
    int deleteByPrimaryKey(String fraultIdNumberId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_id_number
     *
     * @mbg.generated Wed Jun 28 09:29:32 CST 2017
     */
    int insert(FraultIdNumber record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_id_number
     *
     * @mbg.generated Wed Jun 28 09:29:32 CST 2017
     */
    int insertSelective(FraultIdNumber record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_id_number
     *
     * @mbg.generated Wed Jun 28 09:29:32 CST 2017
     */
    FraultIdNumber selectByPrimaryKey(String fraultIdNumberId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_id_number
     *
     * @mbg.generated Wed Jun 28 09:29:32 CST 2017
     */
    int updateByPrimaryKeySelective(FraultIdNumber record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table frault_id_number
     *
     * @mbg.generated Wed Jun 28 09:29:32 CST 2017
     */
    int updateByPrimaryKey(FraultIdNumber record);
    int deleteAll();
	int count(FraultIdNumber addr);
	List<FraultIdNumber> selectPage(FraultIdNumber record);
}