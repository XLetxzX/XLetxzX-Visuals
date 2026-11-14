package de.xletxzx.visuals;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class MenuScreen extends Screen {
    private static final Identifier LOGO = Identifier.of("xletxzx", "textures/gui/logo.png");

    private static final String[] CATEGORIES = {"FPS-Boost", "Visuals", "HUD", "Kampf / PvP", "Extras", "Design"};

    // Mods pro Kategorie (alle fest in XLetxzX Visuals enthalten)
    private static final String[][] MODS = {
        {"Sodium", "Sodium Extra", "Lithium", "FerriteCore", "ImmediatelyFast", "Entity Culling", "More Culling",
         "Dynamic FPS", "Krypton", "Bobby", "Clumps", "Packet Fixer", "Fast IP Ping", "Smooth Scroll"},
        {"Skinlayers 3D", "Wavey Capes", "More Cosmetics", "Skyboxify", "Lambdynamiclights", "Item Physic",
         "Natural Motion Blur", "Simply No Shading", "No Texture Rotations", "Block Highlight", "Not Enough Animations",
         "Zoomify", "Freelook", "Gamma Utils"},
        {"BetterF3", "Better Tab", "AppleSkin", "Inventory HUD", "Health Indicators", "Toggle Name Tags",
         "Shulker Box Tooltip", "Shulker Bundle", "Durability Guard", "Second Chat", "Auto Reconnect", "Litematica"},
        {"Combat Hitbox", "Marlow Crystal Optimizer", "Totem Counter", "Totem Tweaks", "Totem Optimizer",
         "Wind Charge Optimizer", "Anchor Optimizer", "Heros Anchor Optimizer", "Shield Fixes", "No Death Animation",
         "Crosshair Addons", "Tier Tagger"},
        {"Simple Voice Chat", "Mod Menu", "Creamy Keys", "Blind Me", "Almanac", "Cicada", "Silicon"},
        {}
    };

    private final Screen parent;
    private int category = 0;

    public MenuScreen(Screen parent) {
        super(Text.literal("XLetxzX Visuals"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = 16;
        int y = 78;
        for (int i = 0; i < CATEGORIES.length; i++) {
            final int idx = i;
            String label = (i == category ? "> " : "") + CATEGORIES[i];
            addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> { category = idx; clearAndInit(); })
                    .dimensions(left, y + i * 24, 110, 20).build());
        }
        if (category == 5) {
            Config c = Config.get();
            int x = 150;
            addDrawableChild(ButtonWidget.builder(Text.literal("Farbe: " + Config.THEME_NAMES[c.theme]), b -> {
                c.theme = (c.theme + 1) % Config.THEME_NAMES.length; Config.save(); clearAndInit();
            }).dimensions(x, 100, 180, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Hintergrund: " + Config.BG_NAMES[c.background]), b -> {
                c.background = (c.background + 1) % Config.BG_NAMES.length; Config.save(); clearAndInit();
            }).dimensions(x, 126, 180, 20).build());
        }
        if (category == 4) {
            Config c = Config.get();
            addDrawableChild(ButtonWidget.builder(Text.literal("Discord-Status: " + (c.discord ? "AN" : "AUS")), b -> {
                c.discord = !c.discord;
                if (c.discord) DiscordRpc.start(); else DiscordRpc.stop();
                Config.save(); clearAndInit();
            }).dimensions(150, 100, 180, 20).build());
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("Schließen"), b -> close())
                .dimensions(left, height - 32, 110, 20).build());
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // eigener Hintergrund wird in render() gezeichnet
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        Config c = Config.get();
        int accent = c.accent();
        int accentDim = (accent & 0x00FFFFFF) | 0x66000000;

        // Hintergrund
        switch (c.background) {
            case 0 -> ctx.fill(0, 0, width, height, 0xF0101014);
            case 1 -> ctx.fillGradient(0, 0, width, height, (accent & 0x00FFFFFF) | 0xE0000000, 0xF0070709);
            default -> {
                ctx.fill(0, 0, width, height, 0xF0000000);
                int s = Math.min(width, height);
                ctx.drawTexture(RenderPipelines.GUI_TEXTURED, LOGO, (width - s) / 2, (height - s) / 2,
                        0f, 0f, s, s, 256, 256, 256, 256);
                ctx.fill(0, 0, width, height, 0xB0000000);
            }
        }

        // Kopfbereich mit Logo und Titel
        ctx.drawTexture(RenderPipelines.GUI_TEXTURED, LOGO, 16, 12, 0f, 0f, 48, 48, 256, 256, 256, 256);
        ctx.drawText(textRenderer, Text.literal("XLetxzX Visuals"), 72, 22, accent, true);
        ctx.drawText(textRenderer, Text.literal("Minecraft 1.21.11"), 72, 36, 0xFFAAAAAA, false);
        ctx.fill(16, 66, 126, 67, accent);

        // Inhaltsbereich
        int cx = 150, cy = 20, cw = width - 150 - 200, ch = height - 40;
        ctx.fill(cx - 8, cy - 8, cx + cw, cy + ch, 0x88000000);
        ctx.fill(cx - 8, cy - 8, cx + cw, cy - 6, accent);
        ctx.drawText(textRenderer, Text.literal(CATEGORIES[category]), cx, cy + 2, accent, true);

        if (category < 5) {
            ctx.drawText(textRenderer, Text.literal("Enthaltene Mods:"), cx, cy + 20, 0xFFFFFFFF, false);
            String[] mods = MODS[category];
            int perCol = Math.max(1, (ch - 60) / 12);
            for (int i = 0; i < mods.length; i++) {
                int col = i / perCol, row = i % perCol;
                ctx.drawText(textRenderer, Text.literal("• " + mods[i]), cx + col * 130, cy + 36 + row * 12, 0xFFCCCCCC, false);
            }
        }

        super.render(ctx, mouseX, mouseY, delta);

        // Changelog oben rechts
        int bx = width - 188, by = 12, bw = 176;
        String[] log = {
            "Changelog",
            "v1.0.0",
            "+ Eigenes Menü (Rechts-Shift)",
            "+ Farben & Hintergründe",
            "+ Kategorien",
            "+ Discord-Status",
            "+ 80 Mods integriert",
        };
        ctx.fill(bx, by, bx + bw, by + 10 + log.length * 12, 0xAA000000);
        ctx.fill(bx, by, bx + bw, by + 2, accent);
        for (int i = 0; i < log.length; i++) {
            int col = i == 0 ? accent : (i == 1 ? 0xFFFFFFFF : 0xFFBBBBBB);
            ctx.drawText(textRenderer, Text.literal(log[i]), bx + 6, by + 8 + i * 12, col, i == 0);
        }
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
