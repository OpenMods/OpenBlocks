package openblocks.client;

import static openblocks.client.Icons.createIcon;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.OpenBlocks;
import openblocks.client.Icons.ComposedIcon;
import openblocks.client.Icons.IDrawableIcon;
import openmods.Log;

public class SoundIconRegistry {

	private static ResourceLocation ob(String id) {
		return OpenBlocks.location("items/sound_" + id);
	}

	private static ResourceLocation mc(String id) {
		return new ResourceLocation("minecraft", id);
	}

	private final static ResourceLocation ICON_FRAME = ob("frame");

	public interface ISoundCategory {
		IDrawableIcon getIcon(Iterator<String> path);

		void registerIcons(AtlasTexture registry);
	}

	public static class ConstantIcon implements ISoundCategory {
		private final IDrawableIcon icon;

		private ConstantIcon(IDrawableIcon icon) {
			this.icon = icon;
		}

		@Override
		public void registerIcons(AtlasTexture registry) {
			icon.registerIcons(registry);
		}

		@Override
		public IDrawableIcon getIcon(Iterator<String> path) {
			return icon;
		}
	}

	private static class MappedCategory implements ISoundCategory {
		public IDrawableIcon defaultIcon;
		private final Map<String, ISoundCategory> subCategories = Maps.newHashMap();

		@Override
		public IDrawableIcon getIcon(Iterator<String> path) {
			String id = path.next();
			ISoundCategory result = subCategories.get(id);
			if (result == null) return defaultIcon;

			IDrawableIcon icon = result.getIcon(path);
			return (icon != null)? icon : defaultIcon;
		}

		public <T extends ISoundCategory> T add(String id, T subcategory) {
			subCategories.put(id, subcategory);
			return subcategory;
		}

		public void add(String id, IDrawableIcon icon) {
			subCategories.put(id, new ConstantIcon(icon));
		}

		@Override
		public void registerIcons(AtlasTexture registry) {
			if (defaultIcon != null) defaultIcon.registerIcons(registry);

			for (ISoundCategory cat : subCategories.values())
				cat.registerIcons(registry);
		}
	}

	private static class TintedIconCategory extends MappedCategory {
		private final ResourceLocation iconId;

		private TintedIconCategory(ResourceLocation iconId) {
			this.iconId = iconId;
		}

		public void add(String id, int color) {
			IDrawableIcon icon = createIcon(iconId, color);
			add(id, new ConstantIcon(icon));
		}
	}

	private static IDrawableIcon createMobIcon(ResourceLocation front, int primaryColor, int secondaryColor) {
		IDrawableIcon frame = createIcon(ICON_FRAME, secondaryColor);
		return makeFramedIcon(front, primaryColor, frame);
	}

	private static void addMob(MappedCategory category, String soundId, ResourceLocation mobId, boolean isHostile) {
		final EntityEggInfo mobInfo = EntityList.ENTITY_EGGS.get(mobId);

		// TODO maybe some default colors for egg-less mobs?
		if (mobInfo != null) addMob(category, soundId, isHostile, mobInfo.primaryColor, mobInfo.secondaryColor);
		else addMob(category, soundId, isHostile, 0xFFFFFFFF, 0x00000000);
	}

	private static void addMob(MappedCategory category, String soundId, boolean isHostile, int primaryColor, int secondaryColor) {
		final MappedCategory cat = category.add(soundId, new MappedCategory());

		final IDrawableIcon defaultIcon = createMobIcon(isHostile? ob("mob_hostile") : ob("mob_friendly"), primaryColor, secondaryColor);
		cat.add("ambient", defaultIcon); // TODO maybe some specific icons?
		cat.add("step", defaultIcon);
		cat.add("death", createMobIcon(ob("mob_death"), primaryColor, secondaryColor));
		cat.add("hurt", createMobIcon(ob("mob_hurt"), primaryColor, secondaryColor));

		cat.defaultIcon = defaultIcon;
	}

	public static class SkipPath implements ISoundCategory {
		private final ISoundCategory child;

		private SkipPath(ISoundCategory child) {
			Preconditions.checkNotNull(child);
			this.child = child;
		}

		@Override
		public IDrawableIcon getIcon(Iterator<String> path) {
			path.next(); // ignore one element
			return child.getIcon(path);
		}

		@Override
		public void registerIcons(AtlasTexture registry) {
			child.registerIcons(registry);
		}

	}

	private final IDrawableIcon genericIcon = simpleIcon("generic", DEFAULT_COLOR);
	private final IDrawableIcon unknownIcon = simpleIcon("unknown", DEFAULT_COLOR);

	private final MappedCategory defaultRoot = new MappedCategory();

	private final Map<String, MappedCategory> roots = Maps.newHashMap();

	private final Map<ResourceLocation, IDrawableIcon> iconCache = Maps.newConcurrentMap();

	public static final int DEFAULT_COLOR = 0xFFFFFF;

	@SubscribeEvent
	public void registerIcons(TextureStitchEvent evt) {
		final AtlasTexture map = evt.getMap();
		genericIcon.registerIcons(map);
		unknownIcon.registerIcons(map);
		defaultRoot.registerIcons(map);

		for (MappedCategory category : roots.values())
			category.registerIcons(map);
	}

	private IDrawableIcon findIcon(ResourceLocation sound) {
		try {
			MappedCategory domainRoot = roots.get(sound.getResourceDomain());

			Iterable<String> path = Splitter.on('.').split(sound.getResourcePath());
			if (domainRoot != null) {
				IDrawableIcon result = domainRoot.getIcon(path.iterator());
				if (result != null) return result;
			}

			IDrawableIcon result = defaultRoot.getIcon(path.iterator());
			return result != null? result : genericIcon;
		} catch (NoSuchElementException e) {
			Log.warn("Malformed sound name: %s", sound);
			return defaultRoot.defaultIcon;
		}
	}

	public IDrawableIcon getIcon(ResourceLocation sound) {
		IDrawableIcon result = iconCache.get(sound);
		if (result == null) {
			result = findIcon(sound);
			iconCache.put(sound, result);
		}

		return result;
	}

	private static IDrawableIcon simpleIcon(String id, int color) {
		return createIcon(ob(id), color);
	}

	private static IDrawableIcon makeLayeredIcon(IDrawableIcon front, IDrawableIcon back) {
		return new ComposedIcon(front, back, 1.0, 0.00001);
	}

	public static IDrawableIcon makeFramedIcon(IDrawableIcon front, IDrawableIcon back, IDrawableIcon frame) {
		IDrawableIcon inner = makeLayeredIcon(front, back);
		return new ComposedIcon(frame, inner, 0.6, 0);
	}

	public static IDrawableIcon makeFramedIcon(IDrawableIcon inner, IDrawableIcon frame) {
		return new ComposedIcon(frame, inner, 0.6, 0);
	}

	private static IDrawableIcon makeFramedIcon(ResourceLocation innerIcon, int innerColor, IDrawableIcon frame) {
		IDrawableIcon inner = createIcon(innerIcon, innerColor);
		return makeFramedIcon(inner, frame);
	}

	private static IDrawableIcon makeFramedIcon(ResourceLocation innerIcon, IDrawableIcon frame) {
		return makeFramedIcon(innerIcon, 0xFFFFFF, frame);
	}

	private static void addForStandardBlock(MappedCategory cat, String name, ResourceLocation block) {
		IDrawableIcon blockIcon = createIcon(block);

		IDrawableIcon dig = createIcon(mc("items/diamond_shovel"));
		IDrawableIcon step = createIcon(mc("items/diamond_boots"));
		IDrawableIcon hurt = createIcon(ob("mob_hurt"));
		IDrawableIcon br = createIcon(mc("blocks/destroy_stage_5"));
		IDrawableIcon unknown = createIcon(ob("unknown"));

		IDrawableIcon whiteFrame = createIcon(ICON_FRAME, 0xFFFFFF);

		final MappedCategory blockCategory = cat.add(name, new MappedCategory());
		blockCategory.add("break", makeFramedIcon(br, blockIcon, whiteFrame));
		blockCategory.add("fall", makeFramedIcon(hurt, blockIcon, whiteFrame));
		blockCategory.add("hit", makeFramedIcon(dig, blockIcon, whiteFrame));
		blockCategory.add("place", makeFramedIcon(blockIcon, whiteFrame));
		blockCategory.add("step", makeFramedIcon(step, blockIcon, whiteFrame));

		blockCategory.defaultIcon = makeLayeredIcon(unknown, blockIcon);
	}

	public static MappedCategory createSingleIconCategory(ResourceLocation icon, boolean frame) {
		final MappedCategory result = new MappedCategory();
		result.defaultIcon = frame? makeFramedIcon(icon, createIcon(ICON_FRAME, 0xFFFFFF)) : createIcon(icon, 0xFFFFFFFF);
		return result;
	}

	private static void addForBasicBlock(MappedCategory cat, String name, ResourceLocation block) {
		IDrawableIcon blockIcon = createIcon(block);
		IDrawableIcon whiteFrame = createIcon(ICON_FRAME, 0xFFFFFF);

		final MappedCategory blockCategory = cat.add(name, new MappedCategory());
		blockCategory.defaultIcon = makeFramedIcon(blockIcon, whiteFrame);
	}

	private static void addForBasicBlockAction(MappedCategory cat, String name, ResourceLocation block) {
		IDrawableIcon blockIcon = createIcon(block);
		IDrawableIcon action = createIcon(ob("click"), 0xFFFFFF);

		final MappedCategory blockCategory = cat.add(name, new MappedCategory());
		blockCategory.defaultIcon = makeLayeredIcon(action, blockIcon);
	}

	private MappedCategory createRoot(String id) {
		MappedCategory root = new MappedCategory();
		roots.put(id, root);
		return root;
	}

	public void registerDefaults() {
		// TODO map sound categories for catch-all
		final IDrawableIcon whiteFrame = createIcon(ICON_FRAME, 0xFFFFFF);
		final IDrawableIcon frame = whiteFrame;
		IDrawableIcon frameWhite = frame;
		IDrawableIcon frameRed = createIcon(ICON_FRAME, 0xFF0000);
		IDrawableIcon frameBlue = createIcon(ICON_FRAME, 0x0000FF);

		defaultRoot.defaultIcon = unknownIcon;

		{
			MappedCategory mcRoot = createRoot("minecraft");
			mcRoot.defaultIcon = unknownIcon;

			{
				final MappedCategory blocks = mcRoot.add("block", new MappedCategory());

				addForStandardBlock(blocks, "anvil", mc("blocks/anvil_base"));
				addForStandardBlock(blocks, "cloth", mc("blocks/wool_colored_white"));
				addForStandardBlock(blocks, "glass", mc("blocks/glass"));
				addForStandardBlock(blocks, "grass", mc("blocks/dirt"));
				addForStandardBlock(blocks, "gravel", mc("blocks/gravel"));
				addForStandardBlock(blocks, "ladder", mc("blocks/ladder"));
				addForStandardBlock(blocks, "metal", mc("blocks/iron_block"));
				addForStandardBlock(blocks, "sand", mc("blocks/sand"));
				addForStandardBlock(blocks, "slime", mc("blocks/slime"));
				addForStandardBlock(blocks, "snow", mc("blocks/snow"));
				addForStandardBlock(blocks, "stone", mc("blocks/stone"));
				addForStandardBlock(blocks, "wood", mc("blocks/log_oak_top"));

				addForBasicBlock(blocks, "brewing_stand", mc("blocks/brewing_stand"));
				addForBasicBlock(blocks, "chest", mc("blocks/planks_oak"));
				addForBasicBlock(blocks, "chorus_flower", mc("blocks/chorus_flower"));
				addForBasicBlock(blocks, "comparator", mc("blocks/comparator_on"));
				addForBasicBlock(blocks, "dispenser", mc("blocks/dispenser_front_horizontal"));
				addForBasicBlock(blocks, "enchantment_table", mc("blocks/enchanting_table_top"));
				addForBasicBlock(blocks, "end_gateway", mc("items/end_crystal"));
				addForBasicBlock(blocks, "enderchest", mc("items/ender_eye"));
				addForBasicBlock(blocks, "fence_gate", mc("blocks/planks_oak"));
				addForBasicBlock(blocks, "fire", mc("blocks/fire_layer_0"));
				addForBasicBlock(blocks, "furnace", mc("blocks/furnace_front_on"));
				addForBasicBlock(blocks, "iron_door", mc("items/door_iron"));
				addForBasicBlock(blocks, "iron_trapdoor", mc("blocks/iron_trapdoor"));
				addForBasicBlock(blocks, "lava", mc("blocks/lava_flow"));
				addForBasicBlock(blocks, "piston", mc("blocks/piston_side"));
				addForBasicBlock(blocks, "portal", mc("blocks/portal"));
				addForBasicBlock(blocks, "redstone_torch", mc("blocks/redstone_torch_on"));
				addForBasicBlock(blocks, "tripwire", mc("blocks/trip_wire_source"));
				addForBasicBlock(blocks, "water", mc("blocks/water_flow"));
				addForBasicBlock(blocks, "waterlily", mc("blocks/waterlily"));
				addForBasicBlock(blocks, "wooden_door", mc("items/door_wood"));
				addForBasicBlock(blocks, "wooden_trapdoor", mc("blocks/trapdoor"));

				addForBasicBlockAction(blocks, "metal_pressureplate", mc("blocks/iron_block"));
				addForBasicBlockAction(blocks, "stone_button", mc("blocks/stone"));
				addForBasicBlockAction(blocks, "stone_pressureplate", mc("blocks/stone"));
				addForBasicBlockAction(blocks, "wood_button", mc("blocks/planks_oak"));
				addForBasicBlockAction(blocks, "wood_pressureplate", mc("blocks/planks_oak"));
				addForBasicBlockAction(blocks, "lever", mc("blocks/lever"));

				{
					TintedIconCategory note = blocks.add("note", new TintedIconCategory(ob("note")));
					note.add("basedrum", 0x00FFFF);
					note.add("bass", 0x0000FF);
					note.add("harp", 0xFFFF00);
					note.add("harp", 0xFF0000);
					note.add("hat", 0x00FF00);
					note.add("pling", 0xFF00FF);
					note.add("snare", 0xFFFFFF);
				}
			}

			{
				MappedCategory entity = mcRoot.add("entity", new MappedCategory());
				addMob(entity, "blaze", mc("blaze"), true);
				addMob(entity, "creeper", mc("Creeper"), true);
				addMob(entity, "elder_guardian", mc("elder_guardian"), true);
				addMob(entity, "enderdragon", mc("ender_dragon"), true);
				addMob(entity, "endermen", mc("enderman"), true);
				addMob(entity, "endermite", mc("endermite"), true);
				addMob(entity, "ghast", mc("ghast"), true);
				addMob(entity, "guardian", mc("guardian"), true);
				addMob(entity, "husk", mc("husk"), true);
				addMob(entity, "evocation_illager", mc("evocation_illager"), true);
				addMob(entity, "magmacube", mc("magma_cube"), true);
				addMob(entity, "shulker", mc("shulker"), true);
				addMob(entity, "silverfish", mc("silverfish"), true);
				addMob(entity, "skeleton", mc("skeleton"), true);
				addMob(entity, "slime", mc("slime"), true);
				addMob(entity, "small_magmacube", mc("magma_cube"), true);
				addMob(entity, "small_slime", mc("slime"), true);
				addMob(entity, "spider", mc("spider"), true);
				addMob(entity, "stray", mc("stray"), true);
				addMob(entity, "vex", mc("vex"), true);
				addMob(entity, "vindication_illager", mc("vindication_illager"), true);
				addMob(entity, "witch", mc("witch"), true);
				addMob(entity, "wither", mc("wither"), true);
				addMob(entity, "wither_skeleton", mc("wither_skeleton"), true);
				addMob(entity, "zombie", mc("zombie"), true);
				addMob(entity, "zombie_villager", mc("zombie_villager"), true);

				addMob(entity, "bat", mc("bat"), false);
				addMob(entity, "cat", mc("ocelot"), false);
				addMob(entity, "chicken", mc("chicken"), false);
				addMob(entity, "cow", mc("cow"), false);
				addMob(entity, "donkey", mc("donkey"), false);
				addMob(entity, "horse", mc("horse"), false);
				addMob(entity, "irongolem", mc("villager_golem"), false);
				addMob(entity, "llama", mc("llama"), false);
				addMob(entity, "mooshroom", mc("mooshroom"), false);
				addMob(entity, "mule", mc("mule"), false);
				addMob(entity, "pig", mc("pig"), false);
				addMob(entity, "polar_bear", mc("polar_bear"), false);
				addMob(entity, "rabbit", mc("rabbit"), false);
				addMob(entity, "sheep", mc("sheep"), false);
				addMob(entity, "skeleton_horse", mc("skeleton_horse"), false);
				addMob(entity, "snowman", mc("snowman"), false);
				addMob(entity, "squid", mc("squid"), false);
				addMob(entity, "villager", mc("villager"), false);
				addMob(entity, "wolf", mc("wolf"), false);
				addMob(entity, "zombie_horse", mc("zombie_horse"), false);
				addMob(entity, "zombie_pig", mc("zombie_pigman"), false); // YMMV

				{
					final MappedCategory player = entity.add("player", new MappedCategory());

					player.add("burp", makeFramedIcon(mc("items/potato_baked"), frameWhite));
					player.add("levelup", makeFramedIcon(mc("items/experience_bottle"), frameWhite));

					final IDrawableIcon playerBack = createIcon(ob("player"));
					player.defaultIcon = makeFramedIcon(playerBack, frameWhite);
					player.add("die", makeFramedIcon(makeFramedIcon(ob("mob_death"), 0xFFFFFF, frameWhite), playerBack, frameRed));
					player.add("hurt", makeFramedIcon(makeFramedIcon(ob("mob_hurt"), 0xFFFFFF, frameWhite), playerBack, frameRed));
				}

				entity.add("experience_orb", createSingleIconCategory(mc("items/experience_bottle"), true));
				entity.add("arrow", createSingleIconCategory(mc("items/arrow"), true));

				entity.add("egg", createSingleIconCategory(mc("items/experience_bottle"), true));
				entity.add("endereye", createSingleIconCategory(mc("items/ender_eye"), true));
				entity.add("enderpearl", createSingleIconCategory(mc("items/ender_pearl"), true));
				entity.add("experience_bottle", createSingleIconCategory(mc("items/experience_bottle"), true));
				entity.add("firework", createSingleIconCategory(mc("items/fireworks"), true));
				entity.add("item", createSingleIconCategory(ob("click"), false));
				entity.add("itemframe", createSingleIconCategory(mc("items/item_frame"), true));
				entity.add("leashknot", createSingleIconCategory(mc("items/lead"), true));
				entity.add("lightning", createSingleIconCategory(ob("click"), false));
				entity.add("lingeringpotion", createSingleIconCategory(mc("items/potion_bottle_lingering"), true));
				entity.add("minecart", createSingleIconCategory(mc("items/minecart_normal"), true));
				entity.add("painting", createSingleIconCategory(mc("items/painting"), true));
				entity.add("snowball", createSingleIconCategory(mc("items/snowball"), true));
				entity.add("splash_potion", createSingleIconCategory(mc("items/potion_bottle_splash"), true));
				entity.add("tnt", createSingleIconCategory(mc("blocks/tnt_side"), true));
				entity.add("armorstand", createSingleIconCategory(mc("items/wooden_armorstand"), true));
				entity.add("bobber", createSingleIconCategory(mc("items/fishing_rod_cast"), true));

				// TODO
				entity.add("evocation_fangs", createSingleIconCategory(mc("items/fishing_rod_cast"), true));

				{
					final MappedCategory generic = entity.add("generic", createSingleIconCategory(ob("generic"), false));

					generic.add("burn", makeFramedIcon(mc("blocks/fire_layer_0"), whiteFrame));
					generic.add("death", simpleIcon("mob_death", 0xFFFFFFFF));
					generic.add("drink", makeFramedIcon(mc("items/potion_bottle_drinkable"), whiteFrame));
					generic.add("eat", makeFramedIcon(mc("items/potato_baked"), whiteFrame));
					generic.add("extinguish_fire", simpleIcon("fizz", 0xFFFFFFFF));
					generic.add("hurt", simpleIcon("mob_hurt", 0xFFFFFFFF));
					generic.add("small_fall", simpleIcon("mob_hurt", 0xFFFFFFFF));
					generic.add("splash", simpleIcon("rain", 0xFFFFFFFF));
					generic.add("swim", simpleIcon("fizz", 0xFFFFFFFF));
				}
			}

			{
				MappedCategory item = mcRoot.add("item", new MappedCategory());

				{
					MappedCategory armor = item.add("armor", new MappedCategory());
					armor.add("equip_chain", makeFramedIcon(mc("items/chainmail_chestplate"), whiteFrame));
					armor.add("equip_diamond", makeFramedIcon(mc("items/diamond_chestplate"), whiteFrame));
					armor.add("equip_gold", makeFramedIcon(mc("items/gold_chestplate"), whiteFrame));
					armor.add("equip_iron", makeFramedIcon(mc("items/iron_chestplate"), whiteFrame));
					armor.add("equip_leather", makeFramedIcon(mc("items/leather_chestplate"), whiteFrame));

					armor.add("equip_generic", makeFramedIcon(mc("items/empty_armor_slot_chestplate"), whiteFrame));
				}

				item.add("bottle", createSingleIconCategory(mc("items/potion_bottle_empty"), true));
				item.add("bucket", createSingleIconCategory(mc("items/bucket_empty"), true));
				item.add("chorus_fruit", createSingleIconCategory(mc("items/chorus_fruit_popped"), true));
				item.add("elytra", createSingleIconCategory(mc("items/elytra"), true));
				item.add("firecharge", createSingleIconCategory(mc("items/fireball"), true));
				item.add("flintandsteel", createSingleIconCategory(mc("items/flint_and_steel"), true));
				item.add("hoe", createSingleIconCategory(mc("items/wood_hoe"), true));
				item.add("shield", createSingleIconCategory(mc("items/empty_armor_slot_shield"), true));
				item.add("shovel", createSingleIconCategory(mc("items/wood_shovel"), true));
			}

			{
				MappedCategory records = mcRoot.add("record", new MappedCategory());
				records.add("13", makeFramedIcon(mc("items/record_13"), frameBlue));
				records.add("cat", makeFramedIcon(mc("items/record_cat"), frameBlue));
				records.add("blocks", makeFramedIcon(mc("items/record_blocks"), frameBlue));
				records.add("chirp", makeFramedIcon(mc("items/record_chirp"), frameBlue));
				records.add("far", makeFramedIcon(mc("items/record_far"), frameBlue));
				records.add("mall", makeFramedIcon(mc("items/record_mall"), frameBlue));
				records.add("mellohi", makeFramedIcon(mc("items/record_mellohi"), frameBlue));
				records.add("stal", makeFramedIcon(mc("items/record_stal"), frameBlue));
				records.add("strad", makeFramedIcon(mc("items/record_strad"), frameBlue));
				records.add("ward", makeFramedIcon(mc("items/record_ward"), frameBlue));
				records.add("11", makeFramedIcon(mc("items/record_11"), frameBlue));
				records.add("wait", makeFramedIcon(mc("items/record_wait"), frameBlue));
			}

			{
				MappedCategory weather = mcRoot.add("weather", new MappedCategory());
				weather.add("rain", simpleIcon("rain", 0x0000FF));
			}

			{
				mcRoot.add("ui", createSingleIconCategory(ob("click"), false));
			}
		}

		{

			IDrawableIcon potato = makeFramedIcon(mc("items/potato_baked"), frameWhite);
			IDrawableIcon apple = makeFramedIcon(mc("items/apple"), frameWhite);
			IDrawableIcon pearl = makeFramedIcon(mc("items/ender_pearl"), frameWhite);
			IDrawableIcon write = makeFramedIcon(mc("items/book_writable"), frameWhite);

			MappedCategory openblocks = createRoot("openblocks");
			openblocks.defaultIcon = genericIcon;

			{
				MappedCategory elevator = openblocks.add("elevator", new MappedCategory());
				elevator.defaultIcon = pearl;
			}

			{
				MappedCategory luggage = openblocks.add("luggage", new MappedCategory());
				luggage.defaultIcon = genericIcon;
				{
					MappedCategory eat = luggage.add("eat", new MappedCategory());
					eat.add("item", apple);
					eat.add("food", potato);
				}
			}

			{
				MappedCategory crayon = openblocks.add("crayon", new MappedCategory());
				crayon.add("place", write);
			}

		}
	}

}
