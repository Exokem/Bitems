package bitems;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT, modid = Bitems.ID)
public class ClientForgeHandler
{
    @SubscribeEvent
    public static void injectTooltips(final ItemTooltipEvent event)
    {
        if (!Configuration.LANGUAGE_CONFIGURATION.enabled())
            return;

        if (event.getToolTip().size() == 0)
            return;

        int insertionLimit = 1;

        if (event.getItemStack().getItem() instanceof PotionItem)
            insertionLimit++;

        insertionLimit = Math.min(event.getToolTip().size(), insertionLimit);

        int i = 0;

        for(; i < insertionLimit; i++)
        {
            Component insertionTooltip = event.getToolTip().get(i);
            insertionTooltip = resolve(insertionTooltip, true);

            if (insertionTooltip != null)
            {
                event.getToolTip().add(++i, insertionTooltip);
                insertionLimit++;
            }
        }

        int size = event.getToolTip().size();

        for (; i < size; i++)
        {
            Component tooltip = event.getToolTip().get(i);
            Component resolvedTooltip = resolve(tooltip, false);
            if (resolvedTooltip != null)
            {
                event.getToolTip().add(resolvedTooltip);
            }
        }
    }

    @Nullable
    private static Component resolve(Component source, boolean ignoreUntranslatable)
    {
        if (source instanceof TranslatableComponent translatableSource)
        {
            Object[] args = new Object[translatableSource.getArgs().length];

            int i = 0;

            for (Object arg : translatableSource.getArgs())
            {
                if (arg instanceof TranslatableComponent translatableArg)
                {
                    Component resolvedArg = resolve(translatableArg, false);
                    args[i++] = resolvedArg == null ? arg : resolvedArg;
                }

                else
                {
                    args[i++] = arg;
                }
            }

            SecondaryTranslationComponent component = Configuration.LANGUAGE_CONFIGURATION.getTranslated(translatableSource.getKey(), translatableSource.getStyle(), args);

            if (component == null)
                return null;

            resolveSiblings(translatableSource, component);

            return component;
        }

        else if (source instanceof TextComponent textSource)
        {
            if (textSource.getSiblings().isEmpty() && ignoreUntranslatable)
                return null;
            TextComponent resolvedText = new TextComponent(textSource.getText());
            resolvedText.setStyle(textSource.getStyle());
            resolveSiblings(textSource, resolvedText);
            return resolvedText;
        }

        return null;
    }

    private static void resolveSiblings(Component source, Component target)
    {
        for (Component sibling : source.getSiblings())
        {
            if (sibling instanceof TranslatableComponent translatableSibling)
            {
                Component resolvedSibling = resolve(sibling, false);

                if (resolvedSibling != null)
                    target.getSiblings().add(resolvedSibling);
            }

            else
            {
                target.getSiblings().add(sibling);
            }
        }
    }
}
