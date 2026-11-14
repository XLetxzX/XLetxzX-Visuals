package de.xletxzx.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.time.Duration;
import java.time.LocalDateTime;

public class MenuScreen extends Screen {
    private static final Identifier LOGO = Identifier.of("xletxzx", "textures/gui/logo.png");

    private static final String[] TABS = {"HOME", "FPS", "VISUALS", "PVP", "EXTRAS"};
    private static final int HOME = 0, FPS = 1, VISUALS = 2, PVP = 3, EXTRAS = 4, DESIGN = 5, CREDITS = 6;

    // {display name, mod id} - click opens the mod settings
    private static final String[][] FPS_MODS = {
        {"Sodium", "sodium"}, {"Sodium Extra", "sodium-extra"}, {"Entity Culling", "entityculling"},
        {"More Culling", "moreculling"}, {"Dynamic FPS", "dynamic_fps"}, {"ImmediatelyFast", "immediatelyfast"},
        {"Lithium", "lithium"}, {"FerriteCore", "ferritecore"}, {"Krypton", "krypton"},
        {"Bobby", "bobby"}, {"Clumps", "clumps"}, {"Smooth Scroll", "smoothscroll"}
    };
    private static final String[][] VISUAL_MODS = {
        {"Skin Layers 3D", "skinlayers3d"}, {"Wavey Capes", "waveycapes"}, {"More Cosmetics", "morecosmetics"},
        {"Skyboxify", "skyboxify"}, {"LambDynamicLights", "lambdynlights"}, {"Item Physic", "itemphysic"},
        {"Natural Motion Blur", "naturalmotionblur"}, {"No Texture Rotations", "notexturerotations"},
        {"Block Highlight", "custom-block-highlight"}, {"Not Enough Animations", "notenoughanimations"},
        {"Zoomify", "zoomify"}, {"Freelook", "freelook"}, {"Gamma Utils", "gammautils"},
        {"Toggle Name Tags", "togglenametags"}
    };
    private static final String[][] PVP_MODS = {
        {"Combat Hitbox", "combat-hitboxes"}, {"Marlow Crystal Optimizer", "marlowcrystal"},
        {"Totem Counter", "totemcounter"}, {"Totem Tweaks", "totemtweaks"}, {"Totem Optimizer", "totemoptimizer"},
        {"Wind Charge Optimizer", "windchargeoptimizer"}, {"Anchor Optimizer", "client_side_anchors"},
        {"Heros Anchor Optimizer", "herosanchoroptimizer"}, {"Shield Fixes", "shieldfixes"},
        {"No Death Animation", "nodeathanimation"}, {"Crosshair Addons", "crosshairaddons"},
        {"Tier Tagger", "tier-tagger"}
    };
    private static final String[][] EXTRA_MODS = {
        {"BetterF3", "betterf3"}, {"Better Tab", "bettertab"}, {"AppleSkin", "appleskin"},
        {"Inventory HUD", "inventoryhud"}, {"Shulker Box Tooltip", "shulkerboxtooltip"},
        {"Second Chat", "secondchat"}, {"Auto Reconnect", "autoreconnectrf"}, {"Litematica", "litematica"},
        {"Simple Voice Chat", "voicechat"}, {"Creamy Keys", "creamykeys"}, {"Mod Menu", "modmenu"},
        {"Fast IP Ping", "fastipping"}
    };

    private static final String[] THEME_EN = {"Purple", "Blue", "Red", "Green", "Orange", "Pink", "White"};
    private static final String[] BG_EN = {"Dark", "Gradient", "Image"};
    // Mods whose settings live inside the Sodium video settings screen
    private static final java.util.Set<String> SODIUM_PAGES = java.util.Set.of(
        "sodium", "sodium-extra", "moreculling", "bobby", "notexturerotations");

    private final Screen parent;
    private int page = HOME;
    private String note = "";
    private long noteUntil = 0;

    public MenuScreen(Screen parent) {
        super(Text.literal("XLetxzX Visuals"));
        this.parent = parent;
    }

    private static String[][] listFor(int p) {
        return switch (p) {
            case FPS -> FPS_MODS;
            case VISUALS -> VISUAL_MODS;
            case PVP -> PVP_MODS;
            case EXTRAS -> EXTRA_MODS;
            default -> null;
        };
    }

    private void showNote(String s) {
        note = s;
        noteUntil = System.currentTimeMillis() + 3000;
    }

    /** Opens a mod's settings through Mod Menu (reflection) or the Sodium video settings. */
    private void openConfig(String name, String modId) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!modId.equals("sodium") && !modId.equals("sodium-extra")) {
            try {
                Class<?> c = Class.forName("com.terraformersmc.modmenu.ModMenu");
                Object s = c.getMethod("getConfigScreen", String.class, Screen.class).invoke(null, modId, this);
                if (s instanceof Screen scr) {
                    mc.setScreen(scr);
                    return;
                }
            } catch (Throwable t) {
                System.err.println("[XLetxzX] config screen error for " + modId + ": " + t);
            }
        }
        if (SODIUM_PAGES.contains(modId)) {
            try {
                mc.setScreen(new VideoOptionsScreen(this, mc, mc.options));
                return;
            } catch (Throwable t) {
                System.err.println("[XLetxzX] video settings error: " + t);
            }
        }
        showNote(name + " has no in-game settings screen (config file only).");
    }

    private void openModList() {
        try {
            Class<?> c = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
            Object s = c.getConstructor(Screen.class).newInstance(this);
            if (s instanceof Screen scr) {
                MinecraftClient.getInstance().setScreen(scr);
                return;
            }
        } catch (Throwable ignored) {
        }
        showNote("Could not open Mod Menu.");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setLastEnum(SimpleOption opt) {
        try {
            Object cur = opt.getValue();
            if (cur instanceof Enum<?> e) {
                Object[] all = e.getDeclaringClass().getEnumConstants();
                opt.setValue(all[all.length - 1]);
            }
        } catch (Throwable ignored) {
        }
    }

    /** Sets the most important vanilla options for maximum FPS. */
    private void applyFpsPreset() {
        try {
            GameOptions o = MinecraftClient.getInstance().options;
            o.getViewDistance().setValue(8);
            o.getSimulationDistance().setValue(6);
            setLastEnum(o.getParticles()); // particles: last level = minimal
            o.getEntityDistanceScaling().setValue(0.5);
            o.getMaxFps().setValue(260);
            o.getEnableVsync().setValue(false);
            o.getBiomeBlendRadius().setValue(1);
            o.write();
            showNote("FPS boost applied (render distance 8, minimal particles, VSync off).");
        } catch (Throwable t) {
            showNote("Could not apply FPS boost.");
        }
    }

    @Override
    protected void init() {
        Config c = Config.get();
        int areaW = width - 40 - 176;

        // tabs
        int tabW = 64;
        int tx = Math.max(150, (width - 176 - TABS.length * (tabW + 4)) / 2 + 60);
        for (int i = 0; i < TABS.length; i++) {
            final int idx = i;
            addDrawableChild(ButtonWidget.builder(Text.literal(TABS[i]), b -> { page = idx; clearAndInit(); })
                    .dimensions(tx + i * (tabW + 4), 5, tabW, 18).build());
        }

        if (page == HOME) {
            int img = Math.min(height - 90, (width - 176) / 3);
            int regionStart = 20 + img + 20;
            int regionEnd = width - 176 - 10;
            int bx = (regionStart + regionEnd) / 2 - 95;
            int by = height / 2 - 10;
            addDrawableChild(ButtonWidget.builder(Text.literal("Continue"), b -> close())
                    .dimensions(bx, by, 190, 22).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Edit my mods"), b -> openModList())
                    .dimensions(bx, by + 28, 190, 22).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Settings"), b -> { page = DESIGN; clearAndInit(); })
                    .dimensions(bx, by + 56, 190, 22).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Credits"), b -> { page = CREDITS; clearAndInit(); })
                    .dimensions(bx, by + 84, 190, 22).build());
        } else if (page == DESIGN) {
            addDrawableChild(ButtonWidget.builder(Text.literal("Color: " + THEME_EN[c.theme]), b -> {
                c.theme = (c.theme + 1) % Config.THEME_NAMES.length; Config.save(); clearAndInit();
            }).dimensions(20, 70, 200, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Background: " + BG_EN[c.background]), b -> {
                c.background = (c.background + 1) % Config.BG_NAMES.length; Config.save(); clearAndInit();
            }).dimensions(20, 96, 200, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Discord status: " + (c.discord ? "ON" : "OFF")), b -> {
                c.discord = !c.discord;
                if (c.discord) DiscordRpc.start(); else DiscordRpc.stop();
                Config.save(); clearAndInit();
            }).dimensions(20, 122, 200, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> { page = HOME; clearAndInit(); })
                    .dimensions(20, 160, 200, 20).build());
        } else if (page == CREDITS) {
            addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> { page = HOME; clearAndInit(); })
                    .dimensions(20, height - 40, 120, 20).build());
        } else {
            int startY = 66;
            if (page == FPS) {
                addDrawableChild(ButtonWidget.builder(Text.literal("Apply FPS boost (vanilla options)"), b -> applyFpsPreset())
                        .dimensions(20, 40, areaW, 20).build());
                startY = 90;
            }
            String[][] list = listFor(page);
            int cols = 3, gap = 6;
            int bw = (areaW - gap * (cols - 1)) / cols;
            for (int i = 0; i < list.length; i++) {
                final String id = list[i][1];
                final String name = list[i][0];
                int x = 20 + (i % cols) * (bw + gap);
                int y = startY + (i / cols) * 24;
                addDrawableChild(ButtonWidget.builder(Text.literal(list[i][0]), b -> openConfig(name, id))
                        .dimensions(x, y, bw, 20).build());
            }
        }

        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(width - 176 - 100, height - 28, 90, 18).build());
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // custom background is drawn in render()
    }

    private void drawCentered(DrawContext ctx, String s, int cx, int y, int color) {
        ctx.drawText(textRenderer, Text.literal(s), cx - textRenderer.getWidth(s) / 2, y, color, true);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        Config c = Config.get();
        int accent = c.accent();
        long t = System.currentTimeMillis();

        // background
        switch (c.background) {
            case 0 -> ctx.fill(0, 0, width, height, 0xFF101018);
            case 1 -> ctx.fillGradient(0, 0, width, height, (accent & 0x00FFFFFF) | 0xFF000000, 0xFF06060A);
            default -> {
                ctx.fill(0, 0, width, height, 0xFF000000);
                int s = Math.min(width, height);
                ctx.drawTexture(RenderPipelines.GUI_TEXTURED, LOGO, (width - s) / 2, (height - s) / 2,
                        0f, 0f, s, s, 256, 256, 256, 256);
                ctx.fill(0, 0, width, height, 0xC0000000);
            }
        }
        // darken for readability
        ctx.fillGradient(0, 0, width, height, 0x40000000, 0xA0000000);

        // snow
        for (int i = 0; i < 70; i++) {
            int x = (int) ((((i * 61) % 100) / 100f) * width + Math.sin(t / 900.0 + i) * 6);
            int y = (int) (((t / (20 + (i % 5) * 8)) + i * 53L) % Math.max(1, height));
            ctx.fill(x, y, x + 2, y + 2, 0xAAFFFFFF);
        }

        // header
        ctx.fill(0, 0, width, 28, 0xCC0B0B12);
        ctx.fill(0, 28, width, 29, accent);
        ctx.drawTexture(RenderPipelines.GUI_TEXTURED, LOGO, 6, 4, 0f, 0f, 20, 20, 256, 256, 256, 256);
        ctx.drawText(textRenderer, Text.literal("XLetxzX Visuals"), 32, 6, accent, true);
        ctx.drawText(textRenderer, Text.literal("v1.1 - 1.21.11"), 32, 17, 0xFF8888AA, false);

        if (page == HOME) {
            int img = Math.min(height - 90, (width - 176) / 3);
            ctx.drawTexture(RenderPipelines.GUI_TEXTURED, LOGO, 20, 50, 0f, 0f, img, img, 256, 256, 256, 256);
            ctx.fill(20, 50 + img + 6, 20 + img, 50 + img + 8, accent);
            drawCentered(ctx, "XLetxzX", 20 + img / 2, 50 + img + 14, 0xFFFFFFFF);

            int regionStart = 20 + img + 20;
            int regionEnd = width - 176 - 10;
            int cx = (regionStart + regionEnd) / 2;
            drawCentered(ctx, "XLetxzX Visuals", cx, height / 2 - 60, accent);
            drawCentered(ctx, "v1.1", cx, height / 2 - 48, 0xFF8888AA);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime xmas = LocalDateTime.of(now.getYear(), 12, 25, 0, 0);
            if (now.isAfter(xmas)) xmas = xmas.plusYears(1);
            Duration d = Duration.between(now, xmas);
            drawCentered(ctx, "Christmas in " + d.toDays() + "d " + d.toHoursPart() + "h " + d.toMinutesPart() + "m",
                    cx, height / 2 - 32, 0xFFFFD27A);
            drawCentered(ctx, "Open this menu with Right Shift", cx, height / 2 + 108, 0xFF777788);
        } else if (page == DESIGN) {
            ctx.drawText(textRenderer, Text.literal("Settings"), 20, 44, accent, true);
        } else if (page == CREDITS) {
            ctx.drawText(textRenderer, Text.literal("Credits"), 20, 44, accent, true);
            String[] lines = {
                "XLetxzX Visuals - made by XLetxzX",
                "",
                "Included mods belong to their respective developers:",
                "Sodium, Lithium, FerriteCore, ImmediatelyFast, Entity Culling,",
                "Mod Menu, Cloth Config, Simple Voice Chat, Litematica and many more.",
                "Thanks to everyone who builds these mods."
            };
            for (int i = 0; i < lines.length; i++) {
                ctx.drawText(textRenderer, Text.literal(lines[i]), 20, 66 + i * 12, 0xFFCCCCCC, false);
            }
        } else {
            ctx.drawText(textRenderer, Text.literal(TABS[page]), 20, 44, accent, true);
            if (page != FPS) {
                ctx.drawText(textRenderer, Text.literal("Click a mod to open its settings"),
                        90, 44, 0xFF8888AA, false);
            }
        }

        // mark active tab
        if (page <= EXTRAS) {
            int tabW = 64;
            int tx = Math.max(150, (width - 176 - TABS.length * (tabW + 4)) / 2 + 60);
            ctx.fill(tx + page * (tabW + 4), 24, tx + page * (tabW + 4) + tabW, 27, accent);
        }

        super.render(ctx, mouseX, mouseY, delta);

        // changelog top right
        String[] log = {
            "Changelog",
            "v1.1",
            "+ New tabbed design",
            "+ Edit mods in the menu",
            "+ FPS boost button",
            "+ Christmas countdown",
            "v1.0",
            "+ Colors & backgrounds",
            "+ Discord status",
        };
        int bx = width - 168, by = 36, bw = 160;
        ctx.fill(bx, by, bx + bw, by + 12 + log.length * 12, 0xAA000000);
        ctx.fill(bx, by, bx + bw, by + 2, accent);
        for (int i = 0; i < log.length; i++) {
            boolean head = i == 0;
            boolean ver = log[i].startsWith("v");
            int col = head ? accent : (ver ? 0xFFFFFFFF : 0xFFBBBBBB);
            ctx.drawText(textRenderer, Text.literal(log[i]), bx + 6, by + 8 + i * 12, col, head);
        }

        // note
        if (System.currentTimeMillis() < noteUntil) {
            drawCentered(ctx, note, (width - 176) / 2, height - 24, 0xFFFFD27A);
        }

        // footer
        ctx.drawText(textRenderer, Text.literal("MADE BY XLETXZX"), 20, height - 14, 0xFF666677, false);
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }
}
