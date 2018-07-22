package model;




public class StepCompositeFactory implements StepFactory {

	@Override
	public Step createInstance(String id, float latitude, float longitude) {
		// TODO Auto-generated method stub
		return new StepComposite(id, latitude, longitude);
	}

}
