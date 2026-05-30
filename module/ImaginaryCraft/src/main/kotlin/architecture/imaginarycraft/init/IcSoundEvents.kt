package architecture.imaginarycraft.init

import architecture.imaginarycraft.core.ImaginaryCraftConstants
import architecture.imaginarycraft.datagen.i18n.LcZhCn
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object IcSoundEvents {
	val REGISTRY: DeferredRegister<SoundEvent> = ImaginaryCraftConstants.modRegister(BuiltInRegistries.SOUND_EVENT)

	private fun registerForHolder(id: String, zhName: String, location: String): DeferredHolder<SoundEvent, SoundEvent> {
		val register = REGISTRY.register(id) { ->
			SoundEvent.createVariableRangeEvent(ImaginaryCraftConstants.modRl(location))
		}
		LcZhCn.addI18nSoundEventText(zhName, register)
		return register
	}
}
