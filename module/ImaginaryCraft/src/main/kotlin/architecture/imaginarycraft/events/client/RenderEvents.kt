package architecture.imaginarycraft.events.client

import architecture.ego_equipment.util.EGOEquipmentUtil
import architecture.imaginarycraft.common.particle.ItemStackParticle
import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

@EventBusSubscriber(modid = EGOEquipmentUtil.ID, value = [Dist.CLIENT])
object RenderEvents {
	@SubscribeEvent
	fun onRenderAfterParticles(event: RenderLevelStageEvent) {
		if (event.stage != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return
		val poseStack = event.poseStack
		val minecraft = Minecraft.getInstance()
		val timer = minecraft.timer
		val partialTicks = timer.getGameTimeDeltaPartialTick(false)
		val camera = event.camera
		val bufferSource = minecraft.renderBuffers().bufferSource()
		poseStack.pushPose()
		val position = camera.position
		poseStack.translate(-position.x, -position.y, -position.z)
		ItemStackParticle.PARTICLES.forEach { it.render(poseStack, camera, partialTicks, bufferSource) }
		poseStack.popPose()
	}
}