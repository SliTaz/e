package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.AppService;
import com.zbensoft.e.payment.db.domain.App;
import com.zbensoft.e.payment.db.mapper.AppMapper;

@Service
public class AppServiceImpl implements AppService {

	@Autowired
	AppMapper appMapper;

	@Override
	public int deleteByPrimaryKey(String appId) {

		return appMapper.deleteByPrimaryKey(appId);
	}

	@Override
	public int insert(App record) {

		return appMapper.insert(record);
	}

	@Override
	public int count(App app) {

		return appMapper.count(app);
	}

	@Override
	public int insertSelective(App record) {

		return appMapper.insertSelective(record);
	}

	@Override
	public App selectByPrimaryKey(String appId) {

		return appMapper.selectByPrimaryKey(appId);
	}

	@Override
	public int updateByPrimaryKeySelective(App record) {

		return appMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(App record) {

		return appMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<App> selectPage(App record) {
		return appMapper.selectPage(record);
	}

	@Override
	public boolean isAppExist(App app) {
		return findByName(app.getName()) != null;
	}

	private App findByName(String name) {
		return appMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		appMapper.deleteAll();
	}

}
