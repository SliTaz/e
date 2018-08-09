package com.zbensoft.e.payment.api.service.impl;

import java.util.Calendar;
import java.util.List;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtTokenUtil;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtUser;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.common.util.DateUtil;
import com.zbensoft.e.payment.db.domain.ConsumerCoupon;
import com.zbensoft.e.payment.db.domain.ConsumerFamilyCoupon;
import com.zbensoft.e.payment.db.domain.Coupon;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRoleRelKey;
import com.zbensoft.e.payment.db.domain.MerchantRoleUserKey;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.MerchantUserBankCard;
import com.zbensoft.e.payment.db.mapper.ConsumerCouponMapper;
import com.zbensoft.e.payment.db.mapper.ConsumerFamilyCouponMapper;
import com.zbensoft.e.payment.db.mapper.CouponMapper;
import com.zbensoft.e.payment.db.mapper.MerchantEmployeeMapper;
import com.zbensoft.e.payment.db.mapper.MerchantEmployeeRoleRelMapper;
import com.zbensoft.e.payment.db.mapper.MerchantRoleUserMapper;
import com.zbensoft.e.payment.db.mapper.MerchantUserBankCardMapper;
import com.zbensoft.e.payment.db.mapper.MerchantUserMapper;

@Service
public class MerchantUserServiceImpl implements MerchantUserService {

	private static final Logger log = LoggerFactory.getLogger(MerchantUserServiceImpl.class);

	@Autowired
	MerchantUserMapper merchantUserMapper;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	@Autowired
	SysRoleUserService sysRoleUserService;
	@Autowired
	MerchantRoleUserMapper merchantRoleUserMapper;
	@Autowired
	MerchantEmployeeMapper merchantEmployeeMapper;
	@Autowired
	MerchantEmployeeRoleRelMapper merchantEmployeeRoleRelMapper;
	@Autowired
	MerchantUserBankCardMapper merchantUserBankCardMapper;

	@Autowired
	CouponMapper couponMapper;
	@Autowired
	ConsumerFamilyCouponMapper consumerFamilyCouponMapper;
	@Autowired
	ConsumerCouponMapper consumerCouponMapper;

	@Autowired
	SqlSessionTemplate sqlSessionTemplate;

	private String tokenHead = MessageDef.LOGIN.TOKEN_HEAD;

	@Override
	public int deleteByPrimaryKey(String userId) {
		return merchantUserMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(MerchantUser record) {
		return merchantUserMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantUser record) {
		return merchantUserMapper.insertSelective(record);
	}

	@Override
	public int count(MerchantUser merchantUser) {
		return merchantUserMapper.count(merchantUser);
	}

	@Override
	public MerchantUser selectByPrimaryKey(String userId) {
		return merchantUserMapper.selectByPrimaryKey(userId);
	}

	@Override
	public MerchantUser selectByIdNumber(String idNumber) {
		return merchantUserMapper.selectByIdNumber(idNumber);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantUser record) {
		return merchantUserMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantUser record) {
		return merchantUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantUser> selectPage(MerchantUser record) {
		return merchantUserMapper.selectPage(record);
	}

	@Override
	public boolean isMerchantUserExist(MerchantUser merchantUser) {
		return selectByUserName(merchantUser.getUserName()) != null;
	}

	@Override
	public void deleteAll() {
		merchantUserMapper.deleteAll();
	}

	@Override
	public MerchantUser selectByUserName(String userName) {
		return merchantUserMapper.selectByUserName(userName);
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
			jwtAuthenticationResponse.setClapStoreNumber(((JwtUser) userDetails).getClapCardNo());
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
	public int updateAmountByPrimaryKey(MerchantUser merchantUser) {
		return merchantUserMapper.updateAmountByPrimaryKey(merchantUser);
	}

	@Override
	public Double queryMerchantBalance(String userName) {
		return merchantUserMapper.queryMerchantBalance(userName);
	}

	@Override
	public MerchantUser selectByClapId(String username) {
		return merchantUserMapper.selectByClapId(username);
	}

	@Override
	public int activateByTime(MerchantUser merchantUser) {
		return merchantUserMapper.activateByTime(merchantUser);
	}

	@Override
	public Double selectSumBalance() {
		return merchantUserMapper.selectSumBalance();
	}

	@Override
	@Transactional(value = "DataSourceManager")
	public void deleteAndInsert(MerchantUser merchantUser, MerchantUser merchantNewUser, List<MerchantEmployee> newEmployeeList, List<MerchantEmployee> deleteEmployeeList,
			List<ConsumerFamilyCoupon> toDBConsumerFamilyCouponList) {

		if (merchantNewUser != null) {// 插入新的Seller

			if (merchantUser != null) {// 删除原有Seller
				merchantUserMapper.deleteByPrimaryKey(merchantUser.getUserId());
				merchantRoleUserMapper.deleteByUserId(merchantUser.getUserId());

				// 删除这个seller下对应用户的券关系
				Coupon delCouponSer = new Coupon();
				delCouponSer.setConsumerGroupId(merchantUser.getClapStoreNo());
				delCouponSer.setCurrentTime(DateUtil.convertDateToString(Calendar.getInstance().getTime(), DateUtil.DATE_FORMAT_FIVE));
				List<Coupon> delCouponList = couponMapper.selectAvailableCoupon(delCouponSer);
				if (delCouponList != null && delCouponList.size() > 0) {
					for (Coupon delCoupon : delCouponList) {
						ConsumerFamilyCoupon delConsumerFamilyCoupon = new ConsumerFamilyCoupon();
						delConsumerFamilyCoupon.setStatus(0);
						delConsumerFamilyCoupon.setCouponId(delCoupon.getCouponId());
						consumerFamilyCouponMapper.deleteByCouponId(delConsumerFamilyCoupon);

						ConsumerCoupon delConsumerCoupon = new ConsumerCoupon();
						delConsumerCoupon.setStatus(0);
						delConsumerCoupon.setCouponId(delCoupon.getCouponId());
						consumerCouponMapper.deleteByCouponId(delConsumerCoupon);
					}
				}
			}
			merchantUserMapper.insertSelective(merchantNewUser);
			MerchantRoleUserKey merchantRoleUserKey = new MerchantRoleUserKey();
			merchantRoleUserKey.setRoleId("1");
			merchantRoleUserKey.setUserId(merchantNewUser.getUserId());
			merchantRoleUserMapper.insert(merchantRoleUserKey);

			// 跟新商户银行卡
			MerchantUserBankCard merchantUserBankCard = new MerchantUserBankCard();
			merchantUserBankCard.setUserId(merchantNewUser.getUserId());
			merchantUserBankCard.setCardNo(merchantNewUser.getClapStoreNo());
			merchantUserBankCardMapper.updateBySelective(merchantUserBankCard);

			// 增加这个seller下对应用户的券关系
			SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
			try {
				if (toDBConsumerFamilyCouponList != null && toDBConsumerFamilyCouponList.size() > 0) {
					ConsumerFamilyCouponMapper consumerFamilyCouponMapper = session.getMapper(ConsumerFamilyCouponMapper.class);
					for (int index = 0; index < toDBConsumerFamilyCouponList.size(); index++) {
						ConsumerFamilyCoupon object = toDBConsumerFamilyCouponList.get(index);
						consumerFamilyCouponMapper.insert(object);
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
		if (deleteEmployeeList != null && deleteEmployeeList.size() > 0) {// 删除被替换的员工
			for (MerchantEmployee delEmployee : deleteEmployeeList) {
				merchantEmployeeMapper.deleteByPrimaryKey(delEmployee.getEmployeeUserId());
				merchantEmployeeRoleRelMapper.deleteByEmployeeUserId(delEmployee.getEmployeeUserId());
			}
		}
		if (newEmployeeList != null && newEmployeeList.size() > 0) {// 增加新员工
			for (MerchantEmployee newEmployee : newEmployeeList) {
				merchantEmployeeMapper.insertSelective(newEmployee);
				MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey = new MerchantEmployeeRoleRelKey();
				merchantEmployeeRoleRelKey.setRoleId("1");
				merchantEmployeeRoleRelKey.setEmployeeUserId(newEmployee.getEmployeeUserId());
				merchantEmployeeRoleRelMapper.insert(merchantEmployeeRoleRelKey);

			}
		}
	}

	@Override
	public MerchantUser selectByUserId(String merchantUserId) {
		return merchantUserMapper.selectByUserId(merchantUserId);
	}

}
