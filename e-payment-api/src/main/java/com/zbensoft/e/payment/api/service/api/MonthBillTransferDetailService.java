package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.MonthBillTransferDetail;

public interface MonthBillTransferDetailService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int deleteByPrimaryKey(MonthBillTransferDetail key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int insert(MonthBillTransferDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int insertSelective(MonthBillTransferDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    MonthBillTransferDetail selectByPrimaryKey(MonthBillTransferDetail key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int updateByPrimaryKeySelective(MonthBillTransferDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int updateByPrimaryKey(MonthBillTransferDetail record);

	List<MonthBillTransferDetail> selectPage(MonthBillTransferDetail record);

	void deleteAll();

	int count(MonthBillTransferDetail monthBillTransferDetail);

	void deleteByBillDate(String billDate);

	List<MonthBillTransferDetail> queryMontyBillTransferDetail(String userId, String[] split);
}