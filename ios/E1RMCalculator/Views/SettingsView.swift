import SwiftUI

struct SettingsView: View {
    @EnvironmentObject var settings: AppSettings
    @AppStorage("theme_preference") private var themePreference: String = "system"

    private var selectedIncrement: String {
        settings.rounding.hasSuffix("2_5") ? "2.5" : "0.5"
    }

    private var selectedDirection: String {
        settings.rounding.components(separatedBy: "_").first ?? "default"
    }

    private func emitRounding(increment: String? = nil, direction: String? = nil) {
        let inc = increment ?? selectedIncrement
        let dir = direction ?? selectedDirection
        let incKey = inc == "2.5" ? "2_5" : "0_5"
        settings.rounding = "\(dir)_\(incKey)"
    }

    var body: some View {
        Form {
            Section("Appearance") {
                Picker("Theme", selection: $themePreference) {
                    Text("System").tag("system")
                    Text("Light").tag("light")
                    Text("Dark").tag("dark")
                }
                .pickerStyle(.segmented)
                .listRowBackground(Color.clear)
                .listRowInsets(EdgeInsets(top: 8, leading: 0, bottom: 8, trailing: 0))
            }

            Section("Units of Measurement") {
                Picker("Units", selection: $settings.units) {
                    Text("kg").tag("kg")
                    Text("lbs").tag("lbs")
                }
                .pickerStyle(.segmented)
                .listRowBackground(Color.clear)
                .listRowInsets(EdgeInsets(top: 8, leading: 0, bottom: 8, trailing: 0))
            }

            Section {
                Picker("Rounding Increment", selection: Binding(
                    get: { selectedIncrement },
                    set: { emitRounding(increment: $0) }
                )) {
                    Text("2.5").tag("2.5")
                    Text("0.5").tag("0.5")
                }
                .pickerStyle(.segmented)
                .listRowBackground(Color.clear)
                .listRowInsets(EdgeInsets(top: 8, leading: 0, bottom: 8, trailing: 0))
            } header: {
                Text("Rounding Increment")
            } footer: {
                Text("The nearest value all calculated weights are rounded to")
            }

            Section {
                Picker("Rounding Direction", selection: Binding(
                    get: { selectedDirection },
                    set: { emitRounding(direction: $0) }
                )) {
                    Text("Default").tag("default")
                    Text("Always Up").tag("up")
                    Text("Always Down").tag("down")
                }
                .pickerStyle(.segmented)
                .listRowBackground(Color.clear)
                .listRowInsets(EdgeInsets(top: 8, leading: 0, bottom: 8, trailing: 0))
            } header: {
                Text("Rounding Direction")
            } footer: {
                Text("Default rounds to the nearest increment")
            }
        }
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.large)
    }
}

#Preview {
    NavigationStack {
        SettingsView()
            .environmentObject(AppSettings())
    }
}
