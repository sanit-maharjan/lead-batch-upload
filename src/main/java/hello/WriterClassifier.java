package hello;

import org.springframework.batch.support.annotation.Classifier;


public class WriterClassifier  {

	
	@Classifier
	public String classify(PersonDto classifiable) {
		if(classifiable.getOutputType().equals("csv"))
			return "csv";
		else
			return "excel";
	}
    
	
}
