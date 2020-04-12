package openblocks.common;

import java.util.HashMap;
import java.util.Map;
import openmods.Mods;

public class DonationUrlManager {

	protected static DonationUrlManager instance;

	public static DonationUrlManager instance() {
		if (instance == null) {
			instance = new DonationUrlManager();
		}
		return instance;
	}

	private final Map<String, String> donationUrls = new HashMap<>();

	public DonationUrlManager() {
		addUrl(Mods.COMPUTERCRAFT, "http://www.computercraft.info/donate/");
		addUrl(Mods.COMPUTERCRAFT_TURTLE, "http://www.computercraft.info/donate/");
		addUrl(Mods.TINKERSCONSTRUCT, "http://www.minecraftforum.net/topic/1659892-");
		addUrl(Mods.ARSMAGICA2, "http://www.minecraftforum.net/topic/2028696-");
		addUrl(Mods.APPLIEDENERGISTICS, "http://ae-mod.info/");
		addUrl(Mods.BIBLIOCRAFT, "http://www.bibliocraftmod.com/");
		addUrl(Mods.BILLUND, "http://www.computercraft.info/donate/");
		addUrl(Mods.EXTRABEES, "http://www.minecraftforum.net/topic/1324321-");
		addUrl(Mods.EXTRATREES, "http://www.minecraftforum.net/topic/1324321-");
		addUrl(Mods.BIOMESOPLENTY, "http://www.minecraftforge.net/forum/index.php/topic,13677.0.html");
		addUrl(Mods.BUILDCRAFT_BUILDERS, "http://minecraft.curseforge.com/mc-mods/buildcraft/");
		addUrl(Mods.BUILDCRAFT_CORE, "http://minecraft.curseforge.com/mc-mods/buildcraft/");
		addUrl(Mods.BUILDCRAFT_ENERGY, "http://minecraft.curseforge.com/mc-mods/buildcraft/");
		addUrl(Mods.BUILDCRAFT_FACTORY, "http://minecraft.curseforge.com/mc-mods/buildcraft/");
		addUrl(Mods.BUILDCRAFT_SILICON, "http://minecraft.curseforge.com/mc-mods/buildcraft/");
		addUrl(Mods.BUILDCRAFT_TRANSPORT, "http://minecraft.curseforge.com/mc-mods/buildcraft/");
		addUrl(Mods.ENDERSTORAGE, "http://www.minecraftforum.net/topic/909223-");
		addUrl(Mods.EXTRAUTILITIES, "http://www.minecraftforum.net/topic/1776056-");
		addUrl(Mods.CHICKENCHUNKS, "http://www.minecraftforum.net/topic/909223-");
		addUrl(Mods.GRAVITYGUN, "http://ichun.us/mods/");
		addUrl(Mods.HATSTAND, "http://ichun.us/mods/");
		addUrl(Mods.IC2, "http://wiki.industrial-craft.net/index.php?title=Main_Page");
		addUrl(Mods.MAGICBEES, "https://flattr.com/profile/MysteriousAges");
		addUrl(Mods.MPS, "http://machinemuse.net/");
		addUrl(Mods.PORTALGUN, "http://ichun.us/mods/");
		addUrl(Mods.RAILCRAFT, "http://railcraft.wikispaces.com/");
		addUrl(Mods.STEVESCARTS, "http://stevescarts2.wikispaces.com/");
		addUrl(Mods.TRANSLOCATOR, "http://www.minecraftforum.net/topic/909223-");
		addUrl(Mods.WIRELESSREDSTONECBE, "http://www.minecraftforum.net/topic/909223-");
	}

	public void addUrl(String modId, String url) {
		donationUrls.put(modId, url);
	}

	public String getUrl(String modId) {
		return donationUrls.get(modId);
	}
}
