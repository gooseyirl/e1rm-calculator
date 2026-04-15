import SwiftUI

struct RPEPickerView: View {
    @Binding var selectedRPE: Double

    private let rpeValues = OneRepMaxCalculator.getSupportedRpeValues()

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Rate of Perceived Exertion")
                .font(.subheadline)
                .foregroundColor(.secondary)

            Menu {
                ForEach(rpeValues.reversed(), id: \.self) { rpe in
                    Button {
                        selectedRPE = rpe
                    } label: {
                        VStack(alignment: .leading) {
                            Text("RPE \(rpe, specifier: "%.1f")")
                                .fontWeight(.bold)
                            Text(getRpeDescription(rpe))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
            } label: {
                HStack {
                    Text("RPE: \(selectedRPE, specifier: "%.1f")")
                        .foregroundColor(.primary)
                    Spacer()
                    Image(systemName: "chevron.down")
                        .foregroundColor(.secondary)
                        .font(.caption)
                }
                .padding()
                .background(Color(UIColor.systemBackground))
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(Color(UIColor.systemGray4), lineWidth: 1)
                )
            }

            // RPE Info Card
            HStack {
                Text("RPE \(selectedRPE, specifier: "%.1f"): \(getRpeDescription(selectedRPE))")
                    .font(.system(size: 14))
                    .foregroundColor(.secondary)
                Spacer()
            }
            .padding(12)
            .frame(maxWidth: .infinity)
            .background(Color.cyan.opacity(0.1))
            .cornerRadius(8)
        }
    }

    private func getRpeDescription(_ rpe: Double) -> String {
        switch rpe {
        case 10.0:
            return "Maximum effort - no reps left"
        case 9.5:
            return "Could do 1 more rep, maybe"
        case 9.0:
            return "Could definitely do 1 more rep"
        case 8.5:
            return "Could do 1-2 more reps"
        case 8.0:
            return "Could definitely do 2 more reps"
        case 7.5:
            return "Could do 2-3 more reps"
        case 7.0:
            return "Could definitely do 3 more reps"
        case 6.5:
            return "Could do 3-4 more reps"
        case 6.0:
            return "Could definitely do 4 more reps"
        case 5.5:
            return "Could do 4-5 more reps"
        case 5.0:
            return "Could definitely do 5 more reps"
        default:
            return "Unknown RPE"
        }
    }
}

#Preview {
    RPEPickerView(selectedRPE: .constant(8.0))
        .padding()
}
