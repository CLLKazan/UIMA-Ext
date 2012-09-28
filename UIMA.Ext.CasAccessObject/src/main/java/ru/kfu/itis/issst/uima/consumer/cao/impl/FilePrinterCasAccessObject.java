/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;

import ru.kfu.itis.issst.uima.consumer.cao.CasAccessObject;

/**
 * Simple implementation of {@link CasAccessObject} which prints all invocations
 * to specified file
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FilePrinterCasAccessObject implements CasAccessObject {

	private static final Logger log = Logger.getLogger(FilePrinterCasAccessObject.class
			.getSimpleName());

	private long idCounter = 0;
	private PrintWriter printer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void load(DataResource aData) throws ResourceInitializationException {
		log.info("FilePrinterCasAccessObject loading for config: " + aData.getUrl());
		Properties config = new Properties();
		InputStream configIS = null;
		try {
			configIS = aData.getInputStream();
			config.load(configIS);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		} finally {
			if (configIS != null) {
				try {
					configIS.close();
				} catch (IOException e) {
				}
			}
		}
		try {
			File outFile = new File(config.getProperty("output.path"));
			OutputStream os = new FileOutputStream(outFile);
			Writer writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
			printer = new PrintWriter(writer, true);
		} catch (Exception ex) {
			throw new ResourceInitializationException(ex);
		}
		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("FilePrinterCasAccessObject is going to shutdown.");
				printer.close();
			}
		}));
	}

	@Override
	public long persistLaunch(Date startedTime) {
		idCounter++;
		print("Launch: %s, %s", idCounter, startedTime);
		return idCounter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long persistAnnotation(String type, long spanId, String coveredText,
			int startOffset, int endOffset) {
		idCounter++;
		print("Annotation: %s, %s, %s, %s, %s, %s", idCounter, type, spanId, coveredText,
				startOffset, endOffset);
		return idCounter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void persistFeature(long ownerAnnoId, String featureName, long valueAnnoId) {
		print("Feature: %s, %s, %s", ownerAnnoId, featureName, valueAnnoId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long persistDocument(long launchId, String docURI, Long size, Long processingTime) {
		idCounter++;
		print("Document: %s, %s, %s, %s, %s", idCounter, launchId, docURI, size, processingTime);
		return idCounter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long persistSpan(long docId, String coveredText) {
		idCounter++;
		print("Span: %s, %s, %s", idCounter, coveredText, docId);
		return idCounter;
	}

	@Override
	public List<AnnotationDTO> getTopAnnotationsByLaunch(Set<Integer> pastLaunchIds,
			Set<String> topAnnoTypes) {
		throw new UnsupportedOperationException();
	}

	private void print(String str, Object... args) {
		printer.println(String.format(str, args));
	}
}