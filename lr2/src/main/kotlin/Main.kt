package org.example

import processing.core.PApplet
import processing.core.PVector
import processing.event.MouseEvent

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

    private val points: MutableList<PVector> = mutableListOf()
    private var coef = 200f
    private val coefDelta = 10

    private var cX = 0f
    private var cY = 0f

    override fun setup() {
        points.add(PVector(0.5f, 0.25f))
        points.add(PVector(0f, -0.25f))
        points.add(PVector(-0.5f, 0.25f))
        points.forEach { it.mult(coef) }
    }

    override fun draw() {
        translate(0f, 0f)
        background(255)
        stroke(0)
        strokeWeight(2f)
        line(0f, height / 2f, width.toFloat(), height / 2f)
        line(width / 2f, 0f, width / 2f, height.toFloat())

        strokeWeight(10f)
        stroke(0f, 255f, 0f)
        point(cX, cY)

        translate(cX, cY)
        strokeWeight(8f)
        stroke(255f, 0f, 0f)
        for (i in points.indices) {
            val a = points[i % 3]
            val b = points[(i + 1) % 3]
            line(a.x, a.y, b.x, b.y)
        }
    }

    override fun mouseWheel(event: MouseEvent?) {
        when (event?.count) {
            1 -> {
                if (coef - coefDelta < 0) return
                val old = coef
                coef -= coefDelta
                points.forEach { it.mult(coef / old) }
            }

            -1 -> {
                val old = coef
                coef += coefDelta
                points.forEach { it.mult(coef / old) }
            }
        }

        println(coef)
    }

    override fun mouseClicked() {
        cX = mouseX.toFloat()
        cY = mouseY.toFloat()
    }
}