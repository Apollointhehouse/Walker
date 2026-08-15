package dev.apollointhehouse.walker.level

import dev.apollointhehouse.walker.Game
import dev.apollointhehouse.walker.Tickable
import dev.apollointhehouse.walker.entity.Entity
import dev.apollointhehouse.walker.entity.Player
import dev.apollointhehouse.walker.input.Input
import dev.apollointhehouse.walker.level.tile.TileAir
import dev.apollointhehouse.walker.level.tile.TilePos
import dev.apollointhehouse.walker.level.tile.TilePosc
import dev.apollointhehouse.walker.render.Drawable
import dev.apollointhehouse.walker.render.Renderer
import org.joml.Vector2d
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL11

class DrawableLevel(player: Player, private val level: Level) : Level by level, Drawable, Tickable {
    override val entities: List<Entity> field = mutableListOf<Entity>()

    init {
        Renderer.addDrawable(this)

        addEntity(player)
    }

    override fun get(x: Int, y: Int): TilePos {
        return TilePos(x, y)
    }

    fun addEntity(entity: Entity) {
        Renderer.addDrawable(entity)
        entities.add(entity)
        entity.level = this
    }

    override fun getEntities(tilePos: TilePosc): List<Entity> {
        val tileMin = Vector2d(tilePos.x * level.tileSize.toDouble(), tilePos.y * level.tileSize.toDouble())
        val tileMax = Vector2d(tileMin.x() + level.tileSize, tileMin.y() + level.tileSize)

        return entities.filter { entity ->
            val eBB = entity.bb + entity.pos
            eBB.minX() < tileMax.x() && eBB.maxX() > tileMin.x() &&
                    eBB.minY() < tileMax.y() && eBB.maxY() > tileMin.y()
        }
    }

    override fun render(deltaTime: Double) {
        if (!Input.isKeyDown(GLFW.GLFW_KEY_K)) return

        val gap = 1

        Game.camera.apply(deltaTime) {
            for (row in 0..<mapY) {
                for (col in 0..<mapX) {
                    if (getType(TilePos(col, row)) !is TileAir) GL11.glColor3f(1f, 1f, 1f) else GL11.glColor3f(0f, 0f, 0f)

                    val left   = (col)     * tileSize + gap
                    val right  = (col + 1) * tileSize - gap
                    val top    = (row)     * tileSize + gap
                    val bottom = (row + 1) * tileSize - gap

                    GL11.glBegin(GL11.GL_QUADS)
                    GL11.glVertex2i(left,  top)
                    GL11.glVertex2i(left,  bottom)
                    GL11.glVertex2i(right, bottom)
                    GL11.glVertex2i(right, top)
                    GL11.glEnd()
                }
            }
        }
    }

    override fun tick() {
        for (entity in entities) {
            entity.tick()
        }
    }
}