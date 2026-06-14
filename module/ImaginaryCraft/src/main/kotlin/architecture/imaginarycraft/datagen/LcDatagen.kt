package architecture.imaginarycraft.datagen

import architecture.goldenboughs_lib.util.datagen.buildClient
import architecture.goldenboughs_lib.util.datagen.buildServer
import architecture.imaginarycraft.datagen.i18n.LcZhCn
import architecture.imaginarycraft.util.IcUtil
import net.minecraft.core.RegistrySetBuilder
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

/**
 * 数据生成主类
 */
@EventBusSubscriber(modid = IcUtil.ID)
object LcDatagen {
	@SubscribeEvent
	fun gatherData(event: GatherDataEvent) {
		val generator = event.generator
		val output = generator.packOutput
		val completableFuture = event.lookupProvider

		val existingFileHelper = event.existingFileHelper
		// 服务端数据生成
		event.buildServer(LcDatagenDatapackBuiltinEntries(output, completableFuture, RegistrySetBuilder()))

		// 客户端数据生成
		event.buildClient(LcZhCn(output))
		event.buildClient(LcDatagenParticle(output, existingFileHelper))
		event.buildClient(LcDatagenItemModel(output, existingFileHelper))
		event.buildClient(LcDatagenBlockState(output, existingFileHelper))
		event.buildClient(LcDatagenSoundDefinitionsProvider(output, existingFileHelper))
	}
}
