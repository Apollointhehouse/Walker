package dev.apollointhehouse.walker.utils.math

import dev.apollointhehouse.walker.entity.Entity
import dev.apollointhehouse.walker.level.Level
import dev.apollointhehouse.walker.level.MutableLevel
import dev.apollointhehouse.walker.level.tile.TileAir
import dev.apollointhehouse.walker.level.tile.TilePos
import dev.apollointhehouse.walker.level.tile.TilePosc
import dev.apollointhehouse.walker.level.tile.TileType
import org.joml.Vector2d
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RaycastTest {

    class MockEntity(override val pos: Vector2d) : Entity {
        override val oldPos = pos
        override val velocity = Vector2d()
        override val oldVelocity = Vector2d()
        override val angle = 0.0
        override val angleO = 0.0
        override val bb = AABB2d(Vector2d(-16.0, -16.0), Vector2d(16.0, 16.0))
        override var level: Level? = null
        override fun tick() {}
        override fun render(deltaTime: Double) {}
    }

    class MockLevel : MutableLevel {
        override val entities: List<Entity> field = mutableListOf<Entity>()
        override val tileSize: Int = 64
        override val size: Int = 100
        override val mapX: Int = 10
        override val mapY: Int = 10

        override fun addEntity(entity: Entity) {
            entities.add(entity)
            entity.level = this
        }

        override fun getEntities(tilePos: TilePosc): List<Entity> {
            val tileMin = Vector2d(tilePos.x * tileSize.toDouble(), tilePos.y * tileSize.toDouble())
            val tileMax = Vector2d(tileMin.x() + tileSize, tileMin.y() + tileSize)

            return entities.filter { entity ->
                val eBB = entity.bb + entity.pos
                eBB.minX() < tileMax.x() && eBB.maxX() > tileMin.x() &&
                        eBB.minY() < tileMax.y() && eBB.maxY() > tileMin.y()
            }
        }

        override fun get(x: Int, y: Int): TilePosc = TilePos(x, y)
        override fun getType(x: Int, y: Int): TileType = TileAir
        override fun getType(tilePosc: TilePosc): TileType = TileAir
    }

    @Test
    fun testRaycastHitsOtherEntity() {
        val casterPos = Vector2d(60.0, 60.0)
        val targetPos = Vector2d(200.0, 60.0)

        val caster = MockEntity(casterPos)
        val target = MockEntity(targetPos)
        
        val level = MockLevel()
        level.addEntity(caster)
        level.addEntity(target)
        
        val hit = raycast(casterPos, level, 0.0)
        
        assertNotNull(hit, "Raycast should hit the target entity")
        assertTrue(hit is HitResult.EntityHit, "Hit should be an EntityHit")
        assertSame(hit.entity, target, "Should hit target, not caster")
    }

    @Test
    fun testRaycastDoesNotHitCaster() {
        val casterPos = Vector2d(60.0, 60.0)
        
        val caster = MockEntity(casterPos)

        val level = MockLevel()
        level.addEntity(caster)
        
        val hit = raycast(casterPos, level, 0.0)
        
        if (hit is HitResult.EntityHit) {
            assertTrue(hit.entity !== caster, "Should NOT hit caster")
        }
    }
}
