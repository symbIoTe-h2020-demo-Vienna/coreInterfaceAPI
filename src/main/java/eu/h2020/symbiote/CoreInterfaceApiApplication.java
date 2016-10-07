package eu.h2020.symbiote;

import com.google.gson.Gson;
import eu.h2020.symbiote.model.ResourceUrlQueryModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@EnableCircuitBreaker
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class CoreInterfaceApiApplication {

	@LoadBalanced
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(CoreInterfaceApiApplication.class, args);
	}
}

@CrossOrigin
@RestController
@RequestMapping("/search")
class SearchApiGatewayRestController {

	private final RestTemplate restTemplate;

	private static final String SEARCH_URL = "http://searchEngine/core_api/resources";

	private static final String RESOURCEFINDER_URL = "http://coreResourceAccessMonitor/cram_api/resource_urls";

	private static Log log = LogFactory.getLog(SearchApiGatewayRestController.class);

	@Autowired
	public SearchApiGatewayRestController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/resource_url", produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String,String> requestAccessUrl(@RequestParam(value = "resourceIds") String[] resourceIds ) {

		ParameterizedTypeReference<Map<String,String>> ptr = new ParameterizedTypeReference<Map<String,String>>() {
		};

		System.out.println("Handling resource url endpoint");

		ResourceUrlQueryModel model = new ResourceUrlQueryModel(resourceIds);
		Gson gson = new Gson();
		String jsoned = gson.toJson(model);

//		StringEntity input = null;
//		try {
//			input = new StringEntity(jsoned);
//			input.setContentType("application/json");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(jsoned,headers);

		ResponseEntity<Map<String,String>> exchange = this.restTemplate.exchange(
				RESOURCEFINDER_URL,
				HttpMethod.POST,
				entity,
				ptr);

		System.out.println(exchange.getBody());

		return exchange.getBody();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
	public String query(@RequestParam(value = "platform_id", required = false) String platformId,
									@RequestParam(value = "platform_name", required = false) String platformName,
									@RequestParam(value = "owner", required = false) String owner,
									@RequestParam(value = "name", required = false) String name,
									@RequestParam(value = "id", required = false) String id,
									@RequestParam(value = "description", required = false) String description,
									@RequestParam(value = "location_name", required = false) String locationName,
									@RequestParam(value = "location_lat", required = false) Double locationLat,
									@RequestParam(value = "location_long", required = false) Double locationLong,
									@RequestParam(value = "max_distance", required = false) Integer maxDistance,
									@RequestParam(value = "observed_property", required = false) String observedProperty) {
		ParameterizedTypeReference<String> ptr = new ParameterizedTypeReference<String>() {
		};

		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SEARCH_URL);

		if( platformId != null ) {
			builder.queryParam("platform_id", platformId);
		}
		if( platformName != null ) {
			builder.queryParam("platform_name", platformName);
		}
		if( owner != null ) {
			builder.queryParam("owner", owner);
		}
		if( name != null ) {
			builder.queryParam("name", name);
		}
		if( id != null ) {
			builder.queryParam("id", id);
		}
		if( description != null ) {
			builder.queryParam("description", description);
		}
		if( locationName != null ) {
			builder.queryParam("location_name", locationName);
		}
		if( locationLat != null && locationLong != null) {
			String ptquery = "" + locationLat + "," +locationLong;
			builder.queryParam("location_point",ptquery);
		}
		if( maxDistance != null ) {
			builder.queryParam("max_distance",maxDistance);
		}
		if( observedProperty != null ) {
			builder.queryParam("observed_property", observedProperty);
		}
		HttpEntity<?> entity = new HttpEntity<>(headers);

		ResponseEntity<String> exchange = this.restTemplate.exchange(
				builder.build().encode().toUri(),
				HttpMethod.GET,
				entity,
				ptr);

		log.debug(">>>>>    Got search result: " + exchange.getBody() );

		return exchange.getBody();
	}

}