package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.service.api.BankChargeInfoService;
import com.zbensoft.e.payment.db.domain.BankChargeInfo;
import com.zbensoft.e.payment.db.domain.BankTradeInfo;
import com.zbensoft.e.payment.db.mapper.BankChargeInfoMapper;
import com.zbensoft.e.payment.db.mapper.BankTradeInfoMapper;

@Service
public class BankChargeInfoServiceImpl implements BankChargeInfoService {
	@Autowired
	BankChargeInfoMapper bankChargeInfoMapper;
	
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;
	
	
	@Override
	public int deleteByPrimaryKey(String refNo) {
		return bankChargeInfoMapper.deleteByPrimaryKey(refNo);
	}

	@Override
	public int insert(com.zbensoft.e.payment.db.domain.BankChargeInfo record) {
		return bankChargeInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(com.zbensoft.e.payment.db.domain.BankChargeInfo record) {
		return bankChargeInfoMapper.insertSelective(record);
	}

	@Override
	public com.zbensoft.e.payment.db.domain.BankChargeInfo selectByPrimaryKey(String refNo) {
		return bankChargeInfoMapper.selectByPrimaryKey(refNo);
	}

	@Override
	public int updateByPrimaryKeySelective(com.zbensoft.e.payment.db.domain.BankChargeInfo record) {
		return bankChargeInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(com.zbensoft.e.payment.db.domain.BankChargeInfo record) {
		return bankChargeInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public void deleteAll(String bankId) {
		//TODO 暂时没有分表，不做处理
	}

	@Override
	public void deleteAll() {
		bankChargeInfoMapper.deleteAll();
	}

	@Override
	public int insertBatch(List<BankChargeInfo> bankChargeInfoList) {

		if (bankChargeInfoList != null && bankChargeInfoList.size() > 0) {
			SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
			try {
				BankChargeInfoMapper baseMapper = session.getMapper(BankChargeInfoMapper.class);
				for (int index = 0; index < bankChargeInfoList.size(); index++) {
					BankChargeInfo object = bankChargeInfoList.get(index);
					baseMapper.insert(object);
				}
				session.commit();
			} catch (Exception e) {
				session.rollback();
				throw e;
			} finally {
				if (session != null) {
					session.close();
				}
			}

			return bankChargeInfoList.size();
		} else {
			return 0;
		}
	
		
	}

	@Override
	public List<BankChargeInfo> selectPage(BankChargeInfo record) {
		return bankChargeInfoMapper.selectPage(record);
	}

	@Override
	public int count(BankChargeInfo bankChargeInfo) {
		return bankChargeInfoMapper.count(bankChargeInfo);
	}

}