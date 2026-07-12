package architecture.imaginarycraft.events.registry.client;

import architecture.imaginarycraft.common.particle.ItemStackParticle;
import architecture.imaginarycraft.init.IcParticleTypes;
import architecture.imaginarycraft.util.IcUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = IcUtil.ID, value = Dist.CLIENT)
public final class ParticleProvidersRegistry {
    private ParticleProvidersRegistry() {
    }

    @SubscribeEvent
    public static void registry(RegisterParticleProvidersEvent event) {
        event.registerSpecial(IcParticleTypes.ITEM_STACK.get(), new ItemStackParticle.Provider());
    }
}
