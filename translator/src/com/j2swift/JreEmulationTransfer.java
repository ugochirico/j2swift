package com.j2swift;

public class JreEmulationTransfer {
	
	public static void main(String[] args) {
		String[] fileList = new String[]{
				"java/lang/Runnable.java", "java/lang/RuntimeException.java"};
		new JreEmulationTransfer().run(fileList);
	}
	
	private void run(String[] jreListFile) {
		String currentDirectory = System.getProperty("user.dir") + "/";
		String outputDirectory = currentDirectory + "../jre_emul/JreEmulation";
		String commandPrefix = "-jre -d " + outputDirectory;
		String fileNamePrefix = currentDirectory + "../jre_emul/libcore/luni/src/main/java/";
		for (String fileName : jreListFile) {
			String command = commandPrefix + " " + fileNamePrefix + fileName;
			J2Swift.translate(command.split(" "));
		}
	}
}
