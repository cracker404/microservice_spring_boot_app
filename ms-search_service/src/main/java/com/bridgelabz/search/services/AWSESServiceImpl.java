package com.bridgelabz.search.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceResponse;
import com.amazonaws.DefaultRequest;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.AmazonHttpClient.RequestExecutionBuilder;
import com.amazonaws.http.HttpMethodName;
import com.bridgelabz.search.exception.FNException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnExpression("'${mode}'.equals('production')")
public class AWSESServiceImpl implements IESService {
	@Value("${es.region}")
	private String region;

	@Value("${es.endpoint}")
	private String endpoint;

	@Value("${es.service.name}")
	private String serviceName;

	@Autowired
	private BasicAWSCredentials credentials;

	@Autowired
	private AmazonHttpClient client;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ESResponseHandler responseHandler;

	@Autowired
	private ESErrorResponseHandler errorResponseHandler;

	@Override
	public <T> String save(T object, String index, String id) throws FNException {
		try {
			String json = mapper.writeValueAsString(object);

			Request<?> request = new DefaultRequest<Void>(serviceName);
			request.setContent(new ByteArrayInputStream(json.getBytes()));
			request.setEndpoint(URI.create(endpoint + "/" + index + "/" + index + "/" + id));
			request.setHttpMethod(HttpMethodName.POST);
			request.addHeader("Content-Type", "application/json");

			Response<AmazonWebServiceResponse<String>> response = builder(request).execute(responseHandler);

			String jsonResponse = response.getAwsResponse().getResult();

			JsonNode result = mapper.readTree(jsonResponse).get("result");

			if (result.asText().equals("created") || result.asText().equals("updated")) {
				return mapper.readTree(jsonResponse).get("_id").asText();
			}
		} catch (AmazonServiceException | IOException | IllegalArgumentException e) {
			throw new FNException(101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);

		}
		throw new FNException(101, new Object[] { "Document could not be saved in the index" });
	}

	@Override
	public <T> String update(T object, String index, String id) throws FNException {
		return save(object, index, id);
	}

	@Override
	public String getById(String id, String index) throws FNException {
		try {
			Request<?> request = new DefaultRequest<Void>(serviceName);
			request.setEndpoint(URI.create(endpoint + "/" + index + "/" + index + "/" + id));
			request.setHttpMethod(HttpMethodName.GET);

			Response<AmazonWebServiceResponse<String>> response = builder(request).execute(responseHandler);

			String jsonResponse = response.getAwsResponse().getResult();

			if (mapper.readTree(jsonResponse).get("found").asBoolean()) {
				JsonNode node = mapper.readTree(jsonResponse).get("_source");
				return mapper.treeToValue(node, String.class);
			}
		} catch (AmazonServiceException | IOException e) {
			throw new FNException(101, new Object[] { "getById by elastic -" + e.getMessage() }, e);
		}
		return null;
	}

	@Override
	public boolean deleteById(String id, String index) throws FNException {
		try {
			Request<?> request = new DefaultRequest<Void>(serviceName);
			request.setEndpoint(URI.create(endpoint + "/" + index + "/" + index + "/" + id));
			request.setHttpMethod(HttpMethodName.DELETE);

			builder(request).execute(responseHandler);
			return true;
		} catch (AmazonServiceException e) {
			String jsonResponse = e.getErrorMessage();
			try {
				if (mapper.readTree(jsonResponse).get("result").asText().equals("not_found")) {
					throw new FNException(101, new Object[] { "Document not found :-" + e.getMessage() }, e);
				}
			} catch (IOException e1) {
				throw new FNException(101, new Object[] { "Document not found :-" + e.getMessage() }, e);
			}
		} catch (IllegalArgumentException e) {
			throw new FNException(101, new Object[] { "Deleting from aws elastic :-" + e.getMessage() }, e);
		}
		return false;
	}

	@Override
	public List<String> multipleFieldSearchQuery(Map<String, Object> fieldValueMap, String index) throws FNException {
		BoolQueryBuilder builder = QueryBuilders.boolQuery();
		for (String field : fieldValueMap.keySet()) {
			builder.must(QueryBuilders.matchQuery(field, fieldValueMap.get(field)));
		}
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(builder);

		Request<?> request = new DefaultRequest<Void>(serviceName);
		request.setContent(new ByteArrayInputStream((sourceBuilder.toString()).getBytes()));
		request.setEndpoint(URI.create(endpoint + "/" + index + "/" + "_search"));
		request.setHttpMethod(HttpMethodName.POST);
		request.addHeader("Content-Type", "application/json");
		try {
			Response<AmazonWebServiceResponse<String>> response = builder(request).execute(responseHandler);

			String jsonResponse = response.getAwsResponse().getResult();
			List<String> results = new ArrayList<>();
			JsonNode arrayNode = mapper.readTree(jsonResponse).get("hits").get("hits");

			for (JsonNode jsonNode : arrayNode) {
				String t = mapper.treeToValue(jsonNode.get("_source"), String.class);
				results.add(t);
			}

			return results;
		} catch (IOException e) {
			throw new FNException(101,
					new Object[] { "multiFieldSearchWithOnlyAggSum by aws elastic -" + e.getMessage() }, e);
		}

	}

	@Override
	public List<String> filteredQuery(String field, Object value, String index) throws FNException {
		QueryBuilder builder = QueryBuilders.boolQuery()
				.filter(QueryBuilders.matchQuery(field, value).operator(Operator.AND));
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(builder);

		Request<?> request = new DefaultRequest<Void>(serviceName);
		request.setContent(new ByteArrayInputStream((sourceBuilder.toString()).getBytes()));
		request.setEndpoint(URI.create(endpoint + "/" + index + "/" + "_search"));
		request.setHttpMethod(HttpMethodName.POST);
		request.addHeader("Content-Type", "application/json");

		try {
			Response<AmazonWebServiceResponse<String>> response = builder(request).execute(responseHandler);

			String jsonResponse = response.getAwsResponse().getResult();
			List<String> results = new ArrayList<>();
			JsonNode arrayNode = mapper.readTree(jsonResponse).get("hits").get("hits");

			for (JsonNode jsonNode : arrayNode) {
				String t = mapper.treeToValue(jsonNode.get("_source"), String.class);
				results.add(t);
			}

			return results;
		} catch (AmazonServiceException | IOException e) {
			throw new FNException(101, new Object[] { "filteredQuery by aws elastic -" + e.getMessage() }, e);
		}
	}

	@Override
	public List<String> multipleFieldSearchWithWildcard(String text, Map<String, Float> fields,
			Map<String, Object> restrictions, String index) throws FNException {
		try {

			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.queryStringQuery(text).lenient(true).fields(fields));

			if (restrictions != null) {
				restrictions.forEach((field, value) -> boolQueryBuilder.must(QueryBuilders.matchQuery(field, value)));
			}

			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(boolQueryBuilder);

			Request<?> request = new DefaultRequest<Void>(serviceName);
			request.setContent(new ByteArrayInputStream((sourceBuilder.toString()).getBytes()));
			request.setEndpoint(URI.create(endpoint + "/" + index + "/" + "_search"));
			request.setHttpMethod(HttpMethodName.POST);
			request.addHeader("Content-Type", "application/json");

			try {
				Response<AmazonWebServiceResponse<String>> response = builder(request).execute(responseHandler);

				String jsonResponse = response.getAwsResponse().getResult();

				List<String> results = new ArrayList<>();
				JsonNode arrayNode = mapper.readTree(jsonResponse).get("hits").get("hits");

				for (JsonNode jsonNode : arrayNode) {
					String t = mapper.treeToValue(jsonNode.get("_source"), String.class);
					results.add(t);
				}

				return results;
			} catch (IOException e) {
				throw new FNException(101,
						new Object[] { "multiFieldSearchWithOnlyAggSum by aws elastic -" + e.getMessage() }, e);
			}
		} catch (Exception e) {
			throw new FNException(101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}
	}

	private RequestExecutionBuilder builder(Request<?> request) {
		AWS4Signer signer = new AWS4Signer();
		signer.setRegionName(region);
		signer.setServiceName(serviceName);
		signer.sign(request, credentials);

		RequestExecutionBuilder builder = client.requestExecutionBuilder();
		builder.request(request);
		builder.errorResponseHandler(errorResponseHandler);
		return builder;
	}

	@Override
	public List<String> searchByText(String index, String type, String text) throws FNException {
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		QueryBuilder builder = boolQueryBuilder.must(QueryBuilders.queryStringQuery(text).lenient(true));

		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.query(builder);
		sourceBuilder.fetchSource(null, new String[] {"password"});
		
		Request<?> request = new DefaultRequest<Void>(serviceName);
		request.setContent(new ByteArrayInputStream((sourceBuilder.toString()).getBytes()));
		request.setEndpoint(URI.create(endpoint + "/" + index + "/" + "_search"));
		request.setHttpMethod(HttpMethodName.POST);
		request.addHeader("Content-Type", "application/json");
		
		try {
			Response<AmazonWebServiceResponse<String>> response = builder(request).execute(responseHandler);

			String jsonResponse = response.getAwsResponse().getResult();

			List<String> results = new ArrayList<>();
			JsonNode arrayNode = mapper.readTree(jsonResponse).get("hits").get("hits");

			for (JsonNode jsonNode : arrayNode) {
				String t = mapper.treeToValue(jsonNode.get("_source"), String.class);
				results.add(t);
			}

			return results;
		} catch (IOException e) {
			throw new FNException(101,
					new Object[] { "multiFieldSearchWithOnlyAggSum by aws elastic -" + e.getMessage() }, e);
		}
	}
}