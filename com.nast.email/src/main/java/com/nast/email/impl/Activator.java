package com.nast.email.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.nast.email.install.ComMail;
import com.nast.email.install.MailInstallationNodeService;
import com.nast.email.sender.MailProgramNodeService;
import com.ur.urcap.api.contribution.DaemonService;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeService;

/**
 * Hello world activator for the OSGi bundle URCAPS contribution
 *
 */
public class Activator implements BundleActivator {
	@Override
	public void start(BundleContext bundleContext) throws Exception {
		System.out.println("NAST Init Mailer");
		
		// Install		
		ComMail comSignal = new ComMail();	
		bundleContext.registerService(SwingInstallationNodeService.class, new MailInstallationNodeService( comSignal ), null);
		
		// Daemon
		bundleContext.registerService(DaemonService.class, comSignal, null);		
		
		// Program Node
		bundleContext.registerService(SwingProgramNodeService.class, new MailProgramNodeService(), null);
				
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		System.out.println("NAST Mailer says cya!");
	}
}

