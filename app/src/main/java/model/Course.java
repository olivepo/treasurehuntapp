package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@XmlRootElement(name="Course")
public class Course {

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:nnnnnnnnn");
	
	// constructeur public sans arguments nécéssaire à jackson
	public Course() {
		this.begin = LocalDateTime.now();
		this.end = LocalDateTime.now();
	}
	
	public String id;
	public String accountEmail;
	public String name;
	
	@XmlTransient
	@JsonIgnore
	public LocalDateTime begin;
	@XmlElement
	public String getBegin() {
		return begin.format(formatter);
	}
	public void setBegin(String begin) {
		this.begin = LocalDateTime.parse(begin, formatter);
	}
	
	@XmlTransient
	@JsonIgnore
	public LocalDateTime end;
	@XmlElement
	public String getEnd() {
		return end.format(formatter);
	}
	public void setEnd(String end) {
		this.end = LocalDateTime.parse(end, formatter);
	}
	
	public int jokersAllowed;
	public StepComposite start;
	
	@XmlTransient
	public HashMap<String, Step> getStepsAsHashMap() {
		HashMap<String, Step> result = new HashMap<String, Step>();
		CourseStepsIterator iter = new CourseStepsIterator(this);
		Step currentStep = null;
		while (iter.hasNext()) {
			currentStep = iter.next();
			if (!(currentStep.id == start.id) && !result.containsKey(currentStep.id)) {
				result.put(currentStep.id, currentStep);
			}
		}
		return result;
	}
	
	@XmlTransient
	public List<Step> getSteps() {
		return new ArrayList<Step>(getStepsAsHashMap().values());
	}
	public void setSteps(List<Step> steps) {
		// l'étape de départ start doit obligatoirement être renseignée
		if (start == null) return;
		StepCompositeAddStepsFromList(start,steps);
	}
	
	private void StepCompositeAddStepsFromList(StepComposite step, List<Step> steps) {
		HashSet<String> nextStepsIds = new HashSet<String>(step.getNextStepsIds()); // copie car elle va être modifiée par le addStep()
		Iterator<Step> iter;
		Step currentStep;
		for(String nextStepId : nextStepsIds) {
			iter = steps.iterator();
			while (iter.hasNext()) {
				currentStep = iter.next();
				if (currentStep.id.equals(nextStepId)) {
					step.addStep(currentStep);
					if (currentStep instanceof StepComposite) {
						StepCompositeAddStepsFromList((StepComposite) currentStep,steps);
					}
				}
			}
			
		}
	}
	
	
	
}
