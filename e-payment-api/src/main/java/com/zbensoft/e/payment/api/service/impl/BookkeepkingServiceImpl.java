package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.BookkeepkingService;
import com.zbensoft.e.payment.api.service.api.CaptionAccountService;
import com.zbensoft.e.payment.api.service.api.DailyBillService;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.db.domain.Bookkeepking;
import com.zbensoft.e.payment.db.domain.CaptionAccount;
import com.zbensoft.e.payment.db.domain.DailyBill;
import com.zbensoft.e.payment.db.domain.TradeInfo;
import com.zbensoft.e.payment.db.mapper.BookkeepkingMapper;
import com.zbensoft.e.payment.db.mapper.CaptionAccountMapper;
import com.zbensoft.e.payment.db.mapper.DailyBillMapper;
import com.zbensoft.e.payment.db.mapper.TradeInfoMapper;

@Service
public class BookkeepkingServiceImpl implements BookkeepkingService {
	
	@Autowired
	BookkeepkingMapper bookkeepkingMapper;

	@Override
	public int deleteByPrimaryKey(String bookkeepkingSeq) {
		return bookkeepkingMapper.deleteByPrimaryKey(bookkeepkingSeq);
	}

	@Override
	public int insert(Bookkeepking record) {
		return bookkeepkingMapper.insert(record);
	}

	@Override
	public int insertSelective(Bookkeepking record) {
		return bookkeepkingMapper.insertSelective(record);
	}

	@Override
	public Bookkeepking selectByPrimaryKey(String bookkeepkingSeq) {
		return bookkeepkingMapper.selectByPrimaryKey(bookkeepkingSeq);
	}

	@Override
	public int updateByPrimaryKeySelective(Bookkeepking record) {
		return bookkeepkingMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(Bookkeepking record) {
		return bookkeepkingMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<Bookkeepking> selectPage(Bookkeepking record) {
		return bookkeepkingMapper.selectPage(record);
	}

	@Override
	public boolean isExist(Bookkeepking bookkeepking) {
		return false;
	}

	@Override
	public void deleteAll() {
		bookkeepkingMapper.deleteAll();
	}
	
	@Override
	public int count(Bookkeepking bookkeepking) {
		return bookkeepkingMapper.count(bookkeepking);
	}

	

}
