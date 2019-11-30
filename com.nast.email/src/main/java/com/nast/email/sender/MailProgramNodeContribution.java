package com.nast.email.sender;

import com.nast.email.install.MailInstallationNodeContribution;
import com.ur.urcap.api.contribution.DaemonContribution;
import com.ur.urcap.api.contribution.ProgramNodeContribution;
import com.ur.urcap.api.contribution.program.ProgramAPIProvider;
import com.ur.urcap.api.domain.ProgramAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputCallback;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardInputFactory;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

public class MailProgramNodeContribution implements ProgramNodeContribution {
	
	private final ProgramAPI programAPI;
	private final MailProgramNodeView view;
	private final DataModel model;
	private final KeyboardInputFactory keyboardFactory;		
	
	// MailDaemon response
	private static final String MAIL_RESULT_VAR  = "serviceResult";
	
	// Mail Recipient
	private static final String MAIL_RECIPIENT_KEY = "mailRecipient";
	private static final String MAIL_RECIPIENT_DEFAULT_VALUE = "recipient@test.de";
	// Mail Subject 
	private static final String MAIL_SUBJECT_KEY = "mailSubject";
	private static final String MAIL_SUBJECT_DEFAULT_VALUE = "UR is calling";
	// Mail Message
	private static final String MAIL_MESSAGE_KEY = "mailMessage";
	private static final String MAIL_MESSAGE_DEFAULT_VALUE = "Hello World!";
	
	
	/************************************
	 * 
	 ************************************/
	MailProgramNodeContribution(ProgramAPIProvider apiProvider, MailProgramNodeView view, DataModel model) {
		this.programAPI = apiProvider.getProgramAPI();		
		this.keyboardFactory = apiProvider.getUserInterfaceAPI().getUserInteraction().getKeyboardInputFactory();	
		this.view = view;
		this.model = model;
	}
	/************************************
	 * 
	 ************************************/	
	@Override
	public void openView() {		
	
		view.setSmtpRecipient( getSmtpRecipient() );
		
		view.setSmtpSubject( getSmtpSubject() );
		
		view.setSmtpMessage( getSmtpMessage() );
	}
	/************************************
	 * 
	 ************************************/
	@Override
	public void closeView() {
	}
	
	/************************************
	 * 
	 ************************************/
	@Override
	public String getTitle() {
		return "EMail";		
	}
	/************************************
	 * 
	 ************************************/
	@Override
	public boolean isDefined() {	
		
		// cap is activated
		boolean urcapIsActive 	= getInstallation().getUrcapActive();
		// Daemon state
		DaemonContribution.State state = getInstallation().getDaemonState();
				
		// cap is not activated
		if( !urcapIsActive ) {			
			view.setWarningRowVisible( true );
			view.setWarningText( "Warning:\r\nMail URCap is not activated" );
			return false;
		}else{
			view.setWarningRowVisible( false );
		}				
		
		// Button enable true/false
		if (state == DaemonContribution.State.RUNNING) {
			view.setWarningRowVisible( false );
			return true;
		}else {
			view.setWarningRowVisible( true );
			view.setWarningText( "Warning:\r\nMail Service is not running" );
			return false;			
		}
	}
	/************************************
	 * 
	 ************************************/
	@Override
	public void generateScript(ScriptWriter writer) {
		
		String  XMLRPCVariable = getInstallation().getXMLRPCVariable();
		DaemonContribution.State state = getInstallation().getDaemonState();
		
		String smtpHost = getInstallation().getSmtpServer();
		int smtpPort = getInstallation().getSmtpPort();
		String username = getInstallation().getSmtpUser();
		String password = getInstallation().getSmtpPassword();
		String mailRecipient = getSmtpRecipient();
		String mailFrom = getInstallation().getSmtpSender();
		String subject = getSmtpSubject();
		String message = getSmtpMessage();
		
		if (state == DaemonContribution.State.RUNNING) {						
			writer.assign( MAIL_RESULT_VAR, XMLRPCVariable + ".sendMail(\"" + smtpHost + "\"," +  String.valueOf( smtpPort ) + ", \"" +  username + "\", \"" +  password + "\", \"" +  mailRecipient + "\", \"" +  mailFrom + "\", \""+  subject + "\", \"" + message + "\")");
			writer.ifCondition( MAIL_RESULT_VAR + " != 200");
				writer.appendLine("popup( str_cat(\"Err: \"," + MAIL_RESULT_VAR + "), title=\"Error\", error=True, blocking=True)");
			writer.end();
		}		
	}
	/************************************
	 * getInstallation
	 ************************************/
	public MailInstallationNodeContribution getInstallation() {
		return programAPI.getInstallationNode(MailInstallationNodeContribution.class);
	}
	
	/****************************
	* SMTP Recipient Input
	*****************************/
	public KeyboardTextInput getKeyboardForSmtpRecipient() {
		KeyboardTextInput keyboardInput = keyboardFactory.createStringKeyboardInput();
		keyboardInput.setInitialValue( getSmtpRecipient() );		
		return keyboardInput;
	}

	public KeyboardInputCallback<String> getCallbackForSmtpRecipient() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setSmtpRecipient(value);
				view.setSmtpRecipient(value);
			}
		};
	}
	
	public String getSmtpRecipient() {
		return model.get(MAIL_RECIPIENT_KEY, MAIL_RECIPIENT_DEFAULT_VALUE);
	}

	public void setSmtpRecipient(String message) {
		if ("".equals(message)) {
			resetToDefaultSmtpRecipient();
		} else {
			model.set( MAIL_RECIPIENT_KEY, message );
		}
	}

	private void resetToDefaultSmtpRecipient() {
		view.setSmtpRecipient(MAIL_RECIPIENT_DEFAULT_VALUE);
		model.set(MAIL_RECIPIENT_KEY, MAIL_RECIPIENT_DEFAULT_VALUE);
	}
	
	/****************************
	* SMTP Subject Input
	*****************************/
	public KeyboardTextInput getKeyboardForSmtpSubject() {
		KeyboardTextInput keyboardInput = keyboardFactory.createStringKeyboardInput();
		keyboardInput.setInitialValue( getSmtpSubject() );		
		return keyboardInput;
	}

	public KeyboardInputCallback<String> getCallbackForSmtpSubject() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setSmtpSubject(value);
				view.setSmtpSubject(value);
			}
		};
	}
	
	public String getSmtpSubject() {
		return model.get(MAIL_SUBJECT_KEY, MAIL_SUBJECT_DEFAULT_VALUE);
	}

	public void setSmtpSubject(String message) {
		if ("".equals(message)) {
			resetToDefaultSmtpSubject();
		} else {
			model.set( MAIL_SUBJECT_KEY, message );
		}
	}

	private void resetToDefaultSmtpSubject() {
		view.setSmtpSubject(MAIL_SUBJECT_DEFAULT_VALUE);
		model.set(MAIL_SUBJECT_KEY, MAIL_SUBJECT_DEFAULT_VALUE);
	}
	
	/****************************
	* SMTP Message Input
	*****************************/
	public KeyboardTextInput getKeyboardForSmtpMessage() {
		KeyboardTextInput keyboardInput = keyboardFactory.createStringKeyboardInput();
		keyboardInput.setInitialValue( getSmtpMessage() );		
		return keyboardInput;
	}

	public KeyboardInputCallback<String> getCallbackForSmtpMessage() {
		return new KeyboardInputCallback<String>() {
			@Override
			public void onOk(String value) {
				setSmtpMessage(value);
				view.setSmtpMessage(value);
			}
		};
	}
	
	public String getSmtpMessage() {
		return model.get(MAIL_MESSAGE_KEY, MAIL_MESSAGE_DEFAULT_VALUE);
	}

	public void setSmtpMessage(String message) {
		if ("".equals(message)) {
			resetToDefaultSmtpMessage();
		} else {
			model.set( MAIL_MESSAGE_KEY, message );
		}
	}

	private void resetToDefaultSmtpMessage() {
		view.setSmtpMessage(MAIL_MESSAGE_DEFAULT_VALUE);
		model.set(MAIL_MESSAGE_KEY, MAIL_MESSAGE_DEFAULT_VALUE);
	}
}
