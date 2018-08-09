package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.NoticeService;
import com.zbensoft.e.payment.db.domain.Notice;
import com.zbensoft.e.payment.db.mapper.NoticeMapper;

@Service
public class NoticeServiceImpl implements NoticeService {

	@Autowired
	NoticeMapper noticeMapper;

	@Override
	public int deleteByPrimaryKey(String noticeId) {
		return noticeMapper.deleteByPrimaryKey(noticeId);
	}

	@Override
	public int insert(Notice record) {
		return noticeMapper.insert(record);
	}

	@Override
	public int insertSelective(Notice record) {
		return noticeMapper.insertSelective(record);
	}

	@Override
	public Notice selectByPrimaryKey(String noticeId) {
		return noticeMapper.selectByPrimaryKey(noticeId);
	}

	@Override
	public int updateByPrimaryKeySelective(Notice record) {
		return noticeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Notice record) {
		return noticeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Notice> selectPage(Notice record) {
		return noticeMapper.selectPage(record);
	}

	@Override
	public void deleteAll() {
		noticeMapper.deleteAll();
	}

	@Override
	public int count(Notice notice) {
		return noticeMapper.count(notice);
	}

	@Override
	public List<Notice> selectNewestRecords(Notice notice) {
		return noticeMapper.selectNewestRecords(notice);
	}

}
