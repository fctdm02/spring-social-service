<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ page session="false" %>

<h3>Status: Connected to Instagram</h3>
<form name="ig_disconnect" id="ig_disconnect" method="post">
	<button type="submit">Disconnect from Instagram</button>	
	<input type="hidden" name="_method" value="delete" />
</form>
