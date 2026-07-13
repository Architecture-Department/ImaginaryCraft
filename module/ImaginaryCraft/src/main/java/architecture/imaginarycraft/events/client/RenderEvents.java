package architecture.imaginarycraft.events.client;

import architecture.imaginarycraft.common.particle.ItemStackParticle;
import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = IcUtil.ID, value = Dist.CLIENT)
public final class RenderEvents {
	private RenderEvents() {
	}

	@SubscribeEvent
	public static void onRenderAfterParticles(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
		var poseStack = event.getPoseStack();
		var minecraft = Minecraft.getInstance();
		var timer = minecraft.getTimer();
		var partialTicks = timer.getGameTimeDeltaPartialTick(false);
		var camera = event.getCamera();
		var bufferSource = minecraft.renderBuffers().bufferSource();
		poseStack.pushPose();
		var position = camera.getPosition();
		poseStack.translate(-position.x, -position.y, -position.z);
		ItemStackParticle.PARTICLES.forEach(p -> p.render(poseStack, camera, partialTicks, bufferSource));
		poseStack.popPose();
	}
}
