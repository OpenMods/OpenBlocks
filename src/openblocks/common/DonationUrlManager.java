package openblocks.common;

import java.util.HashMap;

public class DonationUrlManager {

	protected static DonationUrlManager instance;
	public static DonationUrlManager instance() {
		if (instance == null) {
			instance = new DonationUrlManager();
		}
		return instance;
	}

	private HashMap<String, String> donationUrls = new HashMap<String, String>();

	public DonationUrlManager() {
		addUrl("OpenBlocks", "http://www.google.com");
		addUrl("OpenPeripheral", "http://www.google.com");
		addUrl("ComputerCraft", "http://www.computercraft.info/donate/");
		addUrl("CCTurtle", "http://www.computercraft.info/donate/")
	}

	public void addUrl(String modId, String url) {
		donationUrls.put(modId, url);
	}

	public String getUrl(String modId) {
		return donationUrls.get(modId);
	}
}
