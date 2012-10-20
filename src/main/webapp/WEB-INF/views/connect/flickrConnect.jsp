<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>
<%@ page session="false" %>

<h3>Status: Not Connected to Flickr</h3>
<form name="fl_connect" id="fl_connect" action="<c:url value="/ux/connect/flickr?promotionDeployPath="/><%=request.getParameter("promotionDeployPath")%>&uid=<%=request.getParameter("uid")%>&perms=read" method="POST">
	<button type="submit">Connect to Flickr</button>	
</form>
