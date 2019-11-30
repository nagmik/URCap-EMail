package com.nast.email.install;

import java.util.Locale;

import com.nast.email.style.Style;
import com.nast.email.style.V3Style;
import com.nast.email.style.V5Style;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.SystemAPI;
import com.ur.urcap.api.domain.data.DataModel;

public class MailInstallationNodeService implements SwingInstallationNodeService<MailInstallationNodeContribution, MailInstallationNodeView> {

	private ComMail comMail;
	
	/************************************
	 * 
	 * 
	 ************************************/
	public MailInstallationNodeService(ComMail comMail) {
		this.comMail = comMail;		
	}
	/************************************
	 * 
	 * 
	 ************************************/
	@Override
	public void configureContribution(ContributionConfiguration configuration) {
	}
	/************************************
	 * 
	 * 
	 ************************************/
	@Override
	public String getTitle(Locale locale) {			
		return "NAST E-Mail";
	}		
	/************************************
	 * 
	 * 
	 ************************************/
	@Override
	public MailInstallationNodeView createView(ViewAPIProvider apiProvider) {
		SystemAPI systemAPI = apiProvider.getSystemAPI();
		Style style = systemAPI.getSoftwareVersion().getMajorVersion() >= 5 ? new V5Style() : new V3Style();
		return new MailInstallationNodeView(style);
	}
	/************************************
	 * 
	 * 
	 ************************************/
	@Override
	public MailInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider, MailInstallationNodeView view, DataModel model, CreationContext context) {
		return new MailInstallationNodeContribution(apiProvider, model, view, comMail );
	}
	
}