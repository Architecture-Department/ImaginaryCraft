package architecture.imaginarycraft.datagen;

import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class IcDatagenBlockState extends BlockStateProvider {
    public IcDatagenBlockState(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, IcUtil.ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
    }
}
