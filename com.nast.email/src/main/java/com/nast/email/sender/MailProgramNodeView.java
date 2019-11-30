package com.nast.email.sender;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.nast.email.impl.Constants;
import com.nast.email.style.Style;
import com.ur.urcap.api.contribution.ContributionProvider;
import com.ur.urcap.api.contribution.program.swing.SwingProgramNodeView;
import com.ur.urcap.api.domain.userinteraction.keyboard.KeyboardTextInput;

public class MailProgramNodeView implements SwingProgramNodeView<MailProgramNodeContribution> {
	
		
	// Provider	
	ContributionProvider<MailProgramNodeContribution> cProvider;	 	
	private final Style style;
	
	/*************************************
	 * view elements
	 ************************************/
	// LED	
	ImageIcon imagesLed[];	
	int totalLedImages = 5;	
		
	private JPanel configView;
	
	// Warnings
	private Box warningRow;
	ImageIcon iconWarning;	
	private JLabel jLabel_warning_led;
	// Warning Message
	private JTextArea textAreaWarning = new JTextArea("");
	
	// Recipient Input
	private JLabel jLabel_smtp_recipient;
	private JTextField smtp_recipient;
	// Subject Input
	private JLabel jLabel_smtp_subject;
	private JTextField smtp_subject;
	// Message
	private JLabel jLabel_smtp_message;
	private JTextField smtp_message;
	
	/***************************************
	/* Style
	****************************************/
	MailProgramNodeView(Style style) {
		this.style = style;
	}
	/***************************************
	/* UI
	****************************************/
	@Override
	public void buildUI(JPanel jPanel, final ContributionProvider<MailProgramNodeContribution> provider) {
		cProvider = provider;
		
		Dimension layoutSize = jPanel.getPreferredSize();		
		Dimension dimPanelView 	= new Dimension( layoutSize.width-20, 400);
		
		// LEDs 
		imagesLed = new ImageIcon[totalLedImages];					
		imagesLed[0] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_LED_OFF ) );
		imagesLed[1] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_LED_GREEN ) );
		imagesLed[2] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_LED_YELLOW ) );
		imagesLed[3] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_LED_RED ) );
		imagesLed[4] = new ImageIcon( getClass().getResource( Constants.ICON_PATH + Constants.RES_SIGN_WARNING ) );
		
		configView = new JPanel();
		configView.setLayout( new BoxLayout( configView, BoxLayout.Y_AXIS ) );
		configView.setPreferredSize(dimPanelView);
		configView.setMinimumSize(dimPanelView);
		configView.setMaximumSize(dimPanelView);
		
		configView.add( this.createRowWarning() );	
		configView.add( this.createVerticalSpacing() );
		
		configView.add( this.createSmtpRecipientInput() );	
		configView.add( this.createVerticalSpacing() );
		
		configView.add( this.createSmtpSubjectInput() );	
		configView.add( this.createVerticalSpacing() );	
		
		configView.add( this.createSmtpMessageInput() );	
		configView.add( this.createVerticalSpacing() );		
		
		jPanel.add( configView );
		
	}
	/***************************************
	/* 
	****************************************/
	public void updateView() {
		
	}
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
	 * Message
	 * 
	 ************************************/
	private Box createRowWarning() {
						
		warningRow = Box.createHorizontalBox();
		warningRow.setAlignmentX(Component.LEFT_ALIGNMENT);	
		
		jLabel_warning_led = new JLabel( imagesLed[2] );
		warningRow.add( jLabel_warning_led );
		
		warningRow.add( createHorizontalSpacing() );		
		
		textAreaWarning.setOpaque(false);
		textAreaWarning.setSize(style.getTextAreaSize1());
		textAreaWarning.setMaximumSize(style.getTextAreaSize1());
		textAreaWarning.setLineWrap(true);
		textAreaWarning.setWrapStyleWord(true);	
		textAreaWarning.setEditable(false);
		warningRow.add(textAreaWarning);			
			
		return warningRow;
	}
	/***************************************
	/* set warning visible
	****************************************/	
	public void setWarningRowVisible( boolean value) {		
		warningRow.setVisible( value );				
	}
	/***************************************
	/* set warning text
	****************************************/		
	public void setWarningText ( String text) {
		textAreaWarning.setText( text );
	}
	/************************************
	 * Smtp Recipient Input
	 ************************************/
	private Box createSmtpRecipientInput() {
		
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		jLabel_smtp_recipient = new JLabel("Recipient");		
		jLabel_smtp_recipient.setSize( style.getLabelSizeLong() );
		jLabel_smtp_recipient.setMaximumSize( style.getLabelSize() );	
		jLabel_smtp_recipient.setMinimumSize( style.getLabelSize() );		
		row.add(jLabel_smtp_recipient);
		
		row.add( createHorizontalSpacing() );
		
		smtp_recipient = new JTextField(); 
		smtp_recipient.setFocusable(false);
		smtp_recipient.setPreferredSize(style.getInputfieldSizeLong());
		smtp_recipient.setMaximumSize(style.getInputfieldSizeLong());
				
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = cProvider.get().getKeyboardForSmtpRecipient();
				keyboardInput.show(smtp_recipient, cProvider.get().getCallbackForSmtpRecipient());
			}
		};						
		smtp_recipient.addMouseListener(mouseAdapter);
		row.add(smtp_recipient);
		
		return row;
	}
	public void setSmtpRecipient(String value) {		
		smtp_recipient.setText(value);
	}
	/************************************
	 * Smtp Subject Input
	 ************************************/
	private Box createSmtpSubjectInput() {
		
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		jLabel_smtp_subject = new JLabel("Subject");		
		jLabel_smtp_subject.setSize( style.getLabelSizeLong() );
		jLabel_smtp_subject.setMaximumSize( style.getLabelSize() );	
		jLabel_smtp_subject.setMinimumSize( style.getLabelSize() );		
		row.add(jLabel_smtp_subject);
		
		row.add( createHorizontalSpacing() );
		
		smtp_subject = new JTextField(); 
		smtp_subject.setFocusable(false);
		smtp_subject.setPreferredSize(style.getInputfieldSizeLong());
		smtp_subject.setMaximumSize(style.getInputfieldSizeLong());
				
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = cProvider.get().getKeyboardForSmtpSubject();
				keyboardInput.show(smtp_subject, cProvider.get().getCallbackForSmtpSubject());
			}
		};						
		smtp_subject.addMouseListener(mouseAdapter);
		row.add(smtp_subject);
		
		return row;
	}
	public void setSmtpSubject(String value) {		
		smtp_subject.setText(value);
	}
	
	/************************************
	 * Smtp Message
	 ************************************/
	private Box createSmtpMessageInput() {
						
		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		jLabel_smtp_message = new JLabel("Message");		
		jLabel_smtp_message.setSize( style.getLabelSizeLong() );
		jLabel_smtp_message.setMaximumSize( style.getLabelSize() );	
		jLabel_smtp_message.setMinimumSize( style.getLabelSize() );		
		row.add(jLabel_smtp_message);
				
		row.add( createHorizontalSpacing() );		
		
		smtp_message = new JTextField("");
		smtp_message.setPreferredSize(style.getInputfieldSizeLong());
		smtp_message.setMaximumSize(style.getInputfieldSizeLong());
				
		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				KeyboardTextInput keyboardInput = cProvider.get().getKeyboardForSmtpMessage();
				keyboardInput.show(smtp_subject, cProvider.get().getCallbackForSmtpMessage());
			}
		};						
		smtp_message.addMouseListener(mouseAdapter);		
		
		row.add(smtp_message);			
			
		return row;
	}
	public void setSmtpMessage(String value) {		
		smtp_message.setText(value);
	}
	
}
