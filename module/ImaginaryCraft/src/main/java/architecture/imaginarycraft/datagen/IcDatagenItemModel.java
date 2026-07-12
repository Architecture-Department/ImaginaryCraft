package architecture.imaginarycraft.datagen;

import architecture.goldenboughs_lib.util.datagen.ItemModelUtil;
import architecture.imaginarycraft.init.IcItems;
import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 物品模型数据生成器
 * 用于为模组中的物品生成对应的模型文件
 */
public class IcDatagenItemModel extends ItemModelProvider {
    public IcDatagenItemModel(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, IcUtil.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ItemModelUtil.withExistingParent(this, "item/food/", "_gui", "gui/", "", IcItems.CANNED_ENKEPHALIN);
    }
}
