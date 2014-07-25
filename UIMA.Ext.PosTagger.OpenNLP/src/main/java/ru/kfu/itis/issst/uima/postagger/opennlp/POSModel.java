/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import opennlp.model.AbstractModel;
import opennlp.tools.util.BaseToolFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.model.BaseModel;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class POSModel extends BaseModel {

	private static final String COMPONENT_NAME = "UIMA.Ext.PoSTagger.ME";

	public static final String POS_MODEL_ENTRY_NAME = "pos.model";
	public static final String MORPH_DICT_ENTRY_NAME = "morph.dict";

	public POSModel(String languageCode, AbstractModel posModel,
			Map<String, String> manifestInfoEntries, POSTaggerFactory posFactory) {

		super(COMPONENT_NAME, languageCode, manifestInfoEntries, posFactory);

		if (posModel == null)
			throw new IllegalArgumentException("The maxentPosModel param must not be null!");

		artifactMap.put(POS_MODEL_ENTRY_NAME, posModel);
		checkArtifactMap();
	}

	public POSModel(InputStream in, MorphDictionary dict) throws IOException,
			InvalidFormatException {
		super(COMPONENT_NAME, in);
		if (dict != null) {
			artifactMap.put(MORPH_DICT_ENTRY_NAME, dict);
		}
	}

	@Override
	protected Class<? extends BaseToolFactory> getDefaultFactory() {
		return POSTaggerFactory.class;
	}

	@Override
	protected void validateArtifactMap() throws InvalidFormatException {
		super.validateArtifactMap();

		if (!(artifactMap.get(POS_MODEL_ENTRY_NAME) instanceof AbstractModel)) {
			throw new InvalidFormatException("POS model is incomplete!");
		}
	}

	public AbstractModel getPosModel() {
		return (AbstractModel) artifactMap.get(POS_MODEL_ENTRY_NAME);
	}

	public POSTaggerFactory getFactory() {
		return (POSTaggerFactory) this.toolFactory;
	}
}
