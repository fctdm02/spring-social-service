package org.springframework.social.flickr.api.impl;

import org.springframework.social.flickr.api.Comment;
import org.springframework.social.flickr.api.Comments;
import org.springframework.social.flickr.api.PhotosetsCommentsOperations;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author HemantS
 *
 */
public class PhotosetsCommentsTemplate extends AbstractFlickrOperations
		implements PhotosetsCommentsOperations {
	private final RestTemplate restTemplate;

	public PhotosetsCommentsTemplate(RestTemplate restTemplate,
			boolean isAuthorizedForUser) {
		super(isAuthorizedForUser);
		this.restTemplate = restTemplate;
	}

	@Override
	public Comment addComment(String apiKey, String photosetId,
			String commentText) {
		requireAuthorization();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		if (apiKey != null)
			parameters.set("api_key", apiKey);
		if (photosetId != null)
			parameters.set("photoset_id", photosetId);
		if (commentText != null)
			parameters.set("comment_text", commentText);
		return restTemplate.postForObject(
				buildUri("flickr.photosets.comments.addComment"), parameters,
				Comment.class);
	}

	@Override
	public void deleteComment(String apiKey, String commentId) {
		requireAuthorization();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		if (apiKey != null)
			parameters.set("api_key", apiKey);
		if (commentId != null)
			parameters.set("comment_id", commentId);
		restTemplate.postForObject(
				buildUri("flickr.photosets.comments.deleteComment"),
				parameters, Object.class);
	}

	@Override
	public void editComment(String apiKey, String commentId, String commentText) {
		requireAuthorization();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		if (apiKey != null)
			parameters.set("api_key", apiKey);
		if (commentId != null)
			parameters.set("comment_id", commentId);
		if (commentText != null)
			parameters.set("comment_text", commentText);
		restTemplate.postForObject(
				buildUri("flickr.photosets.comments.editComment"), parameters,
				Object.class);
	}

	@Override
	public Comments getList(String apiKey, String photosetId) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
		if (apiKey != null)
			parameters.set("api_key", apiKey);
		if (photosetId != null)
			parameters.set("photoset_id", photosetId);
		return restTemplate.getForObject(
				buildUri("flickr.photosets.comments.getList", parameters),
				Comments.class);
	}
}