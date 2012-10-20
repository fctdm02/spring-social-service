<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>

<h3>Friends</h3>
<form name="home" id="home" action="<c:url value="/ux/operations/home"/>" method="GET">
	<b>ProviderID:</b>&nbsp;<c:out value="${providerId}"/><br/>
	<b>Error:</b>&nbsp;<c:out value="${friendsResponse.error}"/><br/>
	<b>Count:</b>&nbsp;<c:out value="${friendsResponse.count}"/><br/>
	<c:forEach items="${friendsResponse.socialFriends}" var="friend">
		<hr>
		<b>ID:</b>&nbsp;${friend.id}<br/>
		<b>Name:</b>&nbsp;${friend.name}<br/>
		<b>Link:</b>&nbsp;${friend.link}<br/>
		<b>Email:</b>&nbsp;${friend.email}<br/>
		<b>First Name:</b>&nbsp;${friend.firstName}<br/>
		<b>Last Name:</b>&nbsp;${friend.lastName}<br/>
		<b>Full Name:</b>&nbsp;${friend.fullName}<br/>
		<b>Gender:</b>&nbsp;${friend.gender}<br/>
	</c:forEach>
	<hr>
	<button type="submit">Home</button>	
</form>
	