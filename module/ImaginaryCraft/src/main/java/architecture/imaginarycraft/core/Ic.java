package architecture.imaginarycraft.core;

import architecture.imaginarycraft.init.IcCreativeModeTabs;
import architecture.imaginarycraft.init.IcItems;
import architecture.imaginarycraft.init.IcParticleTypes;
import architecture.imaginarycraft.init.IcSoundEvents;
import architecture.imaginarycraft.util.IcUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

/**
 * ImaginaryCraft 模组主入口。
 */
@Mod(IcUtil.ID)
@EventBusSubscriber
public final class Ic {

    /**
     * 模组构造函数，NeoForge 自动注入 mod 事件总线。
     *
     * @param modBus mod 事件总线，用于注册 DeferredRegister
     */
    public Ic(IEventBus modBus) {
        IcSoundEvents.REGISTRY.register(modBus);
        IcParticleTypes.REGISTRY.register(modBus);
        IcItems.REGISTRY.register(modBus);
        IcCreativeModeTabs.REGISTRY.register(modBus);
    }

    /**
     * 服务端启动事件处理。
     *
     * @param event 服务端启动事件
     */
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        IcUtil.LOGGER.info("HELLO from server starting");
    }
}
