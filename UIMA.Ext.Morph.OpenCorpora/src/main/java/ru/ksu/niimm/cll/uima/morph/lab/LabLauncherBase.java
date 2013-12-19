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

	@Parameter(names = "--corpus-split-dir", required = true, description = "The directory that contains descriptions of a corpus partitioning")
	protected File corpusSplitDir;
	@Parameter(names = { "-c", "--corpus" }, required = true, description = "Path to corpus directory")
	protected File srcCorpusDir;

}