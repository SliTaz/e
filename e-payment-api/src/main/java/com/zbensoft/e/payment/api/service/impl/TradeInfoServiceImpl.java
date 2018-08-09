package com.zbensoft.e.payment.api.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.service.api.TradeInfoService;
import com.zbensoft.e.payment.db.domain.ConsumerCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerTradeKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MerchantTrade;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.TradeInfo;
import com.zbensoft.e.payment.db.mapper.ConsumerTradeMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerUserMapper;
import com.zbensoft.e.payment.db.mapper.MerchantTradeMapper;
import com.zbensoft.e.payment.db.mapper.MerchantUserMapper;
import com.zbensoft.e.payment.db.mapper.TradeInfoMapper;

@Service
public class TradeInfoServiceImpl implements TradeInfoService {

	@Autowired
	TradeInfoMapper tradeInfoMapper;
	@Autowired
	ConsumerTradeMapper consumerTradeMapper;
	@Autowired
	MerchantTradeMapper merchantTradeMapper;
	@Autowired
	MerchantUserMapper merchantUserMapper;
	@Autowired
	ConsumerUserMapper consumerUserMapper;
	
	

	@Override
	@Transactional(value = "DataSourceManager")
	public int deleteByPrimaryKey(String tradeSeq) {
		TradeInfo record = selectByPrimaryKey(tradeSeq);
		if (record == null) {
			return 0;
		}
		int rInt = tradeInfoMapper.deleteByPrimaryKey(tradeSeq);// update
		if (rInt == 1) {

			if (record.getPayUserId().equalsIgnoreCase(record.getRecvUserId())) {
				deleteUserTrade(record.getTradeSeq(), record.getPayUserId());// update
			} else {
				deleteUserTrade(record.getTradeSeq(), record.getPayUserId());
				deleteUserTrade(record.getTradeSeq(), record.getRecvUserId());
			}
		}
		return rInt;
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public int insert(TradeInfo record) {
		int rInt = tradeInfoMapper.insert(record);
		if (rInt == 1) {
			if (record.getPayUserId().equalsIgnoreCase(record.getRecvUserId())) {
				insertUserTrade(record, record.getPayUserId(), record.getPayEmployeeUserId());
			} else {
				insertUserTrade(record, record.getPayUserId(), record.getPayEmployeeUserId());
				insertUserTrade(record, record.getRecvUserId(), record.getRecvEmployeeUserId());
			}
		}
		return rInt;
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public int insertSelective(TradeInfo record) {
		int rInt = tradeInfoMapper.insertSelective(record);
		if (rInt == 1) {
			if (record.getPayUserId().equalsIgnoreCase(record.getRecvUserId())) {
				insertSelectiveUserTrade(record, record.getPayUserId(), record.getPayEmployeeUserId());
			} else {
				insertSelectiveUserTrade(record, record.getPayUserId(), record.getPayEmployeeUserId());
				insertSelectiveUserTrade(record, record.getRecvUserId(), record.getRecvEmployeeUserId());
			}
		}
		return rInt;
	}

	@Override
	public TradeInfo selectByPrimaryKey(String tradeSeq) {
		return tradeInfoMapper.selectByPrimaryKey(tradeSeq);
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public int updateByPrimaryKeySelective(TradeInfo record) {
		int rInt = tradeInfoMapper.updateByPrimaryKeySelective(record);
		if (rInt == 1) {
			if(record.getPayUserId()!=null){
				if (record.getPayUserId().equalsIgnoreCase(record.getRecvUserId())) {
					updateByPrimaryKeySelectiveUserTrade(record, record.getPayUserId(), record.getPayEmployeeUserId());
				} else {
					updateByPrimaryKeySelectiveUserTrade(record, record.getPayUserId(), record.getPayEmployeeUserId());
					updateByPrimaryKeySelectiveUserTrade(record, record.getRecvUserId(), record.getRecvEmployeeUserId());
				}
			}
		}
		return rInt;
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public int updateByPrimaryKey(TradeInfo record) {
		int rInt = tradeInfoMapper.updateByPrimaryKey(record);
		if (rInt == 1) {
			if (record.getDeleteFlag() != null) {
				if (record.getPayUserId().equalsIgnoreCase(record.getRecvUserId())) {
					updateUserTrade(record, record.getPayUserId(), record.getPayEmployeeUserId());
				} else {
					updateUserTrade(record, record.getPayUserId(), record.getPayEmployeeUserId());
					updateUserTrade(record, record.getRecvUserId(), record.getRecvEmployeeUserId());
				}
			}
		}
		return rInt;
	}

	@Override
	public List<TradeInfo> selectPage(TradeInfo record) {
		// return selectPageUserTrade(record, record.getPayUserId());
		return tradeInfoMapper.selectPage(record);
	}

	@Override
	public int count(TradeInfo tradeInfo) {
		// return countByUserTrade(tradeInfo, tradeInfo.getPayUserId());
		return tradeInfoMapper.count(tradeInfo);
	}

	@Override
	public List<TradeInfo> selectPageByUser(TradeInfo tradeInfo) {
		return selectPageUserTrade(tradeInfo, tradeInfo.getPayUserId(), tradeInfo.getRecvEmployeeUserId());
		// return tradeInfoMapper.selectPageByUser(tradeInfo);
	}

	@Override
	public int countByUser(TradeInfo tradeInfo) {
		return countByUserTrade(tradeInfo, tradeInfo.getPayUserId(), tradeInfo.getPayEmployeeUserId());
		// return tradeInfoMapper.countByUser(tradeInfo);
	}

	// update为已删除
	@Override
	@Transactional(value = "DataSourceManager")
	public void deleteByTradeInfo(String tradeSeq, String userId) {
		TradeInfo recordTmp = selectByPrimaryKey(tradeSeq);
		if (recordTmp == null) {
			return;
		}
		if (StringUtils.isEmpty(userId)) {
			tradeInfoMapper.deleteByTradeInfo(recordTmp);
		} else {
			TradeInfo record = new TradeInfo();
			record.setTradeSeq(recordTmp.getTradeSeq());
			record.setDeleteFlag(1);
			if (userId.equals(recordTmp.getPayUserId())) {
				updateByPrimaryKeySelectiveUserTrade(record, recordTmp.getPayUserId(), null);
			} else if (userId.equals(recordTmp.getRecvUserId()) && !recordTmp.getPayUserId().equalsIgnoreCase(recordTmp.getRecvUserId())) {
				updateByPrimaryKeySelectiveUserTrade(record, recordTmp.getRecvUserId(), null);
			}
		}
	}

	// 还原
	@Override
	@Transactional(value = "DataSourceManager")
	public void restoreByPrimaryKey(String tradeSeq, String userId) {
		TradeInfo recordTmp = selectByPrimaryKey(tradeSeq);
		if (recordTmp == null) {
			return;
		}
		if (StringUtils.isEmpty(userId)) {
			tradeInfoMapper.restoreByPrimaryKey(tradeSeq);
		} else {
			TradeInfo record = new TradeInfo();
			record.setTradeSeq(recordTmp.getTradeSeq());
			record.setDeleteFlag(0);
			if (userId.equals(recordTmp.getPayUserId())) {
				updateByPrimaryKeySelectiveUserTrade(record, recordTmp.getPayUserId(), null);
			} else if (userId.equals(recordTmp.getRecvUserId()) && !recordTmp.getPayUserId().equalsIgnoreCase(recordTmp.getRecvUserId())) {
				updateByPrimaryKeySelectiveUserTrade(record, recordTmp.getRecvUserId(), null);
			}
		}
	}

	// 永久删除,记录不删除
	@Override
	@Transactional(value = "DataSourceManager")
	public void foreverDeleteByPrimaryKey(String tradeSeq, String userId) {
		if (StringUtils.isEmpty(userId)) {
			return;
		}
		TradeInfo record = selectByPrimaryKey(tradeSeq);
		if (record == null) {
			return;
		}
		// tradeInfoMapper.foreverDeleteByPrimaryKey(tradeSeq);

		if (userId.equals(record.getPayUserId())) {
			foreverDeleteUserTrade(record.getTradeSeq(), record.getPayUserId());
		} else if (userId.equals(record.getRecvUserId()) && !record.getPayUserId().equalsIgnoreCase(record.getRecvUserId())) {
			foreverDeleteUserTrade(record.getTradeSeq(), record.getRecvUserId());
		}
	}

	@Override
	public TradeInfo getTradInfoByTradeSeq(String tradeSeq) {
		return tradeInfoMapper.getTradInfoByTradeSeq(tradeSeq);
	}

	@Override
	public List<TradeInfo> selectbyOrderNoInDay(TradeInfo tradeInfo) {
		return tradeInfoMapper.selectbyOrderNoInDay(tradeInfo);
	}

	private List<TradeInfo> selectPageUserTrade(TradeInfo record, String userId, String employeeUserId) {
		List<TradeInfo> list = new ArrayList<>();
		List<ConsumerTradeKey> consumerTradeKeyList = null;
		List<MerchantTrade> merchantTradeList = null;
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey(record, userId);
			if (consumerTradeKey.getCreateTimeStartSer() == null || "".equals(consumerTradeKey.getCreateTimeStartSer())) {
				consumerTradeKeyList = consumerTradeMapper.selectPageTwo(consumerTradeKey);
			} else {
				consumerTradeKeyList = consumerTradeMapper.selectPage(consumerTradeKey);
			}
			
			if (consumerTradeKeyList != null && consumerTradeKeyList.size() > 0) {
				for (ConsumerTradeKey consumerTradeKey2 : consumerTradeKeyList) {
					list.add(tradeInfoMapper.selectByPrimaryKey(consumerTradeKey2.getTradeSeq()));
				}
			}
		} else {
			MerchantTrade merchantTrade = new MerchantTrade(record, userId, employeeUserId);
			if (merchantTrade.getCreateTimeStartSer() == null || "".equals(merchantTrade.getCreateTimeStartSer())) {
				merchantTradeList = merchantTradeMapper.selectPageTwo(merchantTrade);
			} else {
				merchantTradeList = merchantTradeMapper.selectPage(merchantTrade);
			}
			if (merchantTradeList != null && merchantTradeList.size() > 0) {
				for (MerchantTrade merchantTrade2 : merchantTradeList) {
					list.add(tradeInfoMapper.selectByPrimaryKey(merchantTrade2.getTradeSeq()));
				}
			}
		}
		return list;
	}

	private int countByUserTrade(TradeInfo record, String userId, String employeeUserId) {
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey(record, userId);
			if(consumerTradeKey.getCreateTimeStartSer()==null||"".equals(consumerTradeKey.getCreateTimeStartSer())){
				return consumerTradeMapper.countTwo(consumerTradeKey);
			}else{
				return consumerTradeMapper.count(consumerTradeKey);
			}
		} else {
			MerchantTrade merchantTrade = new MerchantTrade(record, userId, employeeUserId);
			if(merchantTrade.getCreateTimeStartSer()==null||"".equals(merchantTrade.getCreateTimeStartSer())){
				return merchantTradeMapper.countTwo(merchantTrade);
			}else{
				return merchantTradeMapper.count(merchantTrade);
			}
		}
	}

	private void insertUserTrade(TradeInfo record, String userId, String employeeUserId) {
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey(record, userId);
			consumerTradeMapper.insert(consumerTradeKey);
		} else {
			MerchantTrade merchantTrade = new MerchantTrade(record, userId, employeeUserId);
			merchantTradeMapper.insert(merchantTrade);
		}
	}

	private void insertSelectiveUserTrade(TradeInfo record, String userId, String employeeUserId) {
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey(record, userId);
			consumerTradeMapper.insertSelective(consumerTradeKey);
		} else {
			MerchantTrade merchantTrade = new MerchantTrade(record, userId, employeeUserId);
			merchantTradeMapper.insertSelective(merchantTrade);
		}
	}

	private void updateByPrimaryKeySelectiveUserTrade(TradeInfo record, String userId, String employeeUserId) {
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey(record, userId);
			consumerTradeMapper.updateByPrimaryKeySelective(consumerTradeKey);
		} else {
			MerchantTrade merchantTrade = new MerchantTrade(record, userId, employeeUserId);
			merchantTradeMapper.updateByPrimaryKeySelective(merchantTrade);
		}
	}

	private void updateUserTrade(TradeInfo record, String userId, String employeeUserId) {
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey(record, userId);
			consumerTradeMapper.updateByPrimaryKey(consumerTradeKey);
		} else {
			MerchantTrade merchantTrade = new MerchantTrade(record, userId, employeeUserId);
			merchantTradeMapper.updateByPrimaryKey(merchantTrade);
		}
	}

	private void deleteUserTrade(String tradeSeq, String userId) {
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey();
			consumerTradeKey.setTradeSeq(tradeSeq);
			consumerTradeKey.setUserId(userId);
			consumerTradeMapper.deleteByPrimaryKey(consumerTradeKey);
		} else {
			MerchantTrade merchantTrade = new MerchantTrade();
			merchantTrade.setTradeSeq(tradeSeq);
			merchantTrade.setUserId(userId);
			merchantTradeMapper.deleteByPrimaryKey(merchantTrade);
		}
	}

	private void foreverDeleteUserTrade(String tradeSeq, String userId) {
		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey();
			consumerTradeKey.setTradeSeq(tradeSeq);
			consumerTradeKey.setUserId(userId);
			consumerTradeMapper.foreverDeleteByPrimaryKey(consumerTradeKey);
		} else {
			MerchantTrade merchantTrade = new MerchantTrade();
			merchantTrade.setTradeSeq(tradeSeq);
			merchantTrade.setUserId(userId);
			merchantTradeMapper.foreverDeleteByPrimaryKey(merchantTrade);
		}
	}

	@Override
	public void remarkOrder(TradeInfo tradeInfo, String userId) {

		TradeInfo record = selectByPrimaryKey(tradeInfo.getTradeSeq());
		if (record == null) {
			return;
		}

		if (userId.startsWith(MessageDef.USER_TYPE.CONSUMER_STRING)) {
			ConsumerTradeKey consumerTradeKey = new ConsumerTradeKey(record, userId);
			consumerTradeKey.setRemark(tradeInfo.getRemark());
			consumerTradeMapper.updateByPrimaryKeySelective(consumerTradeKey);
		} else {
			MerchantTrade merchantTrade = new MerchantTrade(record, userId, tradeInfo.getPayEmployeeUserId());
			merchantTradeMapper.updateByPrimaryKeySelective(merchantTrade);
		}

	}

	@Override
	public List<TradeInfo> getTradInfoByParentTradeSeq(String tradeSeq) {
		List<TradeInfo> tradeInfoList = tradeInfoMapper.getTradInfoByParentTradeSeq(tradeSeq);
	    if(tradeInfoList.isEmpty()||tradeInfoList==null)
	    {
	    	return Collections.EMPTY_LIST;
	    }else
	    {
	    	return tradeInfoList;
	    }
	}

	@Override
	public Double sumByDay(TradeInfo tradeInfoSer) {
		return tradeInfoMapper.sumByDay(tradeInfoSer);
	}

	@Override
	public List<TradeInfo> selectByDayLmit(TradeInfo tradeInfoSer) {
		
		return tradeInfoMapper.selectByDayLmit(tradeInfoSer);
	}

	@Override
	public int countByDay(TradeInfo tradeInfoSer) {
		return tradeInfoMapper.countByDay(tradeInfoSer);
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void updateWithPayerSelective(TradeInfo upDateTradeInfo, MerchantUser updateMerchantUser,
			ConsumerUser updateConsumerUser) {
		if(updateMerchantUser!=null&&updateMerchantUser.getUserId()!=null){
			merchantUserMapper.updateByPrimaryKeySelective(updateMerchantUser);
		}
		if(updateConsumerUser!=null&&updateConsumerUser.getUserId()!=null){
			consumerUserMapper.updateByPrimaryKeySelective(updateConsumerUser);
		}
		
		tradeInfoMapper.updateByPrimaryKeySelective(upDateTradeInfo);
	}

	@Override
	public int limiteDelete(TradeInfo tradeInfo) {
		return tradeInfoMapper.limiteDelete(tradeInfo);
		
	}

	@Override
	public List<TradeInfo> selectbyOrderNoInDayForValidateExist(TradeInfo tradeInfo) {

		return tradeInfoMapper.selectbyOrderNoInDayForValidateExist(tradeInfo);
	}

}
