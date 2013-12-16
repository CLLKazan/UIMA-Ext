/**
 * 
 */
package ru.kfu.itis.issst.uima.morph.hunpos;

import static com.google.common.collect.Lists.newArrayList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.State;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.annolab.tt4j.ExecutableResolver;
import org.annolab.tt4j.PlatformDetector;
import org.annolab.tt4j.TokenAdapter;
import org.annolab.tt4j.TokenHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.kfu.itis.cll.uima.io.StreamGobblerBase;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * 
 * @param <TT>
 *            token type
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class HunposWrapper<TT> {

	private final Logger log = LoggerFactory.getLogger(getClass());

	// config fields
	private String modelName;
	private TokenAdapter<TT> tokenAdapter;
	private TokenHandler<TT> tokenHandler;
	private PlatformDetector platformDetector;
	private ExecutableResolver exeResolver;
	private String processIOEncoding = "utf-8";
	// state fields
	private File modelFile;
	private String processCmd;
	private Process process;

	{
		// default configuration
		platformDetector = new PlatformDetector();
		exeResolver = new DefaultHunposExecutableResolver();
		exeResolver.setPlatformDetector(platformDetector);
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		if (process != null) {
			throw new IllegalStateException();
		}
		if (!Objects.equal(this.modelName, modelName)) {
			modelFile = null;
		}
		this.modelName = modelName;
	}

	public void setTokenAdapter(TokenAdapter<TT> tokenAdapter) {
		this.tokenAdapter = tokenAdapter;
	}

	public void setTokenHandler(TokenHandler<TT> tokenHandler) {
		this.tokenHandler = tokenHandler;
	}

	public void process(Collection<TT> tokens) throws IOException, HunposException {
		if (tokenAdapter == null) {
			throw new IllegalStateException("tokenAdapter is not set");
		}
		if (tokenHandler == null) {
			throw new IllegalStateException("tokenHandler is not set");
		}
		Process process = getTaggerProcess();
		//
		ResponseReader reader = new ResponseReader(tokens.iterator());
		Thread readerThread = new Thread(reader);
		readerThread.setName("Hunpos-tagger Output Reader");
		readerThread.start();
		// 
		StreamGobblerBase errorStreamGobbler = StreamGobblerBase.toSystemOut(
				process.getErrorStream());
		Thread esgThread = new Thread(errorStreamGobbler);
		esgThread.setName("Hunpos-tagger Error Stream Gobbler");
		esgThread.start();
		//
		TokenWriter writer = new TokenWriter(tokens.iterator());
		Thread writerThread = new Thread(writer);
		writerThread.setName("Hunpos-tagger Input Writer");
		writerThread.start();
		//
		try {
			synchronized (reader) {
				while (State.TERMINATED != readerThread.getState()) {
					checkThreads(writer, reader, errorStreamGobbler);

					try {
						reader.wait(30);
					} catch (InterruptedException e) {
						//
					}
				}
			}
		} finally {
			errorStreamGobbler.done();
		}
	}

	public void destroy() {
		log.info("Cleaning up hunpos-tagger process");
		stopTaggerProcess();
		setModelName(null);
		if (exeResolver != null) {
			exeResolver.destroy();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		destroy();
		super.finalize();
	}

	private void checkThreads(
			TokenWriter writer, ResponseReader reader, StreamGobblerBase errGobbler)
			throws HunposException {
		if (writer.getException() != null) {
			destroy();
			throw new HunposException(writer.getException());
		}
		if (reader.getException() != null) {
			destroy();
			throw new HunposException(reader.getException());
		}
		if (errGobbler.getException() != null) {
			destroy();
			throw new HunposException(errGobbler.getException());
		}
	}

	private class ResponseReader implements Runnable {
		private Iterator<TT> tokenIter;
		// state
		private BufferedReader inReader;
		private Throwable exception;

		private ResponseReader(Iterator<TT> tokenIter) throws UnsupportedEncodingException {
			this.tokenIter = tokenIter;
			inReader = new BufferedReader(new InputStreamReader(
					process.getInputStream(), processIOEncoding));
		}

		public Throwable getException() {
			return exception;
		}

		@Override
		public void run() {
			try {
				while (true) {
					String line = inReader.readLine();
					if (line == null) {
						throw new IllegalStateException("Unexpected death of hunpos-tagger process");
					}
					if (line.isEmpty()) {
						// means we are done with current sentence
						break;
					}
					List<String> outFields = newArrayList(taggerOutputFieldsSplitter.split(line));
					if (outFields.size() != 3) {
						throw new IllegalStateException(String.format(
								"Illegal output format in line:\n%s", line));
					}
					// String respTokenStr = outFields.get(0);
					String tag = outFields.get(1);
					if (tokenIter.hasNext()) {
						TT token = tokenIter.next();
						tokenHandler.token(token, tag, null);
					} else {
						throw new IllegalStateException();
					}
				}
				if (tokenIter.hasNext()) {
					throw new IllegalStateException();
				}
				synchronized (this) {
					notifyAll();
				}
			} catch (IOException e) {
				exception = e;
			}
		}
	}

	private static Splitter taggerOutputFieldsSplitter = Splitter.on('\t');

	private class TokenWriter implements Runnable {
		private Iterator<TT> tokenIter;
		// state
		private PrintWriter outWriter;
		private Throwable exception;

		private TokenWriter(Iterator<TT> tokenIter) throws UnsupportedEncodingException {
			this.tokenIter = tokenIter;
			OutputStreamWriter osw = new OutputStreamWriter(
					process.getOutputStream(), processIOEncoding);
			outWriter = new PrintWriter(new BufferedWriter(osw), true);
		}

		@Override
		public void run() {
			try {
				while (tokenIter.hasNext()) {
					TT token = tokenIter.next();
					String tokenStr = tokenAdapter.getText(token);
					if (tokenStr.indexOf('\t') >= 0) {
						throw new IllegalStateException(String.format(
								"Token '%s' contains illegal characters"));
					}
					outWriter.print(tokenStr);
					// \r\n (CRLF) does not work for Windows build of Hunpos
					outWriter.print('\n');
					outWriter.flush();
				}
				// print empty line to make sentence end
				// \r\n (CRLF) does not work for Windows build of Hunpos
				outWriter.print('\n');
				outWriter.flush();
			} catch (Throwable e) {
				exception = e;
			}
		}

		public Throwable getException() {
			return exception;
		}
	}

	private Process getTaggerProcess() throws IOException {
		if (process == null) {
			List<String> cmd = Lists.newArrayList();
			cmd.add(exeResolver.getExecutable());

			// TODO
			// add hunpos-tag options from config fields

			cmd.add(getModelFile().getAbsolutePath());
			processCmd = Joiner.on(' ').join(cmd);

			log.trace("Invoking hunpos-tagger:\n{}", processCmd);
			ProcessBuilder pb = new ProcessBuilder();
			pb.command(cmd);
			process = pb.start();
			InitMessageReader imr = new InitMessageReader();
			Thread imrThread = new Thread(imr);
			imrThread.start();
			try {
				imrThread.join(TAGGER_READY_MAX_TIMEOUT);
			} catch (InterruptedException e) {
				log.error("Got interrupt.", e);
				stopTaggerProcess();
			}
			if (imrThread.isAlive()) {
				log.error("Can't initialize tagger - max timeout is passed. Going to kill process...");
				stopTaggerProcess();
				throw new IllegalStateException();
			}
			if (!imr.isReady()) {
				throw new IllegalStateException("hunpos-tagger was not initialized properly. " +
						"Check log messages");
			}
		}
		return process;
	}

	private static final String TAGGER_IS_READY_MESSAGE = "tagger compiled";
	private static final long TAGGER_READY_MAX_TIMEOUT = 60000;

	private class InitMessageReader implements Runnable {
		private BufferedReader procOutputReader;
		private boolean ready;

		private InitMessageReader() throws IOException {
			Reader r = new InputStreamReader(process.getErrorStream(), processIOEncoding);
			procOutputReader = new BufferedReader(r);
		}

		@Override
		public void run() {
			String line;
			try {
				while ((line = procOutputReader.readLine()) != null) {
					log.info("hunpos-tagger: {}", line);
					if (line.equalsIgnoreCase(TAGGER_IS_READY_MESSAGE)) {
						ready = true;
						break;
					}
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		public boolean isReady() {
			return ready;
		}
	}

	private File getModelFile() {
		// TODO introduce modelResolver
		if (modelFile == null) {
			if (modelName == null) {
				throw new IllegalStateException("modelName is not set");
			}
			modelFile = new File(modelName);
			if (!modelFile.isFile()) {
				throw new IllegalStateException(String.format(
						"%s is not existing file", modelName));
			}
		}
		return modelFile;
	}

	private void stopTaggerProcess() {
		if (process != null) {
			process.destroy();
			process = null;
			processCmd = null;
			log.debug("Stopped tagger process.");
		}
	}
}
