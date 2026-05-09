package architecture.imaginarycraft.datagen;

import architecture.imaginarycraft.core.ImaginaryCraft;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import org.jetbrains.annotations.NotNull;

public class ModDatagenSoundDefinitionsProvider extends SoundDefinitionsProvider {
	public ModDatagenSoundDefinitionsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, ImaginaryCraft.ID, existingFileHelper);
	}

	public static @NotNull String getSubtitle(SoundEvent soundEvent) {
		return getSubtitle(soundEvent.getLocation());
	}

	public static @NotNull String getSubtitle(ResourceLocation location) {
		return "sound." + location.toLanguageKey();
	}

	@Override
	public void registerSounds() {
	}

	private void add(SoundEvent soundEvent, float volume, float pitch, int weight, int attenuationDistance) {
		add(soundEvent, 1, volume, pitch, weight, attenuationDistance);
	}

	private void add(SoundEvent soundEvent, int amount, float volume, float pitch, int weight, int attenuationDistance) {
		ResourceLocation location = soundEvent.getLocation();
		assert amount > 0 : "sund : " + location + " amount must be greater than 0";
		SoundDefinition definition = SoundDefinition.definition();
		for (int i = 0; i < amount; i++) {
			definition.with(SoundDefinition.Sound.sound(location.withSuffix(i > 0 ? String.valueOf(i) : ""), SoundDefinition.SoundType.SOUND)
				.volume(volume)
				.pitch(pitch)
				.weight(weight)
				.attenuationDistance(attenuationDistance)
			);
		}
		add(soundEvent, definition
			.subtitle(getSubtitle(location))
			.replace(true)
		);
	}
}
