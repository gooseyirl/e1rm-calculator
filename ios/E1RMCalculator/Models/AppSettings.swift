import Foundation

class AppSettings: ObservableObject {
    @Published var units: String {
        didSet { UserDefaults.standard.set(units, forKey: "units") }
    }
    @Published var rounding: String {
        didSet { UserDefaults.standard.set(rounding, forKey: "rounding") }
    }

    init() {
        self.units = UserDefaults.standard.string(forKey: "units") ?? "kg"
        self.rounding = UserDefaults.standard.string(forKey: "rounding") ?? "default_0_5"
    }
}
