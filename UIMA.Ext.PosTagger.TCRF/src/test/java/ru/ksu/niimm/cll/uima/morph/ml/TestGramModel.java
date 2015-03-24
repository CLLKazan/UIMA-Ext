package ru.ksu.niimm.cll.uima.morph.ml;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.GramModelHolder;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.ImmutableGramModel;

import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

/**
 * @author Rinat Gareev
 */
public class TestGramModel implements GramModelHolder, SharedResourceObject {
    public static ExternalResourceDescription createDesc() {
        return createExternalResourceDescription(TestGramModel.class, "file:pom.xml");
    }

    private GramModel gramModel = ImmutableGramModel.builder()
            .addGrammeme("POST").addGrammeme("N", "POST").addGrammeme("V", "POST")
            .addGrammeme("ADJF", "POST").addGrammeme("PREP", "POST")
            .addGrammeme("NUMBER").addGrammeme("sing", "NUMBER").addGrammeme("plur", "NUMBER")
            .addGrammeme("CASE").addGrammeme("nomn", "CASE").addGrammeme("gent", "CASE").addGrammeme("loct", "CASE")
            .addGrammeme("accs", "CASE").addGrammeme("inst", "CASE")
            .addGrammeme("ANIM").addGrammeme("anim", "ANIM")
            .build();

    @Override
    public GramModel getGramModel() {
        return gramModel;
    }

    @Override
    public void load(DataResource aData) throws ResourceInitializationException {
    }
}
