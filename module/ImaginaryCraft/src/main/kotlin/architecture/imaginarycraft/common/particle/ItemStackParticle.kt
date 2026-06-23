package architecture.imaginarycraft.common.particle

import architecture.goldenboughs_lib.api.AllOpe
import architecture.goldenboughs_lib.util.LibUtil.ITEM_DISPLAY_CONTEXT_STREAM_CODEC
import architecture.imaginarycraft.init.IcParticleTypes
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.Mth
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

// TODO 修复粒子问题
@AllOpe
class ItemStackParticle : Particle {

	//region 静态列表

	companion object {
		@JvmStatic
		val PARTICLES = mutableListOf<ItemStackParticle>()
	}

	//endregion

	//region 渲染属性

	var itemStack: ItemStack = ItemStack.EMPTY
	var itemDisplayContext: ItemDisplayContext = ItemDisplayContext.NONE
	var xRot: Double = 0.0
	var xRotO: Double = 0.0
	var yRot: Double = 0.0
	var yRotO: Double = 0.0
	var zRot: Double = 0.0
	var zRotO: Double = 0.0

	/** 渲染缩放比例（1.0=原始大小，可用于淡出缩小等效果） */
	var scale: Double = 1.0

	/** 上一帧的渲染缩放比例，用于插值 */
	var scaleO: Double = 1.0

	//endregion

	//region 物理属性

	/** 是否启用物理效果（重力、碰撞、弹力、摩擦、角速度等） */
	var enablePhysics: Boolean = false

	/** 重力加速度倍数，默认 1.0 为标准重力 */
	var gravityScale: Double = 1.0

	/** 弹力系数，0=无弹力，1=完全弹性，默认 0.5 */
	var bounce: Double = 0.5

	/** 地面摩擦力，默认 0.8（每次 tick 速度乘以该值） */
	var groundFriction: Double = 0.8

	/** X 轴角速度（度/tick） */
	var xRotSpeed: Double = 0.0

	/** Y 轴角速度（度/tick） */
	var yRotSpeed: Double = 0.0

	/** Z 轴角速度（度/tick） */
	var zRotSpeed: Double = 0.0

	/** 是否已进入碰撞淡出状态 */
	var isFadingOut: Boolean = false

	/** 进入淡出时的缩放值 */
	private var scaleOnFadeStart: Double = 1.0

	//endregion

	//region 构造器

	constructor(
		level: ClientLevel,
		x: Double,
		y: Double,
		z: Double,
		xSpeed: Double,
		ySpeed: Double,
		zSpeed: Double
	) : super(level, x, y, z, xSpeed, ySpeed, zSpeed) {
		PARTICLES.add(this)
	}

	constructor(
		level: ClientLevel,
		x: Double,
		y: Double,
		z: Double
	) : super(level, x, y, z) {
		PARTICLES.add(this)
	}

	//endregion

	//region 插值查询

	fun getYRot(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), yRotO, yRot)

	fun getXRot(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), xRotO, xRot)

	fun getZRot(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), zRotO, zRot)

	fun getY(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), yo, y)

	fun getX(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), zo, x)

	fun getZ(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), zo, z)

	fun getScale(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), scaleO, scale)

	//endregion

	//region 渲染

	override fun render(
		buffer: VertexConsumer,
		camera: Camera,
		partialTicks: Float
	) {
	}

	fun render(
		poseStack: PoseStack,
		camera: Camera,
		partialTicks: Float,
		bufferSource: MultiBufferSource
	) {
		poseStack.pushPose()
		val combinedLight = getLightColor(partialTicks)
		val combinedOverlay = OverlayTexture.NO_OVERLAY
		val itemRenderer = Minecraft.getInstance().itemRenderer

		poseStack.translate(
			getX(partialTicks).toFloat(),
			getY(partialTicks).toFloat(),
			getZ(partialTicks).toFloat()
		)
		poseStack.mulPose(Axis.XP.rotationDegrees(getXRot(partialTicks).toFloat()))
		poseStack.mulPose(Axis.YP.rotationDegrees(getYRot(partialTicks).toFloat()))
		poseStack.mulPose(Axis.ZP.rotationDegrees(getZRot(partialTicks).toFloat()))
		val s = getScale(partialTicks).toFloat()
		poseStack.scale(s, s, s)
		itemRenderer.renderStatic(
			itemStack,
			itemDisplayContext,
			combinedLight,
			combinedOverlay,
			poseStack,
			bufferSource,
			level,
			0
		)
		poseStack.popPose()
	}

	override fun getRenderType(): ParticleRenderType = ParticleRenderType.NO_RENDER

	//endregion

	//region Tick

	override fun tick() {
		xRotO = xRot
		yRotO = yRot
		zRotO = zRot
		scaleO = scale

		if (enablePhysics) {
			tickPhysics()
		} else {
			super.tick()
		}
	}

	/**
	 * 物理 tick 逻辑：重力、角速度、碰撞、反弹、摩擦、淡出缩小。
	 */
	private fun tickPhysics() {
		xo = x
		yo = y
		zo = z

		if (this.age++ >= this.lifetime) {
			this.remove()
			return
		}

		val prevX = x
		val prevY = y
		val prevZ = z

		this.yd -= gravityScale * this.gravity.toDouble()
		this.move(this.xd, this.yd, this.zd)
		if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
			this.xd *= 1.1
			this.zd *= 1.1
		}

		this.xd *= this.friction.toDouble()
		this.yd *= this.friction.toDouble()
		this.zd *= this.friction.toDouble()
		if (this.onGround) {
			this.xd *= 0.7
			this.zd *= 0.7
		}

		xRot += xRotSpeed
		yRot += yRotSpeed
		zRot += zRotSpeed

		val collidedX = x == prevX && xd != 0.0
		val collidedY = y == prevY && yd != 0.0
		val collidedZ = z == prevZ && zd != 0.0

		if (collidedX || collidedY || collidedZ) {
			onCollision(collidedX, collidedY, collidedZ)
		}

		if (collidedY) yd = -yd * bounce
		if (collidedX) xd = -xd * bounce
		if (collidedZ) zd = -zd * bounce

		if (isFadingOut) {
			val progress = age.toDouble() / lifetime.toDouble()
			scale = scaleOnFadeStart * (1.0 - progress)
		}
	}

	//endregion

	//region 碰撞回调

	/**
	 * 粒子碰撞时调用（仅在 [enablePhysics] 为 true 时触发）。
	 * 默认行为：触发缩小淡出（不立即移除）。
	 * 子类可重写此方法实现自定义碰撞行为。
	 */
	fun onCollision(collidedX: Boolean, collidedY: Boolean, collidedZ: Boolean) {
		if (!isFadingOut) {
			isFadingOut = true
			scaleOnFadeStart = scale
		}
	}

	//endregion

	//region 生命周期

	override fun remove() {
		super.remove()
		PARTICLES.remove(this)
	}

	//endregion

	//region Provider

	class Provider : ParticleProvider<Options> {
		override fun createParticle(
			type: Options,
			level: ClientLevel,
			x: Double,
			y: Double,
			z: Double,
			xSpeed: Double,
			ySpeed: Double,
			zSpeed: Double
		): Particle {
			return ItemStackParticle(level, x, y, z, xSpeed, ySpeed, zSpeed).apply {
				itemStack = type.itemStack
				itemDisplayContext = type.itemDisplayContext
				xRot = type.xRot
				xRotO = type.xRot
				yRot = type.yRot
				yRotO = type.yRot
				zRot = type.zRot
				zRotO = type.zRot
				enablePhysics = type.enablePhysics
				gravityScale = type.gravityScale
				bounce = type.bounce
				groundFriction = type.groundFriction
				xRotSpeed = type.xRotSpeed
				yRotSpeed = type.yRotSpeed
				zRotSpeed = type.zRotSpeed
				lifetime = type.lifetime
			}
		}
	}

	//endregion

	//region Options

	/** Options 不包含 scale/scaleO，它们是运行时状态，由粒子本地管理。 */
	@JvmRecord
	data class Options(
		val itemStack: ItemStack,
		val itemDisplayContext: ItemDisplayContext = ItemDisplayContext.NONE,
		val xRot: Double = 0.0,
		val yRot: Double = 0.0,
		val zRot: Double = 0.0,
		val enablePhysics: Boolean = false,
		val gravityScale: Double = 1.0,
		val bounce: Double = 0.5,
		val groundFriction: Double = 0.8,
		val xRotSpeed: Double = 0.0,
		val yRotSpeed: Double = 0.0,
		val zRotSpeed: Double = 0.0,
		val lifetime: Int = 20 * 5
	) : ParticleOptions {
		companion object {

			@JvmField
			val CODEC: MapCodec<Options> = RecordCodecBuilder.mapCodec { instance ->
				instance.group(
					ItemStack.CODEC.fieldOf("itemStack").forGetter { o: Options -> o.itemStack },
					ItemDisplayContext.CODEC.fieldOf("itemDisplayContext").forGetter { o: Options -> o.itemDisplayContext },
					Codec.DOUBLE.optionalFieldOf("xRot", 0.0).forGetter { o: Options -> o.xRot },
					Codec.DOUBLE.optionalFieldOf("yRot", 0.0).forGetter { o: Options -> o.yRot },
					Codec.DOUBLE.optionalFieldOf("zRot", 0.0).forGetter { o: Options -> o.zRot },
					Codec.BOOL.optionalFieldOf("enablePhysics", false).forGetter { o: Options -> o.enablePhysics },
					Codec.DOUBLE.optionalFieldOf("gravityScale", 1.0).forGetter { o: Options -> o.gravityScale },
					Codec.DOUBLE.optionalFieldOf("bounce", 0.5).forGetter { o: Options -> o.bounce },
					Codec.DOUBLE.optionalFieldOf("groundFriction", 0.8).forGetter { o: Options -> o.groundFriction },
					Codec.DOUBLE.optionalFieldOf("xRotSpeed", 0.0).forGetter { o: Options -> o.xRotSpeed },
					Codec.DOUBLE.optionalFieldOf("yRotSpeed", 0.0).forGetter { o: Options -> o.yRotSpeed },
					Codec.DOUBLE.optionalFieldOf("zRotSpeed", 0.0).forGetter { o: Options -> o.zRotSpeed },
					Codec.INT.optionalFieldOf("lifetime", 20 * 5).forGetter { o: Options -> o.lifetime }
				).apply(instance, ::Options)
			}

			@JvmField
			val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Options> = StreamCodec.of(
				{ buf, p ->
					ItemStack.STREAM_CODEC.encode(buf, p.itemStack)
					ITEM_DISPLAY_CONTEXT_STREAM_CODEC.encode(buf, p.itemDisplayContext)
					ByteBufCodecs.DOUBLE.encode(buf, p.xRot)
					ByteBufCodecs.DOUBLE.encode(buf, p.yRot)
					ByteBufCodecs.DOUBLE.encode(buf, p.zRot)
					ByteBufCodecs.BOOL.encode(buf, p.enablePhysics)
					ByteBufCodecs.DOUBLE.encode(buf, p.gravityScale)
					ByteBufCodecs.DOUBLE.encode(buf, p.bounce)
					ByteBufCodecs.DOUBLE.encode(buf, p.groundFriction)
					ByteBufCodecs.DOUBLE.encode(buf, p.xRotSpeed)
					ByteBufCodecs.DOUBLE.encode(buf, p.yRotSpeed)
					ByteBufCodecs.DOUBLE.encode(buf, p.zRotSpeed)
					ByteBufCodecs.INT.encode(buf, p.lifetime)
				}, { buf ->
					Options(
						ItemStack.STREAM_CODEC.decode(buf),
						ITEM_DISPLAY_CONTEXT_STREAM_CODEC.decode(buf),
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
				}
			)
		}

		override fun getType(): ParticleType<*> = IcParticleTypes.ITEM_STACK.get()
	}

	//endregion
}
