package com.nast.email.install;


import java.awt.EventQueue;
import java.util.Timer;
import java.util.TimerTask;

import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.InstallationAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.inputvalidation.InputValidationFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

public class MailInstallationNodeContribution implements InstallationNodeContribution {

	
	private final InstallationAPI installationAPI;	
	
	private final MailInstallationNodeView view;
	private final KeyboardInputFactory keyboardFactory;	
	private final InputValidationFactory validatorFactory;	
	private DataModel model;	
	
	private final ComMail comMail;
	
		
	
	// DAEMON
	private static final String XMLRPC_VARIABLE = "mailDaemon";
	private XmlRpcDaemonInterface xmlRpcDaemonInterface;
	
	private static final String DAEMON_ENABLED_KEY = "mailDaemonEnabled";
	private static final boolean DAEMON_ENABLED_DEFAULT_VALUE = true;
	
	private static final String XMLRPC_HOST = "127.0.0.1";
	private static final int XMLRPC_PORT = 33015;
	
	private Timer uiTimer;
	private boolean pauseTimer = false;
	
	// MAIL	Server
	private static final String SMTP_SERVER_KEY = "smtpServer";
	private static final String SMTP_SERVER_DEFAULT_VALUE = "smtp.provider.de";
	// SMTP Port
	private static final String SMTP_PORT_KEY = "smtpPortKey";
	private static final Integer SMTP_PORT_DEFAULT_VALUE = 587;
	// SMTP User
	private static final String SMTP_USER_KEY = "smtpUser";
	private static final String SMTP_USER_DEFAULT_VALUE = "mail@example.de";
	// MAIL	PASSWORD
	private static final String SMTP_PASSWORD_KEY = "smtpPassword";
	private static final String SMTP_PASSWORD_DEFAULT_VALUE = "12345";
	// MAIL	SENDER
	private static final String SMTP_SENDER_KEY = "smtpSender";
	private static final String SMTP_SENDER_DEFAULT_VALUE = "mail@example.de";	
	// URCAP Active
	private static final String URCAP_ACTIVE_KEY = "urcap_mail_active";
	private static final boolean URCAP_ACTIVE_KEY_DEFAULT_VALUE = true;
	
	/************************************
	 * 
	 * 
	 ************************************/
	public MailInstallationNodeContribution(InstallationAPIProvider apiProvider, DataModel model, MailInstallationNodeView view, ComMail comMail) {
		
		this.keyboardFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();
		this.validatorFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getInputValidationFactory();
		
		this.model = model;
		this.view = view;
						
		this.installationAPI = apiProvider.getInstallationAPI();
		this.comMail = comMail;
				
		applyDesiredDaemonStatus();
	}
	/************************************
	 * 
	 * 
	 ************************************/
	@Override
	public void openView() {
		
		// Timer 
		initTimer();					
		
		// set smtp server
		view.setSmtpServer( getSmtpServer() );
		// set smtp port
		view.setSmtpPort( getSmtpPort() );
		// set smtp user
		view.setSmtpUser( getSmtpUser() );
		// set smtp password
		view.setSmtpPassword( getSmtpPassword() );
		// set smtp sender
		view.setSmtpSender( getSmtpSender() );
				
		// update view
		view.updateView();
	}	
	/************************************
	 * 
	 * 
	 ************************************/
	@Override
	public void closeView() {
		
	}
	/************************************
	 * 
	 * 
	 ************************************/
	public boolean isDefined() {
		return true;
	}	
	/************************************
	 * 
	 * 
	 ************************************/
	@Override
	public void generateScript(ScriptWriter writer) {
		
		// URCap isActivated	
		boolean urcapIsActivated 		= getUrcapActive();		
		
		// DAEMON STATE
		DaemonContribution.State state = getDaemonState();
		
		if( urcapIsActivated ) {
			if (state == DaemonContribution.State.RUNNING) {					
				String rpcString = "rpc_factory(\"xmlrpc\",\"http://" + XMLRPC_HOST + ":" + String.valueOf( XMLRPC_PORT ) + "/RPC2\")";				
				writer.assign(XMLRPC_VARIABLE, rpcString );
			}else {
				writer.appendLine("# MAIL SERVICE NOT RUNNING");										
			}
		}else {
			writer.appendLine("# MAIL URCAP NOT ACTIVATED");			
		}
	}
	/************************************
	 * 
	 * 
	 ************************************/
	private void initTimer() {
		
		uiTimer = new Timer(true);
		uiTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (!pauseTimer) {
							updateUI();
						}
					}
				});
			}
		}, 0, 1000);
	}
	/************************************
	 * 
	 * 
	 ************************************/	
	private void updateUI() {
		// Daemon state
		DaemonContribution.State state = getDaemonState();

		// Button enable true/false
		if (state == DaemonContribution.State.RUNNING) {
			view.setStartButtonEnabled(false);
			view.setStopButtonEnabled(true);
			view.setDaemonState(true);
		} else {
			view.setStartButtonEnabled(true);
			view.setStopButtonEnabled(false);
			view.setDaemonState(false);
		}
	}
	/********************************************
	 * 
	 * 
	 *******************************************/	
	// UI Button start daemon
	public void onStartClick() {
		model.set(DAEMON_ENABLED_KEY, true);
		applyDesiredDaemonStatus();
	}
	
	/********************************************
	 * 
	 * 
	 *******************************************/
	// UI Button stop daemon
	public void onStopClick() {
		model.set(DAEMON_ENABLED_KEY, false);
		applyDesiredDaemonStatus();
	}
	
	/********************************************
	 * 
	 * 
	 *******************************************/	
	private void applyDesiredDaemonStatus() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if ( isDaemonEnabled() && getUrcapActive() ) {					
					try {
						pauseTimer = true;
						awaitDaemonRunning(5000);						
					} catch(Exception e){
						System.out.println(e.getMessage());						
					} finally {
						pauseTimer = false;
					}
				} else {
					comMail.getDaemonContribution().stop();
				}
			}
		}).start();
	}
	/********************************************
	 * 
	 * 
	 *******************************************/	
	private void awaitDaemonRunning(long timeOutMilliSeconds) throws InterruptedException {
		comMail.getDaemonContribution().start();
		long endTime = System.nanoTime() + timeOutMilliSeconds * 1000L * 1000L;
		while(System.nanoTime() < endTime && (comMail.getDaemonContribution().getState() != DaemonContribution.State.RUNNING || !xmlRpcDaemonInterface.isReachable())) {
			Thread.sleep(100);
		}
	}	
	
	/********************************************
	 * 
	 * 
	 *******************************************/
	public DaemonContribution.State getDaemonState() {
		return comMail.getDaemonContribution().getState();
	}

	public Boolean isDaemonEnabled() {
		return model.get(DAEMON_ENABLED_KEY, DAEMON_ENABLED_DEFAULT_VALUE);
	}

	public String getXMLRPCVariable(){
		return XMLRPC_VARIABLE;
	}

	public XmlRpcDaemonInterface getXmlRpcDaemonInterface() {
		return xmlRpcDaemonInterface;
	}
	
	/****************************
	* URCAP ACTIVE
	*****************************/
	public void setUrcapActive( final boolean val ) {			
		model.set(URCAP_ACTIVE_KEY, val);								
	}
	
	public boolean getUrcapActive() {
		return model.get(URCAP_ACTIVE_KEY, URCAP_ACTIVE_KEY_DEFAULT_VALUE);
	}
	
	/****************************
	* SMTP Server Input
	*****************************/
	public KeyboardTextInput getKeyboardForSmtpServer() {
		KeyboardTextInput keyboardInput = keyboardFactory.createStringKeyboardInput();
		keyboardInput.setInitialValue( getSmtpServer() );
		keyboardInput.setErrorValidator(validatorFactory.createStringLengthValidator(1, 25));
		return keyboardInput;
	}

	public KeyboardInputCallback<String> getCallbackForSmtpServer() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setSmtpServer(value);
				view.setSmtpServer(value);
			}
		};
	}
	
	public String getSmtpServer() {
		return model.get(SMTP_SERVER_KEY, SMTP_SERVER_DEFAULT_VALUE);
	}

	public void setSmtpServer(String message) {
		if ("".equals(message)) {
			resetToDefaultSmtpServer();
		} else {
			model.set( SMTP_SERVER_KEY, message );
		}
	}

	private void resetToDefaultSmtpServer() {
		view.setSmtpServer(SMTP_SERVER_DEFAULT_VALUE);
		model.set(SMTP_SERVER_KEY, SMTP_SERVER_DEFAULT_VALUE);
	}
	
	/****************************
	* SMTP Port Input
	*****************************/
	public KeyboardNumberInput<Integer>  getKeyboardForSmtpPort() {
		KeyboardNumberInput<Integer>  keyboardInput = keyboardFactory.createPositiveIntegerKeypadInput();
		keyboardInput.setInitialValue( getSmtpPort() );
		keyboardInput.setErrorValidator(validatorFactory.createIntegerRangeValidator(0, 10000));
		return keyboardInput;
	}

	public KeyboardInputCallback<Integer> getCallbackForSmtpPort() {
		return new KeyboardInputCallback<Integer>() {
			@Override
			public void onOk(Integer value) {
				setSmtpPort(value);
				view.setSmtpPort(value);
			}
		};
	}
	
	public int getSmtpPort() {
		return model.get(SMTP_PORT_KEY, SMTP_PORT_DEFAULT_VALUE );
	}

	public void setSmtpPort(int message) {
		if ("".equals(message)) {
			resetToDefaultSmtpPort();
		} else {
			model.set( SMTP_PORT_KEY, message );
		}
	}

	private void resetToDefaultSmtpPort() {
		view.setSmtpPort(SMTP_PORT_DEFAULT_VALUE);
		model.set(SMTP_PORT_KEY, SMTP_PORT_DEFAULT_VALUE);
	}
	
	/****************************
	* SMTP User Input
	*****************************/
	public KeyboardTextInput getKeyboardForSmtpUser() {
		KeyboardTextInput keyboardInput = keyboardFactory.createStringKeyboardInput();
		keyboardInput.setInitialValue( getSmtpUser() );
		keyboardInput.setErrorValidator(validatorFactory.createStringLengthValidator(1, 25));
		return keyboardInput;
	}

	public KeyboardInputCallback<String> getCallbackForSmtpUser() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setSmtpUser(value);
				view.setSmtpUser(value);
			}
		};
	}
	
	public String getSmtpUser() {
		return model.get(SMTP_USER_KEY, SMTP_USER_DEFAULT_VALUE);
	}

	public void setSmtpUser(String message) {
		if ("".equals(message)) {
			resetToDefaultSmtpUser();
		} else {
			model.set( SMTP_USER_KEY, message );
		}
	}

	private void resetToDefaultSmtpUser() {
		view.setSmtpUser(SMTP_USER_DEFAULT_VALUE);
		model.set(SMTP_USER_KEY, SMTP_USER_DEFAULT_VALUE);
	}
	
	/****************************
	* SMTP Password Input
	*****************************/
	public KeyboardTextInput getKeyboardForSmtpPassword() {
		KeyboardTextInput keyboardInput = keyboardFactory.createPasswordKeyboardInput();
		keyboardInput.setInitialValue( getSmtpPassword() );
		keyboardInput.setErrorValidator(validatorFactory.createStringLengthValidator(1, 25));
		return keyboardInput;
	}

	public KeyboardInputCallback<String> getCallbackForSmtpPassword() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setSmtpPassword(value);
				view.setSmtpPassword(value);
			}
		};
	}
	
	public String getSmtpPassword() {
		return model.get(SMTP_PASSWORD_KEY, SMTP_PASSWORD_DEFAULT_VALUE);
	}

	public void setSmtpPassword(String message) {
		if ("".equals(message)) {
			resetToDefaultSmtpPassword();
		} else {
			model.set( SMTP_PASSWORD_KEY, message );
		}
	}

	private void resetToDefaultSmtpPassword() {
		view.setSmtpPassword(SMTP_PASSWORD_DEFAULT_VALUE);
		model.set(SMTP_PASSWORD_KEY, SMTP_PASSWORD_DEFAULT_VALUE);
	}
	
	/****************************
	* SMTP Sender Input
	*****************************/
	public KeyboardTextInput getKeyboardForSmtpSender() {
		KeyboardTextInput keyboardInput = keyboardFactory.createStringKeyboardInput();
		keyboardInput.setInitialValue( getSmtpSender() );
		keyboardInput.setErrorValidator(validatorFactory.createStringLengthValidator(1, 25));
		return keyboardInput;
	}

	public KeyboardInputCallback<String> getCallbackForSmtpSender() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setSmtpSender(value);
				view.setSmtpSender(value);
			}
		};
	}
	
	public String getSmtpSender() {
		return model.get(SMTP_SENDER_KEY, SMTP_SENDER_DEFAULT_VALUE);
	}

	public void setSmtpSender(String message) {
		if ("".equals(message)) {
			resetToDefaultSmtpSender();
		} else {
			model.set( SMTP_SENDER_KEY, message );
		}
	}

	private void resetToDefaultSmtpSender() {
		view.setSmtpSender(SMTP_SENDER_DEFAULT_VALUE);
		model.set(SMTP_SENDER_KEY, SMTP_SENDER_DEFAULT_VALUE);
	}
	
}
