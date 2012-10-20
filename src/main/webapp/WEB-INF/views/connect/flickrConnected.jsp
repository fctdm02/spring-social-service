<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>

<h3>Status: Connected to Flickr</h3>
<form name="fl_disconnect" id="fl_disconnect" method="post">
	<button type="submit">Disconnect from Flickr</button>	
	<input type="hidden" name="_method" value="delete" />
</form>
