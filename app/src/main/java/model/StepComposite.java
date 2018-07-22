package model;

import java.util.HashMap;
import java.util.HashSet;

import javax.xml.bind.annotation.*;



@XmlRootElement(name="StepComposite")
public class StepComposite extends Step {
	
	@XmlElement
	private HashMap<String, Step> nextSteps;
	@XmlElement
	private HashSet<String> nextStepsIds;
	
	// constructeur public sans arguments nécéssaire à jackson
	public StepComposite() {
		nextSteps = new HashMap<String, Step>();
		nextStepsIds= new HashSet<String>();
	}
	
	public StepComposite(String id, float latitude, float longitude) {
		this();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		
	}
	
	@XmlTransient
	public HashSet<String> getNextStepsIds() {
		return nextStepsIds;
	}
	@XmlTransient
	public Step getNextStep(String id) {
		return nextSteps.get(id);
	}
	
	public void addStep(Step step) {
		nextSteps.put(step.id,step);
		nextStepsIds.add(step.id);
	}

	public void removeStep(String id) {
		nextSteps.remove(id);
		nextStepsIds.remove(id);
	}

}
