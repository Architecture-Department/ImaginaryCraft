package architecture.imaginarycraft.core

import architecture.imaginarycraft.init.IcCreativeModeTabs
import architecture.imaginarycraft.init.IcItems
import architecture.imaginarycraft.init.IcSoundEvents
import architecture.imaginarycraft.util.IcUtil
import architecture.imaginarycraft.util.IcUtil.LOGGER
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.server.ServerStartingEvent
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(IcUtil.ID)
@EventBusSubscriber
object Ic {
	@SubscribeEvent
	fun onServerStarting(event: ServerStartingEvent) {
		LOGGER.info("HELLO from server starting")
	}

	init {
		val modContainer = LOADING_CONTEXT.activeContainer
		val modBus = MOD_BUS

		IcSoundEvents.REGISTRY.register(modBus)
		IcItems.REGISTRY.register(modBus)
		IcCreativeModeTabs.REGISTRY.register(modBus)
	}
}
