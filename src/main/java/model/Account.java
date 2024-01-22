package model;

public class Account {
	
	String username;
	String email;
	String psw;
	
	public Account(String us, String em, String ps) {
		
		username = us;
		email = em;
		psw = ps;
	}
	
	public Account(String us) {
		username = us;
	}
}
