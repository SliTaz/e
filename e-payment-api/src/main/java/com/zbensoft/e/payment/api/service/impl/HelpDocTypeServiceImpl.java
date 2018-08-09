package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.HelpDocTypeService;
import com.zbensoft.e.payment.db.domain.ConsumptionType;
import com.zbensoft.e.payment.db.domain.HelpDocType;
import com.zbensoft.e.payment.db.mapper.HelpDocTypeMapper;
@Service
public class HelpDocTypeServiceImpl implements HelpDocTypeService {
	
	@Autowired
	HelpDocTypeMapper helpDocTypeMapper;

	@Override
	public int deleteByPrimaryKey(String helpDocTypeid) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.deleteByPrimaryKey(helpDocTypeid);
	}

	@Override
	public int insert(HelpDocType record) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.insert(record);
	}

	@Override
	public int insertSelective(HelpDocType record) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.insertSelective(record);
	}

	@Override
	public HelpDocType selectByPrimaryKey(String helpDocTypeid) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.selectByPrimaryKey(helpDocTypeid);
	}

	@Override
	public int updateByPrimaryKeySelective(HelpDocType record) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public int updateByPrimaryKey(HelpDocType record) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.updateByPrimaryKey(record);
	}

	@Override
	public HelpDocType findByName(String name) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.findByName(name);
	}

	@Override
	public int deleteAll() {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.deleteAll();
	}

	@Override
	public List<HelpDocType> selectPage(HelpDocType record) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.selectPage(record);
	}

	@Override
	public int count(HelpDocType helpDocType) {
		// TODO Auto-generated method stub
		return helpDocTypeMapper.count(helpDocType);
	}
	
	@Override
	public boolean isHelpDocTypeExist(HelpDocType helpDocType) {
		return findByName(helpDocType.getTypeName()) != null;
	}

}
