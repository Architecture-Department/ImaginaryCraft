package architecture.imaginarycraft.events;

import architecture.imaginarycraft.init.IcItems;
import architecture.imaginarycraft.util.IcUtil;
import architecture.resonator_combat_framework.animation.IAnimationProvider;
import architecture.resonator_combat_framework.animation.data.PlayConfig;
import architecture.resonator_combat_framework.init.RcfAnimationControllers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = IcUtil.ID)
public final class PlayerEvents {
	private static final ResourceLocation CANNED_ENKEPHALIN = IcUtil.modRl("player/canned_enkephalin");

	private PlayerEvents() {
	}

	@SubscribeEvent
	public static void onUseItemStart(LivingEntityUseItemEvent.Start event) {
		if (!(event.getEntity() instanceof Player entity)) return;
		var itemStack = event.getItem();
		var item = itemStack.getItem();
		var hand = event.getHand();
		if (entity.level().isClientSide()) {
			if (item == IcItems.CANNED_ENKEPHALIN.get()) {
				IAnimationProvider.Companion.getMapperProvider(entity)
					.getController(RcfAnimationControllers.ACTION)
					.trigger(CANNED_ENKEPHALIN, new PlayConfig.Build().mirror(hand != InteractionHand.MAIN_HAND).build());
			}
		}
	}

	@SubscribeEvent
	public static void onUseItemTick(LivingEntityUseItemEvent.Tick event) {
		if (!(event.getEntity() instanceof Player entity)) return;
		if (entity.level().isClientSide()) {
		}
	}

	@SubscribeEvent
	public static void onUseItemStop(LivingEntityUseItemEvent.Stop event) {
		if (!(event.getEntity() instanceof Player entity)) return;
		var itemStack = event.getItem();
		var item = itemStack.getItem();
		if (entity.level().isClientSide()) {
			if (item == IcItems.CANNED_ENKEPHALIN.get()) {
				var controller = IAnimationProvider.Companion.getMapperProvider(entity)
					.getController(RcfAnimationControllers.ACTION);
				if (controller != null && controller.equalsCurrentAnimId(CANNED_ENKEPHALIN)) {
					controller.stop(-1);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
		if (!(event.getEntity() instanceof Player entity)) return;
		var itemStack = event.getItem();
		var item = itemStack.getItem();
		if (entity.level().isClientSide()) {
		}
	}
}
