import SwiftUI

struct ResultCardView: View {
    let oneRepMax: Double
    @EnvironmentObject var settings: AppSettings

    var body: some View {
        VStack(spacing: 8) {
            Text("Estimated 1RM")
                .font(.system(size: 16))
                .foregroundColor(.secondary)

            Text(WeightRounder.format(oneRepMax, rounding: settings.rounding))
                .font(.system(size: 48, weight: .bold))
                .foregroundColor(.blue)

            Text(settings.units)
                .font(.system(size: 14))
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity)
        .padding(24)
        .background(Color.blue.opacity(0.1))
        .cornerRadius(12)
    }
}

#Preview {
    ResultCardView(oneRepMax: 388.4)
        .environmentObject(AppSettings())
        .padding()
}
