package architecture.imaginarycraft.datagen

import architecture.goldenboughs_lib.util.buildClient
import architecture.goldenboughs_lib.util.buildServer
import architecture.imaginarycraft.core.ImaginaryCraft
import architecture.imaginarycraft.datagen.i18n.ModZhCn
import net.minecraft.core.RegistrySetBuilder
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

/**
 * 数据生成主类
 */
@EventBusSubscriber(modid = ImaginaryCraft.ID)
object ModDatagen {
	@SubscribeEvent
	fun gatherData(event: GatherDataEvent) {
		val generator = event.generator
		val output = generator.packOutput
		val completableFuture = event.lookupProvider

		val existingFileHelper = event.existingFileHelper
		// 服务端数据生成
		event.buildServer(ModDatagenDatapackBuiltinEntries(output, completableFuture, RegistrySetBuilder()))

		// 客户端数据生成
		event.buildClient(ModZhCn(output))
		event.buildClient(ModDatagenParticle(output, existingFileHelper))
		event.buildClient(ModDatagenItemModel(output, existingFileHelper))
		event.buildClient(ModDatagenBlockState(output, existingFileHelper))
		event.buildClient(ModDatagenSoundDefinitionsProvider(output, existingFileHelper))
	}
}
