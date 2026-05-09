package architecture.imaginarycraft.core;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ImaginaryCraft.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ImaginaryCraft.ID, value = Dist.CLIENT)
public final class ImaginaryCraftClient {

	public ImaginaryCraftClient(ModContainer container) {
		container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
	}

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		ImaginaryCraft.LOGGER.info("Client {}", ImaginaryCraft.NAME);
	}
}
