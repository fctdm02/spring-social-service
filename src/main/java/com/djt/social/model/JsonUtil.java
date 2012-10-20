/**
 * 
 */
package com.djt.social.model;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * Static utility class with helper methods for serializing/deserializing 
 * Social Service related model objects into/from JSON.
 * 
 * @author Tom.Myers
 */
public final class JsonUtil {
	
	/* */
	private static final String JSON_PARSE_ERROR = "Unable to parse JSON: ";
	
	/* */
	private JsonUtil() {	
	}

	/**
	 * 
	 * @param jsonString
	 * @return
	 * @throws IOException 
	 */
	public static SocialProfileResponse deserializeSocialProfileFromJson(String jsonString) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonString, new TypeReference<SocialProfileResponse>() {});
		} catch (Exception e) {
			throw new IllegalStateException(JSON_PARSE_ERROR + jsonString, e);
		}
	}
	
	/**
	 * 
	 * @param jsonString
	 * @return
	 * @throws IOException 
	 */
	public static SocialAlbumsResponse deserializeSocialAlbumsFromJson(String jsonString) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonString, new TypeReference<SocialAlbumsResponse>() {});
		} catch (Exception e) {
			throw new IllegalStateException(JSON_PARSE_ERROR + jsonString, e);
		}
	}

	/**
	 * 
	 * @param jsonString
	 * @return
	 * @throws IOException 
	 */
	public static SocialPhotosResponse deserializeSocialPhotosFromJson(String jsonString) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonString, new TypeReference<SocialPhotosResponse>() {});
		} catch (Exception e) {
			throw new IllegalStateException(JSON_PARSE_ERROR + jsonString, e);
		}
	}
	
	/**
	 * 
	 * @param jsonString
	 * @return
	 * @throws IOException 
	 */
	public static SocialVideosResponse deserializeSocialVideosFromJson(String jsonString) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonString, new TypeReference<SocialVideosResponse>() {});
		} catch (Exception e) {
			throw new IllegalStateException(JSON_PARSE_ERROR + jsonString, e);
		}
	}
	
	/**
	 * 
	 * @param jsonString
	 * @return
	 * @throws IOException 
	 */
	public static SocialFriendsResponse deserializeSocialFriendsFromJson(String jsonString) throws IOException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonString, new TypeReference<SocialFriendsResponse>() {});
		} catch (Exception e) {
			throw new IllegalStateException(JSON_PARSE_ERROR + jsonString, e);
		}
	}
}