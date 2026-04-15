import SwiftUI

struct InputFieldsView: View {
    @Binding var weight: String
    @Binding var reps: String
    @EnvironmentObject var settings: AppSettings

    var body: some View {
        VStack(spacing: 16) {
            // Weight Input
            VStack(alignment: .leading, spacing: 4) {
                Text("Weight (\(settings.units))")
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                TextField("Enter weight", text: $weight)
                    .keyboardType(.decimalPad)
                    .textFieldStyle(.roundedBorder)
                    .padding(.vertical, 8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(UIColor.systemGray4), lineWidth: 1)
                    )
            }

            // Reps Input
            VStack(alignment: .leading, spacing: 4) {
                Text("Reps (1-10)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                TextField("Enter reps", text: $reps)
                    .keyboardType(.numberPad)
                    .textFieldStyle(.roundedBorder)
                    .padding(.vertical, 8)
                    .overlay(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(Color(UIColor.systemGray4), lineWidth: 1)
                    )
            }
        }
    }
}

#Preview {
    InputFieldsView(weight: .constant(""), reps: .constant(""))
        .environmentObject(AppSettings())
        .padding()
}
