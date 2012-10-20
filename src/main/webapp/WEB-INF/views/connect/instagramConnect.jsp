<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

<h3>Status: Not Connected to Instagram</h3>
<form name="ig_connect" id="ig_connect" action="<c:url value="/ux/connect/instagram?promotionDeployPath="/><%=request.getParameter("promotionDeployPath")%>&uid=<%=request.getParameter("uid")%>" method="POST">
	<button type="submit">Connect to Instagram</button>	
</form>
	