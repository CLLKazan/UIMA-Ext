/**
 * 
 */
package ru.kfu.itis.issst.uima.postagger.opennlp;

import opennlp.tools.util.model.BaseModel;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public interface OpenNLPModelHolder<MT extends BaseModel> {

	MT getModel();

}
