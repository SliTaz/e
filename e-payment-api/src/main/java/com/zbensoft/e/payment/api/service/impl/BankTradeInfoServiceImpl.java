package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.service.api.BankTradeInfoService;
import com.zbensoft.e.payment.db.domain.BankTradeInfo;
import com.zbensoft.e.payment.db.mapper.BankTradeInfoMapper;

@Service
public class BankTradeInfoServiceImpl implements BankTradeInfoService {

	@Autowired
	BankTradeInfoMapper bankTradeInfoMapper;
	@Autowired
	SqlSessionTemplate sqlSessionTemplate;

	@Override
	public int deleteByPrimaryKey(String refNo) {
		return bankTradeInfoMapper.deleteByPrimaryKey(refNo);
	}

	@Override
	public int insert(BankTradeInfo record) {
		return bankTradeInfoMapper.insert(record);
	}

	@Override
	public int insertSelective(BankTradeInfo record) {
		return bankTradeInfoMapper.insertSelective(record);
	}

	@Override
	public BankTradeInfo selectByPrimaryKey(String bankId, String refNo) {
		BankTradeInfo bankTradeInfo = new BankTradeInfo();
		bankTradeInfo.setBankId(bankId);
		bankTradeInfo.setRefNo(refNo);
		return bankTradeInfoMapper.selectByPrimaryKey(bankTradeInfo);
	}

	@Override
	public int updateByPrimaryKeySelective(BankTradeInfo record) {
		return bankTradeInfoMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(BankTradeInfo record) {
		return bankTradeInfoMapper.updateByPrimaryKey(record);
	}

	@Override
	public int insertBatch(List<BankTradeInfo> recordList) throws Exception {
		if (recordList != null && recordList.size() > 0) {
			SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
			try {
				BankTradeInfoMapper baseMapper = session.getMapper(BankTradeInfoMapper.class);
				for (int index = 0; index < recordList.size(); index++) {
					BankTradeInfo object = recordList.get(index);
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

			return recordList.size();
		} else {
			return 0;
		}
	}

	@Override
	public void deleteAll(String bankId) {
		BankTradeInfo bankTradeInfo = new BankTradeInfo();
		bankTradeInfo.setBankId(bankId);
		bankTradeInfoMapper.deleteAll(bankTradeInfo);
	}

}
