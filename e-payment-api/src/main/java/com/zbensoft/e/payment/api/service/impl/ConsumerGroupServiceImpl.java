package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zbensoft.e.payment.api.service.api.ConsumerGroupService;
import com.zbensoft.e.payment.db.domain.ConsumerGroup;
import com.zbensoft.e.payment.db.domain.ConsumerGroupUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.mapper.ConsumerGroupMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerGroupUserMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerUserMapper;

@Service
public class ConsumerGroupServiceImpl implements ConsumerGroupService {
	
	private static final Logger log = LoggerFactory.getLogger(ConsumerGroupServiceImpl.class);

	@Autowired
	ConsumerGroupMapper consumerGroupMapper;
	
	@Autowired
	ConsumerUserMapper consumerUserMapper;
	
	@Autowired
	ConsumerGroupUserMapper consumerGroupUserMapper;

	@Override
	public int deleteByPrimaryKey(String consumerGroupId) {
		return consumerGroupMapper.deleteByPrimaryKey(consumerGroupId);
	}

	@Override
	public int insert(ConsumerGroup record) {
		return consumerGroupMapper.insert(record);
	}

	@Override
	public int insertSelective(ConsumerGroup record) {
		return consumerGroupMapper.insertSelective(record);
	}

	@Override
	public ConsumerGroup selectByPrimaryKey(String consumerGroupId) {
		return consumerGroupMapper.selectByPrimaryKey(consumerGroupId);
	}

	@Override
	public int updateByPrimaryKeySelective(ConsumerGroup record) {
		return consumerGroupMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(ConsumerGroup record) {
		return consumerGroupMapper.updateByPrimaryKey(record);
	}

	@Override
	public int count(ConsumerGroup consumerGroup) {
		return consumerGroupMapper.count(consumerGroup);
	}

	@Override
	public List<ConsumerGroup> selectPage(ConsumerGroup consumerGroup) {
		return consumerGroupMapper.selectPage(consumerGroup);
	}

	@Override
	public boolean isNameExist(ConsumerGroup consumerGroup) {//可以名称相同
		return false;
	}

	@Override
	public void deleteAll() {
		consumerGroupMapper.deleteAll();
	}

	@Override
	@Transactional(value="DataSourceManager")
	public int saveOrUpdate(List<String> list,String consumerGroupId,boolean deleteFlagAll) {
		//System.out.println("deleteFlagAll:"+deleteFlagAll);
		int import_int=0;
		
		if(deleteFlagAll){//删除之前的全部数据
			consumerGroupUserMapper.deleteByGroupId(consumerGroupId);
		}
		
		//再插入数据
		if(list!=null&&list.size()>0){
			for (String  userId : list) {
				try {//如果本次操作有异常则放弃本次操作进入下一循环的操作
					
					ConsumerUser consumerUser=consumerUserMapper.selectByPrimaryKey(userId);
					//System.out.println("userId:"+userId+";consumerUser:"+consumerUser);
					if(consumerUser!=null){//用户表中有值，则继续
						//插入到中间表中：判断用户和组的中间表中是否已存在，如果不存在则新增，存在则不操作
						ConsumerGroupUserKey consumerGroupUserKey=new ConsumerGroupUserKey();
						consumerGroupUserKey.setUserId(userId);
						consumerGroupUserKey.setConsumerGroupId(consumerGroupId);
						
						int int_count=consumerGroupUserMapper.getCountByTwoId(consumerGroupUserKey);
						if(int_count==0){//不存在，则需要新增
							consumerGroupUserMapper.insert(consumerGroupUserKey);
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
		
		
		/*if(list!=null&&list.size()>0){
			for (ConsumerGroup consumerGroup : list) {
				try {//如果本次操作有异常则放弃本次操作进入下一循环的操作
					String consumerGroupId=consumerGroup.getConsumerGroupId();
					
					ConsumerGroup consumerGroupTmp=selectByPrimaryKey(consumerGroupId);
					if(consumerGroupTmp==null){//没有需要新增
						insert(consumerGroup);
					}else{//修改
						updateByPrimaryKey(consumerGroup);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}*/
	}

	@Override
	public void statusEnable(String consumerGroupId) {
		consumerGroupMapper.statusEnable(consumerGroupId);
	}

	@Override
	public void statusDisable(String consumerGroupId) {
		consumerGroupMapper.statusDisable(consumerGroupId);
	}
	
	

}
