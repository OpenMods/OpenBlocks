package openblocks.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;

@Cancelable
public class GraveSpawnEvent extends PlayerEvent {

	public List<EntityItem> loot;

	public IChatComponent clickText;

	public String gravestoneText;

	private int x;

	private int y;

	private int z;

	private boolean hasLocation;

	public GraveSpawnEvent(EntityPlayer player, int x, int y, int z, List<EntityItem> loot, String gravestoneText, IChatComponent clickText) {
		super(player);
		this.loot = loot;
		this.gravestoneText = gravestoneText;
		this.clickText = clickText;

		this.hasLocation = true;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public GraveSpawnEvent(EntityPlayer player, List<EntityItem> loot, String gravestoneText, IChatComponent clickText) {
		super(player);
		this.loot = loot;
		this.gravestoneText = gravestoneText;
		this.clickText = clickText;

		this.hasLocation = false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public boolean hasLocation() {
		return hasLocation;
	}

	public void setPosition(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.hasLocation = true;
	}

	public void setX(int x) {
		this.x = x;
		this.hasLocation = true;
	}

	public void setY(int y) {
		this.y = y;
		this.hasLocation = true;
	}

	public void setZ(int z) {
		this.z = z;
		this.hasLocation = true;
	}

}
