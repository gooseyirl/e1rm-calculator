import SwiftUI

struct ContentView: View {
    @EnvironmentObject var settings: AppSettings
    @EnvironmentObject var storeManager: StoreManager

    @State private var weight: String = ""
    @State private var reps: String = "1"
    @State private var selectedRPE: Double = 10.0
    @State private var calculatedMax: Double? = nil
    @State private var path: [AppScreen] = []
    @State private var dailyQuote: String = MotivationalQuotes.nextQuote()

    enum AppScreen: Hashable {
        case settings
        case setsPlanner
    }

    var body: some View {
        NavigationStack(path: $path) {
            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: 24) {
                        // MARK: - Header Section
                        VStack(spacing: 8) {
                            Text("E1RM Calculator")
                                .font(.system(size: 32, weight: .bold))
                                .foregroundColor(.blue)

                            Text("One Rep Max Estimator")
                                .font(.system(size: 16))
                                .foregroundColor(.secondary)
                        }
                        .padding(.top, 40)

                        // MARK: - Input Section
                        InputFieldsView(weight: $weight, reps: $reps)

                        RPEPickerView(selectedRPE: $selectedRPE)

                        // MARK: - Calculate Button
                        Button(action: calculate) {
                            Text("Calculate 1RM")
                                .font(.system(size: 18, weight: .medium))
                                .frame(maxWidth: .infinity)
                                .padding()
                                .background(weight.isEmpty || reps.isEmpty ? Color.gray : Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                        }
                        .disabled(weight.isEmpty || reps.isEmpty)

                        // MARK: - Result Section
                        if let max = calculatedMax {
                            ResultCardView(oneRepMax: max)
                            TrainingPercentagesView(oneRepMax: max)
                        }

                        // MARK: - Info Section
                        InfoCardView()

                        Spacer()
                    }
                    .padding(24)
                }

                // MARK: - Quote Footer (shown after donation)
                if storeManager.isDonated {
                    HStack(spacing: 8) {
                        Image(systemName: "star.fill")
                            .foregroundColor(.yellow)
                            .font(.system(size: 12))
                        Text(dailyQuote)
                            .font(.system(size: 11))
                            .foregroundColor(.secondary)
                            .fixedSize(horizontal: false, vertical: true)
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(UIColor.secondarySystemBackground))
                }
            }
            .navigationDestination(for: AppScreen.self) { screen in
                switch screen {
                case .settings:
                    SettingsView()
                case .setsPlanner:
                    SetsPlannerView(initialE1rm: calculatedMax)
                }
            }
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Menu {
                        Button(action: { path.append(.setsPlanner) }) {
                            Label("Sets Planner", systemImage: "list.bullet.clipboard")
                        }
                        Button(action: { path.append(.settings) }) {
                            Label("Settings", systemImage: "gear")
                        }
                        if !storeManager.isDonated {
                            Button(action: { Task { await storeManager.purchase() } }) {
                                Label("Support Developer", systemImage: "star")
                            }
                        }
                    } label: {
                        Image(systemName: "ellipsis.circle")
                            .font(.system(size: 20))
                    }
                }
            }
            .onTapGesture {
                UIApplication.shared.sendAction(
                    #selector(UIResponder.resignFirstResponder),
                    to: nil, from: nil, for: nil
                )
            }
            .alert("Purchase Unavailable", isPresented: Binding(
                get: { storeManager.purchaseError != nil },
                set: { if !$0 { storeManager.purchaseError = nil } }
            )) {
                Button("OK") { storeManager.purchaseError = nil }
            } message: {
                Text(storeManager.purchaseError ?? "")
            }
        }
    }

    private func calculate() {
        if let w = Double(weight), let r = Int(reps) {
            calculatedMax = OneRepMaxCalculator.calculateOneRepMax(weight: w, reps: r, rpe: selectedRPE)
            UIApplication.shared.sendAction(
                #selector(UIResponder.resignFirstResponder),
                to: nil, from: nil, for: nil
            )
        }
    }
}

#Preview {
    ContentView()
        .environmentObject(AppSettings())
        .environmentObject(StoreManager())
}
