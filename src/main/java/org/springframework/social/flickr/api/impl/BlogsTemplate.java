package org.springframework.social.flickr.api.impl;

import org.springframework.social.flickr.api.Blogs;
import org.springframework.social.flickr.api.BlogsOperations;
import org.springframework.social.flickr.api.Services;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author HemantS
 *
 */
public class BlogsTemplate extends AbstractFlickrOperations implements
		BlogsOperations {
	private final RestTemplate restTemplate;

	public BlogsTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
		super(isAuthorizedForUser);
		this.restTemplate = restTemplate;
	}

	@Override
	public Blogs getList(String apiKey, String service) {
		requireAuthorization();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		if (apiKey != null)
			parameters.set("api_key", apiKey);
		if (service != null)
			parameters.set("service", service);
		return restTemplate.getForObject(
				buildUri("flickr.blogs.getList", parameters), Blogs.class);
	}

	@Override
	public Services getServices(String apiKey) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		if (apiKey != null)
			parameters.set("api_key", apiKey);
		return restTemplate.getForObject(
				buildUri("flickr.blogs.getServices", parameters),
				Services.class);
	}

	@Override
	public void postPhoto(String apiKey, String blogId, String photoId,
			String title, String description, String blogPassword,
			String service) {
		requireAuthorization();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		if (apiKey != null)
			parameters.set("api_key", apiKey);
		if (blogId != null)
			parameters.set("blog_id", blogId);
		if (photoId != null)
			parameters.set("photo_id", photoId);
		if (title != null)
			parameters.set("title", title);
		if (description != null)
			parameters.set("description", description);
		if (blogPassword != null)
			parameters.set("blog_password", blogPassword);
		if (service != null)
			parameters.set("service", service);
		restTemplate.postForObject(buildUri("flickr.blogs.postPhoto"),
				parameters, Object.class);
	}
}