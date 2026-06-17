package architecture.imaginarycraft.datagen

import architecture.goldenboughs_lib.util.datagen.ItemModelUtil.withExistingParent
import architecture.imaginarycraft.init.IcItems
import architecture.imaginarycraft.util.IcUtil
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper

/**
 * 物品模型数据生成器
 * 用于为模组中的物品生成对应的模型文件
 */
class IcDatagenItemModel(output: PackOutput, existingFileHelper: ExistingFileHelper) :
	ItemModelProvider(output, IcUtil.ID, existingFileHelper) {

	override fun registerModels() {
		withExistingParent("item/food/", "_gui", "gui/", "", IcItems.CANNED_ENKEPHALIN)
	}
}
