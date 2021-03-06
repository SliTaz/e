package com.zbensoft.e.payment.api.service.api;

import java.math.BigDecimal;
import java.util.List;

import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.TradeInfo;

public interface TradeInfoService {
    
	/**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_info
     *
     * @mbg.generated Thu May 25 13:25:04 GMT+08:00 2017
     */
    int deleteByPrimaryKey(String tradeSeq);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_info
     *
     * @mbg.generated Thu May 25 13:25:04 GMT+08:00 2017
     */
    int insert(TradeInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_info
     *
     * @mbg.generated Thu May 25 13:25:04 GMT+08:00 2017
     */
    int insertSelective(TradeInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_info
     *
     * @mbg.generated Thu May 25 13:25:04 GMT+08:00 2017
     */
    TradeInfo selectByPrimaryKey(String tradeSeq);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_info
     *
     * @mbg.generated Thu May 25 13:25:04 GMT+08:00 2017
     */
    int updateByPrimaryKeySelective(TradeInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table trade_info
     *
     * @mbg.generated Thu May 25 13:25:04 GMT+08:00 2017
     */
    int updateByPrimaryKey(TradeInfo record);
    
    List<TradeInfo> selectPage(TradeInfo record);

	int count(TradeInfo tradeInfo);

	List<TradeInfo> selectPageByUser(TradeInfo tradeInfo);

	int countByUser(TradeInfo tradeInfo);

	void restoreByPrimaryKey(String tradeSeq, String userId);

	void foreverDeleteByPrimaryKey(String tradeSeq, String userId);

	TradeInfo getTradInfoByTradeSeq(String tradeSeq);

	void deleteByTradeInfo(String tradeSeq,String userId);

	List<TradeInfo> selectbyOrderNoInDay(TradeInfo tradeInfo);

	void remarkOrder(TradeInfo tradeInfo, String userId);

	List<TradeInfo> getTradInfoByParentTradeSeq(String tradeSeq);

	Double sumByDay(TradeInfo tradeInfoSer);

	List<TradeInfo> selectByDayLmit(TradeInfo tradeInfoSer);

	int countByDay(TradeInfo tradeInfoSer);

	void updateWithPayerSelective(TradeInfo upDateTradeInfo, MerchantUser updateMerchantUser,
			ConsumerUser updateConsumerUser);

	int limiteDelete(TradeInfo tradeInfo);

	List<TradeInfo> selectbyOrderNoInDayForValidateExist(TradeInfo tradeInfoForSelectbyOrderNo);

}