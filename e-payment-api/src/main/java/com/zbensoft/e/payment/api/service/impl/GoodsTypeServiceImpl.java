package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.GoodsTypeService;
import com.zbensoft.e.payment.db.domain.GoodsType;
import com.zbensoft.e.payment.db.mapper.GoodsTypeMapper;

@Service
public class GoodsTypeServiceImpl implements GoodsTypeService {
	@Autowired
	GoodsTypeMapper goodsTypeMapper;

	@Override
	public int deleteByPrimaryKey(String bankId) {

		return goodsTypeMapper.deleteByPrimaryKey(bankId);
	}

	@Override
	public int insert(GoodsType record) {

		return goodsTypeMapper.insert(record);
	}

	@Override
	public int insertSelective(GoodsType record) {

		return goodsTypeMapper.insertSelective(record);
	}

	@Override
	public GoodsType selectByPrimaryKey(String bankId) {

		return goodsTypeMapper.selectByPrimaryKey(bankId);
	}

	@Override
	public int updateByPrimaryKeySelective(GoodsType record) {

		return goodsTypeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(GoodsType record) {

		return goodsTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<GoodsType> selectPage(GoodsType record) {
		return goodsTypeMapper.selectPage(record);
	}

	@Override
	public boolean isGoodsTypeExist(GoodsType goodsType) {
		return findByName(goodsType.getName()) != null;
	}

	private GoodsType findByName(String name) {
		return goodsTypeMapper.findByName(name);
	}

	@Override
	public void deleteAll() {
		goodsTypeMapper.deleteAll();

	}

	@Override
	public int count(GoodsType goodsType) {
		return goodsTypeMapper.count(goodsType);
	}

	@Override
	public List<GoodsType> findAll() {
		return goodsTypeMapper.findAll();
	}
}
