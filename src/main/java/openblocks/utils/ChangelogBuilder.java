package openblocks.utils;

import java.io.*;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import openblocks.OpenBlocks;
import openmods.Log;
import openmods.utils.ItemUtils;

public class ChangelogBuilder {

	public static ItemStack createChangeLog(String version) {

		String filename = String.format("/openblocks/changelogs/%s", version);
		InputStream input = OpenBlocks.class.getResourceAsStream(filename);

		if (input != null) {

			ItemStack book = new ItemStack(Items.written_book);

			NBTTagCompound bookTag = ItemUtils.getItemTag(book);

			bookTag.setString("title", StatCollector.translateToLocalFormatted("openblocks.changelog.title", version));
			bookTag.setString("author", "The OpenMods team");

			NBTTagList bookPages = new NBTTagList();
			bookTag.setTag("pages", bookPages);

			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			try {

				int pageNumber = 1;
				StringBuilder pageInfo = new StringBuilder();
				String line = null;

				while ((line = in.readLine()) != null) {
					if (line.equals("EOP")) {
						bookPages.appendTag(new NBTTagString(Integer.toString(pageNumber++), pageInfo.toString()));
						pageInfo = new StringBuilder();
					} else {
						pageInfo.append(line);
						pageInfo.append("\n");
					}
				}
			} catch (IOException e) {
				Log.warn(e, "Failed to read changelog");
			}

			return book;
		}

		return null;
	}

}
