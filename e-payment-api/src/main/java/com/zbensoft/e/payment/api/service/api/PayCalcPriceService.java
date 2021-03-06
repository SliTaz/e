package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.PayCalcPrice;

public interface PayCalcPriceService {
	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table pay_calc_price
	 *
	 * @mbg.generated Thu May 25 10:46:10 CST 2017
	 */
	int deleteByPrimaryKey(String payCalcPriceId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table pay_calc_price
	 *
	 * @mbg.generated Thu May 25 10:46:10 CST 2017
	 */
	int insert(PayCalcPrice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table pay_calc_price
	 *
	 * @mbg.generated Thu May 25 10:46:10 CST 2017
	 */
	int insertSelective(PayCalcPrice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table pay_calc_price
	 *
	 * @mbg.generated Thu May 25 10:46:10 CST 2017
	 */
	PayCalcPrice selectByPrimaryKey(String payCalcPriceId);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table pay_calc_price
	 *
	 * @mbg.generated Thu May 25 10:46:10 CST 2017
	 */
	int updateByPrimaryKeySelective(PayCalcPrice record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds
	 * to the database table pay_calc_price
	 *
	 * @mbg.generated Thu May 25 10:46:10 CST 2017
	 */
	int updateByPrimaryKey(PayCalcPrice record);

	List<PayCalcPrice> selectPage(PayCalcPrice record);

	boolean isPayCalcPriceExist(PayCalcPrice payCalcPrice);

	void deleteAll();

	int count(PayCalcPrice payCalcPrice);
}