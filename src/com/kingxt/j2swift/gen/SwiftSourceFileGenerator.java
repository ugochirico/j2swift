package com.kingxt.j2swift.gen;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.kingxt.Options;
import com.kingxt.j2swift.util.ErrorUtil;

public abstract class SwiftSourceFileGenerator extends AbstractSourceGenerator {

	private final GenerationUnit unit;
	private final Map<String, GeneratedType> typesByName;
	private final List<GeneratedType> orderedTypes;

	/**
	 * Create a new generator.
	 * 
	 * @param unit
	 *            The AST of the source to generate
	 * @param emitLineDirectives
	 *            if true, generate CPP line directives
	 */
	protected SwiftSourceFileGenerator(GenerationUnit unit) {
		super(new SourceBuilder(false));
		this.unit = unit;
		orderedTypes = getOrderedGeneratedTypes(unit);
		typesByName = Maps.newHashMap();
		for (GeneratedType type : orderedTypes) {
			String name = type.getTypeName();
			if (name != null) {
				typesByName.put(name, type);
			}
		}
	}

	private static List<GeneratedType> getOrderedGeneratedTypes(
			GenerationUnit generationUnit) {
		// Ordered map because we iterate over it below.
		Collection<GeneratedType> generatedTypes = generationUnit
				.getGeneratedTypes();
		LinkedHashMap<String, GeneratedType> typeMap = Maps.newLinkedHashMap();
		for (GeneratedType generatedType : generatedTypes) {
			String name = generatedType.getTypeName();
			if (name != null) {
				Object dupe = typeMap.put(name, generatedType);
				assert dupe == null : "Duplicate type name: " + name;
			}
		}

		LinkedHashSet<GeneratedType> orderedTypes = Sets.newLinkedHashSet();

		for (GeneratedType generatedType : generatedTypes) {
			collectType(generatedType, orderedTypes, typeMap);
		}

		return Lists.newArrayList(orderedTypes);
	}

	private static void collectType(GeneratedType generatedType,
			LinkedHashSet<GeneratedType> orderedTypes,
			Map<String, GeneratedType> typeMap) {
		for (String superType : generatedType.getSuperTypes()) {
			GeneratedType requiredType = typeMap.get(superType);
			if (requiredType != null) {
				collectType(requiredType, orderedTypes, typeMap);
			}
		}
		orderedTypes.add(generatedType);
	}

	protected GenerationUnit getGenerationUnit() {
		return unit;
	}

	protected List<GeneratedType> getOrderedTypes() {
		return orderedTypes;
	}

	protected GeneratedType getLocalType(String name) {
		return typesByName.get(name);
	}

	protected boolean isLocalType(String name) {
		return typesByName.containsKey(name);
	}

	protected String getOutputPath() {
		return getGenerationUnit().getOutputPath() + getSuffix();
	}

	protected void save(String outputPath) {
		try {
			File outputDirectory = Options.getOutputDirectory();
			File outputFile = new File(outputDirectory, outputPath);
			File dir = outputFile.getParentFile();
			if (dir != null && !dir.exists()) {
				if (!dir.mkdirs()) {
					ErrorUtil.warning("cannot create output directory: "
							+ outputDirectory);
				}
			}
			String source = getBuilder().toString();

			// Make sure file ends with a new-line.
			if (!source.endsWith("\n")) {
				source += '\n';
			}

			Files.write(source, outputFile, Options.getCharset());
		} catch (IOException e) {
			ErrorUtil.error(e.getMessage());
		} finally {
			reset();
		}
	}

	private String getSuffix() {
		return ".swift";
	}
}
