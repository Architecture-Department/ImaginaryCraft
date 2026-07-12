package architecture.imaginarycraft.datagen;

import architecture.imaginarycraft.datagen.i18n.IcZhCn;
import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.core.RegistrySetBuilder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * 数据生成主类
 */
@EventBusSubscriber(modid = IcUtil.ID)
public final class IcDatagen {
    private IcDatagen() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var completableFuture = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();

        // 服务端数据生成
        generator.addProvider(
                event.includeServer(),
                new IcDatagenDatapackBuiltinEntries(output, completableFuture, new RegistrySetBuilder())
        );

        // 客户端数据生成
        generator.addProvider(event.includeClient(), new IcZhCn(output));
        generator.addProvider(event.includeClient(), new IcDatagenParticle(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new IcDatagenItemModel(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new IcDatagenBlockState(output, existingFileHelper));
        generator.addProvider(event.includeClient(), new IcDatagenSoundDefinitionsProvider(output, existingFileHelper));
    }
}
