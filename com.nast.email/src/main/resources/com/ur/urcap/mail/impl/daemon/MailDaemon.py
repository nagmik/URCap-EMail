#!/usr/bin/env python

import xmlrpclib
from SimpleXMLRPCServer import SimpleXMLRPCServer
import string
import smtplib
from email.MIMEMultipart import MIMEMultipart
from email.MIMEText import MIMEText

# *****************************
# send email
# *****************************
def sendMail( smtpHost, smtpPort, username, password, mailRecipient, mailFrom, subject, message ):
		
	msg = MIMEMultipart()
	msg['From'] = mailFrom
	msg['To'] = mailRecipient
	msg['Subject'] = subject
	 
	body = message
	msg.attach(MIMEText(body, 'plain'))	 
		
	## Connect to host
    	try:
        	server = smtplib.SMTP(smtpHost, smtpPort) 
    	except smtplib.socket.gaierror:		
        	return 400

    	## Login
    	try:
        	server.login(username, password)
    	except smtplib.SMTPAuthenticationError:
        	server.quit()		
        	return 401

    	## Send message
    	try:
		text = msg.as_string()
        	server.sendmail(mailFrom, mailRecipient, text)        	
    	except smtplib.SMTPException:         	
		return 402
    	finally:		
        	server.quit()

	return 200	
# *****************************
server = SimpleXMLRPCServer(("", 33015), allow_none=True)
server.register_function(sendMail, "sendMail")
server.serve_forever()
