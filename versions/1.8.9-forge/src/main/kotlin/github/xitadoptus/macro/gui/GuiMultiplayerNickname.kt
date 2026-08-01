package github.xitadoptus.macro.gui

import github.xitadoptus.macro.MacroMod
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiMultiplayer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import java.awt.Color
import java.io.File
import java.io.IOException

/**
 * Tela customizada de multiplayer com input de nickname no canto inferior esquerdo
 */
class GuiMultiplayerNickname(parent: GuiScreen?) : GuiMultiplayer(parent) {
    private var nicknameField: GuiTextField? = null
    private val nicknameFile = File(MacroMod.configDir, "nickname.txt")

    override fun initGui() {
        super.initGui()
        
        // Criar campo de texto no canto inferior esquerdo
        nicknameField = GuiTextField(999, mc.fontRendererObj, 10, height - 30, 150, 20)
        nicknameField?.maxStringLength = 16 // Limite do Minecraft para nicknames
        nicknameField?.text = loadNickname()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        
        // Desenhar label e campo de nickname
        mc.fontRendererObj.drawStringWithShadow(
            "Nickname:",
            10f,
            (height - 42).toFloat(),
            Color.WHITE.rgb
        )
        nicknameField?.drawTextBox()
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        nicknameField?.mouseClicked(mouseX, mouseY, mouseButton)
    }

    @Throws(IOException::class)
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (nicknameField?.isFocused == true) {
            nicknameField?.textboxKeyTyped(typedChar, keyCode)
            saveNickname(nicknameField?.text ?: "")
        } else {
            super.keyTyped(typedChar, keyCode)
        }
    }

    override fun updateScreen() {
        super.updateScreen()
        nicknameField?.updateCursorCounter()
    }

    private fun loadNickname(): String {
        return try {
            if (nicknameFile.exists()) {
                nicknameFile.readText().trim()
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    private fun saveNickname(nickname: String) {
        try {
            if (!MacroMod.configDir.exists()) {
                MacroMod.configDir.mkdirs()
            }
            nicknameFile.writeText(nickname)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Obtém o nickname salvo
         */
        fun getSavedNickname(): String {
            val file = File(MacroMod.configDir, "nickname.txt")
            return try {
                if (file.exists()) file.readText().trim() else ""
            } catch (e: Exception) {
                ""
            }
        }
    }
}
