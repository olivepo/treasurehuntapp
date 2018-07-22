package model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;



@XmlRootElement(name="RunThroughs")
public class RunThroughs {
	
	public List<RunThrough> list;
	
	// constructeur public sans arguments nécéssaire à jackson
	public RunThroughs() {
		list = new ArrayList<RunThrough>();
	}
	
}
