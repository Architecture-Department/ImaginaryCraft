package architecture.imaginarycraft.datagen;

import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 创建一个数据包内置条目
 */
public class IcDatagenDatapackBuiltinEntries extends DatapackBuiltinEntriesProvider {
    public IcDatagenDatapackBuiltinEntries(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries,
            RegistrySetBuilder datapackEntriesBuilder
    ) {
        super(output, registries, datapackEntriesBuilder, Set.of(IcUtil.ID));
    }
}
