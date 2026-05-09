package architecture.imaginarycraft.datagen;

import architecture.imaginarycraft.core.ImaginaryCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 创建一个数据包内置条目
 */
public final class ModDatagenDatapackBuiltinEntries extends DatapackBuiltinEntriesProvider {

	public ModDatagenDatapackBuiltinEntries(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder datapackEntriesBuilder) {
		super(output, registries, datapackEntriesBuilder, Set.of(ImaginaryCraft.ID));
	}
}
