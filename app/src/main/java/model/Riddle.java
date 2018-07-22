package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="Riddle")
public class Riddle {
	
	public String text;
	public String jokerText;
	public boolean isMCQ;
	public List<AnswerChoice> answerChoices;
	
	// constructeur public sans arguments nécéssaire à jackson
	public Riddle() {
		answerChoices = new ArrayList<AnswerChoice>();
	}
	
}
