package architecture.imaginarycraft.datagen;

import architecture.imaginarycraft.core.ImaginaryCraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ModDatagenParticle extends ParticleDescriptionProvider {
	public ModDatagenParticle(PackOutput output, ExistingFileHelper fileHelper) {
		super(output, fileHelper);
	}

	private static @NotNull ResourceLocation getPath(String name) {
		return ResourceLocation.fromNamespaceAndPath(ImaginaryCraft.ID, name);
	}

	@Override
	protected void addDescriptions() {
	}

	private <T extends ParticleType<?>> void sprite(Supplier<T> type, String name) {
		sprite(type.get(), ResourceLocation.fromNamespaceAndPath(ImaginaryCraft.ID, name));
	}

	private <T extends ParticleType<?>> void sprite(Supplier<T> type, String... names) {
		spriteSet(type.get(), Arrays.stream(names)
			.map(ModDatagenParticle::getPath)
			.collect(Collectors.toList()));
	}
}
