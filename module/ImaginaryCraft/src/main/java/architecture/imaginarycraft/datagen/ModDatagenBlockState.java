package architecture.imaginarycraft.datagen;

import architecture.imaginarycraft.core.ImaginaryCraft;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class ModDatagenBlockState extends BlockStateProvider {
	public ModDatagenBlockState(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, ImaginaryCraft.ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {

	}
}
