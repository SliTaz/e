package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantUserBankCardService;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.mapper.MerchantUserBankCardMapper;

@Service
public class MerchantUserBankCardServiceImpl implements MerchantUserBankCardService {

	@Autowired
	MerchantUserBankCardMapper merchantUserBankCardMapper;

	@Override
	public int deleteByPrimaryKey(String bankBindId) {
		return merchantUserBankCardMapper.deleteByPrimaryKey(bankBindId);
	}

	@Override
	public int insert(MerchantUserBankCard record) {
		return merchantUserBankCardMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantUserBankCard record) {
		return merchantUserBankCardMapper.insertSelective(record);
	}

	@Override
	public MerchantUserBankCard selectByCardNo(String cardNo) {
		return merchantUserBankCardMapper.selectByCardNo(cardNo);
	}
	
	@Override
	public MerchantUserBankCard selectByPrimaryKey(String bankBindId) {
		return merchantUserBankCardMapper.selectByPrimaryKey(bankBindId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantUserBankCard record) {
		return merchantUserBankCardMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantUserBankCard record) {
		return merchantUserBankCardMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantUserBankCard> selectPage(MerchantUserBankCard record) {
		return merchantUserBankCardMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		merchantUserBankCardMapper.deleteAll();
	}

	@Override
	public int count(MerchantUserBankCard merchantUserBankCard) {
		return merchantUserBankCardMapper.count(merchantUserBankCard);
	}

	@Override
	public List<MerchantUserBankCard> selectByUserId(String userId) {
		return merchantUserBankCardMapper.selectByUserId(userId);
	}

	@Override
	public MerchantUserBankCard selectByUserIdBankCard(MerchantUserBankCard merchantUserBankCardSer) {
		return merchantUserBankCardMapper.selectByUserIdBankCard(merchantUserBankCardSer);
		
	}

	@Override
	public MerchantUserBankCard selectByUserIdCardNo(MerchantUserBankCard merchantUserBankCard) {
		return merchantUserBankCardMapper.selectByUserIdCardNo(merchantUserBankCard);
		
	}

	@Override
	public void updateBySelective(MerchantUserBankCard merchantUserBankCardUpdate) {
		merchantUserBankCardMapper.updateBySelective(merchantUserBankCardUpdate);
	}

	@Override
	public MerchantUserBankCard selectByClapStoreCardNo(MerchantUserBankCard  merchantUserBankCard ) {
		return  merchantUserBankCardMapper.selectByClapStoreCardNo(merchantUserBankCard);
	}



}
