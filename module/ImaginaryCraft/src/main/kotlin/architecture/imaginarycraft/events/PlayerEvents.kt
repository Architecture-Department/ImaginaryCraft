package architecture.imaginarycraft.events

import architecture.imaginarycraft.init.IcItems
import architecture.imaginarycraft.util.IcUtil
import architecture.resonator_combat_framework.events.registry.AnimationControllers
import architecture.resonator_combat_framework.module.entity_animation.IProxyAnimationProvider.Companion.getAnimationTransformer
import architecture.resonator_combat_framework.module.entity_animation.animation.data.AnimationPlayData
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent

private val CANNED_ENKEPHALIN = IcUtil.modRl("player/canned_enkephalin")

@EventBusSubscriber(modid = IcUtil.ID)
object PlayerEvents {
	@SubscribeEvent
	fun onUseItemStart(event: LivingEntityUseItemEvent.Start) {
		val entity = event.entity
		if (entity !is Player) return
		val itemStack = event.item
		val item = itemStack.item
		val hand = event.hand
		if (entity.level().isClientSide) {
			if (item == IcItems.CANNED_ENKEPHALIN.get()) {
				entity.getAnimationTransformer().trigger(
					AnimationControllers.ACTION,
					CANNED_ENKEPHALIN,
					AnimationPlayData(
						mirror = hand != InteractionHand.MAIN_HAND
					)
				)
				return
			}
		}
	}

	@SubscribeEvent
	fun onUseItemTick(event: LivingEntityUseItemEvent.Tick) {
		val entity = event.entity
		if (entity !is Player) return
		if (entity.level().isClientSide) {
		}
	}

	@SubscribeEvent
	fun onUseItemStop(event: LivingEntityUseItemEvent.Stop) {
		val entity = event.entity
		if (entity !is Player) return
		val itemStack = event.item
		val item = itemStack.item
		if (entity.level().isClientSide) {
			if (item == IcItems.CANNED_ENKEPHALIN.get()) {
				entity.getAnimationTransformer().getController(AnimationControllers.ACTION)?.apply {
					if (equalsCurrentAnimId(CANNED_ENKEPHALIN)) {
						stop()
					}
				}
			}
		}
	}

	@SubscribeEvent
	fun onUseItemFinish(event: LivingEntityUseItemEvent.Finish) {
		val entity = event.entity
		if (entity !is Player) return
		val itemStack = event.item
		val item = itemStack.item
		if (entity.level().isClientSide) {
		}
	}
}