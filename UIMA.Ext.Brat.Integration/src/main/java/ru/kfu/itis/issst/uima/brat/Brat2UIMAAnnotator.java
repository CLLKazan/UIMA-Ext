package ru.kfu.itis.issst.uima.brat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import ru.kfu.itis.cll.uima.commons.DocumentMetadata;
import ru.kfu.itis.cll.uima.consumer.XmiWriter;

/**
 * Brat 2 UIMA Annotator is CAS Annotator to convert Brat standoff format
 * annotations to UIMA annotations. 1) defines input, ouput files directories of
 * .txt and .ann files 2) reading files and process its content using specified
 * file name parameter in DocumentMetadata annotations. 4) reading Brat
 * annotations and converts them to UIMA annotation (*.xmi files) T: text-bound
 * annotation R: relation E: event A: attribute M: modification (alias for
 * attribute, for backward compatibility) N: normalization #: note
 * @author pathfinder
 */

public class Brat2UIMAAnnotator extends CasAnnotator_ImplBase {

	// Input Parameters
	// Brat Annotation types
	// Brat Output directory

	public final static String BRAT_OUT = "BratOutputDir";
	public final static String TYPES_TO_BRAT = "TypesToBrat";
	public final static String CONF_FILE = "annotation.conf";
	public final static String DEFAULT_BRAT_OUTPUTDIR = "data/brat/out/";
	public static String BRAT_OUTPUTDIR;
	public final static String ENCODING = "UTF-8";
	public final static String FILE_SEPARATOR = "file.separator";
	public final static String TXT_FILE_FORMAT = ".txt";
	public final static String ANN_FILE_FORMAT = ".ann";
	public final static String XMI_FILE_FORMAT = ".xmi";
	public final static String RELATION_ARG_NAME = "Arg";
	public final static String EVENT_ARG_NAME = "EArg";
	public final static String DM_ENTITY_TYPE_FEATURE_BASE_NAME = "sourceUri";

	// public static int tCounter = 0;
	// public static int rCounter = 0;
	// public static int eCounter = 0;

	// Brat types
	private HashMap<String, String> entities = new HashMap<String, String>();
	private HashMap<String, String> events = new HashMap<String, String>();
	private HashMap<String, String> eventsArgs = new HashMap<String, String>();
	private HashMap<String, String> relations = new HashMap<String, String>();
	private HashMap<String, String> output = new HashMap<String, String>();
	// output
	// private HashMap<String, String> outs = new HashMap<String, String>();

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

	// Logger
	private static Logger LOGGER;

	public final static String TEXTBOUND_ANN = "T";
	public final static String RELATION_ANN = "R";
	public final static String EVENT_ANN = "E";

	@Override
	public void process(CAS casObj) throws AnalysisEngineProcessException {

		System.out.println("Processing ...");
		// LOGGER.info("Saving text and annotations files into brat output directory.");
		AnnotationFS fs;

		// Initializing JCAS Object and getting annotation iterator and source
		// URI annotation (Document Metadata)
		try {
			jcas = casObj.getJCas();
		} catch (CASException e1) {
			e1.printStackTrace();
		}
		txt = jcas.getDocumentText();
		System.out.println(txt);
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
			System.out.println(fs + "FT" + dmTypeFeature);
			if (fs != null) {
				// Writing annotations files init:

				try {
					if (fs.getType()
							.getName()
							.equals("ru.kfu.itis.cll.uima.commons.DocumentMetadata")
							&& fs.getFeatureValueAsString(dmTypeFeature) != null
							&& !fs.getFeatureValueAsString(dmTypeFeature)
									.equals("x-unspecified")) {
						try {
							System.out.println("Item Processing");

							uri = new URI(fs.getFeatureValueAsString(dmTypeFeature));
							sourceUri = uri.getPath();
							annFile = new File(sourceUri);
							fileName = annFile.getName();
							af = new File(BRAT_OUTPUTDIR + fileName
									+ ANN_FILE_FORMAT);
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

		// Let's see what type of file do we have here.
		// jcas.reset();

		if (sourceUri.split("\\.")[sourceUri.split("\\.").length - 1]
				.equals("ann")) {
			// System.out.println(sourceUri + ":IT IS ANN FILE => skip it!");
		} else if (sourceUri.split("\\.")[sourceUri.split("\\.").length - 1]
				.equals("txt")) {
			// System.out.println(sourceUri.replaceAll("txt$","ann") +
			// ":IT IS TXT FILE");

			File txtFile = new File(sourceUri.replaceAll("txt$", "ann"));

			try {

				txt = FileUtils.readFileToString(txtFile);

			} catch (CASRuntimeException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			String[] lines = txt.split(System.getProperty("line.separator"));

			for (String s : lines) {
				// RECOGNIZE THE TYPE OF ANNOTATION
				// Replace numbers
				String symb = s.split("\t")[0].replaceAll("[0-9]*$", "");
				switch (symb) {
				case TEXTBOUND_ANN:
					// Set entity annotation
					try {
						makeEntityAnnotation(s);
					} catch (ClassNotFoundException | InstantiationException
							| IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException
							| SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case RELATION_ANN:
					// Set relation annotation
					try {
						makeRelationAnnotation(s);
					} catch (ClassNotFoundException | NoSuchMethodException
							| SecurityException | InstantiationException
							| IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					break;
				case EVENT_ANN:
					// Set event annotation
					makeEventAnnotation(s);
					break;
				}
			}

			iterator = annotationIndex.iterator();
			// Iterate over the annotations
			// while (iterator.isValid()) {
			// fs = iterator.get();
			// if (fs != null) {
			//
			// iterator.moveToNext();
			// }
			// }
			// Writing annotations text
			if (sourceUri != null) {
				LOGGER.log(Level.FINEST, fileName + " is in process.");
				f = new File(BRAT_OUTPUTDIR, fileName + XMI_FILE_FORMAT);
				FileOutputStream out = null;
				try {
					// write XMI
					out = new FileOutputStream(f.getAbsolutePath());
					XmiCasSerializer ser = new XmiCasSerializer(
							jcas.getTypeSystem());
					XMLSerializer xmlSer = new XMLSerializer(out, true);
					ser.serialize(jcas.getCas(), xmlSer.getContentHandler());
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} else
				LOGGER.log(Level.WARNING, "TEXT FILE NAME IS EMPTY");
			// }
		}
		//
		// // finalize per one annotation file
		// tCounter = 1;
		// rCounter = 1;
		// eCounter = 1;
		// af = null;
		// f = null;

	}

	private void makeEventAnnotation(String s) {
		
		
		
		// TODO Auto-generated method stub

	}

	private void makeRelationAnnotation(String s) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		
		System.out.println("parsing string" + s);

		if (s.split("\t").length > 0 
				&& 
			s.split("\t")[s.split("\t").length - 1].split(" ").length > 0) 
		{
			System.out.println("Making relation annotation ..."
					+ relations.get(s.split("\t")[1].split(" ")[0]));
			System.out.println("Making relation annotation ..."
					+ relations.keySet());
			
			if (entities.get(s.split("\t")[1].split(" ")[0]) != null) {
				Class<?> c = Class.forName(relations.get(s.split("\t")[1].split(" ")[0]));
				Constructor<?> cons = c.getConstructor(JCas.class);
				Object object = cons.newInstance(jcas);
				System.out.println(object);
				java.lang.reflect.Method beginMethod, endMethod, addToIndexes;
				try {
					System.out.println("parsing ..."
							+ s.split("\t")[1].split(" ")[1]);
					beginMethod = object.getClass().getMethod("setBegin",
							int.class);
					beginMethod.invoke(object,
							Integer.parseInt(s.split("\t")[1].split(" ")[1]));

					System.out.println("parsing ..."
							+ s.split("\t")[1].split(" ")[2]);
					endMethod = object.getClass()
							.getMethod("setEnd", int.class);
					endMethod.invoke(object,
							Integer.parseInt(s.split("\t")[1].split(" ")[2]));

					addToIndexes = object.getClass().getMethod("addToIndexes");
					addToIndexes.invoke(object);
					System.out.println("result");

				} catch (SecurityException e) {
					// ...
				} catch (NoSuchMethodException e) {
					// ...
				}
			}
		}
	}

	private void makeEntityAnnotation(String s) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		System.out.println("parsing string" + s);

		if (s.split("\t").length > 0
				&& s.split("\t")[s.split("\t").length - 1].split(" ").length > 0) {
			System.out.println("Making entity annotation ..."
					+ entities.get(s.split("\t")[1].split(" ")[0]));
			if (entities.get(s.split("\t")[1].split(" ")[0]) != null) {
				Class<?> c = Class.forName(entities.get(s.split("\t")[1]
						.split(" ")[0]));
				Constructor<?> cons = c.getConstructor(JCas.class);
				Object object = cons.newInstance(jcas);
				System.out.println(object);
				java.lang.reflect.Method beginMethod, endMethod, addToIndexes;
				try {
					System.out.println("parsing ..."
							+ s.split("\t")[1].split(" ")[1]);
					beginMethod = object.getClass().getMethod("setBegin",
							int.class);
					beginMethod.invoke(object,
							Integer.parseInt(s.split("\t")[1].split(" ")[1]));

					System.out.println("parsing ..."
							+ s.split("\t")[1].split(" ")[2]);
					endMethod = object.getClass()
							.getMethod("setEnd", int.class);
					endMethod.invoke(object,
							Integer.parseInt(s.split("\t")[1].split(" ")[2]));

					addToIndexes = object.getClass().getMethod("addToIndexes");
					addToIndexes.invoke(object);
					System.out.println("result");

				} catch (SecurityException e) {
					// ...
				} catch (NoSuchMethodException e) {
					// ...
				}
			}

		}

		// s.split("\t")[1]s.split(" ")[0]

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

		System.out.println("Started Brat2UIMA Annotator Processor ");

		LOGGER = getContext().getLogger();

		LOGGER.log(Level.INFO, "Annotator is initializing ...");
		String bratDirectoryPath = (String) ctx
				.getConfigParameterValue(BRAT_OUT);
		if (bratDirectoryPath == null) {
			BRAT_OUTPUTDIR = DEFAULT_BRAT_OUTPUTDIR;
			throw new IllegalStateException(BRAT_OUT + " param is NULL");
		} else
			BRAT_OUTPUTDIR = bratDirectoryPath;

		try {
			writer = new BufferedWriter(new FileWriter(BRAT_OUTPUTDIR
					+ System.getProperty(FILE_SEPARATOR) + CONF_FILE));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// How to define Unique types to brat parameter name for nameValuePair
		LOGGER.log(Level.INFO,
				"Reading UIMA types to convert to brat annotations ... ");

		String[] typesToBrat = (String[]) ctx
				.getConfigParameterValue(TYPES_TO_BRAT);

		try {
			convertToBratTypes(typesToBrat);
			// readingConfiguration();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UIMAIllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	private void convertToBratTypes(String[] typesToBrat) throws IOException,
			UIMAIllegalArgumentException {

		// Generate entities to brat configuration file
		LOGGER.log(Level.INFO, "Creating brat types config file.");

		File inputFile = new File(BRAT_OUTPUTDIR, CONF_FILE);
		if (!inputFile.isFile()) {
			LOGGER.log(Level.SEVERE,
					"Specified file does not exist. creating the new one");

			File theDir = new File(BRAT_OUTPUTDIR);
			if (!theDir.exists())
				theDir.mkdir();

			boolean blnCreated = false;
			blnCreated = inputFile.createNewFile();

			LOGGER.log(Level.INFO, "Was file " + inputFile.getPath()
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
					LOGGER.log(Level.WARNING, "There is no type for " + s
							+ "this object!");
				try {
					anTypes = BratTypes.valueOf(bratType.toUpperCase());
				} catch (Exception e) {
					fl = true;
					continue;
				}
				if (!fl)
					switch (anTypes) {

					case ENTITY:
						entities.put(s.split(";")[0], s.split(";")[1]);
						break;

					case RELATION:
						relations.put(s.split(";")[0].split(":")[0],
								s.split(";")[1]);
						break;

					case EVENT:
						events.put(s.split(";")[0].split(":")[0],
								s.split(";")[1]);
						break;

					default:
						break;
					}
			}

			// Writing results to file annotation configuration file
			writeToFile("[entities]");

			// System.out.println(entities);
			for (String s : entities.keySet()) {
				writeToFile(entities.get(s));
			}

			writeToFile("[events]");

			// System.out.println(events);
			for (String s : events.keySet()) {
				if (events.get(s).split(":").length > 0)
					writeToFile(events.get(s));
				else
					writeToFile(s);
			}

			writeToFile("[attributes]");

			writeToFile("[relations]");

			// System.out.println(relations);
			for (String s : relations.keySet()) {
				writeToFile(relations.get(s));
			}
			writer.close();
		}
	}

	private String parseEventArgs(String args)
			throws UIMAIllegalArgumentException {
		String out = null;

		String argss[] = args.split(":");

		if (args.length() > 0) {
			out = "";
			out += argss[0] + "\t ";
			int ik = 0;
			String val, param, arg;
			for (String s : argss) {
				if (ik != 0) {

					val = s.replaceAll("[\\?\\*\\+\\{\\d\\}]", "");

					arg = s.replaceAll("[^A-z]", "") + "-" + EVENT_ARG_NAME;

					param = s.replaceAll("[\\|\\-\\_A-z]", "");

					out += arg + param + ":" + val;

					eventsArgs.put(val, arg);

					if (ik != argss.length - 1 && ik != 0)
						out += ", ";
				}
				ik++;
			}
		} else {
			out = "";
			if (args.equals("") | args == null)
				throw new UIMAIllegalArgumentException(
						"Event configuration error!");
			else
				out += args;
		}
		System.out.println("Process" + eventsArgs);
		return out;
	}

	//
	private String parseRelationArgs(String args)
			throws UIMAIllegalArgumentException {

		String out = null;
		String argss[] = args.split(":");
		// Relation has to have exactly two arguments
		if (argss.length == 3) {
			out = "";
			out += argss[0] + "\t ";
			// Add arguments and replace unnecessary characters
			out += RELATION_ARG_NAME + "1:"
					+ argss[1].replaceAll("[\\?\\*\\+\\{\\d\\}]", "");
			eventsArgs.put(argss[1].replaceAll("[\\?\\*\\+\\{\\d\\}]", ""),
					RELATION_ARG_NAME);

			out += ", ";
			out += RELATION_ARG_NAME + "2:"
					+ argss[2].replaceAll("[\\?\\*\\+\\{\\d\\}]", "");
			eventsArgs.put(argss[2].replaceAll("[\\?\\*\\+\\{\\d\\}]", ""),
					RELATION_ARG_NAME);

		} else
			throw new UIMAIllegalArgumentException(
					"Relations must have exactly two arguments");
		return out;
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