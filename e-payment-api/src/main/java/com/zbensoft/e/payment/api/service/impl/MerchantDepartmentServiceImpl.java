package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.MerchantDepartmentService;
import com.zbensoft.e.payment.db.domain.MerchantDepartment;
import com.zbensoft.e.payment.db.mapper.MerchantDepartmentMapper;

@Service
public class MerchantDepartmentServiceImpl implements MerchantDepartmentService {
	@Autowired
	MerchantDepartmentMapper merchantDepartmentMapper;

	@Override
	public int deleteByPrimaryKey(String merchantDepartmentId) {
		
		return merchantDepartmentMapper.deleteByPrimaryKey(merchantDepartmentId);
	}

	@Override
	public int insert(MerchantDepartment record) {
		
		return merchantDepartmentMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantDepartment record) {
		
		return merchantDepartmentMapper.insertSelective(record);
	}

	@Override
	public MerchantDepartment selectByPrimaryKey(String merchantDepartmentId) {
		
		return merchantDepartmentMapper.selectByPrimaryKey(merchantDepartmentId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantDepartment record) {
		
		return merchantDepartmentMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantDepartment record) {
		
		return merchantDepartmentMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantDepartment> selectPage(MerchantDepartment record) {
		return merchantDepartmentMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		merchantDepartmentMapper.deleteAll();
	}

	@Override
	public int count(MerchantDepartment merchantDepartment) {
		return merchantDepartmentMapper.count(merchantDepartment);
	}
	
}
