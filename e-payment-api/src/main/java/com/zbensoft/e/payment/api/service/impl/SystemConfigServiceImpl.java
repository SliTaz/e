package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.SystemConfigService;
import com.zbensoft.e.payment.db.domain.SystemConfig;
import com.zbensoft.e.payment.db.domain.SystemConfigKey;
import com.zbensoft.e.payment.db.mapper.SystemConfigMapper;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

	@Autowired
	SystemConfigMapper systemConfigMapper;

	@Override
	public int deleteByPrimaryKey(SystemConfigKey key) {
		return systemConfigMapper.deleteByPrimaryKey(key);
	}

	@Override
	public int insert(SystemConfig record) {
		return systemConfigMapper.insert(record);
	}

	@Override
	public int insertSelective(SystemConfig record) {
		return systemConfigMapper.insertSelective(record);
	}

	@Override
	public SystemConfig selectByPrimaryKey(SystemConfigKey key) {
		return systemConfigMapper.selectByPrimaryKey(key);
	}

	@Override
	public int updateByPrimaryKeySelective(SystemConfig record) {
		return systemConfigMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SystemConfig record) {
		return systemConfigMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SystemConfig> selectByApplicationServerCode(String applicationServerCode) {
		return systemConfigMapper.selectByApplicationServerCode(applicationServerCode);
	}

	@Override
	public List<SystemConfig> selectPage(SystemConfig record) {
		return systemConfigMapper.selectPage(record);
	}

	@Override
	public int count(SystemConfig systemConfig) {
		return systemConfigMapper.count(systemConfig);
	}

	@Override
	public boolean isExist(SystemConfig systemConfig) {
		return selectByApplicationServerCode(systemConfig.getApplicationServerCode()) !=null;
	}

}
