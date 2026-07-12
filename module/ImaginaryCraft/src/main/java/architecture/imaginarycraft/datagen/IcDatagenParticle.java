package architecture.imaginarycraft.datagen;

import architecture.goldenboughs_lib.datagen.BasicParticleDescriptionProvider;
import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class IcDatagenParticle extends BasicParticleDescriptionProvider {
    public IcDatagenParticle(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper);
    }

    @Override
    protected void addDescriptions() {
    }

    private <T extends ParticleType<?>> void sprite(Supplier<T> type, String name) {
        sprite(type.get(), IcUtil.modRl(name));
    }

    private <T extends ParticleType<?>> void sprite(Supplier<T> type, String... names) {
        spriteSet(
                type.get(),
                Arrays.stream(names)
                        .map(name -> getPath(name))
                        .toList()
        );
    }

    private ResourceLocation getPath(String name) {
        return IcUtil.modRl(name);
    }
}
