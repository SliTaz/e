package com.zbensoft.e.payment.api.config.spring.securityjwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.service.api.ConsumerRoleService;
import com.zbensoft.e.payment.api.service.api.ConsumerRoleUserService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserClapService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.GovMenuService;
import com.zbensoft.e.payment.api.service.api.GovRoleService;
import com.zbensoft.e.payment.api.service.api.GovRoleUserService;
import com.zbensoft.e.payment.api.service.api.GovUserService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeRoleRelService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeRoleService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeService;
import com.zbensoft.e.payment.api.service.api.MerchantRoleService;
import com.zbensoft.e.payment.api.service.api.MerchantRoleUserService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.SysMenuService;
import com.zbensoft.e.payment.api.service.api.SysRoleService;
import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.api.service.api.SysUserService;
import com.zbensoft.e.payment.db.domain.ConsumerRole;
import com.zbensoft.e.payment.db.domain.ConsumerRoleUserKey;
import com.zbensoft.e.payment.db.domain.ConsumerUser;
import com.zbensoft.e.payment.db.domain.ConsumerUserClap;
import com.zbensoft.e.payment.db.domain.GovMenuUserMenuResponse;
import com.zbensoft.e.payment.db.domain.GovUser;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRole;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeRoleRelKey;
import com.zbensoft.e.payment.db.domain.MerchantRole;
import com.zbensoft.e.payment.db.domain.MerchantRoleUserKey;
import com.zbensoft.e.payment.db.domain.MerchantUser;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuParam;
import com.zbensoft.e.payment.db.domain.SysMenuUserMenuResponse;
import com.zbensoft.e.payment.db.domain.SysUser;

@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {

	private static final Logger log = LoggerFactory.getLogger(JwtUserDetailsServiceImpl.class);

	@Autowired
	private ConsumerUserService consumerUserService;
	@Autowired
	private ConsumerRoleUserService consumerRoleUserService;
	@Autowired
	private ConsumerRoleService consumerRoleService;
	@Autowired
	private ConsumerUserClapService consumerUserClapService;

	@Autowired
	private MerchantUserService merchantUserService;
	@Autowired
	private MerchantRoleUserService merchantRoleUserService;
	@Autowired
	private MerchantRoleService merchantRoleService;

	@Autowired
	private GovUserService govUserService;
	@Autowired
	private GovRoleUserService govRoleUserService;
	@Autowired
	private GovRoleService govRoleService;
	@Autowired
	private GovMenuService govMenuService;

	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysRoleUserService sysRoleUserService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysMenuService sysMenuService;

	@Autowired
	private MerchantEmployeeService merchantEmployeeService;
	@Autowired
	private MerchantEmployeeRoleRelService merchantEmployeeRoleRelService;
	@Autowired
	private MerchantEmployeeRoleService merchantEmployeeRoleService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String userType = username.substring(0, 2);
		username = username.substring(3);

		if (MessageDef.USER_TYPE.CONSUMER_STRING.equals(userType) || MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType)) {
			return loadConsumerUserByUsername(username);

		}
		if (MessageDef.USER_TYPE.MERCHANT_STRING.equals(userType) || MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
			
			try {
				 return loadMerchantUserByUsername(username);
			} catch (UsernameNotFoundException e) {
				if (MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {// 员工只能在app上登录
					return loadMerchantEmployeeByUsername(username);
				}else{
					throw e;
				}
			}

		}

		if (MessageDef.USER_TYPE.GOV_STRING.equals(userType)) {
			return loadGovUserByUsername(username);
		}
		if (MessageDef.USER_TYPE.SYS_STRING.equals(userType)) {
			return loadSysUserByUsername(username);
		}
		log.info(String.format("No user found with username '%s'.", username));
		throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
	}

	private UserDetails loadMerchantEmployeeByUsername(String username) {
		boolean isNonLocked = true;
		boolean isEable = true;

		MerchantEmployee merchantEmployee = merchantEmployeeService.selectByIdNumber(username);
		if (merchantEmployee == null) {
			log.info(String.format("No Merchant Employee user found with username '%s'.", username));
			throw new UsernameNotFoundException(String.format("No Merchant Employee user found with username '%s'.", username));
		}
		if (merchantEmployee.getStatus() == null || merchantEmployee.getStatus() == MessageDef.STATUS.DISABLE_INT) {
			isEable = false;
		}
		if (merchantEmployee.getIsLocked() == null || merchantEmployee.getIsLocked() == MessageDef.LOCKED.LOCKED) {
			isNonLocked = false;
		}

		if (isEable && isNonLocked) {
			MerchantUser user = merchantUserService.selectByPrimaryKey(merchantEmployee.getUserId());
//			merchantEmployee.setUserName(user.getUserName() + MessageDef.USER_TYPE.DELIMITER + merchantEmployee.getUserName());
			merchantEmployee.setUserName(merchantEmployee.getUserName());
			return new JwtUser(merchantEmployee.getEmployeeUserId(), username, merchantEmployee.getUserName(), merchantEmployee.getPassword(), merchantEmployee.getEmail(), merchantEmployee.getIdNumber(),
					user.getClapStoreNo(), getGrantedAuthorities(merchantEmployee), null, isEable, isNonLocked, merchantEmployee.getIsFirstLogin(), 2, merchantEmployee.getEmailBindStatus(), true, user.getUserId());

		} else {
			return null;
		}
	}

	private Collection<? extends GrantedAuthority> getGrantedAuthorities(MerchantEmployee merchantEmployee) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new ZBGrantedAuthority("ROLE_MERCHANT"));
		List<MerchantEmployeeRoleRelKey> merchantEmployeeRoleRelKeyList = merchantEmployeeRoleRelService.selectByUserId(merchantEmployee.getEmployeeUserId());
		if (merchantEmployeeRoleRelKeyList != null && merchantEmployeeRoleRelKeyList.size() > 0) {
			for (MerchantEmployeeRoleRelKey merchantEmployeeRoleRelKey : merchantEmployeeRoleRelKeyList) {
				MerchantEmployeeRole merchantEmployeeRole = merchantEmployeeRoleService.selectByPrimaryKey(merchantEmployeeRoleRelKey.getRoleId());
				if (merchantEmployeeRole != null) {
					authorities.add(new ZBGrantedAuthority("ROLE_" + merchantEmployeeRole.getCode()));
				}
			}
		}
		return authorities;
	}

	private UserDetails loadConsumerUserByUsername(String username) {
		// ConsumerUser user = consumerUserService.selectByUserName(username);
		ConsumerUserClap consumerUserClap = consumerUserClapService.selectByIdNumber(username);
		if (consumerUserClap == null) {
			log.info(String.format("No Consumer user clap found with username '%s'.", username));
			throw new UsernameNotFoundException(String.format("No Consumer user clap found with username '%s'.", username));
		}
		ConsumerUser user = consumerUserService.selectByPrimaryKey(consumerUserClap.getUserId());
		if (user == null) {
			log.info(String.format("No Consumer user found with username '%s'.", username));
			throw new UsernameNotFoundException(String.format("No Consumer user found with username '%s'.", username));
		}
		boolean isEable = true;
		if (user.getStatus() == null || user.getStatus() == MessageDef.STATUS.DISABLE_INT) {
			isEable = false;
		}
		boolean isNonLocked = true;
		if (user.getIsLocked() == null || user.getIsLocked() == MessageDef.LOCKED.LOCKED) {
			isNonLocked = false;
		}
		return new JwtUser(user.getUserId(), username, user.getUserName(), user.getPassword(), null, consumerUserClap.getIdNumber(), consumerUserClap.getClapNo(), getGrantedAuthorities(user), null, isEable, isNonLocked,
				user.getIsDefaultPassword(), user.getIsDefaultPayPassword(), user.getEmailBindStatus());
	}

	private UserDetails loadMerchantUserByUsername(String username) {
		// MerchantUser user = merchantUserService.selectByUserName(username);
		MerchantUser user = merchantUserService.selectByIdNumber(username);
		if (user == null) {
			log.info(String.format("No Merchant user found with username '%s'.", username));
			 throw new UsernameNotFoundException(String.format("No Merchant user found with username '%s'.", username));
		}
		boolean isEable = true;
		if (user.getStatus() == null || user.getStatus() == MessageDef.STATUS.DISABLE_INT) {
			isEable = false;
		}
		boolean isNonLocked = true;
		if (user.getIsLocked() == null || user.getIsLocked() == MessageDef.LOCKED.LOCKED) {
			isNonLocked = false;
		}
		return new JwtUser(user.getUserId(), username, user.getUserName(), user.getPassword(), null, user.getIdNumber(), user.getClapStoreNo(), getGrantedAuthorities(user), null, isEable, isNonLocked,
				user.getIsDefaultPassword(), user.getIsDefaultPayPassword(), user.getEmailBindStatus());
	}

	private UserDetails loadGovUserByUsername(String username) {
		GovUser user = govUserService.selectByUserName(username);
		if (user == null) {
			log.info(String.format("No Gov user found with username '%s'.", username));
			throw new UsernameNotFoundException(String.format("No Gov user found with username '%s'.", username));
		}
		boolean isEable = true;
		if (user.getStatus() == null || user.getStatus() == MessageDef.STATUS.DISABLE_INT) {
			isEable = false;
		}
		return new JwtUser(user.getUserId(), username, user.getUserName(), user.getPassword(), null, null, null, getGrantedAuthorities(user), null, isEable, true, 2, 2, -1);
	}

	private UserDetails loadSysUserByUsername(String username) {
		SysUser user = sysUserService.selectByUserName(username);
		if (user == null) {
			log.info(String.format("No user found with username '%s'.", username));
			throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
		}
		boolean isEable = true;
		if (user.getStatus() == null || user.getStatus() == MessageDef.STATUS.DISABLE_INT) {
			isEable = false;
		}
		return new JwtUser(user.getUserId(), username, user.getUserName(), user.getPassword(), null, null, null, getGrantedAuthorities(user), null, isEable, true, 2, 2, -1);
	}

	private List<GrantedAuthority> getGrantedAuthorities(ConsumerUser user) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		PageHelper.startPage(1, 100000);
		List<ConsumerRoleUserKey> ConsumeRoleUserKeyList = consumerRoleUserService.selectByUserId(user.getUserId());
		if (ConsumeRoleUserKeyList != null && ConsumeRoleUserKeyList.size() > 0) {
			for (ConsumerRoleUserKey consumeRoleUserKey : ConsumeRoleUserKeyList) {
				ConsumerRole consumeRole = consumerRoleService.selectByPrimaryKey(consumeRoleUserKey.getRoleId());
				if (consumeRole != null) {
					authorities.add(new ZBGrantedAuthority("ROLE_" + consumeRole.getCode()));
				}
			}
		}
		return authorities;
	}

	private List<GrantedAuthority> getGrantedAuthorities(MerchantUser user) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		PageHelper.startPage(1, 100000);
		List<MerchantRoleUserKey> merchantRoleUserKeyList = merchantRoleUserService.selectByUserId(user.getUserId());
		if (merchantRoleUserKeyList != null && merchantRoleUserKeyList.size() > 0) {
			for (MerchantRoleUserKey merchantRoleUserKey : merchantRoleUserKeyList) {
				MerchantRole merchantRole = merchantRoleService.selectByPrimaryKey(merchantRoleUserKey.getRoleId());
				if (merchantRole != null) {
					authorities.add(new ZBGrantedAuthority("ROLE_" + merchantRole.getCode()));
				}
			}
		}
		return authorities;
	}

	private List<GrantedAuthority> getGrantedAuthorities(GovUser user) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		SysMenuUserMenuParam sysMenuUserMenuParam = new SysMenuUserMenuParam();
		sysMenuUserMenuParam.setUserId(user.getUserId());
		sysMenuUserMenuParam.setMenuType(MessageDef.MENU_TYPE.FUNCTION);
		PageHelper.startPage(1, 100000);
		List<GovMenuUserMenuResponse> govMenuUserMenuResponseList = govMenuService.getUserMenus(sysMenuUserMenuParam, null);

		if (govMenuUserMenuResponseList != null && govMenuUserMenuResponseList.size() > 0) {
			for (GovMenuUserMenuResponse govMenuUserMenuResponse : govMenuUserMenuResponseList) {
				authorities.add(new ZBGrantedAuthority("ROLE_" + govMenuUserMenuResponse.getMenuKeyWord()));
			}
		}
		return authorities;
	}

	private List<GrantedAuthority> getGrantedAuthorities(SysUser user) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		SysMenuUserMenuParam sysMenuUserMenuParam = new SysMenuUserMenuParam();
		sysMenuUserMenuParam.setUserId(user.getUserId());
		sysMenuUserMenuParam.setMenuType(MessageDef.MENU_TYPE.FUNCTION);
		PageHelper.startPage(1, 100000);
		List<SysMenuUserMenuResponse> sysMenuUserMenuResponseList = sysMenuService.getUserMenus(sysMenuUserMenuParam, null);

		if (sysMenuUserMenuResponseList != null && sysMenuUserMenuResponseList.size() > 0) {
			for (SysMenuUserMenuResponse sysMenuUserMenuResponse : sysMenuUserMenuResponseList) {
				authorities.add(new ZBGrantedAuthority("ROLE_" + sysMenuUserMenuResponse.getMenuKeyWord()));
			}
		}
		return authorities;
	}
}
