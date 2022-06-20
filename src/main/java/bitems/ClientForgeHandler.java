package bitems;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.Mutable;

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
        if (source instanceof MutableComponent translatableSource)
        {
            ComponentContents contents = translatableSource.getContents();

            if (contents instanceof TranslatableContents secContents)
            {
                Object[] args = new Object[secContents.getArgs().length];

                int i = 0;

                for (Object arg : secContents.getArgs())
                {
                    if (arg instanceof MutableComponent translatableArg)
                    {
                        Component resolvedArg = resolve(translatableArg, false);
                        args[i++] = resolvedArg == null ? arg : resolvedArg;
                    }

                    else
                    {
                        args[i++] = arg;
                    }
                }

                MutableComponent component = Configuration.LANGUAGE_CONFIGURATION.getTranslated(secContents.getKey(), translatableSource.getStyle(), args);

                if (component == null)
                    return null;

                resolveSiblings(translatableSource, component);

                return component;
            }

            else if (contents instanceof LiteralContents literalContents)
            {
                if (source.getSiblings().isEmpty() && ignoreUntranslatable)
                    return null;

                MutableComponent resolvedText = Component.literal(literalContents.text());
                resolvedText.setStyle(source.getStyle());
                resolveSiblings(source, resolvedText);
                return resolvedText;
            }

            else
            {
                if (source.getSiblings().isEmpty() && ignoreUntranslatable)
                    return null;

                MutableComponent resolvedText = Component.literal("");
                resolvedText.setStyle(source.getStyle());
                resolveSiblings(source, resolvedText);
                return resolvedText;
            }
        }

        return null;
    }

    private static void resolveSiblings(Component source, Component target)
    {
        for (Component sibling : source.getSiblings())
        {
            if (sibling instanceof MutableComponent translatableSibling)
            {
                Component resolvedSibling = resolve(translatableSibling, false);

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
