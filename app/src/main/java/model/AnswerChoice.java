package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="AnswerChoice")
public class AnswerChoice {
	
	public String text;
	public boolean isValid;
	
	// constructeur public sans arguments nécéssaire à jackson
	public AnswerChoice() {
		
	}
	
	public AnswerChoice(String text,boolean isValid) {
		this.text = text;
		this.isValid = isValid;
	}
	
}
