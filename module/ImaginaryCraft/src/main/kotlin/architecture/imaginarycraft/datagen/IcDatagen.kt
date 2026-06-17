package architecture.imaginarycraft.datagen

import architecture.goldenboughs_lib.util.datagen.buildClient
import architecture.goldenboughs_lib.util.datagen.buildServer
import architecture.imaginarycraft.datagen.i18n.IcZhCn
import architecture.imaginarycraft.util.IcUtil
import net.minecraft.core.RegistrySetBuilder
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

/**
 * 数据生成主类
 */
@EventBusSubscriber(modid = IcUtil.ID)
object IcDatagen {
	@SubscribeEvent
	fun gatherData(event: GatherDataEvent) {
		val generator = event.generator
		val output = generator.packOutput
		val completableFuture = event.lookupProvider

		val existingFileHelper = event.existingFileHelper
		// 服务端数据生成
		event.buildServer(IcDatagenDatapackBuiltinEntries(output, completableFuture, RegistrySetBuilder()))

		// 客户端数据生成
		event.buildClient(IcZhCn(output))
		event.buildClient(IcDatagenParticle(output, existingFileHelper))
		event.buildClient(IcDatagenItemModel(output, existingFileHelper))
		event.buildClient(IcDatagenBlockState(output, existingFileHelper))
		event.buildClient(IcDatagenSoundDefinitionsProvider(output, existingFileHelper))
	}
}
