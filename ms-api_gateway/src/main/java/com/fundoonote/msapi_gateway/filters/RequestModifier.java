package com.fundoonote.msapi_gateway.filters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

@Component
public class RequestModifier extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();
		Map<String, List<String>> params = context.getRequestQueryParams();
		if (params == null) {
			params = new HashMap<>();
		}
		params.put("my-test-key", Arrays.asList("my-test-value"));
		System.out.println("Called");
		context.setRequestQueryParams(params);
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
