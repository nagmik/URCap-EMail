package com.nast.email.install;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.nast.email.impl.Constants;
import com.nast.email.style.Style;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardNumberInput;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

public class MailInstallationNodeView implements SwingInstallationNodeView<MailInstallationNodeContribution> {
	

	private final Style style;
			
	// Provider	
	MailInstallationNodeContribution cInstallationNode;	
	
	// LED
	ImageIcon imagesLed[];			
	int totalLedImages = 5;				
	
	// Service / Daemon 
	private JButton btnStartDaemon;
	private JButton btnStopDaemon;
	
	private JLabel 	jLabel_config_service;
	private JLabel 	jLabel_config_service_led;	
		
	// SMTP // Server
	private JLabel 	jLabel_config_smtp_server;
	private JTextField smtp_server;
	// SMTP // Port
	private JLabel 	jLabel_config_smtp_port;
	private JTextField smtp_port;
	// SMTP // User
	private JLabel 	jLabel_config_smtp_user;
	private JTextField smtp_user;
	// SMTP // Password
	private JLabel 	jLabel_config_smtp_password;
	private JPasswordField smtp_password;
	// SMTP // Sender
	private JLabel 	jLabel_config_smtp_sender;
	private JTextField smtp_sender;
	
	// URCAP IS ACTIVE
	public JCheckBox cbUrcapActive;	

	/************************************
	 * 
	 * 
	 ************************************/
	public  MailInstallationNodeView(Style style) {
		this.style = style;
	}
	
	/************************************
	 * 
	 * 
	 ************************************/
	@Override
	public void buildUI(JPanel jPanel, final  MailInstallationNodeContribution installationNode) {
		
		cInstallationNode = installationNode;
		
		Dimension layoutSize = jPanel.getPreferredSize();
		
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));		
		jPanel.add( createRowLogo( layoutSize.width ) );
		jPanel.add( createView( layoutSize ) );	
		
		
	}	
	/************************************
	 * 
	 * 
	 ************************************/
	public void updateView() {
				
		// update checkbox
		updateCheckBoxUrcapActive();		
		
	}
	/************************************
	 * 
	 * 
	 ************************************/
	private void updateUI() {}
	/***************************************
	/* Spacing
	****************************************/	
	private Component createVerticalSpacing() {
		return Box.createRigidArea(new Dimension(  0, style.getVerticalSpacing() ));
	}	
	
	private Component createHorizontalSpacing() {
		return Box.createRigidArea(new Dimension( style.getHorizontalSpacing(), 0 ));
	}			
	/************************************
	 * logo
	 * 
	 ************************************/
	private Box createRowLogo( int containerWidth ) {
						
		Box logoBox = Box.createHorizontalBox();
		logoBox.setAlignmentX(Component.LEFT_ALIGNMENT);	
		
		ImageIcon logo 	= new ImageIcon( getClass().getResource( Constants.NAST_INSTALL_LOGO ) );
		JLabel logoLabel = new JLabel( logo );
		
		int spacerWidth = (int) (containerWidth - logoLabel.getPreferredSize().getWidth() - 10);		
		Dimension dimLabel = new Dimension( spacerWidth, 40);
				
		JLabel blank = new JLabel("");		
		blank.setSize( dimLabel );
		blank.setMaximumSize( dimLabel );
		blank.setPreferredSize( dimLabel );
				
		logoBox.add( blank );				
		logoBox.add( logoLabel );					
			
		return logoBox;
	}
	/************************************
	 * create View
	 ************************************/
	private JPanel createView( Dimension layoutSize ) {
						
		// LEDs 
		imagesLed = new ImageIcon[totalLedImages];					
		imagesLed[0] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_LED_OFF ) );
		imagesLed[1] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_LED_GREEN ) );
		imagesLed[2] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_LED_YELLOW ) );
		imagesLed[3] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_LED_RED ) );
		imagesLed[4] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_SIGN_WARNING ) );
							
		JPanel jPanel1 = new JPanel();		
		jPanel1.setLayout( new BoxLayout( jPanel1, BoxLayout.Y_AXIS ) );					
								
		jPanel1.add( createCheckBoxUrcapActive() );
		jPanel1.add( createVerticalSpacing() );
		
		jPanel1.add( createRowMailService() );
		jPanel1.add( createVerticalSpacing() );			
		
		jPanel1.add( createSmtpServerInput() );
		jPanel1.add( createVerticalSpacing() );		                 
		
		jPanel1.add( createSmtpPortInput() );
		jPanel1.add( createVerticalSpacing() );
				
		jPanel1.add( createSmtpUserInput() );
		jPanel1.add( createVerticalSpacing() );		
		
		jPanel1.add( createSmtpPasswordInput() );
		jPanel1.add( createVerticalSpacing() );		
		
		jPanel1.add( createSmtpSenderInput() );
		jPanel1.add( createVerticalSpacing() );		
	
		return jPanel1;		
	}
	
	/************************************
	 * URCap Active
	 ************************************/
	private Box createCheckBoxUrcapActive() {
		
		Box cbBox = Box.createHorizontalBox();
		cbBox.setAlignmentX(Component.LEFT_ALIGNMENT);	
		
		cbUrcapActive = new JCheckBox("URCAP is activated");
		this.cbUrcapActive.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {												
				boolean isSelected = cbUrcapActive.isSelected();
				cInstallationNode.setUrcapActive( isSelected );				
				updateCheckBoxUrcapActive();				
			}
		});		
		cbBox.add(cbUrcapActive);					
		return cbBox;
	}
	/************************************
	 * update check box urcap active
	 ************************************/
	private void updateCheckBoxUrcapActive() {				
		boolean isSelected = cInstallationNode.getUrcapActive();	
		cbUrcapActive.setSelected( isSelected );	
		if( !isSelected ) {
			cInstallationNode.onStopClick();
		}
	}
	
	/************************************
	 * 
	 ************************************/	
	private Box createRowMailService() {
		
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);	
				
		jLabel_config_service_led = new JLabel( imagesLed[0] );
		row.add( jLabel_config_service_led );
		
		row.add( createHorizontalSpacing() );
						
		jLabel_config_service = new JLabel("Mail Service");
		jLabel_config_service.setSize( style.getLabelSizeLong_1() );
		jLabel_config_service.setMaximumSize( style.getLabelSizeLong_1() );		
		row.add(jLabel_config_service);
		
		row.add( createHorizontalSpacing() );
		btnStartDaemon = new JButton("Start");
		btnStartDaemon.setSize(style.getButtonSize());
		btnStartDaemon.setMaximumSize(style.getButtonSize());
		btnStartDaemon.setMinimumSize(style.getButtonSize());
		btnStartDaemon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cInstallationNode.onStartClick();
			}
		});
		row.add(btnStartDaemon);
		
		row.add( createHorizontalSpacing() );
		
		btnStopDaemon = new JButton("Stop");
		btnStopDaemon.setSize(style.getButtonSize());
		btnStopDaemon.setMaximumSize(style.getButtonSize());
		btnStopDaemon.setMinimumSize(style.getButtonSize());
		btnStopDaemon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cInstallationNode.onStopClick();
			}
		});
		row.add(btnStopDaemon);					
				
		return row;
	}	
	/************************************
	 * activate/deactivate Button
	 ************************************/	
	public void setStartButtonEnabled(boolean enabled) {		
		btnStartDaemon.setEnabled(enabled);		
	}
	/************************************
	 * activate/deactivate Button
	 ************************************/	
	public void setStopButtonEnabled(boolean enabled) {		
		btnStopDaemon.setEnabled(enabled);		
	}		
	/************************************
	 * Daemon State LED
	 ************************************/	
	public void setDaemonState(boolean running) {
		if(running){
			jLabel_config_service_led.setIcon( imagesLed[1] );
		}else {
			jLabel_config_service_led.setIcon( imagesLed[0] );		
		}
	}		
	/************************************
	 * Smtp Server Input
	 ************************************/
	private Box createSmtpServerInput() {
		
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);	
				
		jLabel_config_smtp_server = new JLabel("SMTP Server");		
		jLabel_config_smtp_server.setSize( style.getLabelSizeLong() );
		jLabel_config_smtp_server.setMaximumSize( style.getLabelSizeLong() );	
		jLabel_config_smtp_server.setMinimumSize( style.getLabelSizeLong() );		
		row.add(jLabel_config_smtp_server);
		
		row.add( createHorizontalSpacing() );
		
		smtp_server = new JTextField(); 
		smtp_server.setFocusable(false);
		smtp_server.setPreferredSize(style.getInputfieldSizeLong());
		smtp_server.setMaximumSize(style.getInputfieldSizeLong());
				
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = cInstallationNode.getKeyboardForSmtpServer();
				keyboardInput.show(smtp_server, cInstallationNode.getCallbackForSmtpServer());
			}
		};						
		smtp_server.addMouseListener(mouseAdapter);
		row.add(smtp_server);
		
		return row;	
	}
	
	public void setSmtpServer(String value) {
		smtp_server.setText(value);
	}
	
	/************************************
	 * SMTP Port
	 ************************************/
	private Box createSmtpPortInput() {
		
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);	
				
		jLabel_config_smtp_port = new JLabel("SMTP Port");
		jLabel_config_smtp_port.setSize(style.getLabelSizeLong());
		jLabel_config_smtp_port.setMaximumSize(style.getLabelSizeLong());	
		jLabel_config_smtp_port.setMinimumSize(style.getLabelSizeLong());		
		row.add(jLabel_config_smtp_port);
		
		row.add( createHorizontalSpacing() );
		
		smtp_port = new JTextField(); 
		smtp_port.setFocusable(false);
		smtp_port.setPreferredSize(style.getInputfieldSizeLong());
		smtp_port.setMaximumSize(style.getInputfieldSizeLong());
				
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardNumberInput<Integer> keyboardInput = cInstallationNode.getKeyboardForSmtpPort();
				keyboardInput.show(smtp_port, cInstallationNode.getCallbackForSmtpPort());
			}
		};						
		smtp_port.addMouseListener(mouseAdapter);
		row.add(smtp_port);
		
		return row;	
	}
	public void setSmtpPort(Integer value) {
		smtp_port.setText( value.toString() );
	}
	/************************************
	 * Smtp User Input
	 ************************************/
	private Box createSmtpUserInput() {
		
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);	
				
		jLabel_config_smtp_user = new JLabel("User");		
		jLabel_config_smtp_user.setSize( style.getLabelSizeLong() );
		jLabel_config_smtp_user.setMaximumSize( style.getLabelSizeLong() );	
		jLabel_config_smtp_user.setMinimumSize( style.getLabelSizeLong() );		
		row.add(jLabel_config_smtp_user);
		
		row.add( createHorizontalSpacing() );
		
		smtp_user = new JTextField(); 
		smtp_user.setFocusable(false);
		smtp_user.setPreferredSize(style.getInputfieldSizeLong());
		smtp_user.setMaximumSize(style.getInputfieldSizeLong());
				
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = cInstallationNode.getKeyboardForSmtpUser();
				keyboardInput.show(smtp_user, cInstallationNode.getCallbackForSmtpUser());
			}
		};						
		smtp_user.addMouseListener(mouseAdapter);
		row.add(smtp_user);
		
		return row;	
	}
	
	public void setSmtpUser(String value) {		
		smtp_user.setText(value);
	}
	
	/************************************
	 * Smtp Password Input
	 ************************************/
	private Box createSmtpPasswordInput() {
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		jLabel_config_smtp_password = new JLabel("Password");		
		jLabel_config_smtp_password.setSize( style.getLabelSizeLong() );
		jLabel_config_smtp_password.setMaximumSize( style.getLabelSizeLong() );	
		jLabel_config_smtp_password.setMinimumSize( style.getLabelSizeLong() );		
		row.add(jLabel_config_smtp_password);
		
		row.add( createHorizontalSpacing() );
		
		smtp_password = new JPasswordField(); 
		smtp_password.setFocusable(false);
		smtp_password.setPreferredSize(style.getInputfieldSizeLong());
		smtp_password.setMaximumSize(style.getInputfieldSizeLong());
				
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = cInstallationNode.getKeyboardForSmtpPassword();
				keyboardInput.show(smtp_password, cInstallationNode.getCallbackForSmtpPassword());
			}
		};						
		smtp_password.addMouseListener(mouseAdapter);
		row.add(smtp_password);
		
		return row;
	}
	public void setSmtpPassword(String value) {		
		smtp_password.setText(value);
	}
	/************************************
	 * Smtp Sender Input
	 ************************************/
	private Box createSmtpSenderInput() {
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		jLabel_config_smtp_sender = new JLabel("Sender");		
		jLabel_config_smtp_sender.setSize( style.getLabelSizeLong() );
		jLabel_config_smtp_sender.setMaximumSize( style.getLabelSizeLong() );	
		jLabel_config_smtp_sender.setMinimumSize( style.getLabelSizeLong() );		
		row.add(jLabel_config_smtp_sender);
		
		row.add( createHorizontalSpacing() );
		
		smtp_sender = new JTextField(); 
		smtp_sender.setFocusable(false);
		smtp_sender.setPreferredSize(style.getInputfieldSizeLong());
		smtp_sender.setMaximumSize(style.getInputfieldSizeLong());
				
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = cInstallationNode.getKeyboardForSmtpSender();
				keyboardInput.show(smtp_sender, cInstallationNode.getCallbackForSmtpSender());
			}
		};						
		smtp_sender.addMouseListener(mouseAdapter);
		row.add(smtp_sender);
		
		return row;
	}
	public void setSmtpSender(String value) {		
		smtp_sender.setText(value);
	}
}
