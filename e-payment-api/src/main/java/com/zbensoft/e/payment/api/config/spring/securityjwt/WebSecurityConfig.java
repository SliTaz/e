package com.zbensoft.e.payment.api.config.spring.securityjwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setHideUserNotFoundExceptions(false);
		authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
		return new JwtAuthenticationTokenFilter();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// 由于使用的是JWT，我们这里不需要csrf
				.csrf().disable()

				.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

				// 基于token，所以不需要session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

				.authorizeRequests()
				// .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 允许对于网站静态资源的无授权访问
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/**/*.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.woff2",
                        "/**/*.ttf",
                        "/**/*.png",
                        "/swagger-resources",
                        "/v2/api-docs"
                ).permitAll()
                // 对于获取token的rest api要允许匿名访问
                .antMatchers("/login").permitAll()
                .antMatchers("/logout").permitAll()
                .antMatchers("/consumerUser/get/picture/path/*").permitAll()
                .antMatchers("/merchantUser/get/picture/path/*").permitAll()
                .antMatchers("/merchantEmployee/get/picture/path/*").permitAll()
                .antMatchers("/notice/getAppNotice").permitAll()
                .antMatchers("/notice/get/noticepicture/path/*").permitAll()
                .antMatchers("/app/reportAppErrors").permitAll()
                .antMatchers("/app/getApp").permitAll()
                .antMatchers("/app/downloadApp").permitAll()
                .antMatchers("/app/downloadSellerAndroidApp").permitAll()
                .antMatchers("/app/downloadBuyerAndroidApp").permitAll()
                .antMatchers("/app/reportAppErrorMessage").permitAll()
                
                
                .antMatchers("/webservice/**").permitAll()
                .antMatchers("/merchantUser/get/errorLog").permitAll()
                .antMatchers("/consumerUser/get/errorLog").permitAll()
                .antMatchers("/clapUseStore/get/errorLog").permitAll()
                .antMatchers("/license").permitAll()
                .antMatchers("/heapdump").permitAll()
                
                //找回密码不需要登录
                .antMatchers("/consumerUser/vid").permitAll()
                .antMatchers("/consumerUser/retrievePassword").permitAll()
                .antMatchers("/merchantUser/vid").permitAll()
                .antMatchers("/merchantUser/retrievePassword").permitAll()
                .antMatchers("/merchantEmployee/vid").permitAll()
                .antMatchers("/merchantEmployee/retrievePassword").permitAll()
                
                .antMatchers("/statistics/getOnlineUserCount").permitAll()
                .antMatchers("/consumerUser/registerGetConsumerInfo").permitAll()
                .antMatchers("/consumerUser/registerMail").permitAll()
                .antMatchers("/consumerUser/registerValidateEmailCode").permitAll()
                .antMatchers("/consumerUser/registerImgvalidateCode").permitAll()
                
                .antMatchers("/TestBuyerRegisterApi/*").permitAll()
                
                

				// .antMatchers("/**/**").permitAll()
				// 除上面外的所有请求全部需要鉴权认证
				.anyRequest().authenticated();

		// 添加JWT filter
		httpSecurity.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

		// 禁用缓存
		httpSecurity.headers().cacheControl();
		httpSecurity.headers().frameOptions().sameOrigin();
		httpSecurity.headers().contentTypeOptions();
		httpSecurity.headers().httpStrictTransportSecurity();
		httpSecurity.headers().xssProtection();
	}
}
