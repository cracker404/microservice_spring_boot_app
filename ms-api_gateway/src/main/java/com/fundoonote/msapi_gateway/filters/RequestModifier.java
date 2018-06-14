package com.fundoonote.msapi_gateway.filters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fundoonote.msapi_gateway.utils.TokenUtility;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class RequestModifier extends ZuulFilter {
	
	private static Logger log = LoggerFactory.getLogger(RequestModifier.class);
	
	@Autowired
	private TokenUtility tokenUtility;
	
	

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();
		
		HttpServletRequest request = context.getRequest();
		log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));
		
		Map<String, List<String>> params = context.getRequestQueryParams();
		if (params == null) {
			params = new HashMap<>();
		}
		if (!request.getRequestURI().contains("login") && !request.getRequestURI().contains("registration")) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			params.put("userid", Arrays.asList(authentication.getName()));
			System.out.println("Called");
			context.setRequestQueryParams(params);
		}
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 0;
	}

}
