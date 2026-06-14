package architecture.imaginarycraft.datagen

import architecture.imaginarycraft.util.IcUtil
import net.minecraft.core.HolderLookup
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import java.util.concurrent.CompletableFuture

/**
 * 创建一个数据包内置条目
 */
class LcDatagenDatapackBuiltinEntries(
	output: PackOutput,
	registries: CompletableFuture<HolderLookup.Provider>,
	datapackEntriesBuilder: RegistrySetBuilder
) : DatapackBuiltinEntriesProvider(output, registries, datapackEntriesBuilder, setOf(IcUtil.ID))
