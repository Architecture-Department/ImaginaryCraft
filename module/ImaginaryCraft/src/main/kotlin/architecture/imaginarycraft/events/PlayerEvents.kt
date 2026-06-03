package architecture.imaginarycraft.events

import architecture.resonator_combat_framework.core.RcfConstants
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent

@EventBusSubscriber(modid = RcfConstants.ID)
object PlayerEvents {
	@SubscribeEvent
	fun onClientChatReceivedPlayer(event: ClientChatReceivedEvent.Player) {
	}
}