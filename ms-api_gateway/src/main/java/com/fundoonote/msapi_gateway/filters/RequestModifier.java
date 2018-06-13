package com.fundoonote.msapi_gateway.filters;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class RequestModifier extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext context = RequestContext.getCurrentContext();
		Map<String, List<String>> params = context.getRequestQueryParams();
		params.put("my-test-key", Arrays.asList("my-test-value"));
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
