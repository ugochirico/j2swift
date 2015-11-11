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

package com.kingxt.j2swift.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jdt.core.dom.ITypeBinding;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.kingxt.Options;
import com.kingxt.j2swift.types.IOSTypeBinding;

/**
 * Manages the mapping of types to their header files.
 */
public class HeaderMap {

  /**
   * Public packages included by the j2objc libraries. This list is necessary so
   * that when package directories are suppressed, the platform headers can still
   * be found.
   */
  // TODO(tball): move this list to a distributed file, perhaps generated by build.
  private static final Set<String> PLATFORM_PACKAGES = Sets.newHashSet(new String[] {
      "android",
      "com.android.internal.util",
      "com.google.android",
      "com.google.common",
      "com.google.common.annotations",
      "com.google.common.base",
      "com.google.common.cache",
      "com.google.common.collect",
      "com.google.common.hash",
      "com.google.common.io",
      "com.google.common.math",
      "com.google.common.net",
      "com.google.common.primitives",
      "com.google.common.util",
      "com.google.j2objc",
      "com.google.protobuf",
      "dalvik",
      "java",
      "javax",
      "junit",
      "libcore",
      "org.apache.harmony",
      "org.hamcrest",
      "org.junit",
      "org.kxml2",
      "org.mockito",
      "org.w3c",
      "org.xml.sax",
      "org.xmlpull",
      "sun.misc",
  });

  private final Map<String, String> map = Maps.newHashMap();

  public String get(ITypeBinding type) {
    if (type instanceof IOSTypeBinding) {
      // Some IOS types are declared in a different header.
      String header = ((IOSTypeBinding) type).getHeader();
      if (header != null) {
        return header;
      }
    }

    return get(type.getErasure().getQualifiedName());
  }

  public String get(String qualifiedName) {
    String mappedHeader = map.get(qualifiedName);
    if (mappedHeader != null) {
      return mappedHeader;
    }

    // Use package directories for platform classes if they do not have an entry in the header
    // mapping.
    if (Options.usePackageDirectories() || isPlatformClass(qualifiedName)) {
      return qualifiedName.replace('.', '/') + ".h";
    } else {
      return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1) + ".h";
    }
  }

  public void put(String qualifiedName, String header) {
    map.put(qualifiedName, header);
  }

  private static boolean isPlatformClass(String className) {
    String[] parts = className.split("\\.");
    String pkg = null;
    for (int i = 0; i < parts.length; i++) {
      pkg = i == 0 ? parts[0] : String.format("%s.%s", pkg, parts[i]);
      if (PLATFORM_PACKAGES.contains(pkg)) {
        return true;
      }
    }
    return false;
  }

  public void loadMappings() {
    List<String> headerMappingFiles = Options.getHeaderMappingFiles();
    List<Properties> headerMappingProps = Lists.newArrayList();

    try {
      if (headerMappingFiles == null) {
        try {
          headerMappingProps.add(FileUtil.loadProperties(Options.DEFAULT_HEADER_MAPPING_FILE));
        } catch (FileNotFoundException e) {
          // Don't fail if mappings aren't configured and the default mapping is absent.
        }
      } else {
        for (String resourceName : headerMappingFiles) {
          headerMappingProps.add(FileUtil.loadProperties(resourceName));
        }
      }
    } catch (IOException e) {
      ErrorUtil.error(e.getMessage());
    }

    for (Properties mappings : headerMappingProps) {
      Enumeration<?> keyIterator = mappings.propertyNames();
      while (keyIterator.hasMoreElements()) {
        String key = (String) keyIterator.nextElement();
        map.put(key, mappings.getProperty(key));
      }
    }
  }

  public void printMappings() {
    File outputFile = Options.getOutputHeaderMappingFile();
    if (outputFile == null) {
      return;
    }
    try {
      if (!outputFile.exists()) {
        outputFile.getParentFile().mkdirs();
        outputFile.createNewFile();
      }
      PrintWriter writer = new PrintWriter(outputFile);

      for (Map.Entry<String, String> entry : map.entrySet()) {
        writer.println(String.format("%s=%s", entry.getKey(), entry.getValue()));
      }

      writer.close();
    } catch (IOException e) {
      ErrorUtil.error(e.getMessage());
    }
  }
}
