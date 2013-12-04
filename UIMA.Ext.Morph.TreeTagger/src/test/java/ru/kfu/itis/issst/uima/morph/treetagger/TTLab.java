/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.treetagger;

import static com.google.common.collect.Sets.newHashSet;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;
import static org.uimafit.factory.TypeSystemDescriptionFactory.createTypeSystemDescription;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.task.Task;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;

import ru.ksu.niimm.cll.uima.morph.lab.CorpusPartitioningTask;
import ru.ksu.niimm.cll.uima.morph.lab.CorpusPreprocessingTask;
import ru.ksu.niimm.cll.uima.morph.lab.LabLauncherBase;
import ru.ksu.niimm.cll.uima.morph.opencorpora.resource.CachedSerializedDictionaryResource;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class TTLab extends LabLauncherBase {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		System.setProperty("DKPRO_HOME", "wrk/tt-lab");
		TTLab lab = new TTLab();
		new JCommander(lab).parse(args);
		lab.run();
	}

	// the leading '_' is added to avoid confusion in Task classes
	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> _posCategoriesList;
	private Set<String> _posCategories;

	private TTLab(){
	}
	
	private void run() {
		_posCategories = newHashSet(_posCategoriesList);
		// prepare input TypeSystem
		final TypeSystemDescription inputTS = createTypeSystemDescription(
				"ru.kfu.itis.cll.uima.commons.Commons-TypeSystem",
				"ru.kfu.cll.uima.tokenizer.tokenizer-TypeSystem",
				"ru.kfu.cll.uima.segmentation.segmentation-TypeSystem",
				"org.opencorpora.morphology-ts");
		// prepare morph dictionary resource
		final ExternalResourceDescription morphDictDesc = createExternalResourceDescription(
				CachedSerializedDictionaryResource.class, "file:dict.opcorpora.ser");
		//
		UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, morphDictDesc);
		//
		Task prepareLexiconTask = new ExecutableTaskBase() {
			
			@Override
			public void execute(TaskContext aContext) throws Exception {
				// TODO Auto-generated method stub
				// XXX
			}
		};
		//
		Task corpusPartitioningTask = new CorpusPartitioningTask(foldsNum);
	}
}
