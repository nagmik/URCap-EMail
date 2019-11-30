package com.nast.email.sender;

import java.util.Locale;

import com.nast.email.style.*;
import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.program.ContributionConfiguration;
import com.ur.urcap.api.contribution.program.CreationContext;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;
import com.ur.urcap.api.domain.SystemAPI;
import com.ur.urcap.api.domain.data.DataModel;

public class MailProgramNodeService implements SwingProgramNodeService<MailProgramNodeContribution, MailProgramNodeView> {

	@Override
	public String getId() {
		return "EMail";
	}

	@Override
	public void configureContribution(ContributionConfiguration contributionConfiguration) {
		
	}

	@Override
	public String getTitle(Locale locale) {		
		return "EMail";			
	}

	@Override
	public MailProgramNodeView createView(ViewAPIProvider apiProvider) {
		SystemAPI systemAPI = apiProvider.getSystemAPI();
		Style style = systemAPI.getSoftwareVersion().getMajorVersion() >= 5 ? new V5Style() : new V3Style();
		return new MailProgramNodeView(style);
	}

	@Override
	public MailProgramNodeContribution createNode(ProgramAPIProvider apiProvider, MailProgramNodeView view, DataModel model, CreationContext creationContext) {
		return new MailProgramNodeContribution(apiProvider, view,  model );
	}
	
}
