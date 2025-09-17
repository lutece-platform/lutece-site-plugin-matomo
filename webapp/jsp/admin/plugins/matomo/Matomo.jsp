<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.matomo.web.ManageMatomoJspBean"%>

${ manageMatomoJspBean.init( pageContext.request, ManageMatomoJspBean.RIGHT_MATOMO_MANAGEMENT ) }
${ manageMatomoJspBean.getMatomoDashboard( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
