package architecture.imaginarycraft.events

import architecture.imaginarycraft.init.IcItems
import architecture.imaginarycraft.util.IcUtil
import architecture.resonator_combat_framework.events.registry.AnimationControllers
import architecture.resonator_combat_framework.module.entity_animation.animation.data.AnimationPlayData
import architecture.resonator_combat_framework.module.entity_animation.mixed.IAnimationProxyProvider.Companion.getAnimationTransformer
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent

@EventBusSubscriber(modid = IcUtil.ID)
object PlayerEvents {
	/**
	 * 开始使用物品
	 */
	@SubscribeEvent
	fun onUseItemStart(event: LivingEntityUseItemEvent.Start) {
		val entity = event.entity
		if (entity !is Player) return
		val itemStack = event.item
		val item = itemStack.item
		if (entity.level().isClientSide) {
			if (item == IcItems.CANNED_ENKEPHALIN.get()) {
				entity.getAnimationTransformer().trigger(
					AnimationPlayData(
						"player.imaginarycraft.canned_enkephalin", controllerName = AnimationControllers.ACTION
					)
				)
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

	/**
	 * 暂停使用物品
	 */
	@SubscribeEvent
	fun onUseItemStop(event: LivingEntityUseItemEvent.Stop) {
		val entity = event.entity
		if (entity !is Player) return
		val itemStack = event.item
		val item = itemStack.item
		if (entity.level().isClientSide) {
			if (item == IcItems.CANNED_ENKEPHALIN.get()) {
				entity.getAnimationTransformer().getController(AnimationControllers.ACTION)?.apply {
					if (!equalsCurrentAnimId("player.imaginarycraft.canned_enkephalin")) return
					stop()
				}
			}
		}
	}

	/**
	 * 物品使用完成
	 */
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