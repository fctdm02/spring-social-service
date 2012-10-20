<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

<h3>Status: Not Connected to Twitter</h3>
<form name="tw_connect" id="tw_connect" action="<c:url value="/ux/connect/twitter?promotionDeployPath="/><%=request.getParameter("promotionDeployPath")%>&uid=<%=request.getParameter("uid")%>" method="POST">
	<button type="submit">Connect to Twitter</button>	
</form>
