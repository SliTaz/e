package com.zbensoft.e.payment.api.quartz.job.clap.firstImport.thread;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.factory.RecordFactory;
import com.zbensoft.e.payment.api.quartz.job.clap.firstImport.factory.ToDBFactory;
import com.zbensoft.e.payment.api.service.api.ConsumerFamilyService;
import com.zbensoft.e.payment.api.service.api.ConsumerRoleUserService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.vo.clap.ClapBuyer;
import com.zbensoft.e.payment.common.mutliThread.MultiThread;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;

public class EnCodeThread2 extends MultiThread {
	
	private static final Logger log = LoggerFactory.getLogger(EnCodeThread2.class);

	private static String key = "EnCodeThread2";
	ConsumerUserService consumerUserService=SpringBeanUtil.getBean(ConsumerUserService.class);

	ConsumerUserClapService consumerUserClapService=SpringBeanUtil.getBean(ConsumerUserClapService.class);;

	MerchantUserService merchantUserService=SpringBeanUtil.getBean(MerchantUserService.class);
	ConsumerFamilyService consumerFamilyService=SpringBeanUtil.getBean(ConsumerFamilyService.class);

	ConsumerRoleUserService consumerRoleUserService=SpringBeanUtil.getBean(ConsumerRoleUserService.class);
	
	
	@Value("${password.default}")
	private String DEFAULT_PASSWORD;
	
	@Value("${payPassword.default}")
	private String DEFAULT_PAYPASSWORD;

	public EnCodeThread2(String name) {
		super(name);
	}

	@Override
	public boolean process() {
		
		List<String> rList = new ArrayList<>();
		RecordFactory.getInstance().getToDB(rList, 200);
		long countRec=0l;
		if (rList != null && rList.size() > 0) {
			long startTime=System.currentTimeMillis();
			for (String record : rList) {
				//读取文件内容
				ClapBuyer buyer = new ClapBuyer(record);
				String userId = null;
				if (buyer != null && buyer.getVid() != null) {// buyer是否为空
					try {
//						boolean isExist = isExistBuyer(buyer);//buyer 是否存在
						ConsumerUser consumerUserNew = new ConsumerUser();
						ConsumerUserClap consumerUserClapNew = new ConsumerUserClap();
						ConsumerRoleUserKey consumerRoleUserKeyNew = new ConsumerRoleUserKey();
							boolean isSucc=true;
							//新增consumerUser
							isSucc = consumerUserToDB(consumerUserNew,buyer);
							if(!isSucc){
								TASK_LOG.INFO(String.format("%s ConsumerUserToDB Failed", key));
								return false;
							}
							// 再插入消费用户clap卡
							isSucc =consumerUserClapToDB(consumerUserClapNew,buyer);
							if(!isSucc){
								TASK_LOG.INFO(String.format("%s ConsumerUserClapToDB Failed", key));
								return false;
							}
							
							// 新增ConsumerRoleUser
							isSucc=getConsumerRoleUserKey(consumerRoleUserKeyNew,buyer);
							if(!isSucc){
								TASK_LOG.INFO(String.format("%s ConsumerRoleUserKeyToDB Failed", key));
								return false;
							}
							
							insertUserTransactional(consumerUserNew,consumerUserClapNew,consumerRoleUserKeyNew,buyer);
					} catch (Exception e) {//数据插入异常
						log.error(String.format("%s This record insert failed", key), e);
						TASK_LOG.ERROR(String.format("%s This record insert failed", key), e);
					}

				}else{//第[n]条数为空或长度不对
					TASK_LOG.ERROR(String.format("%s read one record failed, The record is %s", key,record));
				}
			
				countRec++;
			}
			
//			System.out.println(this.getName()+" finish 200 case, in "+(System.currentTimeMillis()-startTime));
		}
		return false;
	}
	
	private void insertUserTransactional(ConsumerUser consumerUserNew, ConsumerUserClap consumerUserClapNew,
			ConsumerRoleUserKey consumerRoleUserKeyNew, ClapBuyer buyer) {
		ToDBFactory.getInstance().addConsumerUser(consumerUserNew);
		ToDBFactory.getInstance().addConsumerUserClap(consumerUserClapNew);
		ConsumerRoleUserKey resultKey = consumerRoleUserService.selectByPrimaryKey(consumerRoleUserKeyNew);
		if (resultKey == null) {
			ToDBFactory.getInstance().addConsumerRoleUserKeyr(consumerRoleUserKeyNew);
		}
		
		// 新增或更新family
		ConsumerFamily consumerFamilyNew =new ConsumerFamily();
		ConsumerFamily consumerFamily = consumerFamilyService.selectByPrimaryKey(buyer.getFamilyCode());
		if (consumerFamily == null) {
			consumerFamilyNew.setFamilyId(buyer.getFamilyCode());
			consumerFamilyNew.setName(buyer.getFamilyCode());
			consumerFamilyNew.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
			ToDBFactory.getInstance().addConsumerFamilyList(consumerFamilyNew);

		} else {
			TASK_LOG.INFO("----->####<-----There are more than one user in this family, VId: "+consumerUserClapNew.getIdNumber()+" Family ID"+consumerFamily.getFamilyId());
		}
		
	}





	private boolean getConsumerRoleUserKey(ConsumerRoleUserKey consumerRoleUserKeyNew,ClapBuyer buyer) {
		if (buyer != null) {
				consumerRoleUserKeyNew.setUserId(getUserId(buyer));
				consumerRoleUserKeyNew.setRoleId("1");
				return true;
		}
		return false;
	}


	private boolean consumerUserClapToDB(ConsumerUserClap consumerUserClapNew ,ClapBuyer buyer) {
		if (buyer != null) {
				consumerUserClapNew.setConsumerUserClapId(getUserId(buyer));
				consumerUserClapNew.setUserId(getUserId(buyer));
				consumerUserClapNew.setIdNumber(buyer.getVid());
				consumerUserClapNew.setName1(buyer.getName1());
				consumerUserClapNew.setName2(buyer.getName2());
				consumerUserClapNew.setLastName1(buyer.getLastName1());
				consumerUserClapNew.setLastName2(buyer.getLastName2());
				consumerUserClapNew.setSex(buyer.getSex().equals("f") ? MessageDef.USER_SEX.FEMALE : MessageDef.USER_SEX.MALE);
				consumerUserClapNew.setCommunityCode(buyer.getCommunityCode());
				consumerUserClapNew.setFamilyId(buyer.getFamilyCode());
				consumerUserClapNew.setClapSeqNo(buyer.getPatrimonyCardSerial());
				consumerUserClapNew.setClapNo(buyer.getPatrimonyCardCode());
				consumerUserClapNew.setClapStoreNo(buyer.getClapCode());
				consumerUserClapNew.setBindTime(PageHelperUtil.getCurrentDate());
				consumerUserClapNew.setDatebirth(DateUtil.convertStringToDate(buyer.getDateBirth(), DateUtil.DATE_FORMAT_TWO));
				consumerUserClapNew.setStatus(Integer.valueOf(buyer.getStatus()));
				return true;
		}
		return false;
	}


	private boolean consumerUserToDB(ConsumerUser consumerUserNew, ClapBuyer buyer) {
		if (buyer != null) {
			consumerUserNew.setUserId(getUserId(buyer));
			consumerUserNew.setUserName(getUserName(buyer));
			// 后台添加字段 start
			consumerUserNew.setBalance(0);// 余额为0
			consumerUserNew.setStatus(getStatus(buyer));// 设置状态
			// 密码
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if (buyer.getPatrimonyCardCode() != null) {
				String number = buyer.getPatrimonyCardCode().replaceAll("[^0-9]+", "");
				// 登陆密码
				String pwd = encoder.encode(CommonFun.generaPassword(number, 6));
				consumerUserNew.setPassword(pwd);
				// 支付密码
				consumerUserNew.setPayPassword(pwd);
			} else {
				consumerUserNew.setPassword(encoder.encode(DEFAULT_PASSWORD));
				consumerUserNew.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD));
			}
			consumerUserNew.setIsLocked(0);// 未冻结
			consumerUserNew.setIsBindClap(1);// 已绑定
			consumerUserNew.setIsBindBankCard(0);// 未绑定
			consumerUserNew.setIsFirstLogin(1);// 首次登陆
			consumerUserNew.setCreateTime(PageHelperUtil.getCurrentDate());
			consumerUserNew.setIsActive(1);
			consumerUserNew.setIsDefaultPassword(1);
			consumerUserNew.setIsDefaultPayPassword(1);
			consumerUserNew.setEmailBindStatus(0);
//			consumerUserService.insert(consumerUserNew);
			return true;
		}
		return false;
	}





	
	private String getUserId(ClapBuyer buyer){
		if(buyer!=null&&buyer.getVid()!=null){
			return MessageDef.USER_TYPE.CONSUMER_STRING + buyer.getVid();
		}
		return null;
	}



	private String getUserName(ClapBuyer buyer) {
		String names = null;
		if (buyer.getName1() != null && !"".equals(buyer.getName1())) {
			names = buyer.getName1();
		}
		if (buyer.getLastName1() != null && !"".equals(buyer.getLastName1())) {
			names += " " + buyer.getLastName1();
		}
		
		if (names == null) {
			if (buyer.getName2() != null && !"".equals(buyer.getName2())) {
				names = buyer.getName2();
			}
			if (buyer.getLastName2() != null && !"".equals(buyer.getLastName2())) {
				names += " " + buyer.getLastName2();
			}
		}
		
		return names;
	}
	
	private int getStatus(ClapBuyer buyer) {
		if(buyer.getStatus()!=null){
			if(MessageDef.BUYER_STATUS.ACTIVE.equals(buyer.getStatus())){
				return 0;
			}
		}
		
		return 1;
	}
	

	

}
