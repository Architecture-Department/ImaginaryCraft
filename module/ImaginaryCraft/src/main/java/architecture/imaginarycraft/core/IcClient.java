package architecture.imaginarycraft.core;

import architecture.imaginarycraft.util.IcUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * ImaginaryCraft 模组客户端入口。
 */
@Mod(value = IcUtil.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = IcUtil.ID, value = Dist.CLIENT)
public final class IcClient {

    /**
     * 模组客户端构造函数，NeoForge 自动注入 mod 事件总线。
     *
     * @param modBus mod 事件总线
     */
    public IcClient(IEventBus modBus) {
        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (minecraft, parent) -> new ConfigurationScreen(parent)
        );
    }

    /**
     * 客户端初始化事件处理。
     *
     * @param event 客户端初始化事件
     */
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        IcUtil.LOGGER.info("Client {}", IcUtil.NAME);
    }
}
