package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.GoodsUnitService;
import com.zbensoft.e.payment.db.domain.GoodsUnit;
import com.zbensoft.e.payment.db.mapper.GoodsUnitMapper;

@Service
public class GoodsUnitServiceImpl implements GoodsUnitService {
	@Autowired
	GoodsUnitMapper goodsUnitMapper;

	@Override
	public int deleteByPrimaryKey(String goodUnitId) {

		return goodsUnitMapper.deleteByPrimaryKey(goodUnitId);
	}

	@Override
	public int insert(GoodsUnit record) {

		return goodsUnitMapper.insert(record);
	}

	@Override
	public int insertSelective(GoodsUnit record) {

		return goodsUnitMapper.insertSelective(record);
	}

	@Override
	public GoodsUnit selectByPrimaryKey(String goodUnitId) {

		return goodsUnitMapper.selectByPrimaryKey(goodUnitId);
	}

	@Override
	public int updateByPrimaryKeySelective(GoodsUnit record) {

		return goodsUnitMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(GoodsUnit record) {

		return goodsUnitMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<GoodsUnit> selectPage(GoodsUnit record) {
		return goodsUnitMapper.selectPage(record);
	}

	@Override
	public boolean isGoodsUnitExist(GoodsUnit goodsUnit) {
		return findByName(goodsUnit.getName()) != null;
	}

	private GoodsUnit findByName(String name) {
		return goodsUnitMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		goodsUnitMapper.deleteAll();

	}

	@Override
	public int count(GoodsUnit goodsUnit) {
		return goodsUnitMapper.count(goodsUnit);
	}
}
