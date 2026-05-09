package architecture.imaginarycraft.util

import net.minecraft.resources.ResourceLocation

fun resourceLocationOf(id: String, path: String): ResourceLocation {
	return ResourceLocation.fromNamespaceAndPath(id, path)
}
