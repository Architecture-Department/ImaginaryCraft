package architecture.imaginarycraft.init;

import architecture.imaginarycraft.datagen.i18n.IcZhCn;
import architecture.imaginarycraft.util.IcUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class IcSoundEvents {
    public static final DeferredRegister<SoundEvent> REGISTRY = IcUtil.modRegister(BuiltInRegistries.SOUND_EVENT);

    /** 汽水：打开 */
    public static final DeferredHolder<SoundEvent, SoundEvent> SODA_OPEN = registerForHolder("soda.open", "汽水：打开");

    /** 汽水：饮用 */
    public static final DeferredHolder<SoundEvent, SoundEvent> SODA_DRINK = registerForHolder("soda.drink", "汽水：饮用");

    /** 汽水：饮尽 */
    public static final DeferredHolder<SoundEvent, SoundEvent> SODA_DRINK_UP = registerForHolder("soda.drink_up", "汽水：饮尽");

    /** 汽水罐：碰撞 */
    public static final DeferredHolder<SoundEvent, SoundEvent> SODA_COLLISION = registerForHolder("soda.collision", "汽水罐：碰撞");

    private IcSoundEvents() {
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerForHolder(
            String id, String zhName, float range, boolean newSystem
    ) {
        var register = REGISTRY.register(id, () -> {
            if (newSystem) {
                return SoundEvent.createFixedRangeEvent(IcUtil.modRl(id), range);
            } else {
                return SoundEvent.createVariableRangeEvent(IcUtil.modRl(id));
            }
        });
        IcZhCn.addI18nSoundEventText(zhName, register);
        return register;
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerForHolder(
            String id, String zhName, float range
    ) {
        return registerForHolder(id, zhName, range, true);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> registerForHolder(
            String id, String zhName
    ) {
        return registerForHolder(id, zhName, 16f, true);
    }
}
