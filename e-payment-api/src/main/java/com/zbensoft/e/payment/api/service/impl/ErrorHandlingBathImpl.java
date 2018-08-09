package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ErrorHandlingService;
import com.zbensoft.e.payment.db.domain.ErrorHandling;
import com.zbensoft.e.payment.db.mapper.ErrorHandlingMapper;

@Service
public class ErrorHandlingBathImpl implements ErrorHandlingService {

	@Autowired
	ErrorHandlingMapper errorHandlingMapper;

	@Override
	public int deleteByPrimaryKey(String errorHandlingId) {

		return errorHandlingMapper.deleteByPrimaryKey(errorHandlingId);
	}

	@Override
	public int insert(ErrorHandling record) {

		return errorHandlingMapper.insert(record);
	}

	@Override
	public int insertSelective(ErrorHandling record) {

		return errorHandlingMapper.insertSelective(record);
	}

	@Override
	public ErrorHandling selectByPrimaryKey(String errorHandlingId) {

		return errorHandlingMapper.selectByPrimaryKey(errorHandlingId);
	}

	@Override
	public int updateByPrimaryKeySelective(ErrorHandling record) {

		return errorHandlingMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ErrorHandling record) {

		return errorHandlingMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ErrorHandling> selectPage(ErrorHandling record) {
		return errorHandlingMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		errorHandlingMapper.deleteAll();
	}

	@Override
	public int count(ErrorHandling errorHandling) {
		return errorHandlingMapper.count(errorHandling);
	}

	@Override
	public void deleteByErrorHandling(ErrorHandling errorHandling) {
		errorHandlingMapper.deleteByErrorHandling(errorHandling);
	}

}
