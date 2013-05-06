/**
 * 
 */
package ru.kfu.itis.issst.uima.brat;

import ru.kfu.itis.cll.uima.cpe.StatusCallbackListenerAdapter;

class TestStatusCallbackListener extends StatusCallbackListenerAdapter {

	boolean aborted = false;
	boolean completed = false;

	@Override
	public void collectionProcessComplete() {
		completed = true;
	}

	@Override
	public void aborted() {
		aborted = true;
	}
}