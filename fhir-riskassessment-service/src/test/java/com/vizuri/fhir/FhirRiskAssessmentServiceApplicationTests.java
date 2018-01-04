/*
 * Copyright 2015 Vizuri, a business division of AEM Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.vizuri.fhir;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.hl7.fhir.dstu3.model.RiskAssessment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import ca.uhn.fhir.context.FhirContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations="classpath:application-test.properties")
public class FhirRiskAssessmentServiceApplicationTests {
	
	private static Logger logger =LoggerFactory.getLogger(FhirRiskAssessmentServiceApplicationTests.class);
	
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void testRiskAssessmentService() {
		FhirContext ctx = FhirContext.forDstu3();
		
		InputStream is = getClass().getResourceAsStream("/examples-json/RiskAssessment-example.json");
		Reader reader = new InputStreamReader(is);
		
		RiskAssessment riskAssessment = (RiskAssessment)ctx.newJsonParser().parseResource(reader);
		String id = riskAssessment.getIdElement().getIdPart();
		
		logger.info(">>>>>>Sending Request:" + riskAssessment.getId());
		
		ResponseEntity<RiskAssessment>retMed = restTemplate.postForEntity("/riskAssessment",  riskAssessment, RiskAssessment.class);
		logger.info(">>>>Create RiskAssessment Response:" +retMed.getStatusCodeValue());
		logger.info(">>>>Response Message:" +retMed.getBody());
		assertTrue(retMed.getStatusCodeValue() == 200);
		
		List<RiskAssessment>retMeds=restTemplate.getForObject("/riskAssessment", List.class);
		
		logger.info("Got RiskAssessment" +retMeds.size());
		assertTrue(retMeds.size() == 1);
		
		RiskAssessment m3 = restTemplate.getForObject("/riskAssessment/" +id, RiskAssessment.class);
		logger.info("Got RiskAssessment" + m3.getId());
		assertTrue(id.equals(m3.getIdElement().getIdPart()));
		
		restTemplate.delete("/riskAssessment/"+id);
		
		List<RiskAssessment>retMeds2=restTemplate.getForObject("/riskAssessment", List.class);
		logger.info("Get Response Type:" +retMeds2.size());
		assertTrue(retMeds2.size()==0);
		
		
	}

}
