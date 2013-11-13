package openblocks;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class Mods {
	public static final String IC2 = "IC2";
	public static final String BUILDCRAFT = "BuildCraft|Core";
	public static final String APPLIED_ENERGISTICS = "AppliedEnergistics";
	public static final String FORESTRY = "Forestry";
	public static final String THAUMCRAFT = "Thaumcraft";
	public static final String PORTALGUN = "PortalGun";
	public static final String MPS = "powersuits";
	public static final String THERMALEXPANSION = "ThermalExpansion";
	public static final String RAILCRAFT = "Railcraft";
	public static final String COMPUTERCRAFT = "ComputerCraft";
	public static final String COMPUTERCRAFT_TURTLE = "CCTurtle";
	public static final String ENDERSTORAGE = "EnderStorage";
	public static final String OPENPERIPHERAL = "OpenPeripheral";
	public static final String OPENBLOCKS = "OpenBlocks";
	public static final String TCONSTRUCT = "TConstruct";
	public static final String ARSMAGICA2 = "arsmagica2";
	public static final String BIBLIOCRAFT = "BiblioCraft";
	public static final String BILLUND = "Billund";
	public static final String EXTRABEES = "ExtraBees";
	public static final String EXTRATREES = "ExtraTrees";
	public static final String BIOMESOPLENTY = "BiomesOPlenty";
	public static final String BUILDCRAFT_BUILDERS = "BuildCraft|Builders";
	public static final String BUILDCRAFT_CORE = "BuildCraft|Core";
	public static final String BUILDCRAFT_ENERGY = "BuildCraft|Energy";
	public static final String BUILDCRAFT_FACTORY = "BuildCraft|Factory";
	public static final String BUILDCRAFT_SILICON = "BuildCraft|Factory";
	public static final String BUILDCRAFT_TRANSPORT = "BuildCraft|Transport";
	public static final String EXTRAUTILITIES = "ExtraUtilities";
	public static final String CHICKENCHUNKS = "ChickenChunks";
	public static final String GRAVITYGUN = "GraviGun";
	public static final String HATSTAND = "HatStand";
	public static final String MAGICBEES = "MagicBees";
	public static final String STEVESCARTS = "StevesCarts";
	public static final String TRANSLOCATOR = "Translocator";
	public static final String WIRELESSREDSTONECBE = "WR-CBE|Core";

	@SuppressWarnings("rawtypes")
	public static ModContainer getModForItemStack(ItemStack stack) {
		Item item = stack.getItem();
		Class klazz = null;
		if (item == null) { return null; }
		UniqueIdentifier identifier = GameRegistry.findUniqueIdentifierFor(item);
		klazz = item.getClass();
		if (klazz.getName().startsWith("net.minecraft")) {
			return null;
		}
		if (identifier == null) {
			if (item instanceof ItemBlock) {
				int blockId = ((ItemBlock)item).getBlockID();
				Block block = Block.blocksList[blockId];
				if (block != null) {
					identifier = GameRegistry.findUniqueIdentifierFor(block);
					klazz = block.getClass();
				}
			}
		}
		Map<String, ModContainer> modList = Loader.instance().getIndexedModList();
		if (identifier != null) {
			ModContainer container = modList.get(identifier.modId);
			if (container != null) { return container; }
		}

		String[] itemClassParts = klazz.getName().split("\\.");
		ModContainer closestMatch = null;
		int mostMatchingPackages = 0;
		for (Entry<String, ModContainer> entry : modList.entrySet()) {
			Object mod = entry.getValue().getMod();
			if (mod == null) {
				continue;
			}
			String[] modClassParts = mod.getClass().getName().split("\\.");
			int packageMatches = 0;
			for (int i = 0; i < modClassParts.length; i++) {
				if (i < itemClassParts.length && itemClassParts[i] != null
						&& itemClassParts[i].equals(modClassParts[i])) {
					packageMatches++;
				} else {
					break;
				}
			}
			if (packageMatches > mostMatchingPackages) {
				mostMatchingPackages = packageMatches;
				closestMatch = entry.getValue();
			}
		}

		return closestMatch;
	}
}
