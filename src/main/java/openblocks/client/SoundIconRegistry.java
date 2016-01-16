package openblocks.client;

import static openblocks.client.Icons.createIcon;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import openblocks.OpenBlocks;
import openblocks.client.Icons.ComposedIcon;
import openblocks.client.Icons.IDrawableIcon;
import openmods.Log;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

public class SoundIconRegistry {

	private static ResourceLocation ob(String id) {
		return OpenBlocks.location("items/sound_" + id);
	}

	private static ResourceLocation mc(String id) {
		return new ResourceLocation("minecraft", id);
	}

	private final static ResourceLocation ICON_FRAME = ob("frame");

	public interface ISoundCategory {
		public IDrawableIcon getIcon(Iterator<String> path);

		public void registerIcons(TextureMap registry);
	}

	public static class ConstantIcon implements ISoundCategory {
		private final IDrawableIcon icon;

		private ConstantIcon(IDrawableIcon icon) {
			this.icon = icon;
		}

		@Override
		public void registerIcons(TextureMap registry) {
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
		public void registerIcons(TextureMap registry) {
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

	private static class MobIcons {
		public final IDrawableIcon normalIcon;
		public final IDrawableIcon hurtIcon;
		public final IDrawableIcon deathIcon;

		private MobIcons(IDrawableIcon normalIcon, IDrawableIcon hurtIcon, IDrawableIcon deathIcon) {
			this.normalIcon = normalIcon;
			this.hurtIcon = hurtIcon;
			this.deathIcon = deathIcon;
		}

		private void registerIcons(TextureMap registry) {
			normalIcon.registerIcons(registry);
			hurtIcon.registerIcons(registry);
			deathIcon.registerIcons(registry);
		}
	}

	private static class MobSounds implements ISoundCategory {
		private Map<String, MobIcons> mobs = Maps.newHashMap();
		private MobIcons unknownMob;

		private static MobIcons createMobIcons(String innerIcon, int innerColor, int frameColor) {
			IDrawableIcon frame = createIcon(ICON_FRAME, frameColor);
			IDrawableIcon normal = makeFramedIcon(ob(innerIcon), innerColor, frame);
			IDrawableIcon hurt = makeFramedIcon(ob("mob_hurt"), innerColor, frame);
			IDrawableIcon death = makeFramedIcon(ob("mob_death"), innerColor, frame);
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
		public void registerIcons(TextureMap registry) {
			unknownMob.registerIcons(registry);
			for (MobIcons icons : mobs.values())
				icons.registerIcons(registry);
		}

		public void addMob(String soundId, int mobId, boolean isHostile) {
			EntityEggInfo mobInfo = EntityList.entityEggs.get(mobId);

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
		public void registerIcons(TextureMap registry) {
			child.registerIcons(registry);
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
		genericIcon.registerIcons(evt.map);
		unknownIcon.registerIcons(evt.map);
		defaultRoot.registerIcons(evt.map);

		for (MappedCategory category : roots.values())
			category.registerIcons(evt.map);
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

	private static IDrawableIcon makeIcon(ResourceLocation block, IDrawableIcon front, IDrawableIcon frame) {
		IDrawableIcon back = createIcon(block);
		return makeFramedIcon(makeLayeredIcon(front, back), frame);
	}

	private static void addBlocks(MappedCategory cat, IDrawableIcon front, IDrawableIcon frame) {
		cat.add("cloth", makeIcon(mc("blocks/wool_colored_white"), front, frame));
		cat.add("grass", makeIcon(mc("blocks/dirt"), front, frame));
		cat.add("gravel", makeIcon(mc("blocks/gravel"), front, frame));
		cat.add("sand", makeIcon(mc("blocks/sand"), front, frame));
		cat.add("snow", makeIcon(mc("blocks/snow"), front, frame));
		cat.add("stone", makeIcon(mc("blocks/stone"), front, frame));
		cat.add("wood", makeIcon(mc("blocks/log_oak_top"), front, frame));
		cat.add("ladder", makeIcon(mc("blocks/ladder"), front, frame));
	}

	private MappedCategory createRoot(String id) {
		MappedCategory root = new MappedCategory();
		roots.put(id, root);
		return root;
	}

	public void registerDefaults() {
		// TODO 1.8.9 review vanilla sounds
		// TODO 1.8.9 map sound categories for catch-all
		IDrawableIcon frameWhite = createIcon(ICON_FRAME, 0xFFFFFF);
		IDrawableIcon frameRed = createIcon(ICON_FRAME, 0xFF0000);
		IDrawableIcon frameGreen = createIcon(ICON_FRAME, 0x00FF00);
		IDrawableIcon frameBlue = createIcon(ICON_FRAME, 0x0000FF);
		IDrawableIcon frameYellow = createIcon(ICON_FRAME, 0xFFFF00);

		IDrawableIcon shovel = createIcon(mc("items/diamond_shovel"));
		IDrawableIcon boots = createIcon(mc("items/diamond_boots"));

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
				fire.add("fire", makeFramedIcon(mc("blocks/fire_layer_0"), frameRed));
				fire.add("ignite", makeFramedIcon(mc("items/flint_and_steel"), frameRed));

			}

			mcRoot.add("fireworks", makeFramedIcon(mc("items/fireworks"), frameRed));

			addBlocks(mcRoot.add("dig", new MappedCategory()), shovel, frameYellow);
			addBlocks(mcRoot.add("step", new MappedCategory()), boots, frameGreen);

			{
				TintedIconCategory liquid = mcRoot.add("liquid", new TintedIconCategory(ob("liquid")));
				liquid.add("lava", makeFramedIcon(mc("blocks/lava_flow"), frameRed));
				liquid.add("water", makeFramedIcon(mc("blocks/water_flow"), frameBlue));
				liquid.add("lavapop", 0xFF0000);
			}

			{
				IDrawableIcon hurt = makeFramedIcon(ob("mob_hurt"), 0xFFFFFF, frameWhite);
				IDrawableIcon death = makeFramedIcon(ob("mob_death"), 0xFFFFFF, frameWhite);

				MappedCategory game = mcRoot.add("game", new MappedCategory());

				{
					MappedCategory potion = game.add("potion", new MappedCategory());
					potion.add("smash", makeFramedIcon(mc("items/potion_bottle_splash"), frameWhite));
				}

				{
					MappedCategory potion = game.add("tnt", new MappedCategory());
					potion.add("primed", makeFramedIcon(mc("blocks/tnt_side"), frameRed));
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
					IDrawableIcon playerBack = createIcon(mc("skull_steve"), 0xFFFFFF); // TODO 1.8.9 find replacement
					MappedCategory player = game.add("player", new MappedCategory());
					player.defaultIcon = makeFramedIcon(playerBack, frameWhite);
					player.add("die", makeFramedIcon(hurt, playerBack, frameRed));
					player.add("hurt", makeFramedIcon(hurt, playerBack, frameRed));
				}
			}

			{
				MappedCategory records = mcRoot.add("records", new MappedCategory());
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
				TintedIconCategory note = mcRoot.add("note", new TintedIconCategory(ob("note")));
				note.add("bass", 0x0000FF);
				note.add("bassattack", 0xFFFF00);
				note.add("bd", 0x00FFFF);
				note.add("harp", 0xFF0000);
				note.add("hat", 0x00FF00);
				note.add("pling", 0xFF00FF);
				note.add("snare", 0xFFFFFF);
			}

			mcRoot.add("portal", makeFramedIcon(mc("blocks/portal"), frameWhite));
			mcRoot.add("minecart", makeFramedIcon(mc("items/minecart_normal"), frameWhite));

			{
				MappedCategory random = mcRoot.add("random", new MappedCategory());

				IDrawableIcon anvil = makeFramedIcon(mc("blocks/anvil_base"), frameWhite);
				random.add("anvil_land", anvil);
				random.add("anvil_use", anvil);
				random.add("anvil_break", anvil);

				random.add("bow", makeFramedIcon(mc("items/bow_standby"), frameWhite));
				random.add("bowhit", makeFramedIcon(mc("items/arrow"), frameWhite));

				IDrawableIcon damage = makeFramedIcon(mc("blocks/destroy_stage_5"), frameWhite);
				random.add("break", damage);

				IDrawableIcon eat = makeFramedIcon(mc("items/potato_baked"), frameWhite);
				random.add("eat", eat);
				random.add("burp", eat);

				IDrawableIcon chest = makeFramedIcon(mc("blocks/planks_oak"), frameWhite);
				random.add("chestclosed", chest);
				random.add("chestopen", chest);

				IDrawableIcon click = simpleIcon("click", DEFAULT_COLOR);
				random.add("click", click);
				random.add("wood_click", click);
				random.add("pop", click);

				IDrawableIcon door = makeFramedIcon(mc("items/door_wood"), frameWhite);
				random.add("door_close", door);
				random.add("door_open", door);

				IDrawableIcon drink = makeFramedIcon(mc("items/potion_bottle_drinkable"), frameWhite);
				random.add("drink", drink);

				IDrawableIcon tnt = makeFramedIcon(mc("blocks/tnt_side"), frameWhite);
				random.add("explode", tnt);

				random.add("fizz", simpleIcon("fizz", DEFAULT_COLOR));

				IDrawableIcon exp = makeFramedIcon(mc("items/experience_bottle"), frameWhite);
				random.add("levelup", exp);
				random.add("orb", exp);

				IDrawableIcon glass = makeFramedIcon(mc("blocks/glass"), frameWhite);
				random.add("splash", glass);
			}

			{
				MappedCategory tile = mcRoot.add("tile", new MappedCategory());
				IDrawableIcon piston = makeFramedIcon(mc("blocks/piston_side"), frameWhite);
				tile.add("piston", piston);
			}

			// TODO 1.8.9 find replacement
			mcRoot.add("creeper", makeFramedIcon(mc("blocks/skull_creeper"), frameGreen));
		}

		IDrawableIcon potato = makeFramedIcon(mc("items/potato_baked"), frameWhite);
		IDrawableIcon apple = makeFramedIcon(mc("items/apple"), frameWhite);
		IDrawableIcon pearl = makeFramedIcon(mc("items/ender_pearl"), frameWhite);
		IDrawableIcon write = makeFramedIcon(mc("items/book_writable"), frameWhite);

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
