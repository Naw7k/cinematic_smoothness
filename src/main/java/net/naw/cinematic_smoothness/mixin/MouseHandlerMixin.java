package net.naw.cinematic_smoothness.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.util.SmoothDouble;
import net.naw.cinematic_smoothness.Cinematic_smoothness;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Shadow(remap = false)
    @Final
    private Minecraft minecraft;

    @Redirect(
            method = "*",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;smoothCamera:Z", remap = false),
            remap = false
    )
    private boolean redirectSmoothCameraToggle(Options options) {
        return options.smoothCamera;
    }

    @Redirect(
            method = "*",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/SmoothDouble;getNewDeltaValue(DD)D", remap = false),
            remap = false
    )
    private double onGetNewDeltaValue(SmoothDouble instance, double originalRawValue, double originalWeight) {
        if (minecraft.options.smoothCamera) {
            // // THE FIX: Define the 'active' value for the math
            // // If Extended Range is OFF, we clamp the math between 0.0 and 1.0
            // // so the "Ghost" 200% or -70% effect stops immediately.
            double s = Cinematic_smoothness.config.fineControl
                    ? Cinematic_smoothness.config.smoothness
                    : Math.max(0.0, Math.min(1.0, Cinematic_smoothness.config.smoothness));

            // // Absolute OFF check (using our 'active' s)
            if (s <= -1.05) return originalRawValue;
            if (!Cinematic_smoothness.config.fineControl && s <= 0.0) return originalRawValue;

            double multiplier;
            if (s < 0) {
                // // Mapping negatives to ultra-light smoothing
                multiplier = 1.0 + (Math.abs(s) * 49.0);
            } else {
                // // Positive range (0% to 200%)
                double weightFactor = s * s;
                multiplier = 5.0 - (weightFactor * 4.0);
                if (multiplier < 0.1) multiplier = 0.1;
            }

            return instance.getNewDeltaValue(originalRawValue, originalWeight * multiplier);
        }
        return instance.getNewDeltaValue(originalRawValue, originalWeight);
    }
}