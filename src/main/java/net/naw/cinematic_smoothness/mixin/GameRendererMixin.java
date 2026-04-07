package net.naw.cinematic_smoothness.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.naw.cinematic_smoothness.Cinematic_smoothness;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow(remap = false)
    @Final
    private Minecraft minecraft;

    @Shadow(remap = false)
    @Final
    private GameRenderState gameRenderState;

    @Unique
    private float zoomProgress = 0.0f;

    // // FIXED: Now checks if Smooth Camera (F2) is actually ON before hiding the outline
    @Inject(method = "shouldRenderBlockOutline", at = @At("HEAD"), cancellable = true, remap = false)
    private void onShouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
        if (this.minecraft.options.smoothCamera && Cinematic_smoothness.config.hideBlockOutline) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "extractCamera", at = @At("TAIL"), remap = false)
    private void applyCinematicZoom(DeltaTracker deltaTracker, float worldPartialTicks, float cameraEntityPartialTicks, CallbackInfo ci) {
        float zoomSpeed = 0.06f;
        boolean shouldZoom = this.minecraft.options.smoothCamera && Cinematic_smoothness.config.useCinematicZoom;

        if (shouldZoom && zoomProgress < 1.0f) {
            zoomProgress += zoomSpeed;
        } else if (!shouldZoom && zoomProgress > 0.0f) {
            zoomProgress -= zoomSpeed;
        }

        if (zoomProgress > 0.0f) {
            float zoomFactor = 1.0f - (0.15f * zoomProgress);
            gameRenderState.levelRenderState.cameraRenderState.projectionMatrix.scale(1.0f / zoomFactor, 1.0f / zoomFactor, 1.0f);
            gameRenderState.levelRenderState.cameraRenderState.hudFov *= zoomFactor;
        }
    }
}
