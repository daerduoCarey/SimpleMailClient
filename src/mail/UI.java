package mail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
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
		
		// load one of user records
		System.out.println("First, you need to choose an account to use!\n");
		auto_setting();
		System.out.println("Now you can receive the e-mails of account: " + user_record.account);
		
		while(true) {
			System.out.print("\nWhat do you want to do? Read/Write emails(Enter q to quit): ");
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
		String prompt = "\nYou can do the following operations:\n";
		prompt += "1. read the mail list\n";
		prompt += "2. read a specific mail\n";
		prompt += "3. delete a mail\n";
		prompt += "4. quit\n";
		prompt += "Which one do you choose(Enter the number): ";
		
		flag = true;
		
		while(flag) {
			System.out.print(prompt);
			try {
				String tmp = in.readLine();
				int x = Integer.parseInt(tmp);
				switch(x) {
				case 1:
					System.out.print("\nFollowing are the data you want: ");
					System.out.println(mail.getMailList());
					break;
				case 2:
					System.out.print("\nTell me the email number to view: ");
					String tmp2 = in.readLine();
					int t = Integer.parseInt(tmp2);
					System.out.println(mail.viewMail(t));
					break;
				case 3:
					System.out.print("\nTell me the email number to delete: ");
					String tmp3 = in.readLine();
					int t2 = Integer.parseInt(tmp3);
					if(mail.deleteMail(t2)) {
						System.out.println("Successfully remove mail " + t2);
					} else {
						System.out.println("Error occurred when removing mail " + t2);
					}
					break;
				case 4: 
					flag = false;
					break;
				default:
					break;
				}
			} catch(Exception e) {
				
			}
		}
	}
	
	
	private void sendEmail() {
		
		System.out.println("\nNow, you can send an email.");
		
		smtp mail = new smtp();
		
		boolean flag = mail.init(user_record.smtp_server, user_record.smtp_port);
		if(!flag) {
			System.out.println("Error Occurred! Sorry!");
			return;
		}
		
		try{
			String res;
			
			//set account
			mail.setPassword(user_record.password);
			mail.setUsername(user_record.username);
			mail.setAccount(user_record.account);
			
			//recepts
			System.out.print("Your receivers(Separate by semicolon): ");
			res = in.readLine();
			mail.addRecepts(res);
			
			//subject
			System.out.print("Subject: ");
			res = in.readLine();
			mail.setSubject(res);
			
			//content
			System.out.println("Content(End with single line with a dot): ");
			String ans = "";
			res = in.readLine();
			while(res.intern() != ".".intern()) {
				ans += res + "\n";
				res = in.readLine();
			}
			mail.setContent(ans);
			
			//fake sender(optional)
			System.out.print("Fake Sender(Optional): ");
			res = in.readLine();
			mail.setAccount2(res);
			
			//send email
			mail.sendEmail();
		}catch(Exception e) {
			System.out.println("Error Occurred! Sorry!");
		}
		
		mail.close();
	}
	
	
	public void auto_setting() {
		try{
			BufferedReader input_file;
			try{
				input_file = new BufferedReader(new FileReader(record_file));
			}catch(FileNotFoundException e){
				PrintWriter output_file = new PrintWriter(new BufferedWriter(new FileWriter(record_file, true)));
			    output_file.close();
			    input_file = new BufferedReader(new FileReader(record_file));
			}
			
			LinkedList<userRecord> rec_list = new LinkedList<userRecord>();
			int total_records = 0;
			System.out.println("There are some user records you can use(if any):");
			while(true) {
				String rec = input_file.readLine();
				if(rec == null || rec.intern() == "".intern()) {
					break;
				}
				++ total_records;
				userRecord ur = new userRecord(rec);
				rec_list.add(ur);
				System.out.println(total_records+"\t"+ur.account);
			}
			if(total_records > 0) System.out.println();
			
			if(total_records == 0) {
				System.out.println("\nYou are the new users. You need to set up your account first.");
				input_file.close();
				create_new_setting();
				return;
			}
			
			while(true) {
				System.out.print("Choose one to use(Press 0 to create another one):");
				String no = in.readLine();
				int x;
				try	{
					x = Integer.parseInt(no);
				}catch(NumberFormatException e) {
					continue;
				}
				if(x>=1 && x<=total_records) {
					user_record = rec_list.get(x-1);
					input_file.close();
					break;
				} else if(x == 0) {
					input_file.close();
					create_new_setting();
					break;
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void create_new_setting() {
		
		try{
			
			System.out.print("Your SMTP server: ");
			String smtp = in.readLine();
			System.out.print("Your POP3 server: ");
			String pop3 = in.readLine();
			int smtp_port = getPortNum("Your SMTP port(Press Enter to Use Default 25): "); if(smtp_port == -1) smtp_port = 25;
			int pop3_port = getPortNum("Your POP3 port(Press Enter to Use Default 110): "); if(pop3_port == -1) pop3_port = 110;
			String account = getStringWithSubString("@", "Your account: ");
			System.out.print("Your password: ");
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
	
	private String getStringWithSubString(String sub, String prompt) {
		boolean flag = true;
		String res = "";
		
		while(flag) {
			System.out.print(prompt);
			
			try{
				res = in.readLine();
				int tmp = res.indexOf(sub);
				if(tmp >= 0) flag = false;
			}catch(Exception e) {
				
			}
		}
		
		return res;
	}
	
	private int getPortNum(String prompt) {
		int res = 0;
		boolean flag = true;
		
		while(flag) {
			System.out.print(prompt);
			try{
				String tmp = in.readLine();
				if(tmp.intern() == "".intern() || tmp == null) {
					return -1;
				}
				res = Integer.parseInt(tmp);
				flag = false;
			} catch(Exception e) {
				
			}
		}
		
		return res;
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
