package mail;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;

public class pop3 {

	public HashMap<Integer, Integer> getMailList() {
		list();
		return mail_list;
	}
	
	public int getEmailNum() {
		status();
		return tot_email;
	}
	
	public int getTotalSize() {
		status();
		return tot_size;
	}
	
	public boolean init(String s) {
		return init(s, 110);
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
		if(res == null || !res.startsWith("+OK")) {
			System.out.println("Error: receive " + res);
			close();
			return false;
		} else {
			System.out.println("-----> " + res);
		}
		
		return true;
	}
	
	public boolean login(String un, String pw) {
		assertAll();
		
		String res;
		
		try{
			send("USER " + un + CRLF);
			res = get();
			check(res, "+OK", "LOGIN", res);
			send("PASS " + pw + CRLF);
			res = get();
			check(res, "+OK", "PASSWORD", res);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;	
	}
	
	public boolean quit() {
		assertAll();
		
		String res;
		
		try{
			send("QUIT " + CRLF);
			res = get();
			check(res, "+OK", "QUIT", res);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean deleteMail(int t) {
		assertAll();
		
		String res;
		
		try{
			send("DELE " + t + CRLF);
			res = get();
			check(res, "+OK", "DELETE", res);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean status() {	
		assertAll();
		
		String res;
		
		try{
			send("STAT " + CRLF);
			res = get();
			check(res, "+OK", "STAT", res);
			parse_status(res.substring(4));
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		
		return true;
	}
	
	public boolean list() {
		assertAll();
		
		String res;
		
		try{
			send("LIST " + CRLF);
			res = get();
			check(res, "+OK", "LIST", res);
			
			parse_status(res.substring(4));
			
			//retrieve all email status
			int tot = tot_email, no, size;
			mail_list.clear();
			while(tot>0) {
				--tot;
				res = get();
				no = Integer.parseInt(res.substring(0, res.indexOf(" ")));
				size = Integer.parseInt(res.substring(res.indexOf(" ")+1, res.length()));
				mail_list.put(new Integer(no), new Integer(size));
			}
			
			res = get();
			
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public String viewMail(int t) {
		assertAll();
		
		String res;
		
		try {
			send("RETR " + t + CRLF);
			res = get();
			check(res, "+OK", "RETR "+t, res);
			String ans = "";
			while((res = get()).intern()!=".".intern()) {
				ans += res + "\r\n";
			}
			return ans;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void parse_status(String x) {
		tot_email = Integer.parseInt(x.substring(0, x.indexOf(" ")));
		tot_size = Integer.parseInt(x.substring(x.indexOf(" ")+1, x.length()));
		System.out.println(tot_email+" "+tot_size);
	}
	
	public void assertAll() {
		assert(socket != null);
		assert(in != null);
		assert(out != null);
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
	
	BufferedReader in;
	DataOutputStream out;
	
	String server;
	int port, tot_email, tot_size;
	
	HashMap<Integer, Integer> mail_list = new HashMap<Integer, Integer>();
	
	final String CRLF = "\r\n";
}
