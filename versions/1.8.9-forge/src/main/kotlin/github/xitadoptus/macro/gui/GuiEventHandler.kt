package github.xitadoptus.macro.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiControls
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.GameSettings
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Handler para interceptar abertura de GUIs e substituir por versões customizadas
 */
object GuiEventHandler {

    @SubscribeEvent
    fun onGuiOpen(event: GuiOpenEvent) {
        val mc = Minecraft.getMinecraft()

        // Substituir menu de multiplayer pelo customizado
        if (event.gui is GuiMultiplayer && event.gui !is GuiMultiplayerNickname) {
            val parent = getParentScreen(event.gui)
            event.gui = GuiMultiplayerNickname(parent)
        }

        // Substituir menu de controles pelo customizado
        if (event.gui is GuiControls && event.gui !is GuiMacroKeybinds) {
            val controls = event.gui as GuiControls
            val parent = getParentScreen(controls)
            val settings = getGameSettings(controls) ?: mc.gameSettings
            event.gui = GuiMacroKeybinds(parent, settings)
        }
    }

    /**
     * Tenta obter o parentScreen usando reflection com múltiplos nomes possíveis de campo.
     * O Minecraft pode usar nomes obfuscados ou de mapeamento (MCP), então tentamos os mais comuns.
     */
    private fun getParentScreen(gui: Any?): GuiScreen? {
        if (gui == null) return null
        // Nomes possíveis após deobfuscação com MCP mappings 1.8.9
        val fieldNames = listOf("parentScreen", "field_146441_g", "prevGuiScreen")
        for (name in fieldNames) {
            try {
                val f = gui.javaClass.getDeclaredField(name)
                f.isAccessible = true
                return f.get(gui) as? GuiScreen
            } catch (_: NoSuchFieldException) {}
        }
        // Fallback: varrer todos os campos do tipo GuiScreen
        return try {
            gui.javaClass.declaredFields
                .firstOrNull { f ->
                    f.isAccessible = true
                    GuiScreen::class.java.isAssignableFrom(f.type)
                }
                ?.also { it.isAccessible = true }
                ?.get(gui) as? GuiScreen
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Tenta obter o GameSettings da GuiControls usando reflection.
     */
    private fun getGameSettings(controls: GuiControls): GameSettings? {
        val fieldNames = listOf("options", "field_146447_f", "gameSettings")
        for (name in fieldNames) {
            try {
                val f = controls.javaClass.getDeclaredField(name)
                f.isAccessible = true
                return f.get(controls) as? GameSettings
            } catch (_: NoSuchFieldException) {}
        }
        return try {
            controls.javaClass.declaredFields
                .firstOrNull { f ->
                    f.isAccessible = true
                    GameSettings::class.java.isAssignableFrom(f.type)
                }
                ?.also { it.isAccessible = true }
                ?.get(controls) as? GameSettings
        } catch (_: Exception) {
            null
        }
    }
}
