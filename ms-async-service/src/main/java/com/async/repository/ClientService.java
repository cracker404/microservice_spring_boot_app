package com.async.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.async.response.Response;

@FeignClient(name="MS-SEARCH", fallbackFactory=ClientFallbackFactory.class)
public interface ClientService 
{
	@PostMapping(value = "/save", 
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces =MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Response> save(@RequestBody Object object, @RequestParam("index") String index, 
			@RequestParam("id") String Id);
	
	@PutMapping(value = "/update",
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces =MediaType.APPLICATION_JSON_VALUE )
	ResponseEntity<Response> update(@RequestBody Object object, @RequestParam("index") String index, 
			@RequestParam("id") String Id);
	
	@DeleteMapping(value = "/delete",
			consumes=MediaType.APPLICATION_JSON_VALUE,
			produces =MediaType.APPLICATION_JSON_VALUE )
	Boolean deleteById(@RequestParam("index") String index, @RequestParam("id") String Id);
	
	@GetMapping("/")
	String send();

}
/*static class HystrixClientFallback implements HystrixClient {
    @Override
    public Hello iFailSometimes() {
        return new Hello("fallback");
    }
}*/
