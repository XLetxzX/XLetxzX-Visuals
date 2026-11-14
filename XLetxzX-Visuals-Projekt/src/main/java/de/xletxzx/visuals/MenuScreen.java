package de.xletxzx.visuals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
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

    // {Anzeigename, Mod-ID} - Klick öffnet die Einstellungen der Mod
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

    private final Screen parent;
    private int page = HOME;
    private String note = "";
    private long
