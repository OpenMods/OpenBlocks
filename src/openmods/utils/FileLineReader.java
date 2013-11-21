package openmods.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileLineReader {

	public static void readLineByLine(BufferedReader reader, ILineReadMethod callback)
			throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			callback.read(line);
		}
		reader.close();
	}

	public static void readLineByLine(String filename, ILineReadMethod callback)
			throws IOException {
		readLineByLine(new BufferedReader(new FileReader(filename)), callback);
	}
}