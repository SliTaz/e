package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultTypeService;
import com.zbensoft.e.payment.db.domain.FraultType;
import com.zbensoft.e.payment.db.mapper.FraultTypeMapper;

@Service
public class FraultTypeServiceImpl implements FraultTypeService {
	@Autowired
	FraultTypeMapper fraultTypeMapper;

	@Override
	public int deleteByPrimaryKey(String fraultTypeId) {

		return fraultTypeMapper.deleteByPrimaryKey(fraultTypeId);
	}

	@Override
	public int insert(FraultType record) {

		return fraultTypeMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultType record) {

		return fraultTypeMapper.insertSelective(record);
	}

	@Override
	public FraultType selectByPrimaryKey(String fraultTypeId) {

		return fraultTypeMapper.selectByPrimaryKey(fraultTypeId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultType record) {

		return fraultTypeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultType record) {

		return fraultTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<FraultType> selectPage(FraultType record) {
		return fraultTypeMapper.selectPage(record);
	}

	@Override
	public boolean isFraultTypeExist(FraultType fraultType) {
		return findByName(fraultType.getName()) != null;
	}

	private FraultType findByName(String name) {
		return fraultTypeMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		fraultTypeMapper.deleteAll();

	}

	@Override
	public int count(FraultType fraultType) {
		return fraultTypeMapper.count(fraultType);
	}
}
