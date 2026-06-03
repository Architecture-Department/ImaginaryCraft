package architecture.imaginarycraft.events.client

import architecture.resonator_combat_framework.core.RcfConstants
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent

@EventBusSubscriber(modid = RcfConstants.ID, value = [Dist.CLIENT])
object PlayerEvents {
	@SubscribeEvent
	fun onClientChatReceivedPlayer(event: ClientChatReceivedEvent.Player) {
	}
}