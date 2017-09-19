<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% pageContext.setAttribute("newLineChar", "\n"); %>
<!DOCTYPE html>

<html>
  <head>
    <meta charset="UTF-8"/>

    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/scripts/parsley/parsley.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/application.css"/>

    <script defer="defer" type="text/javascript" src="//code.jquery.com/jquery-1.12.4.min.js"></script>
    <script defer="defer" type="text/javascript" src="//code.jquery.com/ui/1.12.1/jquery-ui.min.js"></script>
    <script defer="defer" type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/blueimp-file-upload/9.5.7/jquery.fileupload.min.js"></script>
    <script defer="defer" type="text/javascript" src="${pageContext.request.contextPath}/scripts/parsley/parsley.min.js"></script>
    <script defer="defer" type="text/javascript" src="${pageContext.request.contextPath}/scripts/parsley/fi.js"></script>
    <script defer="defer" type="text/javascript" src="${pageContext.request.contextPath}/scripts/moment/moment.min.js"></script>
    <script defer="defer" type="text/javascript" src="${pageContext.request.contextPath}/scripts/notificationqueue/notificationqueue.js"></script>
    <script defer="defer" type="text/javascript" src="${pageContext.request.contextPath}/scripts/gui/application/application.js"></script>
    <script defer="defer" type="text/javascript" src="${pageContext.request.contextPath}/scripts/gui/application/application-management.js"></script>

  </head>
  <body data-application-entity-id="${applicationEntityId}" data-application-id="${applicationId}">
    <div class="notification-queue">
      <div class="notification-queue-items">
      </div>
    </div>
    <main class="application-management">
      <jsp:include page="/templates/applications/manage-header.jsp"></jsp:include>
      <section class="application-wrapper">
        <section class="application-section application-data">
          <h3 class="application-data-header">Hakemuksen tiedot</h3>
          <c:forEach var="section" items="${sections}">
            <h4 class="application-data-title">${section.key}</h4>
            <c:forEach var="field" items="${section.value}">
              <div class="field-container-view">
                <span class="field-name">${field.key}</span>
                <span class="field-value">${fn:replace(field.value, newLineChar, "<br/>")}</span>
              </div>
            </c:forEach>
          </c:forEach>
          <h4 class="application-data-title">Liitteet</h4>
          <div id="attachments-readonly-container" class="attachments-container">
          </div>
        </section>
        <section class="application-info">
          possu kuva ja muut tiedot
        </section>
        <jsp:include page="/templates/applications/manage-log.jsp"></jsp:include>
      </section>
    </main>
  </body>
</html>
