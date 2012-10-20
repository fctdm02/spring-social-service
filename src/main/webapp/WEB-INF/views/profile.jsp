<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ taglib uri="http://www.springframework.org/spring-social/facebook/tags" prefix="facebook" %>
<%@ page session="false" %>

<h3>Profile</h3>
<form name="home" id="home" action="<c:url value="/ux/operations/home"/>" method="GET">
	<b>ProviderID:</b>&nbsp;<c:out value="${profile.providerId}"/><br/>
	<b>Error:</b>&nbsp;<c:out value="${profileResponse.error}"/><br/>
	<b>ID:</b>&nbsp;<c:out value="${profileResponse.socialProfile.id}"/><br/>
	<b>Name:</b>&nbsp;<c:out value="${profileResponse.socialProfile.name}"/><br/>
	<b>Link:</b>&nbsp;<c:out value="${profileResponse.socialProfile.link}"/><br/>
	<b>Email:</b>&nbsp;<c:out value="${profileResponse.socialProfile.email}"/><br/>
	<b>First Name:</b>&nbsp;<c:out value="${profileResponse.socialProfile.firstName}"/><br/>
	<b>Last Name:</b>&nbsp;<c:out value="${profileResponse.socialProfile.lastName}"/><br/>
	<b>Full Name:</b>&nbsp;<c:out value="${profileResponse.socialProfile.fullName}"/><br/>
	<b>Gender:</b>&nbsp;<c:out value="${profileResponse.socialProfile.gender}"/><br/>
	<p/>
	<button type="submit">Home</button>	
</form>
	