package clientrest;

import static org.junit.Assert.*;
import org.junit.*;

import java.time.LocalDateTime;
import java.util.List;

import client.CourseRESTMethods;
import treasurehunt.model.AnswerChoice;
import treasurehunt.model.Course;
import treasurehunt.model.Riddle;
import treasurehunt.model.StepComposite;
import treasurehunt.model.StepCompositeFactory;
import treasurehunt.model.StepLeaf;
import treasurehunt.model.StepLeafFactory;


public class CourseRESTMethodsTest {

	private final static String id = "uniqueid";
	private final static String accountEmail = "test@montest.fr";
	private final static String name = "name";
	private final static LocalDateTime end = LocalDateTime.now();
	private final static int jokersAllowed = 3;
	
	@Test
	public void testAll() {

		testDelete();
		// base vide : Echec des operations GET,DELETE
		assertFalse(testGet());
		assertFalse(testDelete());

		// insertion de 1 element
		assertTrue(testPut());

		// base complete : réussite des opérations GET,DELETE
		assertTrue(testGet());
		assertTrue(testGetAll());
		assertTrue(testDelete());

		// base a nouveau vide
		assertFalse(testGet());
	}


	private boolean testPut() {

		Course c = new Course();
		c.id = id;
		c.accountEmail = accountEmail;
		c.name = name;
		c.end = end;
		c.jokersAllowed = jokersAllowed;
		c.start = (StepComposite) new StepCompositeFactory().createInstance("step1",0.0f,0.0f);
		c.start.id = "step1id";
		c.start.riddle = new Riddle();
		c.start.riddle.isMCQ = true;	
		c.start.riddle.answerChoices.add(new AnswerChoice("réponse1",true));
		c.start.riddle.answerChoices.add(new AnswerChoice("réponse2",false));
		StepComposite step2 = (StepComposite) new StepCompositeFactory().createInstance("step2",0.0f,0.0f);
		StepComposite step3 = (StepComposite) new StepCompositeFactory().createInstance("step3",0.0f,0.0f);
		step3.riddle = new Riddle();
		step3.riddle.answerChoices.add(new AnswerChoice("Réponse d",true));
		StepLeaf step4 = (StepLeaf) new StepLeafFactory().createInstance("step4",0.0f,0.0f);
		c.start.addStep(step2);
		c.start.addStep(step3);
		step2.addStep(step4);
		step3.addStep(step4);
		
		try {
			return CourseRESTMethods.put(c);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean testGet() {

		Course c;
		try {
			c = CourseRESTMethods.get(id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (c == null) return false;
		if (!c.id.equals(id)) return false;
		if (!c.name.equals(name)) return false;
		if (!c.end.equals(end)) return false;
		if (!c.start.id.equals("step1id")) return false;
		if (!c.start.getNextStepsIds().contains("step2")) return false;
		if (!c.start.getNextStepsIds().contains("step3")) return false;
		StepComposite step2 = (StepComposite) c.start.getNextStep("step2");
		StepComposite step3 = (StepComposite) c.start.getNextStep("step3");
		if (!step2.getNextStepsIds().contains("step4")) return false;
		if (!step3.getNextStepsIds().contains("step4")) return false;
		if (step2.getNextStep("step4") != step3.getNextStep("step4")) return false; // les deux doivent pointer vers le même objet step4
		if (!step3.riddle.answerChoices.get(0).text.equals("Réponse d")) return false;
		
		return true;

	}

	private boolean testDelete() {

		try {
			return CourseRESTMethods.delete(id);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean testGetAll() {

		List<Course> list;
		try {
			list = CourseRESTMethods.getNearestCourses(0.0f,0.0f);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return !list.isEmpty();

	}

}