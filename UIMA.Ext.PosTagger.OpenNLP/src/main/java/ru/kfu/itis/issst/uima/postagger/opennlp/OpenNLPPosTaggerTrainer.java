/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import opennlp.model.AbstractModel;
import opennlp.model.EventStream;
import opennlp.model.TrainUtil;
import opennlp.tools.cmdline.CmdLineUtil;
import opennlp.tools.util.BeamSearchContextGenerator;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import ru.kfu.cll.uima.segmentation.fstype.Sentence;
import ru.kfu.cll.uima.tokenizer.fstype.Token;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class OpenNLPPosTaggerTrainer {

	// config fields
	private String languageCode;
	private File modelOutFile;
	private TrainingParameters trainParams;
	// derived
	private ObjectStream<Sentence> sentenceStream;
	private POSTaggerFactory taggerFactory;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public File getModelOutFile() {
		return modelOutFile;
	}

	public void setModelOutFile(File modelOutFile) {
		this.modelOutFile = modelOutFile;
	}

	public TrainingParameters getTrainingParameters() {
		return trainParams;
	}

	public void setTrainingParameters(TrainingParameters trainParams) {
		this.trainParams = trainParams;
	}

	public POSTaggerFactory getTaggerFactory() {
		return taggerFactory;
	}

	public void setTaggerFactory(POSTaggerFactory taggerFactory) {
		this.taggerFactory = taggerFactory;
	}

	public ObjectStream<Sentence> getSentenceStream() {
		return sentenceStream;
	}

	public void setSentenceStream(ObjectStream<Sentence> sentenceStream) {
		this.sentenceStream = sentenceStream;
	}

	public void train() throws IOException {
		if (languageCode == null) {
			throw new IllegalStateException("languageCode is not provided");
		}
		if (modelOutFile == null) {
			throw new IllegalStateException("model output path is not provided");
		}
		if (trainParams == null) {
			throw new IllegalStateException("training parameters are not set");
		}
		if (sentenceStream == null) {
			throw new IllegalStateException("sentence stream is not configured");
		}
		if (taggerFactory == null) {
			throw new IllegalStateException("tagger factory is not configured");
		}
		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		BeamSearchContextGenerator<Token> contextGenerator = taggerFactory.getContextGenerator();

		AbstractModel posModel;
		try {
			if (!TrainUtil.isSequenceTraining(trainParams.getSettings())) {

				EventStream es = new POSTokenEventStream<Sentence>(sentenceStream, contextGenerator);

				posModel = TrainUtil.train(es, trainParams.getSettings(), manifestInfoEntries);
			}
			else {
				throw new UnsupportedOperationException("Sequence training");
				//POSSampleSequenceStream ss = new POSSampleSequenceStream(samples, contextGenerator);
				// posModel = TrainUtil.train(ss, trainParams.getSettings(), manifestInfoEntries);
			}
		} finally {
			sentenceStream.close();
		}
		POSModel modelAggregate = new POSModel(languageCode,
				posModel, manifestInfoEntries, taggerFactory);
		CmdLineUtil.writeModel("PoS-tagger", modelOutFile, modelAggregate);
	}
}
