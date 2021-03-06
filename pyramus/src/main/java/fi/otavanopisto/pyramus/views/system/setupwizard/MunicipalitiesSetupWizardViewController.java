package fi.otavanopisto.pyramus.views.system.setupwizard;

import fi.internetix.smvc.controllers.PageRequestContext;
import fi.otavanopisto.pyramus.dao.DAOFactory;
import fi.otavanopisto.pyramus.dao.base.MunicipalityDAO;

public class MunicipalitiesSetupWizardViewController extends SetupWizardController {
  
  public MunicipalitiesSetupWizardViewController() {
    super("municipalities");
  }
  
  @Override
  public void setup(PageRequestContext requestContext) throws SetupWizardException {

  }
  
  @Override
  public void save(PageRequestContext requestContext) throws SetupWizardException {
    MunicipalityDAO municipalityDAO = DAOFactory.getInstance().getMunicipalityDAO();
    
    int rowCount = requestContext.getInteger("municipalitiesTable.rowCount");
    for (int i = 0; i < rowCount; i++) {
      String colPrefix = "municipalitiesTable." + i;
      String name = requestContext.getString(colPrefix + ".name");
      String code = requestContext.getString(colPrefix + ".code");
      municipalityDAO.create(name, code);
    }  
  }

  @Override
  public boolean isInitialized(PageRequestContext requestContext) throws SetupWizardException {
    MunicipalityDAO municipalityDAO = DAOFactory.getInstance().getMunicipalityDAO();
    return !municipalityDAO.listUnarchived().isEmpty();
  }

}
