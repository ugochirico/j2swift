package com.kingxt;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.kingxt.j2swift.pipeline.GenerationBatch;
import com.kingxt.j2swift.pipeline.InputFilePreprocessor;
import com.kingxt.j2swift.pipeline.ProcessingContext;
import com.kingxt.j2swift.pipeline.TranslationProcessor;
import com.kingxt.j2swift.util.DeadCodeMap;
import com.kingxt.j2swift.util.ErrorUtil;
import com.kingxt.j2swift.util.FileUtil;
import com.kingxt.j2swift.util.JdtParser;
import com.kingxt.j2swift.util.ProGuardUsageParser;

public class J2Swift {

	public static void main(String[] args) {
		args = new String[]{"-d", "/Users/xutao1/Desktop/otu", "/Users/xutao1/Documents/j2swift/src/com/kingxt/test/TestBaseClass.java"};
		
		if (args.length == 0) {
			Options.help(true);
		}
		String[] files = null;
		try {
			files = Options.load(args);
			if (files.length == 0) {
				Options.usage("no source files");
			}
		} catch (IOException e) {
			ErrorUtil.error(e.getMessage());
			System.exit(1);
		}

		run(Arrays.asList(files));

		checkErrors();
	}

	/**
	 * Runs the entire J2ObjC pipeline.
	 * 
	 * @param fileArgs
	 *            the files to process, same format as command-line args to
	 *            {@link #main}.
	 */
	public static void run(List<String> fileArgs) {
		File preProcessorTempDir = null;
		File strippedSourcesDir = null;
		try {
			JdtParser parser = createParser();

			List<ProcessingContext> inputs = Lists.newArrayList();
			GenerationBatch batch = new GenerationBatch();
			batch.processFileArgs(fileArgs);
			inputs.addAll(batch.getInputs());
			if (ErrorUtil.errorCount() > 0) {
				return;
			}
			InputFilePreprocessor inputFilePreprocessor = new InputFilePreprocessor(
					parser);
			inputFilePreprocessor.processInputs(inputs);
			if (ErrorUtil.errorCount() > 0) {
				return;
			}
			strippedSourcesDir = inputFilePreprocessor.getStrippedSourcesDir();
			if (strippedSourcesDir != null) {
				parser.prependSourcepathEntry(strippedSourcesDir.getPath());
			}

			Options.getHeaderMap().loadMappings();
			TranslationProcessor translationProcessor = new TranslationProcessor(
					parser, loadDeadCodeMap());
			translationProcessor.processInputs(inputs);
			translationProcessor.processBuildClosureDependencies();
			if (ErrorUtil.errorCount() > 0) {
				return;
			}
			translationProcessor.postProcess();

			Options.getHeaderMap().printMappings();
		} finally {
			FileUtil.deleteTempDir(preProcessorTempDir);
			FileUtil.deleteTempDir(strippedSourcesDir);
		}
	}

	@VisibleForTesting
	public static JdtParser createParser() {
		JdtParser parser = new JdtParser();
		parser.addClasspathEntries(Options.getClassPathEntries());
		parser.addClasspathEntries(Options.getBootClasspath());
		parser.addSourcepathEntries(Options.getSourcePathEntries());
		parser.setIncludeRunningVMBootclasspath(false);
		parser.setEncoding(Options.fileEncoding());
		parser.setEnableDocComments(Options.docCommentsEnabled());
		return parser;
	}

	private static DeadCodeMap loadDeadCodeMap() {
		File file = Options.getProGuardUsageFile();
		if (file != null) {
			try {
				return ProGuardUsageParser.parse(Files.asCharSource(file,
						Charset.defaultCharset()));
			} catch (IOException e) {
				throw new AssertionError(e);
			}
		}
		return null;
	}

	private static void checkErrors() {
		int errors = ErrorUtil.errorCount();
		if (Options.treatWarningsAsErrors()) {
			errors += ErrorUtil.warningCount();
		}
		if (errors > 0) {
			System.exit(errors);
		}
	}
}
