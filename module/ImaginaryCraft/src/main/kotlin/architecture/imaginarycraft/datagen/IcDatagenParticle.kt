package architecture.imaginarycraft.datagen

import architecture.goldenboughs_lib.datagen.BasicParticleDescriptionProvider
import architecture.imaginarycraft.util.IcUtil
import net.minecraft.core.particles.ParticleType
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors

class IcDatagenParticle(output: PackOutput, fileHelper: ExistingFileHelper) :
	BasicParticleDescriptionProvider(output, fileHelper) {

	override fun addDescriptions() {
	}

	private fun <T : ParticleType<*>> sprite(type: Supplier<T>, name: String) {
		sprite(type.get(), IcUtil.modRl(name))
	}

	private fun <T : ParticleType<*>> sprite(type: Supplier<T>, vararg names: String) {
		spriteSet(
			type.get(),
			Arrays.stream(names)
				.map { name -> getPath(name) }
				.collect(Collectors.toList())
		)
	}

	private fun getPath(name: String): ResourceLocation {
		return IcUtil.modRl(name)
	}
}
