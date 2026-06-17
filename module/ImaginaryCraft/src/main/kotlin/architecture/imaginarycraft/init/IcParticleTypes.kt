package architecture.imaginarycraft.init

import architecture.goldenboughs_lib.common.particle.SpecialParticleType
import architecture.imaginarycraft.common.particle.ItemStackParticle
import architecture.imaginarycraft.util.IcUtil.modRegister
import com.mojang.serialization.MapCodec
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

/**
 * 粒子类型
 */
object IcParticleTypes {
	@JvmField
	val REGISTRY: DeferredRegister<ParticleType<*>> = modRegister(BuiltInRegistries.PARTICLE_TYPE)

	@JvmField
	val ITEM_STACK = register(
		"item_stack", true, ItemStackParticle.Options.CODEC, ItemStackParticle.Options.STREAM_CODEC
	)

	private fun <T : ParticleOptions> register(
		id: String, overrideLimiter: Boolean, mapCodec: MapCodec<T>, streamCodec: StreamCodec<in RegistryFriendlyByteBuf, T>
	): DeferredHolder<ParticleType<*>, ParticleType<T>> {
		return register(id) { SpecialParticleType(overrideLimiter, mapCodec, streamCodec) }
	}

	private fun <O : ParticleType<*>> register(
		id: String, particleType: Supplier<O>
	): DeferredHolder<ParticleType<*>, O> {
		return REGISTRY.register(id, particleType)
	}
}
