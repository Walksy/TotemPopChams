package walksy.popchams.config;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import walksy.popchams.config.impl.CategoryBank;

import java.awt.*;

public class Config {

    public static final ConfigClassHandler<Config> CONFIG = ConfigClassHandler.createBuilder(Config.class)
        .serializer(config -> GsonConfigSerializerBuilder.create(config)
            .setPath(FabricLoader.getInstance().getConfigDir().resolve("walksytotempopchams.json"))
            .build())
        .build();

    @SerialEntry
    public boolean modEnabled = true;

    @SerialEntry
    public boolean showOwnPops = false;

    @SerialEntry
    public boolean fadeOut = true;

    @SerialEntry
    public double lifeTime = 50;

    @SerialEntry
    public boolean disperse = false;

    @SerialEntry
    public double disperseSpeed = 1.5;

    @SerialEntry
    public double disperseMaxDistance = 1;

    @SerialEntry
    public boolean filledModelEnabled = true;

    @SerialEntry
    public Color filledColor = new Color(137, 0, 255, 150);

    @SerialEntry
    public boolean wireframeEnabled = true;

    @SerialEntry
    public Color wireframeColor = new Color(100, 0, 255, 150);

    @SerialEntry
    public double wireframeThickness = 2.0;


    public static Screen createConfigScreen(Screen parent) {
        var screen = YetAnotherConfigLib.create(CONFIG, (defaults, config, builder) -> {
            builder.title(Text.literal("Totem Pop Chams Config"));
            builder.category(CategoryBank.general(config, defaults));
            return builder;
        });
        return screen.generateScreen(parent);
    }
}
