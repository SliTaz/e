package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumptionTypeService;
import com.zbensoft.e.payment.db.domain.ConsumptionType;
import com.zbensoft.e.payment.db.mapper.ConsumptionTypeMapper;

@Service
public class ConsumptionTypeServiceImpl implements ConsumptionTypeService {

	@Autowired
	ConsumptionTypeMapper consumptionTypeMapper;

	@Override
	public int deleteByPrimaryKey(String billConsumptionTypeId) {

		return consumptionTypeMapper.deleteByPrimaryKey(billConsumptionTypeId);
	}

	@Override
	public int insert(ConsumptionType record) {

		return consumptionTypeMapper.insert(record);
	}

	@Override
	public int count(ConsumptionType consumptionType) {

		return consumptionTypeMapper.count(consumptionType);
	}

	@Override
	public int insertSelective(ConsumptionType record) {

		return consumptionTypeMapper.insertSelective(record);
	}

	@Override
	public ConsumptionType selectByPrimaryKey(String billConsumptionTypeId) {

		return consumptionTypeMapper.selectByPrimaryKey(billConsumptionTypeId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumptionType record) {

		return consumptionTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumptionType record) {

		return consumptionTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumptionType> selectPage(ConsumptionType record) {
		return consumptionTypeMapper.selectPage(record);
	}

	@Override
	public boolean isConsumptionTypeExist(ConsumptionType consumptionType) {
		return findByName(consumptionType.getName()) != null;
	}

	private ConsumptionType findByName(String name) {
		return consumptionTypeMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		consumptionTypeMapper.deleteAll();
	}

}
