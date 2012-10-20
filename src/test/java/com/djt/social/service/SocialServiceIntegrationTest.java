package com.djt.social.service;

import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By.ById;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.djt.social.AbstractSocialServiceIntegrationTest;
import com.djt.social.model.JsonUtil;
import com.djt.social.model.SocialAlbum;
import com.djt.social.model.SocialAlbumsResponse;
import com.djt.social.model.SocialFriendsResponse;
import com.djt.social.model.SocialPhoto;
import com.djt.social.model.SocialPhotosResponse;
import com.djt.social.model.SocialProfile;
import com.djt.social.model.SocialProfileResponse;
import com.djt.social.model.SocialVideo;
import com.djt.social.model.SocialVideosResponse;
import com.djt.social.service.ISocialService;

/**
 * 
 * @author Tom.Myers
 *
 * <p/>
 * <a href=\"http://release.seleniumhq.org/selenium-ide/1.8.1/selenium-ide-1.8.1.xpi\">Selenium Browser Plugin for creating tests by recording</a><br>
 * <a href=\"http://arquillian.org/guides/functional_testing_using_drone\">Functional Unit Testing with Arquillian Drone and Selenium</a><br>
 * <a href=\"http://docs.jboss.org/arquillian/reference/1.0.0.Alpha5/en-US/html/container.reference.html#container.tomcat-embedded-6\">Embedded Tomcat Container Extension for Arquillan</a><br>
 */
@RunWith(Arquillian.class)
public class SocialServiceIntegrationTest extends AbstractSocialServiceIntegrationTest {

	private static String TEST_ACCESS_TOKEN = null;
	
	private static final String GET_SOCIAL_PROFILE_REST_SIGNATURE 							= "/profile?promotionDeployPath={promotionDeployPath}&providerId={providerId}&accessToken={accessToken}&accessTokenSecret={accessTokenSecret}";	
	private static final String GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE 						= "/photoalbums?promotionDeployPath={promotionDeployPath}&providerId={providerId}&id={id}&offset={offset}&limit={limit}&accessToken={accessToken}&accessTokenSecret={accessTokenSecret}";
	private static final String GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE 	= "/photos?promotionDeployPath={promotionDeployPath}&providerId={providerId}&id={id}&offset={offset}&limit={limit}&accessToken={accessToken}&accessTokenSecret={accessTokenSecret}";
	private static final String GET_SOCIAL_VIDEO_ALBUMS_REST_SIGNATURE 						= "/videoalbums?promotionDeployPath={promotionDeployPath}&providerId={providerId}&id={id}&offset={offset}&limit={limit}&accessToken={accessToken}&accessTokenSecret={accessTokenSecret}";
	private static final String GET_SOCIAL_VIDEOS_FROM_SOCIAL_VIDEO_ALBUM_REST_SIGNATURE 	= "/videos?promotionDeployPath={promotionDeployPath}&providerId={providerId}&id={id}&offset={offset}&limit={limit}&accessToken={accessToken}&accessTokenSecret={accessTokenSecret}";
	private static final String GET_SOCIAL_FRIENDS_REST_SIGNATURE 							= "/friends?promotionDeployPath={promotionDeployPath}&providerId={providerId}&id={id}&offset={offset}&limit={limit}&accessToken={accessToken}&accessTokenSecret={accessTokenSecret}";
	
	@Drone 
	protected HtmlUnitDriver driver;	
	
	@ArquillianResource 
	protected URL deploymentUrl;
	
	
	protected String promotionDeployPath = "djt/dev-wks-testpromo";

	
	// TODO: What happens when these access tokens expire?
	protected String testFacebookUserId = "100003986680460";
	protected String testFacebookAlbumId = "122532174556338";
	protected String testFacebookAccessToken = "AAADrESbHw04BACHeQyvV0kd4G2ZCuOPxCTfHoZBKneakZBS1ZBcuQ5u9ZCGApwltVEAUlkp7UxlrahIyfngZCHviiESg4SMwA53G3W6QUYsAZDZD";
	protected String testFacebookAccessTokenSecret = "";
    protected String testFacebookApiKey = "478237082200706";
    protected String testFacebookApiKeySecret = "83d02f698a33cfdcf479e05905f39609";
	
	protected String testInstagramUserId = "184525800";
	protected String testInstagramAlbumId = "0";
	protected String testInstagramAccessToken = "184525800.aeb2ea9.9a1a302a35af43d3bea0d8a851eb5a6c";
	protected String testInstagramAccessTokenSecret = "";

	protected String testFlickrAlbumId = "0";
	protected String testFlickrAccessToken = "72157631328487046-86074216899be19a";
	protected String testFlickrAccessTokenSecret = "e2fc15d4647279e3";
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		
	}
	
	@Before
	public void setUp() throws Exception {
		System.clearProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME);
		driver = new HtmlUnitDriver(true);
		
		String url = deploymentUrl + "rest/social/cacheremoveall";
		driver.get(url);
	}
	
	@After
	public void tearDown() throws Exception {
		System.clearProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME);
	}

	@Test
	@RunAsClient
	public void test_installSocialProviderApiKeyForPromotion_facebook() throws Exception {

		String url = deploymentUrl + "ux/operations/home";
		
		driver.get(url);

		WebElement formWebElement = driver.findElementById("install_api_key");
		
		WebElement promotionDeployPathField = formWebElement.findElement(new ById("promotionDeployPath"));
		promotionDeployPathField.sendKeys("djt/localhost-promo");
		
		WebElement providerIdField = formWebElement.findElement(new ById("providerId"));
		providerIdField.sendKeys(ISocialService.PROVIDER_NAME_FACEBOOK);
		
		WebElement apiKeyField = formWebElement.findElement(new ById("apiKey"));
		apiKeyField.sendKeys(testFacebookApiKey);
		
		WebElement apiKeySecretField = formWebElement.findElement(new ById("apiKeySecret"));
		apiKeySecretField.sendKeys(testFacebookApiKeySecret);

		formWebElement.submit();
		
		String actual = driver.getPageSource();
		Assert.assertTrue("Installation of social provider API Key failed", actual.indexOf("Installed API Key for social media provider") >= 0);
	}	

	//@Test
	//@RunAsClient
	public void test_connect_facebook() throws Exception {

		String url = deploymentUrl + "ux/operations/home";
		
		driver.get(url);

		WebElement formWebElement = driver.findElementById("install_api_key");
		
		WebElement promotionDeployPathField = formWebElement.findElement(new ById("promotionDeployPath"));
		promotionDeployPathField.sendKeys("djt/localhost-promo");
		
		WebElement providerIdField = formWebElement.findElement(new ById("providerId"));
		providerIdField.sendKeys(ISocialService.PROVIDER_NAME_FACEBOOK);
		
		WebElement apiKeyField = formWebElement.findElement(new ById("apiKey"));
		apiKeyField.sendKeys(testFacebookApiKey);
		
		WebElement apiKeySecretField = formWebElement.findElement(new ById("apiKeySecret"));
		apiKeySecretField.sendKeys(testFacebookApiKeySecret);

		formWebElement.submit();

		
		// Get the test web app home page.
		url = deploymentUrl + "ux/operations/home";
		driver.get(url);

		
		// Submit the "connect to facebook" form.
		formWebElement = driver.findElementById("fb_connect");
		promotionDeployPathField = formWebElement.findElement(new ById("promotionDeployPath"));
		promotionDeployPathField.sendKeys("djt/localhost-promo");
		WebElement id = formWebElement.findElement(new ById("uid"));
		id.sendKeys("ptqa@eprize.com");
		formWebElement.submit();

		
		// See if we need to authenticate with facebook.
		String pageSource = driver.getPageSource();
		if (pageSource.contains("login_form")) {
			
			formWebElement = driver.findElementById("login_form");
			WebElement email = formWebElement.findElement(new ById("email"));
			email.sendKeys("ptqa@eprize.com");
			WebElement password = formWebElement.findElement(new ById("pass"));
			password.sendKeys("QAtesting");
			formWebElement.submit();
			
			pageSource = driver.getPageSource();
		}
		
		
		// We should have the connection response, which contains the access token.
		int index = pageSource.indexOf("accessToken=");
		if (index != -1) {
			String accessToken = pageSource.substring(index+12);
			index = accessToken.indexOf("</h4>");
			if (index != -1) {
				accessToken = accessToken.substring(0, index);
				accessToken = accessToken.trim();
				TEST_ACCESS_TOKEN = accessToken;
			}
		}
		
		Assert.assertNotNull("access token is null", TEST_ACCESS_TOKEN);
	}	
	
	//@Test
	//@RunAsClient
	public void test_getStoredAccessToken_facebook() throws Exception {

		// Get the test web app home page.
		String url = deploymentUrl + "ux/operations/home";
		driver.get(url);

		
		// Submit the "connect to facebook" form.
		WebElement formWebElement = driver.findElementById("fb_connect");
		WebElement promotionDeployPathField = formWebElement.findElement(new ById("promotionDeployPath"));
		promotionDeployPathField.sendKeys("djt/localhost-promo");
		WebElement id = formWebElement.findElement(new ById("uid"));
		id.sendKeys("ptqa@eprize.com");
		formWebElement.submit();

		
		// See if we need to authenticate with facebook.
		String pageSource = driver.getPageSource();
		if (pageSource.contains("login_form")) {
			
			formWebElement = driver.findElementById("login_form");
			WebElement email = formWebElement.findElement(new ById("email"));
			email.sendKeys("ptqa@eprize.com");
			WebElement password = formWebElement.findElement(new ById("pass"));
			password.sendKeys("QAtesting");
			formWebElement.submit();
			
			pageSource = driver.getPageSource();
		}
		
		
		// We should have the connection response, which contains the access token.
		int index = pageSource.indexOf("accessToken=");
		if (index != -1) {
			String accessToken = pageSource.substring(index+12);
			index = accessToken.indexOf("</h4>");
			if (index != -1) {
				accessToken = accessToken.substring(0, index);
				accessToken = accessToken.trim();
				TEST_ACCESS_TOKEN = accessToken;
			}
		}
		
		Assert.assertNotNull("access token is null", TEST_ACCESS_TOKEN);

		
		url = deploymentUrl + "ux/operations/home";
		driver.get(url);
		formWebElement = driver.findElementById("get_access_token");
		promotionDeployPathField = formWebElement.findElement(new ById("promotionDeployPath"));
		promotionDeployPathField.sendKeys("djt/localhost-promo");
		WebElement providerIdField = formWebElement.findElement(new ById("providerId"));
		providerIdField.sendKeys(ISocialService.PROVIDER_NAME_FACEBOOK);
		id = formWebElement.findElement(new ById("id"));
		id.sendKeys("ptqa@eprize.com");
		formWebElement.submit();

		
		String actual = driver.getPageSource();
		String expected = "{ \"error\": \"\", \"accessToken\": \"" + TEST_ACCESS_TOKEN + "\", \"accessTokenSecret\": \"\" }";
		Assert.assertTrue("access token not found", actual.contains(expected));
	}	
	
	@Test
	@RunAsClient
	public void test_getStoredAccessToken_facebook_nocache() throws Exception {

		String url = deploymentUrl + "ux/operations/home";
		driver.get(url);
		WebElement formWebElement = driver.findElementById("get_access_token");
		WebElement promotionDeployPathField = formWebElement.findElement(new ById("promotionDeployPath"));
		promotionDeployPathField.sendKeys("djt/localhost-promo");
		WebElement providerIdField = formWebElement.findElement(new ById("providerId"));
		providerIdField.sendKeys(ISocialService.PROVIDER_NAME_FACEBOOK);
		WebElement id = formWebElement.findElement(new ById("id"));
		id.sendKeys("ptqa@eprize.com");
		formWebElement.submit();

		
		// String actual = driver.getPageSource();
		// String expected = "{ \"error\": \"\", \"accessToken\": \"" + TEST_ACCESS_TOKEN + "\", \"accessTokenSecret\": \"\" }";
		// Assert.assertTrue("access token not found", actual.contains(expected));
	}	
	
	@Test
	@RunAsClient
	public void test_getStatus() throws Exception {

		String url = deploymentUrl + "rest/social/version";
		driver.get(url);

		String expectedPageSourceVersion = "{\"error\":null,\"version\":\"";
		String expectedPageSourceTimestamp = "\",\"timestamp\":\"";
		String actualPageSource = driver.getPageSource();
		Assert.assertTrue("version information is missing", actualPageSource.contains(expectedPageSourceVersion));
		Assert.assertTrue("timestamp information is missing", actualPageSource.contains(expectedPageSourceTimestamp));
	}
	
	@Test
	@RunAsClient
	public void test_profile_invalidServiceProvider() throws Exception {
	
		// STEP 1: ARRANGE
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PROFILE_REST_SIGNATURE,
			"dummy",
			"dummy",
			"dummy",
			"dummy");

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String expectedJsonResponse = "{\"error\":\"getSocialProfile(promotionDeployPath=dummy, providerId=dummy, accessToken=dummy, accessTokenSecret=dummy), error=No API Key was found for social provider: [dummy] and promotion: [dummy]. Please install the appropriate API Keys and try again.\",\"socialProfile\":null}";
		String actualJsonResponse = driver.getPageSource();
		Assert.assertEquals("response for invalid service provider is incorrect", expectedJsonResponse, actualJsonResponse);
	}
	
	@Test
	@RunAsClient
	public void test_profile_instagram_invalidAccessToken() throws Exception {
	
		// STEP 1: ARRANGE
		String methodSignature = performTokenReplacementOnMethodSignature(
			promotionDeployPath,
			GET_SOCIAL_PROFILE_REST_SIGNATURE,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			"dummy",
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String expectedJsonResponse = "No service was found.";
		String actualJsonResponse = driver.getPageSource();
		Assert.assertTrue("response for invalid access token for instagram is incorrect", actualJsonResponse.contains(expectedJsonResponse));
	}
	
	@Test
	@RunAsClient
	public void test_profile_instagram() throws Exception {
	
		// STEP 1: ARRANGE
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PROFILE_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyProfileResponse(jsonResponse);
	}

	@Test
	@RunAsClient
	public void test_photoalbums_invalidPromotionDeployPath() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 0;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			"dummy",
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			testInstagramAlbumId,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		SocialAlbumsResponse socialAlbumsResponse = JsonUtil.deserializeSocialAlbumsFromJson(jsonResponse);
		String expected = "getSocialPhotoAlbums(promotionDeployPath=dummy, providerId=instagram, offset=0, limit=0, actualPageSize=-1, accessToken=184525800.aeb2ea9.9a1a302a35af43d3bea0d8a851eb5a6c, accessTokenSecret=), error=No API Key was found for social provider: [instagram] and promotion: [dummy]. Please install the appropriate API Keys and try again.";
		String actual = socialAlbumsResponse.getError();
		Assert.assertEquals("error response is incorrect", expected, actual);
	}
	
	@Test
	@RunAsClient
	public void test_photoalbums_instagram_allAlbums() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 0;
		int expectedPageSize = 1;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			testInstagramAlbumId,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photoalbums_instagram_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = 1;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			testInstagramAlbumId,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_photos_instagram_invalidDeploymentPath() throws Exception {
	
		// STEP 1: ARRANGE
		String id = "0";
		int offset = 0;
		int limit = 20;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE,
			"dummy",
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			id,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		SocialAlbumsResponse socialAlbumsResponse = JsonUtil.deserializeSocialAlbumsFromJson(jsonResponse);
		String expected = "getSocialPhotosFromSocialPhotoAlbum(promotionDeployPath=dummy, providerId=instagram, offset=0, limit=20, actualPageSize=-1, accessToken=184525800.aeb2ea9.9a1a302a35af43d3bea0d8a851eb5a6c, accessTokenSecret=), error=No API Key was found for social provider: [instagram] and promotion: [dummy]. Please install the appropriate API Keys and try again.";
		String actual = socialAlbumsResponse.getError();
		Assert.assertEquals("error response is incorrect", expected, actual);
	}

	@Test
	@RunAsClient
	public void test_friends_instagram_invalidDeploymentPath() throws Exception {
	
		// STEP 1: ARRANGE
		String id = "0";
		int offset = 0;
		int limit = 20;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_FRIENDS_REST_SIGNATURE,
			"dummy",
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			id,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		SocialFriendsResponse socialFriendsResponse = JsonUtil.deserializeSocialFriendsFromJson(jsonResponse);
		String expected = "getSocialFriends(promotionDeployPath=dummy, providerId=instagram, offset=0, limit=20, actualPageSize=-1, accessToken=184525800.aeb2ea9.9a1a302a35af43d3bea0d8a851eb5a6c, accessTokenSecret=), error=No API Key was found for social provider: [instagram] and promotion: [dummy]. Please install the appropriate API Keys and try again.";
		String actual = socialFriendsResponse.getError();
		Assert.assertEquals("error response is incorrect", expected, actual);
	}
	
	@Test
	@RunAsClient
	public void test_photos_instagram_defaultOffsetAndLimit() throws Exception {
	
		// STEP 1: ARRANGE
		String id = "0";
		int offset = 0;
		int limit = 20;
		int expectedPageSize = ISocialService.DEFAULT_PAGE_SIZE;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			id,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);
		
		methodSignature = methodSignature.replace("&offset=0&limit=20", "");

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyPhotosResponse(jsonResponse, offset, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photos_instagram_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		String id = "0";
		int offset = 0;
		int limit = 20;
		int expectedPageSize = limit;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			id,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyPhotosResponse(jsonResponse, offset, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_videoalbums_instagram_invalidPromotionDeployPath() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_VIDEO_ALBUMS_REST_SIGNATURE,
			"dummy",
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			testInstagramUserId,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		SocialAlbumsResponse socialAlbumsResponse = JsonUtil.deserializeSocialAlbumsFromJson(jsonResponse);
		String expected = "getSocialVideoAlbums(promotionDeployPath=dummy, providerId=instagram, offset=0, limit=20, actualPageSize=-1, accessToken=184525800.aeb2ea9.9a1a302a35af43d3bea0d8a851eb5a6c, accessTokenSecret=), error=No API Key was found for social provider: [instagram] and promotion: [dummy]. Please install the appropriate API Keys and try again.";
		String actual = socialAlbumsResponse.getError();
		Assert.assertEquals("error response is incorrect", expected, actual);
	}
	
	@Test
	@RunAsClient
	public void test_videoalbums_instagram_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = 1;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_VIDEO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			testInstagramUserId,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_videos_instagram_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = 0;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_VIDEOS_FROM_SOCIAL_VIDEO_ALBUM_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			testInstagramUserId,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyVideosResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_friends_instagram_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = 3;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_FRIENDS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_INSTAGRAM,
			testInstagramUserId,
			offset, 
			limit,
			testInstagramAccessToken,
			testInstagramAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyFriendsResponse(jsonResponse, expectedPageSize);
	}
	
	
	
	
	
	
	
	
	
	
	@Test
	@RunAsClient
	public void test_profile_facebook() throws Exception {
	
		// STEP 1: ARRANGE
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PROFILE_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyProfileResponse(jsonResponse);
	}

	@Test
	@RunAsClient
	public void test_photoalbums_facebook_allAlbums() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 12;
		int expectedPageSize = limit;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photoalbums_facebook_offsetZeroLimitTen() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 5;
		int expectedPageSize = limit;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_photoalbums_facebook_offsetZeroSystemPropertyOverriddenLimit() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = -1;
		int expectedPageSize = 12; // There are only 12 albums currently.	
		System.setProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME, Integer.toString(expectedPageSize));
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photoalbums_facebook_offsetFiveDefaultLimit() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 5;
		int limit = -1;
		int expectedPageSize = 7; // There are only 12 albums currently.
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photoalbums_facebook_offsetZeroInvalidLimit_minimum() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 1;
		int expectedPageSize = 12; // There are only 12 albums currently.
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_photoalbums_facebook_offsetZeroInvalidLimit_maximum() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 1000;
		int expectedPageSize = 12;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_photoalbums_facebook_invalidSystemPropertyPageSize() throws Exception {
	
		// STEP 1: ARRANGE
		System.setProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME, "dummy");
		int offset = 0;
		int limit = -1;
		int expectedPageSize = 12;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_photoalbums_facebook_systemPropertyPageSizeTen() throws Exception {
	
		// STEP 1: ARRANGE
		System.setProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME, Integer.toString(10));
		int offset = 0;
		int limit = -1;
		int expectedPageSize = 12;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photoalbums_facebook_invalidSystemPropertyPageSize_minimum() throws Exception {
	
		// STEP 1: ARRANGE
		System.setProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME, Integer.toString(1));
		int offset = 0;
		int limit = -1;
		int expectedPageSize = 12;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photoalbums_facebook_invalidSystemPropertyPageSize_maximum() throws Exception {
	
		// STEP 1: ARRANGE
		System.setProperty(ISocialService.SOCIAL_SERVICE_DEFAULT_PAGE_SIZE_SYSTEM_PROPERTY_NAME, Integer.toString(1000));
		int offset = 0;
		int limit = -1;
		int expectedPageSize = 12;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	
	
	@Test
	@RunAsClient
	public void test_photos_facebook_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = limit;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookAlbumId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyPhotosResponse(jsonResponse, offset, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_videoalbums_facebook_all() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 0;
		int expectedPageSize = 1;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_VIDEO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookUserId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_videoalbums_facebook_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = 1;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_VIDEO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookUserId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_videos_facebook_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = 0;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_VIDEOS_FROM_SOCIAL_VIDEO_ALBUM_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookUserId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyVideosResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_videos_facebook_invalidPromotionDeployPath() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_VIDEOS_FROM_SOCIAL_VIDEO_ALBUM_REST_SIGNATURE,
			"dummy",
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookUserId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		SocialAlbumsResponse socialAlbumsResponse = JsonUtil.deserializeSocialAlbumsFromJson(jsonResponse);
		String expected = "getSocialVideosFromSocialVideoAlbum(promotionDeployPath=dummy, providerId=facebook, offset=0, limit=20, actualPageSize=-1, accessToken=AAADrESbHw04BACHeQyvV0kd4G2ZCuOPxCTfHoZBKneakZBS1ZBcuQ5u9ZCGApwltVEAUlkp7UxlrahIyfngZCHviiESg4SMwA53G3W6QUYsAZDZD, accessTokenSecret=), error=No API Key was found for social provider: [facebook] and promotion: [dummy]. Please install the appropriate API Keys and try again.";
		String actual = socialAlbumsResponse.getError();
		Assert.assertEquals("error response is incorrect", expected, actual);
	}

	@Test
	@RunAsClient
	public void test_friends_facebook_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = 6;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_FRIENDS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FACEBOOK,
			testFacebookUserId,
			offset, 
			limit,
			testFacebookAccessToken,
			testFacebookAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyFriendsResponse(jsonResponse, expectedPageSize);
	}

	@Test
	@RunAsClient
	public void test_connect_Facebook() throws Exception {

		System.out.println("deploymentUrl: " + deploymentUrl);
		
		String methodSignature = deploymentUrl + "ux/operations/home?uid=testuser";

		driver.get(methodSignature);
		String actualResponse = driver.getPageSource();
		System.out.println("connect response: " + actualResponse);
		System.out.println();
	}
	

	@Test
	@RunAsClient
	public void test_getProfile_Flickr() throws Exception {
	
		// STEP 1: ARRANGE
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PROFILE_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FLICKR,
			testFlickrAccessToken,
			testFlickrAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyProfileResponse(jsonResponse);
	}

	@Test
	@RunAsClient
	public void test_photos_flickr_page_1__50_photos_perPage() throws Exception {
	
		// STEP 1: ARRANGE
		String id = "0";
		int offset = 0;
		int limit = 50;
		int expectedPageSize = limit;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FLICKR,
			id,
			offset, 
			limit,
			testFlickrAccessToken,
			testFlickrAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyPhotosResponse(jsonResponse, offset, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photos_flickr_page_3__50_photos_perPage() throws Exception {
	
		// STEP 1: ARRANGE
		String id = "0";
		int offset = 100;
		int limit = 50;
		int expectedPageSize = limit;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FLICKR,
			id,
			offset, 
			limit,
			testFlickrAccessToken,
			testFlickrAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyPhotosResponse(jsonResponse, offset, expectedPageSize);
	}
	
	
	@Test
	@RunAsClient
	public void test_photos_flickr_page_5__50_photos_perPage() throws Exception {
	
		// STEP 1: ARRANGE
		String id = "0";
		int offset = 200;
		int limit = 50;
		int expectedPageSize = 9;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTOS_FROM_SOCIAL_PHOTO_ALBUM_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FLICKR,
			id,
			offset, 
			limit,
			testFlickrAccessToken,
			testFlickrAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyPhotosResponse(jsonResponse, offset, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_photoalbums_flickr_allPhotoAlbums() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 0;
		int expectedPageSize = 1;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_PHOTO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FLICKR,
			testInstagramAlbumId,
			offset, 
			limit,
			testFlickrAccessToken,
			testFlickrAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	@Test
	@RunAsClient
	public void test_videoalbums_flickr_offsetZeroLimitTwenty() throws Exception {
	
		// STEP 1: ARRANGE
		int offset = 0;
		int limit = 20;
		int expectedPageSize = 1;
		String methodSignature = performTokenReplacementOnMethodSignature(
			GET_SOCIAL_VIDEO_ALBUMS_REST_SIGNATURE,
			promotionDeployPath,
			ISocialService.PROVIDER_NAME_FLICKR,
			testInstagramUserId,
			offset, 
			limit,
			testFlickrAccessToken,
			testFlickrAccessTokenSecret);

		
		// STEP 2: ACT
		driver.get(methodSignature);
		
		
		// STEP 3: ASSERT
		String jsonResponse = driver.getPageSource();
		verifyAlbumResponse(jsonResponse, expectedPageSize);
	}
	
	
	/*
	 * @param methodSignature
	 * @param promotionDeployPath
	 * @param providerId
	 * @param id
	 * @param offset
	 * @param limit
	 * @param accessToken
	 * @param accessTokenSecret
	 * @return
	 * @throws Exception
	 */
	String performTokenReplacementOnMethodSignature(
		String methodSignature,	
		String promotionDeployPath,
		String providerId,
		String id,
		int offset,
		int limit,
		String accessToken,
		String accessTokenSecret) throws Exception {
		
		methodSignature = methodSignature.replace("{promotionDeployPath}", promotionDeployPath);
		methodSignature = methodSignature.replace("{providerId}", providerId);
		methodSignature = methodSignature.replace("{id}", id);
		methodSignature = methodSignature.replace("{offset}", Integer.toString(offset));
		methodSignature = methodSignature.replace("{limit}", Integer.toString(limit));
		methodSignature = methodSignature.replace("{accessToken}", accessToken);
		methodSignature = methodSignature.replace("{accessTokenSecret}", accessTokenSecret);
		methodSignature = deploymentUrl + "rest/social" + methodSignature;
		return methodSignature;
	}	

	/*
	 * @param methodSignature
	 * @param promotionDeployPath
	 * @param providerId
	 * @param accessToken
	 * @param accessTokenSecret
	 * @return
	 * @throws Exception
	 */
	String performTokenReplacementOnMethodSignature(
		String methodSignature,
		String promotionDeployPath,
		String providerId,
		String accessToken,
		String accessTokenSecret) throws Exception {
		
		methodSignature = methodSignature.replace("{promotionDeployPath}", promotionDeployPath);
		methodSignature = methodSignature.replace("{providerId}", providerId);
		methodSignature = methodSignature.replace("{accessToken}", accessToken);
		methodSignature = methodSignature.replace("{accessTokenSecret}", accessTokenSecret);
		methodSignature = deploymentUrl + "rest/social" + methodSignature;
		return methodSignature;
	}	
	
	/*
	 * @param methodSignature
	 * @param promotionDeployPath
	 * @param providerId
	 * @param sourceUrl
	 * @param targetFilepath
	 * @param accessToken
	 * @param accessTokenSecret
	 * @return
	 * @throws Exception
	 */
	String performTokenReplacementOnMethodSignature(
		String methodSignature,	
		String promotionDeployPath,
		String providerId,
		String sourceUrl,
		String targetFilepath,
		String accessToken,
		String accessTokenSecret) throws Exception {

		methodSignature = methodSignature.replace("{promotionDeployPath}", promotionDeployPath);
		methodSignature = methodSignature.replace("{providerId}", providerId);
		methodSignature = methodSignature.replace("{sourceUrl}", sourceUrl);
		methodSignature = methodSignature.replace("{targetFilepath}", targetFilepath);
		methodSignature = methodSignature.replace("{accessToken}", accessToken);
		methodSignature = methodSignature.replace("{accessTokenSecret}", accessTokenSecret);
		methodSignature = deploymentUrl + "rest/social" + methodSignature;
		return methodSignature;
	}	
	
	/*
	 * 
	 * @param jsonResponse
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void verifyProfileResponse(String jsonResponse) throws Exception {
		
		SocialProfileResponse socialProfileResponse = JsonUtil.deserializeSocialProfileFromJson(jsonResponse);
		
		String error = socialProfileResponse.getError();
		Assert.assertEquals("error is non-empty", "", error);
		
		SocialProfile socialProfile = socialProfileResponse.getSocialProfile();

		if (socialProfile != null) {
			
			String actualId = socialProfile.getId();
			String actualName = socialProfile.getName();
			String actualLink = socialProfile.getLink();
			String email = socialProfile.getEmail();
			String firstName = socialProfile.getFirstName();
			String lastName = socialProfile.getLastName();
			String gender = socialProfile.getGender();
			
			Assert.assertTrue("social provider user id is empty", !actualId.isEmpty());
		}
	}
	
	/*
	 * 
	 * @param jsonResponse
	 * @param offset
	 * @param expectedPageSize
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void verifyAlbumResponse(String jsonResponse, int expectedPageSize) throws Exception {
		
		SocialAlbumsResponse socialAlbumsResponse = JsonUtil.deserializeSocialAlbumsFromJson(jsonResponse);
		
		List<SocialAlbum> socialAlbums = socialAlbumsResponse.getSocialAlbums();
		Assert.assertEquals("socialAlbums size is incorrect", Integer.toString(expectedPageSize), Integer.toString(socialAlbums.size()));

		int count = socialAlbumsResponse.getCount();
		Assert.assertTrue("album total count is incorrect", count > 0);
		
		for (int i=0; i < socialAlbums.size(); i++) {
			
			SocialAlbum socialAlbum = socialAlbums.get(i);

			String actualId = socialAlbum.getId();
			String actualName = socialAlbum.getName();
			String actualLink = socialAlbum.getLink();
			
			Assert.assertTrue("id is empty", !actualId.isEmpty());
			Assert.assertTrue("name is empty", !actualName.isEmpty());
			//Assert.assertTrue("link is empty", !actualLink.isEmpty());
		}
	}

	/*
	 * 
	 * @param jsonResponse
	 * @param offset
	 * @param expectedPageSize
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void verifyPhotosResponse(String jsonResponse, int offset, int expectedPageSize) throws Exception {
		
		SocialPhotosResponse socialPhotosResponse = JsonUtil.deserializeSocialPhotosFromJson(jsonResponse);
		
		List<SocialPhoto> socialPhotos = socialPhotosResponse.getSocialPhotos();
		Assert.assertEquals("socialPhotos size is incorrect", Integer.toString(expectedPageSize), Integer.toString(socialPhotos.size()));

		int count = socialPhotosResponse.getCount();
		Assert.assertTrue("Photos total count is zero", count > 0);
		
		for (int i=0; i < socialPhotos.size(); i++) {
			
			SocialPhoto socialPhoto = socialPhotos.get(i);

			String actualId = socialPhoto.getId();
			String actualName = socialPhoto.getName();
			String actualLink = socialPhoto.getLink();
			String actualThumbnailLink = socialPhoto.getThumbnail();
			
			Assert.assertTrue("id is empty", !actualId.isEmpty());
			Assert.assertTrue("link is empty", !actualLink.isEmpty());
			Assert.assertTrue("thumbnail is empty", !actualThumbnailLink.isEmpty());
		}
	}

	/*
	 * 
	 * @param jsonResponse
	 * @param offset
	 * @param expectedPageSize
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void verifyVideosResponse(String jsonResponse, int expectedPageSize) throws Exception {
		
		SocialVideosResponse socialVideosResponse = JsonUtil.deserializeSocialVideosFromJson(jsonResponse);
		
		List<SocialVideo> socialVideos = socialVideosResponse.getSocialVideos();
		Assert.assertEquals("socialVideos size is incorrect", Integer.toString(expectedPageSize), Integer.toString(socialVideos.size()));
		
		int totalCount = socialVideosResponse.getCount();
		
		for (int i=0; i < socialVideos.size(); i++) {
			
			SocialVideo socialVideo = socialVideos.get(i);

			String actualId = socialVideo.getId();
			String actualName = socialVideo.getName();
			String actualLink = socialVideo.getLink();
			String actualThumbnailLink = socialVideo.getThumbnail();
			
			Assert.assertEquals("id is empty", !actualId.isEmpty());
			Assert.assertEquals("name is empty", !actualName.isEmpty());
			Assert.assertEquals("link is empty", !actualLink.isEmpty());
			Assert.assertEquals("thumbnail is empty", !actualThumbnailLink.isEmpty());
		}
	}
	
	/*
	 * 
	 * @param jsonResponse
	 * @param expectedPageSize
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private void verifyFriendsResponse(String jsonResponse, int expectedPageSize) throws Exception {
		
		SocialFriendsResponse socialFriendsResponse = JsonUtil.deserializeSocialFriendsFromJson(jsonResponse);
		
		List<SocialProfile> socialFriends = socialFriendsResponse.getSocialFriends();
		Assert.assertEquals("socialFriends page size is incorrect", Integer.toString(expectedPageSize), Integer.toString(socialFriends.size()));

		int count = socialFriendsResponse.getCount(); 
		
		for (int i=0; i < socialFriends.size(); i++) {
			
			SocialProfile socialFriend = socialFriends.get(i);

			String actualId = socialFriend.getId();
			String actualName = socialFriend.getName();
			String actualLink = socialFriend.getLink();
			String email = socialFriend.getEmail();
			String firstName = socialFriend.getFirstName();
			String lastName = socialFriend.getLastName();
			String gender = socialFriend.getGender();
		
			Assert.assertTrue("friend id is empty", !actualId.isEmpty());
			Assert.assertTrue("friend name is empty", !actualName.isEmpty());
			Assert.assertTrue("friend link is empty", !actualLink.isEmpty());
		}
	}
}