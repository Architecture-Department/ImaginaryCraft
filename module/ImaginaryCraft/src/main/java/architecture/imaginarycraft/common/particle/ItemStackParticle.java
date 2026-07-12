package architecture.imaginarycraft.common.particle;

import architecture.goldenboughs_lib.util.LibUtil;
import architecture.imaginarycraft.init.IcParticleTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemStackParticle extends Particle {

    //region Constants & Static Fields

    public static final List<ItemStackParticle> PARTICLES = new ArrayList<>();

    //endregion

    //region Render Properties

    private ItemStack itemStack = ItemStack.EMPTY;
    private ItemDisplayContext itemDisplayContext = ItemDisplayContext.NONE;
    private double xRot = 0.0;
    private double xRotO = 0.0;
    private double yRot = 0.0;
    private double yRotO = 0.0;
    private double zRot = 0.0;
    private double zRotO = 0.0;
    private double scale = 1.0;
    private double scaleO = 1.0;

    //endregion

    //region Physics Properties

    private boolean enablePhysics = false;
    private double gravityScale = 1.0;
    private double bounce = 0.5;
    private double groundFriction = 0.8;
    private double xRotSpeed = 0.0;
    private double yRotSpeed = 0.0;
    private double zRotSpeed = 0.0;
    private boolean isFadingOut = false;
    private double scaleOnFadeStart = 1.0;

    //endregion

    //region Constructors

    public ItemStackParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        PARTICLES.add(this);
    }

    public ItemStackParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        PARTICLES.add(this);
    }

    //endregion

    //region Getters & Setters

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemDisplayContext getItemDisplayContext() {
        return itemDisplayContext;
    }

    public void setItemDisplayContext(ItemDisplayContext itemDisplayContext) {
        this.itemDisplayContext = itemDisplayContext;
    }

    public double getXRot() {
        return xRot;
    }

    public void setXRot(double xRot) {
        this.xRot = xRot;
    }

    public double getXRotO() {
        return xRotO;
    }

    public void setXRotO(double xRotO) {
        this.xRotO = xRotO;
    }

    public double getYRot() {
        return yRot;
    }

    public void setYRot(double yRot) {
        this.yRot = yRot;
    }

    public double getYRotO() {
        return yRotO;
    }

    public void setYRotO(double yRotO) {
        this.yRotO = yRotO;
    }

    public double getZRot() {
        return zRot;
    }

    public void setZRot(double zRot) {
        this.zRot = zRot;
    }

    public double getZRotO() {
        return zRotO;
    }

    public void setZRotO(double zRotO) {
        this.zRotO = zRotO;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScaleO() {
        return scaleO;
    }

    public void setScaleO(double scaleO) {
        this.scaleO = scaleO;
    }

    public boolean isEnablePhysics() {
        return enablePhysics;
    }

    public void setEnablePhysics(boolean enablePhysics) {
        this.enablePhysics = enablePhysics;
    }

    public double getGravityScale() {
        return gravityScale;
    }

    public void setGravityScale(double gravityScale) {
        this.gravityScale = gravityScale;
    }

    public double getBounce() {
        return bounce;
    }

    public void setBounce(double bounce) {
        this.bounce = bounce;
    }

    public double getGroundFriction() {
        return groundFriction;
    }

    public void setGroundFriction(double groundFriction) {
        this.groundFriction = groundFriction;
    }

    public double getXRotSpeed() {
        return xRotSpeed;
    }

    public void setXRotSpeed(double xRotSpeed) {
        this.xRotSpeed = xRotSpeed;
    }

    public double getYRotSpeed() {
        return yRotSpeed;
    }

    public void setYRotSpeed(double yRotSpeed) {
        this.yRotSpeed = yRotSpeed;
    }

    public double getZRotSpeed() {
        return zRotSpeed;
    }

    public void setZRotSpeed(double zRotSpeed) {
        this.zRotSpeed = zRotSpeed;
    }

    public boolean isFadingOut() {
        return isFadingOut;
    }

    public void setFadingOut(boolean fadingOut) {
        isFadingOut = fadingOut;
    }

    //endregion

    //region Interpolated Query Methods

    public double getYRot(float partialTicks) {
        return Mth.lerp((double) partialTicks, yRotO, yRot);
    }

    public double getXRot(float partialTicks) {
        return Mth.lerp((double) partialTicks, xRotO, xRot);
    }

    public double getZRot(float partialTicks) {
        return Mth.lerp((double) partialTicks, zRotO, zRot);
    }

    public double getY(float partialTicks) {
        return Mth.lerp((double) partialTicks, yo, y);
    }

    public double getX(float partialTicks) {
        return Mth.lerp((double) partialTicks, zo, x);
    }

    public double getZ(float partialTicks) {
        return Mth.lerp((double) partialTicks, zo, z);
    }

    public double getScale(float partialTicks) {
        return Mth.lerp((double) partialTicks, scaleO, scale);
    }

    //endregion

    //region Render

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        // Empty implementation
    }

    public void render(PoseStack poseStack, Camera camera, float partialTicks, MultiBufferSource bufferSource) {
        poseStack.pushPose();
        int combinedLight = getLightColor(partialTicks);
        int combinedOverlay = OverlayTexture.NO_OVERLAY;
        var itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.translate((float) getX(partialTicks), (float) getY(partialTicks), (float) getZ(partialTicks));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) getXRot(partialTicks)));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) getYRot(partialTicks)));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) getZRot(partialTicks)));
        float s = (float) getScale(partialTicks);
        poseStack.scale(s, s, s);
        itemRenderer.renderStatic(itemStack, itemDisplayContext, combinedLight, combinedOverlay, poseStack, bufferSource, level, 0);
        poseStack.popPose();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }

    //endregion

    //region Tick

    @Override
    public void tick() {
        xRotO = xRot;
        yRotO = yRot;
        zRotO = zRot;
        scaleO = scale;

        if (enablePhysics) {
            tickPhysics();
        } else {
            super.tick();
        }
    }

    private void tickPhysics() {
        xo = x;
        yo = y;
        zo = z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        double prevX = x;
        double prevY = y;
        double prevZ = z;

        this.yd -= gravityScale * (double) this.gravity;
        this.move(this.xd, this.yd, this.zd);

        if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
        }

        this.xd *= (double) this.friction;
        this.yd *= (double) this.friction;
        this.zd *= (double) this.friction;
        if (this.onGround) {
            this.xd *= 0.7;
            this.zd *= 0.7;
        }

        xRot += xRotSpeed;
        yRot += yRotSpeed;
        zRot += zRotSpeed;

        boolean collidedX = x == prevX && xd != 0.0;
        boolean collidedY = y == prevY && yd != 0.0;
        boolean collidedZ = z == prevZ && zd != 0.0;

        if (collidedX || collidedY || collidedZ) {
            onCollision(collidedX, collidedY, collidedZ);
        }

        if (collidedY) yd = -yd * bounce;
        if (collidedX) xd = -xd * bounce;
        if (collidedZ) zd = -zd * bounce;

        if (isFadingOut) {
            double progress = (double) age / (double) lifetime;
            scale = scaleOnFadeStart * (1.0 - progress);
        }
    }

    //endregion

    //region Collision

    public void onCollision(boolean collidedX, boolean collidedY, boolean collidedZ) {
        if (!isFadingOut) {
            isFadingOut = true;
            scaleOnFadeStart = scale;
        }
    }

    //endregion

    //region Lifecycle

    @Override
    public void remove() {
        super.remove();
        PARTICLES.remove(this);
    }

    //endregion

    //region Inner Types

    public static class Provider implements ParticleProvider<Options> {
        @Override
        public Particle createParticle(Options type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ItemStackParticle particle = new ItemStackParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.itemStack = type.itemStack();
            particle.itemDisplayContext = type.itemDisplayContext();
            particle.xRot = type.xRot();
            particle.xRotO = type.xRot();
            particle.yRot = type.yRot();
            particle.yRotO = type.yRot();
            particle.zRot = type.zRot();
            particle.zRotO = type.zRot();
            particle.enablePhysics = type.enablePhysics();
            particle.gravityScale = type.gravityScale();
            particle.bounce = type.bounce();
            particle.groundFriction = type.groundFriction();
            particle.xRotSpeed = type.xRotSpeed();
            particle.yRotSpeed = type.yRotSpeed();
            particle.zRotSpeed = type.zRotSpeed();
            particle.lifetime = type.lifetime();
            return particle;
        }
    }

    public record Options(
            ItemStack itemStack,
            ItemDisplayContext itemDisplayContext,
            double xRot,
            double yRot,
            double zRot,
            boolean enablePhysics,
            double gravityScale,
            double bounce,
            double groundFriction,
            double xRotSpeed,
            double yRotSpeed,
            double zRotSpeed,
            int lifetime
    ) implements ParticleOptions {

        public static final MapCodec<Options> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ItemStack.CODEC.fieldOf("itemStack").forGetter(Options::itemStack),
                        ItemDisplayContext.CODEC.fieldOf("itemDisplayContext").forGetter(Options::itemDisplayContext),
                        Codec.DOUBLE.optionalFieldOf("xRot", 0.0).forGetter(Options::xRot),
                        Codec.DOUBLE.optionalFieldOf("yRot", 0.0).forGetter(Options::yRot),
                        Codec.DOUBLE.optionalFieldOf("zRot", 0.0).forGetter(Options::zRot),
                        Codec.BOOL.optionalFieldOf("enablePhysics", false).forGetter(Options::enablePhysics),
                        Codec.DOUBLE.optionalFieldOf("gravityScale", 1.0).forGetter(Options::gravityScale),
                        Codec.DOUBLE.optionalFieldOf("bounce", 0.5).forGetter(Options::bounce),
                        Codec.DOUBLE.optionalFieldOf("groundFriction", 0.8).forGetter(Options::groundFriction),
                        Codec.DOUBLE.optionalFieldOf("xRotSpeed", 0.0).forGetter(Options::xRotSpeed),
                        Codec.DOUBLE.optionalFieldOf("yRotSpeed", 0.0).forGetter(Options::yRotSpeed),
                        Codec.DOUBLE.optionalFieldOf("zRotSpeed", 0.0).forGetter(Options::zRotSpeed),
                        Codec.INT.optionalFieldOf("lifetime", 100).forGetter(Options::lifetime)
                ).apply(instance, Options::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, Options> STREAM_CODEC = StreamCodec.of(
                (buf, p) -> {
                    ItemStack.STREAM_CODEC.encode(buf, p.itemStack());
                    LibUtil.ITEM_DISPLAY_CONTEXT_STREAM_CODEC.encode(buf, p.itemDisplayContext());
                    ByteBufCodecs.DOUBLE.encode(buf, p.xRot());
                    ByteBufCodecs.DOUBLE.encode(buf, p.yRot());
                    ByteBufCodecs.DOUBLE.encode(buf, p.zRot());
                    ByteBufCodecs.BOOL.encode(buf, p.enablePhysics());
                    ByteBufCodecs.DOUBLE.encode(buf, p.gravityScale());
                    ByteBufCodecs.DOUBLE.encode(buf, p.bounce());
                    ByteBufCodecs.DOUBLE.encode(buf, p.groundFriction());
                    ByteBufCodecs.DOUBLE.encode(buf, p.xRotSpeed());
                    ByteBufCodecs.DOUBLE.encode(buf, p.yRotSpeed());
                    ByteBufCodecs.DOUBLE.encode(buf, p.zRotSpeed());
                    ByteBufCodecs.INT.encode(buf, p.lifetime());
                },
                buf -> new Options(
                        ItemStack.STREAM_CODEC.decode(buf),
                        LibUtil.ITEM_DISPLAY_CONTEXT_STREAM_CODEC.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.BOOL.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.DOUBLE.decode(buf),
                        ByteBufCodecs.INT.decode(buf)
                )
        );

        /**
         * 使用默认值创建 Options。默认 lifetime 为 100 ticks（5 秒）。
         */
        public static Options of(ItemStack itemStack) {
            return new Options(itemStack, ItemDisplayContext.NONE, 0.0, 0.0, 0.0, false, 1.0, 0.5, 0.8, 0.0, 0.0, 0.0, 100);
        }

        @Override
        public ParticleType<?> getType() {
            return IcParticleTypes.ITEM_STACK.get();
        }
    }

    //endregion
}
