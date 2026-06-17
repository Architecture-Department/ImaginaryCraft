package architecture.imaginarycraft.datagen

import architecture.goldenboughs_lib.datagen.BasicSoundDefinitionsProvider
import architecture.imaginarycraft.init.IcSoundEvents
import architecture.imaginarycraft.util.IcUtil
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.ExistingFileHelper

open class IcDatagenSoundDefinitionsProvider(
	output: PackOutput,
	existingFileHelper: ExistingFileHelper
) : BasicSoundDefinitionsProvider(output, IcUtil.ID, existingFileHelper) {

	override fun registerSounds() {
		add(IcSoundEvents.SODA_OPEN.get())
		add(IcSoundEvents.SODA_DRINK.get(), 2)
		add(IcSoundEvents.SODA_DRINK_UP.get())
		add(IcSoundEvents.SODA_COLLISION.get())
	}
}
