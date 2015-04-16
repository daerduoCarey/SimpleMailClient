*******************************************************************************************
*			SIMPLE MAIL CLIENT		designed and implemented by Kaichun Mo				  *
*******************************************************************************************

This is my Simple Mail Client supporting both receiving and sending e-mails. Besides, this 
is the project targeting as the first assignment in Computer Networking course in SJTU, 
Spring 2015, which is taught by Professor Yanmin Zhu.

In the following sections, I will introduce you how to implement the basics of both SMTP 
and POP3 using Java. In the first section, I will introduce you how to use this Simple Mail 
Client. In the second section, I will talk about the two protocols respectively. And, in 
the last section, I will briefly introduce my design structure.


1. HOW TO USE THIS SIMPLE MAIL CLIENT

You can run this Simple Mail Client by only typing into the terminal

						make
						make run

To clear the class files compiled, type

						make clear					

After running this program, you will see a Command User Interface and you can just follow 
the instructions inside the program itself. Have fun when using it!



2. BASICS ABOUT SMTP AND POP3

2.1 SMTP

I just implement the basic commands in SMTP. To be specific, HELO, AUTH, MAIL FROM, RCPT 
TO, DATA and QUIT. 

To handshake with the server before sending e-mails, we need to say HELO to the server and 
wait for a response beginning with status code 250. 

Before sending e-mails, we still need to login. 
The AUTH LOGIN command is used to achieve this goal. We need a series of contacts when 
logging in. All the user name and password are passed through base64. 

To send e-mails, we need to use MAIL FROM, RCPT TO and DATA. Within the DATA commands, we 
need to obey with the canonical e-mail format. For example, we use From, To and Subject to 
index all components in a regular e-mail.

To finish the communication with the server, client need to say QUIT to the server.

2.2 POP3

I just implement the basic commands in POP3. To be specific, USER, PASS, STAT, LIST, DELE, 
RETR and QUIT.

To log in, client need to communicate with the server through USER and PASS commands. Each 
correct communication with the server will be responded with some messages starting with 
"+OK". If you receive some messages starting with "-ERR", the server cannot help you to 
fulfill your job.

To get the status of the mailbox, we need to use STAT and LIST. STAT will be responded with 
the total number of the e-mails and the total size of occupied space on your server. LIST 
will be responded with the status information mentioned above, as well as a e-mail list, 
each of which is shown by two numbers, the number and the size of this e-mail.

My mail client use RETR to retrieve specific e-mail content. User has to indicate the 
number of the e-mail and the client will pass this parameter to the server. And, the server 
will send the exact e-mail to the client. And, to delete a specific e-mail on the server, 
one can use DELE.



3. IMPLEMENTATION DESIGN

I divide the whole program into four Java files. One is for the implementation of SMTP. One 
is for POP3. While the other twos are for UI and Main. The Java file userRecord is just an 
assistant for implementing of user account storage on the disk, which is part of UI Java 
File.

3.1 Main Java File

It just provides an entry to UI interface and start the program.

3.2 SMTP Java File

Each mail sending request will be fulfilled by a SMTP instance. UI instance will just pass 
some necessary parameters to a newly created SMTP instance. And, by calling the sendEmail 
method of this SMTP instance, the e-mail will be sent to the server through some contacts 
with the server.

In this file, all the SMTP commands mentioned in section 2.1 are implemented through socket 
interface provided in package java.net.socket. I can just put some message into the socket and 
the socket interface will automatically deliver this message to the server. After some seconds, 
I will get the response from the server. I can just read the message through the same socket 
instance. After getting the response message, I can send another messages to the server 
consequently. In case of errors, this Java class will deal with them.

3.3 POP3 Java File

Each mail receiving request will be fulfilled by a POP3 instance. UI instance will just pass 
some necessary parameters to a newly created POP3 instance. And, by calling the receiveEmail 
method of this POP3 instance, user can freely manage the e-mails located on the remote server. 
He or she can list, view or delete the e-mails through this client.

In this file, all the POP3 commands mentioned in section 2.2 are implemented through socket 
interface provided in package java.net.socket. I can just put some message into the socket and 
the socket interface will automatically deliver this message to the server. After some seconds, 
I will get the response from the server. I can just read the message through the same socket 
instance. After getting the response message, I can send another messages to the server 
consequently. In case of errors, this Java class will deal with them.

3.4 UI Java File

This Java class just provide a relatively simple but usable Command User Interface to the 
potential users. The users can write or read e-mails on their servers under the assistance 
of this CUI.

To log in using one account, the user must indicate some necessary information about his or 
her SMTP and POP3 servers. But, during the following logins, the user are required to type in 
the same annoying stuff again. He or she can just select one account from all the accounts 
that he or she has used before. This is implemented just by storing all the information into a 
file on the disk.

To send an e-mail, the user can indicate the receivers, subject and content of the e-mail. If 
all the things are correct, the e-mail will be successfully delivered to the targeted server. 
Users will see some prompts if some errors occurred. To receive e-mails, the user can just 
select operations among list, retrieve and delete. Each will fulfill some necessary jobs of a 
qualified mail receiver.




														Author: Kaichun Mo
														Student ID: 5120309052
														Contact: daerduomkch [at] sjtu.edu.cn
														Website: www.kaichun-mo.com

