package bitems;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
@Mod(Bitems.ID)
public class Bitems
{
    public static final String ID = "bitems";
    public static final Logger OUTPUT = LogUtils.getLogger();

    public Bitems()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.LANGUAGE_CONFIGURATION_SPEC);

        // get the language this way
//        Minecraft.getInstance().getLanguageManager().getLanguage("en_us");
    }
}
