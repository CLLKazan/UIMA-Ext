/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.File;

import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

import com.beust.jcommander.Parameter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class LabLauncherBase {

	static {
		Slf4jLoggerImpl.forceUsingThisImplementation();
	}

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Parameter(names = "--corpus-split-dir", required = true, description = "The directory that contains descriptions of a corpus partitioning")
	protected File corpusSplitDir;
	@Parameter(names = { "-c", "--corpus" }, required = true, description = "Path to corpus directory")
	protected File srcCorpusDir;

	// prepare input TypeSystem
	protected TypeSystemDescription inputTS = createTypeSystemDescription(
			"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
			"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
			"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
			"org.opencorpora.morphology-ts");
	// prepare morph dictionary resource
	protected ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
			CachedSerializedDictionaryResource.class,
			LabConstants.URL_RELATIVE_MORPH_DICTIONARY);
}