package com.zbensoft.e.payment.api.config.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfigurer extends WebMvcConfigurerAdapter {

	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CORSInterceptor()).addPathPatterns("/**");
	}

}
