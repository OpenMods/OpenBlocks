package openblocks.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntitySelectionHandler {

	public interface ISelectAware {}

	public interface ISelectionRenderer<E extends Entity> {
		void render(E e, PlayerEntity player, RenderGlobal context, float partialTickTime);
	}

	public static class RegisterSelectionRendererEvent<I extends Entity & ISelectAware> extends Event {
		public Class<I> cls;
		public ISelectionRenderer<I> renderer;
	}

	public static <I extends Entity & ISelectAware> void registerRenderer(Class<I> cls, ISelectionRenderer<I> renderer) {
		RegisterSelectionRendererEvent<I> evt = new RegisterSelectionRendererEvent<>();
		evt.cls = cls;
		evt.renderer = renderer;
		MinecraftForge.EVENT_BUS.post(evt);
	}

	private final Map<Class<? extends Entity>, ISelectionRenderer<Entity>> registry = Maps.newIdentityHashMap();

	@SubscribeEvent
	public void renderEvents(RenderWorldLastEvent evt) {
		final Minecraft mc = Minecraft.getMinecraft();

		if (mc.objectMouseOver != null) {
			final Entity target = mc.objectMouseOver.entityHit;
			if (target instanceof ISelectAware) {
				ISelectionRenderer<Entity> renderer = registry.get(mc.objectMouseOver.entityHit.getClass());
				if (renderer != null) renderer.render(target, mc.player, evt.getContext(), evt.getPartialTicks());
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("unchecked")
	public void handleRegister(RegisterSelectionRendererEvent<?> evt) {
		registry.put(evt.cls, (ISelectionRenderer<Entity>)evt.renderer);
	}
}
