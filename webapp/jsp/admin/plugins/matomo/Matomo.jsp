<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="managematomo" scope="session" class="fr.paris.lutece.plugins.matomo.web.ManageMatomoJspBean" />

<% managematomo.init( request, managematomo.RIGHT_MATOMO_MANAGEMENT ); %>
<%= managematomo.getMatomoDashboard ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
