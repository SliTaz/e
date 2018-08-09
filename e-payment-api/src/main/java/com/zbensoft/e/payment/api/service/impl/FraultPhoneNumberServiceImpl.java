package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultPhoneNumberService;
import com.zbensoft.e.payment.db.domain.FraultPhoneNumber;
import com.zbensoft.e.payment.db.mapper.FraultPhoneNumberMapper;
@Service
public class FraultPhoneNumberServiceImpl implements FraultPhoneNumberService {
	@Autowired
	FraultPhoneNumberMapper fraultPhoneNumberMapper;
	@Override
	public int deleteByPrimaryKey(String fraultPhoneNumberId) {
		
		return fraultPhoneNumberMapper.deleteByPrimaryKey(fraultPhoneNumberId);
	}

	@Override
	public int insert(FraultPhoneNumber record) {
		
		return fraultPhoneNumberMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultPhoneNumber record) {
		
		return fraultPhoneNumberMapper.insertSelective(record);
	}

	@Override
	public FraultPhoneNumber selectByPrimaryKey(String fraultPhoneNumberId) {
		
		return fraultPhoneNumberMapper.selectByPrimaryKey(fraultPhoneNumberId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultPhoneNumber record) {
		
		return fraultPhoneNumberMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultPhoneNumber record) {
		
		return fraultPhoneNumberMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		
		return fraultPhoneNumberMapper.deleteAll();
	}

	@Override
	public int count(FraultPhoneNumber number) {
		
		return fraultPhoneNumberMapper.count(number);
	}

	@Override
	public List<FraultPhoneNumber> selectPage(FraultPhoneNumber number) {
		
		return fraultPhoneNumberMapper.selectPage(number);
	}

}
