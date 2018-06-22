package com.fundoonote.msapi_gateway.filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
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
	
	List<String> ignoreUrls;
	private static Logger log = LoggerFactory.getLogger(RequestModifier.class);
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
		
		boolean match =  ignoreUrls.stream().noneMatch(s -> request.getRequestURI().contains(s));
		return match;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();

		HttpServletRequest request = context.getRequest();
	
		log.info(String.format("%s request to %s", request.getMethod(), request.getRequestURL().toString()));

		//Map<String, String[]> params = request.getParameterMap();
		
		//Map<String, List<String>> params = context.getRequestQueryParams();
		/*if (params == null) {
			params = new HashMap<>();
		}*/
		/*if (!request.getRequestURI().contains("activate") && !request.getRequestURI().contains("save") 
				&& !request.getRequestURI().contains("oauth/token")&& !request.getRequestURI().contains("forgotpassword")) 
		{*/
			try
			{
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
				//params.put("userId", Arrays.asList(authentication.getName()));
				//params.put("userId", new String [] {authentication.getName()});
				//context.set("userId",authentication.getName() );
				//context.setRequestQueryParams(param);
				context.addZuulRequestHeader("userId",authentication.getName());
			} 
			catch (Exception e) 
			{
				return sendUnauthorized(context);
			}
		//}
		return null;
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 10;
	}
	
	private Object sendUnauthorized(RequestContext context) {
		context.setResponseStatusCode(401);
		context.setResponseBody("UnAuthorized");
		context.setSendZuulResponse(false);
		return null;
	}

}
