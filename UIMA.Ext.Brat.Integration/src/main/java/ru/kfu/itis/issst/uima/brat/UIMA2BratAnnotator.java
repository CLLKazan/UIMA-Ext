package ru.kfu.itis.issst.uima.brat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.FeatureStructureImpl;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

/**
 * UIMA Annotator is CAS Annotator to convert UIMA annotations to brat standoff
 * format annotations. 1) defines input, ouput files directories 2) reading
 * annotator descriptor file and converts parameters to brat configuration file
 * saved as annotation.conf 3) saves annotations text file using specified file
 * name parameter in DocumentMetadata annotations. 4) reading UIMA annotations
 * and converts them to brat annotation (*.ann files)
 * 
 * T: text-bound annotation R: relation E: event A: attribute M: modification
 * (alias for attribute, for backward compatibility) N: normalization #: note
 * 
 * For event annotation you have to add additional info about event entities
 * into desc file
 * 
 * @author pathfinder
 */

public class UIMA2BratAnnotator extends CasAnnotator_ImplBase {

	// Input Parameters
	// Brat Annotation types
	// Brat Output directory

	public final static String BRAT_OUTPUTDIR = "data/bratOutPutDir/";
	public final static String TYPES_TO_BRAT = "TypesToBrat";
	public final static String ENCODING = "UTF-8";
	public final static String CONF_FILE = "annotation.conf";
	public final static String FILE_SEPARATOR = "file.separator";
	public final static String DM_ENTITY_TYPE_FEATURE_BASE_NAME = "sourceUri";

	public static int tCounter = 0;
	public static int rCounter = 0;
	public static int eCounter = 0;

	// Brat types
	private HashMap<String, String> entities = new HashMap<String, String>();
	private HashMap<String, String> events = new HashMap<String, String>();
	private HashMap<String, String> relations = new HashMap<String, String>();

	// output
	//private HashMap<String, String> outs = new HashMap<String, String>();

	private static BufferedWriter writer;

	FSIterator<AnnotationFS> iterator = null;
	URI uri = null;
	String sourceUri = null;
	Type dmEntityType = null;
	String txt = "";
	JCas jcas = null;
	Feature dmTypeFeature = null;
	AnnotationIndex<AnnotationFS> annotationIndex = null;
	File annFile, af, f = null;
	String ann, fileName = null, t, r, ev = null;

	@Override
	public void process(CAS casObj) throws AnalysisEngineProcessException {

		System.out
				.println("Saving text and annotations into brat output directory's files.");
		AnnotationFS fs;
		// Initializing CAS Object and getting annotation iterator and source
		// URI annotation (Document Metadata)
		try {
			jcas = casObj.getJCas();
		} catch (CASException e1) {
			e1.printStackTrace();
		}
		txt = jcas.getDocumentText();
		dmEntityType = (Type) jcas.getTypeSystem().getType(
				DocumentMetadata.class.getName());
		annotationIndex = (AnnotationIndex<AnnotationFS>) jcas.getCas()
				.getAnnotationIndex();
		iterator = annotationIndex.iterator();
		dmTypeFeature = dmEntityType
				.getFeatureByBaseName(DM_ENTITY_TYPE_FEATURE_BASE_NAME);

		// Find sourceUri annotation first to write annotations somewhere!
		while (iterator.isValid()) {
			fs = iterator.get();
			if (fs != null) {
				// Writing annotations files init:
				try {
					if (fs.getFeatureValueAsString(dmTypeFeature) != null
							&& !fs.getFeatureValueAsString(dmTypeFeature)
									.equals("x-unspecified")) {
						try {
							uri = new URI(
									fs.getFeatureValueAsString(dmTypeFeature));
							sourceUri = uri.getPath();
							annFile = new File(sourceUri);
							fileName = annFile.getName();
							af = new File(BRAT_OUTPUTDIR + "ann/" + fileName
									+ ".ann");
							break;
						} catch (CASRuntimeException e) {
							e.printStackTrace();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				iterator.moveToNext();
			}
		}
		iterator = annotationIndex.iterator();
		// Iterate over the annotations
		while (iterator.isValid()) {
			fs = iterator.get();
			if (fs != null) {
				// Get entity annotations
				if (entities.get(fs.getType().getName()) != null) {
					t = getEntity(fs, af);
				}
				// Get relation annotations
				if (relations.get(fs.getType().getName()) != null) {

					r = getEvent(fs, af, iterator);
				}
				// Get event annotations
				if (events.get(fs.getType().getName()) != null) {
					ev = getEvent(fs, af, iterator);
				}
				iterator.moveToNext();
			}
		}
		// Writing annotations text
		if (sourceUri != null) {
			System.out.println(sourceUri + " is text file name. Writing ...");
			f = new File(BRAT_OUTPUTDIR + "txt/", fileName + ".txt");
			try {
				FileUtils.write(f, txt, ENCODING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			System.out.println("TEXT FILE NAME IS EMPTY");

		// finalize per one annotation file
		tCounter = 0;
		rCounter = 0;
		eCounter = 0;
		af = null;
		f = null;

	}

	private String getEvent(AnnotationFS fs, File af,
			FSIterator<AnnotationFS> iterator) {

		String ann, t2 = null, t = null, out = "E";
		boolean fl = false;

		// Set event text-bound annotation.
		if (events.get(fs.getType().getName()) == null) {
			if (relations.get(fs.getType().getName()) != null) {
				events.put(fs.getType().getName(),
						relations.get(fs.getType().getName()));
				out = "R";
				fl = true;
			}
		}
		if (!fl) {
			entities.put(fs.getType().getName(),
					events.get(fs.getType().getName()).split(":")[0]);

			t2 = getEntity(fs, af);

			entities.remove(fs.getType().getName());

		}
		if (!fl)
			ann = "E" + eCounter + "\t"
					+ events.get(fs.getType().getName()).split(":")[0];
		else
			ann = "R" + rCounter + "\t"
					+ events.get(fs.getType().getName()).split(":")[0];

		//System.out.println("STRING OUTPUT:" + fs.toString());

		Type tp = fs.getType();
		HashMap<String, String> args = new HashMap<String, String>();
		String[] afs = events.get(fs.getType().getName()).split(":");

		int k = 1;
		for (Feature f : tp.getFeatures()) {
			// Recognize a valid features and add them to brat annotation
			// structure
			try {
				if (afs.length > 0) {
					for (String ftype : afs) {
						if (entities.containsValue(ftype)
								&& !f.getRange().isPrimitive()
								&& fs.getFeatureValue(f)!=null
								&& entities.containsKey(fs.getFeatureValue(f)
										.getType().getName())
								&& ftype.equals(entities
										.get(fs.getFeatureValue(f).getType()
												.getName()))) {
							// System.out.println(fs.getFeatureValue(f));
							t = getEntity((AnnotationFS) fs.getFeatureValue(f),	af);
							args.put(ftype.substring(0, 3) + k, t);
							k++;
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (fl) {
			if (relations.get(fs.getType().getName()) != null) {
				events.remove(fs.getType().getName());
				fl = false;
			}
		}
		//System.out.println("RG" + args.size() + args.toString());
		for (String s : args.keySet()) {
			ann += " " + s + ":" + args.get(s);
		}
		ann += "\n";
		if (!fl) {
			out += eCounter;
			this.eCounter++;
		} else {
			out += rCounter;
			this.rCounter++;
		}
		if (ann.length() != 0) {
			try {
				if(!checkUnique(af, ann))
				 FileUtils.writeStringToFile(af, ann, true);
				//outs.put(out, ann);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out;
	}

	private String getEntity(AnnotationFS fs, File af) {
		String ann, out = "T";
		// Add entity to annotation file ...
		ann = "T" + tCounter + "\t" + entities.get(fs.getType().getName())
				+ " " + fs.getBegin() + " " + fs.getEnd() + "\t"
				+ fs.getCoveredText();
		//System.out.println(ann);
		ann += "\n";
		out += tCounter;
		tCounter++;
		if (ann.length() != 0) {
			try {
				if(!checkUnique(af,ann))
				FileUtils.writeStringToFile(af, ann, true);			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return out;
	}

	private boolean checkUnique(File af2, String ann2) throws IOException {
		if(af.canRead())
		for(String s:FileUtils.readLines(af2)){
			//System.out.println(s+"check"+ann2.split("\n")[0]);
			if(s.replace(s.split("\t")[0]+"\t", "").equals(ann2.replace(ann2.split("\t")[0]+"\t","").split("\n")[0]))
			return true;
		}
		return false;
	}

	@Override
	public void typeSystemInit(TypeSystem ts)
			throws AnalysisEngineProcessException {
		super.typeSystemInit(ts);
	}

	@Override
	public void initialize(UimaContext ctx)
			throws ResourceInitializationException {
		super.initialize(ctx);

		System.out.println("Annotator initialization ... ");
		try {
			writer = new BufferedWriter(new FileWriter(BRAT_OUTPUTDIR
					+ System.getProperty(FILE_SEPARATOR) + CONF_FILE));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// How to define Unique types to brat parameter name for nameValuePair
		System.out.println("Reading types to BRAT parameters ... ");
		String[] typesToBrat = (String[]) ctx
				.getConfigParameterValue(TYPES_TO_BRAT);
		try {
			convertToBratTypes(typesToBrat);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void convertToBratTypes(String[] typesToBrat) throws IOException {

		// Generate entities to brat configuration file
		System.out.println("Converting types ...");

		File inputFile = new File(BRAT_OUTPUTDIR, CONF_FILE);
		if (!inputFile.isFile()) {
			System.err
					.println("Specified file does not exist ... creating the new one");

			File theDir = new File(BRAT_OUTPUTDIR);
			if (!theDir.exists())
				theDir.mkdir();

			boolean blnCreated = false;
			blnCreated = inputFile.createNewFile();

			System.out.println("Was file " + inputFile.getPath()
					+ " created ? : " + blnCreated);
			return;
		}

		BratTypes anTypes = null;
		String bratType = "none";

		boolean fl;
		if (typesToBrat.length != 0) {

			for (String s : typesToBrat) {
				fl = false;
				if (s.split(";").length > 2)
					bratType = s.split(";")[2];
				else
					System.out.println("There is no type for this object!");
				try {
					anTypes = BratTypes.valueOf(bratType.toUpperCase());
				} catch (Exception e) {
					fl = true;
					continue;
				}
				if (!fl)
					switch (anTypes) {

					case ENTITY:
						entities.put(s.split(";")[1], s.split(";")[0]);
						break;

					case RELATION:
						relations.put(s.split(";")[1], s.split(";")[0]);
						break;

					case EVENT:
						events.put(s.split(";")[1], s.split(";")[0]);
						break;

					default:
						break;
					}
			}

			// Writing results to file annotation configuration file
			writeToFile("[entities]");

			System.out.println(entities);
			for (String s : entities.keySet()) {
				writeToFile(entities.get(s));
			}

			writeToFile("[events]");

			System.out.println(events);
			for (String s : events.keySet()) {
				if (events.get(s).split(":").length > 0)
					writeToFile(events.get(s).split(":")[0]);
				else
					writeToFile(s);
			}

			writeToFile("[attributes]");

			// for (String s : attributes) {
			// writeToFile(s);
			// }

			writeToFile("[relations]");

			System.out.println(relations);
			for (String s : relations.keySet()) {
				writeToFile(relations.get(s).split(":")[0]);
			}
			writer.close();
		}
	}

	public static void writeToFile(String text) {
		try {
			writer.write(text);
			writer.newLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum BratTypes {
		ENTITY, EVENT, RELATION, ATTRIBUTE
	}
}