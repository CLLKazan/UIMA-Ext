package ru.kfu.itis.cll.uima.consumer;

import com.google.common.base.Function;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.factory.TypeSystemDescriptionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.CasCreationUtils;
import org.junit.Before;
import org.junit.Test;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;
import ru.kfu.itis.cll.uima.util.DocumentUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author Rinat Gareev
 */
public class DefaultSourceURI2OutputFilePathFunctionTest {
    private Function<DocumentMetadata, Path> func = new DefaultSourceURI2OutputFilePathFunction();

    private DocumentMetadata meta;

    @Before
    public void init() throws ResourceInitializationException, CASException {
        TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription(
                DocumentUtils.TYPESYSTEM_COMMONS);
        CAS cas = CasCreationUtils.createCas(tsd, null, null);
        cas.setDocumentText("Some text");
        JCas jCas = cas.getJCas();
        meta = new DocumentMetadata(jCas, 0, 0);
        meta.addToIndexes();
    }

    @Test
    public void testWithScheme() {
        meta.setSourceUri("file:/some/folder/file.txt");
        Path result = func.apply(meta);
        assertEquals(Paths.get("/some/folder/file.txt.xmi"), result);
    }

    @Test
    public void testRelative() {
        meta.setSourceUri("someFolder/someFile");
        Path result = func.apply(meta);
        assertEquals(Paths.get("someFolder/someFile.xmi"), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpaqueURIs() {
        meta.setSourceUri("file:someFolder/someFile");
        func.apply(meta);
    }
}
