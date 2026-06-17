package architecture.imaginarycraft.datagen

import architecture.imaginarycraft.util.IcUtil
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

class IcDatagenBlockState(output: PackOutput, exFileHelper: ExistingFileHelper) :
	BlockStateProvider(output, IcUtil.ID, exFileHelper) {

	override fun registerStatesAndModels() {
	}
}
