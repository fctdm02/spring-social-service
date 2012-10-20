<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>

<h3>Photos</h3>
<form name="home" id="home" action="<c:url value="/ux/operations/home"/>" method="GET">
	<b>Error:</b>&nbsp;<c:out value="${photosResponse.error}"/><br/>
	<b>Count:</b>&nbsp;<c:out value="${photosResponse.count}"/><br/>
	<c:forEach items="${photosResponse.socialPhotos}" var="photo">
		<hr>
		<b>ID:</b>&nbsp;<c:out value="${photo.id}"/><br/>
		<b>Name:</b>&nbsp;<c:out value="${photo.name}"/><br/>
		<b>Source URL:</b>&nbsp;<a href="<c:out value="${photo.link}"/>"><c:out value="${photo.link}"/></a><br/>
		<img src="${photo.thumbnail}" align="middle"/><br/>
		<b>Checksum:</b>&nbsp;<c:out value="${photo.checksum}"/><br/>
	</c:forEach>
	<hr>
	<button type="submit">Home</button>	
</form>
	