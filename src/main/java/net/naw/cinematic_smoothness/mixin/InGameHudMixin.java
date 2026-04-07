package net.naw.cinematic_smoothness.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.DeltaTracker;
import net.naw.cinematic_smoothness.Cinematic_smoothness;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {
    @Shadow(remap = false)
    @Final
    private Minecraft minecraft;

    @Unique
    private float barProgress = 0.0f;

    // // FIXED: Now checks if Smooth Camera (F2) is actually ON before hiding
    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderCrosshair(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.options.smoothCamera && Cinematic_smoothness.config.hideCrosshair) {
            ci.cancel();
        }
    }

    // // 1. THE "BEHIND" INJECTION (Runs before the Hotbar)
    @Inject(method = "extractRenderState", at = @At("HEAD"), remap = false)
    private void renderBarsBehind(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (Cinematic_smoothness.config.showHudWithBars) {
            drawBars(graphics, deltaTracker);
        }
    }

    // // 2. THE "IN-FRONT" INJECTION (Runs after everything else)
    @Inject(method = "extractRenderState", at = @At("TAIL"), remap = false)
    private void renderBarsInFront(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!Cinematic_smoothness.config.showHudWithBars) {
            drawBars(graphics, deltaTracker);
        }
    }

    @Unique
    private void drawBars(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        float slideSpeed = 0.15f;
        boolean shouldShow = this.minecraft.options.smoothCamera && Cinematic_smoothness.config.showBlackBars;

        if (shouldShow && barProgress < 1.0f) {
            barProgress += slideSpeed * deltaTracker.getGameTimeDeltaTicks();
            if (barProgress > 1.0f) barProgress = 1.0f;
        } else if (!shouldShow && barProgress > 0.0f) {
            barProgress -= slideSpeed * deltaTracker.getGameTimeDeltaTicks();
            if (barProgress < 0.0f) barProgress = 0.0f;
        }

        if (barProgress > 0.0f) {
            int width = graphics.guiWidth();
            int height = graphics.guiHeight();
            int maxBarHeight = height / 8;
            int currentBarHeight = (int) (maxBarHeight * barProgress);

            graphics.fill(0, 0, width, currentBarHeight, 0xFF000000);
            graphics.fill(0, height - currentBarHeight, width, height, 0xFF000000);
        }
    }
}
