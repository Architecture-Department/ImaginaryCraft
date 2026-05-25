package architecture.imaginarycraft.datagen

import architecture.imaginarycraft.core.ImaginaryCraft
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import java.util.concurrent.CompletableFuture
import architecture.imaginarycraft.core.ImaginaryCraftConstants

/**
 * 创建一个数据包内置条目
 */
class ModDatagenDatapackBuiltinEntries(
	output: PackOutput,
	registries: CompletableFuture<HolderLookup.Provider>,
	datapackEntriesBuilder: RegistrySetBuilder
) : DatapackBuiltinEntriesProvider(output, registries, datapackEntriesBuilder, setOf(ImaginaryCraftConstants.ID))
