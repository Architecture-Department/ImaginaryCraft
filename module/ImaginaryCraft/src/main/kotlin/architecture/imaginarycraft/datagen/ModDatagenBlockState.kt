package architecture.imaginarycraft.datagen

import architecture.imaginarycraft.core.ImaginaryCraft
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import architecture.imaginarycraft.core.ImaginaryCraftConstants

class ModDatagenBlockState(output: PackOutput, exFileHelper: ExistingFileHelper) :
	BlockStateProvider(output, ImaginaryCraftConstants.ID, exFileHelper) {

	override fun registerStatesAndModels() {
	}
}
