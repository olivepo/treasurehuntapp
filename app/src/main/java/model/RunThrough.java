package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.HashMap;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;



@XmlRootElement(name="RunThrough")
public class RunThrough {
	
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:nnnnnnnnn");
	
	public String id;
	public String accountEmail;
	public String courseId;
	
	@XmlTransient
	@JsonIgnore
	public LocalDateTime startedAt;
	@XmlElement
	public String getStartedAt() {
		return startedAt.format(formatter);
	}
	public void setStartedAt(String startedAt) {
		this.startedAt = LocalDateTime.parse(startedAt, formatter);
	}
	
	@XmlTransient
	@JsonIgnore
	public LocalDateTime endedAt;
	@XmlElement
	public String getEndedAt() {
		return endedAt.format(formatter);
	}
	public void setEndedAt(String endedAt) {
		this.endedAt = LocalDateTime.parse(endedAt, formatter);
	}
	
	@XmlTransient
	private LocalDateTime currentStepBegin;
	@XmlElement
	public String getCurrentStepBegin() {
		return currentStepBegin.format(formatter);
	}
	public void setCurrentStepBegin(String currentStepBegin) {
		this.currentStepBegin = LocalDateTime.parse(currentStepBegin, formatter);
	}
	
	@XmlTransient
	private Step currentStep;
	@XmlTransient
	private HashMap<String,StepResolution> stepResolutions;
	@XmlElement
	public HashMap<String,StepResolution> getStepResolutions() {
		return stepResolutions;
	}
	public void setStepResolutions(HashMap<String,StepResolution> stepResolutions) {
		this.stepResolutions = stepResolutions;
	}
	
	// constructeur public sans arguments nécéssaire à jackson
	public RunThrough() {
		this.startedAt = LocalDateTime.now();
		this.endedAt = LocalDateTime.now();
		this.currentStepBegin = LocalDateTime.now();
		this.stepResolutions = new HashMap<String,StepResolution>();
	}
	
	@XmlTransient
	public boolean isCompleted() {
		return false;
	}
	
	@XmlTransient
	public int getScore() {
		return 0;
	}
	
	@XmlTransient
	public Step getCurrentStep() {
		return currentStep;
	}
	
	@XmlTransient
	public void setCurrentStep(Step step) {
		currentStepBegin = LocalDateTime.now();
		currentStep = step;
	}
	
	public void validateCurrentStepResolution(LocalDateTime time, boolean jokerUsed) {
		StepResolution stepResolution = new StepResolution();
		stepResolution.durationInMinutes = (int)Duration.between(currentStepBegin, time).toMinutes();
		stepResolution.jokerUsed = jokerUsed;
		stepResolution.stepId = currentStep.id;
		stepResolutions.put(currentStep.id,stepResolution);
	}
}
