package openblocks.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import openblocks.Log;
import openblocks.OpenBlocks;
import openblocks.common.CommonProxy;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class LanguageUtils {
	public static void setupLanguages() {

		try {
			InputStream input = CommonProxy.class.getResourceAsStream(String.format("%s/languages.txt", OpenBlocks.getLanguagePath()));

			if (input == null) {

				Log.info("Can't find languages file!");
				return;
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));

			FileLineReader.readLineByLine(reader, new ILineReadMethod() {
				@Override
				public void read(String line) {

					URL url = CommonProxy.class.getResource(String.format("%s/%s.lang", OpenBlocks.getLanguagePath(), line));
					if (url == null) { return; }
					LanguageRegistry.instance().loadLocalization(url, line, false);
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}
}