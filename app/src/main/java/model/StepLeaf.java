package model;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="StepLeaf")
public class StepLeaf extends Step {
	
	// constructeur public sans arguments nécéssaire à jackson
	public StepLeaf() {
		
	}
	
	public StepLeaf(String id, float latitude, float longitude) {
		this();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public String courseEndMessage;


}
