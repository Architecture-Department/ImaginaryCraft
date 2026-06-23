package architecture.imaginarycraft.events.registry.client

import architecture.imaginarycraft.common.particle.ItemStackParticle
import architecture.imaginarycraft.init.IcParticleTypes
import architecture.imaginarycraft.util.IcUtil
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent

@EventBusSubscriber(modid = IcUtil.ID, value = [Dist.CLIENT])
object ParticleProvidersRegistry {
	@SubscribeEvent
	fun registry(event: RegisterParticleProvidersEvent) {
		event.registerSpecial(IcParticleTypes.ITEM_STACK.get(), ItemStackParticle.Provider())
	}
}