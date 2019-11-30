package com.nast.email.install;

import java.net.MalformedURLException;
import java.net.URL;

import com.nast.email.impl.Constants;
import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.DaemonService;

public class ComMail implements DaemonService {
private DaemonContribution daemonContribution;
	
	public ComMail(){
		;
	}
	
	@Override
	public void init(DaemonContribution daemonContribution){
		this.daemonContribution = daemonContribution;		
		try{			
			daemonContribution.installResource(new URL(Constants.DAEMON_FILE_PATH));
		} catch (MalformedURLException e) {			
			System.out.println("exception: MalformedURLException");
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public URL getExecutable(){
		try{				     
			return new URL(Constants.DAEMON_FILE_PATH + Constants.DAEMON_NAME);					
		} catch (MalformedURLException e) {
			System.out.println("exception: getExecutable");
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public DaemonContribution getDaemonContribution(){		
		return daemonContribution;
	}
}
