package mail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.LinkedList;

import it.sauronsoftware.base64.Base64;

public class smtp {
	
	public void setAccount(String acc) {
		account = acc;
	}
	
	public void setUsername(String un) {
		username = un;
	}
	
	public void setPassword(String pw) {
		password = pw;
	}
	
	public void addRecept(String rc) {
		recept_list.add(rc);
	}
	
	public void setSubject(String sb) {
		subject = sb;
	}
	
	public void setContent(String ct) {
		content = ct;
	}
	
	public boolean init(String s) {
		return init(s, 25);
	}
	
	public boolean init(String s, int p) {
		
		server = s;
		port = p;
		
		// create new socket and connect it to the server
		try{
			socket = new Socket(server, port);
		} catch(Exception e) {
			System.out.println("Fail to connect to the server: " + server + " on port " + port + "!");
			return false;
		}
		
		// initialize the outputStream and inputStream
		try{
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new DataOutputStream(socket.getOutputStream()); 
		} catch(Exception e) {
			System.out.println("Fail to use this created socket to communicate!");
			return false;
		}
		
		// wait for acknowledgement of connection
		String res = null;
		try{
			res = get();
		} catch (Exception e) {
			System.out.println("Error: Get Response!");
			return false;
		}
		if(res == null || !res.startsWith("220")) {
			System.out.println("Error: receive " + res);
			close();
			return false;
		}
		
		return true;
	}
	
	public boolean sendEmail() {
		
		assert(socket != null);
		assert(in != null);
		assert(out != null);
		
		String res;
		
		try {
			
			//HELO
			send("HELO " + server + CRLF);
			res = get();
			check(res, "250", "HELO", res);
			
			//AUTH LOGIN
			send("AUTH LOGIN " + CRLF);
			res = get();
			check(res, "334", "LOGIN", res);
			
			//send username
			send(Base64.encode(username) + CRLF);
			res = get();
			check(res, "334", "USERNAME", res);
			
			//send password
			send(Base64.encode(password) + CRLF);
			res = get();
			check(res, "235", "USERNAME", res);
			
			//MAIL FROM
			send("MAIL FROM:<" + account + ">" + CRLF);
			res = get();
			check(res, "250", "MAIL FROM", res);
			
			String message = "From: " + account + CRLF;
			
			//MAIL RCPT
			for(String recept: recept_list) {
				send("RCPT TO:<" + recept + ">" + CRLF);
				res = get();
				check(res, "250", "RCPT TO", res);
				
				message += "To: " + recept + CRLF;
			}
			
			//DATA
			send("DATA" + CRLF);
			res = get();
			check(res, "354", "DATA", res);
			
			//send message
			message += "Subject: " + subject + CRLF;
			message += CRLF;
			message += content + CRLF + ".";
			
			send(message + CRLF);
			res = get();
			check(res, "250", "SEND MESSAGE", res);
			
			//QUIT
			send("QUIT" + CRLF);
			res = get();
			check(res, "221", "QUIT", res);
			
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
		
		return true;
	}
	
	public void send(String message) throws Exception {
		out.writeBytes(message);
		out.flush();
	}
	
	public String get() throws IOException {
		return in.readLine();
	}
	
	public void check(String input, String flag, String errorMessage, String receivedMessage) throws Exception {
		if(!input.startsWith(flag)) {
			Exception e = new Exception(errorMessage+" ERROR"+": "+receivedMessage);
			throw e;
		}
		System.out.println("-----> "+receivedMessage);
	}
	
	public void close() {
		try {
			socket.close();
			in.close();
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	Socket socket;
	int port;
	String server;
	
	BufferedReader in;
	DataOutputStream out;
	
	String username, account, password, subject, content;
	LinkedList<String> recept_list = new LinkedList<String>();
	
	final String CRLF = "\r\n";
}
