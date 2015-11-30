package com.j2swift;

public class JreEmulationTransfer {

	public static void main(String[] args) {
		String[] fileList = new String[] {
				"java/lang/Integer.java",
				"java/lang/TypeNotPresentException.java",
				"java/lang/IntegralToString.java",
				"java/lang/Character.java",
				
				"java/lang/AssertionError.java",
				"java/lang/ArrayStoreException.java",
				"java/lang/CharSequence.java",
				"java/lang/Appendable.java",
				"java/lang/Number.java",
				"java/lang/Error.java",
				"java/lang/Boolean.java",
				"java/lang/RuntimeException.java",
				"java/lang/ReflectiveOperationException.java",
				"java/lang/NoSuchFieldException.java",
				"java/lang/IllegalArgumentException.java",
				"java/lang/NumberFormatException.java",
				"java/lang/NegativeArraySizeException.java",
				"java/lang/ClassCastException.java",
				"java/lang/Runnable.java",
				"java/lang/Exception.java", 
				"java/lang/RuntimeException.java",
				"java/lang/Comparable.java",
				"java/lang/SecurityException.java",
				"java/lang/StringIndexOutOfBoundsException.java",
				"java/lang/UnsupportedOperationException.java",
				"java/lang/ArithmeticException.java",
				"java/lang/IndexOutOfBoundsException.java",
				"java/lang/ArrayIndexOutOfBoundsException.java",
				
				"java/io/ObjectStreamException.java",
				"java/io/InvalidObjectException.java",
				"java/io/IOException.java", 
				"java/io/Serializable.java",
				 };
		new JreEmulationTransfer().run(fileList);
	}

	private void run(String[] jreListFile) {
		String currentDirectory = System.getProperty("user.dir") + "/";
		String outputDirectory = currentDirectory + "../jre_emul/JreEmulation";
		String commandPrefix = "-jre -d " + outputDirectory + " -classpath " + currentDirectory + "../jre_emul/libcore/luni/src/main/java";
		String fileNamePrefix = currentDirectory
				+ "../jre_emul/libcore/luni/src/main/java/";
		for (String fileName : jreListFile) {
			String command = commandPrefix + " " + fileNamePrefix + fileName;
			J2Swift.translate(command.split(" "));
		}
	}
}
