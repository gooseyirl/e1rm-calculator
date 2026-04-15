import Foundation

/**
 * One Rep Max Calculator using RPE (Rate of Perceived Exertion)
 * This follows the Barbell Medicine approach which incorporates RPE into 1RM calculations
 */
struct OneRepMaxCalculator {

    /**
     * RPE to percentage table based on Mike Tuchscherer's RPE chart
     * This maps RPE values to the percentage of 1RM
     */
    private static let rpePercentageTable: [Double: [Int: Double]] = [
        // RPE 10 = max effort (0 RIR)
        10.0: [1: 100.0, 2: 95.5, 3: 92.2, 4: 89.2, 5: 86.3, 6: 83.7, 7: 81.1, 8: 78.6, 9: 76.2, 10: 74.0],
        // RPE 9.5 (0.5 RIR)
        9.5: [1: 97.8, 2: 93.9, 3: 90.7, 4: 87.8, 5: 85.0, 6: 82.4, 7: 79.9, 8: 77.4, 9: 75.1, 10: 72.9],
        // RPE 9 (1 RIR)
        9.0: [1: 95.5, 2: 92.2, 3: 89.2, 4: 86.3, 5: 83.7, 6: 81.1, 7: 78.6, 8: 76.2, 9: 74.0, 10: 71.8],
        // RPE 8.5 (1.5 RIR)
        8.5: [1: 93.9, 2: 90.7, 3: 87.8, 4: 85.0, 5: 82.4, 6: 79.9, 7: 77.4, 8: 75.1, 9: 72.9, 10: 70.7],
        // RPE 8 (2 RIR)
        8.0: [1: 92.2, 2: 89.2, 3: 86.3, 4: 83.7, 5: 81.1, 6: 78.6, 7: 76.2, 8: 74.0, 9: 71.8, 10: 69.7],
        // RPE 7.5 (2.5 RIR)
        7.5: [1: 90.7, 2: 87.8, 3: 85.0, 4: 82.4, 5: 79.9, 6: 77.4, 7: 75.1, 8: 72.9, 9: 70.7, 10: 68.6],
        // RPE 7 (3 RIR)
        7.0: [1: 89.2, 2: 86.3, 3: 83.7, 4: 81.1, 5: 78.6, 6: 76.2, 7: 74.0, 8: 71.8, 9: 69.7, 10: 67.6],
        // RPE 6.5 (3.5 RIR)
        6.5: [1: 87.8, 2: 85.0, 3: 82.4, 4: 79.9, 5: 77.4, 6: 75.1, 7: 72.9, 8: 70.7, 9: 68.6, 10: 66.6],
        // RPE 6 (4 RIR)
        6.0: [1: 86.3, 2: 83.7, 3: 81.1, 4: 78.6, 5: 76.2, 6: 74.0, 7: 71.8, 8: 69.7, 9: 67.6, 10: 65.6],
        // RPE 5.5 (4.5 RIR)
        5.5: [1: 84.8, 2: 82.4, 3: 79.8, 4: 77.3, 5: 75.0, 6: 72.9, 7: 70.7, 8: 68.7, 9: 66.6, 10: 64.6],
        // RPE 5 (5 RIR)
        5.0: [1: 83.3, 2: 81.1, 3: 78.5, 4: 76.0, 5: 73.8, 6: 71.8, 7: 69.6, 8: 67.7, 9: 65.6, 10: 63.6]
    ]

    /**
     * Calculate estimated 1RM using RPE-based method
     *
     * - Parameters:
     *   - weight: The weight lifted
     *   - reps: Number of repetitions performed (1-10)
     *   - rpe: Rate of Perceived Exertion (6.0-10.0)
     * - Returns: Estimated one rep max, or nil if inputs are invalid
     */
    static func calculateOneRepMax(weight: Double, reps: Int, rpe: Double) -> Double? {
        // Validate inputs
        guard weight > 0 else { return nil }
        guard reps >= 1 && reps <= 10 else { return nil }
        guard rpe >= 5.0 && rpe <= 10.0 else { return nil }

        // Get the percentage from the RPE table
        guard let rpeRow = rpePercentageTable[rpe],
              let percentageOfMax = rpeRow[reps] else {
            return nil
        }

        // Calculate 1RM: if weight is X% of 1RM, then 1RM = weight / (X/100)
        return weight / (percentageOfMax / 100.0)
    }

    /**
     * Calculate estimated reps at a given weight based on 1RM
     *
     * - Parameters:
     *   - oneRepMax: The calculated or known 1RM
     *   - weight: The target weight
     *   - rpe: The target RPE
     * - Returns: Estimated number of reps possible at the given weight and RPE
     */
    static func calculateRepsAtWeight(oneRepMax: Double, weight: Double, rpe: Double) -> Int? {
        guard oneRepMax > 0 && weight > 0 && weight <= oneRepMax else { return nil }
        guard rpe >= 5.0 && rpe <= 10.0 else { return nil }

        let percentageOfMax = (weight / oneRepMax) * 100.0
        guard let rpeRow = rpePercentageTable[rpe] else { return nil }

        // Find the closest number of reps for this percentage
        var closestReps = 1
        var closestDiff = Double.greatestFiniteMagnitude

        for (reps, percentage) in rpeRow {
            let diff = abs(percentage - percentageOfMax)
            if diff < closestDiff {
                closestDiff = diff
                closestReps = reps
            }
        }

        return closestReps
    }

    /**
     * Get all supported RPE values
     */
    static func getSupportedRpeValues() -> [Double] {
        return rpePercentageTable.keys.sorted()
    }

    /**
     * Calculate weight for a target rep range and RPE
     *
     * - Parameters:
     *   - oneRepMax: The known 1RM
     *   - reps: Target number of reps
     *   - rpe: Target RPE
     * - Returns: Weight to use for the given reps and RPE
     */
    static func calculateWeightForReps(oneRepMax: Double, reps: Int, rpe: Double) -> Double? {
        guard oneRepMax > 0 else { return nil }
        guard reps >= 1 && reps <= 10 else { return nil }
        guard rpe >= 5.0 && rpe <= 10.0 else { return nil }

        guard let rpeRow = rpePercentageTable[rpe],
              let percentageOfMax = rpeRow[reps] else {
            return nil
        }

        return oneRepMax * (percentageOfMax / 100.0)
    }
}
