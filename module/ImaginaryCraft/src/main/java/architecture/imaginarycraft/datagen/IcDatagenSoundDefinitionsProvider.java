package architecture.imaginarycraft.datagen;

import architecture.goldenboughs_lib.datagen.BasicSoundDefinitionsProvider;
import architecture.imaginarycraft.init.IcSoundEvents;
import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class IcDatagenSoundDefinitionsProvider extends BasicSoundDefinitionsProvider {
    public IcDatagenSoundDefinitionsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, IcUtil.ID, existingFileHelper);
    }

    @Override
    public void registerSounds() {
        add(IcSoundEvents.SODA_OPEN.get(), 0.5f, 0.5f, 1, 8);
        add(IcSoundEvents.SODA_DRINK.get(), 2, 0.5f, 0.5f, 1, 8);
        add(IcSoundEvents.SODA_DRINK_UP.get(), 0.5f, 0.5f, 1, 8);
        add(IcSoundEvents.SODA_COLLISION.get(), 0.5f, 0.5f, 1, 8);
    }
}
