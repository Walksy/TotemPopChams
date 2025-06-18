package walksy.popchams;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import walksy.popchams.config.Config;

public class TotemPopChams implements ModInitializer {

    public static MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void onInitialize()
    {
        Config.CONFIG.load();
    }
}
