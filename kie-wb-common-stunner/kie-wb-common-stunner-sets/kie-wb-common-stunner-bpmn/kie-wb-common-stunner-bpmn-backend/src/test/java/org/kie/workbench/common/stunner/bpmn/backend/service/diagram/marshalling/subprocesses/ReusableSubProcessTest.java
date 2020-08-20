/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling.subprocesses;

import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeValue;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;

import static org.assertj.core.api.Assertions.assertThat;

public class ReusableSubProcessTest extends SubProcessTest<ReusableSubprocess> {

    private static final String BPMN_SUB_PROCESS_FILE_PATH = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/reusableSubProcesses.bpmn";

    private static final String TOP_LEVEL_EMPTY_SUBPROCESS_ID = "_83336319-6615-4FCC-B2EF-3376CB552CA9";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID = "_B26D5827-370D-40F4-A92E-0D7F36E56BFD";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID = "_0E89076D-A208-44DC-A7EF-9BF03B0A2886";
    private static final String TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID = "_56591DB6-79A6-4D9E-94DE-B52EC3825F9A";

    private static final String SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID = "_0C801E16-60D9-4F68-BE7F-1C296DEC9B3A";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID = "_335A3BD6-BFC0-4D77-A9CC-BA2F15CE4D53";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID = "_9AF1C895-A124-4646-9061-FA8802DFDE97";
    private static final String SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID = "_B4DED88D-9214-4690-8199-063EB688D7B6";

    private static final String TOP_LEVEL_SUBPROCESS_WITH_EDGES = "_BEBE4D01-648F-4DC5-8ED5-72D2F61F63D3";
    private static final String SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES = "_362B7975-3E50-4B53-B351-C9D8C62B0C96";

    private static final int AMOUNT_OF_NODES_IN_DIAGRAM = 18;

    private static final String DEFAULT_CALLED_ELEMENT = "";
    private static final String DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT = "||||";

    private static final boolean IS_INDEPENDENT = true;
    private static final boolean WAIT_FOR_COMPLETION = true;
    private static final boolean IS_NOT_INDEPENDENT = false;
    private static final boolean DO_NOT_WAIT_FOR_COMPLETION = false;

    private static final String TASK_SCRIPT_JAVA_LANGUAGE = "java";
    private static final String TASK_ON_ENTRY_ACTION_JAVA = "System.out.println(\"On Entry Action\");\n" +
            "System.out.println(\"`&(^*&^(\\n\\r\");\n" +
            "Object o = kcontext.getVariable(\"hello_world\");";
    private static final String TASK_ON_EXIT_ACTION_JAVA = "System.out.println(\"On Exit Action\");\n" +
            "System.out.println(\"`&(^*&^(\\n\\r\");\n" +
            "Object o = kcontext.getVariable(\"hello_world\");";

    private static final String TASK_SCRIPT_JAVASCRIPT_LANGUAGE = "javascript";
    private static final String TASK_ON_ENTRY_ACTION_JAVASCRIPT = "console.log(\"On Entry Action\");\n" +
            "print(\"~``!@#$%^&*()_+=-{}|\\n\\r][:\\\",.?\");\n" +
            "print(\"somevalue\" + \"~``!@#$%^&*()_+=-{}|\\n\\r][:\\\",.?\");";
    private static final String TASK_ON_EXIT_ACTION_JAVASCRIPT = "console.log(\"On Exit Action\");\n" +
            "print(\"~``!@#$%^&*()_+=-{}|\\n\\r][:\\\",.?\");\n" +
            "print(\"somevalue\" + \"~``!@#$%^&*()_+=-{}|\\n\\r][:\\\",.?\");";

    private static final String TASK_SCRIPT_MVEL_LANGUAGE = "mvel";
    private static final String TASK_ON_ENTRY_ACTION_MVEL = "System.out.println(\"On Entry Action\");\n" +
            "System.out.println(\"`&(^*&^(\\n\\r\");\n" +
            "Object o = kcontext.getVariable(\"hello_world\");";
    private static final String TASK_ON_EXIT_ACTION_MVEL = "System.out.println(\"On Exit Action\");\n" +
            "System.out.println(\"`&(^*&^(\\n\\r\");\n" +
            "Object o = kcontext.getVariable(\"hello_world\");";

    private static final String SLA_DUE_DATE = "12/25/1983";

    public ReusableSubProcessTest() throws Exception {
    }

    @Test
    @Override
    public void testUnmarshallTopLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess topLevelSubProcess = getSubProcessNodeById(diagram,
                                                                      TOP_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                      EMPTY_INCOME_EDGES,
                                                                      EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(topLevelSubProcess.getExecutionSet(),
                                             DEFAULT_CALLED_ELEMENT,
                                             IS_NOT_INDEPENDENT,
                                             IS_NOT_ASYNC,
                                             DO_NOT_WAIT_FOR_COMPLETION,
                                             EMPTY_VALUE,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE);
        assertDataIOSet(topLevelSubProcess.getDataIOSet(), DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME = "Reusable process01 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION = "Reusable process01 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CALLED_ELEMENT = "testProject.linkedReusableSubProcess";
        final String SUB_PROCESS_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess topLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                          TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                                                                          EMPTY_INCOME_EDGES,
                                                                          EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(topLevelSubProcessJava.getExecutionSet(),
                                             CALLED_ELEMENT,
                                             IS_INDEPENDENT,
                                             IS_ASYNC,
                                             WAIT_FOR_COMPLETION,
                                             TASK_ON_ENTRY_ACTION_JAVA,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             TASK_ON_EXIT_ACTION_JAVA,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             SLA_DUE_DATE);
        assertDataIOSet(topLevelSubProcessJava.getDataIOSet(), SUB_PROCESS_DATA_INPUT_OUTPUT);

        ReusableSubprocess topLevelSubProcessJavascript = getSubProcessNodeById(diagram,
                                                                                TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                                                                                EMPTY_INCOME_EDGES,
                                                                                EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJavascript.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(topLevelSubProcessJavascript.getExecutionSet(),
                                             CALLED_ELEMENT,
                                             IS_INDEPENDENT,
                                             IS_ASYNC,
                                             WAIT_FOR_COMPLETION,
                                             TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                             TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                             TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                             TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                             SLA_DUE_DATE);
        assertDataIOSet(topLevelSubProcessJavascript.getDataIOSet(), SUB_PROCESS_DATA_INPUT_OUTPUT);

        ReusableSubprocess topLevelSubProcessMVEL = getSubProcessNodeById(diagram,
                                                                          TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID,
                                                                          EMPTY_INCOME_EDGES,
                                                                          EMPTY_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessMVEL.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(topLevelSubProcessMVEL.getExecutionSet(),
                                             CALLED_ELEMENT,
                                             IS_INDEPENDENT,
                                             IS_ASYNC,
                                             WAIT_FOR_COMPLETION,
                                             TASK_ON_ENTRY_ACTION_MVEL,
                                             TASK_SCRIPT_MVEL_LANGUAGE,
                                             TASK_ON_EXIT_ACTION_MVEL,
                                             TASK_SCRIPT_MVEL_LANGUAGE,
                                             SLA_DUE_DATE);
        assertDataIOSet(topLevelSubProcessMVEL.getDataIOSet(), SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallTopLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Sub-process";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess topLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                          TOP_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                          ONE_INCOME_EDGE,
                                                                          TWO_OUTCOME_EDGES);

        assertGeneralSet(topLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(topLevelSubProcessJava.getExecutionSet(),
                                             DEFAULT_CALLED_ELEMENT,
                                             IS_NOT_INDEPENDENT,
                                             IS_NOT_ASYNC,
                                             WAIT_FOR_COMPLETION,
                                             TASK_ON_ENTRY_ACTION_JAVA,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             TASK_ON_EXIT_ACTION_JAVA,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE);
        assertDataIOSet(topLevelSubProcessJava.getDataIOSet(), DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelEmptyPropertiesSubProcess() {
        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess subProcessLevelSubProcess = getSubProcessNodeById(diagram,
                                                                             SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID,
                                                                             EMPTY_INCOME_EDGES,
                                                                             EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcess.getGeneral(), DEFAULT_NAME, DEFAULT_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(subProcessLevelSubProcess.getExecutionSet(),
                                             DEFAULT_CALLED_ELEMENT,
                                             IS_NOT_INDEPENDENT,
                                             IS_NOT_ASYNC,
                                             DO_NOT_WAIT_FOR_COMPLETION,
                                             EMPTY_VALUE,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE);
        assertDataIOSet(subProcessLevelSubProcess.getDataIOSet(), DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelFilledPropertiesSubProcess() {
        final String SUB_PROCESS_NAME = "Reusable process03 name ~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./";
        final String SUB_PROCESS_DOCUMENTATION = "Reusable process03 doc\n ~!@#$%^&*()_+`1234567890-={}|[]\\:\";'<>?,./";
        final String CALLED_ELEMENT = "testProject.linkedReusableSubProcess";
        final String SUB_PROCESS_DATA_INPUT_OUTPUT = "|input:String||output:String|[din]processGlobalVar->input,[dout]output->processGlobalVar";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess subProcessLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                                 SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                                                                                 EMPTY_INCOME_EDGES,
                                                                                 EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(subProcessLevelSubProcessJava.getExecutionSet(),
                                             CALLED_ELEMENT,
                                             IS_INDEPENDENT,
                                             IS_ASYNC,
                                             WAIT_FOR_COMPLETION,
                                             TASK_ON_ENTRY_ACTION_JAVA,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             TASK_ON_EXIT_ACTION_JAVA,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             SLA_DUE_DATE);
        assertDataIOSet(subProcessLevelSubProcessJava.getDataIOSet(), SUB_PROCESS_DATA_INPUT_OUTPUT);

        ReusableSubprocess subProcessLevelSubProcessJavascript = getSubProcessNodeById(diagram,
                                                                                       SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                                                                                       EMPTY_INCOME_EDGES,
                                                                                       EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJavascript.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(subProcessLevelSubProcessJavascript.getExecutionSet(),
                                             CALLED_ELEMENT,
                                             IS_INDEPENDENT,
                                             IS_ASYNC,
                                             WAIT_FOR_COMPLETION,
                                             TASK_ON_ENTRY_ACTION_JAVASCRIPT,
                                             TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                             TASK_ON_EXIT_ACTION_JAVASCRIPT,
                                             TASK_SCRIPT_JAVASCRIPT_LANGUAGE,
                                             SLA_DUE_DATE);
        assertDataIOSet(subProcessLevelSubProcessJavascript.getDataIOSet(), SUB_PROCESS_DATA_INPUT_OUTPUT);

        ReusableSubprocess subProcessLevelSubProcessMVEL = getSubProcessNodeById(diagram,
                                                                                 SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID,
                                                                                 EMPTY_INCOME_EDGES,
                                                                                 EMPTY_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessMVEL.getGeneral(), SUB_PROCESS_NAME, SUB_PROCESS_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(subProcessLevelSubProcessMVEL.getExecutionSet(),
                                             CALLED_ELEMENT,
                                             IS_INDEPENDENT,
                                             IS_ASYNC,
                                             WAIT_FOR_COMPLETION,
                                             TASK_ON_ENTRY_ACTION_MVEL,
                                             TASK_SCRIPT_MVEL_LANGUAGE,
                                             TASK_ON_EXIT_ACTION_MVEL,
                                             TASK_SCRIPT_MVEL_LANGUAGE,
                                             SLA_DUE_DATE);
        assertDataIOSet(subProcessLevelSubProcessMVEL.getDataIOSet(), SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testUnmarshallSubProcessLevelSubProcessWithEdges() {
        final String SUB_PROCESS_NAME = "Sub-process";

        Diagram<Graph, Metadata> diagram = getDiagram();
        assertDiagram(diagram, AMOUNT_OF_NODES_IN_DIAGRAM);

        ReusableSubprocess subProcessLevelSubProcessJava = getSubProcessNodeById(diagram,
                                                                                 SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES,
                                                                                 ONE_INCOME_EDGE,
                                                                                 TWO_OUTCOME_EDGES);

        assertGeneralSet(subProcessLevelSubProcessJava.getGeneral(), SUB_PROCESS_NAME, DEFAULT_DOCUMENTATION);
        assertReusableSubProcessExecutionSet(subProcessLevelSubProcessJava.getExecutionSet(),
                                             DEFAULT_CALLED_ELEMENT,
                                             IS_NOT_INDEPENDENT,
                                             IS_NOT_ASYNC,
                                             WAIT_FOR_COMPLETION,
                                             TASK_ON_ENTRY_ACTION_JAVA,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             TASK_ON_EXIT_ACTION_JAVA,
                                             TASK_SCRIPT_JAVA_LANGUAGE,
                                             EMPTY_VALUE);
        assertDataIOSet(subProcessLevelSubProcessJava.getDataIOSet(), DEFAULT_SUB_PROCESS_DATA_INPUT_OUTPUT);
    }

    @Test
    @Override
    public void testMarshallTopLevelSubProcessWithEdges() {
        checkSubProcessMarshalling(getTopLevelSubProcessWithEdgesId(), ONE_INCOME_EDGE, TWO_OUTCOME_EDGES);
    }

    @Test
    @Override
    public void testMarshallSubProcessLevelSubProcessWithEdges() {
        checkSubProcessMarshalling(getSubProcessLevelSubProcessWithEdgesId(), ONE_INCOME_EDGE, TWO_OUTCOME_EDGES);
    }

    @Override
    Class<ReusableSubprocess> getSubProcessType() {
        return ReusableSubprocess.class;
    }

    @Override
    String getBpmnSubProcessFilePath() {
        return BPMN_SUB_PROCESS_FILE_PATH;
    }

    @Override
    String getTopLevelEmptyPropertiesSubProcessId() {
        return TOP_LEVEL_EMPTY_SUBPROCESS_ID;
    }

    @Override
    String[] getTopLevelFilledPropertiesSubProcessesIds() {
        return new String[]{TOP_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                TOP_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                TOP_LEVEL_FILLED_SUBPROCESS_MVEL_ID};
    }

    @Override
    String getTopLevelSubProcessWithEdgesId() {
        return TOP_LEVEL_SUBPROCESS_WITH_EDGES;
    }

    @Override
    String getSubProcessLevelEmptyPropertiesSubProcessId() {
        return SUBPROCESS_LEVEL_EMPTY_SUBPROCESS_ID;
    }

    @Override
    String[] getSubProcessLevelFilledPropertiesSubProcessesIds() {
        return new String[]{SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVA_ID,
                SUBPROCESS_LEVEL_FILLED_SUBPROCESS_JAVASCRIPT_ID,
                SUBPROCESS_LEVEL_FILLED_SUBPROCESS_MVEL_ID};
    }

    @Override
    String getSubProcessLevelSubProcessWithEdgesId() {
        return SUBPROCESS_LEVEL_SUBPROCESS_WITH_EDGES;
    }

    private void assertReusableSubProcessExecutionSet(BaseReusableSubprocessTaskExecutionSet executionSet,
                                                      String calledElement,
                                                      boolean independent,
                                                      boolean isAsync,
                                                      boolean waitForCompletion,
                                                      String onEntryActionScriptValue,
                                                      String onEntryActionScriptLanguage,
                                                      String onExitActionScriptValue,
                                                      String onExitActionScriptLanguage,
                                                      String slaDueDate) {
        assertThat(executionSet).isNotNull();
        assertThat(executionSet.getCalledElement()).isNotNull();
        assertThat(executionSet.getIndependent()).isNotNull();
        assertThat(executionSet.getIsAsync()).isNotNull();
        assertThat(executionSet.getWaitForCompletion()).isNotNull();
        assertThat(executionSet.getSlaDueDate()).isNotNull();

        assertThat(executionSet.getOnEntryAction()).isNotNull();
        assertThat(executionSet.getOnExitAction()).isNotNull();
        assertThat(executionSet.getOnEntryAction().getValue()).isNotNull();
        assertThat(executionSet.getOnExitAction().getValue()).isNotNull();

        List<ScriptTypeValue> onEntryScriptTypeValues = executionSet.getOnEntryAction().getValue().getValues();
        List<ScriptTypeValue> onExitScriptTypeValues = executionSet.getOnExitAction().getValue().getValues();

        assertThat(onEntryScriptTypeValues).isNotNull();
        assertThat(onExitScriptTypeValues).isNotNull();
        assertThat(onEntryScriptTypeValues.get(0)).isNotNull();
        assertThat(onExitScriptTypeValues.get(0)).isNotNull();

        assertThat(executionSet.getCalledElement().getValue()).isEqualTo(calledElement);
        assertThat(executionSet.getIndependent().getValue()).isEqualTo(independent);
        assertThat(executionSet.getIsAsync().getValue()).isEqualTo(isAsync);
        assertThat(executionSet.getWaitForCompletion().getValue()).isEqualTo(waitForCompletion);
        assertThat(executionSet.getSlaDueDate().getValue()).contains(slaDueDate);

        assertThat(onEntryScriptTypeValues.get(0).getScript()).isEqualTo(onEntryActionScriptValue);
        assertThat(onEntryScriptTypeValues.get(0).getLanguage()).isEqualTo(onEntryActionScriptLanguage);
        assertThat(onExitScriptTypeValues.get(0).getScript()).isEqualTo(onExitActionScriptValue);
        assertThat(onExitScriptTypeValues.get(0).getLanguage()).isEqualTo(onExitActionScriptLanguage);
    }

    private void assertDataIOSet(DataIOSet dataIOSet, String value) {
        assertThat(dataIOSet).isNotNull();
        assertThat(dataIOSet.getAssignmentsinfo()).isNotNull();
        assertThat(dataIOSet.getAssignmentsinfo().getValue()).isEqualTo(value);
    }
}
