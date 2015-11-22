/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.j2swift.pipeline;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.j2swift.Options;
import com.j2swift.ast.CompilationUnit;
import com.j2swift.gen.GenerationUnit;
import com.j2swift.gen.SwiftImplementationGenerator;
import com.j2swift.translate.AbstractMethodRewriter;
import com.j2swift.translate.Functionizer;
import com.j2swift.translate.OperatorRewriter;
import com.j2swift.translate.PrimitiveRewriter;
import com.j2swift.translate.Rewriter;
import com.j2swift.types.HeaderImportCollector;
import com.j2swift.types.ImplementationImportCollector;
import com.j2swift.types.Import;
import com.j2swift.util.DeadCodeMap;
import com.j2swift.util.ErrorUtil;
import com.j2swift.util.JdtParser;
import com.j2swift.util.TimeTracker;

/**
 * Processes source files by translating each source into an Objective-C header
 * and an Objective-C source file.
 *
 * @author Tom Ball, Keith Stanger, Mike Thvedt
 */
public class TranslationProcessor extends FileProcessor {

  private static final Logger logger = Logger.getLogger(TranslationProcessor.class.getName());

  private final DeadCodeMap deadCodeMap;

  private int processedCount = 0;

  public TranslationProcessor(JdtParser parser, DeadCodeMap deadCodeMap) {
    super(parser);
    this.deadCodeMap = deadCodeMap;
  }

  @Override
  protected void processConvertedTree(ProcessingContext input, CompilationUnit unit) {
    String unitName = input.getOriginalSourcePath();
    if (logger.isLoggable(Level.INFO)) {
      System.out.println("translating " + unitName);
    }
    TimeTracker ticker = getTicker(unitName);
    applyMutations(unit, deadCodeMap, ticker);
    ticker.tick("Tree mutations");
    ticker.printResults(System.out);
    processedCount++;

    GenerationUnit genUnit = input.getGenerationUnit();
    genUnit.addCompilationUnit(unit);

    // Add out-of-date dependencies to translation list.
    if (closureQueue != null) {
      checkDependencies(unit);
    }

    if (genUnit.isFullyParsed()) {
      generateObjectiveCSource(genUnit);
    }
  }

  /**
   * Translates a parsed source file, modifying the compilation unit by
   * substituting core Java type and method references with iOS equivalents.
   * For example, <code>java.lang.Object</code> maps to <code>NSObject</code>,
   * and <code>java.lang.String</code> to <code>NSString</code>. The source is
   * also modified to add support for iOS memory management, extract inner
   * classes, etc.
   */
  public static void applyMutations(
      CompilationUnit unit, DeadCodeMap deadCodeMap, TimeTracker ticker) {
    ticker.push();

//    if (deadCodeMap != null) {
//      new DeadCodeEliminator(unit, deadCodeMap).run(unit);
//      ticker.tick("DeadCodeEliminator");
//    }
//
//    OuterReferenceResolver outerResolver = new OuterReferenceResolver();
//    outerResolver.run(unit);
//    ticker.tick("OuterReferenceResolver");
//
//    // Update code that has GWT references.
//    new GwtConverter().run(unit);
//    ticker.tick("GwtConverter");
//
    // Modify AST to be more compatible with Objective C
    new Rewriter().run(unit);
    ticker.tick("Rewriter");

    // Add abstract method stubs.
    new AbstractMethodRewriter(unit).run(unit);
    ticker.tick("AbstractMethodRewriter");

//    new VariableRenamer().run(unit);
//    ticker.tick("VariableRenamer");
//
//    // Rewrite enhanced for loops into correct C code.
//    new EnhancedForRewriter().run(unit);
//    ticker.tick("EnhancedForRewriter");
//
//    // Add auto-boxing conversions.
//    new Autoboxer().run(unit);
//    ticker.tick("Autoboxer");
//
//    // Extract inner and anonymous classes
//    new AnonymousClassConverter().run(unit);
//    ticker.tick("AnonymousClassConverter");
//
//    new InnerClassExtractor(outerResolver, unit).run(unit);
//    ticker.tick("InnerClassExtractor");
//
//    // Normalize init statements
//    new InitializationNormalizer(deadCodeMap).run(unit);
//    ticker.tick("InitializationNormalizer");
//
//    // Fix references to outer scope and captured variables.
//    new OuterReferenceFixer(outerResolver).run(unit);
//    ticker.tick("OuterReferenceFixer");
//
//    // Rewrites expressions that would cause unsequenced compile errors.
//    if (Options.extractUnsequencedModifications()) {
//      new UnsequencedExpressionRewriter().run(unit);
//      ticker.tick("UnsequencedExpressionRewriter");
//    }
//
//    // Adds nil_chk calls wherever an expression is dereferenced.
//    new NilCheckResolver().run(unit);
//    ticker.tick("NilCheckResolver");
//
//    // Before: ArrayRewriter - Adds ArrayCreation nodes.
//    // Before: Functionizer - Can't rewrite function arguments.
//    new VarargsRewriter().run(unit);
//    ticker.tick("VarargsRewriter");
//
//    // Add dealloc/finalize method(s), if necessary.  This is done
//    // after inner class extraction, so that each class releases
//    // only its own instance variables.
//    new DestructorGenerator().run(unit);
//    ticker.tick("DestructorGenerator");
//
//    new JavaCloneWriter().run(unit);
//    ticker.tick("JavaCloneWriter");
//
//    new ConstantBranchPruner().run(unit);
//    ticker.tick("ConstantBranchPruner");
//
//    new OcniExtractor(unit).run(unit);
//    ticker.tick("OcniExtractor");
//
//    // Before: Functionizer - Edits constructor invocations before they are
//    //   functionized.
//    new EnumRewriter().run(unit);
//    ticker.tick("EnumRewriter");
//
//    // Before: Functionizer - Needs to rewrite some ClassInstanceCreation nodes
//    //   before Functionizer does.
//    // Before: StaticVarRewriter, OperatorRewriter - Doesn't know how to handle
//    //   the hasRetainedResult flag on ClassInstanceCreation nodes.
//    new JavaToIOSMethodTranslator().run(unit);
//    ticker.tick("JavaToIOSMethodTranslator");
//
//    // After: OcniExtractor - So that native methods can be correctly
//    //   functionized.
//    new Functionizer().run(unit);
//    ticker.tick("Functionizer");
//
//    // After: OuterReferenceFixer, Functionizer - Those passes edit the
//    //   qualifier on SuperMethodInvocation nodes.
//    new SuperMethodInvocationRewriter().run(unit);
//    ticker.tick("SuperMethodInvocationRewriter");
//
    new OperatorRewriter().run(unit);
    ticker.tick("OperatorRewriter");
    
    new PrimitiveRewriter().run(unit);
    ticker.tick("PrimitiveRewriter");
    
    new Functionizer().run(unit);
    ticker.tick("Functionizer");
//
//    // After: OperatorRewriter - Static load rewriting needs to happen after
//    //   operator rewriting.
//    new StaticVarRewriter().run(unit);
//    ticker.tick("StaticVarRewriter");
//
//    // After: StaticVarRewriter, OperatorRewriter - They set the
//    //   hasRetainedResult on ArrayCreation nodes.
//    new ArrayRewriter().run(unit);
//    ticker.tick("ArrayRewriter");
//
//    // Breaks up deeply nested expressions such as chained method calls.
//    // Should be one of the last translations because other mutations will
//    // affect how deep the expressions are.
//    new ComplexExpressionExtractor().run(unit);
//    ticker.tick("ComplexExpressionExtractor");
//
//    // Should be one of the last translations because methods and functions
//    // added in other phases may need added casts.
//    new CastResolver().run(unit);
//    ticker.tick("CastResolver");
//
//    // After: InnerClassExtractor, Functionizer - Expects all types to be
//    //   top-level and functionizing to have occured.
//    new PrivateDeclarationResolver().run(unit);
//    ticker.tick("PrivateDeclarationResolver");
//
//    // Make sure we still have a valid AST.
//    unit.validate();
//
//    ticker.pop();
  }

  @VisibleForTesting
  public static void generateObjectiveCSource(GenerationUnit unit) {
    assert unit.getOutputPath() != null;
    assert unit.isFullyParsed();
    TimeTracker ticker = getTicker(unit.getOutputPath());
    logger.fine("Generating " + unit.getOutputPath());
    logger.finest("writing output file(s) to " + Options.getOutputDirectory().getAbsolutePath());
    ticker.push();

    // write header
//    if (Options.generateSegmentedHeaders()) {
//      ObjectiveCSegmentedHeaderGenerator.generate(unit);
//    } else {
//      ObjectiveCHeaderGenerator.generate(unit);
//    }
    SwiftImplementationGenerator.generate(unit);
//    ticker.tick("Header generation");
//
//    // write implementation file
//    ObjectiveCImplementationGenerator.generate(unit);
//    ticker.tick("Implementation generation");
//
//    unit.finished();
//    ticker.pop();
//    ticker.tick("Source generation");
//    ticker.printResults(System.out);
  }

  protected void handleError(ProcessingContext input) {
    // Causes the generation unit to release any trees it was holding.
    input.getGenerationUnit().failed();
  }

  public void postProcess() {
    if (logger.isLoggable(Level.INFO)) {
      int nFiles = processedCount;
      System.out.println(String.format(
          "Translated %d %s: %d errors, %d warnings",
          nFiles, nFiles == 1 ? "file" : "files", ErrorUtil.errorCount(),
          ErrorUtil.warningCount()));
    }
    if (logger.isLoggable(Level.FINE)) {
      System.out.println(String.format("Translated %d methods as functions",
          ErrorUtil.functionizedMethodCount()));
    }
  }

  private void checkDependencies(CompilationUnit unit) {
    HeaderImportCollector hdrCollector =
        new HeaderImportCollector(HeaderImportCollector.Filter.INCLUDE_ALL);
    hdrCollector.collect(unit);
    ImplementationImportCollector implCollector = new ImplementationImportCollector();
    implCollector.collect(unit);
    Set<Import> imports = hdrCollector.getForwardDeclarations();
    imports.addAll(hdrCollector.getSuperTypes());
    imports.addAll(implCollector.getImports());
    for (Import imp : imports) {
      String qualifiedName = imp.getJavaQualifiedName();
      if (qualifiedName != null) {
        closureQueue.addName(qualifiedName);
      }
    }
  }

  private static TimeTracker getTicker(String name) {
    if (logger.isLoggable(Level.FINEST)) {
      return TimeTracker.start(name);
    } else {
      return TimeTracker.noop();
    }
  }
}
