package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ConsumerFamilyService;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyMapper;

@Service
public class ConsumerFamilyServiceImpl implements ConsumerFamilyService {
	@Autowired
	ConsumerFamilyMapper consumerFamilyMapper;

	@Override
	public int deleteByPrimaryKey(String familyId) {
		
		return consumerFamilyMapper.deleteByPrimaryKey(familyId);
	}

	@Override
	public int insert(ConsumerFamily record) {
		
		return consumerFamilyMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerFamily record) {
		
		return consumerFamilyMapper.insertSelective(record);
	}

	@Override
	public ConsumerFamily selectByPrimaryKey(String familyId) {
		
		return consumerFamilyMapper.selectByPrimaryKey(familyId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerFamily record) {
		
		return consumerFamilyMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerFamily record) {
		
		return consumerFamilyMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerFamily> selectPage(ConsumerFamily record) {
		return consumerFamilyMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		consumerFamilyMapper.deleteAll();
	}

	@Override
	public int count(ConsumerFamily consumerFamily) {
		return consumerFamilyMapper.count(consumerFamily);
	}
	
	@Override
	public boolean isConsumerFamilyExist(ConsumerFamily consumerFamily) {
		return findByName(consumerFamily.getName()) != null;
	}

	private ConsumerFamily findByName(String name) {
		return consumerFamilyMapper.findByName(name);
	}

}
