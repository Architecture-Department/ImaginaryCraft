package architecture.imaginarycraft.core;

import architecture.imaginarycraft.init.IcCreativeModeTabs;
import architecture.imaginarycraft.init.IcItems;
import architecture.imaginarycraft.init.IcParticleTypes;
import architecture.imaginarycraft.init.IcSoundEvents;
import architecture.imaginarycraft.util.IcUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(IcUtil.ID)
@EventBusSubscriber
public final class Ic {
	public Ic(IEventBus modBus, ModContainer container) {
		IcSoundEvents.REGISTRY.register(modBus);
		IcParticleTypes.REGISTRY.register(modBus);
		IcItems.REGISTRY.register(modBus);
		IcCreativeModeTabs.REGISTRY.register(modBus);
	}

	@SubscribeEvent
	public static void onServerStarting(ServerStartingEvent event) {
		IcUtil.LOGGER.info("HELLO from server starting");
	}
}
