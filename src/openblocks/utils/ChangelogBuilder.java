package openblocks.utils;

import java.io.*;
import java.nio.charset.Charset;

import openblocks.ModInfo;
import openblocks.OpenBlocks;

import org.apache.commons.io.IOUtils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;

public class ChangelogBuilder {

	public static ItemStack createChangeLog() {
		
		String filename = String.format("/openblocks/changelogs/%s", ModInfo.VERSION);
		InputStream input = OpenBlocks.class.getResourceAsStream(filename);
		
		if (input != null) {
			
			ItemStack book = new ItemStack(Item.writtenBook);
			
			NBTTagCompound bookTag = new NBTTagCompound();
			
			bookTag.setString("title", String.format(StatCollector.translateToLocal("openblocks.changelog.title"), ModInfo.VERSION));
			bookTag.setString("author", "The OpenMods team");
			
			NBTTagList bookPages = new NBTTagList("pages");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(input));
			
			try {

				int pageNumber = 1;
				StringBuilder pageInfo = new StringBuilder();
				String line = null;
				
				while((line = in.readLine()) != null) {
				    if (line.equals("EOP")) {
						bookPages.appendTag(new NBTTagString(""+pageNumber++, pageInfo.toString()));
				    	pageInfo = new StringBuilder();
				    } else {
				    	pageInfo.append(line);
				    	pageInfo.append("\n");
				    }
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			bookTag.setTag("pages", bookPages);
			book.setTagCompound(bookTag);
			
			return book;
		}

		return null;
	}

}
