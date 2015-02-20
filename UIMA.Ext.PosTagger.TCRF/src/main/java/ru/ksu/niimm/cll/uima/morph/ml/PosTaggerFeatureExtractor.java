package ru.ksu.niimm.cll.uima.morph.ml;

import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.descriptor.ExternalResource;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionary;
import ru.kfu.itis.issst.uima.morph.dictionary.resource.MorphDictionaryHolder;

/**
 * @author Rinat Gareev
 */
class PosTaggerFeatureExtractor implements IncrementalFeatureExtractor{
    // constants
    public static final String RESOURCE_MORPH_DICTIONARY = "morphDictionary";
    public static final String PARAM_LEFT_CONTEXT_SIZE = "leftContextSize";
    public static final String PARAM_RIGHT_CONTEXT_SIZE = "rightContextSize";

    // aggregate
    @ExternalResource(key = RESOURCE_MORPH_DICTIONARY, mandatory = true)
    private MorphDictionaryHolder morphDictHolder;
    @ConfigurationParameter(name = PARAM_LEFT_CONTEXT_SIZE, defaultValue = "2")
    private Integer leftContextSize;
    @ConfigurationParameter(name = PARAM_RIGHT_CONTEXT_SIZE, defaultValue = "2")
    private Integer rightContextSize;

    private FeatureExtractionPlan fePlan;
    private MorphDictionary morphDictionary;

    PosTaggerFeatureExtractor() {

    }
}
