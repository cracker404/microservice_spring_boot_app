package com.fundoonote.msapi_gateway.filters;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class RequestLogger extends ZuulFilter {

	List<String> ignoreUrls;
	private static Logger log = LoggerFactory.getLogger(RequestLogger.class);
	
	@PostConstruct
	public void init(){
		ignoreUrls = Arrays.asList("activate", "oauth/token", "user/save", 
				"forgotpassword", "resetpassword", "changepassword");
	}
	@Override
	public boolean shouldFilter() 
	{
		RequestContext context = RequestContext.getCurrentContext();
		HttpServletRequest request = context.getRequest();
		
		boolean match =  ignoreUrls.stream().anyMatch(s -> request.getRequestURI().contains(s));
		return match;
	}


	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();

		HttpServletRequest request = context.getRequest();
	
		log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 9;
	}

}
