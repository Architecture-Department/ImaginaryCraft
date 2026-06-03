package architecture.imaginarycraft.events.client

import architecture.resonator_combat_framework.core.RcfConstants
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent

@EventBusSubscriber(modid = RcfConstants.ID, value = [Dist.CLIENT])
object PlayerEvents {
	@SubscribeEvent
	fun onUseItemFinish(event: LivingEntityUseItemEvent.Finish) {
		if (event.entity !is Player) return
	}
}