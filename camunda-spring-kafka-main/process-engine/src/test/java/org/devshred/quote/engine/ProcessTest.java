package org.devshred.quote.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.community.mockito.DelegateExpressions.registerJavaDelegateMock;
import static org.camunda.community.mockito.DelegateExpressions.verifyJavaDelegateMock;
import static org.devshred.quote.engine.ProcessConstants.MSG_NAME_QuoteResponse;
import static org.devshred.quote.engine.ProcessConstants.PROCESS_KEY_quote;
import static org.devshred.quote.engine.ProcessConstants.VAR_NAME_quote;

import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineExtension.class)
class ProcessTest {
    @Test
    @Deployment(resources = { "quote.bpmn" })
    public void shouldExecuteProcess() {
        registerJavaDelegateMock("requestQuoteAdapter");
        registerJavaDelegateMock("writeQuoteAdapter");

        // start process
        var correlationId = UUID.randomUUID().toString();
        var processInstance = runtimeService().createProcessInstanceByKey(PROCESS_KEY_quote).businessKey(correlationId) //
                .setVariables(Map.of( //
                        ProcessConstants.VAR_NAME_locale, "de_DE", //
                        ProcessConstants.VAR_NAME_quoteType, "CHUCK_NORRIS")) //
                .execute();

        assertThat(processInstance).isActive();
        assertThat(processInstanceQuery().count()).isEqualTo(1);

        // proceed with task
        final Map<String, Object> variables = Map.of(VAR_NAME_quote, "great");
        runtimeService().createMessageCorrelation(MSG_NAME_QuoteResponse) //
                .processInstanceBusinessKey(correlationId) //
                .setVariables(variables) //
                .correlateAllWithResult();

        assertThat(processInstance).isEnded();

        verifyJavaDelegateMock("requestQuoteAdapter").executed();
        verifyJavaDelegateMock("writeQuoteAdapter").executed();
    }
}