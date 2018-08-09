package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zbensoft.e.payment.api.service.api.ConsumerGroupService;
import com.zbensoft.e.payment.api.service.api.MerchantGroupService;
import com.zbensoft.e.payment.db.domain.ConsumerGroup;
import com.zbensoft.e.payment.db.domain.ConsumerGroupUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.MerchantGroup;
import com.zbensoft.e.payment.db.domain.MerchantGroupUserKey;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.mapper.ConsumerGroupMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerGroupUserMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerUserMapper;
import com.zbensoft.e.payment.db.mapper.MerchantGroupMapper;
import com.zbensoft.e.payment.db.mapper.MerchantGroupUserMapper;
import com.zbensoft.e.payment.db.mapper.MerchantUserMapper;

@Service
public class MerchantGroupServiceImpl implements MerchantGroupService {
	
	private static final Logger log = LoggerFactory.getLogger(MerchantGroupServiceImpl.class);

	@Autowired
	MerchantGroupMapper merchantGroupMapper;
	
	@Autowired
	MerchantUserMapper merchantUserMapper;
	
	@Autowired
	MerchantGroupUserMapper merchantGroupUserMapper;
	
	@Override
	public int deleteByPrimaryKey(String merchantGroupId) {
		return merchantGroupMapper.deleteByPrimaryKey(merchantGroupId);
	}

	@Override
	public int insert(MerchantGroup record) {
		return merchantGroupMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantGroup record) {
		return merchantGroupMapper.insertSelective(record);
	}

	@Override
	public MerchantGroup selectByPrimaryKey(String merchantGroupId) {
		return merchantGroupMapper.selectByPrimaryKey(merchantGroupId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantGroup record) {
		return merchantGroupMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantGroup record) {
		return merchantGroupMapper.updateByPrimaryKey(record);
	}

	@Override
	public int count(MerchantGroup merchantGroup) {
		return merchantGroupMapper.count(merchantGroup);
	}

	@Override
	public List<MerchantGroup> selectPage(MerchantGroup merchantGroup) {
		return merchantGroupMapper.selectPage(merchantGroup);
	}

	@Override
	public boolean isNameExist(MerchantGroup merchantGroup) {//可以相同
		return false;
	}

	@Override
	public void deleteAll() {
		merchantGroupMapper.deleteAll();
	}

	@Override
	@Transactional(value="DataSourceManager")
	public int saveOrUpdate(List<String> list, String merchantGroupId,boolean deleteFlagAll) {
		//System.out.println("deleteFlagAll:"+deleteFlagAll);
		int import_int=0;
		
		if(deleteFlagAll){//删除之前的全部数据
			merchantGroupUserMapper.deleteByGroupId(merchantGroupId);
		}
		
		if(list!=null&&list.size()>0){
			for (String  userId : list) {
				try {//如果本次操作有异常则放弃本次操作进入下一循环的操作
					
					MerchantUser merchantUser=merchantUserMapper.selectByPrimaryKey(userId);
					if(merchantUser!=null){//用户表中有值，则继续
						//插入到中间表中：判断用户和组的中间表中是否已存在，如果不存在则新增，存在则不操作
						MerchantGroupUserKey merchantGroupUserKey=new MerchantGroupUserKey();
						merchantGroupUserKey.setUserId(userId);
						merchantGroupUserKey.setMerchantGroupId(merchantGroupId);
						
						int int_count=merchantGroupUserMapper.getCountByTwoId(merchantGroupUserKey);
						if(int_count==0){//不存在，则需要新增
							merchantGroupUserMapper.insert(merchantGroupUserKey);
							import_int=import_int+1;
						}
					}
					
					
				} catch (Exception e) {
					log.error("",e);
				}
			}
			return import_int;
		}
		return import_int;
		
	}

	@Override
	public void statusEnable(String merchantGroupId) {
		merchantGroupMapper.statusEnable(merchantGroupId);
	}

	@Override
	public void statusDisable(String merchantGroupId) {
		merchantGroupMapper.statusDisable(merchantGroupId);
	}
	
	
	
	

}
