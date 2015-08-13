package openblocks.client.renderer.tileentity.tank;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidStack;
import openmods.utils.Diagonal;

public class TankRenderUtils {

	private static final float PHASE_PER_DISTANCE = 0.5f;

	private static final float WAVE_AMPLITUDE = 0.01f;

	private static final float WAVE_FREQUENCY = 0.1f;

	public static float clampLevel(float level) {
		if (level <= 0.1f) return 0.1f;
		if (level >= 0.9f) return 1.0f;
		return level;
	}

	public static float calculatePhase(int x, int y, int z, Diagonal diagonal) {
		float posX = x + diagonal.offsetX / 2.0f;
		float posY = y + diagonal.offsetY / 2.0f;
		float posZ = z + diagonal.offsetZ / 2.0f;
		return (posX + posY + posZ) * PHASE_PER_DISTANCE;
	}

	public static float calculatePhase(int x, int y, int z) {
		return (x + y + z) * PHASE_PER_DISTANCE;
	}

	public static float calculateWaveAmplitude(float time, float phase) {
		return MathHelper.sin(time * WAVE_FREQUENCY + phase) * WAVE_AMPLITUDE;
	}

	public static FluidStack safeCopy(FluidStack stack) {
		return stack != null? stack : null;
	}

	public static TileEntity getTileEntitySafe(World world, int x, int y, int z) {
		if (world.blockExists(x, y, z)) {
			Chunk chunk = world.getChunkFromBlockCoords(x, z);
			return chunk.getTileEntityUnsafe(x & 0xF, y, z & 0xF);
		}

		return null;
	}

}
