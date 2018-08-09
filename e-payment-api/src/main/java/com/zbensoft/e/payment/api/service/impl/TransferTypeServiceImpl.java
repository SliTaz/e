package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.TransferTypeService;
import com.zbensoft.e.payment.db.domain.TransferType;
import com.zbensoft.e.payment.db.mapper.TransferTypeMapper;

@Service
public class TransferTypeServiceImpl implements TransferTypeService {

	@Autowired
	TransferTypeMapper transferTypeMapper;

	@Override
	public int deleteByPrimaryKey(String transferTypeId) {

		return transferTypeMapper.deleteByPrimaryKey(transferTypeId);
	}

	@Override
	public int insert(TransferType record) {

		return transferTypeMapper.insert(record);
	}

	@Override
	public int count(TransferType transferType) {

		return transferTypeMapper.count(transferType);
	}

	@Override
	public int insertSelective(TransferType record) {

		return transferTypeMapper.insertSelective(record);
	}

	@Override
	public TransferType selectByPrimaryKey(String billTransferTypeId) {

		return transferTypeMapper.selectByPrimaryKey(billTransferTypeId);
	}

	@Override
	public int updateByPrimaryKeySelective(TransferType record) {

		return transferTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(TransferType record) {

		return transferTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<TransferType> selectPage(TransferType record) {
		return transferTypeMapper.selectPage(record);
	}

	@Override
	public boolean isTransferTypeExist(TransferType transferType) {
		return findByName(transferType.getName()) != null;
	}

	private TransferType findByName(String name) {
		return transferTypeMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		transferTypeMapper.deleteAll();
	}

}
