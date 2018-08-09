package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ApplicationServerService;
import com.zbensoft.e.payment.db.domain.ApplicationServer;
import com.zbensoft.e.payment.db.mapper.ApplicationServerMapper;

@Service
public class ApplicationServerServiceImpl implements ApplicationServerService {

	@Autowired
	ApplicationServerMapper applicationServerMapper;

	@Override
	public int deleteByPrimaryKey(String applicationServerCode) {
		return applicationServerMapper.deleteByPrimaryKey(applicationServerCode);
	}

	@Override
	public int insert(ApplicationServer record) {
		return applicationServerMapper.insert(record);
	}

	@Override
	public int insertSelective(ApplicationServer record) {
		return applicationServerMapper.insertSelective(record);
	}

	@Override
	public ApplicationServer selectByPrimaryKey(String applicationServerCode) {
		return applicationServerMapper.selectByPrimaryKey(applicationServerCode);
	}

	@Override
	public int updateByPrimaryKeySelective(ApplicationServer record) {
		return applicationServerMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ApplicationServer record) {
		return applicationServerMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ApplicationServer> selectPage(ApplicationServer record) {
		return applicationServerMapper.selectPage(record);
	}

	@Override
	public int count(ApplicationServer applicationServer) {
		return applicationServerMapper.count(applicationServer);
	}

	@Override
	public boolean isExist(ApplicationServer applicationServer) {
		return selectByName(applicationServer.getName()) !=null;
	}

	@Override
	public ApplicationServer selectByName(String name) {
		return applicationServerMapper.selectByName(name);
	}

}
