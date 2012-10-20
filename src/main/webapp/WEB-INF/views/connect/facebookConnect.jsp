<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>

<h3>Status: Not Connected to Facebook</h3>
<form name="fb_connect" id="fb_connect" action="<c:url value="/ux/connect/facebook?promotionDeployPath="/><%=request.getParameter("promotionDeployPath")%>&uid=<%=request.getParameter("uid")%>" method="POST">
	<input type="hidden" name="scope" value="email,publish_stream,user_photos,offline_access" />
	<button type="submit">Connect to Facebook</button>	
</form>