package architecture.imaginarycraft.init;

import architecture.goldenboughs_lib.common.particle.SpecialParticleType;
import architecture.imaginarycraft.common.particle.ItemStackParticle;
import architecture.imaginarycraft.util.IcUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 粒子类型
 */
public final class IcParticleTypes {
    public static final DeferredRegister<ParticleType<?>> REGISTRY = IcUtil.modRegister(BuiltInRegistries.PARTICLE_TYPE);

    public static final DeferredHolder<ParticleType<?>, ParticleType<ItemStackParticle.Options>> ITEM_STACK = register(
            "item_stack", true, ItemStackParticle.Options.CODEC, ItemStackParticle.Options.STREAM_CODEC
    );

    private IcParticleTypes() {
    }

    private static <T extends ParticleOptions> DeferredHolder<ParticleType<?>, ParticleType<T>> register(
            String id, boolean overrideLimiter,
            MapCodec<T> mapCodec,
            StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec
    ) {
        return register(id, () -> new SpecialParticleType<>(overrideLimiter, mapCodec, streamCodec));
    }

    @SuppressWarnings("unchecked")
    private static <O extends ParticleType<?>> DeferredHolder<ParticleType<?>, O> register(
            String id, Supplier<O> particleType
    ) {
        return (DeferredHolder<ParticleType<?>, O>) REGISTRY.register(id, particleType);
    }
}
