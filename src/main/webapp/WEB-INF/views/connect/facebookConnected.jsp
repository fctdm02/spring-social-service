<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>

<h3>Status: Connected to Facebook</h3>
<form name="fb_disconnect" id="fb_disconnect" method="post">
	<button type="submit">Disconnect from Facebook</button>	
	<input type="hidden" name="_method" value="delete" />
</form>
