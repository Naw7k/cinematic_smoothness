package net.naw.cinematic_smoothness.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.naw.cinematic_smoothness.Cinematic_smoothness;
import org.jspecify.annotations.NonNull;

public class SmoothnessScreen extends Screen {
    protected final Screen parent;
    private static int bgFocusMode = 2;

    public SmoothnessScreen(Screen parent) {
        super(Component.literal("Cinematic Smoothness"));
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (bgFocusMode == 1) {
            guiGraphics.fillGradient(0, 0, this.width, this.height, 0x40000000, 0x40000000);
        } else if (bgFocusMode == 2) {
            this.renderTransparentBackground(guiGraphics);
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        boolean isMasterSwitchOn = this.minecraft.options.smoothCamera;

        if (Cinematic_smoothness.config.fineControl && Cinematic_smoothness.config.smoothness == 0.0) {
            Cinematic_smoothness.config.smoothness = -0.1;
        }

        AbstractSliderButton smoothnessSlider = getAbstractSliderButton(centerX, centerY, isMasterSwitchOn);
        smoothnessSlider.active = isMasterSwitchOn;
        this.addRenderableWidget(smoothnessSlider);

        // 2. Extended Range Toggle
        CycleButton<Boolean> rangeBtn = CycleButton.onOffBuilder(Cinematic_smoothness.config.fineControl)
                .withTooltip(val -> Tooltip.create(Component.literal("Unlocks specialized ranges: -100% (Instant) up to 200% (Heavy).")))
                .create(centerX - 100, centerY - 30, 178, 20, Component.literal("Extended Range"), (btn, val) -> {
                    Cinematic_smoothness.config.fineControl = val;
                    Cinematic_smoothness.config.save();
                    this.clearWidgets(); this.init();
                });
        rangeBtn.active = isMasterSwitchOn;
        this.addRenderableWidget(rangeBtn);

        // 3. Eye Icon
        this.addRenderableWidget(Button.builder(Component.literal("👁"), (btn) -> bgFocusMode = (bgFocusMode + 1) % 3)
                .tooltip(Tooltip.create(Component.literal("Cycle Menu Focus: Pure (None), Light Tint, or Vanilla Blur.")))
                .bounds(centerX + 80, centerY - 30, 20, 20).build());

        // 4. Bars Button (Dynamic Tooltips & Stable Alignment)
        String barMsg = !Cinematic_smoothness.config.showBlackBars ? "Bars: OFF" :
                (Cinematic_smoothness.config.showHudWithBars ? "Bars: ON (LOW)" : "Bars: ON (TOP)");

        String barTip = !Cinematic_smoothness.config.showBlackBars ? "Toggle cinematic aspect ratio bars." :
                (Cinematic_smoothness.config.showHudWithBars ? "Bars are rendered behind the HUD." : "Bars cover the HUD for a pure movie look.");

        Button barsBtn = Button.builder(Component.literal(barMsg), (btn) -> {
            if (!Cinematic_smoothness.config.showBlackBars) {
                Cinematic_smoothness.config.showBlackBars = true;
                Cinematic_smoothness.config.showHudWithBars = false;
            }
            else if (!Cinematic_smoothness.config.showHudWithBars) {
                Cinematic_smoothness.config.showHudWithBars = true;
            }
            else {
                Cinematic_smoothness.config.showBlackBars = false;
            }
            Cinematic_smoothness.config.save();
            this.clearWidgets();
            this.init();
        }).tooltip(Tooltip.create(Component.literal(barTip))).bounds(centerX - 100, centerY - 5, 98, 20).build();
        barsBtn.active = isMasterSwitchOn;
        this.addRenderableWidget(barsBtn);

        // 5. Zoom Button
        CycleButton<Boolean> zoomBtn = CycleButton.onOffBuilder(Cinematic_smoothness.config.useCinematicZoom)
                .withTooltip(val -> Tooltip.create(Component.literal("Changes the FOV for a tighter 'focal' shot when active.")))
                .create(centerX + 2, centerY - 5, 98, 20, Component.literal("Zoom"), (btn, val) -> {
                    Cinematic_smoothness.config.useCinematicZoom = val; Cinematic_smoothness.config.save();
                });
        zoomBtn.active = isMasterSwitchOn;
        this.addRenderableWidget(zoomBtn);

        // 6. Hide Crosshair
        CycleButton<Boolean> crossBtn = CycleButton.onOffBuilder(Cinematic_smoothness.config.hideCrosshair)
                .withTooltip(val -> Tooltip.create(Component.literal("Completely hides the crosshair while cinematic camera is active.")))
                .create(centerX - 100, centerY + 20, 98, 20, Component.literal("Hide Crosshair"), (btn, val) -> {
                    Cinematic_smoothness.config.hideCrosshair = val; Cinematic_smoothness.config.save();
                });
        crossBtn.active = isMasterSwitchOn;
        this.addRenderableWidget(crossBtn);

        // 7. Hide Block Outline
        CycleButton<Boolean> outlineBtn = CycleButton.onOffBuilder(Cinematic_smoothness.config.hideBlockOutline)
                .withTooltip(val -> Tooltip.create(Component.literal("Removes the black selection box around blocks for cleaner shots.")))
                .create(centerX + 2, centerY + 20, 98, 20, Component.literal("Hide Outline"), (btn, val) -> {
                    Cinematic_smoothness.config.hideBlockOutline = val; Cinematic_smoothness.config.save();
                });
        outlineBtn.active = isMasterSwitchOn;
        this.addRenderableWidget(outlineBtn);

        // 8. Reset
        this.addRenderableWidget(Button.builder(Component.literal("Reset"), (btn) -> {
                    Cinematic_smoothness.config.smoothness = 1.0; Cinematic_smoothness.config.fineControl = false;
                    Cinematic_smoothness.config.showBlackBars = false; Cinematic_smoothness.config.useCinematicZoom = false;
                    Cinematic_smoothness.config.hideCrosshair = false; Cinematic_smoothness.config.hideBlockOutline = false;
                    Cinematic_smoothness.config.save(); this.clearWidgets(); this.init();
                }).tooltip(Tooltip.create(Component.literal("Restores vanilla cinematic behavior and disables extra features.")))
                .bounds(centerX - 100, centerY + 55, 98, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Done"), (btn) -> this.onClose())
                .bounds(centerX + 2, centerY + 55, 98, 20).build());
    }

    private static @NonNull AbstractSliderButton getAbstractSliderButton(int centerX, int centerY, boolean isMasterSwitchOn) {
        double currentVal = Cinematic_smoothness.config.smoothness;
        double sliderHandlePos = Cinematic_smoothness.config.fineControl ? (currentVal + 1.1) / 3.1 : Math.max(0, Math.min(1, currentVal));

        // 1. Smoothness Slider
        return new AbstractSliderButton(centerX - 100, centerY - 55, 200, 20, Component.empty(), sliderHandlePos) {
            { updateMessage(); updateTooltip(); }

            private void updateTooltip() {
                if (!isMasterSwitchOn) {
                    this.setTooltip(Tooltip.create(Component.literal("Master Switch is OFF. These settings are currently inactive.")));
                    return;
                }
                double val = Cinematic_smoothness.config.smoothness;
                String desc = (val < 0) ? "Sharp, instant camera response (Lower than vanilla)." :
                        (val == 0) ? "Cinematic smoothing is disabled." :
                                (val > 1.0) ? "Ultra-heavy smoothing for slow, sweeping cinematic pans." :
                                        "Adjusts how much the camera 'drags' behind your movement.";
                this.setTooltip(Tooltip.create(Component.literal(desc)));
            }

            @Override
            protected void updateMessage() {
                double val = Cinematic_smoothness.config.smoothness;
                String icon = "📷 ";
                double displayVal = (!Cinematic_smoothness.config.fineControl && val > 1.0) ? 1.0 : val;
                boolean isOff = (Cinematic_smoothness.config.fineControl && val <= -1.05) || (!Cinematic_smoothness.config.fineControl && val <= 0.0);

                String statusLabel = isOff ? " (OFF)" : " (" + (int)Math.round(displayVal * 100) + "%)";
                MutableComponent text = Component.literal(icon + "Smoothness:" + statusLabel);

                if (!isMasterSwitchOn || isOff) {
                    text.withStyle(s -> s.withColor(0xFF888888));
                } else {
                    if (Cinematic_smoothness.config.fineControl) {
                        if (val < 0) text.withStyle(s -> s.withColor(0xFF55FFFF));
                        else if (val > 1.0) text.withStyle(s -> s.withColor(0xFFFF5555));
                        else text.withStyle(s -> s.withColor(0xFFFFFFFF));
                    } else {
                        text.withStyle(s -> s.withColor(0xFFFFFFFF));
                    }
                }
                this.setMessage(text);
            }

            @Override
            protected void applyValue() {
                double raw = Cinematic_smoothness.config.fineControl ? (this.value * 3.1) - 1.1 : this.value;
                double snapped = Math.round(raw * 10.0) / 10.0;
                if (Cinematic_smoothness.config.fineControl && snapped == 0.0) snapped = (raw < 0) ? -0.1 : 0.1;
                Cinematic_smoothness.config.smoothness = snapped;
                Cinematic_smoothness.config.save();
                updateTooltip();
            }
        };
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        if (!this.minecraft.options.smoothCamera) {
            String message;
            if (this.minecraft.options.keySmoothCamera.isUnbound()) {
                message = "Cinematic Camera is OFF (Set a keybind in Controls)";
            } else {
                String keyName = this.minecraft.options.keySmoothCamera.getTranslatedKeyMessage().getString();
                message = "Cinematic Camera is OFF (Press " + keyName + " to enable)";
            }
            guiGraphics.drawCenteredString(this.font, message, this.width / 2, this.height / 2 + 85, 0xFFFF5555);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() { this.minecraft.setScreen(this.parent); }
}