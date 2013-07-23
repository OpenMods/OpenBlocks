package openblocks.utils;

import net.minecraftforge.common.ForgeDirection;

public class MathUtils {

    public static final double lengthSq(double x, double y, double z) {
        return (x * x) + (y * y) + (z * z);
    }

    public static final double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }
    
}
