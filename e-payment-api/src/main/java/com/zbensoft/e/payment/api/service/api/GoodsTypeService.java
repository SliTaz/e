package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.GoodsType;

public interface GoodsTypeService {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int deleteByPrimaryKey(String goodId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int insert(GoodsType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int insertSelective(GoodsType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    GoodsType selectByPrimaryKey(String goodId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int updateByPrimaryKeySelective(GoodsType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table goods_type
     *
     * @mbg.generated Wed Jun 07 15:11:51 CST 2017
     */
    int updateByPrimaryKey(GoodsType record);
    
	List<GoodsType> selectPage(GoodsType record);

	boolean isGoodsTypeExist(GoodsType goodsType);

	void deleteAll();
	
	int count(GoodsType goodsType);

	List<GoodsType> findAll();
}