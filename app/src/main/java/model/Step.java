package model;

import com.fasterxml.jackson.annotation.*;



@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@JsonSubTypes({ @JsonSubTypes.Type(value = StepComposite.class, name = "StepComposite"),
				@JsonSubTypes.Type(value = StepLeaf.class, name = "StepLeaf") })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public abstract class Step {
	
	public String id;
	public float latitude;
	public float longitude;
	public int scorePointsGivenIfSuccess;
	public int maximumDurationInMinutes;
	public String description;
	public Riddle riddle;
	
}
