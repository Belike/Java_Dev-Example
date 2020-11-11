package com.camunda.training;


import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class ProcessJUnitTest {
	
	@Rule
	public ProcessEngineRule rule = new ProcessEngineRule();
  
	@Test
	@Deployment(resources = "twitter_qa.bpmn")
	public void testHappyPath() {
    
	    // Create a HashMap to put in variables for the process instance
	    Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("approved", true);
	    variables.put("content", "Hey ya, Norman hier");
	    // Start process with Java API and variables
	    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("TwitterQa", variables);
	    // Make assertions on the process instance
	    assertThat(processInstance).isEnded();
	}

}
