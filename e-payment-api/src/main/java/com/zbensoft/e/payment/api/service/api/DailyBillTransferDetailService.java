package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.DailyBillTransferDetail;

public interface DailyBillTransferDetailService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int deleteByPrimaryKey(DailyBillTransferDetail key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int insert(DailyBillTransferDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int insertSelective(DailyBillTransferDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    DailyBillTransferDetail selectByPrimaryKey(DailyBillTransferDetail key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int updateByPrimaryKeySelective(DailyBillTransferDetail record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table daily_bill_consumption_detail
     *
     * @mbg.generated Mon Jun 19 15:47:39 CST 2017
     */
    int updateByPrimaryKey(DailyBillTransferDetail record);

	List<DailyBillTransferDetail> selectPage(DailyBillTransferDetail record);

	void deleteAll();

	int count(DailyBillTransferDetail dailyBillTransferDetail);

	void deleteByBillDate(String billDate);
}