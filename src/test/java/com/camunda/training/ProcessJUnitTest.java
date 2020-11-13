package com.camunda.training;


import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class ProcessJUnitTest {
	
	@Rule
	@ClassRule
	public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().build();
	
  
	@Before
	public void setup() {
		init(rule.getProcessEngine());
		Mocks.register("createTweetDelegate", new CreateTweetDelegate());
	}
	
	@Test
	@Deployment(resources = "twitter_qa.bpmn")
	public void testHappyPath() {
    
	    // Create a HashMap to put in variables for the process instance
	    Map<String, Object> variables = new HashMap<String, Object>();
	    variables.put("content", "Norman is happy (path) : " + ThreadLocalRandom.current().nextInt());
	    // Start process with Java API and variables
	    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("TwitterQa", variables);
	    
	    assertThat(processInstance).isStarted();
	    
	    // Test The UserTask
	    assertThat(processInstance).isWaitingAt("CheckTweet_UserTask");
	    complete(task(), withVariables("approved", true));
	    
	    assertThat(processInstance).isWaitingAt("PostTweet_ServiceTask");
	    execute(job());
	    	    
	    // Make assertions on the process instance
	    assertThat(processInstance).isEnded();
	}
	
	@Test
	@Deployment(resources = "twitter_qa.bpmn")
	public void testRejectedPath() {
		
	    ProcessInstance processInstance = runtimeService()
	            .createProcessInstanceByKey("TwitterQa")
	            .setVariables(withVariables("approved", false, "content", "Alles doof"))
	            .startAfterActivity("CheckTweet_UserTask")
	            .execute();
	    
	    assertThat(processInstance).isStarted();
	    
	    assertThat(processInstance).isEnded().hasPassed("TweetRejected_EndEvent");
	}

}
