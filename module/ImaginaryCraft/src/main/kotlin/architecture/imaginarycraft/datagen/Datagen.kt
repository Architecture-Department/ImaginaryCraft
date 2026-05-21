package architecture.imaginarycraft.datagen

import architecture.imaginarycraft.core.ImaginaryCraft
import architecture.imaginarycraft.datagen.i18n.ModZhCn
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.DataGenerator
import net.minecraft.data.DataProvider
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

/**
 * 数据生成主类
 */
@EventBusSubscriber(modid = ImaginaryCraft.ID)
object Datagen {
	@JvmStatic
	@SubscribeEvent
	fun gatherData(event: GatherDataEvent) {
		val generator = event.generator
		val output = generator.packOutput
		val completableFuture = event.lookupProvider

		val existingFileHelper = event.existingFileHelper
		// 服务端数据生成
		buildServer(event, generator, ModDatagenDatapackBuiltinEntries(output, completableFuture, RegistrySetBuilder()))

		// 客户端数据生成
		buildClient(event, generator, ModZhCn(output))
		buildClient(event, generator, ModDatagenParticle(output, existingFileHelper))
		buildClient(event, generator, ModDatagenItemModel(output, existingFileHelper))
		buildClient(event, generator, ModDatagenBlockState(output, existingFileHelper))
		buildClient(event, generator, ModDatagenSoundDefinitionsProvider(output, existingFileHelper))
	}

	private fun <T : DataProvider> buildClient(
		event: GatherDataEvent,
		generator: DataGenerator,
		provider: T
	): T {
		return generator.addProvider(event.includeClient(), provider)
	}

	private fun <T : DataProvider> buildServer(
		event: GatherDataEvent,
		generator: DataGenerator,
		provider: T
	): T {
		return generator.addProvider(event.includeServer(), provider)
	}
}
