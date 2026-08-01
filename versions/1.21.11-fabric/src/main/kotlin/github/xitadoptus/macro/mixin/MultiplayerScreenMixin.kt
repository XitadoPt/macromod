package github.xitadoptus.macro.mixin

import github.xitadoptus.macro.MacroMod
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.io.File

@Mixin(JoinMultiplayerScreen::class)
abstract class MultiplayerScreenMixin : Screen(Component.empty()) {

    // Campo guardado como variável de instância via mixin shadow trick —
    // usamos uma lista local ao init porque campos em mixins Kotlin não são confiáveis entre re-inits.

    @Inject(method = ["init"], at = [At("TAIL")])
    private fun macroengine_initNickname(ci: CallbackInfo) {
        val savedNick = loadNickname()

        // Label
        addRenderableWidget(
            StringWidget(10, height - 44, 60, 10, Component.literal("Nickname:"), font)
        )

        // Input
        val box = EditBox(font, 10, height - 31, 140, 20, Component.literal("Nickname"))
        box.setMaxLength(16)
        box.value = savedNick
        // Não usa setResponder aqui pois queremos salvar só no Apply
        addRenderableWidget(box)

        // Botão Apply ao lado do input
        addRenderableWidget(
            Button.builder(Component.literal("Apply")) {
                val nick = box.value.trim()
                saveNickname(nick)
                applyNicknameToSession(nick)
            }.bounds(155, height - 31, 46, 20).build()
        )
    }

    /**
     * Aplica o nickname na sessão local do Minecraft.
     * Em ambiente de dev (runClient) o perfil é offline e o nome pode ser trocado.
     * Em produção com conta Microsoft o servidor valida o nome — isso afeta apenas o nome local/cosmético.
     */
    private fun applyNicknameToSession(nick: String) {
        if (nick.isBlank()) return
        try {
            val mc = Minecraft.getInstance()
            // Acessa o campo "user" via reflection (campo privado em GameConfig/Minecraft)
            val userField = mc.javaClass.declaredFields.firstOrNull { it.type.simpleName == "User" }
                ?: mc.javaClass.superclass?.declaredFields?.firstOrNull { it.type.simpleName == "User" }
            if (userField != null) {
                userField.isAccessible = true
                val user = userField.get(mc) ?: return
                val nameField = user.javaClass.declaredFields.firstOrNull {
                    it.type == String::class.java
                } ?: return
                nameField.isAccessible = true
                nameField.set(user, nick)
            }
        } catch (_: Exception) {
            // Se não conseguir via reflection, apenas salva — o nick fica disponível via getSavedNickname()
        }
    }

    private fun loadNickname(): String = try {
        val f = File(MacroMod.configDir, "nickname.txt")
        if (f.exists()) f.readText().trim() else ""
    } catch (_: Exception) { "" }

    private fun saveNickname(text: String) = try {
        if (!MacroMod.configDir.exists()) MacroMod.configDir.mkdirs()
        File(MacroMod.configDir, "nickname.txt").writeText(text)
    } catch (_: Exception) {}
}
