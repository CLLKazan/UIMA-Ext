/**
 *
 */
package ru.kfu.itis.issst.uima.demo;

import com.google.common.collect.Lists;
import org.apache.uima.UIMAFramework;
import org.apache.uima.aae.jms_adapter.JmsAnalysisEngineServiceAdapter;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.CustomResourceSpecifier;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.Resource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.impl.CustomResourceSpecifier_impl;
import org.apache.uima.resource.impl.Parameter_impl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opencorpora.cas.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.uima.aae.jms_adapter.JmsAnalysisEngineServiceStub.PARAM_BROKER_URL;
import static org.apache.uima.aae.jms_adapter.JmsAnalysisEngineServiceStub.PARAM_ENDPOINT;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class ITDemoPipelineAsyncService {

    private Logger log = LoggerFactory.getLogger(getClass());
    private AnalysisEngine ae;

    @Before
    public void init() throws ResourceInitializationException {
        CustomResourceSpecifier jmsAEServiceAdapterDesc = new CustomResourceSpecifier_impl();
        jmsAEServiceAdapterDesc.setResourceClassName(JmsAnalysisEngineServiceAdapter.class
                .getName());
        List<Parameter> params = Lists.newArrayList();
        params.add(new Parameter_impl(PARAM_BROKER_URL, "tcp://localhost:61616"));
        // a queue name defined in demo-pipeline-deployment.xml
        params.add(new Parameter_impl(PARAM_ENDPOINT, "top-lemmatizer-queue"));
        jmsAEServiceAdapterDesc.setParameters(params.toArray(new Parameter[params.size()]));
        Resource _ae = UIMAFramework.produceResource(jmsAEServiceAdapterDesc, null);
        this.ae = (AnalysisEngine) _ae;
    }

    @After
    public void closeAE() {
        ae.destroy();
        ae = null;
    }

    @Test
    public void shouldProcessTestText1() throws ResourceInitializationException, IOException,
            AnalysisEngineProcessException, CASException {
        CAS cas = ae.newCAS();
        cas.setDocumentText(readFileToString(new File("test-data/test1.txt"), "utf-8"));
        ae.process(cas);
        //
        printEntities(cas.getJCas());
    }

    private void printEntities(JCas jCas) {
        StringBuilder entTextBuffer = new StringBuilder();
        for (Word w : JCasUtil.select(jCas, Word.class)) {
            entTextBuffer.append(MorphCasUtils.getFirstLemma(w)).append(", ");
        }
        log.info("Lemma sequence: \n{}", entTextBuffer);
    }
}
