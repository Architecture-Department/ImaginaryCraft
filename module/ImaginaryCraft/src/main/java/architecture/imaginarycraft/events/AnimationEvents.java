package architecture.imaginarycraft.events;

import architecture.imaginarycraft.common.particle.ItemStackParticle;
import architecture.imaginarycraft.init.IcParticleTypes;
import architecture.imaginarycraft.util.IcUtil;
import architecture.resonator_combat_framework.event.definition.ParticleEvent;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.joml.Vector3d;

@EventBusSubscriber(modid = IcUtil.ID)
public final class AnimationEvents {
    private AnimationEvents() {
    }

    @SubscribeEvent
    public static void onParticlePre(ParticleEvent.Pre event) {
    }

    @SubscribeEvent
    public static void onParticlePost(ParticleEvent.Post event) {
        ResourceLocation animId = event.getController().getCurrentAnimId();
        ResourceLocation particleId = event.getParticleId();
        String locatorName = event.getLocatorName();
        ParticleType<?> particleType = event.getParticle();
        Vector3d rotate = event.getRotate();
        Vector3d pos = event.getPos();
        Entity holder = event.getHolder();
        if (animId != null && "player.imaginarycraft.canned_enkephalin".equals(animId.getPath())) {
            // TODO 等之后再优化
//            cannedEnkephalin(particleType, locatorName, holder, particleId, rotate, pos);
        }
    }

    @SuppressWarnings("unused")
    private static void cannedEnkephalin(
            ParticleType<?> particleType,
            String locatorName,
            Entity holder,
            ResourceLocation particleId,
            Vector3d rotate,
            Vector3d pos
    ) {
        if (particleType != IcParticleTypes.ITEM_STACK.get()) return;

        // 将 rotate 映射为初始移动速度
        double speedFactor = 0.02;
        double xSpeed = rotate.x * speedFactor;
        double ySpeed = rotate.y * speedFactor;
        double zSpeed = rotate.z * speedFactor;

        ItemStack itemStack;
        if (holder instanceof LivingEntity living) {
            itemStack = switch (locatorName) {
                case "right_item" -> living.getMainHandItem();
                case "left_item" -> living.getOffhandItem();
                default -> living.getMainHandItem();
            };
        } else {
            itemStack = holder.getWeaponItem();
        }
        if (itemStack == null) {
            itemStack = ItemStack.EMPTY;
        }

        ItemDisplayContext itemDisplayContext = switch (locatorName) {
            case "right_item" -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            case "left_item" -> ItemDisplayContext.THIRD_PERSON_LEFT_HAND;
            default -> ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
        };

        ItemStackParticle.Options options = new ItemStackParticle.Options(
                itemStack,
                itemDisplayContext,
                rotate.x,
                rotate.y,
                rotate.z,
                true,
                1.0,
                0.5,
                0.8,
                0.0,
                0.0,
                0.0,
                100
        );
        Level level = holder.level();
        if (level.isClientSide()) {
            level.addParticle(options, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
        } else if (level instanceof ServerLevel) {
//            ((ServerLevel) level).sendParticles(options, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 1.0);
        }
    }
}
