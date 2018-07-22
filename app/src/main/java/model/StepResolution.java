package model;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name="StepResolution")
public class StepResolution {
	
	public String stepId;
	public int durationInMinutes;
	public boolean jokerUsed;
	
	// constructeur public sans arguments nécéssaire à jackson
	public StepResolution() {
		
	}
	
	@XmlTransient
	@JsonIgnore
	public int getScore() {
		return 0;
	}
	
}
