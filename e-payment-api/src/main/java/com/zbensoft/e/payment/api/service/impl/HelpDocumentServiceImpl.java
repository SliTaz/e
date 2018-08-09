package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.HelpDocumentService;
import com.zbensoft.e.payment.db.domain.HelpDocument;
import com.zbensoft.e.payment.db.mapper.HelpDocumentMapper;

@Service
public class HelpDocumentServiceImpl implements HelpDocumentService {
	
	@Autowired
	HelpDocumentMapper helpDocumentMapper;

	@Override
	public int deleteByPrimaryKey(String documentId) {
		return helpDocumentMapper.deleteByPrimaryKey(documentId);
	}

	@Override
	public int insert(HelpDocument record) {
		return helpDocumentMapper.insert(record);
	}

	@Override
	public int insertSelective(HelpDocument record) {
		return helpDocumentMapper.insertSelective(record);
	}

	@Override
	public HelpDocument selectByPrimaryKey(String documentId) {
		return helpDocumentMapper.selectByPrimaryKey(documentId);
	}

	@Override
	public int updateByPrimaryKeySelective(HelpDocument record) {
		return helpDocumentMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(HelpDocument record) {
		return helpDocumentMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<HelpDocument> selectPage(HelpDocument record) {
		return helpDocumentMapper.selectPage(record);
	}

	@Override
	public boolean isExist(HelpDocument helpDocument) {
		return false;
	}

	
	@Override
	public int count(HelpDocument helpDocument) {
		return helpDocumentMapper.count(helpDocument);
	}

	

}
