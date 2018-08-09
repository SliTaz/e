package com.zbensoft.e.payment.api.service.impl;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisDef;
import com.zbensoft.e.payment.api.common.RedisUtil;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtTokenUtil;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtUser;
import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.api.service.api.SysUserService;
import com.zbensoft.e.payment.db.domain.SysRoleUserKey;
import com.zbensoft.e.payment.db.domain.SysUser;
import com.zbensoft.e.payment.db.mapper.SysUserMapper;

@Service
public class SysUserServiceImpl implements SysUserService {
	private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

	@Autowired
	SysUserMapper sysUserMapper;
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserDetailsService userDetailsService;
	@Autowired
	JwtTokenUtil jwtTokenUtil;
	@Autowired
	SysRoleUserService sysRoleUserService;

	private String tokenHead = MessageDef.LOGIN.TOKEN_HEAD;

	@Override
	public int deleteByPrimaryKey(String userId) {
		return sysUserMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(SysUser record) {
		return sysUserMapper.insert(record);
	}

	@Override
	public int insertSelective(SysUser record) {
		return sysUserMapper.insertSelective(record);
	}

	@Override
	public int count(SysUser user) {
		return sysUserMapper.count(user);
	}

	@Override
	public SysUser selectByPrimaryKey(String userId) {
		return sysUserMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKeySelective(SysUser record) {
		return sysUserMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(SysUser record) {
		return sysUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<SysUser> selectPage(SysUser record) {
		return sysUserMapper.selectPage(record);
	}

	@Override
	public boolean isUserExist(SysUser user) {
		return selectByUserName(user.getUserName()) != null;
	}

	@Override
	public void deleteAll() {
		sysUserMapper.deleteAll();
	}

	@Override
	public SysUser selectByUserName(String userName) {
		return sysUserMapper.selectByUserName(userName);
	}

	@Override
	public JwtAuthenticationResponse login(String username, String password) throws Exception {
		try {
			UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
			// Perform the security
			final Authentication authentication = authenticationManager.authenticate(upToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			// Reload password post-security so we can generate token
			final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			final String token = jwtTokenUtil.generateToken(userDetails, username);
			return new JwtAuthenticationResponse(tokenHead + token, ((JwtUser) userDetails).getId(), ((JwtUser) userDetails).getUsername(), username, ((JwtUser) userDetails).getIsDefaultPassword(),
					((JwtUser) userDetails).getIsDefaultPayPassword(), ((JwtUser) userDetails).getEmailBindStatus(), ((JwtUser) userDetails).getAuthorities(), (JwtUser) userDetails);
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
	public JwtAuthenticationResponse refresh(String oldToken) {
		final String token = oldToken.substring(tokenHead.length());
		String username = jwtTokenUtil.getUsernameFromToken(token);

		if (username != null && !username.isEmpty()) {
			String userType = username.substring(0, 2);
			// String loginType = RedisDef.LOGIN_TYPE.WEB;
			// if (MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType) || MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
			// loginType = RedisDef.LOGIN_TYPE.APP;
			// }
			String redisKey = RedisUtil.key_TOKEN(username);
			if (RedisUtil.hasKey(redisKey)) {
				if (SecurityContextHolder.getContext().getAuthentication() != null) {
					String tokenRedis = RedisUtil.get_TOKEN(redisKey);
					if (tokenRedis != null) {
						JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);
						if (user != null && jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
							return new JwtAuthenticationResponse(tokenHead + jwtTokenUtil.refreshToken(token), user.getId(), user.getUsername(), username, user.getIsDefaultPassword(), user.getIsDefaultPayPassword(),
									user.getEmailBindStatus(), user.getAuthorities(), (JwtUser) user);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void logout(String oldToken) {
		if (oldToken != null && oldToken.length() > tokenHead.length()) {
			final String token = oldToken.substring(tokenHead.length());
			String username = jwtTokenUtil.getUsernameFromToken(token);

			if (username != null && !username.isEmpty()) {
				String userType = username.substring(0, 2);
				// String loginType = RedisDef.LOGIN_TYPE.WEB;
				// if (MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType) || MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
				// loginType = RedisDef.LOGIN_TYPE.APP;
				// }
				RedisUtil.delete_TOKEN(username);
			}
		}
	}

	@Override
	public SysUser register(SysUser userToAdd) {
		final String username = userToAdd.getUserName();
		if (sysUserMapper.selectByUserName(username) != null) {
			return null;
		}
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		final String rawPassword = userToAdd.getPassword();
		userToAdd.setUserId(System.currentTimeMillis() + "");
		userToAdd.setPassword(encoder.encode(rawPassword));
		// TODO: 创建时间
		// userToAdd..setLastPasswordResetDate(new Date());
		sysUserMapper.insert(userToAdd);

		SysRoleUserKey sysRoleUserKey = new SysRoleUserKey();

		sysRoleUserKey.setUserId(userToAdd.getUserId());
		sysRoleUserKey.setRoleId("201");
		sysRoleUserService.insert(sysRoleUserKey);

		return userToAdd;
	}

	public static void main(String[] args) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.matches("111111", encoder.encode("111111")));
	}
}
