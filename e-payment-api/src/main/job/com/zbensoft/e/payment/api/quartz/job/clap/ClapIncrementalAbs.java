package com.zbensoft.e.payment.api.quartz.job.clap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.PageHelperUtil;
import com.zbensoft.e.payment.api.common.SpringBeanUtil;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.CouponService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeService;
import com.zbensoft.e.payment.api.service.api.MerchantUserBankCardService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.vo.clap.ClapBuyer;
import com.zbensoft.e.payment.api.vo.clap.ClapBuyerHeader;
import com.zbensoft.e.payment.api.vo.clap.ClapDistribution;
import com.zbensoft.e.payment.api.vo.clap.ClapDistributionHeader;
import com.zbensoft.e.payment.api.vo.clap.ClapSeller;
import com.zbensoft.e.payment.api.vo.clap.ClapSellerAccount;
import com.zbensoft.e.payment.api.vo.clap.ClapSellerAccountHeader;
import com.zbensoft.e.payment.api.vo.clap.ClapSellerHeader;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.Coupon;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.domain.TradeInfo;
/**
 * Clap数据更新 抽象类
 * @author Wang Chenyang
 *
 */
public abstract class   ClapIncrementalAbs {
	
	private static final Logger log = LoggerFactory.getLogger(ClapIncrementalAbs.class);

	@Value("${password.default}")
	private String DEFAULT_PASSWORD;

	@Value("${payPassword.default}")
	private String DEFAULT_PAYPASSWORD;
	
	private static String key = "ClapIncrementalAbs";
	ConsumerUserService consumerUserService = SpringBeanUtil.getBean(ConsumerUserService.class);
	ConsumerUserClapService consumerUserClapService = SpringBeanUtil.getBean(ConsumerUserClapService.class);;
	MerchantUserService merchantUserService = SpringBeanUtil.getBean(MerchantUserService.class);
	MerchantEmployeeService merchantEmployeeService=SpringBeanUtil.getBean(MerchantEmployeeService.class);
	CouponService couponService=SpringBeanUtil.getBean(CouponService.class);
	MerchantUserBankCardService merchantUserBankCardService=SpringBeanUtil.getBean(MerchantUserBankCardService.class);
	
	
	
	String JOB_CLAP_DOWNLOAD_FILE_PATH = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_DOWNLOAD_FILE_PATH);
	String BYER_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_BYER_FILE_PREFIX);
	String BYER_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_BYER_FILE_DATEFORMATE);
	int JOB_CLAP_SFTP_FILE_DAY_BEFORE =SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_SFTP_FILE_DAY_BEFORE);
	String buyerFileName = BYER_FILE_PREFIX+ DateUtil.convertDateToString(DateUtil.addDate(new Date(), JOB_CLAP_SFTP_FILE_DAY_BEFORE, 3), BYER_FILE_DATEFORMATE)+".txt";
	
	
	String SELLER_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SELLER_FILE_PREFIX);
	String SELLER_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SELLER_FILE_DATEFORMATE);
	String sellerFileName = SELLER_FILE_PREFIX+ DateUtil.convertDateToString(DateUtil.addDate(new Date(), JOB_CLAP_SFTP_FILE_DAY_BEFORE, 3), SELLER_FILE_DATEFORMATE)+".txt";
	
	String DISTRIBUTION_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_DISTRIBUTION_FILE_PREFIX);
	String DISTRIBUTION_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_DISTRIBUTION_FILE_DATEFORMATE);
	String disFileName=DISTRIBUTION_FILE_PREFIX+DateUtil.convertDateToString(DateUtil.addDate(new Date(),JOB_CLAP_SFTP_FILE_DAY_BEFORE,3),DISTRIBUTION_FILE_DATEFORMATE)+".txt";
	
	String SELLERACCOUNT_FILE_PREFIX = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SELLERACCOUNT_FILE_PREFIX);
	String SELLERACCOUNT_FILE_DATEFORMATE = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.JOB_CLAP_SELLERACCOUNT_FILE_DATEFORMATE);
	String sellerAccountFileName=SELLERACCOUNT_FILE_PREFIX+DateUtil.convertDateToString(DateUtil.addDate(new Date(),JOB_CLAP_SFTP_FILE_DAY_BEFORE,3),SELLERACCOUNT_FILE_DATEFORMATE)+".txt";
	
	
	private Map<String, List<ClapSeller>> clapMap=new HashMap<>();
	long sellerFinishCount=0;
	ClapSellerHeader sellerUpdateHeader=null;
	
	protected boolean executeClapSellerUpdateJob() {
		
		boolean isSucc = true;
		isSucc = loadFile();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s load SellerFile is error", key));
			return false;
		} else {
			TASK_LOG.INFO(String.format("%s load SellerFile is Success", key));
			
		}
		isSucc=upDateSellerToDB();
		
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s upDateSellerToDB is error", key));
			return false;
		} else {
			TASK_LOG.INFO(String.format("%s upDateSellerToDB is Success", key));
		}
		return true;
	}
	
	protected boolean executeClapSellerAccountUpdateJob() {
		boolean isSucc = true;
		isSucc = sellerAccountFileToDB();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s fileToDB is error", key));
			return false;
		} else {
			TASK_LOG.INFO(String.format("%s fileToDB is Success", key));
		}
		return true;
	}
	
	
	protected boolean executeClapBuyerUpdateJob() {
		boolean isSucc = true;
		isSucc = buyerFileToDB();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s BuyerFileToDB is error", key));
			return false;
		} else {
			TASK_LOG.INFO(String.format("%s BuyerFileToDB is Success", key));
		}
		return true;
	}
	
	protected boolean executeClapDistributionUpdateJob() {
		boolean isSucc = true;
		isSucc = disFileToDB();
		if (!isSucc) {
			TASK_LOG.INFO(String.format("%s fileToDB is error", key));
			return false;
		} else {
			TASK_LOG.INFO(String.format("%s fileToDB is Success", key));
		}
		return true;
	}

	
/***Buyer functions Start***/		
	
	/**
	 * Buyer入库方法
	 * @return
	 */
	private boolean buyerFileToDB() {
		BufferedReader reader=null;
		try {
			
			File file =getClapBuyerFile();//获取文件
			if (file.exists() && file.isFile()) {//判断文件是否存在
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
				reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// 用5M的缓冲读取文本文件
				String line = reader.readLine();//读取文件头
				if (line != null) {//文件头是否未空
					ClapBuyerHeader buyerUpdateHeader=new ClapBuyerHeader(line);//生成文件头类
					if (buyerUpdateHeader != null && buyerUpdateHeader.getTotalFileRecord()!=null) {
						long readCount=1;
						long totalCount=0;
						while ((line = reader.readLine()) != null) {//读取文件内容
							totalCount++;
							ClapBuyer buyer = new ClapBuyer(line);
							if (buyer != null && buyer.getVid() != null) {// buyer是否为空
								try {
									ConsumerUser existResult=null;
									boolean isExist = isExistBuyer(buyer,existResult);//buyer 是否存在
									ConsumerUser consumerUserNew = new ConsumerUser();
									ConsumerUserClap consumerUserClapNew = new ConsumerUserClap();
									ConsumerRoleUserKey consumerRoleUserKeyNew = new ConsumerRoleUserKey();
									if (!isExist) {// 新增;
										boolean isSucc=true;
										//新增consumerUser
										isSucc = consumerUserToDB(consumerUserNew,buyer);
										if(!isSucc){
											TASK_LOG.ERROR(String.format("%s Insert ConsumerUser To DB Failed Record=%s", key,line));
											continue;
										}
										// 再插入消费用户clap卡
										isSucc =consumerUserClapToDB(consumerUserClapNew,buyer);
										if(!isSucc){
											TASK_LOG.ERROR(String.format("%s Insert ConsumerUserClap To DB Failed Record=%s", key,line));
											continue;
										}
										
										// 新增ConsumerRoleUser
										isSucc=getConsumerRoleUserKey(consumerRoleUserKeyNew,buyer);
										if(!isSucc){
											TASK_LOG.INFO(String.format("%s Insert ConsumerRoleUserKey To DB Failed Record=%s", key,line));
											continue;
										}
										consumerUserService.insertUserTransactional(consumerUserNew,consumerUserClapNew,consumerRoleUserKeyNew,buyer);
										readCount++;

									} else {// 更新
										boolean isSucc=true;
										// 更新 consumerUser
										isSucc = getUpdateConsumerUser(consumerUserNew ,buyer);
										if(!isSucc){
											TASK_LOG.ERROR(String.format("%s Update ConsumerUser To DB Failed Record=%s", key,line));
											continue;
										}
										// 更新 consumerUserClap
										isSucc = getUpdateConsumerUserClap(existResult,consumerUserClapNew,buyer);
										if(!isSucc){
											TASK_LOG.ERROR(String.format("%s Update ConsumerUserClap To DB  Failed Record=%s", key,line));
											continue;
										}
										// 更新consumerRoleUser
										isSucc = getConsumerRoleUserKey(consumerRoleUserKeyNew, buyer);
										if(!isSucc){
											TASK_LOG.ERROR(String.format("%s Update ConsumerRoleUserKey To DB Failed Record=%s", key,line));
											continue;
										}
										consumerUserService.updateUserTransactional(consumerUserNew,consumerUserClapNew,consumerRoleUserKeyNew,buyer);
										readCount++;
									}
									
								} catch (Exception e) {//数据插入异常
									log.error(String.format("%s This record insert failed", key), e);
									TASK_LOG.ERROR(String.format("%s This record insert failed", key), e);
								}

							}else{//第[n]条数为空或长度不对
								TASK_LOG.ERROR(String.format("%s Reading file %s, The [%d] record is null or length incorrect", key,file.getName(),totalCount));
							}
							
						}
						TASK_LOG.INFO(String.format("%s Read file %s finish, success record --%d/%s--", key,file.getName(),(readCount-1),buyerUpdateHeader.getTotalFileRecord()));
					}else{//文件头格式不正确
						TASK_LOG.ERROR(String.format("%s Read file %s failed, file Header formate incorrect", key,file.getName()));
						return false;
					}
				}else{//文件头不存在
					TASK_LOG.ERROR(String.format("%s read file %s failed, file Header missed", key,file.getName()));
					return false;
				}
			}else{//文件不存在
				TASK_LOG.ERROR(String.format("%s read file %s failed, file not exist", key,file.getName()));
				return false;
			}
			return true;
		} catch (Exception e) {//读取文件异常
			log.error(String.format("%s Read buyer file failed", key), e);
			TASK_LOG.ERROR(String.format("%s Read buyer file failed", key), e);
		}finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					log.error(String.format("%s Read buyer IO close exception", key), e1);
				}
			}
		}
		return false;
	}

	/**
	 * 获取新的ConsumerUserClap
	 * @param existResul
	 * @param consumerUserClapNew
	 * @param buyer
	 * @return Boolean
	 */
	private boolean getUpdateConsumerUserClap(ConsumerUser existResul,ConsumerUserClap consumerUserClapNew ,ClapBuyer buyer) {
		if (buyer != null) {
			if(MessageDef.BUYER_STATUS.INACTIVATED.equals(buyer.getStatus())){//如果未注销，则校验卡号是否与原来卡号相同
				if(existResul!=null&&existResul.getUserId()!=null){
					ConsumerUserClap exitsConsumerUserClap=consumerUserClapService.selectByUser(existResul.getIdNumber());
					if(!buyer.getPatrimonyCardSerial().equals(exitsConsumerUserClap.getClapSeqNo())){//卡号不相同，则不更新该条数据
						return false;
					}
				}
				
			}
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


	/**
	 * 获取新的ConsumerUser
	 * @param consumerUserNew
	 * @param buyer
	 * @return
	 */
	private boolean getUpdateConsumerUser(ConsumerUser consumerUserNew ,ClapBuyer buyer) {
		if (buyer != null) {
			if(MessageDef.BUYER_STATUS.INACTIVATED.equals(buyer.getStatus())){
				consumerUserNew.setUserId(getUserId(buyer));
				consumerUserNew.setUserName(getUserName(buyer));
				consumerUserNew.setStatus(getStatus(buyer));// 设置状态
			}else{//更新所有字段
				consumerUserNew.setUserId(getUserId(buyer));
				consumerUserNew.setUserName(getUserName(buyer));
				// 后台添加字段 start
//				consumerUserNew.setBalance(0);// 余额为0
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
//				consumerUserService.insert(consumerUserNew);
			}
			return true;
		}
		return false;
	}

	/**
	 * 获取新的ConsumerRoleUserKey
	 * @param consumerRoleUserKeyNew
	 * @param buyer
	 * @return
	 */
	private boolean getConsumerRoleUserKey(ConsumerRoleUserKey consumerRoleUserKeyNew,ClapBuyer buyer) {
		if (buyer != null) {
				consumerRoleUserKeyNew.setUserId(getUserId(buyer));
				consumerRoleUserKeyNew.setRoleId("1");
				return true;
		}
		return false;
	}

	/**
	 * 获取入库的ConsumerUserClap
	 * @param consumerUserClapNew
	 * @param buyer
	 * @return
	 */
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

	/**
	 * 获取入库的ConsumerUser
	 * @param consumerUserNew
	 * @param buyer
	 * @return
	 */
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
//				consumerUserService.insert(consumerUserNew);
				return true;
		}
		return false;
	}

	/**
	 * 判断用户是存在
	 * @param buyer
	 * @param existResult
	 * @return
	 */
	private boolean isExistBuyer(ClapBuyer buyer,ConsumerUser existResult) {
		existResult = consumerUserService.selectByPrimaryKey(getUserId(buyer));
		if(existResult!=null){
			return true;
		}
		return false;
	}


	/**
	 * 获取用户UserId
	 * @param buyer
	 * @return
	 */
	private String getUserId(ClapBuyer buyer){
		if(buyer!=null&&buyer.getVid()!=null){
			return MessageDef.USER_TYPE.CONSUMER_STRING + buyer.getVid();
		}
		return null;
	}
	
	/**
	 * 获取用户更新文件路径
	 * @return
	 */
	private File getClapBuyerFile() {
		
		File file = new File(JOB_CLAP_DOWNLOAD_FILE_PATH + buyerFileName);
		return file;
	}

	/**
	 * 获取用户名: Name1 LastName1
	 * @param buyer
	 * @return
	 */
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
	
	/**
	 * 获取转换后的状态
	 * @param buyer
	 * @return
	 */
	private int getStatus(ClapBuyer buyer) {
		if(buyer.getStatus()!=null){
			if(MessageDef.BUYER_STATUS.ACTIVE.equals(buyer.getStatus())){
				return 0;
			}
		}
		
		return 1;
	}
	
/***Buyer functions End***/	
	
	
	
/***Seller functions Start***/	
	
	/**
	 * Seller更新入库方法
	 * @return
	 */
	private boolean upDateSellerToDB() {
		if(clapMap!=null&&clapMap.size()>0){
			 try {
				for (Entry<String, List<ClapSeller>> entry : clapMap.entrySet()) {
					 	MerchantUser merchantUser=merchantUserService.selectByClapId(entry.getKey());
					 	boolean isSucc=true;
					 	if(merchantUser==null){//新增
					 		isSucc=createClapStoreToDB(entry.getValue());
					 		if(!isSucc){
					 			TASK_LOG.INFO(String.format("%s upDateSellerToDB Failed, Clap Store No: %s", key,entry.getKey()));
					 		}
					 	}else{//更新
					 		isSucc=compareClapStoreToDB(merchantUser,entry.getValue());
					 		if(!isSucc){
					 			TASK_LOG.INFO(String.format("%s upDateSellerToDB Failed, Clap Store No: %s", key,entry.getKey()));
					 		}
					 	}
					 	
				    }
				
				TASK_LOG.INFO(String.format("%s upDateSellerToDB finished , success record --%d/%s--", key,sellerFinishCount,sellerUpdateHeader.getTotalFileRecord()));
			} catch (Exception e) {
				log.error(String.format("%s upDateSellerToDB Exception", key), e);
				TASK_LOG.ERROR(String.format("%s upDateSellerToDB Exception", key), e);
				return false;
			}
		}
		
		return true;
	}

	/**
	 * 新增Seller方法,同一个商店
	 * @param clapStore
	 * @return
	 */
	private boolean createClapStoreToDB(List<ClapSeller> clapStore) {
		List<MerchantEmployee> newEmployeeList=new ArrayList<>();
		MerchantUser merchantNewUser=null;
		if(clapStore!=null&&clapStore.size()>0){
			for (ClapSeller storeSeller : clapStore) {
				if(storeSeller.getRoleType()!=null){
					if("s".equals(storeSeller.getRoleType())){//更新商戶
						merchantNewUser=getMerchantUser(storeSeller);
						sellerFinishCount++;
					}
				}else{
					TASK_LOG.INFO(String.format("%s compareClapSotreToDB getRoleType is Null", key));
				}
			}

			for (ClapSeller storeSeller : clapStore) {//防止Seller在employed后面更新
				if(storeSeller.getRoleType()!=null){
					if("e".equals(storeSeller.getRoleType())){
						if(merchantNewUser==null){//如果新增商店没有seller信息，则无法跟新employed信息
							TASK_LOG.INFO(String.format("%s create Employee failed, %s No Clap-store-No", key,storeSeller.getVid()));
						}else{
							MerchantEmployee newEmployee = getEmployee(merchantNewUser,storeSeller);
							newEmployeeList.add(newEmployee);
							sellerFinishCount++;
						}
					}
				}else{
					TASK_LOG.INFO(String.format("%s compareClapSotreToDB getRoleType is Null", key));
				}
			}
			
			//创建这个seller下对应用户的券关系
			List<ConsumerFamilyCoupon> toDBConsumerFamilyCouponList = new ArrayList<>();// 券family列表
			Set<String> buyerfamilySet = new HashSet<>();
			
			//2017-12-11 逻辑修改
			int start=0;
			int JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME);
			List<ConsumerUserClap> consumerUserClapList = new ArrayList<>();
			
			while (true) {
				int pageNum = PageHelperUtil.getPageNum(String.valueOf(start),
						String.valueOf(JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME));
				int pageSize = PageHelperUtil.getPageSize(String.valueOf(start),
						String.valueOf(JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME));
				PageHelper.startPage(pageNum, pageSize);
				consumerUserClapList = consumerUserClapService.selectByClapStoreNo(merchantNewUser.getClapStoreNo());

				if (consumerUserClapList != null && consumerUserClapList.size() > 0) {
					for (ConsumerUserClap consumerUserClapTmp : consumerUserClapList) {
						buyerfamilySet.add(consumerUserClapTmp.getFamilyId());
					}
					start += consumerUserClapList.size();
				}
				if (consumerUserClapList == null || consumerUserClapList.size() != JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME) {
					if(start==0){
						TASK_LOG.INFO(String.format("%s get user list from Seller failed consumerUserClapList=null", key));
					}
					break;
					
				}
			}
			Coupon newCouponSer = new Coupon();
			newCouponSer.setConsumerGroupId(merchantNewUser.getClapStoreNo());
			newCouponSer.setCurrentTime(DateUtil.convertDateToString(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_FIVE));
			start=0;
			int JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME);
			List<Coupon> newCouponList=new ArrayList<>();
			while (true) {
				int pageNum = PageHelperUtil.getPageNum(String.valueOf(start),String.valueOf(JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME));
				int pageSize = PageHelperUtil.getPageSize(String.valueOf(start),String.valueOf(JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME));
				PageHelper.startPage(pageNum, pageSize);
				newCouponList = couponService.selectAvailableCoupon(newCouponSer);
				if (newCouponList != null && newCouponList.size() > 0) {
					for (Coupon newCoupon : newCouponList) {
						getConsumerFamilyCouponList(toDBConsumerFamilyCouponList, buyerfamilySet, newCoupon);

					}
					start += newCouponList.size();
				}
				if (newCouponList == null || newCouponList.size() != JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME) {
					if (start == 0) {
						TASK_LOG.INFO(String.format("%s get newcoupon failed newCouponList=null", key));
					}
					break;

				}
			}
			merchantUserService.deleteAndInsert(null, merchantNewUser, newEmployeeList, null,toDBConsumerFamilyCouponList);
			
			return true;
		}
		return false;
	}

	/**
	 * 比较更新Seller方法,同一个商店
	 * @param merchantUser
	 * @param clapStore
	 * @return
	 */
	private boolean compareClapStoreToDB(MerchantUser merchantUser, List<ClapSeller> clapStore) {
		List<MerchantEmployee> newEmployeeList=new ArrayList<>();
		List<MerchantEmployee> deleteEmployeeList=new ArrayList<>();
		MerchantUser merchantNewUser=null;
		try {
			PageHelper.startPage(1, 100000);
			List<MerchantEmployee> employeeList=merchantEmployeeService.selectByUserId(merchantUser.getUserId());
			if(clapStore!=null&&clapStore.size()>0){
				for (ClapSeller storeSeller : clapStore) {
					if(storeSeller.getRoleType()!=null){
						if("s".equals(storeSeller.getRoleType())){//更新商戶
							merchantNewUser=getMerchantUser(storeSeller);
							sellerFinishCount++;
						}else{
							if (employeeList != null && employeeList.size() > 0) {
								if (isNewEmployee(storeSeller, employeeList)) {// 比较查找新增的的员工
									MerchantEmployee newEmployee = getEmployee(merchantUser,storeSeller);
									newEmployeeList.add(newEmployee);
									sellerFinishCount++;
								}
							}else{//都是新员工
								MerchantEmployee newEmployee = getEmployee(merchantUser,storeSeller);
								newEmployeeList.add(newEmployee);
								sellerFinishCount++;
							}
						}
					}else{
						TASK_LOG.INFO(String.format("%s compareClapSotreToDB getRoleType is Null", key));
					}
				}
				if (employeeList != null && employeeList.size() > 0) {
					for (MerchantEmployee oldMerchantEmployee : employeeList) {
						if (isDelEmployee(oldMerchantEmployee, clapStore)) {
							deleteEmployeeList.add(oldMerchantEmployee);
						}
					}
				}
				
				//创建这个seller下对应用户的券关系
				List<ConsumerFamilyCoupon> toDBConsumerFamilyCouponList = new ArrayList<>();// 券family列表
				Set<String> buyerfamilySet = new HashSet<>();
				
				
				int start=0;
				int JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME);
				List<ConsumerUserClap> consumerUserClapList = new ArrayList<>();
				
				while (true) {
					int pageNum = PageHelperUtil.getPageNum(String.valueOf(start),
							String.valueOf(JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME));
					int pageSize = PageHelperUtil.getPageSize(String.valueOf(start),
							String.valueOf(JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME));
					PageHelper.startPage(pageNum, pageSize);
					consumerUserClapList = consumerUserClapService.selectByClapStoreNo(merchantNewUser.getClapStoreNo());

					if (consumerUserClapList != null && consumerUserClapList.size() > 0) {
						for (ConsumerUserClap consumerUserClapTmp : consumerUserClapList) {
							buyerfamilySet.add(consumerUserClapTmp.getFamilyId());
						}
						start += consumerUserClapList.size();
					}
					if (consumerUserClapList == null || consumerUserClapList.size() != JOB_CLAP_TRADE_SELECT_BY_CLAPSTORENO_ONETIME) {
						if(start==0){
							TASK_LOG.INFO(String.format("%s get user list from Seller failed consumerUserClapList=null", key));
						}
						break;
						
					}
				}
				Coupon newCouponSer = new Coupon();
				newCouponSer.setConsumerGroupId(merchantNewUser.getClapStoreNo());
				newCouponSer.setCurrentTime(DateUtil.convertDateToString(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_FIVE));
				start=0;
				int JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME);
				List<Coupon> newCouponList=new ArrayList<>();
				while (true) {
					int pageNum = PageHelperUtil.getPageNum(String.valueOf(start),String.valueOf(JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME));
					int pageSize = PageHelperUtil.getPageSize(String.valueOf(start),String.valueOf(JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME));
					PageHelper.startPage(pageNum, pageSize);
					newCouponList = couponService.selectAvailableCoupon(newCouponSer);
					if (newCouponList != null && newCouponList.size() > 0) {
						for (Coupon newCoupon : newCouponList) {
							getConsumerFamilyCouponList(toDBConsumerFamilyCouponList, buyerfamilySet, newCoupon);

						}
						start += newCouponList.size();
					}
					if (newCouponList == null || newCouponList.size() != JOB_CLAP_TRADE_SELECT_AVAILABLE_COUPON_ONETIME) {
						if (start == 0) {
							TASK_LOG.INFO(String.format("%s get newcoupon failed newCouponList=null", key));
						}
						break;

					}
				}
			
				merchantUserService.deleteAndInsert(merchantUser,merchantNewUser,newEmployeeList,deleteEmployeeList,toDBConsumerFamilyCouponList);
				return true;
			}else{
				TASK_LOG.INFO(String.format("%s compareClapSotreToDB clapStore is Null", key));
			}
		} catch (Exception e) {
			log.error(String.format("%s compareClapSotreToDB Exception", key), e);
			TASK_LOG.ERROR(String.format("%s compareClapSotreToDB Exception", key), e);
		}
		
		
		return false;
	}

	/**
	 * 是否为删除员工
	 * @param oldMerchantEmployee
	 * @param clapStore
	 * @return
	 */
	private boolean isDelEmployee(MerchantEmployee oldMerchantEmployee, List<ClapSeller> clapStore) {
		if (oldMerchantEmployee != null && clapStore != null && clapStore.size() > 0) {
			for (ClapSeller seller : clapStore) {
				if (oldMerchantEmployee.getIdNumber().equals(seller.getVid())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 获取新员工信息
	 * @param MerchantUser
	 * @param ClapSeller
	 * @return MerchantEmployee
	 */
	private MerchantEmployee getEmployee(MerchantUser merchantUser, ClapSeller storeSeller) {
		MerchantEmployee newEmployee=new MerchantEmployee();
		newEmployee.setEmployeeUserId(getUserId(storeSeller));
		newEmployee.setUserId(merchantUser.getUserId());
		newEmployee.setIdNumber(storeSeller.getVid());
		newEmployee.setUserName(getUserName(storeSeller));
		newEmployee.setEmailBindStatus(0);
		newEmployee.setStatus(0);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (storeSeller.getVid() != null ) {
			String pwd = CommonFun.generaPassword(storeSeller.getVid(), 6);
			// 登陆密码
			newEmployee.setPassword(encoder.encode(pwd));
		} else {
			newEmployee.setPassword(encoder.encode(DEFAULT_PASSWORD));
		}
		newEmployee.setIsLocked(0);
		newEmployee.setIsFirstLogin(1);
		newEmployee.setCreateTime(PageHelperUtil.getCurrentDate());
		
		return newEmployee;
	}


	/**
	 * 是否为员工
	 * @param storeSeller
	 * @param employeeList
	 * @return
	 */
	private boolean isNewEmployee(ClapSeller storeSeller, List<MerchantEmployee> employeeList) {
		if (storeSeller != null && employeeList != null && employeeList.size() > 0) {
			for (MerchantEmployee merchantEmployee : employeeList) {
				if (storeSeller.getVid().equals(merchantEmployee.getIdNumber())) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 获取商户
	 * @param storeSeller
	 * @return
	 */
	private MerchantUser getMerchantUser(ClapSeller storeSeller) {
		MerchantUser merchantUser = new MerchantUser();
		if (storeSeller != null) {
			merchantUser.setUserId(getUserId(storeSeller));
			merchantUser.setUserName(getUserName(storeSeller));
			merchantUser.setBalance(0d);// 余额为0
			merchantUser.setStatus(0);

			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			if (storeSeller.getClapCode() != null && storeSeller.getClapCode().split("-").length > 0) {
				String[] clapCodes = storeSeller.getClapCode().split("-");
				String pwd = CommonFun.generaPassword(clapCodes[clapCodes.length - 1], 6);
				String enPassword = encoder.encode(pwd);
				// 登陆密码
				merchantUser.setPassword(enPassword);
				// 支付密码
				merchantUser.setPayPassword(enPassword);

			} else {
				merchantUser.setPassword(encoder.encode(DEFAULT_PASSWORD));
				merchantUser.setPayPassword(encoder.encode(DEFAULT_PAYPASSWORD));
			}

			merchantUser.setEmployeeLimit(4);

			merchantUser.setIsActive(1);
			merchantUser.setIsLocked(0);
			merchantUser.setIsBindBankCard(0);
			merchantUser.setIsDefaultPassword(1);
			merchantUser.setIsDefaultPayPassword(1);
			merchantUser.setIsFirstLogin(1);
			merchantUser.setCreateTime(PageHelperUtil.getCurrentDate());
			merchantUser.setIdNumber(storeSeller.getVid());
			merchantUser.setClapStoreNo(storeSeller.getClapCode());
			merchantUser.setEmailBindStatus(0);

		}
		return merchantUser;
	}

	/**
	 * 加载 Seller文件数据并放入内存
	 * @return Boolean
	 */
	private boolean loadFile() {
		BufferedReader reader=null;
		BufferedInputStream fis=null;
		try {
			
			File file = new File(JOB_CLAP_DOWNLOAD_FILE_PATH + sellerFileName);
			if (file.exists() && file.isFile()) {
				fis = new BufferedInputStream(new FileInputStream(file));
				reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// 用5M的缓冲读取文本文件
				String line = reader.readLine();
				if (line != null) {// 文件头是否未空
					sellerUpdateHeader = new ClapSellerHeader(line);
					if (sellerUpdateHeader != null && sellerUpdateHeader.getTotalFileRecord() != null) {
						long readCount = 1;
						clapMap.clear();
						while ((line = reader.readLine()) != null) {
							ClapSeller sellerUpdate = new ClapSeller(line);
							if (sellerUpdate != null && sellerUpdate.getVid() != null) {
								boolean isSucc = true;
								isSucc = putSellerToMem(sellerUpdate);
								if (!isSucc) {
									TASK_LOG.ERROR(String.format("%s putSellerToMem Failed, Record=%s", key,line));
									return false;
								}
								readCount++;
							} else{//第[n]条数为空或长度不对
								TASK_LOG.ERROR(String.format("%s read file %s success, The [%d] record is null or length incorrect", key,file.getName(),readCount));
							}
						}
						TASK_LOG.INFO(String.format("%s read file %s finish, success record --%d/%s--", key,file.getName(),(readCount-1),sellerUpdateHeader.getTotalFileRecord()));
						return true;
					}else{
						TASK_LOG.ERROR(String.format("%s read file %s failed, file Header formate incorrect", key,file.getName()));
						return false;
					}
				} else {
					TASK_LOG.ERROR(String.format("%s read file %s failed, file Header missed", key,file.getName()));
					return false;
				}
			}else{
				TASK_LOG.ERROR(String.format("%s read file %s failed, file not exist", key,file.getName()));
				return false;
			}
		} catch (Exception e) {
			log.error("Read buyer file failed", e);
			TASK_LOG.ERROR("Read buyer file failed", e);
		}finally {
			if (reader != null) {
				try {
					reader.close();
					
				} catch (IOException e1) {
					log.error("BufferedReader close exception", e1);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e2) {
					log.error("BufferedInputStream close exception", e2);
				}
			}
		}

		return false;
	}
	
	
	/**
	 * Seller放入内存
	 * @param ClapSeller
	 * @return Boolean
	 */
	private boolean putSellerToMem(ClapSeller sellerUpdate) {
		if(sellerUpdate!=null){
			if(clapMap!=null&&clapMap.size()>0){
				if(clapMap.get(sellerUpdate.getClapCode())==null){
					List<ClapSeller> newClapStore=new ArrayList<>();
					newClapStore.add(sellerUpdate);
					clapMap.put(sellerUpdate.getClapCode(), newClapStore);
					return true;
				}else{
					clapMap.get(sellerUpdate.getClapCode()).add(sellerUpdate);
					return true;
				}
			}else{
				List<ClapSeller> newClapStore=new ArrayList<>();
				newClapStore.add(sellerUpdate);
				clapMap.put(sellerUpdate.getClapCode(), newClapStore);
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * 获取Seller Id
	 * @param ClapSeller
	 * @return Boolean
	 */
	private String getUserId(ClapSeller seller){
		if(seller!=null&&seller.getVid()!=null){
			return MessageDef.USER_TYPE.MERCHANT_STRING + seller.getVid();
		}
		return null;
	}
	
	/**
	 * 获取Seller 在E-pay中展示的名字。
	 * 名字规则为 Name1 + LastName1，第一个名字加第一个姓氏。
	 * @param ClapSeller
	 * @return String
	 */
	private String getUserName(ClapSeller seller) {
		String names = null;
		if (seller.getName1() != null && !"".equals(seller.getName1())) {
			names = seller.getName1();
		}
		if (seller.getLastName1() != null && !"".equals(seller.getLastName1())) {
			names += " " + seller.getLastName1();
		}
		if (names == null) {
			if (seller.getName2() != null && !"".equals(seller.getName2())) {
				names = seller.getName2() + " " + seller.getName2();
			}
			if (seller.getLastname2() != null && !"".equals(seller.getLastname2())) {
				names += " " + seller.getLastname2();
			}
		}
		return names;
	}
	

/***Seller functions End***/	
	
	
/***Distribution functions start***/
	
	/**
	 * 券分发文件入库
	 * @return
	 */
	private boolean disFileToDB() {
		BufferedReader reader=null;
		try {
			File file =getDistributionFile();   
			if (file.exists() && file.isFile()) {// 判断文件是否存在
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
				reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// 用5M的缓冲读取文本文件
				String line = reader.readLine();
				if (line != null) {// 文件头是否未空
					ClapDistributionHeader distributionUpdateHeader=new ClapDistributionHeader(line);
					if (distributionUpdateHeader != null && distributionUpdateHeader.getTotalFileRecord() != null) {
						long readCount=1;
						boolean isSucc=true;
						while ((line = reader.readLine()) != null) {
							
							ClapDistribution distribution=new ClapDistribution(line);
							if (distribution != null && distribution.getUniqueCode() != null&& distribution.getStatus() != null) {
								switch (distribution.getStatus()) {
								case MessageDef.CLAP_DISTRIBUTION.ADD:
									isSucc=addDistributionToDB(distribution);
									if(!isSucc){
										TASK_LOG.INFO(String.format("%s add Distribution failed, s%", key,line));
										continue;
									}
									break;
								case MessageDef.CLAP_DISTRIBUTION.UPDATE:
									isSucc=updateDistributionToDB(distribution);
									if(!isSucc){
										TASK_LOG.INFO(String.format("%s update Distribution  failed s%", key,line));
										continue;
									}
									break;
								case MessageDef.CLAP_DISTRIBUTION.NULLED:
									isSucc=nulledDistributionToDB(distribution);
									if(!isSucc){
										TASK_LOG.INFO(String.format("%s nulled Distribution failed s%", key,line));
										continue;
									}
									break;
								default:
									break;
								}
							}
							
							readCount++;	
						}
						TASK_LOG.INFO(String.format("%s read file %s finish, success record --%d/%s--", key,file.getName(),(readCount-1),distributionUpdateHeader.getTotalFileRecord()));
					}else{
						TASK_LOG.INFO(String.format("%s update Distribution failed, header is null", key));
						return false;
					}
				}else{
					TASK_LOG.INFO(String.format("%s update Distribution failed, read header is null", key));
					return false;
				}
			}else{
				TASK_LOG.INFO(String.format("%s update Distribution failed, file is null", key));
				return false;
			}
		} catch (IOException e) {
			log.error(String.format("%s Read Distribution file failed", key), e);
			TASK_LOG.ERROR(String.format("%s Read Distribution file failed", key), e);
			return false;
		}finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					log.error(String.format("%s Reader close exceptions", key), e1);
				}
			}
		}
		
		return true;
	}
	
	
	/**
	 * 取消券更新入库
	 * @param distribution
	 * @return
	 */
	private boolean nulledDistributionToDB(ClapDistribution distribution) {
		if(distribution!=null&&distribution.getUniqueCode()!=null){
			try {
				Coupon existCoupon=couponService.selectByPrimaryKey(distribution.getUniqueCode());
				if(existCoupon!=null){
					existCoupon.setStatus(1);
					couponService.updateWithFamilyCoupon(existCoupon);
				}
			} catch (Exception e) {
				log.error(String.format("%s nulledDistributionToDB failed", key), e);
				TASK_LOG.ERROR(String.format("%s nulledDistributionToDB failed", key), e);
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 更新券入库
	 * @param distribution
	 * @return
	 */
	private boolean updateDistributionToDB(ClapDistribution distribution) {
		if(distribution!=null&&distribution.getUniqueCode()!=null){
			try {
				Coupon existCoupon=couponService.selectByPrimaryKey(distribution.getUniqueCode());
				if(existCoupon!=null){
					existCoupon.setUserEndTime(DateUtil.convertStringToDate(distribution.getEndTime()+" 23:59:59", DateUtil.DATE_FORMAT_EIGHT));
					couponService.updateByPrimaryKey(existCoupon);
				}
			} catch (Exception e) {
				log.error(String.format("%s updateDistributionToDB failed", key), e);
				TASK_LOG.ERROR(String.format("%s updateDistributionToDB failed", key), e);
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * 新增券入库
	 * @param distribution
	 * @return
	 */
	private boolean addDistributionToDB( ClapDistribution distribution) {
		if(distribution!=null&&distribution.getClapStoreCode()!=null){
			Coupon toDBcoupon =getCoupon(distribution);//新增券
			List<ConsumerFamilyCoupon> toDBConsumerFamilyCouponList=new ArrayList<>();//券family列表
			int start = 0;
			int JOB_CLAP_DISTRIBUTION_SELECT_BUYER_ONETIME = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.JOB_CLAP_DISTRIBUTION_SELECT_BUYER_ONETIME);
			try {
				Set<String> buyerfamilySet = new HashSet<>();
				while (true) {
					//2017-12-11 逻辑修改
					int pageNum = PageHelperUtil.getPageNum(String.valueOf(start),String.valueOf(JOB_CLAP_DISTRIBUTION_SELECT_BUYER_ONETIME));
					int pageSize = PageHelperUtil.getPageSize(String.valueOf(start),String.valueOf(JOB_CLAP_DISTRIBUTION_SELECT_BUYER_ONETIME));
					PageHelper.startPage(pageNum, pageSize);
					List<ConsumerUserClap> consumerUserClapList = consumerUserClapService.selectByClapStoreNo(toDBcoupon.getConsumerGroupId());
					if (consumerUserClapList != null&&consumerUserClapList.size()>0){
						for (ConsumerUserClap consumerUserClap : consumerUserClapList) {
							buyerfamilySet.add(consumerUserClap.getFamilyId());
						}
						start+=JOB_CLAP_DISTRIBUTION_SELECT_BUYER_ONETIME;
					}
					
					if(consumerUserClapList == null||consumerUserClapList.size()!=JOB_CLAP_DISTRIBUTION_SELECT_BUYER_ONETIME){
						if(start==0){
							TASK_LOG.INFO(String.format("%s addNewDistributionToDB failed ,consumerUserClapList is null", key));
						}
						break;
					}
					
				}
				getConsumerFamilyCouponList(toDBConsumerFamilyCouponList, buyerfamilySet, toDBcoupon);
				if (toDBConsumerFamilyCouponList != null && toDBConsumerFamilyCouponList.size() > 0&& toDBcoupon != null) {
					couponService.insertCouponFamilyCoupon(toDBcoupon, toDBConsumerFamilyCouponList);
					return true;
				}
			} catch (Exception e) {
				log.error(String.format("%s addNewDistributionToDB failed", key), e);
				TASK_LOG.ERROR(String.format("%s addNewDistributionToDB failed", key), e);
			}
		}
		return false;
		
	}

	/**
	 * 获取用户家庭券列表
	 * @param toDBConsumerFamilyCouponList
	 * @param buyerfamilySet
	 * @param coupon
	 */
	private void getConsumerFamilyCouponList(List<ConsumerFamilyCoupon> toDBConsumerFamilyCouponList,Set<String> buyerfamilySet,Coupon coupon) {
		if (coupon != null) {
			if (buyerfamilySet != null && buyerfamilySet.size() > 0) {
				for (String buyerfamilId : buyerfamilySet) {
					ConsumerFamilyCoupon consumerFamilyCoupon = new ConsumerFamilyCoupon();
					consumerFamilyCoupon.setFamilyId(buyerfamilId);
					consumerFamilyCoupon.setCouponId(coupon.getCouponId());
					consumerFamilyCoupon.setStatus(0);
					toDBConsumerFamilyCouponList.add(consumerFamilyCoupon);
				}
			}
		}
		
	}

	/**
	 * 获取新的券
	 * @param distribution
	 * @return
	 */
	private Coupon getCoupon(ClapDistribution distribution) {
		Coupon coupon =new Coupon();
		if(distribution.getUniqueCode()!=null){
			coupon.setCouponId(distribution.getUniqueCode());
		}
		if(distribution.getAmount()!=null){
			double amount=getMoney(distribution.getAmount());
			coupon.setAmount(amount);
		}
		if(distribution.getName()!=null){
			coupon.setName(distribution.getName());
		}
		if(distribution.getStartTime()!=null){
			coupon.setUserStartTime(DateUtil.convertStringToDate(distribution.getStartTime(), DateUtil.DATE_FORMAT_TWO));
		}
		if(distribution.getEndTime()!=null){
			coupon.setUserEndTime(DateUtil.convertStringToDate(distribution.getEndTime()+" 23:59:59", DateUtil.DATE_FORMAT_EIGHT));
		}
		coupon.setConsumerGroupId(distribution.getClapStoreCode());
		coupon.setCreateTime(PageHelperUtil.getCurrentDate());
		coupon.setType(2);//Distribution
		coupon.setGetLimit(2);//家庭（store）
		coupon.setStatus(0);//启用
		coupon.setGetType(2);//发放
		coupon.setGrandType(1);//已发放
		return coupon;
	}

	/**
	 * 券金额规整
	 * @param amount
	 * @return
	 */
	private double getMoney(String amount) {
		if (amount != null && StringUtils.isNumeric(amount)) {
			String amountTmp = amount.substring(0, amount.length() - 2) + "." + amount.substring(amount.length() - 2);
			return Double.valueOf(amountTmp);
		}
		return 0;
	}
	

	/**
	 * 获取券文件
	 * @return
	 */
	private File getDistributionFile() {
		
		File file = new File(JOB_CLAP_DOWNLOAD_FILE_PATH+disFileName);   
		return file;
	}
	/***Distribution functions End***/	
	
	
	
	/***SellerAccount functions Start***/	
	
	
	/**
	 * 商户银行卡绑定入库方法
	 * @return
	 */
	private boolean sellerAccountFileToDB() {
		BufferedReader reader=null;
		try {
			File file =getSellerAccountFile();   
			if (file.exists() && file.isFile()) {// 判断文件是否存在
				BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
				reader= new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// 用5M的缓冲读取文本文件
				String line = reader.readLine();
				if (line != null) {// 文件头是否未空
					ClapSellerAccountHeader clapSellerAccountHeader=new ClapSellerAccountHeader(line);
					if (clapSellerAccountHeader != null && clapSellerAccountHeader.getTotalFileRecord() != null) {
						long readCount=1;
						boolean isSucc=true;
						while ((line = reader.readLine()) != null) {
							
							ClapSellerAccount clapSellerAccount=new ClapSellerAccount(line);
							if (clapSellerAccount != null && clapSellerAccount.getClapStoreNo() != null&& clapSellerAccount.getStatus() != null) {
								if( MessageDef.CLAP_SELLER_ACCOUNT.DELETE.equals(clapSellerAccount.getStatus())){//逻辑删除
									isSucc=deleteSellerAccountToDB(clapSellerAccount);
									if(!isSucc){
										TASK_LOG.INFO(String.format("%s delete SellerAccount The [%d] failed", key,readCount));
									}
								}else{//新增、更新
									isSucc=addUpdateSellerAccountToDB(clapSellerAccount);
									if(!isSucc){
										TASK_LOG.INFO(String.format("%s addUpdate SellerAccount [%d] failed", key,readCount));
									}
								}
							}else{
								TASK_LOG.INFO(String.format("%s SellerAccount is Null, [%d] failed", key,readCount));
							}
							
							readCount++;	
						}
						TASK_LOG.INFO(String.format("%s read file %s finish, success record --%d/%s--", key,file.getName(),(readCount-1),clapSellerAccountHeader.getTotalFileRecord()));
					}else{
						TASK_LOG.INFO(String.format("%s update SellerAccount failed, header is null", key));
						return false;
					}
				}else{
					TASK_LOG.INFO(String.format("%s update SellerAccount failed, read header is null", key));
					return false;
				}
			}else{
				TASK_LOG.INFO(String.format("%s update SellerAccount failed, file is null", key));
				return false;
			}
		} catch (IOException e) {
			log.error(String.format("%s Read SellerAccount file failed", key), e);
			TASK_LOG.ERROR(String.format("%s Read SellerAccount file failed", key), e);
			return false;
		}finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		
		return true;
	
	}

	/**
	 * 商户银行卡信息 新增/更新入库方法
	 * @param clapSellerAccount
	 * @return
	 */
	private boolean addUpdateSellerAccountToDB(ClapSellerAccount clapSellerAccount) {
		if(clapSellerAccount!=null&&clapSellerAccount.getClapStoreNo()!=null){
			try {
				MerchantUser merchantUserResult=merchantUserService.selectByClapId(clapSellerAccount.getClapStoreNo());
				if(merchantUserResult!=null&&merchantUserResult.getUserId()!=null){
					MerchantUserBankCard merchantUserBankCard=new MerchantUserBankCard();
					merchantUserBankCard.setUserId(merchantUserResult.getUserId());
					merchantUserBankCard.setIdNumber(clapSellerAccount.getVid());
					merchantUserBankCard.setHolerName(clapSellerAccount.getHolderName());
					merchantUserBankCard.setType(Integer.valueOf(clapSellerAccount.getBankAccountType()));
					merchantUserBankCard.setBankId(clapSellerAccount.getBankId());
					merchantUserBankCard.setCardNo(clapSellerAccount.getBankAccountNo());
					merchantUserBankCard.setClapStoreNo(clapSellerAccount.getClapStoreNo());
					merchantUserBankCard.setDeleteFlag(0);
					
					MerchantUserBankCard result=merchantUserBankCardService.selectByClapStoreCardNo(merchantUserBankCard);
					if((clapSellerAccount.getStatus()!=null&&MessageDef.CLAP_SELLER_ACCOUNT.UPDATE.equals(clapSellerAccount.getStatus()))||(result!=null)){//更新
						merchantUserBankCardService.updateBySelective(merchantUserBankCard);
					}else{//新增
						merchantUserBankCard.setBankBindId(IDGenerate.generateCommOne(IDGenerate.MERCHANT_USER_BANK_CARD));
						merchantUserBankCardService.insert(merchantUserBankCard);
					}
					
				}else{
					TASK_LOG.INFO(String.format("%s addUpdateSellerAccountToDB failed, merchantUserResult is null", key));
				}
				return true;
			} catch (Exception e) {
				log.error(String.format("%s deleteSellerAccount ToDB failed", key), e);
				TASK_LOG.ERROR(String.format("%s deleteSellerAccount ToDB file failed", key), e);
				return false;
			}
		}else{
			TASK_LOG.INFO(String.format("%s addUpdateSellerAccountToDB failed, clapSellerAccount is null", key));
		}
		return false;
	}
	/**
	 * 商户银行卡 删除入库
	 * @param clapSellerAccount
	 * @return
	 */
	private boolean deleteSellerAccountToDB(ClapSellerAccount clapSellerAccount) {
		if(clapSellerAccount!=null&&clapSellerAccount.getClapStoreNo()!=null){
			try {
					MerchantUserBankCard merchantUserBankCardUpdate=new MerchantUserBankCard();
					merchantUserBankCardUpdate.setCardNo(clapSellerAccount.getBankAccountNo());
					merchantUserBankCardUpdate.setClapStoreNo(clapSellerAccount.getClapStoreNo());
					merchantUserBankCardUpdate.setDeleteFlag(1);
					merchantUserBankCardService.updateBySelective(merchantUserBankCardUpdate);
				return true;
			} catch (Exception e) {
				log.error(String.format("%s deleteSellerAccount ToDB failed", key), e);
				TASK_LOG.ERROR(String.format("%s deleteSellerAccount ToDB file failed", key), e);
				return false;
			}
		}else{
			TASK_LOG.INFO(String.format("%s deleteSellerAccountToDB failed, clapSellerAccount is null", key));
		}
		return false;
	}

	/**
	 * 商户银行卡文件
	 * @return
	 */
	private File getSellerAccountFile() {
		File file = new File(JOB_CLAP_DOWNLOAD_FILE_PATH+sellerAccountFileName);   
		return file;
	}

	/***SellerAccount functions Start***/	
}
