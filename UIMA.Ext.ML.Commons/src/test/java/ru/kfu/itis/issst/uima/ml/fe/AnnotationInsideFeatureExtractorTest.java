package ru.kfu.itis.issst.uima.ml.fe;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.cleartk.ml.Feature;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.itis.cll.uima.io.axml.AXMLReader;
import ru.kfu.itis.issst.uima.ml.test.Product;
import ru.kfu.itis.issst.uima.tokenizer.TokenizerAPI;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static org.junit.Assert.assertEquals;
import static ru.kfu.itis.issst.cleartk.FeatureUtils.featureAsStringFunc;

/**
 * @author Rinat Gareev
 */
public class AnnotationInsideFeatureExtractorTest {

    private static final TypeSystemDescription inputTSD = TypeSystemDescriptionFactory.createTypeSystemDescription(
            TokenizerAPI.TYPESYSTEM_TOKENIZER, "test.test-fe-ts");
    private CAS cas;
    private AnnotationInsideFeatureExtractor<Token, Product> fe = new AnnotationInsideFeatureExtractor<>(
            Token.class, "ProdName", Product.class);

    @Before
    public void setup() throws ResourceInitializationException {
        cas = CasCreationUtils.createCas(inputTSD, null, null);
    }

    @Test
    public void test1() throws IOException, SAXException, CASException {
        AXMLReader.read(new File("test-data/test-fe-1.xml"), cas);
        final JCas cas = this.cas.getJCas();
        fe.onCASChange(cas);
        try {
            List<Token> toks = newArrayList(JCasUtil.select(cas, Token.class));
            List<String> actFeatValues = transform(toks, new Function<Token, String>() {
                @Override
                public String apply(Token input) {
                    List<Feature> tokFeats = fe.extract(cas, input);
                    String extResStr = Joiner.on('+').join(transform(tokFeats, featureAsStringFunc(':')));
                    return extResStr.isEmpty() ? null : extResStr;
                }
            });
            assertEquals(Lists.newArrayList(
                            null, null, null, null,
                            "I_ProdName",
                            null, null,
                            "I_ProdName", "I_ProdName",
                            null),
                    actFeatValues);
        } finally {
            fe.onCASChange(null);
        }
    }
}
