package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.GovUserLoginHisService;
import com.zbensoft.e.payment.db.domain.GovUserLoginHis;
import com.zbensoft.e.payment.db.mapper.GovUserLoginHisMapper;
@Service
public class GovUserLoginHisServiceImpl implements GovUserLoginHisService{
	@Autowired
	GovUserLoginHisMapper govUserLoginHisMapper;
	
	@Override
	public int deleteByPrimaryKey(String consumerUserLoginHisId) {
		return govUserLoginHisMapper.deleteByPrimaryKey(consumerUserLoginHisId);
	}

	@Override
	public int insert(GovUserLoginHis record) {
		return govUserLoginHisMapper.insert(record);
	}

	@Override
	public int insertSelective(GovUserLoginHis record) {
		return govUserLoginHisMapper.insertSelective(record);
	}

	@Override
	public GovUserLoginHis selectByPrimaryKey(String consumerUserLoginHisId) {
		return govUserLoginHisMapper.selectByPrimaryKey(consumerUserLoginHisId);
	}

	@Override
	public int updateByPrimaryKeySelective(GovUserLoginHis record) {
		return govUserLoginHisMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(GovUserLoginHis record) {
		return govUserLoginHisMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<GovUserLoginHis> selectPage(GovUserLoginHis govUserLoginHis) {
		return govUserLoginHisMapper.selectPage(govUserLoginHis);
	}

	@Override
	public int count(GovUserLoginHis govUserLoginHis) {
		return govUserLoginHisMapper.count(govUserLoginHis);
	}

	@Override
	public void deleteAll() {
		govUserLoginHisMapper.deleteAll();
		
	}
	
	
}