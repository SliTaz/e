package com.zbensoft.e.payment.api.config.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class CORSInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Object arg2) throws Exception {
		
		
		 HttpServletRequest request = (HttpServletRequest) servletRequest;
		    String method = request.getMethod();
		    if(method.equals("OPTIONS") || method.equals("options"))
		    {
		        HttpServletResponse response = (HttpServletResponse) servletResponse;
		        response.setHeader("Access-Control-Allow-Origin", "*");
		        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE,PATCH");
		        response.setHeader("Access-Control-Max-Age", "3600");
		        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
		        response.setStatus(200);
		    }
		    else
		    {
		        HttpServletResponse response = (HttpServletResponse) servletResponse;
		        response.setHeader("Access-Control-Allow-Origin", "*");
		        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE,PATCH");
		        response.setHeader("Access-Control-Max-Age", "3600");
		        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
		    }
		    
//		response.addHeader("Access-Control-Allow-Origin", "*");
//		response.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,HEAD,PATCH,OPTIONS,TRACE");
//		response.addHeader("Access-Control-Allow-Headers",
//				"Origin, X-Requested-With, Content-Type, Accept,Authorization");
//		//response.addHeader("cors-allowed-headers", "Origin, X-Requested-With, Content-Type, Accept,Authorization");
//		response.addHeader("Access-Control-Allow-Credentials", "true");

		return true;
	}

}
