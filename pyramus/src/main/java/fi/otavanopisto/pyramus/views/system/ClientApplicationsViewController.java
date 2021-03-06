package fi.otavanopisto.pyramus.views.system;

import java.util.List;
import java.util.UUID;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.otavanopisto.pyramus.dao.DAOFactory;
import fi.otavanopisto.pyramus.dao.clientapplications.ClientApplicationAccessTokenDAO;
import fi.otavanopisto.pyramus.dao.clientapplications.ClientApplicationAuthorizationCodeDAO;
import fi.otavanopisto.pyramus.dao.clientapplications.ClientApplicationDAO;
import fi.otavanopisto.pyramus.domainmodel.clientapplications.ClientApplication;
import fi.otavanopisto.pyramus.domainmodel.clientapplications.ClientApplicationAccessToken;
import fi.otavanopisto.pyramus.domainmodel.clientapplications.ClientApplicationAuthorizationCode;
import fi.otavanopisto.pyramus.framework.PyramusFormViewController;
import fi.otavanopisto.pyramus.framework.UserRole;
import fi.otavanopisto.pyramus.util.OauthClientSecretGenerator;

public class ClientApplicationsViewController extends PyramusFormViewController {

  @Override
  public void processForm(PageRequestContext requestContext) {
    ClientApplicationDAO clientApplicationDAO = DAOFactory.getInstance().getClientApplicationDAO();
    
    List<ClientApplication> clientApplications = clientApplicationDAO.listAll();
    requestContext.getRequest().setAttribute("clientApplications", clientApplications);
    requestContext.setIncludeJSP("/templates/system/clientapplications.jsp");
  }

  @Override
  public void processSend(PageRequestContext requestContext) {
    ClientApplicationDAO clientApplicationDAO = DAOFactory.getInstance().getClientApplicationDAO();
    ClientApplicationAuthorizationCodeDAO clientApplicationAuthorizationCodeDAO = DAOFactory.getInstance().getClientApplicationAuthorizationCodeDAO();
    ClientApplicationAccessTokenDAO clientApplicationAccessTokenDAO = DAOFactory.getInstance().getClientApplicationAccessTokenDAO();

    Long clientApplicationsRowCount = requestContext.getLong("clientApplicationsTable.rowCount");
    for (int i = 0; i < clientApplicationsRowCount; i++) {
      String colPrefix = "clientApplicationsTable." + i;

      Long id = requestContext.getLong(colPrefix + ".id");
      Boolean remove = "1".equals(requestContext.getString(colPrefix + ".remove"));
      Boolean regenerateSecret = "1".equals(requestContext.getString(colPrefix + ".regenerateSecret"));
      Boolean skipPrompt = "1".equals(requestContext.getString(colPrefix + ".skipPrompt"));
      String clientName = requestContext.getString(colPrefix + ".appName");
      String clientId = requestContext.getString(colPrefix + ".appId");
      String clientSecret = requestContext.getString(colPrefix + ".appSecret");

      if (id == null && !remove) {
        clientId = UUID.randomUUID().toString();
        clientSecret = new OauthClientSecretGenerator(80).nextString();
        clientApplicationDAO.create(clientName, clientId, clientSecret, skipPrompt);
      } else if(id != null) {
        ClientApplication clientApplication = clientApplicationDAO.findById(id);

        if (remove) {
          List<ClientApplicationAuthorizationCode> authCodes = clientApplicationAuthorizationCodeDAO.listByClientApplication(clientApplication);
          for(ClientApplicationAuthorizationCode clientApplicationAuthorizationCode : authCodes){
            ClientApplicationAccessToken clientApplicationAccessToken = clientApplicationAccessTokenDAO.findByAuthCode(clientApplicationAuthorizationCode);
            if(clientApplicationAccessToken != null){
              clientApplicationAccessTokenDAO.delete(clientApplicationAccessToken);
            }
            clientApplicationAuthorizationCodeDAO.delete(clientApplicationAuthorizationCode);
          }
          clientApplicationDAO.delete(clientApplication);
        }else{
          if (regenerateSecret) {
            clientSecret = new OauthClientSecretGenerator(80).nextString();
            clientApplicationDAO.updateClientSecret(clientApplication, clientSecret);
          }
          clientApplicationDAO.updateName(clientApplication, clientName);
          clientApplicationDAO.updateSkipPrompt(clientApplication, skipPrompt);
        }
      }
    }
    processForm(requestContext);
  }

  @Override
  public UserRole[] getAllowedRoles() {
    return new UserRole[] { UserRole.ADMINISTRATOR };
  }
}
