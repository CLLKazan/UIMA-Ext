/**
 * 
 */
package ru.kfu.itis.issst.uima.consumer.cao;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import ru.kfu.itis.issst.uima.consumer.cao.DeltaCalc.DeltaListener;
import ru.kfu.itis.issst.uima.consumer.cao.impl.AnnotationDTO;
import ru.kfu.itis.issst.uima.consumer.cao.impl.MysqlJdbcCasAccessObject;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class DeltaCalcLauncher {

	public static void main(String[] args) throws IOException {
		if (args.length != 5) {
			System.err.println("Usage:\n <cao-ds.properties> <annoType,annoType...> " +
					"<oldLaunchId,oldLaunchId,...> <newLaunchId,newLaunchId,...> <outputDir>");
			return;
		}
		File caoDsConfigFile = new File(args[0]);
		if (!caoDsConfigFile.isFile()) {
			System.err.println(caoDsConfigFile + " is not existing file");
			return;
		}
		String typesStr = args[1];
		String[] typesArr = typesStr.split(",");
		Set<String> types = new HashSet<String>(typesArr.length);
		for (String type : typesArr) {
			type = type.trim();
			if (!type.isEmpty()) {
				types.add(type);
			}
		}
		if (types.isEmpty()) {
			System.err.println("Empty 'types' argument");
			return;
		}

		Set<Integer> oldLaunchIds;
		try {
			oldLaunchIds = toIntegerSet(args[2]);
		} catch (Exception e) {
			System.err.println("old launch ids argument is incorrect: " + args[2]);
			e.printStackTrace();
			return;
		}

		Set<Integer> newLaunchIds;
		try {
			newLaunchIds = toIntegerSet(args[3]);
		} catch (Exception e) {
			System.err.println("new launch ids argument is incorrect: " + args[3]);
			e.printStackTrace();
			return;
		}

		File outputDir = new File(args[4]);
		if (outputDir.exists()) {
			if (outputDir.isFile()) {
				System.err.println(outputDir + " is not directory");
				return;
			}
		} else {
			FileUtils.forceMkdir(outputDir);
		}

		outputDir = new File(outputDir, genOutputName(types, oldLaunchIds, newLaunchIds));
		if (outputDir.exists()) {
			System.err.println("Output dir " + outputDir + " is already exist");
			return;
		}
		FileUtils.forceMkdir(outputDir);

		Properties caoDsConfig = readProperties(caoDsConfigFile);
		MysqlJdbcCasAccessObject cao = new MysqlJdbcCasAccessObject();
		cao.load(caoDsConfig);

		DeltaCalc calc = new DeltaCalc(cao);
		calc.setTopAnnoTypes(types);
		calc.setPastLaunchIds(oldLaunchIds);
		calc.setNewLaunchIds(newLaunchIds);

		FileWritingDeltaListener listener = null;
		try {
			listener = new FileWritingDeltaListener(outputDir);
			calc.addListener(listener);
			calc.run();
		} finally {
			if (listener != null) {
				listener.close();
			}
		}

		String report = String.format("New annotations: %s\nLost annotations: %s\n" +
				"Changed annotations: %s\nSavedAnnotations: %s",
				listener.newCounter, listener.lostCounter,
				listener.changedCounter, listener.savedCounter);
		FileUtils.writeStringToFile(new File(outputDir, "report.txt"), report, "utf-8");

		System.out.println(report);
		System.out.println("Done.");
	}

	private static String genOutputName(Set<String> types, Set<Integer> oldLaunchIds,
			Set<Integer> newLaunchIds) {
		StringBuilder sb = new StringBuilder();
		for (String type : types) {
			sb.append(type).append('-');
		}
		for (Integer launchId : oldLaunchIds) {
			sb.append(launchId).append('-');
		}
		sb.append("to");
		for (Integer launchId : newLaunchIds) {
			sb.append('-').append(launchId);
		}
		return sb.toString();
	}

	private static Set<Integer> toIntegerSet(String str) {
		String[] strArr = str.split(",");
		Set<Integer> result = new HashSet<Integer>();
		for (String curStr : strArr) {
			result.add(Integer.valueOf(curStr));
		}
		return result;
	}

	private static Properties readProperties(File file) {
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(file));
			try {
				props.load(is);
			} finally {
				is.close();
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return props;
	}

	private static PrintWriter makeWriter(File file) throws IOException {
		OutputStream os = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
		return new PrintWriter(bw, true);
	}

	private static class FileWritingDeltaListener implements DeltaListener {

		private PrintWriter newWriter;
		private PrintWriter lostWriter;
		private PrintWriter changedWriter;
		// state
		private int newCounter;
		private int lostCounter;
		private int changedCounter;
		private int savedCounter;

		public FileWritingDeltaListener(File outDir) throws IOException {
			File newAnnosFile = new File(outDir, "new.txt");
			File lostAnnosFile = new File(outDir, "lost.txt");
			File changedAnnosFile = new File(outDir, "changed.txt");

			newWriter = makeWriter(newAnnosFile);
			lostWriter = makeWriter(lostAnnosFile);
			changedWriter = makeWriter(changedAnnosFile);
		}

		public void close() {
			closeQuietly(newWriter);
			closeQuietly(lostWriter);
			closeQuietly(changedWriter);
		}

		@Override
		public void onNewAnnotation(AnnotationDTO anno) {
			print(newWriter, anno);
			newCounter++;
		}

		@Override
		public void onChangedAnnotation(AnnotationDTO oldAnno, AnnotationDTO newAnno) {
			PrintWriter writer = changedWriter;
			writer.print("Ids: ");
			writer.print(oldAnno.getId());
			writer.print(" ");
			writer.println(newAnno.getId());
			writer.println(newAnno.getType());
			writer.println(newAnno.getDocUri());
			writer.print("OLD: ");
			writer.println(oldAnno.getTxt());
			writer.print("NEW: ");
			writer.println(newAnno.getTxt());
			writer.println("===================================================");
			changedCounter++;
		}

		@Override
		public void onLostAnnotation(AnnotationDTO anno) {
			print(lostWriter, anno);
			lostCounter++;
		}

		@Override
		public void onSavedAnnotation(AnnotationDTO oldAnno, AnnotationDTO newAnno) {
			savedCounter++;
		}

		private void print(PrintWriter writer, AnnotationDTO anno) {
			writer.print(anno.getId());
			writer.print(":");
			writer.println(anno.getType());
			writer.println(anno.getDocUri());
			writer.println(anno.getTxt());
			writer.println("===================================================");
		}
	}
}