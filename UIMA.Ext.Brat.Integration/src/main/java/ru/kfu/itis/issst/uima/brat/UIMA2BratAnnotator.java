package ru.kfu.itis.issst.uima.brat;

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
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

/**
 * UIMA Annotator is CAS Annotator to convert UIMA annotations to brat standoff format annotations.
 * 1) defines input, ouput files directories
 * 2) reading annotator descriptor file and converts parameters to brat configuration file saved as annotation.conf
 * 3) saves annotations text file using specified file name parameter in DocumentMetadata annotations.
 * 4) reading UIMA annotations and converts them to brat annotation (*.ann files)
 
    T: text-bound annotation
    R: relation
    E: event
    A: attribute
    M: modification (alias for attribute, for backward compatibility)
    N: normalization 
    #: note
    
 * @author pathfinder
 */

public class UIMA2BratAnnotator extends CasAnnotator_ImplBase {

	// Input Parameters
	// Brat Annotation types
	// Brat Output directory

	public final static String BRAT_OUTPUTDIR = "data/bratOutPutDir";
	public final static String TYPES_TO_BRAT  = "TypesToBrat";
	public final static String ENCODING       = "UTF-8";	
	public final static String CONF_FILE      = "annotation.conf";
	public final static String FILE_SEPARATOR = "file.separator";
	public final static String DM_ENTITY_TYPE_FEATURE_BASE_NAME 
	                                          = "sourceUri";
	
	public static int tCounter = 0;

	// Brat types
	private HashMap<String, String> entities   = new HashMap<String, String>();
	private List<String> events                = new ArrayList<String>();
	private List<String> attributes            = new ArrayList<String>();
	private List<String> relations             = new ArrayList<String>();
	private static BufferedWriter writer;

	//private static BufferedWriter annWriter;


	@Override
	public void process(CAS casObj) throws AnalysisEngineProcessException {

		System.out.println("Saving text to file in brat output directory.");
		String txt = "";
		JCas jcas = null;
		try {
			jcas = casObj.getJCas();
			txt = jcas.getDocumentText();
		} catch (CASException e1) {
			e1.printStackTrace();
		}

		AnnotationIndex<AnnotationFS> 
		annotationIndex = null;
		
		FSIterator<AnnotationFS> iterator = null;

		URI           uri = null;
		String sourceUri  = null;
		Type dmEntityType = null;
        	 dmEntityType = (Type) jcas.getTypeSystem().getType(
		DocumentMetadata.class.getName());

		annotationIndex = (AnnotationIndex<AnnotationFS>) jcas.getCas().getAnnotationIndex();
		iterator = annotationIndex.iterator();

		Feature dmTypeFeature = dmEntityType.getFeatureByBaseName(DM_ENTITY_TYPE_FEATURE_BASE_NAME);
		File annFile = null;
		String   ann = null;
		
		while (iterator.isValid()) {
			AnnotationFS fs = iterator.get();
			if (fs != null) {
			    
				// System.out.println( fs.getType().getName() );
				// Identify Annotaion type
				
				System.out.println( 
						            fs+" : " + 
				                    entities.get( fs.getType().getName() )
				                   );
				
				 
				if( entities.get(fs.getType().getName())!=null ){
				
					// Add entity to Annotaion file ...
					
					 ann =  "T" + tCounter + "\t" + 
			                    entities.get( fs.getType().getName() ) + " " + fs.getBegin() 
			                    + " " + fs.getEnd() + "\t" + fs.getCoveredText(); 
			                   
					 System.out.println(ann);
					try {
						FileUtils.write(annFile,ann,ENCODING);
					} catch (IOException e) {
						e.printStackTrace();
					}
					 this.tCounter++;
				 }
				 
				// fs.get
				
				if( fs.getFeatureValueAsString(dmTypeFeature)!=null 
					&& !fs.getFeatureValueAsString(dmTypeFeature).equals("x-unspecified") ) {
				
				try {
					System.out.println("Opening the new file ... " + fs.getFeatureValueAsString(dmTypeFeature) + dmTypeFeature);
					uri = new URI(fs.getFeatureValueAsString(dmTypeFeature));
					sourceUri = uri.getPath();
					//String sannFile = new File(sourceUri).getName();
					annFile = new File(sourceUri+".ann");
					System.out.println("Writing brat annotations to file: "+sourceUri+".ann");
					
				} catch (CASRuntimeException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				
				}
				//sourceUri = uri.getPath();
				iterator.moveToNext();
			}
			
		}

		
		if (sourceUri != null) {
			System.out.println(sourceUri + " is text file name. Writing ...");
			File f = new File(BRAT_OUTPUTDIR, sourceUri);
			try {
				FileUtils.write(f, txt, ENCODING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			System.out.println("TEXT FILE NAME IS EMPTY");

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
		String[] typesToBrat = (String[]) ctx.getConfigParameterValue(TYPES_TO_BRAT);

		
		try {
			convertToBratTypes(typesToBrat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void convertToBratTypes(String[] typesToBrat) throws IOException {

		// TODO Auto-generated method stub
		// generate entities to brat conf file
		System.out.println("Converting types ...");

		File inputFile = new File(BRAT_OUTPUTDIR, CONF_FILE);
		if (!inputFile.isFile()) {
			System.err.println("Specified file does not exist ... creating the new one");

			File theDir = new File(BRAT_OUTPUTDIR);
			if (!theDir.exists())
				theDir.mkdir();

			boolean blnCreated = false;
			blnCreated = inputFile.createNewFile();

			System.out.println("Was file " + inputFile.getPath()
					            + " created ? : " + blnCreated);
			return;
		}
		//String encoding = "UTF-8";
		//File   bratConf = inputFile.getParentFile();
		BratTypes anTypes = null;
		String bratType = "none";
		// by default
		boolean fl;
		if (typesToBrat.length != 0) {

			for (String s : typesToBrat) {
				// System.out.println(s);
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
					case ENTITY:entities.put(s.split(";")[1],
								             s.split(";")[0]);
						break;
					default:
						break;
					}
			}

			// writing results to file annotaion conf file
			writeToFile("[entities]");

			
			for (String s : entities.keySet()) {
				writeToFile(entities.get(s));
			}
			
			writeToFile("[events]");
			for (String s : events) {
				writeToFile(s);
			}

			writeToFile("[attributes]");
			for (String s : attributes) {
				writeToFile(s);
			}

			writeToFile("[relations]");
			for (String s : relations) {
				writeToFile(s);
			}

			writer.close();
		}

	}

	public static void writeToFile(String text) {
		try {
			
			writer.write(text);
			writer.newLine();

		} catch (Exception e) {
		}
	}

	public enum BratTypes {
		ENTITY, EVENT, RELATION, ATTRIBUTE
	}
}