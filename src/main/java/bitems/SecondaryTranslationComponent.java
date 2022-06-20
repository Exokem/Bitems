package bitems;

import com.google.common.collect.ImmutableList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.TranslatableFormatException;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecondaryTranslationComponent extends TranslatableComponent
{
    private final Language language;

    public SecondaryTranslationComponent(String key, Language _language)
    {
        super(key);
        language = _language;
    }

    public SecondaryTranslationComponent(String key, Language _language, Object... args)
    {
        super(key, args);
        language = _language;
    }

    public Language getLanguage()
    {
        return language;
    }

    @Override
    protected void decompose()
    {
        if (language != this.decomposedWith)
        {
            this.decomposedWith = language;
            String s = language.getOrDefault(this.key);

            try
            {
                ImmutableList.Builder<FormattedText> builder = ImmutableList.builder();
                this.decomposeTemplate(s, builder::add);
                this.decomposedParts = builder.build();
            }

            catch (TranslatableFormatException translatableformatexception)
            {
                this.decomposedParts = ImmutableList.of(FormattedText.of(s));
            }
        }
    }
}
