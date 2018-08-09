package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.FraultIpAddrService;
import com.zbensoft.e.payment.db.domain.FraultIpAddr;
import com.zbensoft.e.payment.db.mapper.FraultIpAddrMapper;
@Service
public class FraultIpAddrServiceImpl implements FraultIpAddrService {
@Autowired
  FraultIpAddrMapper fraultIpAddrMapper;
	@Override
	public int deleteByPrimaryKey(FraultIpAddr fraultIpAddrId) {
		return fraultIpAddrMapper.deleteByPrimaryKey(fraultIpAddrId);
	}

	@Override
	public int insert(FraultIpAddr record) {
		return fraultIpAddrMapper.insert(record);
	}

	@Override
	public int insertSelective(FraultIpAddr record) {
		return fraultIpAddrMapper.insertSelective(record);
	}

	@Override
	public FraultIpAddr selectByPrimaryKey(FraultIpAddr fraultIpAddrId) {
		return fraultIpAddrMapper.selectByPrimaryKey(fraultIpAddrId);
	}

	@Override
	public int updateByPrimaryKeySelective(FraultIpAddr record) {
		return fraultIpAddrMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(FraultIpAddr record) {
		return fraultIpAddrMapper.updateByPrimaryKey(record);
	}

	@Override
	public int deleteAll() {
		return fraultIpAddrMapper.deleteAll();
	}

	@Override
	public int count(FraultIpAddr addr) {
		return fraultIpAddrMapper.count(addr);
	}

	@Override
	public List<FraultIpAddr> selectPage(FraultIpAddr record) {
		return fraultIpAddrMapper.selectPage(record);
	}

}
