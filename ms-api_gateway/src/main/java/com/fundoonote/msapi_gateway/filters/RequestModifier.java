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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.fundoonote.msapi_gateway.chache.RedisService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class RequestModifier extends ZuulFilter {
	@Autowired
	private RedisService redisService;

	private static Logger log = LoggerFactory.getLogger(RequestModifier.class);

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();

		HttpServletRequest request = context.getRequest();
		log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

		Map<String, List<String>> params = context.getRequestQueryParams();
		if (params == null) {
			params = new HashMap<>();
		}
		if (!request.getRequestURI().contains("login") && !request.getRequestURI().contains("register") && !request.getRequestURI().contains("oauth/token")) {
			try {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				
				Object object = redisService.get("USER", "user" + authentication.getName());
				
				if (object == null) {
					return sendUnauthorized(context);
				}
				
				Map<String, Object> map = (Map<String, Object>) object;
				
				boolean isValid = false;
				String roleFromRedis = map.get("role").toString();

				for (GrantedAuthority authority : authentication.getAuthorities()) {
					if (authority.getAuthority().equals(roleFromRedis)) {
						isValid = true;
						break;
					}
				}
				if (!isValid) {
					return sendUnauthorized(context);
				}
				params.put("userid", Arrays.asList(authentication.getName()));
				context.setRequestQueryParams(params);
			} catch (Exception e) {
				return sendUnauthorized(context);
			}
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
	
	private Object sendUnauthorized(RequestContext context) {
		context.setResponseStatusCode(401);
		context.setResponseBody("UnAuthorized");
		context.setSendZuulResponse(false);
		return null;
	}

}
