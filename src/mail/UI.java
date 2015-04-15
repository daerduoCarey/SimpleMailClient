package mail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.LinkedList;

public class UI {
	
	public void run() {
		System.out.println("This is your simple mail client.\n"); 
		in = new BufferedReader(new InputStreamReader(System.in));
		String input;
		
		while(true) {
			System.out.println("What do you want to do? Read/Write emails(Enter q to quit): ");
			try{
				input = in.readLine();
			} catch(Exception e) {
				input = "";
			}
			
			if(input.intern() == "read".intern() || input.intern() == "Read".intern() ||
					input.intern() == "R".intern() || input.intern() == "r".intern()) {
				receiveEmail();
			} else if(input.intern() == "write".intern() || input.intern() == "Write".intern() ||
					input.intern() == "W".intern() || input.intern() == "w".intern()) {
				sendEmail();
			} else if(input.intern() == "quit".intern() || input.intern() == "Quit".intern() ||
					input.intern() == "Q".intern() || input.intern() == "q".intern()) {
				break;
			}
		}
		
		System.out.println("Bye. Have a good day!"); 
	}
	
	private void receiveEmail() {
		// load one of user records
		auto_setting();
		
		System.out.println("Now you can receive the e-mails of account: " + user_record.account);
		
		// connect to your server
		pop3 mail = new pop3();
		boolean flag = mail.init(user_record.pop3_server, user_record.pop3_port);
		if(!flag) {
			System.out.println("Error: cannot connect to server " + user_record.pop3_server + " on port "
					+ user_record.pop3_port);
			return;
		}
		
		flag = mail.login(user_record.username, user_record.password);
		if(!flag) {
			System.out.println("Error: cannot login with account. Please check your username or password!");
			return;
		}
		
		// regular operations
	}
	
	
	private void sendEmail() {
		
	}
	
	
	public void auto_setting() {
		try{
			BufferedReader input_file = new BufferedReader(new FileReader(record_file));
			
			LinkedList<userRecord> rec_list = new LinkedList<userRecord>();
			int total_records = 0;
			System.out.println("There are some user records you can use:\n");
			while(true) {
				++ total_records;
				String rec = in.readLine();
				if(rec.intern() == "".intern()) {
					break;
				}
				userRecord ur = new userRecord(rec);
				rec_list.add(ur);
				System.out.println(total_records+"\t"+ur.account);
			}
			
			System.out.println("Choose one to use(Press 0 to create another one):");
			while(true) {
				String no = in.readLine();
				int x = Integer.parseInt(no);
				if(x>=1 && x<=total_records) {
					user_record = rec_list.get(x);
					input_file.close();
					break;
				} else if(x == 0) {
					input_file.close();
					create_new_setting(total_records);
					break;
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void create_new_setting(int old_record_num) {
		
		try{
			
			System.out.println("Your SMTP server: ");
			String smtp = in.readLine();
			System.out.println("Your POP3 server: ");
			String pop3 = in.readLine();
			System.out.println("Your SMTP port: ");
			int smtp_port = Integer.parseInt(in.readLine());
			System.out.println("Your POP3 port: ");
			int pop3_port = Integer.parseInt(in.readLine());
			System.out.println("Your account: ");
			String account = in.readLine();
			System.out.println("Your password: ");
			String password = in.readLine();
			
			user_record = new userRecord(smtp, pop3, smtp_port, pop3_port, account, password);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		try{
			PrintWriter output_file = new PrintWriter(new BufferedWriter(new FileWriter(record_file, true)));
		    output_file.println(user_record);
		    output_file.close();
		}catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	//only for testing
	private void receiveEmail_test() {
		
		pop3 mail = new pop3();
		
		boolean flag = mail.init("pop3.163.com");
		if(!flag) {
			System.out.println("Error Occurred!");
			return;
		}
		
		mail.login("daerduoCarey", "mkch695660509");

		System.out.println(mail.viewMail(1));
		
		mail.quit();
	}
	
	private void sendEmail_test() {
		
		smtp mail = new smtp();
		
		boolean flag = mail.init("smtp.163.com");
		if(!flag) {
			System.out.println("Error Occurred!");
			return;
		}
		
		mail.setSubject("Hello");
		mail.setContent("Hello, world!");
		mail.addRecept("daerduoCarey@163.com");
		mail.setPassword("mkch695660509");
		mail.setUsername("daerduoCarey");
		mail.setAccount("daerduoCarey@163.com");
		mail.sendEmail();
		mail.close();
		
	}
	
	BufferedReader in;
	userRecord user_record = null;
	
	final String record_file = ".records";
}
