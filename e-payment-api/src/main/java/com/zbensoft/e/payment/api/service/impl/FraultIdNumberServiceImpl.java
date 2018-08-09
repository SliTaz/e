package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultIdNumberService;
import com.zbensoft.e.payment.db.domain.FraultIdNumber;
import com.zbensoft.e.payment.db.mapper.FraultIdNumberMapper;
@Service
public class FraultIdNumberServiceImpl implements FraultIdNumberService {
@Autowired
FraultIdNumberMapper fraultIdNumberMapper;

	@Override
	public int deleteByPrimaryKey(String fraultIdNumberId) {
		return fraultIdNumberMapper.deleteByPrimaryKey(fraultIdNumberId);
	}

	@Override
	public int insert(FraultIdNumber record) {
		
		return fraultIdNumberMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultIdNumber record) {
		
		return fraultIdNumberMapper.insertSelective(record);
	}

	@Override
	public FraultIdNumber selectByPrimaryKey(String fraultIdNumberId) {
		
		return fraultIdNumberMapper.selectByPrimaryKey(fraultIdNumberId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultIdNumber record) {
		
		return fraultIdNumberMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultIdNumber record) {
		
		return fraultIdNumberMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		
		return fraultIdNumberMapper.deleteAll();
	}

	@Override
	public int count(FraultIdNumber addr) {
		
		return fraultIdNumberMapper.count(addr);
	}

	@Override
	public List<FraultIdNumber> selectPage(FraultIdNumber record) {
		
		return fraultIdNumberMapper.selectPage(record);
	}

}
