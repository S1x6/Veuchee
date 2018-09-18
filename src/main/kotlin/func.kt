import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

private enum class Direction {
    LEFT,
    RIGHT
}

private const val searchInterval = 1.0

class Solver constructor(private val a: Double,
                         private val b: Double,
                         private val c: Double,
                         private val e: Double) {

    fun getRoots(): ArrayList<Double> {
        val res = ArrayList<Double>()
        val discriminant = getDerivativeDiscriminant()
        if (discriminant <= 0.0) { // case with one root when f'(x) has no roots or only one root
            val x1 = 0.0
            val x2 = when {
                getFunctionValue(0.0) < -e -> findSignChange(x1, Direction.RIGHT, ::getFunctionValue)
                getFunctionValue(0.0) > e -> findSignChange(x1, Direction.LEFT, ::getFunctionValue)
                else -> 0.0
            }
            res.add(getRootBySegmentDivision(x1, x2, ::getFunctionValue))
        } else {
            val dRoots = getDerivativeRoots()
            val dRoot1 = min(dRoots!![0], dRoots[1])
            val dRoot2 = max(dRoots[0], dRoots[1])
            val y1 = getFunctionValue(dRoot1)
            val y2 = getFunctionValue(dRoot2)
            if (y1 > e && y2 > e) { // cases with one root when f'(x) has two roots (1)
                val x1 = findSignChange(dRoot1, Direction.LEFT, ::getFunctionValue)
                res.add(getRootBySegmentDivision(x1, dRoot1, ::getFunctionValue))
            } else if (y1 < -e && y2 < -e) { // cases with one root when f'(x) has two roots (2)
                val x2 = findSignChange(dRoot2, Direction.RIGHT, ::getFunctionValue)
                res.add(getRootBySegmentDivision(dRoot2, x2, ::getFunctionValue))
            } else if (y1 > e && y2 < -e) { // case with three roots
                var x2 = dRoot1
                var x1 = findSignChange(x2, Direction.LEFT, ::getFunctionValue)
                res.add(getRootBySegmentDivision(x1, x2, ::getFunctionValue))
                res.add(getRootBySegmentDivision(dRoot1, dRoot2, ::getFunctionValue))
                x1 = dRoot2
                x2 = findSignChange(x1, Direction.RIGHT, ::getFunctionValue)
                res.add(getRootBySegmentDivision(x1, x2, ::getFunctionValue))
            } else if (y1 > e) { // case with two roots (1)
                val x1 = findSignChange(dRoot1, Direction.LEFT, ::getFunctionValue)
                res.add(getRootBySegmentDivision(x1, dRoot1, ::getFunctionValue))
                res.add(dRoot2)
            } else { // case with two roots (2)
                val x2 = findSignChange(dRoot2, Direction.RIGHT, ::getFunctionValue)
                res.add(getRootBySegmentDivision(dRoot2, x2, ::getFunctionValue))
                res.add(dRoot1)
            }
        }
        return res
    }

    private fun getFunctionValue(x: Double): Double {
        return x * x * x + a * x * x + b * x + c
    }

    private fun getDerivativeValue(x: Double): Double {
        return 3 * x * x + 2 * a * x + b
    }

    private fun getDerivativeDiscriminant(): Double {
        return 4 * a * a - 12 * b
    }

    private fun getDerivativeRoots(): ArrayList<Double>? {
        val d = getDerivativeDiscriminant()
        return if (d > 0) ArrayList<Double>().also { list -> list.add(((-2 * a) + sqrt(d)) / 6) }.also { list -> list.add(((-2 * a) - sqrt(d)) / 6) } else null
    }

    private fun findSignChange(startX: Double, direction: Direction, func: (Double) -> Double): Double {
        var x2 = startX
        if (func(x2) < 0.0) {
            do {
                x2 = if (direction == Direction.RIGHT) x2 + searchInterval else x2 - searchInterval
            } while (func(x2) <= 0.0)
        } else {
            do {
                x2 = if (direction == Direction.RIGHT) x2 + searchInterval else x2 - searchInterval
            } while (func(x2) >= 0.0)
        }
        return x2
    }

    private fun getRootBySegmentDivision(x1_: Double, x2_: Double, func: (Double) -> Double): Double {
        var x1 = min(x1_, x2_)
        var x2 = max(x1_, x2_)
        var z: Double
        var f: Double
        val inc = getDerivativeValue((x1 + x2) / 2) > 0 // true if function is increasing
        do {
            z = (x1 + x2) / 2
            f = func(z)
            if (inc) {
                if (f > e) x2 = z else if (f < -e) x1 = z
            } else {
                if (f > e) x1 = z else if (f < -e) x2 = z
            }
        } while (abs(f) > e)
        return z
    }
}