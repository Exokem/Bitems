package bitems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class Configuration
{
    public static final LanguageConfiguration LANGUAGE_CONFIGURATION;
    public static final ForgeConfigSpec LANGUAGE_CONFIGURATION_SPEC;

    static
    {
        Pair<LanguageConfiguration, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder()
                .configure(LanguageConfiguration::new);

        LANGUAGE_CONFIGURATION = pair.getLeft();
        LANGUAGE_CONFIGURATION_SPEC = pair.getRight();
    }

    static class LanguageConfiguration
    {
        private static ClientLanguage secondaryLanguage;

        private final ForgeConfigSpec.ConfigValue<String> secondaryLanguageKey;
        private final ForgeConfigSpec.ConfigValue<Boolean> enabled;

        public LanguageConfiguration(ForgeConfigSpec.Builder builder)
        {
            secondaryLanguageKey = builder
                    .comment("Specify the key for the secondary display language, e.g. 'en-us'")
                    .worldRestart()
                    .define("secondary_language_key", "en_us");

            enabled = builder
                    .comment("true: Show the secondary language display, false: Hide the secondary language display")
                    .worldRestart()
                    .define("show_secondary_language", true);
        }

        public String secondaryLanguageKey()
        {
            return secondaryLanguageKey.get();
        }

        public Language secondaryLanguage()
        {
            return secondaryLanguage;
        }

        public boolean enabled()
        {
            return enabled.get();
        }

        @Nullable
        public MutableComponent getTranslated(String target, Style style, Object... args)
        {
            if (secondaryLanguage == null)
            {
                try
                {
                    secondaryLanguage = ClientLanguage.loadFrom(Minecraft.getInstance().getResourceManager(), List.of(Minecraft.getInstance().getLanguageManager().getLanguage(secondaryLanguageKey())));
                }

                catch (Exception e)
                {
                    Bitems.OUTPUT.error("Failed to load secondary display language");
                    return null;
                }
            }

            String translated = secondaryLanguage.getOrDefault(target);

            if (translated.equals(target))
                return null;


            MutableComponent component = MutableComponent.create(new SecondaryTranslationContents(target, secondaryLanguage, args))
                    ;
            component.setStyle(style);

            return component;
        }
    }
}
