package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.ProfitStatementService;
import com.zbensoft.e.payment.db.domain.ProfitStatement;
import com.zbensoft.e.payment.db.mapper.ProfitStatementMapper;
@Service
public class ProfitStatementServiceImpl implements ProfitStatementService {
	@Autowired
	ProfitStatementMapper profitStatementMapper;
	@Override
	public int deleteByPrimaryKey(String statisticsTime) {
		return profitStatementMapper.deleteByPrimaryKey(statisticsTime);
	}

	@Override
	public int insert(ProfitStatement record) {
		return profitStatementMapper.insert(record);
	}

	@Override
	public int insertSelective(ProfitStatement record) {
		return profitStatementMapper.insertSelective(record);
	}

	@Override
	public ProfitStatement selectByPrimaryKey(String statisticsTime) {
		return profitStatementMapper.selectByPrimaryKey(statisticsTime);
	}

	@Override
	public int updateByPrimaryKeySelective(ProfitStatement record) {
		return profitStatementMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ProfitStatement record) {
		return profitStatementMapper.updateByPrimaryKey(record);
	}

	@Override
	public int count(ProfitStatement profitStatement) {
		return profitStatementMapper.count(profitStatement);
	}

	@Override
	public List<ProfitStatement> selectPage(ProfitStatement record) {
		return profitStatementMapper.selectPage(record);
	}

}
