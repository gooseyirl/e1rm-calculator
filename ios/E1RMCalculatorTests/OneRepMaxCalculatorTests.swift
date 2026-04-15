import XCTest
@testable import E1RMCalculator

final class OneRepMaxCalculatorTests: XCTestCase {

    // MARK: - calculateOneRepMax Tests

    func testCalculateOneRepMax_ValidInputs() {
        // Test case from Android: 315 lbs × 5 reps @ RPE 8.0 should be ~388 lbs
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 315, reps: 5, rpe: 8.0)
        XCTAssertNotNil(result)
        XCTAssertEqual(result ?? 0, 388.4, accuracy: 0.1)
    }

    func testCalculateOneRepMax_RPE10_1Rep() {
        // RPE 10.0 with 1 rep should equal the weight itself (100%)
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 225, reps: 1, rpe: 10.0)
        XCTAssertNotNil(result)
        XCTAssertEqual(result ?? 0, 225, accuracy: 0.1)
    }

    func testCalculateOneRepMax_RPE9_5Reps() {
        // 200 lbs × 5 reps @ RPE 9.0
        // RPE 9.0, 5 reps = 83.7%
        // 1RM = 200 / 0.837 = ~239
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 200, reps: 5, rpe: 9.0)
        XCTAssertNotNil(result)
        XCTAssertEqual(result ?? 0, 239.0, accuracy: 0.5)
    }

    func testCalculateOneRepMax_RPE6_10Reps() {
        // Test lowest RPE with highest reps
        // 135 lbs × 10 reps @ RPE 6.0
        // RPE 6.0, 10 reps = 65.6%
        // 1RM = 135 / 0.656 = ~205.8
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 135, reps: 10, rpe: 6.0)
        XCTAssertNotNil(result)
        XCTAssertEqual(result ?? 0, 205.8, accuracy: 0.5)
    }

    func testCalculateOneRepMax_InvalidWeight_Zero() {
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 0, reps: 5, rpe: 8.0)
        XCTAssertNil(result)
    }

    func testCalculateOneRepMax_InvalidWeight_Negative() {
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: -100, reps: 5, rpe: 8.0)
        XCTAssertNil(result)
    }

    func testCalculateOneRepMax_InvalidReps_Zero() {
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 225, reps: 0, rpe: 8.0)
        XCTAssertNil(result)
    }

    func testCalculateOneRepMax_InvalidReps_TooHigh() {
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 225, reps: 11, rpe: 8.0)
        XCTAssertNil(result)
    }

    func testCalculateOneRepMax_InvalidRPE_TooLow() {
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 225, reps: 5, rpe: 5.5)
        XCTAssertNil(result)
    }

    func testCalculateOneRepMax_InvalidRPE_TooHigh() {
        let result = OneRepMaxCalculator.calculateOneRepMax(weight: 225, reps: 5, rpe: 10.5)
        XCTAssertNil(result)
    }

    // MARK: - calculateRepsAtWeight Tests

    func testCalculateRepsAtWeight_ValidInputs() {
        // With 1RM of 300, at 225 lbs and RPE 8.0
        // 225/300 = 75% which should be around 8 reps at RPE 8.0 (74.0%)
        let result = OneRepMaxCalculator.calculateRepsAtWeight(oneRepMax: 300, weight: 225, rpe: 8.0)
        XCTAssertNotNil(result)
        XCTAssertEqual(result, 8)
    }

    func testCalculateRepsAtWeight_InvalidOneRepMax() {
        let result = OneRepMaxCalculator.calculateRepsAtWeight(oneRepMax: 0, weight: 225, rpe: 8.0)
        XCTAssertNil(result)
    }

    func testCalculateRepsAtWeight_WeightExceedsMax() {
        let result = OneRepMaxCalculator.calculateRepsAtWeight(oneRepMax: 300, weight: 350, rpe: 8.0)
        XCTAssertNil(result)
    }

    // MARK: - getSupportedRpeValues Tests

    func testGetSupportedRpeValues_ReturnsAllNineValues() {
        let values = OneRepMaxCalculator.getSupportedRpeValues()
        XCTAssertEqual(values.count, 9)
    }

    func testGetSupportedRpeValues_IsSorted() {
        let values = OneRepMaxCalculator.getSupportedRpeValues()
        XCTAssertEqual(values, [6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0, 9.5, 10.0])
    }

    // MARK: - calculateWeightForReps Tests

    func testCalculateWeightForReps_ValidInputs() {
        // With 1RM of 400, for 5 reps at RPE 8.0
        // RPE 8.0, 5 reps = 81.1%
        // Weight = 400 * 0.811 = 324.4
        let result = OneRepMaxCalculator.calculateWeightForReps(oneRepMax: 400, reps: 5, rpe: 8.0)
        XCTAssertNotNil(result)
        XCTAssertEqual(result ?? 0, 324.4, accuracy: 0.1)
    }

    func testCalculateWeightForReps_InvalidOneRepMax() {
        let result = OneRepMaxCalculator.calculateWeightForReps(oneRepMax: 0, reps: 5, rpe: 8.0)
        XCTAssertNil(result)
    }

    func testCalculateWeightForReps_InvalidReps() {
        let result = OneRepMaxCalculator.calculateWeightForReps(oneRepMax: 400, reps: 11, rpe: 8.0)
        XCTAssertNil(result)
    }

    func testCalculateWeightForReps_InvalidRPE() {
        let result = OneRepMaxCalculator.calculateWeightForReps(oneRepMax: 400, reps: 5, rpe: 11.0)
        XCTAssertNil(result)
    }

    // MARK: - Edge Case Tests

    func testAllRPEValues_AllReps() {
        // Test all combinations of RPE and reps to ensure table is complete
        let rpeValues = [6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0, 9.5, 10.0]
        let reps = Array(1...10)

        for rpe in rpeValues {
            for rep in reps {
                let result = OneRepMaxCalculator.calculateOneRepMax(weight: 100, reps: rep, rpe: rpe)
                XCTAssertNotNil(result, "Failed for RPE \(rpe) and \(rep) reps")
            }
        }
    }

    func testRoundTripCalculation() {
        // Calculate 1RM, then calculate weight back, should match original
        let originalWeight = 225.0
        let reps = 5
        let rpe = 8.0

        if let oneRepMax = OneRepMaxCalculator.calculateOneRepMax(weight: originalWeight, reps: reps, rpe: rpe),
           let calculatedWeight = OneRepMaxCalculator.calculateWeightForReps(oneRepMax: oneRepMax, reps: reps, rpe: rpe) {
            XCTAssertEqual(calculatedWeight, originalWeight, accuracy: 0.1)
        } else {
            XCTFail("Round trip calculation failed")
        }
    }
}
