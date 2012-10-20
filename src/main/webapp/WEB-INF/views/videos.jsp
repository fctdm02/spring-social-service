<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>

<h3>Videos</h3>
<form name="home" id="home" action="<c:url value="/ux/operations/home"/>" method="GET">
	<b>Error:</b>&nbsp;<c:out value="${videosResponse.error}"/><br/>
	<b>Count:</b>&nbsp;<c:out value="${videosResponse.count}"/><br/>
	<c:forEach items="${videosResponse.socialVideos}" var="video">
		<hr>
		<b>ID:</b>&nbsp;<c:out value="${video.id}"/><br/>
		<b>Name:</b>&nbsp;<c:out value="${video.name}"/><br/>
		<b>Source URL:</b>&nbsp;<a href="<c:out value="${video.link}"/>"><c:out value="${video.link}"/></a><br/>
		<img src="${video.thumbnail}" align="middle"/><br/>
		<b>Checksum:</b>&nbsp;<c:out value="${video.checksum}"/><br/>		
	</c:forEach>
	<hr>
	<button type="submit">Home</button>	
</form>
	