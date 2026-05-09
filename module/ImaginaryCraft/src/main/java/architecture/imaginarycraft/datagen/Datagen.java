package architecture.imaginarycraft.datagen;

import architecture.imaginarycraft.core.ImaginaryCraft;
import architecture.imaginarycraft.datagen.i18n.ModZhCn;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * 数据生成主类
 */
@EventBusSubscriber(modid = ImaginaryCraft.ID)
public final class Datagen {
	@SubscribeEvent
	public static void gatherData(@NotNull GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<HolderLookup.Provider> completableFuture = event.getLookupProvider();

		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		// 服务端数据生成
		buildServer(event, generator, new ModDatagenDatapackBuiltinEntries(output, completableFuture, new RegistrySetBuilder()));

		// 客户端数据生成
		buildClient(event, generator, new ModZhCn(output));
		buildClient(event, generator, new ModDatagenParticle(output, existingFileHelper));
		buildClient(event, generator, new ModDatagenItemModel(output, existingFileHelper));
		buildClient(event, generator, new ModDatagenBlockState(output, existingFileHelper));
		buildClient(event, generator, new ModDatagenSoundDefinitionsProvider(output, existingFileHelper));
	}

	private static <T extends DataProvider> @NotNull T buildClient(
		@NotNull GatherDataEvent event,
		@NotNull DataGenerator generator,
		T provider
	) {
		return generator.addProvider(event.includeClient(), provider);
	}

	private static <T extends DataProvider> @NotNull T buildServer(
		@NotNull GatherDataEvent event,
		@NotNull DataGenerator generator,
		T provider
	) {
		return generator.addProvider(event.includeServer(), provider);
	}
}
