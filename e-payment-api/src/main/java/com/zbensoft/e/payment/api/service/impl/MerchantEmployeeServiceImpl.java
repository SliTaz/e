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
import com.zbensoft.e.payment.api.service.api.MerchantEmployeeService;
import com.zbensoft.e.payment.api.service.api.SysRoleUserService;
import com.zbensoft.e.payment.db.domain.MerchantEmployee;
import com.zbensoft.e.payment.db.mapper.MerchantEmployeeMapper;

@Service
public class MerchantEmployeeServiceImpl implements MerchantEmployeeService {

	private static final Logger log = LoggerFactory.getLogger(MerchantEmployeeServiceImpl.class);

	@Autowired
	MerchantEmployeeMapper merchantEmployeeMapper;
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
	public int deleteByPrimaryKey(String employeeUserId) {
		return merchantEmployeeMapper.deleteByPrimaryKey(employeeUserId);
	}

	@Override
	public int insert(MerchantEmployee record) {
		return merchantEmployeeMapper.insert(record);
	}

	@Override
	public int insertSelective(MerchantEmployee record) {
		return merchantEmployeeMapper.insertSelective(record);
	}

	@Override
	public int count(MerchantEmployee merchantEmployee) {
		return merchantEmployeeMapper.count(merchantEmployee);
	}

	@Override
	public MerchantEmployee selectByPrimaryKey(String employeeUserId) {
		return merchantEmployeeMapper.selectByPrimaryKey(employeeUserId);
	}

	@Override
	public int updateByPrimaryKeySelective(MerchantEmployee record) {
		return merchantEmployeeMapper.updateByPrimaryKeySelective(record);
	}

	@Override
	public int updateByPrimaryKey(MerchantEmployee record) {
		return merchantEmployeeMapper.updateByPrimaryKey(record);
	}

	@Override
	public List<MerchantEmployee> selectPage(MerchantEmployee record) {
		return merchantEmployeeMapper.selectPage(record);
	}

	@Override
	public boolean isMerchantEmployeeExist(MerchantEmployee merchantEmployee) {
		return selectByIdNumber(merchantEmployee.getUserName()) != null;
	}

	@Override
	public List<MerchantEmployee> selectByUserId(String userId) {
		return merchantEmployeeMapper.selectByUserId(userId);

	}

	@Override
	public void deleteAll() {
		merchantEmployeeMapper.deleteAll();
	}

	@Override
	public MerchantEmployee selectByIdNumber(String userName) {
		return merchantEmployeeMapper.selectByIdNumber(userName);
	}

	@Override
	public JwtAuthenticationResponse login(String userName, String password) throws Exception {
		try {
			UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(userName, password);
			// Perform the security
			final Authentication authentication = authenticationManager.authenticate(upToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Reload password post-security so we can generate token
			final UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
			final String token = jwtTokenUtil.generateToken(userDetails, userName);

			JwtAuthenticationResponse jwtAuthenticationResponse = new JwtAuthenticationResponse(tokenHead + token, ((JwtUser) userDetails).getId(), ((JwtUser) userDetails).getUsername(), userName,
					((JwtUser) userDetails).getIsDefaultPassword(), ((JwtUser) userDetails).getIsDefaultPayPassword(), ((JwtUser) userDetails).getEmailBindStatus(), ((JwtUser) userDetails).getAuthorities(),
					(JwtUser) userDetails);
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

}
