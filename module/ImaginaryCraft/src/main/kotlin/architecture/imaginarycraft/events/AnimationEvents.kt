package architecture.imaginarycraft.events

import architecture.imaginarycraft.common.particle.ItemStackParticle
import architecture.imaginarycraft.init.IcParticleTypes
import architecture.imaginarycraft.util.IcUtil
import architecture.resonator_combat_framework.module.entity_animation.event.AnimationParticleEvent
import net.minecraft.core.particles.ParticleType
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import org.joml.Vector3d

@EventBusSubscriber(modid = IcUtil.ID)
object AnimationEvents {
	@SubscribeEvent
	fun onParticlePre(event: AnimationParticleEvent.Pre) {
	}

	@SubscribeEvent
	fun onParticlePost(event: AnimationParticleEvent.Post) {
		val animId = event.animationController.currentAnimId
		val particleId = event.particleId
		val locatorName = event.locatorName
		val particleType = event.particle
		val rotate = event.rotate
		val pos = event.pos
		val holder = event.getHolder()
		if (animId?.path == "player.imaginarycraft.canned_enkephalin") {
			// TODO 等之后再优化
//			cannedEnkephalin(particleType, locatorName, holder, particleId, rotate, pos)
			return
		}
	}

	private fun cannedEnkephalin(
		particleType: ParticleType<*>?,
		locatorName: String,
		holder: Entity,
		particleId: ResourceLocation,
		rotate: Vector3d,
		pos: Vector3d
	) {
		if (particleType != IcParticleTypes.ITEM_STACK.get()) return

		// 将 rotate 映射为初始移动速度
		val speedFactor = 0.02
		val xSpeed = rotate.x * speedFactor
		val ySpeed = rotate.y * speedFactor
		val zSpeed = rotate.z * speedFactor
		val itemStack = when (locatorName) {
			"right_item" -> if (holder is LivingEntity) holder.mainHandItem else holder.weaponItem
			"left_item" -> if (holder is LivingEntity) holder.offhandItem else holder.weaponItem
			else -> if (holder is LivingEntity) holder.mainHandItem else holder.weaponItem
		} ?: ItemStack.EMPTY
		val itemDisplayContext = when (locatorName) {
			"right_item" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
			"left_item" -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND
			else -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
		}

		val options = ItemStackParticle.Options(
			itemStack = itemStack,
			itemDisplayContext = itemDisplayContext,
			xRot = rotate.x,
			yRot = rotate.y,
			zRot = rotate.z,
			enablePhysics = true,
		)
		val level = holder.level()
		if (level.isClientSide) {
			level.addParticle(options, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0)
		} else if (level is ServerLevel) {
//			level.sendParticles(options, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 1.0)
		}
	}
}
