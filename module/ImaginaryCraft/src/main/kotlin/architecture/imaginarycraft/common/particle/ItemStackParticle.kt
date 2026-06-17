package architecture.imaginarycraft.common.particle

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

class ItemStackParticle : Particle {
	companion object {
		@JvmStatic
		val PARTICLES = mutableListOf<ItemStackParticle>()
	}

	var itemStack: ItemStack = ItemStack.EMPTY
	var itemDisplayContext: ItemDisplayContext = ItemDisplayContext.NONE
	var xRot: Double = 0.0
	var xRotO: Double = 0.0
	var yRot: Double = 0.0
	var yRotO: Double = 0.0
	var zRot: Double = 0.0
	var zRotO: Double = 0.0

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

	fun getYRot(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), yRot, yRotO)

	fun getXRot(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), xRot, xRotO)

	fun getZRot(partialTicks: Float): Double =
		Mth.lerp(partialTicks.toDouble(), zRot, zRotO)

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

		poseStack.translate(x, y, z)
		poseStack.mulPose(Axis.XP.rotationDegrees(getXRot(partialTicks).toFloat()))
		poseStack.mulPose(Axis.YP.rotationDegrees(getYRot(partialTicks).toFloat()))
		poseStack.mulPose(Axis.ZP.rotationDegrees(getZRot(partialTicks).toFloat()))
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

	override fun tick() {
		this.xRotO = this.xRot
		this.yRotO = this.yRot
		this.zRotO = this.zRot
		super.tick()
	}

	override fun remove() {
		super.remove()
		PARTICLES.remove(this)
	}

	override fun getRenderType(): ParticleRenderType = ParticleRenderType.NO_RENDER

	@JvmRecord
	data class Options(
		val itemStack: ItemStack,
		val itemDisplayContext: ItemDisplayContext,
		val xRot: Double,
		val xRotO: Double,
		val yRot: Double,
		val yRotO: Double,
		val zRot: Double,
		val zRotO: Double,
	) : ParticleOptions {
		companion object {

			@JvmField
			val CODEC: MapCodec<Options> = RecordCodecBuilder.mapCodec { instance ->
				instance.group(
					ItemStack.CODEC.fieldOf("itemStack").forGetter { it.itemStack },
					ItemDisplayContext.CODEC.fieldOf("itemDisplayContext").forGetter { it.itemDisplayContext },
					Codec.DOUBLE.fieldOf("xRot").forGetter { it.xRot },
					Codec.DOUBLE.fieldOf("xRotO").forGetter { it.xRotO },
					Codec.DOUBLE.fieldOf("yRot").forGetter { it.yRot },
					Codec.DOUBLE.fieldOf("yRotO").forGetter { it.yRotO },
					Codec.DOUBLE.fieldOf("zRot").forGetter { it.zRot },
					Codec.DOUBLE.fieldOf("zRotO").forGetter { it.zRotO },
				).apply(instance, ::Options)
			}

			@JvmField
			val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Options> = StreamCodec.of(
				{ buf, p ->
					ItemStack.STREAM_CODEC.encode(buf, p.itemStack)
					ITEM_DISPLAY_CONTEXT_STREAM_CODEC.encode(buf, p.itemDisplayContext)
					ByteBufCodecs.DOUBLE.encode(buf, p.xRot)
					ByteBufCodecs.DOUBLE.encode(buf, p.xRotO)
					ByteBufCodecs.DOUBLE.encode(buf, p.yRot)
					ByteBufCodecs.DOUBLE.encode(buf, p.yRotO)
					ByteBufCodecs.DOUBLE.encode(buf, p.zRot)
					ByteBufCodecs.DOUBLE.encode(buf, p.zRotO)
				}, { buf ->
					Options(
						ItemStack.STREAM_CODEC.decode(buf),
						ITEM_DISPLAY_CONTEXT_STREAM_CODEC.decode(buf),
						ByteBufCodecs.DOUBLE.decode(buf),
						ByteBufCodecs.DOUBLE.decode(buf),
						ByteBufCodecs.DOUBLE.decode(buf),
						ByteBufCodecs.DOUBLE.decode(buf),
						ByteBufCodecs.DOUBLE.decode(buf),
						ByteBufCodecs.DOUBLE.decode(buf),
					)
				}
			)
		}

		override fun getType(): ParticleType<*> = IcParticleTypes.ITEM_STACK.get()
	}
}