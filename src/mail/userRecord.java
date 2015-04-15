package mail;

public class userRecord {

	public userRecord(String input) {
		//TODO
		username = account.substring(0, account.indexOf("@"));
	}
	
	public String toString() {
		String res = smtp_server + "|";
		res += pop3_server + "|";
		res += smtp_port + "|";
		res += pop3_port + "|";
		res += account +"|";
		res += password;
		return res;
	}
	
	public userRecord(String smtp, String pop3, int smtp_port_t, int pop3_port_t, 
			String account_t, String password_t) {
		smtp_server = smtp;
		pop3_server = pop3;
		smtp_port = smtp_port_t;
		pop3_port = pop3_port_t;
		account = account_t;
		password = password_t;
		username = account.substring(0, account.indexOf("@"));
	}
	
	String smtp_server, pop3_server;
	String username, account, password;
	int smtp_port, pop3_port;
}
