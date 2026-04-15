import SwiftUI

struct TrainingPercentagesView: View {
    let oneRepMax: Double
    @EnvironmentObject var settings: AppSettings
    @State private var customPercentage: String = ""

    private let percentages = [95, 90, 85, 80, 75, 70, 65, 60]

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            // Header
            Text("Training Percentages")
                .font(.system(size: 18, weight: .bold))

            // Custom Percentage Calculator
            VStack(alignment: .leading, spacing: 10) {
                Text("Custom %")
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundColor(.blue)

                HStack(spacing: 12) {
                    TextField("Enter percentage", text: $customPercentage)
                        .keyboardType(.numberPad)
                        .textFieldStyle(.roundedBorder)

                    if let percentage = Int(customPercentage), percentage >= 1 && percentage <= 100 {
                        VStack(spacing: 2) {
                            Text("\(percentage)%")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(.secondary)
                            Text("\(formattedWeight(for: percentage)) \(settings.units)")
                                .font(.system(size: 18, weight: .bold))
                                .foregroundColor(.blue)
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 10)
                        .background(Color.blue.opacity(0.1))
                        .cornerRadius(10)
                    }
                }
            }
            .padding(16)
            .background(Color(UIColor.secondarySystemBackground))
            .cornerRadius(12)

            // Percentage Table
            VStack(spacing: 0) {
                ForEach(percentages, id: \.self) { percentage in
                    HStack {
                        Text("\(percentage)%")
                            .font(.system(size: 16, weight: .medium))

                        Spacer()

                        Text("\(formattedWeight(for: percentage)) \(settings.units)")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.blue)
                    }
                    .padding(.vertical, 4)

                    if percentage != percentages.last {
                        Divider()
                    }
                }
            }
        }
    }

    private func formattedWeight(for percentage: Int) -> String {
        let raw = oneRepMax * Double(percentage) / 100.0
        return WeightRounder.format(raw, rounding: settings.rounding)
    }
}

#Preview {
    TrainingPercentagesView(oneRepMax: 388.4)
        .environmentObject(AppSettings())
        .padding()
}
