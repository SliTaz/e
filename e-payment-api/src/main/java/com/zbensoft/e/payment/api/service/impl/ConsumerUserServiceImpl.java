package com.zbensoft.e.payment.api.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtTokenUtil;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtUser;
import com.zbensoft.e.payment.api.exception.RegisterApiFailException;
import com.zbensoft.e.payment.api.exception.RegisterApiReponseBodyNotExistException;
import com.zbensoft.e.payment.api.exception.RegisterApiReponseFormatErrorException;
import com.zbensoft.e.payment.api.exception.RegisterApiReponseIsNullException;
import com.zbensoft.e.payment.api.exception.RegisterApiReponseStatesNotSuccException;
import com.zbensoft.e.payment.api.log.TASK_LOG;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIRegisterUpdateWalletResponse;
import com.zbensoft.e.payment.api.vo.api.buyerRegister.APIRequest;
import com.zbensoft.e.payment.api.vo.buyerRegister.RegisterRequest;
import com.zbensoft.e.payment.api.vo.buyerRegister.RegisterResponse;
import com.zbensoft.e.payment.api.vo.clap.ClapBuyer;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerFamily;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.Coupon;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyCouponMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerRoleUserMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerUserClapMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerUserMapper;
import com.zbensoft.e.payment.db.mapper.CouponMapper;
import com.zbensoft.e.payment.db.mapper.MerchantUserMapper;

@Service
public class ConsumerUserServiceImpl implements ConsumerUserService {

	private static final Logger log = LoggerFactory.getLogger(ConsumerUserServiceImpl.class);

	@Autowired
	ConsumerUserMapper consumerUserMapper;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	@Autowired
	SysRoleUserService sysRoleUserService;

	@Autowired
	ConsumerUserClapMapper consumerUserClapMapper;

	@Autowired
	ConsumerRoleUserMapper consumeRoleUserMapper;

	@Autowired
	ConsumerRoleUserMapper consumerRoleUserMapper;
	@Autowired
	ConsumerFamilyMapper consumerFamilyMapper;
	@Autowired
	MerchantUserMapper merchantUserMapper;
	@Autowired
	CouponMapper couponMapper;
	@Autowired
	ConsumerFamilyCouponMapper consumerFamilyCouponMapper;

	@Autowired
	SqlSessionTemplate sqlSessionTemplate;

	private String tokenHead = MessageDef.LOGIN.TOKEN_HEAD;

	// @CacheEvict(value="demoInfo",key="#userId")
	@Override
	public int deleteByPrimaryKey(String userId) {
		return consumerUserMapper.deleteByPrimaryKey(userId);
	}

	// @CacheEvict(value="demoInfo",key="#record.userId")
	@Override
	public int insert(ConsumerUser record) {
		return consumerUserMapper.insert(record);
	}

	// @CacheEvict(value="demoInfo",key="#record.userId")
	@Override
	public int insertSelective(ConsumerUser record) {
		return consumerUserMapper.insertSelective(record);
	}

	@Override
	public int count(ConsumerUser consumerUser) {
		return consumerUserMapper.count(consumerUser);
	}

	// @Cacheable(value="demoInfo",key="#userId")
	@Override
	public ConsumerUser selectByPrimaryKey(String userId) {
		return consumerUserMapper.selectByPrimaryKey(userId);
	}

	// @CacheEvict(value="demoInfo",key="#record.userId")
	@Override
	public int updateByPrimaryKeySelective(ConsumerUser record) {
		return consumerUserMapper.updateByPrimaryKeySelective(record);
	}

	// @CacheEvict(value="demoInfo",key="#record.userId")
	@Override
	public int updateByPrimaryKey(ConsumerUser record) {
		return consumerUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<ConsumerUser> selectPage(ConsumerUser record) {
		return consumerUserMapper.selectPage(record);
	}

	@Override
	public boolean isConsumerUserExist(ConsumerUser consumerUser) {
		return selectByUserName(consumerUser.getUserName()) != null;
	}

	@Override
	public ConsumerUser selectByUserName(String userName) {
		return consumerUserMapper.selectByUserName(userName);
	}

	@Override
	public JwtAuthenticationResponse login(String userName, String password) throws Exception {
		try {
			UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(userName, password);
			// Perform the security
			final Authentication authentication = authenticationManager.authenticate(upToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Reload password post-security so we can generate token
			final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			final String token = jwtTokenUtil.generateToken(userDetails, userName);

			JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse(tokenHead + token, ((JwtUser) userDetails).getId(), ((JwtUser) userDetails).getUsername(), userName,
					((JwtUser) userDetails).getIsDefaultPassword(), ((JwtUser) userDetails).getIsDefaultPayPassword(), ((JwtUser) userDetails).getEmailBindStatus(), ((JwtUser) userDetails).getAuthorities(),
					(JwtUser) userDetails);
			jwtAuthenticationResponse.setIdNumber(((JwtUser) userDetails).getIdNumber());
			return jwtAuthenticationResponse;
		} catch (BadCredentialsException be) {
			throw be;
		} catch (UsernameNotFoundException ue) {
			log.warn("login exception", ue);
			throw ue;
		} catch (Exception e) {
			log.error("login exception", e);
			throw e;
		}
	}

	@Override
	public Double queryConsumerBalance(String userName) {
		return consumerUserMapper.queryConsumerBalance(userName);
	}

	@Override
	public int updateAmountByPrimaryKey(ConsumerUser consumerUser) {
		return consumerUserMapper.updateAmountByPrimaryKey(consumerUser);
	}

	@Override
	public int activateByTime(ConsumerUser record) {
		return consumerUserMapper.activateByTime(record);
	}

	@Override
	public Double selectSumBalance() {
		return consumerUserMapper.selectSumBalance();
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void updateUserTransactional(ConsumerUser consumerUserNew, ConsumerUserClap consumerUserClapNew, ConsumerRoleUserKey consumerRoleUserKeyNew, ClapBuyer buyer) {
		consumerUserMapper.updateByPrimaryKeySelective(consumerUserNew);

		ConsumerRoleUserKey resultKey = consumerRoleUserMapper.selectByPrimaryKey(consumerRoleUserKeyNew);
		if (resultKey == null) {
			consumerRoleUserMapper.insert(consumerRoleUserKeyNew);
		}

		// 新增或更新family
		ConsumerUserClap consumerUserClapTmp = consumerUserClapMapper.selectByPrimaryKey(MessageDef.USER_TYPE.CONSUMER_STRING + buyer.getVid());
		ConsumerFamily consumerFamilyNew = new ConsumerFamily();

		if (consumerUserClapTmp != null && consumerUserClapTmp.getFamilyId() != null) {
			ConsumerFamily consumerFamily = consumerFamilyMapper.selectByPrimaryKey(consumerUserClapTmp.getFamilyId());
			if (consumerFamily == null) {
				consumerFamilyNew.setFamilyId(buyer.getFamilyCode());
				consumerFamilyNew.setName(buyer.getFamilyCode());
				consumerFamilyNew.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
				consumerFamilyMapper.insert(consumerFamilyNew);

				// 新增券处理
				Date dateSer = Calendar.getInstance().getTime();
				Coupon couponSer = new Coupon();
				couponSer.setConsumerGroupId(buyer.getClapCode());
				couponSer.setCurrentTime(DateUtil.convertDateToString(dateSer, DateUtil.DATE_FORMAT_FIVE));
				List<Coupon> couponList = couponMapper.selectAvailableCoupon(couponSer);
				if (couponList != null && couponList.size() > 0) {
					for (Coupon coupon : couponList) {
						ConsumerFamilyCoupon consumerFamilyCoupon = new ConsumerFamilyCoupon();
						consumerFamilyCoupon.setCouponId(coupon.getCouponId());
						consumerFamilyCoupon.setFamilyId(consumerFamilyNew.getFamilyId());
						consumerFamilyCoupon.setStatus(0);
						consumerFamilyCouponMapper.insert(consumerFamilyCoupon);
					}
				}

			} else {
				if (!buyer.getFamilyCode().equals(consumerFamily.getFamilyId())) {
					consumerFamily.setDeleteFlag(MessageDef.DELETE_FLAG.DELETED);
					consumerFamilyMapper.updateByPrimaryKeySelective(consumerFamily);
					consumerFamilyNew.setFamilyId(buyer.getFamilyCode());
					consumerFamilyNew.setName(buyer.getFamilyCode());
					consumerFamilyNew.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
					consumerFamilyMapper.insert(consumerFamilyNew);

					// 如果familyId 更新 增更新consumerFamilyCoupon
					List<ConsumerFamilyCoupon> consumerFamilyCouponList = consumerFamilyCouponMapper.selectByFamilyId(consumerFamily.getFamilyId());
					if (consumerFamilyCouponList != null && consumerFamilyCouponList.size() > 0 && consumerFamilyNew != null) {
						for (ConsumerFamilyCoupon consumerFamilyCoupon : consumerFamilyCouponList) {
							if (consumerFamilyCoupon.getStatus() == 0) {
								consumerFamilyCouponMapper.deleteByPrimaryKey(consumerFamilyCoupon);
								ConsumerFamilyCoupon newConsumerFamilyCoupon = new ConsumerFamilyCoupon();
								newConsumerFamilyCoupon.setCouponId(consumerFamilyCoupon.getCouponId());
								newConsumerFamilyCoupon.setFamilyId(consumerFamilyNew.getFamilyId());
								newConsumerFamilyCoupon.setStatus(0);
								consumerFamilyCouponMapper.insert(newConsumerFamilyCoupon);
							}
						}
					}

				} else {
					consumerFamily.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
					consumerFamilyMapper.updateByPrimaryKeySelective(consumerFamily);
					// 更新该家庭对应的券
					List<ConsumerFamilyCoupon> consumerFamilyCouponList = consumerFamilyCouponMapper.selectByFamilyId(consumerFamily.getFamilyId());
					Date dateSer = Calendar.getInstance().getTime();
					Coupon couponSer = new Coupon();
					couponSer.setConsumerGroupId(buyer.getClapCode());
					couponSer.setCurrentTime(DateUtil.convertDateToString(dateSer, DateUtil.DATE_FORMAT_FIVE));
					List<Coupon> couponList = couponMapper.selectAvailableCoupon(couponSer);
					if (couponList != null && couponList.size() > 0) {
						for (Coupon coupon : couponList) {

							if (isConsumerFamilyCouponExist(coupon, consumerFamilyCouponList)) {// 判断家庭券是否已经存在
								continue;
							}

							ConsumerFamilyCoupon consumerFamilyCoupon = new ConsumerFamilyCoupon();
							consumerFamilyCoupon.setCouponId(coupon.getCouponId());
							consumerFamilyCoupon.setFamilyId(consumerFamily.getFamilyId());
							consumerFamilyCoupon.setStatus(0);
							consumerFamilyCouponMapper.insert(consumerFamilyCoupon);
						}
					}
				}

			}
			// 更新 ConsumerUser Clap
			consumerUserClapMapper.updateByPrimaryKeySelective(consumerUserClapNew);
		} else {// 没有clap数据
				// 新增或更新family
			ConsumerFamily consumerFamily = consumerFamilyMapper.selectByPrimaryKey(buyer.getFamilyCode());
			if (consumerFamily == null) {
				consumerFamilyNew.setFamilyId(buyer.getFamilyCode());
				consumerFamilyNew.setName(buyer.getFamilyCode());
				consumerFamilyNew.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
				consumerFamilyMapper.insert(consumerFamilyNew);
				// 新增券处理
				Date dateSer = Calendar.getInstance().getTime();
				Coupon couponSer = new Coupon();
				couponSer.setConsumerGroupId(buyer.getClapCode());
				couponSer.setCurrentTime(DateUtil.convertDateToString(dateSer, DateUtil.DATE_FORMAT_FIVE));
				List<Coupon> couponList = couponMapper.selectAvailableCoupon(couponSer);
				if (couponList != null && couponList.size() > 0) {
					for (Coupon coupon : couponList) {
						ConsumerFamilyCoupon consumerFamilyCoupon = new ConsumerFamilyCoupon();
						consumerFamilyCoupon.setCouponId(coupon.getCouponId());
						consumerFamilyCoupon.setFamilyId(consumerFamilyNew.getFamilyId());
						consumerFamilyCoupon.setStatus(0);

						consumerFamilyCouponMapper.insert(consumerFamilyCoupon);
					}
				}

			} else {
				consumerFamily.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
				consumerFamilyMapper.updateByPrimaryKeySelective(consumerFamily);
				TASK_LOG.INFO("----->####<-----There are more than one user in this family, VId: " + consumerUserClapNew.getIdNumber() + " Family ID" + consumerFamily.getFamilyId());
			}
			// 新增clap数据
			consumerUserClapMapper.insert(consumerUserClapNew);
		}

	}

	private boolean isConsumerFamilyCouponExist(Coupon coupon, List<ConsumerFamilyCoupon> consumerFamilyCouponList) {
		if (consumerFamilyCouponList != null && consumerFamilyCouponList.size() > 0) {
			for (ConsumerFamilyCoupon consumerFamilyCouponTmp : consumerFamilyCouponList) {
				if (consumerFamilyCouponTmp.getCouponId().equals(coupon.getCouponId())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void insertUserTransactional(ConsumerUser consumerUserNew, ConsumerUserClap consumerUserClapNew, ConsumerRoleUserKey consumerRoleUserKeyNew, ClapBuyer buyer) {
		consumerUserMapper.insertSelective(consumerUserNew);
		ConsumerRoleUserKey resultKey = consumerRoleUserMapper.selectByPrimaryKey(consumerRoleUserKeyNew);
		if (resultKey == null) {
			consumerRoleUserMapper.insert(consumerRoleUserKeyNew);
		}

		// 新增或更新family
		ConsumerFamily consumerFamilyNew = new ConsumerFamily();
		ConsumerFamily consumerFamily = consumerFamilyMapper.selectByPrimaryKey(buyer.getFamilyCode());
		if (consumerFamily == null) {
			consumerFamilyNew.setFamilyId(buyer.getFamilyCode());
			consumerFamilyNew.setName(buyer.getFamilyCode());
			consumerFamilyNew.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
			consumerFamilyMapper.insert(consumerFamilyNew);
			// 新增券处理
			CommonFun.addNewConsumerFamilyCoupon(couponMapper,consumerFamilyCouponMapper,buyer.getClapCode(),consumerFamilyNew.getFamilyId());
			

		} else {
			consumerFamily.setDeleteFlag(MessageDef.DELETE_FLAG.UNDELETE);
			consumerFamilyMapper.updateByPrimaryKeySelective(consumerFamily);
			TASK_LOG.INFO("----->####<-----There are more than one user in this family, VId: " + consumerUserClapNew.getIdNumber() + " Family ID" + consumerFamily.getFamilyId());
		}
		consumerUserClapMapper.insert(consumerUserClapNew);
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void insertList(List<ConsumerUser> consumerUserList, List<ConsumerUserClap> consumerUserClapList, List<ConsumerFamily> consumerFamilyList, List<ConsumerRoleUserKey> consumerRoleUserKeyList) {

		SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
		try {
			if (consumerUserList != null && consumerUserList.size() > 0) {
				ConsumerUserMapper onsumerUserMapper = session.getMapper(ConsumerUserMapper.class);
				for (int index = 0; index < consumerUserList.size(); index++) {
					ConsumerUser object = consumerUserList.get(index);
					onsumerUserMapper.insertSelective(object);
				}
			}
			if (consumerRoleUserKeyList != null && consumerRoleUserKeyList.size() > 0) {
				ConsumerRoleUserMapper consumerRoleUserMapper = session.getMapper(ConsumerRoleUserMapper.class);
				for (int index = 0; index < consumerRoleUserKeyList.size(); index++) {
					ConsumerRoleUserKey object = consumerRoleUserKeyList.get(index);
					consumerRoleUserMapper.insert(object);
				}
			}

			if (consumerUserClapList != null && consumerUserClapList.size() > 0) {
				ConsumerUserClapMapper consumerUserClapMapper = session.getMapper(ConsumerUserClapMapper.class);
				for (int index = 0; index < consumerUserClapList.size(); index++) {
					ConsumerUserClap object = consumerUserClapList.get(index);
					consumerUserClapMapper.insert(object);
				}
			}

			if (consumerFamilyList != null && consumerFamilyList.size() > 0) {
				ConsumerFamilyMapper consumerFamilyMapper = session.getMapper(ConsumerFamilyMapper.class);
				for (int index = 0; index < consumerFamilyList.size(); index++) {
					ConsumerFamily object = consumerFamilyList.get(index);
					consumerFamilyMapper.insert(object);
				}
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

	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void updateList(List<ConsumerUser> consumerUserList) {
		SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
		try {
			if (consumerUserList != null && consumerUserList.size() > 0) {
				ConsumerUserMapper onsumerUserMapper = session.getMapper(ConsumerUserMapper.class);
				for (int index = 0; index < consumerUserList.size(); index++) {
					ConsumerUser consumerUserUp = consumerUserList.get(index);
					onsumerUserMapper.updateByPrimaryKeySelective(consumerUserUp);
				}
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

	}

	@Override
	@Transactional(value = "DataSourceManager")
	public ResponseRestEntity<RegisterResponse> registerSucc(RegisterRequest registerRequest, CloseableHttpClient httpClient, RequestConfig config, String URL_REGISTER_REGISTER_UPDATE_WALLET) throws Exception {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String userId = MessageDef.USER_TYPE.CONSUMER_STRING + registerRequest.getIdNumber();
		ConsumerUser consumerUser = new ConsumerUser();

		// 名称
		consumerUser.setUserId(userId);

		consumerUser.setUserName(registerRequest.getR_n1());
		if (registerRequest.getR_ap2() != null && registerRequest.getR_ap2().length() > 0) {
			consumerUser.setUserName(consumerUser.getUserName() + " " + registerRequest.getR_ap1());
		}

		// 邮箱
		consumerUser.setEmail(registerRequest.getR_mail());
		consumerUser.setEmailBindStatus(MessageDef.EMAIL_BIND_STATUS.BIND);

		// 密码
		consumerUser.setPassword(encoder.encode(registerRequest.getLoginPassword()));
		consumerUser.setIsDefaultPassword(MessageDef.DEFAULT_LOGIN_PASSWORD.NOT_DEFUALT);

		consumerUser.setPayPassword(encoder.encode(registerRequest.getPaymentPassword()));
		consumerUser.setIsDefaultPayPassword(MessageDef.DEFAULT_PAY_PASSWORD.NOT_DEFUALT);
		consumerUser.setIsFirstLogin(MessageDef.FIRST_LOGIN.NOT_FIRST);

		consumerUser.setBalance(0);// 余额为0
		consumerUser.setStatus(MessageDef.STATUS.ENABLE_INT);// 状态为启用
		consumerUser.setIsLocked(MessageDef.LOCKED.UNLOCKED);// 未冻结
		consumerUser.setIsBindClap(MessageDef.CLAP_BIND_STATUS.BIND);// 已绑定
		consumerUser.setIsBindBankCard(MessageDef.BANK_CARD_BIND_STATUS.UN_BIND);// 未绑定
		consumerUser.setIsActive(MessageDef.CONSUMER_IS_ACTIVE.ACTIVATE);

		consumerUser.setCreateTime(new Date());
		consumerUser.setRemark("Register");
		consumerUserMapper.insert(consumerUser);

		ConsumerRoleUserKey consumerRoleUserKeyTmp = new ConsumerRoleUserKey();
		consumerRoleUserKeyTmp.setUserId(userId);
		consumerRoleUserKeyTmp.setRoleId("1");
		ConsumerRoleUserKey consumeRoleUser = consumerRoleUserMapper.selectByPrimaryKey(consumerRoleUserKeyTmp);
		if (consumeRoleUser == null) {
			consumeRoleUserMapper.insert(consumerRoleUserKeyTmp);
		}

		ConsumerUserClap consumerUserClap = new ConsumerUserClap();
		consumerUserClap.setConsumerUserClapId(userId);
		consumerUserClap.setUserId(userId);
		consumerUserClap.setIdNumber(CommonFun.getRelVid(registerRequest.getIdNumber()));
		consumerUserClap.setStatus(MessageDef.CONSUMER_USER_CLAP_STATUS.NORMAL);
		consumerUserClap.setName1(registerRequest.getR_n1());
		consumerUserClap.setName2(registerRequest.getR_n2());
		consumerUserClap.setLastName1(registerRequest.getR_ap1());
		consumerUserClap.setLastName2(registerRequest.getR_ap2());
		if ("F".equalsIgnoreCase(registerRequest.getR_gen())) {
			consumerUserClap.setSex(MessageDef.USER_SEX.FEMALE);
		} else if ("M".equalsIgnoreCase(registerRequest.getR_gen())) {
			consumerUserClap.setSex(MessageDef.USER_SEX.MALE);
		}
		
		if (registerRequest.getR_fnac() != null) {
			consumerUserClap.setDatebirth(DateUtil.convertStringToDate(registerRequest.getR_fnac(), DateUtil.DATE_FORMAT_ONE));
		}
		 
		consumerUserClap.setClapSeqNo(registerRequest.getR_ser());
		consumerUserClap.setClapNo(registerRequest.getPatrimonyCardCode());

		consumerUserClapMapper.insert(consumerUserClap);
		return register_update_wallet(registerRequest, httpClient, config, URL_REGISTER_REGISTER_UPDATE_WALLET);
	}

	private ResponseRestEntity<RegisterResponse> register_update_wallet(RegisterRequest registerRequest, CloseableHttpClient httpClient, RequestConfig config, String URL_REGISTER_REGISTER_UPDATE_WALLET)
			throws Exception {
		HttpPost httpPost = new HttpPost(URL_REGISTER_REGISTER_UPDATE_WALLET);
		httpPost.setConfig(config);
		APIRequest apiRequest = new APIRequest();
		String[] params = { registerRequest.getIdNumber(), registerRequest.getPatrimonyCardCode(), registerRequest.getR_mail(), "true" };
		apiRequest.setParams(params);
		JSONObject jsonParam = (JSONObject) JSONObject.toJSON(apiRequest);
		StringEntity stringEntity = new StringEntity(jsonParam.toString(), "utf-8");// 解决中文乱码问题
		stringEntity.setContentEncoding("UTF-8");
		stringEntity.setContentType("application/json");
		httpPost.setEntity(stringEntity);
		try {
			CloseableHttpResponse response = httpClient.execute(httpPost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String responseStr = EntityUtils.toString(entity, "UTF-8");
					if (responseStr != null && responseStr.length() > 0) {
						JSONObject jsonObjectResponse = new JSONObject();
						jsonObjectResponse = (JSONObject) jsonObjectResponse.parse(responseStr);
						APIRegisterUpdateWalletResponse apiRegisterUpdateWalletResponse = null;
						if (responseStr.contains("body")) {
							apiRegisterUpdateWalletResponse = JSONObject.toJavaObject(jsonObjectResponse.getJSONObject("body"), APIRegisterUpdateWalletResponse.class);
						} else {
							apiRegisterUpdateWalletResponse = JSONObject.toJavaObject(jsonObjectResponse, APIRegisterUpdateWalletResponse.class);
						}
						if (apiRegisterUpdateWalletResponse != null) {
							if ("3000".equals(apiRegisterUpdateWalletResponse.getCod())) {

								RegisterResponse registerResponse = new RegisterResponse();

								registerResponse.setIdNumber(registerRequest.getIdNumber());
								registerResponse.setPatrimonyCardCode(registerRequest.getPatrimonyCardCode());
								registerResponse.setImgCode(registerRequest.getImgCode());
								registerResponse.setImgValidateCode(registerRequest.getImgValidateCode());

								registerResponse.setR_cod(registerRequest.getR_cod());
								registerResponse.setR_ser(registerRequest.getR_ser());
								registerResponse.setR_stat(registerRequest.isR_stat());
								registerResponse.setR_ced(registerRequest.getR_ced());
								registerResponse.setR_n1(registerRequest.getR_n1());
								registerResponse.setR_n2(registerRequest.getR_n2());
								registerResponse.setR_ap1(registerRequest.getR_ap1());
								registerResponse.setR_ap2(registerRequest.getR_ap2());
								registerResponse.setR_gen(registerRequest.getR_gen());

								registerResponse.setR_fnac(registerRequest.getR_fnac());
								registerResponse.setR_mail(registerRequest.getR_mail());

								return new ResponseRestEntity<RegisterResponse>(registerResponse, HttpRestStatus.OK);
							} else {
								log.warn(responseStr);
								throw new RegisterApiReponseStatesNotSuccException();
							}
						} else {
							log.warn(responseStr);
							throw new RegisterApiReponseFormatErrorException();
						}
					} else {
						log.warn(responseStr);
						throw new RegisterApiReponseBodyNotExistException();
					}
				} else {
					throw new RegisterApiReponseIsNullException();
				}
			} finally {
				response.close();
			}
		} catch (Exception e) {
			log.error("register_update_wallet exception", e);
			throw new RegisterApiFailException();
		} finally {
		}

	}

}
