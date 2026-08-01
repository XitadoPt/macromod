package github.xitadoptus.macro.gui

import github.xitadoptus.macro.engine.MacroStorage
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiControls
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.GameSettings
import java.awt.Color
import java.io.IOException

/**
 * Tela de controles customizada que exibe as teclas do MacroEngine
 */
class GuiMacroKeybinds(parent: GuiScreen?, settings: GameSettings?) : GuiControls(parent, settings) {
    private var scrollOffset = 0
    private val macroKeybinds: List<MacroKeybindInfo>
        get() {
            val binds = mutableListOf<MacroKeybindInfo>()
            MacroStorage.config.macros.forEach { macro ->
                if (macro.key != "NONE" && macro.key.isNotBlank()) {
                    binds.add(MacroKeybindInfo(macro.name, macro.key, macro.enabled))
                }
            }
            return binds
        }

    override fun initGui() {
        super.initGui()
        
        // Adicionar botão para voltar (substituir o botão Done padrão se necessário)
        buttonList.add(GuiButton(200, width / 2 - 100, height - 29, "Done"))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        
        // Desenhar seção de MacroEngine keybinds
        drawMacroKeybinds(mouseX, mouseY)
    }

    private fun drawMacroKeybinds(mouseX: Int, mouseY: Int) {
        val startX = 10
        var startY = height / 2 + 20 // Posicionar abaixo dos controles padrão
        val maxY = height - 50
        
        // Título da seção
        mc.fontRendererObj.drawStringWithShadow(
            "§6§lMacroEngine Keybinds",
            startX.toFloat(),
            startY.toFloat(),
            Color.ORANGE.rgb
        )
        startY += 15
        
        // Desenhar linha separadora
        drawRect(startX, startY, width - 10, startY + 1, Color(100, 100, 100, 200).rgb)
        startY += 5
        
        val keybinds = macroKeybinds
        
        if (keybinds.isEmpty()) {
            mc.fontRendererObj.drawString(
                "§7No macro keybinds configured",
                startX + 10,
                startY + 10,
                Color.GRAY.rgb
            )
            return
        }
        
        // Desenhar keybinds com scroll
        val visibleBinds = keybinds.drop(scrollOffset)
        
        for ((index, keybind) in visibleBinds.withIndex()) {
            val y = startY + index * 24
            if (y > maxY) break
            
            // Background para cada keybind
            val bgColor = if (index % 2 == 0) {
                Color(40, 40, 40, 180).rgb
            } else {
                Color(50, 50, 50, 180).rgb
            }
            drawRect(startX, y, width - 10, y + 22, bgColor)
            
            // Nome do macro
            val nameColor = if (keybind.enabled) Color.WHITE.rgb else Color.GRAY.rgb
            mc.fontRendererObj.drawString(
                keybind.name,
                startX + 10,
                y + 4,
                nameColor
            )
            
            // Tecla
            val keyText = "§e[${keybind.key}]"
            mc.fontRendererObj.drawString(
                keyText,
                startX + 10,
                y + 14,
                Color.YELLOW.rgb
            )
            
            // Status (enabled/disabled)
            val statusText = if (keybind.enabled) "§aEnabled" else "§cDisabled"
            val statusWidth = mc.fontRendererObj.getStringWidth(statusText)
            mc.fontRendererObj.drawString(
                statusText,
                width - 20 - statusWidth,
                y + 8,
                if (keybind.enabled) Color.GREEN.rgb else Color.RED.rgb
            )
        }
        
        // Indicadores de scroll
        if (scrollOffset > 0) {
            mc.fontRendererObj.drawStringWithShadow(
                "§f▲ Scroll up for more",
                (width / 2 - 50).toFloat(),
                startY.toFloat(),
                Color.WHITE.rgb
            )
        }
        
        if (scrollOffset + visibleBinds.size < keybinds.size) {
            mc.fontRendererObj.drawStringWithShadow(
                "§f▼ Scroll down for more",
                (width / 2 - 60).toFloat(),
                maxY.toFloat(),
                Color.WHITE.rgb
            )
        }
    }

    @Throws(IOException::class)
    override fun handleMouseInput() {
        super.handleMouseInput()
        
        // Implementar scroll para keybinds
        val wheel = org.lwjgl.input.Mouse.getEventDWheel()
        if (wheel != 0) {
            val direction = if (wheel > 0) -1 else 1
            val maxScroll = (macroKeybinds.size - 5).coerceAtLeast(0)
            scrollOffset = (scrollOffset + direction).coerceIn(0, maxScroll)
        }
    }

    override fun actionPerformed(button: GuiButton) {
        if (button.id == 200) {
            mc.displayGuiScreen(parentScreen)
        } else {
            super.actionPerformed(button)
        }
    }

    private data class MacroKeybindInfo(
        val name: String,
        val key: String,
        val enabled: Boolean
    )
}
