package openblocks.rubbish;

import static openmods.words.Words.alt;
import static openmods.words.Words.capitalizeFully;
import static openmods.words.Words.opt;
import static openmods.words.Words.seq;
import static openmods.words.Words.word;

import java.util.Random;

import openmods.words.IGenerator;

import org.apache.commons.lang3.StringUtils;

public class LoreGenerator {

	private static IGenerator root;

	private static final Random random = new Random();

	public static String generateLore() {
		if (root == null) root = createGenerator();
		return StringUtils.capitalize(root.generate(random)).replaceAll("\\s+", " ");
	}

	private static IGenerator createGenerator() {
		IGenerator adj = alt("mighty", "powerful", "misspelled", "penultimate", "awesome", "store-brand", "suboptimal", "slightly unsettling", "unremarkable", "sleazy");
		IGenerator parts = alt("codpiece", "loincloth", "tootbrush", "dental floss", "eggbeater", "rubber chicken with a pulley in the middle");
		IGenerator kingdomAdjective = alt("loathing", "meat", "potatoes", "hydrocarbonates", "sweden", "slighlty unpleasant things", "herpaderp");
		IGenerator kingdomish = alt("kingdom", "cave", "gorge", "convention", "king");
		IGenerator placeWithAdj = seq(kingdomish, "of", kingdomAdjective, opt(0.2f, seq("and", kingdomAdjective)));
		IGenerator places = capitalizeFully(alt(placeWithAdj, "dalania", "prussia", "foobaria"));
		IGenerator heroesPrefix = alt("Grunnar", "Hermann", "Sven", "Grarg", "Blarf", "Zerg", "Hans", "Nathan", "Oglaf", "Eric", "Manly", "Girly");
		IGenerator heroesPostfix = alt("", "ish", "ilde", "monkeybutt", "son", "shvili", "berg");
		IGenerator heroName = word(heroesPrefix, heroesPostfix);
		IGenerator heroOptional = alt("slightly", "sometimes", "mistakenly");
		IGenerator heroAdj = alt("insane", "brave", "smelly", "philosophical", "jumping", "toothless", "burning", "heroic");
		IGenerator heroTitle = alt("babycrusher", "wrestler", "nitpicker", "barber", "anesthesiologist", "sharpshooter", "blorg");
		IGenerator heroes = capitalizeFully(seq(heroName, "the", seq(opt(0.2f, heroOptional), heroAdj, heroTitle)));
		IGenerator otherPeople = alt("youtube personalities", "dwarves", "villagers", "elves", "tax collectors", "quality testers", "boring people");
		IGenerator actor = seq(alt(heroes, otherPeople), opt(0.6f, seq("of", places)));
		IGenerator story = alt("that nobody cares about", seq("that previously belonged to", actor), "($1.99 each)", "v2.0");
		IGenerator epicLoot = seq(parts, story);
		IGenerator forge = seq(alt("repurposed from", "originally bundled with ", "forged from", "not to be mistaken with"), epicLoot);
		IGenerator loaned = seq("loaned to", actor);
		IGenerator forgotten = seq("forgotten in", alt("post office", "loo", "deep hole", "hurry"));
		IGenerator action = seq(alt("stolen", loaned, "coded", forgotten, forge, "found behind couch"), "by", heroes);
		IGenerator item = alt("gizmo", "thingmajig", "doodad", "piece", "tat", "rubbish");

		return word(seq(adj, item, opt(0.5f, action)), ", ", forge);
	}

	public static void main(String[] argv) {
		for (int i = 0; i < 10; i++)
			System.out.println(generateLore());
	}
}
