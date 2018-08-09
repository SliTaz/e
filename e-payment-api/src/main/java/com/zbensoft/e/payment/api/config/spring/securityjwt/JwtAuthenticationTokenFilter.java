package com.zbensoft.e.payment.api.config.spring.securityjwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.zbensoft.e.payment.api.common.MessageDef;
import com.zbensoft.e.payment.api.common.RedisUtil;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationTokenFilter.class);

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private String tokenHeader = MessageDef.LOGIN.TOKEN_HEADER;

	private String tokenHead = MessageDef.LOGIN.TOKEN_HEAD;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		try {

			String authHeader = request.getHeader(this.tokenHeader);
			if (authHeader != null && authHeader.startsWith(tokenHead)) {
				final String authToken = authHeader.substring(tokenHead.length()); // The part after "Bearer "
				String username = jwtTokenUtil.getUsernameFromToken(authToken);
				if (username != null && !username.isEmpty()) {
					String userType = username.substring(0, 2);
					String usernameTmp = username.substring(3);
					// String loginType = RedisDef.LOGIN_TYPE.WEB;
					// if (MessageDef.USER_TYPE.CONSUMER_STRING_APP_LOGIN.equals(userType) || MessageDef.USER_TYPE.MERCHANT_STRING_APP_LOGIN.equals(userType)) {
					// loginType = RedisDef.LOGIN_TYPE.APP;
					// }
					String redisKey = RedisUtil.key_TOKEN(username);
					if (RedisUtil.hasKey(redisKey)) {
						if (SecurityContextHolder.getContext().getAuthentication() == null) {
							String spath = request.getServletPath();
							String redisValue = null;
							if (spath.contains("/realTimeData")) {
								redisValue = RedisUtil.get_TOKEN_NO_UPDATE_TIME(redisKey);
							} else {
								redisValue = RedisUtil.get_TOKEN(redisKey);
							}
							if (redisValue != null) {
								String[] redisValues = redisValue.split("\\|");
								String token = redisValues[0];
								String userId = redisValues[1];
								if (redisValues.length == 3 && redisValues[2] != null && redisValues[2].length() > 0) {
									String[] authorities = redisValues[2].split(",");
									List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
									for (String authority : authorities) {
										list.add(new ZBGrantedAuthority(authority));
									}
									if (authHeader.equals(token)) {
										JwtUser user = new JwtUser(userId, null, usernameTmp, null, null, null, null, null, null, true, true, 2, 2, -1);
										UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, list);
										authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
										SecurityContextHolder.getContext().setAuthentication(authentication);
										// StringBuffer sb = new StringBuffer();
										// sb.append("checkToken ").append(username).append(",");
										// sb.append("userName=").append(username).append(",");
										// sb.append("authority=");
										// if (result.getAuthorities() != null && result.getAuthorities().size() > 0) {
										// for (GrantedAuthority grantedAuthority : result.getAuthorities()) {
										// sb.append(grantedAuthority.getAuthority()).append(",");
										// }
										// }
										// logger.info(sb.toString());
									}
								} else {
									SecurityContextHolder.getContext().setAuthentication(null);
								}
							}
						}
					} else {
						SecurityContextHolder.getContext().setAuthentication(null);
					}
				} else {
					SecurityContextHolder.getContext().setAuthentication(null);
				}

				// logger.info("checking authentication " + username);
				//
				// if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				//
				// // 如果我们足够相信token中的数据，也就是我们足够相信签名token的secret的机制足够好
				// // 这种情况下，我们可以不用再查询数据库，而直接采用token中的数据
				// // 本例中，我们还是通过Spring Security的 @UserDetailsService 进行了数据查询
				// // 但简单验证的话，你可以采用直接验证token是否合法来避免昂贵的数据查询
				// UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
				//
				// if (jwtTokenUtil.validateToken(authToken, userDetails)) {
				// UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				// userDetails, null, userDetails.getAuthorities());
				// authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
				// request));
				// logger.info("authenticated user " + username + ", setting security context");
				// SecurityContextHolder.getContext().setAuthentication(authentication);
				// }
				// }
			} else {
				SecurityContextHolder.getContext().setAuthentication(null);
			}

		} catch (Exception e) {
			log.error("", e);
			SecurityContextHolder.getContext().setAuthentication(null);
		}
		chain.doFilter(request, response);
	}
}
