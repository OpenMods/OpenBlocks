package openblocks.rubbish;

import static openmods.words.Words.alt;
import static openmods.words.Words.capitalize;
import static openmods.words.Words.capitalizeFully;
import static openmods.words.Words.opt;
import static openmods.words.Words.range;
import static openmods.words.Words.seq;
import static openmods.words.Words.sub;
import static openmods.words.Words.word;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Random;
import openmods.words.IGenerator;
import org.apache.commons.lang3.StringUtils;

public class LoreGenerator {

	private static final IGenerator heroGenerator = createHeroGenerator();
	private static final IGenerator loreGenerator = createLoreGenerator();

	private static final Random random = new Random();

	public static String generateLore(String playerName, String itemName) {
		Map<String, String> params = Maps.newHashMap();
		params.put("player", playerName);
		params.put("item", itemName);
		return generate(loreGenerator, params);
	}

	public static String generateName() {
		Map<String, String> params = Maps.newHashMap();
		return generate(heroGenerator, params);
	}

	private static String generate(IGenerator generator, Map<String, String> params) {
		return StringUtils.capitalize(generator.generate(random, params)).replaceAll("\\s+", " ");
	}

	private static IGenerator createHeroGenerator() {
		IGenerator heroesPrefix = alt("Grunnar", "Hermann", "Sven", "Grarg", "Blarf", "Hans", "Nathan", "Oglaf", "Eric", "Bob", "Banan", "Alaric");
		IGenerator heroesPostfix = alt("ish", "ilde", "monkeybutt", "son", "shvili", "berg", "bert", "us");
		IGenerator heroName = word(heroesPrefix, opt(0.6f, heroesPostfix));
		IGenerator heroOptional = alt("slightly", "sometimes", "mistakenly", "somehow", "part-time");
		IGenerator heroAdj = alt("insane", "brave", "smelly", "philosophical", "jumping", "toothless", "burning", "heroic", "shy", "narcoleptic", "manly", "girly", "non-euclidian", "euphoric", "misanthropic", "ambivalent", "fictional", "fetishist");
		IGenerator heroClass = alt("babycrusher", "wrestler", "nitpicker", "barber", "anesthesiologist", "sharpshooter", "plumber", "insurance salesman", "clown", "empiricist", "defenestrator", "visigoth", "nipple twister");
		IGenerator classicHeroes = capitalizeFully(seq(heroName, "the", seq(opt(0.2f, heroOptional), heroAdj, heroClass, opt(0.2f, word("(lvl. ", range(1, 11), ")")))));

		IGenerator firstName = alt("Bill", "Juliet", "Nigel", "Steve", "Parsnip", "Cucumber", "Ludwig", "Markus", "Sven", "Clark", "Carl", "Throatwobbler", "Raymond", "Nancy", "Brian", "Brunhilda", "Richard", "Rupert");
		IGenerator lastNameComponent = alt("Smith", "Weston", "Banana", "Drum", "Forklift", "Ampersand", "Fruitbat", "Fhtagn", "Svenson", "Stein", "Gutenabend", "Mangrove", "Bigglesworth", "Larch", "Semicolon", "Wurst", "Nixon", "Baden", "Priapus");
		IGenerator lastName = alt(lastNameComponent, word(lastNameComponent, "-", lastNameComponent));
		IGenerator pseudonym = alt("Duckie", "Nosepicker", "Snort", "Bomber", "Ouch", "Anvil", "Halfslab", "Radiator", "Barbie", "Biggles", "Income Tax", "Not In Face", "Tea Time", "Twerk", "Mutalisk", "Bueno", "Sixpack", "Yellow Snow");
		IGenerator namePrefix = alt("Dr.", "Rev.", "Ms", "Mr", "Prof.", "Hon.", "Sgt.", "Cmdr.", "Sir", "Lady", "Comrade", "His Magnificence", "Her Holiness", "The Right Honourable");
		IGenerator middleName = alt("W.", "T.", "F.");
		IGenerator nameSuffix = alt("M.Sc", "Ph.D", "OBE", "Jr.", "Sr.", "III", "II", "Esq.");

		final IGenerator middleStuff = seq(opt(0.1f, middleName), opt(0.6f, word("\"", pseudonym, "\"")));
		IGenerator modernHeroes = seq(opt(0.4f, namePrefix),
				firstName,
				middleStuff,
				opt(0.3f, alt("von", "de", "van", "van de", "de la")),
				lastName,
				opt(0.2f, word(" ", nameSuffix)));

		return alt(classicHeroes, modernHeroes);
	}

	private static IGenerator createLoreGenerator() {
		IGenerator adj1 = alt("overpowered", "misspelled", "store-brand", "unsettling", "unremarkable", "sleazy", "boring", "golden", "junky", "ergonomic", "low voltage", "many-angled");
		IGenerator adj2 = alt("cursed", "legendary", "unique", "penultimate", "awesome", "suboptimal", "mighty", "ridiculously", "slightly");
		IGenerator adjs = seq(opt(0.7f, adj2), adj1);
		IGenerator parts = alt("codpiece", "loincloth", "tootbrush", "dental floss", "eggbeater", "rubber chicken with a pulley in the middle", "shovel", "hammoc", "panties", "spatula", "fedora");

		IGenerator placeAdj = alt("deadly", "dreadful", "boring", "cheap", "backwater", "tax-free", "gluten-free", "dark", "evil", "misunderstood");
		IGenerator kingdomAdjective = alt("loathing", "meat", "potatoes", "hydrocarbonates", "sweden", "slighlty unpleasant things", "herpaderp", "sobbing", "knitting");
		IGenerator kingdomish = seq(opt(0.4f, placeAdj), alt("kingdom", "cave", "gorge", "convention", "pit", "bazaar", "land"));
		IGenerator placeWithAdj = seq(kingdomish, "of", kingdomAdjective, opt(0.2f, seq("and", kingdomAdjective)));
		IGenerator mountainName = alt("lard", "butter", "rotten eggs", "brimstone", "newts", "doom", "croc", "flipflop");
		IGenerator mountain = seq(opt(0.6f, placeAdj), "Mt.", mountainName);
		final IGenerator hardcodedPlaces = alt("dalania", "prussia", "foobaria", "hot dog stand", "abyssinia", "zanzibar", "eastasia", "freedonia", "latveria", "woolloomooloo", "breslau", "uzbekistan", "north korea", "lower intestine", "hyperborea");
		IGenerator places = capitalizeFully(alt(placeWithAdj, mountain, hardcodedPlaces));

		IGenerator otherPeople = alt("youtube personalities", "dwarves", "villagers", "elves", "tax collectors", "quality testers", "boring people");
		IGenerator actor = seq(alt(heroGenerator, seq(otherPeople, "of", places)));

		IGenerator story = alt("that nobody cares about", seq("that previously belonged to", actor));
		IGenerator epicLoot = seq(opt(0.5f, adjs), parts, story);

		IGenerator created = seq(alt("repurposed from", "originally bundled with ", "forged from", "not to be mistaken with"), epicLoot);
		IGenerator loaned = seq("loaned to", actor);
		IGenerator forgotten = seq("forgotten in", alt("post office", "loo", "deep hole", "hurry"));
		IGenerator origin = alt(created, seq(alt("stolen", loaned, "imagined", forgotten, "found behind couch"), "by", heroGenerator));

		IGenerator itemModifier = alt("replica of");
		IGenerator itemAction = alt("beating", "bleeding", "winds", "things", word(sub("item", "thing"), "ing"), "cooking", "looting", "scrubing", "backpain", "hernia");
		IGenerator itemType = alt(sub("item", "gizmo"), alt("gizmo", "thingmajig", "doodad", "tat", "thingie"));
		IGenerator item = capitalize(seq(opt(0.9f, adjs), itemType, opt(0.9f, seq("of", itemAction))));
		IGenerator fullItem = seq(opt(0.1f, itemModifier), item, opt(0.05f, seq("(TM)")));

		IGenerator taunt = alt("wimp", "noob", "git", "fool", "that scoundrel", "scumbag");
		IGenerator playerGet = seq(alt("stolen from", "found in", "bought in", "dug out in", "smuggled from"), places);
		IGenerator ownerInfo = seq(playerGet, "by", opt(0.3f, seq(taunt, "named")), sub("player", "Frank"));

		IGenerator randomItems = alt("bananas", "grapes", "hairpins", "corks", "shuffling", "squash", "penguins");
		IGenerator organizationSpeciality = alt(randomItems, seq(randomItems, "and", randomItems));

		IGenerator university = capitalizeFully(seq("university of", alt(hardcodedPlaces, organizationSpeciality)));

		IGenerator institutish = alt("institute", "council", "committee");
		IGenerator institute = capitalizeFully(seq(institutish, "of", organizationSpeciality));

		IGenerator foundationFirst = alt("lick", "pick", "poke", "prod", "smell", "ring", "steal", "hug", "kick", "fwap");
		IGenerator foundationSecond = alt("fish", "sauce", "leopard", "pick", "smell", "mayonaise", "steal", "grave", "derp");
		IGenerator foundation = seq(word(capitalizeFully(foundationFirst), "-a-", capitalizeFully(foundationSecond)), "foundation");

		IGenerator organization = alt(university, foundation, institute);

		IGenerator restoredInfo = seq("restored by", organization);
		IGenerator recent = seq("Recently", alt(ownerInfo, restoredInfo));

		IGenerator extra = alt("$1.99 each", "5 quids in plain wrapper", "Accept no substitues", "Made in China", "Batteries not included", "Patent pending");
		return word(fullItem, opt(0.5f, seq(",", origin)), opt(0.5f, seq(".", recent)), opt(0.2f, seq(".", extra)));
	}

	public static void main(String[] argv) {
		// Left for fun!
		System.out.println("Combination count: " + loreGenerator.count().doubleValue());

		for (int i = 0; i < 50; i++) {
			System.out.println(generateLore("xxx", "yyy"));
		}

		for (int i = 0; i < 50; i++) {
			System.out.println(generateName());
		}
	}
}
