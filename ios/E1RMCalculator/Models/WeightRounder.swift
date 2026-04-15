import Foundation

struct WeightRounder {
    static func round(_ weight: Double, rounding: String) -> Double {
        let inc = rounding.hasSuffix("2_5") ? 2.5 : 0.5
        if rounding.hasPrefix("up") {
            return ceil(weight / inc) * inc
        } else if rounding.hasPrefix("down") {
            return floor(weight / inc) * inc
        } else {
            return (weight / inc).rounded() * inc
        }
    }

    static func format(_ weight: Double, rounding: String) -> String {
        let rounded = Self.round(weight, rounding: rounding)
        if rounded.truncatingRemainder(dividingBy: 1.0) == 0.0 {
            return String(Int(rounded))
        } else {
            return String(format: "%.1f", rounded)
        }
    }
}
