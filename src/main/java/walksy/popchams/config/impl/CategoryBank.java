package walksy.popchams.config.impl;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.gui.controllers.ColorController;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import net.minecraft.text.Text;
import walksy.popchams.config.Config;

import java.awt.*;

public class CategoryBank {

    public static ConfigCategory general(Config config, Config defaults)
    {
        var modSettingsGroupBuilder = OptionGroup.createBuilder()
            .name(Text.literal("General Settings"));

        modSettingsGroupBuilder.option(Option.<Boolean>createBuilder()
            .name(Text.literal("Mod Enabled"))
            .binding(
                defaults.modEnabled,
                () -> config.modEnabled,
                value -> config.modEnabled = value
            )
            .controller(BooleanControllerBuilder::create)
            .build()
        );

        modSettingsGroupBuilder.option(Option.<Boolean>createBuilder()
            .name(Text.literal("Show Own Pops"))
            .binding(
                defaults.showOwnPops,
                () -> config.showOwnPops,
                value -> config.showOwnPops = value
            )
            .controller(BooleanControllerBuilder::create)
            .build()
        );

        modSettingsGroupBuilder.option(Option.<Boolean>createBuilder()
            .name(Text.literal("Fade Out"))
            .binding(
                defaults.fadeOut,
                () -> config.fadeOut,
                value -> config.fadeOut = value
            )
            .controller(BooleanControllerBuilder::create)
            .build()
        );


        modSettingsGroupBuilder.option(Option.<Double>createBuilder()
            .name(Text.literal("Life Time"))
            .binding(
                defaults.lifeTime,
                () -> config.lifeTime,
                value -> config.lifeTime = value
            )
            .customController(doubleOption -> new <Double>DoubleSliderController(doubleOption, 0, 250, 1))
            .build()
        );

        var disperseSettings = OptionGroup.createBuilder()
            .name(Text.literal("Disperse Settings"));

        disperseSettings.option(Option.<Boolean>createBuilder()
            .name(Text.literal("Disperse"))
            .binding(
                defaults.disperse,
                () -> config.disperse,
                value -> config.disperse = value
            )
            .controller(BooleanControllerBuilder::create)
            .build()
        );

        disperseSettings.option(Option.<Double>createBuilder()
            .name(Text.literal("Disperse Speed"))
            .binding(
                defaults.disperseSpeed,
                () -> config.disperseSpeed,
                value -> config.disperseSpeed = value
            )
            .customController(doubleOption -> new <Double>DoubleSliderController(doubleOption, 0, 10, 0.5))
            .build()
        );

        disperseSettings.option(Option.<Double>createBuilder()
            .name(Text.literal("Disperse Max Travel Distance"))
            .binding(
                defaults.disperseMaxDistance,
                () -> config.disperseMaxDistance,
                value -> config.disperseMaxDistance = value
            )
            .customController(doubleOption -> new <Double>DoubleSliderController(doubleOption, 0, 8, 0.1))
            .build()
        );

        var filledModelSettings = OptionGroup.createBuilder()
            .name(Text.literal("Filled Model Settings"));

        filledModelSettings.option(Option.<Boolean>createBuilder()
            .name(Text.literal("Filled Model Enabled"))
            .binding(
                defaults.filledModelEnabled,
                () -> config.filledModelEnabled,
                value -> config.filledModelEnabled = value
            )
            .controller(BooleanControllerBuilder::create)
            .build()
        );

        filledModelSettings.option(Option.<Color>createBuilder()
            .name(Text.literal("Filled Model Color"))
            .binding(
                defaults.filledColor,
                () -> config.filledColor,
                value -> config.filledColor = value
            )
            .customController(opt -> new <Color>ColorController(opt, true))
            .build()
        );

        var wireframeSettings = OptionGroup.createBuilder()
            .name(Text.literal("Wireframe Settings"));

        wireframeSettings.option(Option.<Boolean>createBuilder()
            .name(Text.literal("Wireframe Enabled"))
            .binding(
                defaults.wireframeEnabled,
                () -> config.wireframeEnabled,
                value -> config.wireframeEnabled = value
            )
            .controller(BooleanControllerBuilder::create)
            .build()
        );

        wireframeSettings.option(Option.<Double>createBuilder()
            .name(Text.literal("Wireframe Thickness"))
            .binding(
                defaults.wireframeThickness,
                () -> config.wireframeThickness,
                value -> config.wireframeThickness = value
            )
            .customController(doubleOption -> new <Double>DoubleSliderController(doubleOption, 0, 5, 0.1))
            .build()
        );

        wireframeSettings.option(Option.<Color>createBuilder()
            .name(Text.literal("Wireframe Color"))
            .binding(
                defaults.wireframeColor,
                () -> config.wireframeColor,
                value -> config.wireframeColor = value
            )
            .customController(opt -> new <Color>ColorController(opt, true))
            .build()
        );

        return ConfigCategory.createBuilder()
            .name(Text.literal("General"))
            .group(modSettingsGroupBuilder.build())
            .group(disperseSettings.build())
            .group(filledModelSettings.build())
            .group(wireframeSettings.build())
            .build();
    }
}
