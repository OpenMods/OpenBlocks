package openblocks.client;

import static openblocks.client.Icons.blockIcon;
import static openblocks.client.Icons.itemIcon;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import openblocks.client.Icons.ComposedIcon;
import openblocks.client.Icons.IDrawableIcon;
import openmods.Log;

public class SoundIconRegistry {

	private final static String ICON_FRAME = "openblocks:sound_frame";

	public final static String CATEGORY_STREAMING = "!streaming";

	private static String iconIdPrefix(String id) {
		return "openblocks:sound_" + id;
	}

	public interface ISoundCategory {
		public IDrawableIcon getIcon(Iterator<String> path);

		public void registerIcons(int type, IIconRegister registry);
	}

	public static class ConstantIcon implements ISoundCategory {
		private final IDrawableIcon icon;

		private ConstantIcon(IDrawableIcon icon) {
			this.icon = icon;
		}

		@Override
		public void registerIcons(int type, IIconRegister registry) {
			icon.registerIcons(type, registry);
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
		public void registerIcons(int type, IIconRegister registry) {
			if (defaultIcon != null) defaultIcon.registerIcons(type, registry);

			for (ISoundCategory cat : subCategories.values())
				cat.registerIcons(type, registry);
		}
	}

	private static class TintedIconCategory extends MappedCategory {
		private final String iconId;

		private TintedIconCategory(String iconId) {
			this.iconId = iconId;
		}

		public void add(String id, int color) {
			IDrawableIcon icon = itemIcon(iconId, color);
			add(id, new ConstantIcon(icon));
		}
	}

	private static class MobIcons {
		public final IDrawableIcon normalIcon;
		public final IDrawableIcon hurtIcon;
		public final IDrawableIcon deathIcon;

		private MobIcons(IDrawableIcon normalIcon, IDrawableIcon hurtIcon, IDrawableIcon deathIcon) {
			this.normalIcon = normalIcon;
			this.hurtIcon = hurtIcon;
			this.deathIcon = deathIcon;
		}

		private void registerIcons(int type, IIconRegister registry) {
			normalIcon.registerIcons(type, registry);
			hurtIcon.registerIcons(type, registry);
			deathIcon.registerIcons(type, registry);
		}
	}

	private static class MobSounds implements ISoundCategory {
		private Map<String, MobIcons> mobs = Maps.newHashMap();
		private MobIcons unknownMob;

		private static MobIcons createMobIcons(String innerIcon, int innerColor, int frameColor) {
			IDrawableIcon frame = itemIcon(ICON_FRAME, frameColor);
			IDrawableIcon normal = makeFramedItemIcon(iconIdPrefix(innerIcon), innerColor, frame);
			IDrawableIcon hurt = makeFramedItemIcon(iconIdPrefix("mob_hurt"), innerColor, frame);
			IDrawableIcon death = makeFramedItemIcon(iconIdPrefix("mob_death"), innerColor, frame);
			return new MobIcons(normal, hurt, death);
		}

		public MobSounds() {
			unknownMob = createMobIcons("mob_unknown", DEFAULT_COLOR, DEFAULT_COLOR);
		}

		@Override
		public IDrawableIcon getIcon(Iterator<String> path) {
			String mobName = path.next();
			String actionName = path.next();

			MobIcons mob = mobs.get(mobName);

			if (mob == null) mob = unknownMob;

			if (actionName.equals("hit")) return mob.hurtIcon;

			if (actionName.equals("death")) return mob.deathIcon;

			return mob.normalIcon;
		}

		@Override
		public void registerIcons(int type, IIconRegister registry) {
			unknownMob.registerIcons(type, registry);
			for (MobIcons icons : mobs.values())
				icons.registerIcons(type, registry);
		}

		public void addMob(String soundId, int mobId, boolean isHostile) {
			EntityEggInfo mobInfo = (EntityEggInfo)EntityList.entityEggs.get(mobId);

			if (mobInfo != null) mobs.put(soundId, createMobIcons(isHostile? "mob_hostile" : "mob_friendly", mobInfo.primaryColor, mobInfo.secondaryColor));
			else mobs.put(soundId, unknownMob);
		}
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
		public void registerIcons(int type, IIconRegister registry) {
			child.registerIcons(type, registry);
		}

	}

	private final IDrawableIcon genericIcon = simpleIcon("generic", DEFAULT_COLOR);
	private final IDrawableIcon unknownIcon = simpleIcon("unknown", DEFAULT_COLOR);

	private final MappedCategory defaultRoot = new MappedCategory();

	private final Map<String, MappedCategory> roots = Maps.newHashMap();

	private Map<ResourceLocation, IDrawableIcon> iconCache = Maps.newConcurrentMap();

	public static final int DEFAULT_COLOR = 0xFFFFFF;

	@SubscribeEvent
	public void registerIcons(TextureStitchEvent evt) {
		final TextureMap map = evt.map;
		final int textureType = map.getTextureType();

		genericIcon.registerIcons(textureType, map);
		unknownIcon.registerIcons(textureType, map);
		defaultRoot.registerIcons(textureType, map);

		for (MappedCategory category : roots.values())
			category.registerIcons(textureType, map);
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
		return itemIcon(iconIdPrefix(id), color);
	}

	private static IDrawableIcon makeLayeredIcon(IDrawableIcon front, IDrawableIcon back) {
		return new ComposedIcon(front, back, 1.0, 0.00001);
	}

	public static IDrawableIcon makeFramedIcon(IDrawableIcon inner, IDrawableIcon frame) {
		return new ComposedIcon(frame, inner, 0.6, 0);
	}

	public static IDrawableIcon makeFramedIcon(IDrawableIcon front, IDrawableIcon back, IDrawableIcon frame) {
		IDrawableIcon inner = makeLayeredIcon(front, back);
		return new ComposedIcon(frame, inner, 0.6, 0);
	}

	private static IDrawableIcon makeFramedItemIcon(String innerIcon, int innerColor, IDrawableIcon frame) {
		IDrawableIcon inner = itemIcon(innerIcon, innerColor);
		return makeFramedIcon(inner, frame);
	}

	private static IDrawableIcon makeFramedItemIcon(String innerIcon, IDrawableIcon frame) {
		return makeFramedItemIcon(innerIcon, 0xFFFFFF, frame);
	}

	private static IDrawableIcon makeFramedBlockIcon(String innerIcon, int innerColor, IDrawableIcon frame) {
		IDrawableIcon inner = blockIcon(innerIcon, innerColor);
		return makeFramedIcon(inner, frame);
	}

	private static IDrawableIcon makeFramedBlockIcon(String innerIcon, IDrawableIcon frame) {
		return makeFramedBlockIcon(innerIcon, 0xFFFFFF, frame);
	}

	private static IDrawableIcon makeBlockIcon(String block, IDrawableIcon front, IDrawableIcon frame) {
		IDrawableIcon back = blockIcon(block);
		return makeFramedIcon(makeLayeredIcon(front, back), frame);
	}

	private static void addBlocks(MappedCategory cat, IDrawableIcon front, IDrawableIcon frame) {
		cat.add("cloth", makeBlockIcon("wool_colored_white", front, frame));
		cat.add("grass", makeBlockIcon("dirt", front, frame));
		cat.add("gravel", makeBlockIcon("gravel", front, frame));
		cat.add("sand", makeBlockIcon("sand", front, frame));
		cat.add("snow", makeBlockIcon("snow", front, frame));
		cat.add("stone", makeBlockIcon("stone", front, frame));
		cat.add("wood", makeBlockIcon("log_oak_top", front, frame));
		cat.add("ladder", makeBlockIcon("ladder", front, frame));
	}

	private MappedCategory createRoot(String id) {
		MappedCategory root = new MappedCategory();
		roots.put(id, root);
		return root;
	}

	public void registerDefaults() {
		IDrawableIcon frameWhite = itemIcon(ICON_FRAME, 0xFFFFFF);
		IDrawableIcon frameRed = itemIcon(ICON_FRAME, 0xFF0000);
		IDrawableIcon frameGreen = itemIcon(ICON_FRAME, 0x00FF00);
		IDrawableIcon frameBlue = itemIcon(ICON_FRAME, 0x0000FF);
		IDrawableIcon frameYellow = itemIcon(ICON_FRAME, 0xFFFF00);

		IDrawableIcon shovel = itemIcon("diamond_shovel");
		IDrawableIcon boots = itemIcon("diamond_boots");

		defaultRoot.defaultIcon = unknownIcon;

		{
			MappedCategory mcRoot = createRoot("minecraft");
			mcRoot.defaultIcon = unknownIcon;

			{
				MappedCategory ambient = mcRoot.add("ambient", new MappedCategory());
				{

					MappedCategory weather = ambient.add("weather", new MappedCategory());
					weather.add("rain", simpleIcon("rain", 0x0000FF));
					weather.add("thunder", genericIcon);
				}
			}

			{

				MappedCategory fire = mcRoot.add("fire", new MappedCategory());
				fire.add("fire", makeFramedBlockIcon("fire_layer_0", frameRed));
				fire.add("ignite", makeFramedItemIcon("flint_and_steel", frameRed));

			}

			mcRoot.add("fireworks", makeFramedItemIcon("fireworks", frameRed));

			addBlocks(mcRoot.add("dig", new MappedCategory()), shovel, frameYellow);
			addBlocks(mcRoot.add("step", new MappedCategory()), boots, frameGreen);

			{
				TintedIconCategory liquid = mcRoot.add("liquid", new TintedIconCategory(iconIdPrefix("liquid")));
				liquid.add("lava", makeFramedBlockIcon("lava_flow", frameRed));
				liquid.add("water", makeFramedBlockIcon("water_flow", frameBlue));
				liquid.add("lavapop", 0xFF0000);
			}

			{
				IDrawableIcon hurt = makeFramedItemIcon(iconIdPrefix("mob_hurt"), 0xFFFFFF, frameWhite);
				IDrawableIcon death = makeFramedItemIcon(iconIdPrefix("mob_death"), 0xFFFFFF, frameWhite);

				MappedCategory game = mcRoot.add("game", new MappedCategory());

				{
					MappedCategory potion = game.add("potion", new MappedCategory());
					potion.add("smash", makeFramedItemIcon("potion_bottle_splash", frameWhite));
				}

				{
					MappedCategory potion = game.add("tnt", new MappedCategory());
					potion.add("primed", makeFramedBlockIcon("tnt_side", frameRed));
				}

				{
					IDrawableIcon hostileBack = simpleIcon("mob_hostile", 0xFFFFFF);
					MappedCategory hostile = game.add("hostile", new MappedCategory());

					hostile.defaultIcon = makeFramedIcon(hostileBack, frameWhite);
					hostile.add("die", makeFramedIcon(death, hostileBack, frameRed));
					hostile.add("hurt", makeFramedIcon(hurt, hostileBack, frameRed));
				}

				{
					IDrawableIcon neutralBack = simpleIcon("mob_friendly", 0xFFFFFF);
					MappedCategory netural = game.add("neutral", new MappedCategory());
					netural.defaultIcon = makeFramedIcon(neutralBack, frameWhite);
					netural.add("die", makeFramedIcon(death, neutralBack, frameRed));
					netural.add("hurt", makeFramedIcon(hurt, neutralBack, frameRed));
				}

				{
					IDrawableIcon playerBack = itemIcon("skull_steve", 0xFFFFFF);
					MappedCategory player = game.add("player", new MappedCategory());
					player.defaultIcon = makeFramedIcon(playerBack, frameWhite);
					player.add("die", makeFramedIcon(hurt, playerBack, frameRed));
					player.add("hurt", makeFramedIcon(hurt, playerBack, frameRed));
				}
			}

			{
				MappedCategory records = mcRoot.add("records", new MappedCategory());
				records.add("13", makeFramedItemIcon("record_13", frameBlue));
				records.add("cat", makeFramedItemIcon("record_cat", frameBlue));
				records.add("blocks", makeFramedItemIcon("record_blocks", frameBlue));
				records.add("chirp", makeFramedItemIcon("record_chirp", frameBlue));
				records.add("far", makeFramedItemIcon("record_far", frameBlue));
				records.add("mall", makeFramedItemIcon("record_mall", frameBlue));
				records.add("mellohi", makeFramedItemIcon("record_mellohi", frameBlue));
				records.add("stal", makeFramedItemIcon("record_stal", frameBlue));
				records.add("strad", makeFramedItemIcon("record_strad", frameBlue));
				records.add("ward", makeFramedItemIcon("record_ward", frameBlue));
				records.add("11", makeFramedItemIcon("record_11", frameBlue));
				records.add("wait", makeFramedItemIcon("record_wait", frameBlue));
			}

			{
				MobSounds mobs = mcRoot.add("mob", new MobSounds());
				mobs.addMob("blaze", 61, true);
				mobs.addMob("creeper", 50, true);
				mobs.addMob("magmacube", 62, true);
				mobs.addMob("silverfish", 60, true);
				mobs.addMob("skeleton", 51, true);
				mobs.addMob("slime", 55, true);
				mobs.addMob("spider", 52, true);
				mobs.addMob("wither", 64, true);
				mobs.addMob("zombie", 54, true);
				mobs.addMob("enderdragon", 62, true);
				mobs.addMob("endermen", 58, true);
				mobs.addMob("ghast", 56, true);
				mobs.addMob("witch", 66, true);

				mobs.addMob("bat", 65, false);
				mobs.addMob("cat", 62, false);
				mobs.addMob("chicken", 93, false);
				mobs.addMob("cow", 92, false);
				mobs.addMob("horse", 100, false);
				mobs.addMob("irongolem", 99, false);
				mobs.addMob("pig", 90, false);
				mobs.addMob("sheep", 91, false);
				mobs.addMob("villager", 120, false);
				mobs.addMob("zombiepig", 57, false); // YMMV
				mobs.addMob("wolf", 95, false);
			}

			{
				TintedIconCategory note = mcRoot.add("note", new TintedIconCategory(iconIdPrefix("note")));
				note.add("bass", 0x0000FF);
				note.add("bassattack", 0xFFFF00);
				note.add("bd", 0x00FFFF);
				note.add("harp", 0xFF0000);
				note.add("hat", 0x00FF00);
				note.add("pling", 0xFF00FF);
				note.add("snare", 0xFFFFFF);
			}

			mcRoot.add("portal", makeFramedBlockIcon("portal", frameWhite));
			mcRoot.add("minecart", makeFramedItemIcon("minecart_normal", frameWhite));

			{
				MappedCategory random = mcRoot.add("random", new MappedCategory());

				IDrawableIcon anvil = makeFramedBlockIcon("anvil_base", frameWhite);
				random.add("anvil_land", anvil);
				random.add("anvil_use", anvil);
				random.add("anvil_break", anvil);

				random.add("bow", makeFramedItemIcon("bow_standby", frameWhite));
				random.add("bowhit", makeFramedItemIcon("arrow", frameWhite));

				IDrawableIcon damage = makeFramedBlockIcon("destroy_stage_5", frameWhite);
				random.add("break", damage);

				IDrawableIcon eat = makeFramedItemIcon("potato_baked", frameWhite);
				random.add("eat", eat);
				random.add("burp", eat);

				IDrawableIcon chest = makeFramedBlockIcon("planks_oak", frameWhite);
				random.add("chestclosed", chest);
				random.add("chestopen", chest);

				IDrawableIcon click = simpleIcon("click", DEFAULT_COLOR);
				random.add("click", click);
				random.add("wood_click", click);
				random.add("pop", click);

				IDrawableIcon door = makeFramedItemIcon("door_wood", frameWhite);
				random.add("door_close", door);
				random.add("door_open", door);

				IDrawableIcon drink = makeFramedItemIcon("potion_bottle_drinkable", frameWhite);
				random.add("drink", drink);

				IDrawableIcon tnt = makeFramedBlockIcon("tnt_side", frameWhite);
				random.add("explode", tnt);

				random.add("fizz", simpleIcon("fizz", DEFAULT_COLOR));

				IDrawableIcon exp = makeFramedItemIcon("experience_bottle", frameWhite);
				random.add("levelup", exp);
				random.add("orb", exp);

				IDrawableIcon glass = makeFramedBlockIcon("glass", frameWhite);
				random.add("splash", glass);
			}

			{
				MappedCategory tile = mcRoot.add("tile", new MappedCategory());
				IDrawableIcon piston = makeFramedBlockIcon("piston_side", frameWhite);
				tile.add("piston", piston);
			}

			mcRoot.add("creeper", makeFramedItemIcon("skull_creeper", frameGreen));
		}

		IDrawableIcon potato = makeFramedItemIcon("potato_baked", frameWhite);
		IDrawableIcon apple = makeFramedItemIcon("apple", frameWhite);
		IDrawableIcon pearl = makeFramedItemIcon("ender_pearl", frameWhite);
		IDrawableIcon write = makeFramedItemIcon("book_writable", frameWhite);

		{
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
