package model;



public interface StepFactory {
	
	public Step createInstance(String id, float latitude, float longitude);
	
}
