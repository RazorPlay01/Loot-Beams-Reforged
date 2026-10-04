package com.github.razorplay.lootbeams.events;

public interface PresetEvents {
	SimpleEvent<ApplyPreset> APPLY_PRESET = new SimpleEvent<>(listeners -> presetName -> {
		for (ApplyPreset event : listeners) {
			event.onApplyPreset(presetName);
		}
	});

	interface ApplyPreset {
		void onApplyPreset(String presetName);
	}
}
