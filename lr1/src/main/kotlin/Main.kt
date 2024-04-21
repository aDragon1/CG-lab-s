package org.example

import processing.core.PApplet
import kotlin.math.roundToInt

fun main() {
    Art.run()
}

class Art : PApplet() {
    companion object Factory {
        fun run() {
            val art = Art()
            art.setSize(800, 800)
            art.runSketch()
        }
    }

    data class CircleStuff(
        val x1: Float,
        var y1: Float,
        val r: Float,
        var x: Float = 0f,
        var y: Float = 0f,
        var delta: Float = 0f,
        var error: Float = 0f,
        var isInit: Boolean = false
    )

    data class BrezenhemLineStuff(
        var x1: Int,
        var y1: Int,
        var x2: Int,
        var y2: Int,
        var x: Int = 0,
        var y: Int = 0,
        var dx: Int = 0,
        var dy: Int = 0,
        var error: Int = 0,
        var isInit: Boolean = false
    )

    data class DDALineStuff(
        var x1: Float,
        var y1: Float,
        var x2: Float,
        var y2: Float,
        var x: Float = 0f,
        var y: Float = 0f,
        var xInc: Float = 0f,
        var yInc: Float = 0f,
        var step: Int = 0,
        var isInit: Boolean = false
    )

    private var translatedX = 0f
    private var translatedY = 0f

    private lateinit var circleStuff: CircleStuff
    private lateinit var brezenhem4LineStuff: BrezenhemLineStuff
    private lateinit var ddaLineStuff: DDALineStuff
    private var ddaIteration: Int = 0

    @Suppress("SpellCheckingInspection")
    override fun setup() {
        rectMode(CENTER)

        background(255)
        stroke(0)
        strokeWeight(1f)


        val brezhX1 = 0
        val brezhY1 = 0
        val brezhX2 = width / 2 - 10
        val brezhY2 = height / 2 - 10

        val ddaX1 = 0f
        val ddaY1 = 0f
        val ddaX2 = width / 2f - 10
        val ddaY2 = -height / 2f + 10

        brezenhem4LineStuff = BrezenhemLineStuff(brezhX1, brezhY1, brezhX2, brezhY2)
        ddaLineStuff = DDALineStuff(ddaX1, ddaY1, ddaX2, ddaY2)

        circleStuff = CircleStuff(0f, 0f, 250f)

        translatedX = width / 2f
        translatedY = height / 2f
    }


    override fun draw() {
        translate(translatedX, translatedY)
        stroke(0)
        fill(0)
        line(-width / 2f, 0f, width / 2f, 0f)
        line(0f, -height / 2f, 0f, height / 2f)

        bresenhamLine()
        ddaLine()
        brezenhamCircle()
    }


    private fun ddaLine() {
        ddaLineStuff.apply {
            fill(0f, 255f, 0f)
            ellipse(x1, y1, 10f, 10f)

            fill(255f, 0f, 0f)
            ellipse(x2, y2, 10f, 10f)
            if (!isInit) {
                x = x1
                y = y1
                val dx = x2 - x1
                val dy = y2 - y1
                step = max(abs(dx), abs(dy)).roundToInt()
                xInc = dx / step
                yInc = dy / step
                isInit = true
            }

            if (ddaIteration >= step) return@apply

            stroke(145f, 22f, 112f)
            rect(x, y, 5f, 5f)
            x += xInc
            y += yInc
            ddaIteration++
        }
    }

    private fun bresenhamLine() =
        brezenhem4LineStuff.apply {
            fill(0f, 255f, 0f)
            ellipse(x1.toFloat(), y1.toFloat(), 10f, 10f)

            fill(255f, 0f, 0f)
            ellipse(x2.toFloat(), y2.toFloat(), 10f, 10f)
            if (!isInit) {
                x = x1
                y = y1
                dx = x2 - x1
                dy = y2 - y1
                error = 2 * dy - dx
                isInit = true
            }

            if (y >= y2) return@apply

            stroke(41f, 135f, 145f)
            rect(x.toFloat(), y.toFloat(), 5f, 5f)

            while (error > 0) {
                y++
                error -= 2 * dx
            }

            x++
            error += 2 * dy
        }


    private fun brezenhamCircle() {
        circleStuff.apply {
            if (!isInit) {
                x = 0f
                y = r
                delta = 1 - 2 * r
                error = 0f
                isInit = true
            }
            if (y < x) return

            stroke(0f, 0f, 255f) // E - NE (1)
            rect(x1 + y, y1 - x, 10f, 10f)

            stroke(128f, 0f, 128f) // N - NE (2)
            rect(x1 + x, y1 - y, 10f, 10f)

            stroke(255f, 0f, 0f) // N - NW (3)
            rect(x1 - x, y1 - y, 10f, 10f)

            stroke(255f, 165f, 0f) // NW - W (4)
            rect(x1 - y, y1 - x, 10f, 10f)

            stroke(255f, 255f, 0f) // W - SW (5)
            rect(x1 - y, y1 + x, 10f, 10f)

            stroke(255f, 192f, 203f) // SW - S (6)
            rect(x1 - x, y1 + y, 10f, 10f)

            stroke(0f, 255f, 255f) // S - SE (7)
            rect(x1 + x, y1 + y, 10f, 10f)

            stroke(0f, 255f, 0f) // SE - E (8)
            rect(x1 + y, y1 + x, 10f, 10f)

            error = 2 * (delta + y) - 1

            if ((delta < 0) && (error <= 0)) {
                delta += 2 * ++x + 1
                return
            }
            if ((delta > 0) && (error > 0)) {
                delta -= 2 * --y + 1
                return
            }
            delta += 2 * (++x - --y)
        }
    }
}