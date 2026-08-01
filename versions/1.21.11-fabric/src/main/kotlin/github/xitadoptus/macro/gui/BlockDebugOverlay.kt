package github.xitadoptus.macro.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.awt.Color

object BlockDebugOverlay {
    var enabled = false
    
    fun render(graphics: GuiGraphics, minecraft: Minecraft) {
        if (!enabled) return
        if (minecraft.screen != null) return
        val level = minecraft.level ?: return
        val player = minecraft.player ?: return
        
        val hitResult = minecraft.hitResult
        if (hitResult == null || hitResult.type != HitResult.Type.BLOCK) return
        
        val blockHit = hitResult as BlockHitResult
        val pos = blockHit.blockPos
        val state = level.getBlockState(pos)
        val block = state.block
        
        val blockId = block.getDescriptionId()
        
        val blockStateStr = state.toString()
        
        val info = listOf(
            "§fBlock: §e${block.getName().string}",
            "§fID: §b$blockId",
            "§fPosition: §a${pos.x}, ${pos.y}, ${pos.z}",
            "§fState: §7$blockStateStr",
            "§fSide: §c${blockHit.direction.name}"
        )
        
        var y = 10
        info.forEach { line ->
            graphics.drawString(minecraft.font, line, 10, y, Color.WHITE.rgb, false)
            y += 12
        }
    }
}
