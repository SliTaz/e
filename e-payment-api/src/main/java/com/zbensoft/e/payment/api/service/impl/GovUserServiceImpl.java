package com.zbensoft.e.payment.api.service.impl;

import java.util.List;

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

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtAuthenticationResponse;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtTokenUtil;
import com.zbensoft.e.payment.api.config.spring.securityjwt.JwtUser;
import com.zbensoft.e.payment.api.service.api.GovUserService;
import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.db.domain.GovUser;
import com.zbensoft.e.payment.db.mapper.GovUserMapper;

@Service
public class GovUserServiceImpl implements GovUserService {

	private static final Logger log = LoggerFactory.getLogger(GovUserServiceImpl.class);

	@Autowired
	GovUserMapper govUserMapper;
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
		return govUserMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public int insert(GovUser record) {
		return govUserMapper.insert(record);
	}

	@Override
	public int insertSelective(GovUser record) {
		return govUserMapper.insertSelective(record);
	}

	@Override
	public int count(GovUser govUser) {
		return govUserMapper.count(govUser);
	}

	@Override
	public GovUser selectByPrimaryKey(String userId) {
		return govUserMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateByPrimaryKeySelective(GovUser record) {
		return govUserMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(GovUser record) {
		return govUserMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<GovUser> selectPage(GovUser record) {
		return govUserMapper.selectPage(record);
	}

	@Override
	public boolean isGovUserExist(GovUser govUser) {
		return selectByUserName(govUser.getUserName()) != null;
	}

	@Override
	public void deleteAll() {
		govUserMapper.deleteAll();
	}

	@Override
	public GovUser selectByUserName(String userName) {
		return govUserMapper.selectByUserName(userName);
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
			return new JwtAuthenticationResponse(tokenHead + token, ((JwtUser) userDetails).getId(), ((JwtUser) userDetails).getUsername(), userName, ((JwtUser) userDetails).getIsDefaultPassword(),
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

}
