package openblocks.api;

import java.util.Collection;

import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.structure.MapGenStructure;

public interface IStructureGenProvider {
	public boolean canUseOnProvider(IChunkProvider provider);

	public Collection<MapGenStructure> listProviders(IChunkProvider provider);
}