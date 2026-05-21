package architecture.imaginarycraft.init

import architecture.imaginarycraft.core.ImaginaryCraft
import architecture.imaginarycraft.datagen.i18n.ModZhCn
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModSoundEvents {
	val REGISTRY: DeferredRegister<SoundEvent> = ImaginaryCraft.modRegister(BuiltInRegistries.SOUND_EVENT)

	private fun registerForHolder(id: String, zhName: String, location: String): DeferredHolder<SoundEvent, SoundEvent> {
		val register = REGISTRY.register(id) { ->
			SoundEvent.createVariableRangeEvent(ImaginaryCraft.modRl(location))
		}
		ModZhCn.addI18nSoundEventText(zhName, register)
		return register
	}
}
