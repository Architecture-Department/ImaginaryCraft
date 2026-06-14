package architecture.imaginarycraft.mixin;

import architecture.imaginarycraft.init.IcItems;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin {
	@WrapOperation(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
	private void imaginarycraft$eat(
		Level instance,
		Player player, double x, double y,
		double z, SoundEvent sound,
		SoundSource category,
		float volume,
		float pitch,
		Operation<Void> original,
		@Local(argsOnly = true) ItemStack food
	) {
		if (food.is(IcItems.CANNED_ENKEPHALIN.get())) {
			return;
		}
		original.call(instance, player, x, y, z, sound, category, volume, pitch);
	}
}
