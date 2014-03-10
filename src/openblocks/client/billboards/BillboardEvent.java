package openblocks.client.billboards;

import openblocks.client.Icons.IDrawableIcon;

class BillboardEvent {
	
	public final float x, y, z;
	public final IDrawableIcon icon;
	public final double size;

	private double time;
	private final double timeDeltaPerTick;

	BillboardEvent(float x, float y, float z, IDrawableIcon icon, double size, double TTL) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.icon = icon;
		this.size = size;

		time = 1;
		timeDeltaPerTick = 1 / (TTL * 20);
	}

	public void update() {
		time -= timeDeltaPerTick;
	}

	public boolean isAlive() {
		return time >= 0;
	}

	public double getTime(double partialTick) {
		return time - timeDeltaPerTick * partialTick;
	}
}
