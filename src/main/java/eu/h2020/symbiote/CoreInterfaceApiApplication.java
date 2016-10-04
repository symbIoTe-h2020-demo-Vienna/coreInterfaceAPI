package eu.h2020.symbiote;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import eu.h2020.symbiote.model.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

@RestController
@RequestMapping("/search")
class SearchApiGatewayRestController {

	private final RestTemplate restTemplate;

	private static final String URL = "http://searchEngine/core_api/resources";

	@Autowired
	public SearchApiGatewayRestController(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

//	public Collection<String> fallback() {
//		return new ArrayList<>();
//	}

	@RequestMapping(method = RequestMethod.GET, value = "/query")
	public Collection<String> query(@RequestParam(value = "platform_id", required = false) String platformId,
									@RequestParam(value = "platform_name", required = false) String platformName,
									@RequestParam(value = "owner", required = false) String owner,
									@RequestParam(value = "name", required = false) String name,
									@RequestParam(value = "id", required = false) String id,
									@RequestParam(value = "description", required = false) String description,
									@RequestParam(value = "location_name", required = false) String locationName,
									@RequestParam(value = "observed_property", required = false) String observedProperty) {
		ParameterizedTypeReference<List<String>> ptr = new ParameterizedTypeReference<List<String>>() {
		};


		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL);

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
		if( observedProperty != null ) {
			builder.queryParam("observed_property", observedProperty);
		}
		HttpEntity<?> entity = new HttpEntity<>(headers);

		System.out.println( "URI is : " + builder.build().encode().toUri());
		ResponseEntity<List<String>> exchange = this.restTemplate.exchange(
				builder.build().encode().toUri(),
				HttpMethod.GET,
				entity,
				ptr);

		System.out.println(">>>>>>>>>>>>>>>>>>>>>>Got result: exchange" );
		System.out.println( exchange.getBody());
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" );

//		return exchange.getBody().getContent().stream().collect(Collectors.toList());

		return exchange.getBody();
//
//		ResponseEntity<Resources<String>> exchange = this.restTemplate.exchange("http://searchEngine/core_api/resources", HttpMethod.GET, null, ptr);
//
//		return exchange
//				.getBody()
//				.getContent()
//				.stream()
//				.collect(Collectors.toList());
	}

}