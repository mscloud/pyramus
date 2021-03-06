package fi.otavanopisto.pyramus.json.applications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.otavanopisto.pyramus.dao.DAOFactory;
import fi.otavanopisto.pyramus.dao.application.ApplicationDAO;
import fi.otavanopisto.pyramus.domainmodel.application.Application;
import fi.otavanopisto.pyramus.domainmodel.application.ApplicationState;
import fi.otavanopisto.pyramus.framework.JSONRequestController;
import fi.otavanopisto.pyramus.framework.UserRole;
import fi.otavanopisto.pyramus.persistence.search.SearchResult;
import fi.otavanopisto.pyramus.views.applications.ApplicationUtils;

public class SearchApplicationsJSONRequestController extends JSONRequestController {

  public void process(JSONRequestContext requestContext) {
    ApplicationDAO applicationDAO = DAOFactory.getInstance().getApplicationDAO();

    Integer resultsPerPage = NumberUtils.createInteger(requestContext.getRequest().getParameter("maxResults"));
    if (resultsPerPage == null) {
      resultsPerPage = 10;
    }

    Integer page = NumberUtils.createInteger(requestContext.getRequest().getParameter("page"));
    if (page == null) {
      page = 0;
    }
    
    String line = requestContext.getString("line");
    String stateStr = requestContext.getString("state");
    ApplicationState state = null;
    if (stateStr != null) {
      state = ApplicationState.valueOf(stateStr);
    }
    
    SearchResult<Application> searchResult = applicationDAO.searchApplications(resultsPerPage, page, line, state, Boolean.TRUE);

    List<Map<String, Object>> results = new ArrayList<>();
    List<Application> applications = searchResult.getResults();
    
    Collections.sort(applications, new Comparator<Application>() {
      @Override
      public int compare(Application o1, Application o2) {
        Date d1 = ApplicationUtils.getLatest(o1.getCreated(), o1.getApplicantLastModified(), o1.getLastModified());
        Date d2 = ApplicationUtils.getLatest(o2.getCreated(), o2.getApplicantLastModified(), o2.getLastModified());
        return d1 == null ? 1 : d2 == null ? -1 : d2.compareTo(d1);
      }
    });
    
    
    for (Application application : applications) {
      Map<String, Object> applicationInfo = new HashMap<>();
      applicationInfo.put("id", application.getId());
      applicationInfo.put("name", String.format("%s, %s", application.getLastName(), application.getFirstName()));
      applicationInfo.put("email", application.getEmail());
      applicationInfo.put("line", application.getLine());
      applicationInfo.put("state", application.getState());
      Date date = ApplicationUtils.getLatest(application.getCreated(), application.getApplicantLastModified(), application.getLastModified());
      applicationInfo.put("date", date == null ? null : date.getTime());
      applicationInfo.put("handler", application.getHandler() == null ? null : application.getHandler().getFullName());
      results.add(applicationInfo);
    }

    String statusMessage = searchResult.getTotalHitCount() > 0
        ? String.format("Näytetään %d - %d yhteensä %d osumasta",
            searchResult.getFirstResult() + 1,
            searchResult.getLastResult() + 1,
            searchResult.getTotalHitCount())
        : "Ei tuloksia";

    requestContext.addResponseParameter("results", results);
    requestContext.addResponseParameter("statusMessage", statusMessage);
    requestContext.addResponseParameter("pages", searchResult.getPages());
    requestContext.addResponseParameter("page", searchResult.getPage());
  }

  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR, UserRole.MANAGER };
  }

}
