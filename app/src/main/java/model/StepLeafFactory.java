package model;


public class StepLeafFactory implements StepFactory {

	@Override
	public Step createInstance(String id, float latitude, float longitude) {
		// TODO Auto-generated method stub
		return new StepLeaf(id, latitude, longitude);
	}

}
