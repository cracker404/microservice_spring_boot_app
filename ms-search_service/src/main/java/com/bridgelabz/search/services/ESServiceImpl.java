package com.bridgelabz.search.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.bridgelabz.search.exception.FNException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@ConditionalOnExpression("'${mode}'.equals('development')")
public class ESServiceImpl implements IESService {
	@Autowired
	private RestHighLevelClient client;

	@Autowired
	private ObjectMapper mapper;

	public ESServiceImpl() {
		// Default Constructor
	}

	@Override
	public <T> String save(T object, String index, String id) throws FNException {
		String json;
		try {
			json = mapper.writeValueAsString(object);
			IndexRequest indexRequest = new IndexRequest(index, index, id);
			indexRequest.source(json, XContentType.JSON);
			IndexResponse response = client.index(indexRequest);
			return response.getId();
		} catch (IllegalArgumentException | IOException e) {
			throw new FNException(101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}

	}

	@Override
	public <T> String update(T object, String index, String id) throws FNException {
		String json;
		try {
			json = mapper.writeValueAsString(object);
			UpdateRequest updateRequest = new UpdateRequest(index, index, id);
			updateRequest.doc(json, XContentType.JSON);
			UpdateResponse response = client.update(updateRequest);
			return response.getId();
		} catch (IllegalArgumentException | IOException e) {
			throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}
	}

	@Override
	public String getById(String id, String index) throws FNException {
		try {
			GetRequest getRequest = new GetRequest(index, index, id);
			GetResponse response = client.get(getRequest);
			if (response.isExists()) {
				return response.getSourceAsString();
			}
		} catch (IOException e) {
			throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}
		return null;
	}

	@Override
	public boolean deleteById(String id, String index) throws FNException {
		try {
			DeleteRequest deleteRequest = new DeleteRequest(index, index, id);
			DeleteResponse response = client.delete(deleteRequest);
			if (response.getResult().equals(Result.DELETED) || response.getResult().equals(Result.NOT_FOUND)) {
				return true;
			} else {
				throw new FNException(-107, new Object[] { "deleting object", "Object not found" });
			}
		} catch (IllegalArgumentException | IOException e) {
			throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}
	}

	@Override
	public List<String> filteredQuery(String field, Object value, String index) throws FNException {
		try {
			QueryBuilder builder = QueryBuilders.boolQuery()
					.filter(QueryBuilders.matchQuery(field, value).operator(Operator.AND));
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(builder);
			SearchRequest request = new SearchRequest(index).types(index).source(sourceBuilder);
			SearchResponse response = client.search(request);

			List<String> results = new ArrayList<>();

			for (SearchHit hit : response.getHits()) {
				results.add(hit.getSourceAsString());
			}
			return results;
		} catch (IOException e) {
			throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}
	}

	@Override
	public List<String> multipleFieldSearchQuery(Map<String, Object> fieldValueMap, String index) throws FNException {
		try {
			BoolQueryBuilder builder = QueryBuilders.boolQuery();
			for (Map.Entry<String, Object> entry : fieldValueMap.entrySet()) {
				builder.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()).operator(Operator.AND));
			}
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(builder);
			SearchRequest request = new SearchRequest(index).types(index).source(sourceBuilder);
			SearchResponse response = client.search(request);

			List<String> results = new ArrayList<>();

			for (SearchHit hit : response.getHits()) {
				results.add(hit.getSourceAsString());
			}
			return results;
		} catch (IOException e) {
			throw new FNException(-101, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}
	}

	@Override
	public List<String> multipleFieldSearchWithWildcard(String text, Map<String, Float> fields,
			Map<String, Object> restrictions, String index) throws FNException {

		try {

			if (!text.startsWith("*")) {
				text = "*" + text;
			}
			if (!text.endsWith("*")) {
				text = text + "*";
			}

			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			boolQueryBuilder.must(QueryBuilders.queryStringQuery(text).lenient(true).fields(fields));

			if (restrictions != null) {
				restrictions.forEach((field, value) -> boolQueryBuilder
						.must(QueryBuilders.matchQuery(field, value).operator(Operator.AND)));
			}

			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(boolQueryBuilder);

			SearchRequest searchRequest = new SearchRequest(index).types(index).source(sourceBuilder);
			SearchResponse searchResponse;
			searchResponse = client.search(searchRequest);

			List<String> results = new ArrayList<>();
			for (SearchHit hit : searchResponse.getHits()) {
				results.add(hit.getSourceAsString());
			}

			return results;
		} catch (Exception e) {
			throw new FNException(-110, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}

	}

	public List<String> searchByText(String index, String type, String text) throws FNException {

		try {
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			QueryBuilder builder = boolQueryBuilder.must(QueryBuilders.queryStringQuery(text).lenient(true));
			SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
			sourceBuilder.query(builder);
			sourceBuilder.fetchSource(null, new String[] {"password"});
			SearchRequest searchRequest = new SearchRequest(index).types(type).source(sourceBuilder);
			SearchResponse searchResponse = client.search(searchRequest);

			List<String> results = new ArrayList<>();
			for (SearchHit hit : searchResponse.getHits()) {
				results.add(hit.getSourceAsString());
			}

			return results;
		} catch (Exception e) {
			throw new FNException(-110, new Object[] { e.getMessage() + "(" + e.getMessage() + ")" }, e);
		}
	}

}
