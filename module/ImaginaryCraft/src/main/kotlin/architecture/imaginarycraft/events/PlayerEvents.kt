package architecture.imaginarycraft.events

import architecture.resonator_combat_framework.core.RcfConstants
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent

@EventBusSubscriber(modid = RcfConstants.ID)
object PlayerEvents {
	@SubscribeEvent
	fun onUseItemFinish(event: LivingEntityUseItemEvent.Finish) {
		if (event.entity !is Player) return
	}

	@SubscribeEvent
	fun onUseItemStart(event: LivingEntityUseItemEvent.Start) {
		if (event.entity !is Player) return
	}

	@SubscribeEvent
	fun onUseItemStop(event: LivingEntityUseItemEvent.Stop) {
		if (event.entity !is Player) return
	}

	@SubscribeEvent
	fun onUseItemTick(event: LivingEntityUseItemEvent.Tick) {
		if (event.entity !is Player) return
	}
}