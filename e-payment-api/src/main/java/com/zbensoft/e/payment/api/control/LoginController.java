package com.zbensoft.e.payment.api.control;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zbensoft.e.payment.api.common.CommonFun;
import com.zbensoft.e.payment.api.common.HttpRestStatus;
import com.zbensoft.e.payment.api.common.IDGenerate;
import com.zbensoft.e.payment.api.common.LocaleMessageSourceService;
import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.common.ResponseRestEntity;
import com.zbensoft.e.payment.api.common.SystemConfigFactory;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationRequest;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtUser;
import com.zbensoft.e.payment.api.service.api.ConsumerUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.ConsumerUserService;
import com.zbensoft.e.payment.api.service.api.GovUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.GovUserService;
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeLoginHisService;
import com.zbensoft.e.payment.api.service.api.MerchantUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.MerchantUserService;
import com.zbensoft.e.payment.api.service.api.SysUserLoginHisService;
import com.zbensoft.e.payment.api.service.api.SysUserService;
import com.zbensoft.e.payment.common.config.SystemConfigKey;
import com.zbensoft.e.payment.db.domain.ConsumerUserLoginHis;
import com.zbensoft.e.payment.db.domain.GovUserLoginHis;
import com.zbensoft.e.payment.db.domain.MerchantEmployeeLoginHis;
import com.zbensoft.e.payment.db.domain.MerchantUserLoginHis;
import com.zbensoft.e.payment.db.domain.SysUserLoginHis;

import io.swagger.annotations.ApiOperation;

@RestController
public class LoginController {

	private static final Logger log = LoggerFactory.getLogger(LoginController.class);

	private String tokenHeader = MessageDef.LOGIN.TOKEN_HEADER;

	@Autowired
	private ConsumerUserService consumerUserService;
	@Autowired
	private MerchantUserService merchantUserService;
	@Autowired
	private GovUserService govUserService;
	@Autowired
	private SysUserService sysUserService;
	// @Autowired
	// private MerchantEmployeeService merchantEmployeeService;

	// 管理用户登录历史
	@Autowired
	private SysUserLoginHisService sysUserLoginHisService;

	// 政府用户登录历史
	@Autowired
	private GovUserLoginHisService govUserLoginHisService;

	// 商户登录历史
	@Autowired
	private MerchantUserLoginHisService merchantUserLoginHisService;

	// 商户用户登录历史
	@Autowired
	private MerchantEmployeeLoginHisService merchantEmployeeLoginHisService;

	// 消费用户登录历史
	@Autowired
	private ConsumerUserLoginHisService consumerUserLoginHisService;

	@Resource
	private LocaleMessageSourceService localeMessageSourceService;

	// 登陆
	@ApiOperation(value = "login", notes = "")
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public ResponseRestEntity<JwtAuthenticationResponse> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletRequest request) throws AuthenticationException {
//		if (CommonFun.isLimitCountBuyer()) {
//			int a = new Random().nextInt(10);
//			if (a > 0) {
//				return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.UNKNOWN);
//			}
//		}

		if (authenticationRequest.getUserName() == null || authenticationRequest.getUserName().isEmpty()) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_USER_NAME_NOTEMPTY, localeMessageSourceService.getMessage("common.username.notempty"));
		}

		if (authenticationRequest.getPassword() == null || authenticationRequest.getPassword().isEmpty()) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_PASSWORD_NOTEMPTY, localeMessageSourceService.getMessage("common.password.notempty"));
		}
		String userType = authenticationRequest.getUserName().substring(0, 2);
		if (userType == null || userType.isEmpty()) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_TYPE_NOTEMPTY, localeMessageSourceService.getMessage("common.usertype.notempty"));
		}
		if (!MessageDef.USER_TYPE.CONSUMER_STRING.equals(userType) && !MessageDef.USER_TYPE.MERCHANT_STRING.equals(userType) && !MessageDef.USER_TYPE.GOV_STRING.equals(userType)
				&& !MessageDef.USER_TYPE.SYS_STRING.equals(userType) && !MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType) && !MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_TYPE_ERROR, localeMessageSourceService.getMessage("common.usertype.error"));
		}

		// int errorPasswordCountObject = RedisUtil.get_ERROR_PASSWORD_COUNT(authenticationRequest.getUserName(), Calendar.getInstance().getTime());

		// int LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT = SystemConfigFactory.getInstance().getSystemConfigInt(SystemConfigKey.LOGIN_LIMIT_USER_PASSWORD_ERROR_COUNT);
		if (RedisUtil.islimit_ERROR_PASSWORD_COUNT(CommonFun.getRelLoginName(authenticationRequest.getUserName()), Calendar.getInstance().getTime())) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_USER_PASSWORD_ERROR_COUNT, "超出每日密码错误次数限制");
		}

		JwtAuthenticationResponse token = null;
		try {
			if (MessageDef.USER_TYPE.CONSUMER_STRING.equals(userType) || MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType)) {
				if (MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType)) {
					String LOGIN_LIMIT_CONSUMER_APP_VERSION = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.LOGIN_LIMIT_CONSUMER_APP_VERSION);
					if (CommonFun.isLimitVersionComparison(authenticationRequest.getAppVersion(), LOGIN_LIMIT_CONSUMER_APP_VERSION)) {
						return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_LIMIT_APP_VERSION, "已经不支持此版本的app，请更新app。");
					}
				}
				authenticationRequest.setUserName(CommonFun.getRelLoginName(authenticationRequest.getUserName()));
				token = consumerUserService.login(authenticationRequest.getUserName(), authenticationRequest.getPassword());

				try {
					// 插入到历史登录表
					ConsumerUserLoginHis consumerUserLoginHis = new ConsumerUserLoginHis();
					UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
					consumerUserLoginHis.setConsumerUserLoginHisId(IDGenerate.generateCommOne(IDGenerate.CONSUMER_LOGIN_HIS));
					consumerUserLoginHis.setUserId(((JwtUser) userDetails).getId());
					consumerUserLoginHis.setLoginTime(new Date());
					try {

						consumerUserLoginHis.setLoginType(Integer.valueOf(userType));
					} catch (Exception e) {
					}
					consumerUserLoginHis.setAppVersion(authenticationRequest.getAppVersion());
					if (authenticationRequest.getMobileInfo() != null && authenticationRequest.getMobileInfo().length() > 1000) {
						consumerUserLoginHis.setMobileInfo(authenticationRequest.getMobileInfo().substring(0, 1000));
					} else {
						consumerUserLoginHis.setMobileInfo(authenticationRequest.getMobileInfo());
					}

					// 获取本地IP地址
					if (CommonFun.getIP(request) != null) {
						String remoteIp = CommonFun.getIP(request);
						consumerUserLoginHis.setIpAddr(remoteIp);
					}
					consumerUserLoginHisService.insert(consumerUserLoginHis);

				} catch (Exception e) {
					log.error("", e);
				}

			}
			if (MessageDef.USER_TYPE.MERCHANT_STRING.equals(userType) || MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
				if (MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
					String LOGIN_LIMIT_MERCHANT_APP_VERSION = SystemConfigFactory.getInstance().getSystemConfigStr(SystemConfigKey.LOGIN_LIMIT_MERCHANT_APP_VERSION);
					if (CommonFun.isLimitVersionComparison(authenticationRequest.getAppVersion(), LOGIN_LIMIT_MERCHANT_APP_VERSION)) {
						return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_LIMIT_APP_VERSION, "已经不支持此版本的app，请更新app。");
					}
				}

				authenticationRequest.setUserName(CommonFun.getRelLoginName(authenticationRequest.getUserName()));
				// 员工登录
				token = merchantUserService.login(authenticationRequest.getUserName(), authenticationRequest.getPassword());
				if (token.isEmplyoeeFlag()) {

					try {
						// 插入到历史登录表
						MerchantEmployeeLoginHis merchantEmployeeLoginHis = new MerchantEmployeeLoginHis();
						UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
						merchantEmployeeLoginHis.setLoginHisId(IDGenerate.generateCommOne(IDGenerate.MERCHANT_EMPLOYEE_LOGIN_HIS));
						merchantEmployeeLoginHis.setEmployeeUserId(((JwtUser) userDetails).getId());
						merchantEmployeeLoginHis.setLoginTime(new Date());
						try {

							merchantEmployeeLoginHis.setLoginType(Integer.valueOf(userType));
						} catch (Exception e) {
						}
						merchantEmployeeLoginHis.setAppVersion(authenticationRequest.getAppVersion());
						if (authenticationRequest.getMobileInfo() != null && authenticationRequest.getMobileInfo().length() > 1000) {
							merchantEmployeeLoginHis.setMobileInfo(authenticationRequest.getMobileInfo().substring(0, 1000));
						} else {
							merchantEmployeeLoginHis.setMobileInfo(authenticationRequest.getMobileInfo());
						}
						// 获取本地IP地址
						if (CommonFun.getIP(request) != null) {
							String remoteIp = CommonFun.getIP(request);
							merchantEmployeeLoginHis.setIpAddr(remoteIp);
						}
						merchantEmployeeLoginHisService.insert(merchantEmployeeLoginHis);
					} catch (Exception e) {
						log.error("", e);
					}
				} else {
					try {
						// 插入到历史登录表
						MerchantUserLoginHis merchantUserLoginHis = new MerchantUserLoginHis();
						UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
						merchantUserLoginHis.setConsumerUserLoginHisId(IDGenerate.generateCommOne(IDGenerate.GOV_USER_LOGIN_HIS));
						merchantUserLoginHis.setUserId(((JwtUser) userDetails).getId());
						merchantUserLoginHis.setLoginTime(new Date());
						try {

							merchantUserLoginHis.setLoginType(Integer.valueOf(userType));
						} catch (Exception e) {
						}
						merchantUserLoginHis.setAppVersion(authenticationRequest.getAppVersion());
						if (authenticationRequest.getMobileInfo() != null && authenticationRequest.getMobileInfo().length() > 1000) {
							merchantUserLoginHis.setMobileInfo(authenticationRequest.getMobileInfo().substring(0, 1000));
						} else {
							merchantUserLoginHis.setMobileInfo(authenticationRequest.getMobileInfo());
						}
						// 获取本地IP地址
						if (CommonFun.getIP(request) != null) {
							String remoteIp = CommonFun.getIP(request);
							merchantUserLoginHis.setIpAddr(remoteIp);
						}
						merchantUserLoginHisService.insert(merchantUserLoginHis);
					} catch (Exception e) {
						log.error("", e);
					}
				}

			}
			if (MessageDef.USER_TYPE.GOV_STRING.equals(userType)) {
				token = govUserService.login(authenticationRequest.getUserName(), authenticationRequest.getPassword());

				try {
					// 插入到历史登录表
					GovUserLoginHis govUserLoginHis = new GovUserLoginHis();
					UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
					govUserLoginHis.setConsumerUserLoginHisId(IDGenerate.generateCommOne(IDGenerate.GOV_USER_LOGIN_HIS));
					govUserLoginHis.setUserId(((JwtUser) userDetails).getId());
					govUserLoginHis.setLoginTime(new Date());
					// 获取本地IP地址
					if (CommonFun.getIP(request) != null) {
						String remoteIp = CommonFun.getIP(request);
						govUserLoginHis.setIpAddr(remoteIp);
					}
					govUserLoginHisService.insert(govUserLoginHis);
				} catch (Exception e) {
					log.error("", e);
				}

			}
			if (MessageDef.USER_TYPE.SYS_STRING.equals(userType)) {
				token = sysUserService.login(authenticationRequest.getUserName(), authenticationRequest.getPassword());

				try {
					// 插入到历史登录表
					SysUserLoginHis sysUserLoginHis = new SysUserLoginHis();
					UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
					sysUserLoginHis.setConsumerUserLoginHisId(IDGenerate.generateCommOne(IDGenerate.SYS_LOGIN_HIS));
					sysUserLoginHis.setUserId(((JwtUser) userDetails).getId());
					sysUserLoginHis.setLoginTime(new Date());
					// 获取本地IP地址
					if (CommonFun.getIP(request) != null) {
						String remoteIp = CommonFun.getIP(request);
						sysUserLoginHis.setIpAddr(remoteIp);
					}
					sysUserLoginHisService.insert(sysUserLoginHis);
				} catch (Exception e) {
					log.error("", e);
				}

			}
		} catch (UsernameNotFoundException e) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_USER_NOTFUND, localeMessageSourceService.getMessage("common.user.notfound"));
		} catch (DisabledException e) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_USER_DISABLE, localeMessageSourceService.getMessage("common.user.unavailable"));
		} catch (LockedException e) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_USER_LOCKED, localeMessageSourceService.getMessage("common.username.locked"));
		} catch (BadCredentialsException e) {

			int remainingTimes = RedisUtil.increment_ERROR_PASSWORD_COUNT(authenticationRequest.getUserName(), Calendar.getInstance().getTime());
			JwtAuthenticationResponse response = new JwtAuthenticationResponse();
			response.setRemainingTimes(remainingTimes);
			return new ResponseRestEntity<JwtAuthenticationResponse>(response, HttpRestStatus.LOGIN_USER_NAME_PASSWORD_ERROR, localeMessageSourceService.getMessage("common.user.password.error"));
		} catch (Exception e) {
			log.error("LoginController login error", e);
		}
		if (token == null) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.LOGIN_UNKONW_ERROR, "登录未知错误");
		}

		RedisUtil.delete_ERROR_PASSWORD_COUNT(authenticationRequest.getUserName(), Calendar.getInstance().getTime());

		// String loginType = RedisDef.LOGIN_TYPE.WEB;
		// if (MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType) || MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
		// loginType = RedisDef.LOGIN_TYPE.APP;
		// }

		RedisUtil.create_TOKEN(authenticationRequest.getUserName(), token);

		return new ResponseRestEntity<JwtAuthenticationResponse>(token, HttpRestStatus.OK);
	}

	// 退出
	@ApiOperation(value = "Quit", notes = "")
	@RequestMapping(value = "logout", method = RequestMethod.PATCH)
	public ResponseRestEntity<?> logout(HttpServletRequest request) throws AuthenticationException {
		String token = request.getHeader(tokenHeader);
		sysUserService.logout(token);
		return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.OK);
	}

	// 刷新token
	@ApiOperation(value = "Refresh token", notes = "")
	@RequestMapping(value = "refresh", method = RequestMethod.GET)
	public ResponseRestEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) throws AuthenticationException {
		String token = request.getHeader(tokenHeader);
		JwtAuthenticationResponse refreshedToken = sysUserService.refresh(token);
		if (refreshedToken == null) {
			return new ResponseRestEntity<JwtAuthenticationResponse>(HttpRestStatus.BAD_REQUEST);
		} else {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(refreshedToken.getUser(), null, refreshedToken.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// String userType = refreshedToken.getLoginUserName().substring(0, 2);
			// String loginType = RedisDef.LOGIN_TYPE.WEB;
			// if (MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType) || MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
			// loginType = RedisDef.LOGIN_TYPE.APP;
			// }

			RedisUtil.create_TOKEN(refreshedToken.getLoginUserName(), refreshedToken);

			return new ResponseRestEntity<JwtAuthenticationResponse>(refreshedToken, HttpRestStatus.OK);
		}
	}

}