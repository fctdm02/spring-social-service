<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<h3>Status: Connected to Twitter</h3>
<form name="tw_disconnect" id="tw_disconnect" method="post">
	<button type="submit">Disconnect from Twitter</button>	
	<input type="hidden" name="_method" value="delete" />
</form>
