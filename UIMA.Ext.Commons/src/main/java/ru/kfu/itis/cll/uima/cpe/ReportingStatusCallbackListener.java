/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import java.util.List;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionProcessingEngine;
import org.apache.uima.collection.EntityProcessStatus;
import org.apache.uima.collection.StatusCallbackListener;

/**
 * Source code has been borrowed from UIMA examples - SimpleRunCPE.java
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ReportingStatusCallbackListener implements StatusCallbackListener {

	private CollectionProcessingEngine cpe;

	public ReportingStatusCallbackListener(CollectionProcessingEngine cpe) {
		this(cpe, 0);
	}

	public ReportingStatusCallbackListener(CollectionProcessingEngine cpe,
			int entityReportingInterval) {
		this.cpe = cpe;
		this.entityReportingInterval = entityReportingInterval;
	}

	/**
	 * Start time of CPE initialization
	 */
	private long mStartTime;

	/**
	 * Start time of the processing
	 */
	private long mInitCompleteTime;

	private int entityCount = 0;

	private int entityReportingInterval = 0;

	private long size = 0;

	/**
	 * Called when the initialization is completed.
	 * 
	 * @see org.apache.uima.collection.processing.StatusCallbackListener#initializationComplete()
	 */
	public void initializationComplete() {
		System.out.println("CPM Initialization Complete");
		mInitCompleteTime = System.currentTimeMillis();
	}

	/**
	 * Called when the batchProcessing is completed.
	 * 
	 * @see org.apache.uima.collection.processing.StatusCallbackListener#batchProcessComplete()
	 * 
	 */
	public void batchProcessComplete() {
		System.out.print("Completed " + entityCount + " documents");
		if (size > 0) {
			System.out.print("; " + size + " characters");
		}
		System.out.println();
		long elapsedTime = System.currentTimeMillis() - mStartTime;
		System.out.println("Time Elapsed : " + elapsedTime + " ms ");
	}

	/**
	 * Called when the collection processing is completed.
	 * 
	 * @see org.apache.uima.collection.processing.StatusCallbackListener#collectionProcessComplete()
	 */
	public void collectionProcessComplete() {
		long time = System.currentTimeMillis();
		System.out.print("Completed " + entityCount + " documents");
		if (size > 0) {
			System.out.print("; " + size + " characters");
		}
		System.out.println();
		long initTime = mInitCompleteTime - mStartTime;
		long processingTime = time - mInitCompleteTime;
		long elapsedTime = initTime + processingTime;
		System.out.println("Total Time Elapsed: " + elapsedTime + " ms ");
		System.out.println("Initialization Time: " + initTime + " ms");
		System.out.println("Processing Time: " + processingTime + " ms");

		System.out.println("\n\n ------------------ PERFORMANCE REPORT ------------------\n");
		System.out.println(cpe.getPerformanceReport().toString());
	}

	/**
	 * Called when the CPM is paused.
	 * 
	 * @see org.apache.uima.collection.processing.StatusCallbackListener#paused()
	 */
	public void paused() {
		System.out.println("Paused");
	}

	/**
	 * Called when the CPM is resumed after a pause.
	 * 
	 * @see org.apache.uima.collection.processing.StatusCallbackListener#resumed()
	 */
	public void resumed() {
		System.out.println("Resumed");
	}

	/**
	 * Called when the CPM is stopped abruptly due to errors.
	 * 
	 * @see org.apache.uima.collection.processing.StatusCallbackListener#aborted()
	 */
	public void aborted() {
		System.out.println("Aborted");
	}

	/**
	 * Called when the processing of a Document is completed. <br>
	 * The process status can be looked at and corresponding actions taken.
	 * 
	 * @param aCas
	 *            CAS corresponding to the completed processing
	 * @param aStatus
	 *            EntityProcessStatus that holds the status of all the events
	 *            for aEntity
	 */
	public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
		if (aStatus.isException()) {
			List<Exception> exceptions = aStatus.getExceptions();
			for (int i = 0; i < exceptions.size(); i++) {
				((Throwable) exceptions.get(i)).printStackTrace();
			}
			return;
		}
		entityCount++;
		String docText = aCas.getDocumentText();
		if (docText != null) {
			size += docText.length();
		}
		if (entityReportingInterval != 0 && entityCount % entityReportingInterval == 0) {
			System.out.println(String.format("% entities have been processed", entityCount));
		}
	}
}