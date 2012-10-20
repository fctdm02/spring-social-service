<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>

<h3>Photo Albums</h3>
<form name="home" id="home" action="<c:url value="/ux/operations/home"/>" method="GET">
	<b>Error:</b>&nbsp;<c:out value="${albumsResponse.error}"/><br/>
	<b>Count:</b>&nbsp;<c:out value="${albumsResponse.count}"/><br/>
	<c:forEach items="${albumsResponse.socialAlbums}" var="album">
		<hr>
		<b>ID:</b>&nbsp;<c:out value="${album.id}"/><br/>
		<b>Name:</b>&nbsp;<c:out value="${album.name}"/><br/>
	</c:forEach>
	<hr>
	<button type="submit">Home</button>	
</form>
	