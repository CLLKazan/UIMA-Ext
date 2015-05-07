/**
 *
 */
package ru.kfu.itis.issst.uima.morph.commons;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.util.FSCollectionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.opencorpora.cas.Word;
import org.opencorpora.cas.Wordform;
import ru.kfu.cll.uima.tokenizer.fstype.NUM;
import ru.kfu.cll.uima.tokenizer.fstype.Token;
import ru.kfu.cll.uima.tokenizer.fstype.W;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.kfu.itis.cll.uima.cas.AnnotationUtils.toPrettyString;
import static ru.kfu.itis.cll.uima.util.DocumentUtils.getDocumentUri;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class TabSeparatedTaggedDataWriter extends TrainingDataWriterBase {

    public static AnalysisEngineDescription createDescription(File outputDir)
            throws ResourceInitializationException {
        return AnalysisEngineFactory.createEngineDescription(TabSeparatedTaggedDataWriter.class,
                PARAM_OUTPUT_DIR, outputDir.getPath());
    }

    @Override
    protected void processSentence(JCas jCas, List<Token> tokens)
            throws AnalysisEngineProcessException {
        for (Token tok : tokens) {
            Word word = getWordOfToken(tok);
            String tokStr = tok.getCoveredText();
            if (word == null) {
                if (tok instanceof NUM || tok instanceof W) {
                    getLogger().warn(String.format(
                            "Token %s in %s does not have corresponding Word annotation",
                            toPrettyString(tok), getDocumentUri(jCas)));
                    continue;
                }
                String tag = PunctuationUtils.getPunctuationTag(tokStr);
                writeTokenTag(tokStr, Arrays.asList(tag));
            } else {
                Collection<Wordform> wfs = FSCollectionFactory.create(
                        word.getWordforms(), Wordform.class);
                Set<String> tags = Sets.newLinkedHashSet();
                for (Wordform wf : wfs) {
                    tags.add(wf.getPos());
                }
                writeTokenTag(tokStr, tags);
            }
        }
        // write sentence end
        outputWriter.println();
    }

    private static final Joiner TAG_JOINER = Joiner.on('\t').useForNull("null");

    private void writeTokenTag(String token, Iterable<String> tags) {
        StringBuilder sb = new StringBuilder(token).append('\t');
        TAG_JOINER.appendTo(sb, tags);
        sb.append('\n');
        outputWriter.print(sb);
    }
}
