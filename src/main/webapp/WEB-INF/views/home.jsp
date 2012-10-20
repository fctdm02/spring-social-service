<%@ page session="false" %>
<html>
<head>
	<title>Spring Social Service Reference Web Application</title>
</head>
<body>
<hr>
<h3>Social Media Authentication/Authorization (No Access Token Yet):</h3>
<b>NOTES:</b>
&nbsp;<code>promotionDeployPath</code> and <code>uid</code> need to be specified as query parameters to this page in order for the authentication forms below to be populated correctly.<br>
&nbsp;Corresponding "Social Provider API Keys" need to be installed for each given <code>promotionDeployPath</code> in order for authentication to work in general.<br>
&nbsp;First time end-users will not have an access token stored, so you will get an empty result for "get access token" and will need to "Connect" to social provider below.  From then, the access token will be available for as long as it is good.<br>
<hr>
<form name="fb_connect" id="fb_connect" action="/spring-social-service/ux/connect/facebook" method="POST">
	Scope: <input type="text" name="scope" value="email,publish_stream,user_photos,offline_access" /><br>
	Site URL: <input type="text" id="siteUrl" name="siteUrl" value="/spring-social-service/ux/operations/connectionstatus" /><br>
	CID: <input type="text" id="cid" name="cid" value="cidValue" /><br>
	Data: <input type="text" id="data" name="data" value="dataValue" /><br>
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	UID: <input type="text" id="uid" name="uid" value="" /><br>
	<button type="submit">Connect to Facebook</button>	
</form>
<hr>
<form name="fl_connect" id="fl_connect" action="/spring-social-service/ux/connect/flickr?perms=read" method="POST">
	<input type="hidden" name="siteUrl" value="/spring-social-service/ux/operations/connectionstatus" />
	<input type="hidden" name="promotionDeployPath" value="<%=request.getParameter("promotionDeployPath")%>" />
	<input type="hidden" name="uid" value="<%=request.getParameter("uid")%>" />
	<input type="hidden" name="cid" value="cidValue" />
	<input type="hidden" name="data" value="dataValue" />
	<button type="submit">Connect to Flickr</button>	
</form>
<hr>
<form name="ig_connect" id="ig_connect" action="/spring-social-service/ux/connect/instagram" method="POST">
	<input type="hidden" name="siteUrl" value="/spring-social-service/ux/operations/connectionstatus" />
	<input type="hidden" name="promotionDeployPath" value="<%=request.getParameter("promotionDeployPath")%>" />
	<input type="hidden" name="uid" value="<%=request.getParameter("uid")%>" />
	<input type="hidden" name="cid" value="cidValue" />
	<input type="hidden" name="data" value="dataValue" />
	<button type="submit">Connect to Instagram</button>	
</form>
<hr>
<!-- 
<form name="tw_connect" id="tw_connect" action="/spring-social-service/ux/connect/twitter" method="POST">
	<input type="hidden" name="siteUrl" value="/spring-social-service/ux/operations/connectionstatus" />
	<input type="hidden" name="promotionDeployPath" value="<%=request.getParameter("promotionDeployPath")%>" />
	<input type="hidden" name="uid" value="<%=request.getParameter("uid")%>" />
	<input type="hidden" name="cid" value="cidValue" />
	<input type="hidden" name="data" value="dataValue" />
	<button type="submit">Connect to Twitter</button>	
</form>
<hr>
 -->
<h3>Install Social Provider API Key for Promotion:</h3>
<form name="install_api_key" id="install_api_key" action="/spring-social-service/ux/operations/install" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	ApiKey: <input type="text" id="apiKey" name="apiKey" value="" /><br>
	ApiKeySecret: <input type="text" id="apiKeySecret" name="apiKeySecret" value="" /><br>
	<button type="submit">Install API Key</button>	
</form>
<p/>
<hr>
<h3>Get Stored Access Token:</h3>
<form name="get_access_token" id="get_access_token" action="/spring-social-service/ux/operations/accesstoken" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	ID: <input type="text" id="id" name="id" value="" /><br>
	<button type="submit">Get Access Token</button>	
</form>
<p/>
<hr>
<h3>Delete Stored Access Token:</h3>
<form name="delete_access_token" id="delete_access_token" action="/spring-social-service/ux/operations/deleteaccesstoken" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	ID: <input type="text" id="id" name="id" value="" /><br>
	<button type="submit">Delete Access Token</button>	
</form>
<p/>
<hr>
<h3>Get Status:</h3>
<form name="get_status" id="get_status" action="/spring-social-service/ux/operations/status" method="POST">
	<button type="submit">Get Status</button>	
</form>
<p/>
<hr>
<h3>Get Version:</h3>
<form name="get_version" id="get_version" action="/spring-social-service/ux/operations/version" method="POST">
	<button type="submit">Get Version</button>	
</form>
<p/>
<hr>
<h3>Get Photo Albums from Social Media:</h3>
<form name="get_photo_albums" id="get_photo_albums" action="/spring-social-service/ux/operations/photoalbums" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	Offset: <input type="text" id="offset" name="offset" value="" /><br>
	Limit: <input type="text" id="limit" name="limit" value="" /><br>
	AccessToken: <input type="text" id="accessToken" name="accessToken" value="" /><br>
	AccessTokenSecret: <input type="text" id="accessTokenSecret" name="accessTokenSecret" value="" /><br>
	<button type="submit">Get Photo Albums</button>	
</form>
<p/>
<hr>
<h3>Get Photos from Social Media Photo Album:</h3>
<form name="get_photos_from_album" id="get_photos_from_album" action="/spring-social-service/ux/operations/photos" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	AlbumId: <input type="text" id="id" name="id" value="" /><br>
	Offset: <input type="text" id="offset" name="offset" value="" /><br>
	Limit: <input type="text" id="limit" name="limit" value="" /><br>
	AccessToken: <input type="text" id="accessToken" name="accessToken" value="" /><br>
	AccessTokenSecret: <input type="text" id="accessTokenSecret" name="accessTokenSecret" value="" /><br>
	<button type="submit">Get Photos</button>	
</form>
<p/>
<hr>
<h3>Get Video Albums from Social Media:</h3>
<form name="get_video_albums" id="get_video_albums" action="/spring-social-service/ux/operations/videoalbums" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	Offset: <input type="text" id="offset" name="offset" value="" /><br>
	Limit: <input type="text" id="limit" name="limit" value="" /><br>
	AccessToken: <input type="text" id="accessToken" name="accessToken" value="" /><br>
	AccessTokenSecret: <input type="text" id="accessTokenSecret" name="accessTokenSecret" value="" /><br>
	<button type="submit">Get Video Albums</button>	
</form>
<p/>
<hr>
<h3>Get Videos from Social Media Video Album:</h3>
<form name="get_videos_from_album" id="get_videos_from_album" action="/spring-social-service/ux/operations/videos" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	AlbumId: <input type="text" id="id" name="id" value="" /><br>
	Offset: <input type="text" id="offset" name="offset" value="" /><br>
	Limit: <input type="text" id="limit" name="limit" value="" /><br>
	AccessToken: <input type="text" id="accessToken" name="accessToken" value="" /><br>
	AccessTokenSecret: <input type="text" id="accessTokenSecret" name="accessTokenSecret" value="" /><br>
	<button type="submit">Get Videos</button>	
</form>
<p/>
<hr>
<h3>Get Social Profile:</h3>
<form name="get_profile" id="get_profile" action="/spring-social-service/ux/operations/profile" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	AccessToken: <input type="text" id="accessToken" name="accessToken" value="" /><br>
	AccessTokenSecret: <input type="text" id="accessTokenSecret" name="accessTokenSecret" value="" /><br>
	<button type="submit">Get Profile</button>	
</form>
<p/>
<hr>
<h3>Get Friends from Social Media:</h3>
<form name="get_friends" id="get_friends" action="/spring-social-service/ux/operations/friends" method="POST">
	PromotionDeployPath: <input type="text" id="promotionDeployPath" name="promotionDeployPath" value="" /><br>
	ProviderId: <input type="text" id="providerId" name="providerId" value="" /><br>
	UserId: <input type="text" id="id" name="id" value="" /><br>
	Offset: <input type="text" id="offset" name="offset" value="" /><br>
	Limit: <input type="text" id="limit" name="limit" value="" /><br>
	AccessToken: <input type="text" id="accessToken" name="accessToken" value="" /><br>
	AccessTokenSecret: <input type="text" id="accessTokenSecret" name="accessTokenSecret" value="" /><br>
	<button type="submit">Get Friends</button>	
</form>
<p/>
<hr>
<h3>Social Media Disconnect:</h3>
<form name="fb_disconnect" id="fb_disconnect" action="/spring-social-service/ux/connect/facebook" method="POST">
	<input type="hidden" name="promotionDeployPath" value="<%=request.getParameter("promotionDeployPath")%>" />
	<input type="hidden" name="uid" value="<%=request.getParameter("uid")%>" />
	<input type="hidden" name="_method" value="delete">
	<button type="submit">Disconnect from Facebook</button>	
</form>
<p/>
<form name="fl_disconnect" id="fl_disconnect" action="/spring-social-service/ux/connect/flickr?perms=read" method="POST">
	<input type="hidden" name="promotionDeployPath" value="<%=request.getParameter("promotionDeployPath")%>" />
	<input type="hidden" name="uid" value="<%=request.getParameter("uid")%>" />
	<input type="hidden" name="_method" value="delete">
	<button type="submit">Disconnect from Flickr</button>	
</form>
<p/>
<form name="ig_disconnect" id="ig_disconnect" action="/spring-social-service/ux/connect/instagram" method="POST">
	<input type="hidden" name="promotionDeployPath" value="<%=request.getParameter("promotionDeployPath")%>" />
	<input type="hidden" name="uid" value="<%=request.getParameter("uid")%>" />
	<input type="hidden" name="_method" value="delete">
	<button type="submit">Disconnect from Instagram</button>	
</form>
<!-- 
<form name="tw_disconnect" id="tw_disconnect" action="/spring-social-service/ux/connect/twitter" method="POST">
	<input type="hidden" name="promotionDeployPath" value="<%=request.getParameter("promotionDeployPath")%>" />
	<input type="hidden" name="uid" value="<%=request.getParameter("uid")%>" />
	<input type="hidden" name="_method" value="delete">
	<button type="submit">Disconnect from Twitter</button>	
</form>
 -->
</body>
</html>