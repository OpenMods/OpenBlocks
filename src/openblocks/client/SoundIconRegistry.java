package openblocks.client;

import static openblocks.client.Icons.blockIcon;
import static openblocks.client.Icons.itemIcon;

import java.util.Iterator;
import java.util.Map;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityEggInfo;
import net.minecraft.entity.EntityList;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.client.Icons.ComposedIcon;
import openblocks.client.Icons.IDrawableIcon;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

public class SoundIconRegistry {

	private final static String ICON_FRAME = "openblocks:sound_frame";

	public final static String CATEGORY_STREAMING = "!streaming";

	private static String iconIdPrefix(String id) {
		return "openblocks:sound_" + id;
	}

	public interface ISoundCategory {
		public IDrawableIcon getIcon(Iterator<String> path);

		public void registerIcons(int type, IconRegister registry);
	}

	public static class ConstantIcon implements ISoundCategory {
		private final IDrawableIcon icon;

		private ConstantIcon(IDrawableIcon icon) {
			this.icon = icon;
		}

		@Override
		public void registerIcons(int type, IconRegister registry) {
			icon.registerIcons(type, registry);
		}

		@Override
		public IDrawableIcon getIcon(Iterator<String> path) {
			return icon;
		}
	}

	private static class MappedCategory implements ISoundCategory {
		public IDrawableIcon defaultIcon;
		private final Map<String, ISoundCategory> subCategories = Maps
				.newHashMap();

		@Override
		public IDrawableIcon getIcon(Iterator<String> path) {
			String id = path.next();
			ISoundCategory result = subCategories.get(id);
			if (result == null)
				return defaultIcon;

			IDrawableIcon icon = result.getIcon(path);
			return (icon != null) ? icon : defaultIcon;
		}

		public <T extends ISoundCategory> T add(String id, T subcategory) {
			subCategories.put(id, subcategory);
			return subcategory;
		}

		public void add(String id, IDrawableIcon icon) {
			subCategories.put(id, new ConstantIcon(icon));
		}

		@Override
		public void registerIcons(int type, IconRegister registry) {
			if (defaultIcon != null)
				defaultIcon.registerIcons(type, registry);

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

		private MobIcons(IDrawableIcon normalIcon, IDrawableIcon hurtIcon,
				IDrawableIcon deathIcon) {
			this.normalIcon = normalIcon;
			this.hurtIcon = hurtIcon;
			this.deathIcon = deathIcon;
		}

		private void registerIcons(int type, IconRegister registry) {
			normalIcon.registerIcons(type, registry);
			hurtIcon.registerIcons(type, registry);
			deathIcon.registerIcons(type, registry);
		}
	}

	private static class MobSounds implements ISoundCategory {
		private Map<String, MobIcons> mobs = Maps.newHashMap();
		private MobIcons unknownMob;

		private static MobIcons createMobIcons(String innerIcon,
				int innerColor, int frameColor) {
			IDrawableIcon frame = itemIcon(ICON_FRAME, frameColor);
			IDrawableIcon normal = makeFramedItemIcon(iconIdPrefix(innerIcon),
					innerColor, frame);
			IDrawableIcon hurt = makeFramedItemIcon(iconIdPrefix("mob_hurt"),
					innerColor, frame);
			IDrawableIcon death = makeFramedItemIcon(iconIdPrefix("mob_death"),
					innerColor, frame);
			return new MobIcons(normal, hurt, death);
		}

		public MobSounds() {
			unknownMob = createMobIcons("mob_unknown", DEFAULT_COLOR,
					DEFAULT_COLOR);
		}

		@Override
		public IDrawableIcon getIcon(Iterator<String> path) {
			String mobName = path.next();
			String actionName = path.next();

			MobIcons mob = mobs.get(mobName);

			if (mob == null)
				mob = unknownMob;

			if (actionName.equals("hit"))
				return mob.hurtIcon;

			if (actionName.equals("death"))
				return mob.deathIcon;

			return mob.normalIcon;
		}

		@Override
		public void registerIcons(int type, IconRegister registry) {
			unknownMob.registerIcons(type, registry);
			for (MobIcons icons : mobs.values())
				icons.registerIcons(type, registry);
		}

		public void addMob(String soundId, int mobId, boolean isHostile) {
			EntityEggInfo mobInfo = (EntityEggInfo) EntityList.entityEggs
					.get(mobId);

			if (mobInfo != null)
				mobs.put(
						soundId,
						createMobIcons(isHostile ? "mob_hostile"
								: "mob_friendly", mobInfo.primaryColor,
								mobInfo.secondaryColor));
			else
				mobs.put(soundId, unknownMob);
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
		public void registerIcons(int type, IconRegister registry) {
			child.registerIcons(type, registry);
		}

	}

	private final MappedCategory root = new MappedCategory();

	public static final int DEFAULT_COLOR = 0xFFFFFF;

	@ForgeSubscribe
	public void registerIcons(TextureStitchEvent evt) {
		root.registerIcons(evt.map.textureType, evt.map);
	}

	public IDrawableIcon getIcon(String sound) {
		Iterable<String> path = Splitter.onPattern("[.:]").split(sound);
		return root.getIcon(path.iterator());
	}

	private static IDrawableIcon simpleIcon(String id, int color) {
		return itemIcon(iconIdPrefix(id), color);
	}

	private static IDrawableIcon makeFramedItemIcon(String innerIcon,
			int innerColor, IDrawableIcon frame) {
		IDrawableIcon inner = itemIcon(innerIcon, innerColor);
		return new ComposedIcon(frame, inner, 0.6, 0);
	}

	private static IDrawableIcon makeFramedItemIcon(String innerIcon,
			IDrawableIcon frame) {
		return makeFramedItemIcon(innerIcon, 0xFFFFFF, frame);
	}

	private static IDrawableIcon makeFramedBlockIcon(String innerIcon,
			int innerColor, IDrawableIcon frame) {
		IDrawableIcon inner = blockIcon(innerIcon, innerColor);
		return new ComposedIcon(frame, inner, 0.6, 0);
	}

	private static IDrawableIcon makeFramedBlockIcon(String innerIcon,
			IDrawableIcon frame) {
		return makeFramedBlockIcon(innerIcon, 0xFFFFFF, frame);
	}

	private static IDrawableIcon makeBlockIcon(String block,
			IDrawableIcon front, IDrawableIcon frame) {
		IDrawableIcon back = blockIcon(block);
		IDrawableIcon inner = new ComposedIcon(front, back, 1.0, 0.00001);
		return new ComposedIcon(frame, inner, 0.6, 0.0);
	}

	private static void addBlocks(MappedCategory cat, IDrawableIcon front,
			IDrawableIcon frame) {
		cat.add("cloth", makeBlockIcon("wool_colored_white", front, frame));
		cat.add("grass", makeBlockIcon("dirt", front, frame));
		cat.add("gravel", makeBlockIcon("gravel", front, frame));
		cat.add("sand", makeBlockIcon("sand", front, frame));
		cat.add("snow", makeBlockIcon("snow", front, frame));
		cat.add("stone", makeBlockIcon("stone", front, frame));
		cat.add("wood", makeBlockIcon("log_oak_top", front, frame));
		cat.add("ladder", makeBlockIcon("ladder", front, frame));
	}

	public void registerDefaults() {
		IDrawableIcon genericIcon = simpleIcon("generic", DEFAULT_COLOR);
		IDrawableIcon unknownIcon = simpleIcon("unknown", DEFAULT_COLOR);

		root.defaultIcon = unknownIcon;

		IDrawableIcon frameWhite = itemIcon(ICON_FRAME, 0xFFFFFF);
		IDrawableIcon frameRed = itemIcon(ICON_FRAME, 0xFF0000);
		IDrawableIcon frameGreen = itemIcon(ICON_FRAME, 0x00FF00);
		IDrawableIcon frameBlue = itemIcon(ICON_FRAME, 0x0000FF);
		IDrawableIcon frameYellow = itemIcon(ICON_FRAME, 0xFFFF00);

		MappedCategory ambient = new MappedCategory();
		ambient.add("rain", simpleIcon("rain", 0x0000FF));
		ambient.add("thunder", genericIcon);
		root.add("ambient", new SkipPath(ambient));

		IDrawableIcon shovel = itemIcon("diamond_shovel");
		IDrawableIcon boots = itemIcon("diamond_boots");
		addBlocks(root.add("dig", new MappedCategory()), shovel, frameYellow);
		addBlocks(root.add("step", new MappedCategory()), boots, frameGreen);

		root.add("fire", makeFramedBlockIcon("fire_layer_0", frameRed));
		root.add("fireworks", makeFramedItemIcon("fireworks", frameRed));

		TintedIconCategory liquid = root.add("liquid", new TintedIconCategory(
				iconIdPrefix("liquid")));
		liquid.add("lava", makeFramedBlockIcon("lava_still", frameRed));
		liquid.add("water", makeFramedBlockIcon("water_still", frameBlue));
		liquid.add("lavapop", 0xFF0000);
		liquid.add("splash", 0x0000FF);
		liquid.add("swim", 0x0000FF);

		root.add("minecart", makeFramedItemIcon("minecart_normal", frameWhite));

		MobSounds mobs = root.add("mob", new MobSounds());
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

		TintedIconCategory note = root.add("note", new TintedIconCategory(
				iconIdPrefix("note")));
		note.add("bass", 0x0000FF);
		note.add("bassattack", 0xFFFF00);
		note.add("bd", 0x00FFFF);
		note.add("harp", 0xFF0000);
		note.add("hat", 0x00FF00);
		note.add("pling", 0xFF00FF);
		note.add("snare", 0xFFFFFF);

		root.add("portal", makeFramedBlockIcon("portal", frameWhite));

		IDrawableIcon piston = makeFramedBlockIcon("piston_side", frameWhite);
		MappedCategory tile = root.add("tile", new MappedCategory());
		tile.add("piston", piston);

		MappedCategory random = root.add("random", new MappedCategory());
		random.add("bow", makeFramedItemIcon("bow_standby", frameWhite));
		random.add("bowhit", makeFramedItemIcon("arrow", frameWhite));

		IDrawableIcon door = makeFramedItemIcon("door_wood", frameWhite);
		random.add("door_close", door);
		random.add("door_open", door);

		IDrawableIcon chest = makeFramedBlockIcon("planks_oak", frameWhite);
		random.add("chestclosed", chest);
		random.add("chestopen", chest);

		IDrawableIcon anvil = makeFramedBlockIcon("anvil_base", frameWhite);
		random.add("anvil_land", anvil);
		random.add("anvil_use", anvil);
		random.add("anvil_break", anvil);

		IDrawableIcon tnt = makeFramedBlockIcon("tnt_side", frameWhite);
		random.add("explode", tnt);
		random.add("fuse", tnt);

		IDrawableIcon glass = makeFramedBlockIcon("glass", frameWhite);
		random.add("glass", glass);
		random.add("splash", glass);

		IDrawableIcon damage = makeFramedBlockIcon("destroy_stage_5",
				frameWhite);
		random.add("damage", damage);
		random.add("break", damage);

		IDrawableIcon eat = makeFramedItemIcon("potato_baked", frameWhite);
		random.add("eat", eat);
		random.add("burp", eat);

		IDrawableIcon drink = makeFramedItemIcon("potion_bottle_drinkable",
				frameWhite);
		random.add("drink", drink);

		IDrawableIcon exp = makeFramedItemIcon("experience_bottle", frameWhite);
		random.add("levelup", exp);
		random.add("orb", exp);

		IDrawableIcon click = simpleIcon("click", DEFAULT_COLOR);
		random.add("click", click);
		random.add("wood_click", click);
		random.add("pop", click);

		random.add("fizz", simpleIcon("fizz", DEFAULT_COLOR));

		IDrawableIcon apple = makeFramedItemIcon("apple", frameWhite);

		MappedCategory openblocks = root
				.add("openblocks", new MappedCategory());
		openblocks.defaultIcon = genericIcon;
		openblocks.add("teleport", unknownIcon);
		openblocks.add("chump", apple);
		openblocks.add("slowpokenom", eat);

		MappedCategory records = root.add(CATEGORY_STREAMING,
				new MappedCategory());
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

		/*
		 * Missing sounds ambient.cave random.breath random.classic_hurt
		 * random.successful_hit
		 */
	}
}
