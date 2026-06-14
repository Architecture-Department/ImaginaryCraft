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
	val SODA_OPEN = registerForHolder("soda_open", "汽水：打开", "soda.open")

	@JvmField
	val SODA_DRINK = registerForHolder("soda_drink", "汽水：饮用", "soda.drink")

	@JvmField
	val SODA_DRINK_UP = registerForHolder("soda_drink_up", "汽水：饮尽", "soda.drink_up")

	@JvmField
	val SODA_COLLISION = registerForHolder("soda_collision", "汽水罐：碰撞", "soda.collision")

	private fun registerForHolder(id: String, zhName: String, location: String): DeferredHolder<SoundEvent, SoundEvent> {
		val register = REGISTRY.register(id) { ->
			SoundEvent.createVariableRangeEvent(IcUtil.modRl(location))
		}
		LcZhCn.addI18nSoundEventText(zhName, register)
		return register
	}
}
