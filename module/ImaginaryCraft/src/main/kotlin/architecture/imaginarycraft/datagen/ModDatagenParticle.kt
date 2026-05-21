package architecture.imaginarycraft.datagen

import architecture.imaginarycraft.core.ImaginaryCraft
import net.minecraft.core.particles.ParticleType
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider
import java.util.*
import java.util.function.Supplier
import java.util.stream.Collectors

class ModDatagenParticle(output: PackOutput, fileHelper: ExistingFileHelper) :
	ParticleDescriptionProvider(output, fileHelper) {

	override fun addDescriptions() {
	}

	private fun <T : ParticleType<*>> sprite(type: Supplier<T>, name: String) {
		sprite(type.get(), ResourceLocation.fromNamespaceAndPath(ImaginaryCraft.ID, name))
	}

	private fun <T : ParticleType<*>> sprite(type: Supplier<T>, vararg names: String) {
		spriteSet(
			type.get(),
			Arrays.stream(names)
				.map { name: String -> getPath(name) }
				.collect(Collectors.toList())
		)
	}

	companion object {
		private fun getPath(name: String): ResourceLocation {
			return ResourceLocation.fromNamespaceAndPath(ImaginaryCraft.ID, name)
		}
	}
}
