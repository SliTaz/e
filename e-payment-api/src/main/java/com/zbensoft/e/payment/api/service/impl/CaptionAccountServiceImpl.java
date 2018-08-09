package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.CaptionAccountService;
import com.zbensoft.e.payment.db.domain.CaptionAccount;
import com.zbensoft.e.payment.db.mapper.CaptionAccountMapper;

@Service
public class CaptionAccountServiceImpl implements CaptionAccountService {
	
	@Autowired
	CaptionAccountMapper captionAccountMapper;

	@Override
	public int deleteByPrimaryKey(String captionAccountCode) {
		return captionAccountMapper.deleteByPrimaryKey(captionAccountCode);
	}

	@Override
	public int insert(CaptionAccount record) {
		return captionAccountMapper.insert(record);
	}

	@Override
	public int insertSelective(CaptionAccount record) {
		return captionAccountMapper.insertSelective(record);
	}

	@Override
	public CaptionAccount selectByPrimaryKey(String captionAccountCode) {
		return captionAccountMapper.selectByPrimaryKey(captionAccountCode);
	}

	@Override
	public int updateByPrimaryKeySelective(CaptionAccount record) {
		return captionAccountMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(CaptionAccount record) {
		return captionAccountMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<CaptionAccount> selectPage(CaptionAccount record) {
		return captionAccountMapper.selectPage(record);
	}

	@Override
	public boolean isExist(CaptionAccount captionAccount) {
		return findByName(captionAccount.getName()) != null;
	}
	
	private CaptionAccount findByCode(String captionAccountCode) {
		return captionAccountMapper.findByCode(captionAccountCode);
	}
	
	private CaptionAccount findByName(String name) {
		return captionAccountMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		captionAccountMapper.deleteAll();
	}

	@Override
	public int count(CaptionAccount captionAccount) {
		return captionAccountMapper.count(captionAccount);
	}

}
