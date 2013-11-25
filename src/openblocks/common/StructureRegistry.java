package openblocks.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderHell;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraftforge.event.ForgeSubscribe;
import openblocks.api.IStructureGenProvider;
import openblocks.api.IStructureNamer;
import openblocks.api.events.RegisterStructureGenProvider;
import openblocks.api.events.RegisterStructureIdentifierEvent;
import openmods.Log;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;

public class StructureRegistry {

	private List<IStructureNamer> names = Lists.newArrayList();

	@ForgeSubscribe
	public void registerIdentifier(RegisterStructureIdentifierEvent evt) {
		if (evt.namer != null) names.add(evt.namer);
	}

	private String identifyStructure(MapGenStructure structure) {
		for (IStructureNamer n : names) {
			String name = n.identify(structure);
			if (!Strings.isNullOrEmpty(name)) return name;
		}
		return structure.func_143025_a();
	}

	private static <T> void addMapGen(Collection<MapGenStructure> output, Class<T> klazz, T provider, String... names) {
		try {
			MapGenStructure struct = ReflectionHelper.getPrivateValue(klazz, provider, names);
			if (struct != null) output.add(struct);
		} catch (UnableToAccessFieldException e) {
			Log.warn(e, "Can't access fields %s from provider %s. Some structures may not be detected", Arrays.toString(names), provider);
		}
	}

	private List<IStructureGenProvider> providers;

	{
		ImmutableList.Builder<IStructureGenProvider> builder = ImmutableList.builder();

		builder.add(new IStructureGenProvider() {
			@Override
			public boolean canUseOnProvider(IChunkProvider provider) {
				return provider instanceof ChunkProviderGenerate;
			}

			@Override
			public Collection<MapGenStructure> listProviders(IChunkProvider provider) {
				ChunkProviderGenerate cp = (ChunkProviderGenerate)provider;
				List<MapGenStructure> result = Lists.newArrayList();
				addMapGen(result, ChunkProviderGenerate.class, cp, "strongholdGenerator", "field_73225_u");
				addMapGen(result, ChunkProviderGenerate.class, cp, "villageGenerator", "field_73224_v");
				addMapGen(result, ChunkProviderGenerate.class, cp, "mineshaftGenerator", "field_73223_w");
				addMapGen(result, ChunkProviderGenerate.class, cp, "scatteredFeatureGenerator", "field_73233_x");
				return result;
			}
		});

		builder.add(new IStructureGenProvider() {
			@Override
			public boolean canUseOnProvider(IChunkProvider provider) {
				return provider instanceof ChunkProviderFlat;
			}

			@Override
			public Collection<MapGenStructure> listProviders(IChunkProvider provider) {
				ChunkProviderFlat cp = (ChunkProviderFlat)provider;
				List<MapGenStructure> result = Lists.newArrayList();
				try {
					List<MapGenStructure> gen = ReflectionHelper.getPrivateValue(ChunkProviderFlat.class, cp, "structureGenerators", "field_82696_f");
					if (gen != null) result.addAll(gen);
				} catch (UnableToAccessFieldException e) {
					Log.warn(e, "Can't access map gen list from provider %s. Some structures may not be detected", provider);
				}
				return result;
			}
		});

		builder.add(new IStructureGenProvider() {
			@Override
			public boolean canUseOnProvider(IChunkProvider provider) {
				return provider instanceof ChunkProviderHell;
			}

			@Override
			public Collection<MapGenStructure> listProviders(IChunkProvider provider) {
				ChunkProviderHell cp = (ChunkProviderHell)provider;
				List<MapGenStructure> result = Lists.newArrayList();
				addMapGen(result, ChunkProviderHell.class, cp, "genNetherBridge", "field_73172_c");
				return result;
			}
		});
		providers = builder.build();
	}

	@ForgeSubscribe
	public void registerProvider(RegisterStructureGenProvider evt) {
		if (evt.provider != null) providers.add(evt.provider);
	}

	public final static StructureRegistry instance = new StructureRegistry();

	private StructureRegistry() {}

	public Map<String, ChunkPosition> getNearestStructures(WorldServer world, int x, int y, int z) {
		ChunkProviderServer provider = world.theChunkProviderServer;
		try {
			IChunkProvider inner = ReflectionHelper.getPrivateValue(ChunkProviderServer.class, provider, "currentChunkProvider", "field_73246_d");
			if (inner != null) {
				for (IStructureGenProvider p : providers) {
					if (p.canUseOnProvider(inner)) {
						Map<String, ChunkPosition> result = Maps.newHashMap();
						for (MapGenStructure struct : p.listProviders(inner)) {
							try {
								ChunkPosition structPos = struct.getNearestInstance(world, x, y, z);

								if (structPos != null) {
									String structType = identifyStructure(struct);
									if (!Strings.isNullOrEmpty(structType)) result.put(structType, structPos);
								}
							} catch (IndexOutOfBoundsException e) {
								// bug in MC, just ignore
								// fixed by magic of ASM
							}
						}
						return result;
					}
				}
			}
		} catch (UnableToAccessFieldException e) {
			Log.warn(e, "Can't access chunk provider data. No structures will be detected");
		}

		return ImmutableMap.of();
	}
}
