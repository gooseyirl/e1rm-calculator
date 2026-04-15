import SwiftUI

struct InfoCardView: View {
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("About This Calculator")
                .font(.system(size: 16, weight: .bold))

            Text("This calculator uses RPE (Rate of Perceived Exertion) based formulas similar to the Barbell Medicine approach. Enter the weight you lifted, the number of reps (1-10), and your RPE to get an accurate 1RM estimate.")
                .font(.system(size: 14))
                .foregroundColor(.secondary)
                .multilineTextAlignment(.leading)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(Color.gray.opacity(0.1))
        .cornerRadius(12)
    }
}

#Preview {
    InfoCardView()
        .padding()
}
