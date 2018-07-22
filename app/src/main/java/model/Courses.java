package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;



@XmlRootElement(name="Courses")
public class Courses {
	
	public List<Course> list;
	
	// constructeur public sans arguments nécéssaire à jackson
	public Courses() {
		list = new ArrayList<Course>();
	}
	
}