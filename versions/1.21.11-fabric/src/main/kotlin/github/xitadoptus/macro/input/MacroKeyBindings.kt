package github.xitadoptus.macro.input

import com.mojang.blaze3d.platform.InputConstants
import github.xitadoptus.macro.engine.MacroStorage
import github.xitadoptus.macro.mixin.KeyMappingAccessor
import github.xitadoptus.macro.util.KeyboardUtils
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import net.minecraft.resources.Identifier

/**
 * Registra os KeyMappings do MacroEngine na categoria "MacroEngine" do menu Controls.
 *
 * - As 4 teclas de sistema (Viewer, Recorder Stop, Builder Mark, Builder Target) são
 *   registradas na inicialização e ficam permanentemente no menu.
 * - Cada macro com tecla atribuída também é registrado.
 * - A sincronização é bidirecional:
 *     MacroStorage → KeyMappings  via [syncFromStorage] (após save/reload)
 *     KeyMappings  → MacroStorage via [syncToStorage]   (a cada tick com Controls aberto)
 */
object MacroKeyBindings {

    // Categoria própria no menu Controls
    private val CATEGORY = KeyMapping.Category(Identifier.fromNamespaceAndPath("macroengine", "keybindings"))

    // Teclas de sistema
    lateinit var viewerKey:        KeyMapping private set
    lateinit var recorderStopKey:  KeyMapping private set
    lateinit var builderMarkKey:   KeyMapping private set
    lateinit var builderTargetKey: KeyMapping private set

    // Teclas dos macros: nome do macro → KeyMapping
    private val macroMappings = LinkedHashMap<String, KeyMapping>()

    // ── Inicialização ─────────────────────────────────────────────────────────

    fun register() {
        val cfg = MacroStorage.config

        viewerKey        = registerBinding("viewer",         cfg.runtimeViewerKey)
        recorderStopKey  = registerBinding("recorder_stop",  cfg.recorderStopKey)
        builderMarkKey   = registerBinding("builder_mark",   cfg.stepBuilderMarkKey)
        builderTargetKey = registerBinding("builder_target", cfg.stepBuilderTargetKey)

        for (macro in cfg.macros) {
            registerMacroBinding(macro.name, macro.key)
        }
    }

    // ── MacroStorage → KeyMappings ────────────────────────────────────────────

    fun syncFromStorage() {
        val cfg = MacroStorage.config

        applyKey(viewerKey,        cfg.runtimeViewerKey)
        applyKey(recorderStopKey,  cfg.recorderStopKey)
        applyKey(builderMarkKey,   cfg.stepBuilderMarkKey)
        applyKey(builderTargetKey, cfg.stepBuilderTargetKey)

        for (macro in cfg.macros) {
            val m = macroMappings[macro.name]
            if (m != null) applyKey(m, macro.key)
            else registerMacroBinding(macro.name, macro.key)
        }

        KeyMapping.resetMapping()
    }

    // ── KeyMappings → MacroStorage ────────────────────────────────────────────

    /** Retorna true se alguma coisa mudou. */
    fun syncToStorage(): Boolean {
        var changed = false
        val cfg = MacroStorage.config

        readKey(viewerKey).let       { if (it != cfg.runtimeViewerKey)    { cfg.runtimeViewerKey    = it; changed = true } }
        readKey(recorderStopKey).let { if (it != cfg.recorderStopKey)     { cfg.recorderStopKey     = it; changed = true } }
        readKey(builderMarkKey).let  { if (it != cfg.stepBuilderMarkKey)  { cfg.stepBuilderMarkKey  = it; changed = true } }
        readKey(builderTargetKey).let{ if (it != cfg.stepBuilderTargetKey){ cfg.stepBuilderTargetKey= it; changed = true } }

        for (macro in cfg.macros) {
            val m = macroMappings[macro.name] ?: continue
            readKey(m).let { if (it != macro.key) { macro.key = it; changed = true } }
        }
        return changed
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun registerBinding(id: String, keyName: String): KeyMapping {
        val code = codeFor(keyName)
        val mapping = KeyMapping("key.macroengine.$id", InputConstants.Type.KEYSYM, code, CATEGORY)
        return KeyBindingHelper.registerKeyBinding(mapping)
    }

    private fun registerMacroBinding(name: String, keyName: String): KeyMapping {
        val safeId = name.replace(Regex("[^a-zA-Z0-9_]"), "_").lowercase()
        val code   = codeFor(keyName)
        val mapping = KeyMapping("key.macroengine.macro.$safeId", InputConstants.Type.KEYSYM, code, CATEGORY)
        val registered = KeyBindingHelper.registerKeyBinding(mapping)
        macroMappings[name] = registered
        return registered
    }

    private fun applyKey(mapping: KeyMapping, keyName: String) {
        mapping.setKey(InputConstants.Type.KEYSYM.getOrCreate(codeFor(keyName)))
    }

    private fun readKey(mapping: KeyMapping): String {
        val key = (mapping as KeyMappingAccessor).getBoundKey()
        if (key == InputConstants.UNKNOWN) return "NONE"
        val name = KeyboardUtils.keyNameFromCode(key.value)
        return if (name.isBlank() || name == "NONE") "NONE" else name
    }

    private fun codeFor(keyName: String): Int {
        val code = KeyboardUtils.keyCode(keyName)
        return if (code > 0) code else InputConstants.UNKNOWN.value
    }
}
