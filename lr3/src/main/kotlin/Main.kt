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

    enum class Direction { X_AXIS, Y_AXIS, Z_AXIS, NULL }

    private val points: MutableList<PVector> = mutableListOf()
    private var angle = 0f
    private val deltaAngle = 0.03f

    private var dir = Direction.NULL

    private var coef = 200f
    private val coefDelta = 10

    private var cX = 0f
    private var cY = 0f

    override fun setup() {
        points.add(PVector(-0.5f, -0.5f, -0.5f))
        points.add(PVector(0.5f, -0.5f, -0.5f))
        points.add(PVector(0.5f, 0.5f, -0.5f))
        points.add(PVector(-0.5f, 0.5f, -0.5f))
        points.add(PVector(-0.5f, -0.5f, 0.5f))
        points.add(PVector(0.5f, -0.5f, 0.5f))
        points.add(PVector(0.5f, 0.5f, 0.5f))
        points.add(PVector(-0.5f, 0.5f, 0.5f))

        points.forEach { println(it) }
        cX = width / 2f
        cY = height / 2f
    }

    override fun draw() {
        background(255)
        angle += deltaAngle // inc angle

        val rotationX = createRotationXMatrix(angle)
        val rotationY = createRotationYMatrix(angle)
        val rotationZ = createRotationZMatrix(angle)

//        rotate and project point's
        val projectedPoints: MutableList<PVector> = mutableListOf()
        points.forEach {
            val resVector = funnyMathBehind(it, rotationX, rotationY, rotationZ) ?: return
            projectedPoints.add(resVector)
        }

//        translate (0,0) to new center
        translate(cX, cY)

//        plot point at new center
        strokeWeight(12f)
        stroke(200f, 100f, 50f)
        point(0f, 0f)

//        draw shifted point's
        stroke(0)
        strokeWeight(8f)
        projectedPoints.forEach { point(it.x, it.y) }


//        draw line between 'em
        strokeWeight(2f)
        for (i in 0 until 4) {
            connect(i, (i + 1) % 4, projectedPoints)
            connect(i + 4, (i + 1) % 4 + 4, projectedPoints)
            connect(i + 4, i, projectedPoints)
        }
    }

    private fun funnyMathBehind(
        vec: PVector,
        rotationX: List<List<Float>>,
        rotationY: List<List<Float>>,
        rotationZ: List<List<Float>>
    ): PVector? {
        val rotated = when (dir) {
            Direction.X_AXIS -> matrixToVec(multiply(rotationX, vecToMatrix(vec)) ?: return null)
            Direction.Y_AXIS -> matrixToVec(multiply(rotationY, vecToMatrix(vec)) ?: return null)
            Direction.Z_AXIS -> matrixToVec(multiply(rotationZ, vecToMatrix(vec)) ?: return null)
            Direction.NULL -> return null
        }
//        var rotated = matrixToVec(multiply(rotationX, vecToMatrix(vec)) ?: return null)
//        rotated = matrixToVec(multiply(rotationX, vecToMatrix(rotated)) ?: return null)
//        rotated = matrixToVec(multiply(rotationZ, vecToMatrix(rotated)) ?: return null)

//        val dist = 2
//        val z = 1 / (dist - rotated.z)
//        val projection = listOf(
//            listOf(z, 0f, 0f),
//            listOf(0f, z, 0f),
//        )
//        val projected = matrixToVec(multiply(projection, vecToMatrix(rotated)) ?: return null)
//        projected.mult(300f)
//        return projected

        rotated.mult(300f) // Для 4й лабы раскомментить строки выше и удалить эту
        return rotated
    }

    private fun createRotationXMatrix(angle: Float) = listOf(
        listOf(1f, 0f, 0f),
        listOf(0f, cos(angle), -sin(angle)),
        listOf(0f, sin(angle), cos(angle))
    )

    private fun createRotationYMatrix(angle: Float) = listOf(
        listOf(cos(angle), 0f, sin(angle)),
        listOf(0f, 1f, 0f),
        listOf(-sin(angle), 0f, cos(angle))
    )

    private fun createRotationZMatrix(angle: Float) = listOf(
        listOf(cos(angle), -sin(angle), 0f),
        listOf(sin(angle), cos(angle), 0f),
        listOf(0f, 0f, 1f)
    )

    private fun connect(i: Int, j: Int, rotated: List<PVector>) {
        val a = rotated[i]
        val b = rotated[j]
        line(a.x, a.y, b.x, b.y)
    }

    private fun multiply(matrixA: List<List<Float>>, matrixB: List<List<Float>>): List<List<Float>>? {
        if (matrixA[0].size != matrixB.size) {
            println("Эти матрицы нельзя перемножить")
            return null
        }
        val res = List(matrixA.size) { MutableList(matrixB[0].size) { 0f } }
        for (i in res.indices)
            for (j in res[0].indices)
                for (k in matrixA[0].indices)
                    res[i][j] += matrixA[i][k] * matrixB[k][j]
        return res
    }

    private fun vecToMatrix(vec: PVector): List<List<Float>> =
        listOf(listOf(vec.x, 0f, 0f), listOf(vec.y, 0f, 0f), listOf(vec.z, 0f, 0f))

    private fun matrixToVec(m: List<List<Float>>): PVector =
        if (m.size == 3) PVector(m[0][0], m[1][0], m[2][0]) else PVector(m[0][0], m[1][0])

    override fun keyPressed() {
        println(key)
        dir = when (keyCode) {
            88 -> Direction.X_AXIS
            89 -> Direction.Y_AXIS
            90 -> Direction.Z_AXIS
            else -> dir
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