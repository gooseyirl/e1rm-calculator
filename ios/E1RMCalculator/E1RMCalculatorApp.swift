import SwiftUI

@main
struct E1RMCalculatorApp: App {
    @StateObject private var settings = AppSettings()
    @StateObject private var storeManager = StoreManager()
    @AppStorage("theme_preference") private var themePreference: String = "system"

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(settings)
                .environmentObject(storeManager)
                .preferredColorScheme(colorScheme(for: themePreference))
        }
    }

    private func colorScheme(for preference: String) -> ColorScheme? {
        switch preference {
        case "light": return .light
        case "dark": return .dark
        default: return nil
        }
    }
}
