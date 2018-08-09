package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zbensoft.e.payment.api.service.api.ConsumerFamilyGroupService;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyGroup;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyGroupFamilyKey;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyGroupFamilyMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyGroupMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyMapper;

@Service
public class ConsumerFamilyGroupServiceImpl implements ConsumerFamilyGroupService {
	
	private static final Logger log = LoggerFactory.getLogger(ConsumerFamilyGroupServiceImpl.class);

	@Autowired
	ConsumerFamilyGroupMapper consumerFamilyGroupMapper;
	
	@Autowired
	ConsumerFamilyMapper consumerFamilyMapper;
	
	@Autowired
	ConsumerFamilyGroupFamilyMapper consumerFamilyGroupFamilyMapper;
	
	@Override
	public int deleteByPrimaryKey(String consumerFamilyGroupId) {
		return consumerFamilyGroupMapper.deleteByPrimaryKey(consumerFamilyGroupId);
	}

	@Override
	public int insert(ConsumerFamilyGroup record) {
		return consumerFamilyGroupMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerFamilyGroup record) {
		return consumerFamilyGroupMapper.insertSelective(record);
	}

	@Override
	public ConsumerFamilyGroup selectByPrimaryKey(String consumerFamilyGroupId) {
		return consumerFamilyGroupMapper.selectByPrimaryKey(consumerFamilyGroupId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerFamilyGroup record) {
		return consumerFamilyGroupMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerFamilyGroup record) {
		return consumerFamilyGroupMapper.updateByPrimaryKey(record);
	}

	@Override
	public int count(ConsumerFamilyGroup consumerFamilyGroup) {
		return consumerFamilyGroupMapper.count(consumerFamilyGroup);
	}

	@Override
	public List<ConsumerFamilyGroup> selectPage(ConsumerFamilyGroup consumerFamilyGroup) {
		return consumerFamilyGroupMapper.selectPage(consumerFamilyGroup);
	}

	@Override
	public boolean isNameExist(ConsumerFamilyGroup consumerFamilyGroup) {//可以名称相同
		return false;
	}

	@Override
	public void deleteAll() {
		consumerFamilyGroupMapper.deleteAll();
	}

	@Override
	@Transactional(value="DataSourceManager")
	public int saveOrUpdate(List<String> list, String consumerFamilyGroupId,boolean deleteFlagAll) {
		//System.out.println("deleteFlagAll:"+deleteFlagAll);
		int import_int=0;
		
		if(deleteFlagAll){//删除之前的全部数据
			consumerFamilyGroupFamilyMapper.deleteByGroupId(consumerFamilyGroupId);
		}
		
		if(list!=null&&list.size()>0){
			for (String  familyId : list) {
				try {//如果本次操作有异常则放弃本次操作进入下一循环的操作
					
					ConsumerFamily consumerFamily=consumerFamilyMapper.selectByPrimaryKey(familyId);
					
					if(consumerFamily!=null){//家庭表中有值，则继续
						//插入到中间表中：判断家庭和家庭组的中间表中是否已存在，如果不存在则新增，存在则不操作
						ConsumerFamilyGroupFamilyKey consumerFamilyGroupFamilyKey=new ConsumerFamilyGroupFamilyKey();
						consumerFamilyGroupFamilyKey.setFamilyId(familyId);
						consumerFamilyGroupFamilyKey.setConsumerFamilyGroupId(consumerFamilyGroupId);
						
						int int_count=consumerFamilyGroupFamilyMapper.getCountByTwoId(consumerFamilyGroupFamilyKey);
						if(int_count==0){//不存在，则需要新增
							consumerFamilyGroupFamilyMapper.insert(consumerFamilyGroupFamilyKey);
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
	public void statusEnable(String consumerFamilyGroupId) {
		consumerFamilyGroupMapper.statusEnable(consumerFamilyGroupId);
	}

	@Override
	public void statusDisable(String consumerFamilyGroupId) {
		consumerFamilyGroupMapper.statusDisable(consumerFamilyGroupId);
	}
	
	
	

}
