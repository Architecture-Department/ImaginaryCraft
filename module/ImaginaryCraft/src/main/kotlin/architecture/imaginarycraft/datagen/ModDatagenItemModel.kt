package architecture.imaginarycraft.datagen

import architecture.goldenboughs_lib.util.client.DatagenItemModelUtil.withExistingParent
import architecture.imaginarycraft.core.ImaginaryCraft
import architecture.imaginarycraft.init.IcItems
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import architecture.imaginarycraft.core.ImaginaryCraftConstants

/**
 * 物品模型数据生成器
 * 用于为模组中的物品生成对应的模型文件
 */
class ModDatagenItemModel(output: PackOutput, existingFileHelper: ExistingFileHelper) :
	ItemModelProvider(output, ImaginaryCraftConstants.ID, existingFileHelper) {

	override fun registerModels() {
		withExistingParent("_gui", IcItems.CANNED_ENKEPHALIN)
	}
}
