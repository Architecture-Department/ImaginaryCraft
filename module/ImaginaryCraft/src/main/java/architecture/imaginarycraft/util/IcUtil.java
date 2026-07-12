package architecture.imaginarycraft.util;

import architecture.goldenboughs_lib.util.LibUtil;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;

/**
 * ImaginaryCraft 模组通用工具类。
 */
public final class IcUtil {

    /** 模组 ID */
    public static final String ID = "imaginarycraft";

    /** 模组名称 */
    public static final String NAME = "ImaginaryCraft";

    /** 模组日志记录器 */
    public static final Logger LOGGER = LogManager.getLogger(ID);

    private IcUtil() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }

    /**
     * 根据名称创建模组的 ResourceLocation。
     *
     * @param name 资源名称
     * @return 模组命名空间下的 ResourceLocation
     */
    @Contract("_ -> new")
    public static ResourceLocation modRl(String name) {
        return LibUtil.rlOf(ID, name);
    }

    /**
     * 根据名称创建模组的命名空间字符串。
     *
     * @param name 资源名称
     * @return 模组命名空间下的字符串（格式：{@code modid:name}）
     */
    @Contract(pure = true)
    public static String modRlText(String name) {
        return ID + ":" + name;
    }

    /**
     * 根据 Registry 创建 DeferredRegister。
     *
     * @param registry 注册表
     * @param <T>      注册类型
     * @return DeferredRegister 实例
     */
    public static <T> DeferredRegister<T> modRegister(Registry<T> registry) {
        return DeferredRegister.create(registry, ID);
    }

    /**
     * 根据 ResourceKey 创建 DeferredRegister。
     *
     * @param registry 注册表 ResourceKey
     * @param <T>      注册类型
     * @return DeferredRegister 实例
     */
    public static <T> DeferredRegister<T> modRegister(ResourceKey<Registry<T>> registry) {
        return DeferredRegister.create(registry, ID);
    }
}
