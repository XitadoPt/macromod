package github.xitadoptus.macro.gui

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.awt.Color

class MacroHelpScreen(private val previousScreen: Screen?) : Screen(Component.literal("MacroEngine Help")) {
    private var scrollOffset = 0
    private val helpSections = listOf(
        HelpSection("Control Flow", listOf(
            "IF(condition) / ELSEIF(condition) / ELSE / ENDIF",
            "  Conditional blocks. Supports =, ==, !=, <, >, <=, >=, &&, ||, !",
            "IFCONTAINS(value, search)",
            "  True when value contains search",
            "IFBEGINSWITH(value, search)",
            "  True when value starts with search",
            "IFENDSWITH(value, search)",
            "  True when value ends with search",
            "IFMATCHES(value, regex, &out, group)",
            "  True when regex matches. Optionally stores a capture group",
            "DO / LOOP",
            "  Infinite loop capped internally to avoid runaway loops",
            "DO(count) / LOOP",
            "  Counted loop",
            "DO / WHILE(condition)",
            "  Runs while condition is true",
            "DO / UNTIL(condition)",
            "  Runs until condition is true",
            "BREAK",
            "  Exits the current loop",
            "FOR(#i, start, end) / NEXT",
            "  Numeric loop. Step is inferred from start/end",
            "FOREACH(iterator) / NEXT",
            "  Loops over an iterator row set"
        )),
        HelpSection("Chat, Logging, Timing", listOf(
            "ECHO(text)",
            "  Sends chat or a command. Color codes using & are converted",
            "LOG(text)",
            "  Prints a colored client chat message",
            "LOGRAW(json)",
            "  Prints a raw JSON chat component",
            "WAIT(value)",
            "  Sleeps. Supports seconds, ms, and ticks with t",
            "WAITUNTIL(condition)",
            "  Pauses execution until condition becomes true",
            "WAITWHILE(condition)",
            "  Pauses execution while condition remains true",
            "EXEC(file, reason, arg0, arg1...)",
            "  Runs a script file from the macro folder",
            "STOP()",
            "  Stops the current macro",
            "STOP(name)",
            "  Stops running macros whose id contains name",
            "TRACE(distance)",
            "  Ray traces and fills hit variables",
            "LOGTO(fileOrKey, text)",
            "  Appends to a .txt file or stores a GUI property value"
        )),
        HelpSection("Variables, Arrays, Strings", listOf(
            "SET(&var, value)",
            "  Sets a variable. Missing value means true",
            "ASSIGN(&var, expression)",
            "  Assigns an evaluated expression",
            "UNSET(&var)",
            "  Removes a variable",
            "TOGGLE(&var)",
            "  Toggles boolean truthiness",
            "INC(#var, amount) / DEC(#var, amount)",
            "  Adds or subtracts an integer. Default amount is 1",
            "RANDOM(#out, max, min)",
            "  Stores a random integer between min and max",
            "IIF(condition, trueValue, falseValue, &out)",
            "  Inline conditional assignment",
            "TIME(&out, format)",
            "  Stores current time using a Java SimpleDateFormat",
            "LCASE(value, &out) / UCASE(value, &out)",
            "  Changes string case",
            "TRIM(value, &out)",
            "  Trims leading and trailing whitespace",
            "LEN(value, #out) / LENGTH(value, &out)",
            "  Stores string length",
            "SUBSTR(value, start, length, &out)",
            "  Stores a substring",
            "STRIP(&out, value)",
            "  Removes Minecraft formatting codes",
            "REPLACE(&var, search, replacement)",
            "  String replacement",
            "REGEXREPLACE(&out, value, regex, replacement)",
            "  Regex-based string replacement",
            "SPLIT(separator, value, &array[])",
            "  Splits a string into an array",
            "JOIN(separator, &array[], &out)",
            "  Joins array elements into a string",
            "ARRAYSIZE(&array[], #out)",
            "  Stores array size",
            "ARRAYADD(&array[], value)",
            "  Adds a value to an array",
            "ARRAYREMOVE(&array[], index)",
            "  Removes an element from an array"
        )),
        HelpSection("Math", listOf(
            "ADD(#out, a, b, c...)",
            "  Adds numbers together",
            "SUB(#out, a, b)",
            "  Subtracts b from a",
            "MUL(#out, a, b, c...)",
            "  Multiplies numbers together",
            "DIV(#out, a, b)",
            "  Divides a by b",
            "MOD(#out, a, b)",
            "  Modulo operation",
            "POW(#out, base, exponent)",
            "  Power operation",
            "SQRT(#out, value)",
            "  Square root",
            "ABS(#out, value)",
            "  Absolute value",
            "MIN(#out, a, b, c...)",
            "  Minimum value",
            "MAX(#out, a, b, c...)",
            "  Maximum value",
            "ROUND(#out, value)",
            "  Rounds to nearest integer",
            "FLOOR(#out, value)",
            "  Rounds down",
            "CEIL(#out, value)",
            "  Rounds up"
        )),
        HelpSection("Player, Inventory", listOf(
            "LOOK(&yaw, &pitch)",
            "  Stores current yaw and pitch",
            "SETYAW(yaw) / SETPITCH(pitch)",
            "  Sets rotation",
            "GETSLOT(#slot, &item, &count)",
            "  Gets item and count from hotbar slot (0-8)",
            "SETSLOT(#slot, item, count)",
            "  Sets item in hotbar slot",
            "HOTBAR(#slot)",
            "  Switches hotbar slot (0-8)",
            "DROP()",
            "  Drops held item",
            "DROPALL()",
            "  Drops all items in stack",
            "CLICKSLOT(#slot)",
            "  Clicks a slot in the current container",
            "CLOSECONTAINER()",
            "  Closes the current container"
        )),
        HelpSection("Movement", listOf(
            "FORWARD(ticks)",
            "  Moves forward for specified ticks",
            "BACK(ticks)",
            "  Moves backward for specified ticks",
            "LEFT(ticks)",
            "  Strafes left for specified ticks",
            "RIGHT(ticks)",
            "  Strafes right for specified ticks",
            "JUMP()",
            "  Jumps",
            "SPRINT(on/off)",
            "  Toggles sprint",
            "SNEAK(on/off)",
            "  Toggles sneak",
            "SWIM(on/off)",
            "  Toggles swim"
        )),
        HelpSection("Events", listOf(
            "onJoinGame",
            "  Fires when a world/player becomes available",
            "onWorldChange",
            "  Fires when joining or leaving a world",
            "onChat",
            "  Fires when receiving a chat message. Variables: %CHAT%, %CHATCLEAN%"
        ))
    )

    private data class HelpSection(
        val title: String,
        val items: List<String>
    )

    override fun init() {
        addRenderableWidget(
            Button.builder(Component.literal("Back")) {
                minecraft?.setScreen(previousScreen)
            }.bounds(width - 70, height - 30, 60, 20).build()
        )
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderTransparentBackground(graphics)
        
        val title = MacroFonts.text("MacroEngine Command Reference")
        graphics.drawString(font, title, (width - font.width(title)) / 2, 20, Color.WHITE.rgb, true)
        
        val baseY = 50
        var currentY = baseY - scrollOffset
        
        helpSections.forEach { section ->
            val sectionTitle = MacroFonts.text(section.title)
            graphics.drawString(font, sectionTitle, 30, currentY, Color(100, 200, 255).rgb, true)
            currentY += 20
            
            section.items.forEach { item ->
                val lines = wrapText(item, width - 60)
                lines.forEach { line ->
                    if (currentY >= 0 && currentY < height - 20) {
                        val isCommand = line.contains("(") && !line.startsWith("  ")
                        val color = if (isCommand) Color(255, 200, 100).rgb else Color(180, 180, 180).rgb
                        graphics.drawString(font, MacroFonts.text(line), 30, currentY, color, false)
                    }
                    currentY += 12
                }
            }
            currentY += 15
        }
        
        drawScrollBar(graphics)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, horizontalAmount: Double, verticalAmount: Double): Boolean {
        scrollOffset += if (verticalAmount < 0) 30 else -30
        val maxScroll = calculateMaxScroll()
        scrollOffset = scrollOffset.coerceIn(0, maxScroll)
        return true
    }

    private fun calculateMaxScroll(): Int {
        var totalHeight = 0
        helpSections.forEach { section ->
            totalHeight += 20
            section.items.forEach { item ->
                totalHeight += wrapText(item, width - 60).size * 12
            }
            totalHeight += 15
        }
        val visibleHeight = height - 70
        return if (totalHeight > visibleHeight) totalHeight - visibleHeight + 20 else 0
    }

    private fun wrapText(text: String, maxWidth: Int): List<String> {
        val lines = mutableListOf<String>()
        var currentLine = ""
        
        text.split(" ").forEach { word ->
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (font.width(MacroFonts.text(testLine)) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        
        return lines
    }

    private fun drawScrollBar(graphics: GuiGraphics) {
        val maxScroll = calculateMaxScroll()
        if (maxScroll <= 0) return
        
        val barHeight = 40
        val scrollAreaHeight = height - 100
        val barX = width - 25
        val barY = 50 + ((scrollOffset.toFloat() / maxScroll) * (scrollAreaHeight - barHeight)).toInt()
        
        graphics.fill(barX, 50, barX + 10, height - 50, Color(60, 60, 60).rgb)
        graphics.fill(barX, barY, barX + 10, barY + barHeight, Color(120, 120, 120).rgb)
    }

    override fun isPauseScreen(): Boolean = false
}
