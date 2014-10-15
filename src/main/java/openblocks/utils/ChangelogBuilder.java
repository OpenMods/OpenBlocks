package openblocks.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import openmods.Log;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ChangelogBuilder {

	private static final ResourceLocation CHANGELOG = new ResourceLocation("openblocks", "changelog.json");

	private static final Type LIST_TYPE = new TypeToken<ArrayList<Changelog>>() {}.getType();

	public static class ChangelogSection {
		public String title;
		public List<String> lines = Lists.newArrayList();
	}

	public static class Changelog {
		public String version;
		public List<ChangelogSection> sections = Lists.newArrayList();
	}

	public static List<Changelog> readChangeLogs() {
		try {
			IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(CHANGELOG);
			InputStream resourceStream = resource.getInputStream();
			try {
				Reader reader = new InputStreamReader(resourceStream);
				Gson gson = new Gson();
				return gson.fromJson(reader, LIST_TYPE);
			} finally {
				resourceStream.close();
			}
		} catch (Exception e) {
			Log.severe(e, "Failed to read changelog");
		}
		return ImmutableList.of();
	}
}
