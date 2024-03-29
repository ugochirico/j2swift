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
package com.j2swift.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.annotation.Nullable;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;

import com.google.common.io.CharStreams;
import com.j2swift.Options;
import com.j2swift.file.InputFile;
import com.j2swift.file.JarredInputFile;
import com.j2swift.file.RegularInputFile;

/**
 * Utilities for reading {@link com.google.devtools.j2objc.file.InputFile}s.
 *
 * @author Tom Ball, Keith Stanger, Mike Thvedt
 */
public class FileUtil {

  public static String getMainTypeName(InputFile file) {
    String basename = file.getBasename();
    int end = basename.lastIndexOf(".java");
    if (end == -1) {
      end = basename.lastIndexOf(".class");
    }
    if (end != -1) {
      basename = basename.substring(0, end);
    }
    return basename;
  }

  public static String getQualifiedMainTypeName(InputFile file, CompilationUnit unit) {
    String qualifiedName = getMainTypeName(file);
    PackageDeclaration packageDecl = unit.getPackage();
    if (packageDecl != null) {
      String packageName = packageDecl.getName().getFullyQualifiedName();
      qualifiedName = packageName + "." + qualifiedName;
    }
    return qualifiedName;
  }

  /**
   * Find a {@link com.google.devtools.j2objc.file.InputFile} on the source path,
   * either in a directory or a jar.
   * Returns a file guaranteed to exist, or null.
   */
  @Nullable
  public static InputFile findOnSourcePath(String qualifiedName) throws IOException {
    return findOnPaths(qualifiedName, Options.getSourcePathEntries(), ".java");
  }

  /**
   * Find a {@link com.google.devtools.j2objc.file.InputFile} on the class path,
   * either in a directory or a jar.
   * Returns a file guaranteed to exist, or null.
   */
  @Nullable
  public static InputFile findOnClassPath(String qualifiedName) throws IOException {
    return findOnPaths(qualifiedName, Options.getClassPathEntries(), ".class");
  }

  private static InputFile findOnPaths(
      String qualifiedName, List<String> paths, String extension) throws IOException {
    String sourceFileName = qualifiedName.replace('.', File.separatorChar) + extension;
    // Zip/jar files always use forward slashes.
    String jarEntryName = qualifiedName.replace('.', '/') + extension;
    for (String pathEntry : paths) {
      File f = new File(pathEntry);
      if (f.isDirectory()) {
        RegularInputFile regularFile = new RegularInputFile(
            pathEntry + File.separatorChar + sourceFileName, sourceFileName);
        if (regularFile.exists()) {
          return regularFile;
        }
      } else {
        // Assume it's a jar file
        JarredInputFile jarFile = new JarredInputFile(pathEntry, jarEntryName);
        if (jarFile.exists()) {
          return jarFile;
        }
      }
    }
    return null;
  }

  public static String readFile(InputFile file) throws IOException {
    return CharStreams.toString(file.openReader());
  }

  private static InputStream streamForFile(String filename) throws IOException {
    File f = new File(filename);
    if (f.exists()) {
      return new FileInputStream(f);
    } else {
    	//TODO
      InputStream stream = null;// J2ObjC.class.getResourceAsStream(filename);
      if (stream == null) {
        throw new FileNotFoundException(filename);
      }
      return stream;
    }
  }

  /**
   * Reads the given properties file.
   */
  public static Properties loadProperties(String resourceName) throws IOException {
    return loadProperties(streamForFile(resourceName));
  }

  public static Properties loadProperties(InputStream in) throws IOException {
    try {
      Properties p = new Properties();
      p.load(in);
      return p;
    } finally {
      in.close();
    }
  }

  public static File createTempDir(String dirname) throws IOException {
    File tmpDirectory = File.createTempFile(dirname, ".tmp");
    tmpDirectory.delete();
    if (!tmpDirectory.mkdir()) {
      throw new IOException("Could not create tmp directory: " + tmpDirectory.getPath());
    }
    tmpDirectory.deleteOnExit();
    return tmpDirectory;
  }

  /**
   * Recursively delete specified directory.
   */
  public static void deleteTempDir(File dir) {
    // TODO(cpovirk): try Directories.deleteRecursively if a c.g.c.unix dep is OK
    if (dir != null && dir.exists()) {
      for (File f : dir.listFiles()) {
        if (f.isDirectory()) {
          deleteTempDir(f);
        } else {
          f.delete();
        }
      }
      dir.delete();
    }
  }

  private FileUtil() {}
}
