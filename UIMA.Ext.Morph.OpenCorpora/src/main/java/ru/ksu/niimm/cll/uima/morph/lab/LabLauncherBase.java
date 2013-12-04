/**
 * 
 */
package ru.ksu.niimm.cll.uima.morph.lab;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kfu.itis.cll.uima.util.Slf4jLoggerImpl;

import com.beust.jcommander.Parameter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public abstract class LabLauncherBase {

	static {
		Slf4jLoggerImpl.forceUsingThisImplementation();
	}

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Parameter(names = { "-f", "--folds" }, required = true, description = "Number of cross-validation folds")
	protected int foldsNum;
	@Parameter(names = { "-c", "--corpus" }, required = true, description = "Path to corpus directory")
	protected File srcCorpusDir;

}