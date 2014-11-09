package org.biouno.unochoice.model;

import java.io.Serializable;
import java.util.Map;

public interface Script extends Serializable {

	Object eval();
	
	Object eval(Map<String, String> parameters);
	
}
