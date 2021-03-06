package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.GoodsUnit;

public interface GoodsUnitService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int deleteByPrimaryKey(String goodUnitId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int insert(GoodsUnit record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int insertSelective(GoodsUnit record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    GoodsUnit selectByPrimaryKey(String goodId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int updateByPrimaryKeySelective(GoodsUnit record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int updateByPrimaryKey(GoodsUnit record);
    
	List<GoodsUnit> selectPage(GoodsUnit record);

	boolean isGoodsUnitExist(GoodsUnit goodsUnit);

	void deleteAll();
	
	int count(GoodsUnit goodsUnit);
}