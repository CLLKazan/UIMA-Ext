/**
 *
 */
package ru.ksu.niimm.cll.uima.morph.ml;

import com.beust.jcommander.JCommander;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.tudarmstadt.ukp.dkpro.lab.Lab;
import de.tudarmstadt.ukp.dkpro.lab.engine.TaskContext;
import de.tudarmstadt.ukp.dkpro.lab.storage.StorageService.AccessMode;
import de.tudarmstadt.ukp.dkpro.lab.task.*;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.BatchTask.ExecutionPolicy;
import de.tudarmstadt.ukp.dkpro.lab.task.impl.ExecutableTaskBase;
import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.factory.ExternalResourceFactory;
import ru.kfu.itis.cll.uima.io.IoUtils;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.kfu.itis.issst.uima.morph.commons.GramModelBasedTagMapper;
import ru.kfu.itis.issst.uima.morph.commons.TagAssembler;
import ru.ksu.niimm.cll.uima.morph.lab.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.uimafit.factory.AnalysisEngineFactory.createAggregateDescription;
import static ru.ksu.niimm.cll.uima.morph.lab.LabConstants.*;
import static ru.ksu.niimm.cll.uima.morph.ml.TieredSequenceDataWriterResource.FILENAME_FEATURE_EXTRACTION_CONFIG;

/**
 * @author Rinat Gareev (Kazan Federal University)
 */
public class TieredPosTaggerLab2 extends LabLauncherBase {

    static final String DEFAULT_WRK_DIR = "wrk/tiered-pos-tagger2";

    public static void main(String[] args) throws IOException {
        System.setProperty("DKPRO_HOME", new File(DEFAULT_WRK_DIR).getAbsolutePath());
        TieredPosTaggerLab2 lab = new TieredPosTaggerLab2();
        new JCommander(lab).parse(args);
        lab.run();
    }

    private TieredPosTaggerLab2() {
    }

    private void run() throws IOException {
        // create task instances
        UimaTask preprocessingTask = new CorpusPreprocessingTask(inputTS, gramModelDesc);
        // -----------------------------------------------------------------
        UimaTask featureExtractionTask = new FeatureExtractionTaskBase("FeatureExtraction", inputTS) {
            @Discriminator
            List<String> gramTiers;
            @Discriminator
            int leftContextSize;
            @Discriminator
            int rightContextSize;

            @Override
            public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
                    throws ResourceInitializationException, IOException {
                File trainingBaseDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR,
                        AccessMode.READWRITE);
                // write a feature extraction config there
                File feCfgFile = new File(trainingBaseDir, FILENAME_FEATURE_EXTRACTION_CONFIG);
                Properties feCfg = new Properties();
                feCfg.setProperty(SimpleTieredFeatureExtractor.CFG_GRAM_TIERS,
                        Joiner.on(GramTiersFactory.tierSplitterChar).join(gramTiers));
                feCfg.setProperty(SimpleTieredFeatureExtractor.CFG_LEFT_CONTEXT_SIZE,
                        String.valueOf(leftContextSize));
                feCfg.setProperty(SimpleTieredFeatureExtractor.CFG_RIGHT_CONTEXT_SIZE,
                        String.valueOf(rightContextSize));
                IoUtils.writeProperties(feCfg, feCfgFile);
                // wrap it into another aggregate to avoid wrapping of delegates into separate
                // CPEIntegrateCasProcessors by org.uimafit.factory.CpeBuilder
                return createAggregateDescription(WriteFeatures2.createExtractorDescription(
                        gramTiers, morphDictDesc, trainingBaseDir
                ));
            }
        };
        // -----------------------------------------------------------------
        Task trainingTask = new ExecutableTaskBase() {
            {
                setType("Training");
            }

            @Discriminator
            int featureMinFreq;
            @Discriminator
            boolean featurePossibleStates;
            @Discriminator
            boolean featurePossibleTransitions;
            @Discriminator
            int c2;
            @Discriminator
            int optMaxIterations;

            @Override
            public void execute(TaskContext taskCtx) throws Exception {
                File trainingBaseDir = taskCtx.getStorageLocation(KEY_TRAINING_DIR,
                        AccessMode.READONLY);
                File modelBaseDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READWRITE);
                //
                // set training parameters
                List<String> trainerArgs = Lists.newArrayList();
                trainerArgs.add("-a");
                trainerArgs.add("lbfgs");
                addTrainParam(trainerArgs, "max_iterations", optMaxIterations);
                addTrainParam(trainerArgs, "feature.minfreq", featureMinFreq);
                if (featurePossibleStates) {
                    addTrainParam(trainerArgs, "feature.possible_states", 1);
                }
                if (featurePossibleTransitions) {
                    addTrainParam(trainerArgs, "feature.possible_transitions", 1);
                }
                addTrainParam(trainerArgs, "c2", c2);
                //
                TrainTCRF2.trainModels(trainingBaseDir, modelBaseDir,
                        trainerArgs.toArray(new String[trainerArgs.size()]));
            }
        };
        // -----------------------------------------------------------------
        UimaTask analysisTask = new AnalysisTask(gramModelDesc, inputTS, PartitionType.DEV);
        // -----------------------------------------------------------------
        Task evaluationTask = new EvaluationTask(PartitionType.DEV);
        // -----------------------------------------------------------------
        // configure data-flow between tasks
        featureExtractionTask.addImport(preprocessingTask, KEY_CORPUS);
        trainingTask.addImport(featureExtractionTask, KEY_TRAINING_DIR);
        analysisTask.addImport(preprocessingTask, KEY_CORPUS);
        analysisTask.addImport(trainingTask, KEY_MODEL_DIR);
        evaluationTask.addImport(preprocessingTask, KEY_CORPUS);
        evaluationTask.addImport(analysisTask, KEY_OUTPUT_DIR);
        // -----------------------------------------------------------------
        // create parameter space
        ParameterSpace pSpace = new ParameterSpace(
                getFileDimension(DISCRIMINATOR_SOURCE_CORPUS_DIR),
                getFileDimension(DISCRIMINATOR_CORPUS_SPLIT_INFO_DIR),
                // posCategories discriminator is used in the preprocessing task
                getStringSetDimension(DISCRIMINATOR_POS_CATEGORIES),
                getStringListDimension(DISCRIMINATOR_GRAM_TIERS),
                Dimension.create(DISCRIMINATOR_FOLD, 0),
                getIntDimension("featureMinFreq"),
                getIntDimension("c2"),
                getBoolDimension("featurePossibleTransitions"),
                getBoolDimension("featurePossibleStates"),
                getIntDimension("optMaxIterations"),
                getIntDimension("leftContextSize"),
                getIntDimension("rightContextSize"));
        pSpace.addConstraint(new Constraint() {
            @SuppressWarnings("unchecked")
            @Override
            public boolean isValid(Map<String, Object> cfg) {
                List<String> posTiers = (List<String>) cfg.get(DISCRIMINATOR_GRAM_TIERS);
                Set<String> expectedPosCats = getAllCategories(posTiers);
                Set<String> actualPosCats = (Set<String>) cfg.get(DISCRIMINATOR_POS_CATEGORIES);
                return expectedPosCats.equals(actualPosCats);
            }
        });
        // -----------------------------------------------------------------
        // create and run BatchTask
        BatchTask batchTask = new BatchTask();
        batchTask.addTask(preprocessingTask);
        batchTask.addTask(featureExtractionTask);
        batchTask.addTask(trainingTask);
        batchTask.addTask(analysisTask);
        batchTask.addTask(evaluationTask);
        //
        batchTask.setParameterSpace(pSpace);
        batchTask.setExecutionPolicy(ExecutionPolicy.USE_EXISTING);
        try {
            Lab.getInstance().run(batchTask);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void addTrainParam(List<String> params, String name, int value) {
        params.add("-p");
        params.add(name + "=" + value);
    }

    private static Splitter posCatSplitter = Splitter.onPattern("[,&]");

    static Set<String> getAllCategories(List<String> posTiers) {
        Set<String> posCategories = Sets.newLinkedHashSet();
        for (String pt : posTiers) {
            posCategories.addAll(Lists.newLinkedList(posCatSplitter.split(pt)));
        }
        return posCategories;
    }

    static class AnalysisTask extends AnalysisTaskBase {

        private ExternalResourceDescription gramModelDesc;

        AnalysisTask(ExternalResourceDescription gramModelDesc,
                     TypeSystemDescription inputTS,
                     PartitionType targetPartition) {
            super(PartitionType.DEV.equals(targetPartition) ? "Analysis" : "AnalysisFinal",
                    inputTS, targetPartition);
            this.gramModelDesc = gramModelDesc;
        }

        @Override
        public AnalysisEngineDescription getAnalysisEngineDescription(TaskContext taskCtx)
                throws ResourceInitializationException, IOException {
            File modelBaseDir = taskCtx.getStorageLocation(KEY_MODEL_DIR, AccessMode.READONLY);
            File outputDir = taskCtx.getStorageLocation(KEY_OUTPUT_DIR, AccessMode.READWRITE);
            //
            List<AnalysisEngineDescription> primitiveDescs = Lists.newArrayList();
            List<String> primitiveNames = Lists.newArrayList();
            //
            AnalysisEngineDescription goldRemoverDesc = createGoldRemoverDesc();
            primitiveDescs.add(goldRemoverDesc);
            primitiveNames.add("goldRemover");
            //
            AnalysisEngineDescription taggerDesc = SeqClassifierResourceBasedPosTagger.createDescription(
                    TieredSequenceClassifierResource.createDescription(modelBaseDir)
            );
            primitiveDescs.add(taggerDesc);
            primitiveNames.add("pos-tagger");
            //
            AnalysisEngineDescription tagAssembler = TagAssembler.createDescription();
            ExternalResourceFactory.bindExternalResource(tagAssembler,
                    GramModelBasedTagMapper.RESOURCE_GRAM_MODEL, gramModelDesc);
            primitiveDescs.add(tagAssembler);
            primitiveNames.add("tag-assembler");
            //
            AnalysisEngineDescription xmiWriterDesc = createXmiWriterDesc(outputDir);
            primitiveDescs.add(xmiWriterDesc);
            primitiveNames.add("xmiWriter");
            //
            AnalysisEngineDescription aggrDesc = createAggregateDescription(primitiveDescs,
                    primitiveNames,
                    null, null, null, null);
            // wrap it into another aggregate to avoid wrapping of delegates into separate
            // CPEIntegrateCasProcessors by org.uimafit.factory.CpeBuilder
            return createAggregateDescription(aggrDesc);
        }
    }

    private static final String DISCRIMINATOR_GRAM_TIERS = "gramTiers";
}
