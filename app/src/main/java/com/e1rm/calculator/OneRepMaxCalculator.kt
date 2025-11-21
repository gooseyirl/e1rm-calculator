package com.e1rm.calculator

import kotlin.math.pow

/**
 * One Rep Max Calculator using RPE (Rate of Perceived Exertion)
 * This follows the Barbell Medicine approach which incorporates RPE into 1RM calculations
 */
object OneRepMaxCalculator {

    /**
     * RPE to percentage table based on Mike Tuchscherer's RPE chart
     * This maps RPE values to the percentage of 1RM
     */
    private val rpePercentageTable = mapOf(
        // RPE 10 = max effort (0 RIR)
        10.0 to mapOf(1 to 100.0, 2 to 95.5, 3 to 92.2, 4 to 89.2, 5 to 86.3, 6 to 83.7, 7 to 81.1, 8 to 78.6, 9 to 76.2, 10 to 74.0),
        // RPE 9.5 (0.5 RIR)
        9.5 to mapOf(1 to 97.8, 2 to 93.9, 3 to 90.7, 4 to 87.8, 5 to 85.0, 6 to 82.4, 7 to 79.9, 8 to 77.4, 9 to 75.1, 10 to 72.9),
        // RPE 9 (1 RIR)
        9.0 to mapOf(1 to 95.5, 2 to 92.2, 3 to 89.2, 4 to 86.3, 5 to 83.7, 6 to 81.1, 7 to 78.6, 8 to 76.2, 9 to 74.0, 10 to 71.8),
        // RPE 8.5 (1.5 RIR)
        8.5 to mapOf(1 to 93.9, 2 to 90.7, 3 to 87.8, 4 to 85.0, 5 to 82.4, 6 to 79.9, 7 to 77.4, 8 to 75.1, 9 to 72.9, 10 to 70.7),
        // RPE 8 (2 RIR)
        8.0 to mapOf(1 to 92.2, 2 to 89.2, 3 to 86.3, 4 to 83.7, 5 to 81.1, 6 to 78.6, 7 to 76.2, 8 to 74.0, 9 to 71.8, 10 to 69.7),
        // RPE 7.5 (2.5 RIR)
        7.5 to mapOf(1 to 90.7, 2 to 87.8, 3 to 85.0, 4 to 82.4, 5 to 79.9, 6 to 77.4, 7 to 75.1, 8 to 72.9, 9 to 70.7, 10 to 68.6),
        // RPE 7 (3 RIR)
        7.0 to mapOf(1 to 89.2, 2 to 86.3, 3 to 83.7, 4 to 81.1, 5 to 78.6, 6 to 76.2, 7 to 74.0, 8 to 71.8, 9 to 69.7, 10 to 67.6),
        // RPE 6.5 (3.5 RIR)
        6.5 to mapOf(1 to 87.8, 2 to 85.0, 3 to 82.4, 4 to 79.9, 5 to 77.4, 6 to 75.1, 7 to 72.9, 8 to 70.7, 9 to 68.6, 10 to 66.6),
        // RPE 6 (4 RIR)
        6.0 to mapOf(1 to 86.3, 2 to 83.7, 3 to 81.1, 4 to 78.6, 5 to 76.2, 6 to 74.0, 7 to 71.8, 8 to 69.7, 9 to 67.6, 10 to 65.6)
    )

    /**
     * Calculate estimated 1RM using RPE-based method
     *
     * @param weight The weight lifted
     * @param reps Number of repetitions performed (1-10)
     * @param rpe Rate of Perceived Exertion (6.0-10.0)
     * @return Estimated one rep max, or null if inputs are invalid
     */
    fun calculateOneRepMax(weight: Double, reps: Int, rpe: Double): Double? {
        // Validate inputs
        if (weight <= 0) return null
        if (reps < 1 || reps > 10) return null
        if (rpe < 6.0 || rpe > 10.0) return null

        // Get the percentage from the RPE table
        val percentageOfMax = rpePercentageTable[rpe]?.get(reps) ?: return null

        // Calculate 1RM: if weight is X% of 1RM, then 1RM = weight / (X/100)
        return weight / (percentageOfMax / 100.0)
    }

    /**
     * Calculate estimated reps at a given weight based on 1RM
     *
     * @param oneRepMax The calculated or known 1RM
     * @param weight The target weight
     * @param rpe The target RPE
     * @return Estimated number of reps possible at the given weight and RPE
     */
    fun calculateRepsAtWeight(oneRepMax: Double, weight: Double, rpe: Double): Int? {
        if (oneRepMax <= 0 || weight <= 0 || weight > oneRepMax) return null
        if (rpe < 6.0 || rpe > 10.0) return null

        val percentageOfMax = (weight / oneRepMax) * 100.0
        val rpeRow = rpePercentageTable[rpe] ?: return null

        // Find the closest number of reps for this percentage
        var closestReps = 1
        var closestDiff = Double.MAX_VALUE

        for ((reps, percentage) in rpeRow) {
            val diff = kotlin.math.abs(percentage - percentageOfMax)
            if (diff < closestDiff) {
                closestDiff = diff
                closestReps = reps
            }
        }

        return closestReps
    }

    /**
     * Get all supported RPE values
     */
    fun getSupportedRpeValues(): List<Double> {
        return rpePercentageTable.keys.sorted()
    }

    /**
     * Calculate weight for a target rep range and RPE
     *
     * @param oneRepMax The known 1RM
     * @param reps Target number of reps
     * @param rpe Target RPE
     * @return Weight to use for the given reps and RPE
     */
    fun calculateWeightForReps(oneRepMax: Double, reps: Int, rpe: Double): Double? {
        if (oneRepMax <= 0) return null
        if (reps < 1 || reps > 10) return null
        if (rpe < 6.0 || rpe > 10.0) return null

        val percentageOfMax = rpePercentageTable[rpe]?.get(reps) ?: return null
        return oneRepMax * (percentageOfMax / 100.0)
    }
}
