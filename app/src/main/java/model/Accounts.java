package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;



@XmlRootElement(name="Accounts")
public class Accounts {
	
	public List<Account> list;
	
	// constructeur public sans arguments nécéssaire à jackson
	public Accounts() {
		list = new ArrayList<Account>();
	}
	
}
