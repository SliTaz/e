package com.zbensoft.e.payment.api.service.api;

import java.util.List;

import com.zbensoft.e.payment.db.domain.CaptionAccount;

public interface CaptionAccountService {
    
	/**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table caption_account
     *
     * @mbg.generated Thu May 25 14:24:24 GMT+08:00 2017
     */
    int deleteByPrimaryKey(String captionAccountCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table caption_account
     *
     * @mbg.generated Thu May 25 14:24:24 GMT+08:00 2017
     */
    int insert(CaptionAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table caption_account
     *
     * @mbg.generated Thu May 25 14:24:24 GMT+08:00 2017
     */
    int insertSelective(CaptionAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table caption_account
     *
     * @mbg.generated Thu May 25 14:24:24 GMT+08:00 2017
     */
    CaptionAccount selectByPrimaryKey(String captionAccountCode);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table caption_account
     *
     * @mbg.generated Thu May 25 14:24:24 GMT+08:00 2017
     */
    int updateByPrimaryKeySelective(CaptionAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table caption_account
     *
     * @mbg.generated Thu May 25 14:24:24 GMT+08:00 2017
     */
    int updateByPrimaryKey(CaptionAccount record);
    
    List<CaptionAccount> selectPage(CaptionAccount record);
    boolean isExist(CaptionAccount captionAccount);
    void deleteAll();

	int count(CaptionAccount captionAccount);
}