/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class StatusCallbackListenerAdapter implements StatusCallbackListener {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializationComplete() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void batchProcessComplete() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void collectionProcessComplete() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paused() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resumed() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void aborted() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
	}

}