/*
 * Copyright © 2013-2018 camunda services GmbH and various authors (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.test.bpmn.usertask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.test.PluggableProcessEngineTestCase;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.joda.time.Period;

/**
 * @author Roman Smirnov
 *
 */
public class TaskFollowUpDateExtensionsTest extends PluggableProcessEngineTestCase {

  @Deployment(resources = {"org/camunda/bpm/engine/test/bpmn/usertask/TaskFollowUpDateExtensionsTest.testUserTaskFollowUpDate.bpmn20.xml"})
  public void testUserTaskFollowUpDateExtension() throws Exception {

    Date date = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").parse("01-01-2015 12:10:00");
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("dateVariable", date);

    // Start process-instance, passing date that should be used as followUpDate
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process", variables);

    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

    assertNotNull(task.getFollowUpDate());
    assertEquals(date, task.getFollowUpDate());
  }

  @Deployment(resources = {"org/camunda/bpm/engine/test/bpmn/usertask/TaskFollowUpDateExtensionsTest.testUserTaskFollowUpDate.bpmn20.xml"})
  public void testUserTaskFollowUpDateStringExtension() throws Exception {

    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("dateVariable", "2015-01-01T12:10:00");

    // Start process-instance, passing date that should be used as followUpDate
    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process", variables);

    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

    assertNotNull(task.getFollowUpDate());
    Date date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse("01-01-2015 12:10:00");
    assertEquals(date, task.getFollowUpDate());
  }

  @Deployment(resources = {"org/camunda/bpm/engine/test/bpmn/usertask/TaskFollowUpDateExtensionsTest.testUserTaskFollowUpDate.bpmn20.xml"})
  public void testUserTaskRelativeFollowUpDate() {
    Map<String, Object> variables = new HashMap<String, Object>();
    variables.put("dateVariable", "P2DT2H30M");

    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process", variables);

    Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();

    Date followUpDate = task.getFollowUpDate();
    assertNotNull(followUpDate);

    Period period = new Period(task.getCreateTime().getTime(), followUpDate.getTime());
    assertEquals(period.getDays(), 2);
    assertEquals(period.getHours(), 2);
    assertEquals(period.getMinutes(), 30);
  }

}
