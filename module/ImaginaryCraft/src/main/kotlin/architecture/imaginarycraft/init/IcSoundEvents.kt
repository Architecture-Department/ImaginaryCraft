package architecture.imaginarycraft.init

import architecture.imaginarycraft.datagen.i18n.LcZhCn
import architecture.imaginarycraft.util.IcUtil
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object IcSoundEvents {
	@JvmField
	val REGISTRY: DeferredRegister<SoundEvent> = IcUtil.modRegister(BuiltInRegistries.SOUND_EVENT)

	@JvmField
	val SODA_OPEN = registerForHolder("soda.open", "汽水：打开")

	@JvmField
	val SODA_DRINK = registerForHolder("soda.drink", "汽水：饮用")

	@JvmField
	val SODA_DRINK_UP = registerForHolder("soda.drink_up", "汽水：饮尽")

	@JvmField
	val SODA_COLLISION = registerForHolder("soda.collision", "汽水罐：碰撞")

	private fun registerForHolder(
		id: String,
		zhName: String,
		range: Float = 16f,
		newSystem: Boolean = true
	): DeferredHolder<SoundEvent, SoundEvent> {
		val register = REGISTRY.register(id) { ->
			if (newSystem) {
				SoundEvent.createFixedRangeEvent(IcUtil.modRl(id), range)
			} else {
				SoundEvent.createVariableRangeEvent(IcUtil.modRl(id))
			}
		}
		LcZhCn.addI18nSoundEventText(zhName, register)
		return register
	}
}
