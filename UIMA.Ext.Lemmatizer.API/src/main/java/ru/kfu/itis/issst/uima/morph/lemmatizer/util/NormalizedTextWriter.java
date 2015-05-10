package ru.kfu.itis.issst.uima.morph.lemmatizer.util;

import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import ru.kfu.cll.uima.tokenizer.fstype.BREAK;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.TokenBase;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.DocumentUtils;
import ru.kfu.itis.issst.uima.postagger.MorphCasUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Write a transformed text where
 * <ul>
 * <li> each word is replaced by its lemma </li>
 * <li> each token (word, punctuation or special symbol) is separated by a single space from its adjacent tokens</li>
 * <li> sequence of whitespaces (including tabs, excluding line endings) is merged into single space</li>
 * <li> sequence of whitespaces with a line ending is merged into single line ending</li>
 * </ul>
 *
 * @author Rinat Gareev
 */
public class NormalizedTextWriter extends JCasAnnotator_ImplBase {

    public static AnalysisEngineDescription createDescription(File outputDir) throws ResourceInitializationException {
        return AnalysisEngineFactory.createEngineDescription(NormalizedTextWriter.class,
                PARAM_OUTPUT_DIR, outputDir);
    }

    public static final String PARAM_OUTPUT_DIR = "outputDir";
    public static final String OUTPUT_FILENAME_SUFFIX = "-normalized";
    public static final String OUTPUT_FILENAME_EXTENSION = ".txt";

    @ConfigurationParameter(name = PARAM_OUTPUT_DIR)
    private File outputDir;

    @Override
    public void initialize(UimaContext ctx) throws ResourceInitializationException {
        super.initialize(ctx);
        try {
            FileUtils.forceMkdir(outputDir);
        } catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public void process(JCas cas) throws AnalysisEngineProcessException {
        // initialize
        String docFilename;
        try {
            docFilename = DocumentUtils.getDocumentFilename(cas.getCas());
        } catch (URISyntaxException e) {
            throw new AnalysisEngineProcessException(e);
        }
        if (docFilename == null) {
            throw new IllegalStateException("Can't extract a document filename from CAS");
        }
        String outFilename = FilenameUtils.getBaseName(docFilename)
                + OUTPUT_FILENAME_SUFFIX + OUTPUT_FILENAME_EXTENSION;
        File outFile = new File(outputDir, outFilename);
        Map<Token, Word> token2WordIndex = MorphCasUtils.getToken2WordIndex(cas);
        @SuppressWarnings("unchecked")
        FSIterator<TokenBase> tbIter = (FSIterator) cas.getAnnotationIndex(TokenBase.typeIndexID).iterator();
        try (PrintWriter out = IoUtils.openPrintWriter(outFile)) {
            Token lastProcessedTok = null;
            for (Token curTok : JCasUtil.select(cas, Token.class)) {
                // normalize space between
                out.print(normalizeSpaceBetween(tbIter, lastProcessedTok, curTok));
                // normalize current token
                String curTokNorm;
                Word w = token2WordIndex.get(curTok);
                if (w != null) {
                    curTokNorm = MorphCasUtils.getFirstLemma(w);
                } else {
                    curTokNorm = curTok.getCoveredText();
                }
                out.print(curTokNorm);
                //
                lastProcessedTok = curTok;
            }
            // handle a possible line ending after the last token
            out.print(normalizeSpaceBetween(tbIter, lastProcessedTok, null));
        } catch (IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    private String normalizeSpaceBetween(FSIterator<TokenBase> tbIter, final Token x, final Token y) {
        // X must be before Y
        Preconditions.checkArgument(x == null || y == null || x.getCAS().getAnnotationIndex().compare(x, y) < 0);
        if (x == null) {
            tbIter.moveToFirst();
        } else {
            tbIter.moveTo(x);
        }
        while (// if Y is null then iterate till the end
                (y == null && tbIter.isValid())
                        // else - iterate till the Y
                        || (y != null && !tbIter.get().equals(y))) {
            if (tbIter.get() instanceof BREAK) {
                return "\n";
            }
            tbIter.moveToNext();
        }
        return " ";
    }
}
