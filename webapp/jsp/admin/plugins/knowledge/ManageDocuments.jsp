<jsp:useBean id="managedocumentsDocument" scope="session" class="fr.paris.lutece.plugins.knowledge.web.DocumentJspBean" />
<% String strContent = managedocumentsDocument.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
