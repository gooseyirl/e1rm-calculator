import SwiftUI

// "RPE"    — weight from e1rm + RPE table
// "%1RM"   — percentage of e1rm
// "%last"  — percentage change from previous set's weight
// "Weight" — explicit weight
private struct SetConfig: Identifiable {
    var id: Int
    var numSets: String = "1"
    var reps: String = "5"
    var type: String = "RPE"
    var rpe: Double = 8.0
    var percentE1rm: String = "80"
    var percentDelta: String = "5"
    var percentIsIncrease: Bool = false
    var specificWeight: String = ""
}

private struct PlannedSet {
    let reps: Int
    let weight: Double
}

struct SetsPlannerView: View {
    let initialE1rm: Double?

    @EnvironmentObject var settings: AppSettings

    @State private var e1rmInput: String
    @State private var sets: [SetConfig] = [SetConfig(id: 0)]
    @State private var nextId: Int = 1
    @State private var plannedSets: [PlannedSet]? = nil
    @State private var generateError: String? = nil
    @State private var copied: Bool = false
    @State private var localRounding: String = "default_0_5"
    @State private var generateCount: Int = 0

    private let rpeValues = OneRepMaxCalculator.getSupportedRpeValues()

    init(initialE1rm: Double? = nil) {
        self.initialE1rm = initialE1rm
        if let e1rm = initialE1rm {
            let formatted = e1rm.truncatingRemainder(dividingBy: 1) == 0
                ? String(format: "%.0f", e1rm)
                : String(format: "%.1f", e1rm)
            _e1rmInput = State(initialValue: formatted)
        } else {
            _e1rmInput = State(initialValue: "")
        }
    }

    var body: some View {
        ScrollViewReader { proxy in
            ScrollView {
                VStack(alignment: .leading, spacing: 0) {
                    titleSection
                    e1rmSection
                    setsSection
                    generateSection
                    resultsSection
                }
                .padding(24)
                .onChange(of: generateCount) {
                    withAnimation {
                        proxy.scrollTo("bottom", anchor: .bottom)
                    }
                }
            }
        }
        .navigationTitle("Sets Planner")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear { localRounding = settings.rounding }
        .onTapGesture {
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        }
    }

    @ViewBuilder private var titleSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("Sets Planner")
                .font(.system(size: 32, weight: .bold))
                .foregroundColor(.blue)
            Text("Plan your sets")
                .font(.system(size: 16))
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(.bottom, 24)
    }

    @ViewBuilder private var e1rmSection: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("E1RM")
                .font(.system(size: 15, weight: .semibold))
                .foregroundColor(.blue)

            TextField("Estimated 1RM (\(settings.units)) — optional", text: $e1rmInput)
                .keyboardType(.decimalPad)
                .textFieldStyle(.roundedBorder)
                .onChange(of: e1rmInput) { plannedSets = nil; generateError = nil }

            if !e1rmInput.isEmpty && Double(e1rmInput) == nil {
                Text("Enter a valid number")
                    .font(.caption)
                    .foregroundColor(.red)
            } else {
                Text("Required for RPE and % 1RM sets")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(16)
        .background(Color(UIColor.secondarySystemBackground))
        .cornerRadius(12)
        .padding(.bottom, 20)
    }

    @ViewBuilder private var setsSection: some View {
        Text("Sets")
            .font(.system(size: 18, weight: .bold))
            .padding(.bottom, 8)

        ForEach($sets) { $config in
            let index = sets.firstIndex(where: { $0.id == config.id }) ?? 0
            SetConfigCard(
                config: $config,
                index: index,
                canRemove: sets.count > 1,
                units: settings.units,
                rpeValues: rpeValues,
                onRemove: {
                    sets.removeAll { $0.id == config.id }
                    plannedSets = nil
                }
            )
            .padding(.bottom, 12)
        }

        Button(action: {
            var newSet = sets.last ?? SetConfig(id: nextId)
            newSet.id = nextId
            sets.append(newSet)
            nextId += 1
        }) {
            Text("+ Add Set")
                .frame(maxWidth: .infinity)
                .padding(.vertical, 10)
                .background(Color(UIColor.systemBackground))
                .foregroundColor(.blue)
                .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color.blue, lineWidth: 1))
        }
        .padding(.bottom, 8)
    }

    @ViewBuilder private var generateSection: some View {
        Button(action: doGenerate) {
            Text("Generate Sets")
                .font(.system(size: 18, weight: .medium))
                .frame(maxWidth: .infinity)
                .padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(10)
        }
        .padding(.bottom, generateError != nil ? 8 : 0)

        if let err = generateError {
            Text(err)
                .font(.system(size: 13))
                .foregroundColor(.red)
                .padding(.bottom, 8)
        }
    }

    @ViewBuilder private var resultsSection: some View {
        if let sets = plannedSets {
            roundingToggle
            resultsTable(sets: sets)
            copyButton(sets: sets)
                .id("bottom")
                .padding(.bottom, 32)
        }
    }

    @ViewBuilder private var roundingToggle: some View {
        HStack(spacing: 8) {
            Text("Rounding")
                .font(.system(size: 14))
                .foregroundColor(.secondary)
            Spacer()
            ForEach(["0.5", "2.5"], id: \.self) { inc in
                let isSelected = inc == "2.5"
                    ? localRounding.hasSuffix("2_5")
                    : !localRounding.hasSuffix("2_5")
                Button(action: {
                    guard !isSelected else { return }
                    localRounding = inc == "2.5"
                        ? localRounding.replacingOccurrences(of: "0_5", with: "2_5")
                        : localRounding.replacingOccurrences(of: "2_5", with: "0_5")
                    doGenerate()
                }) {
                    Text("\(inc) \(settings.units)")
                        .font(.system(size: 13))
                        .padding(.horizontal, 12)
                        .padding(.vertical, 6)
                        .background(isSelected ? Color.blue : Color(UIColor.systemGray5))
                        .foregroundColor(isSelected ? .white : .primary)
                        .cornerRadius(16)
                }
            }
        }
        .padding(.top, 24)
        .padding(.bottom, 24)
    }

    @ViewBuilder private func resultsTable(sets: [PlannedSet]) -> some View {
        let grouped = groupPlannedSets(sets)
        VStack(spacing: 0) {
            ForEach(Array(grouped.enumerated()), id: \.offset) { index, item in
                let (count, set) = item
                HStack {
                    Text("Set \(index + 1)")
                        .font(.system(size: 13))
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity, alignment: .leading)
                    Text("\(count)×\(set.reps)")
                        .font(.system(size: 15, weight: .bold))
                        .frame(maxWidth: .infinity, alignment: .center)
                    Text("\(WeightRounder.format(set.weight, rounding: localRounding)) \(settings.units)")
                        .font(.system(size: 15, weight: index == 0 ? .bold : .regular))
                        .frame(maxWidth: .infinity, alignment: .trailing)
                }
                .padding(.vertical, 8)
                .padding(.horizontal, 16)
                if index < grouped.count - 1 {
                    Divider().padding(.horizontal, 16)
                }
            }
        }
        .background(Color.blue.opacity(0.08))
        .cornerRadius(12)
        .padding(.bottom, 12)
    }

    @ViewBuilder private func copyButton(sets: [PlannedSet]) -> some View {
        Button(action: {
            UIPasteboard.general.string = buildCopyText(sets)
            copied = true
        }) {
            Text(copied ? "✓ Copied!" : "Copy to Clipboard")
                .frame(maxWidth: .infinity)
                .padding()
                .background(copied ? Color(UIColor.systemGreen) : Color.blue)
                .foregroundColor(.white)
                .cornerRadius(10)
        }
    }

    private func doGenerate() {
        UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        generateError = nil

        let e1rm = Double(e1rmInput)
        var result: [PlannedSet] = []
        var lastWeight: Double? = nil

        for (index, config) in sets.enumerated() {
            guard let reps = Int(config.reps), reps > 0 else {
                generateError = "Set \(index + 1): enter a valid rep count"
                return
            }
            let numSets = max(1, Int(config.numSets) ?? 1)

            let weight: Double
            switch config.type {
            case "RPE":
                guard let e1rm else { generateError = "Enter an E1RM to use RPE sets"; return }
                guard let w = OneRepMaxCalculator.calculateWeightForReps(oneRepMax: e1rm, reps: reps, rpe: config.rpe) else {
                    generateError = "Set \(index + 1): reps/RPE combination not in table"; return
                }
                weight = w
            case "%1RM":
                guard let e1rm else { generateError = "Enter an E1RM to use % 1RM sets"; return }
                guard let pct = Double(config.percentE1rm) else {
                    generateError = "Set \(index + 1): enter a valid percentage"; return
                }
                weight = e1rm * (pct / 100.0)
            case "%last":
                guard let prev = lastWeight else {
                    generateError = "Set \(index + 1): no previous set to reference"; return
                }
                guard let delta = Double(config.percentDelta) else {
                    generateError = "Set \(index + 1): enter a valid percentage"; return
                }
                weight = config.percentIsIncrease ? prev * (1 + delta / 100.0) : prev * (1 - delta / 100.0)
            default: // "Weight"
                guard let w = Double(config.specificWeight) else {
                    generateError = "Set \(index + 1): enter a valid weight"; return
                }
                weight = w
            }

            let rounded = WeightRounder.round(weight, rounding: localRounding)
            for _ in 0..<numSets { result.append(PlannedSet(reps: reps, weight: rounded)) }
            lastWeight = rounded
        }

        plannedSets = result
        copied = false
        generateCount += 1
    }

    private func groupPlannedSets(_ sets: [PlannedSet]) -> [(Int, PlannedSet)] {
        guard !sets.isEmpty else { return [] }
        var result: [(Int, PlannedSet)] = []
        var count = 1
        for i in 1..<sets.count {
            if sets[i].reps == sets[i - 1].reps &&
               WeightRounder.round(sets[i].weight, rounding: localRounding) ==
               WeightRounder.round(sets[i - 1].weight, rounding: localRounding) {
                count += 1
            } else {
                result.append((count, sets[i - 1]))
                count = 1
            }
        }
        result.append((count, sets.last!))
        return result
    }

    private func buildCopyText(_ sets: [PlannedSet]) -> String {
        groupPlannedSets(sets).map { (count, set) in
            "\(count) x \(set.reps) @ \(WeightRounder.format(set.weight, rounding: localRounding))\(settings.units)"
        }.joined(separator: "\n")
    }
}

private struct SetConfigCard: View {
    @Binding var config: SetConfig
    let index: Int
    let canRemove: Bool
    let units: String
    let rpeValues: [Double]
    let onRemove: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {

            // Header
            HStack {
                Text("Set \(index + 1)")
                    .font(.system(size: 15, weight: .semibold))
                Spacer()
                if canRemove {
                    Button("Remove", action: onRemove)
                        .font(.system(size: 13))
                        .foregroundColor(.red)
                }
            }

            // Sets × Reps
            HStack(spacing: 12) {
                VStack(alignment: .leading, spacing: 4) {
                    Text("Sets").font(.caption).foregroundColor(.secondary)
                    TextField("Sets", text: $config.numSets)
                        .keyboardType(.numberPad)
                        .textFieldStyle(.roundedBorder)
                }
                VStack(alignment: .leading, spacing: 4) {
                    Text("Reps").font(.caption).foregroundColor(.secondary)
                    TextField("Reps", text: $config.reps)
                        .keyboardType(.numberPad)
                        .textFieldStyle(.roundedBorder)
                }
            }

            // Type chips: RPE | % 1RM | % last | Weight
            HStack(spacing: 6) {
                ForEach(["RPE", "%1RM", "%last", "Weight"], id: \.self) { type in
                    let chipLabel: String = {
                        switch type {
                        case "%1RM": return "% 1RM"
                        case "%last" where config.type == "%last":
                            return config.percentIsIncrease ? "% +" : "% -"
                        case "%last": return "% last"
                        default: return type
                        }
                    }()
                    let isSelected = config.type == type

                    Button(action: {
                        if config.type == "%last" && type == "%last" {
                            config.percentIsIncrease.toggle()
                        } else {
                            config.type = type
                            config.percentIsIncrease = false
                        }
                    }) {
                        Text(chipLabel)
                            .font(.system(size: 12))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 6)
                            .frame(maxWidth: .infinity)
                            .background(isSelected ? Color.blue : Color(UIColor.systemGray5))
                            .foregroundColor(isSelected ? .white : .primary)
                            .cornerRadius(16)
                    }
                }
            }

            // Type-specific input
            switch config.type {
            case "RPE":
                Picker("RPE", selection: $config.rpe) {
                    ForEach(rpeValues.reversed(), id: \.self) { rpe in
                        Text(String(format: "RPE %.1f", rpe)).tag(rpe)
                    }
                }
                .pickerStyle(.menu)
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(.vertical, 6)
                .background(Color(UIColor.systemGray6))
                .cornerRadius(8)

            case "%1RM":
                HStack {
                    TextField("% of E1RM", text: $config.percentE1rm)
                        .keyboardType(.decimalPad)
                        .textFieldStyle(.roundedBorder)
                    Text("%").foregroundColor(.secondary)
                }

            case "%last":
                VStack(alignment: .leading, spacing: 4) {
                    HStack {
                        TextField(
                            config.percentIsIncrease ? "% increase from last set" : "% reduction from last set",
                            text: $config.percentDelta
                        )
                        .keyboardType(.decimalPad)
                        .textFieldStyle(.roundedBorder)
                        Text("%").foregroundColor(.secondary)
                    }
                    Text("Tap the chip again to toggle + / −")
                        .font(.system(size: 11))
                        .foregroundColor(.secondary)
                }

            default: // "Weight"
                TextField("Weight (\(units))", text: $config.specificWeight)
                    .keyboardType(.decimalPad)
                    .textFieldStyle(.roundedBorder)
            }
        }
        .padding(16)
        .background(Color(UIColor.secondarySystemBackground))
        .cornerRadius(12)
    }
}

#Preview {
    NavigationStack {
        SetsPlannerView()
            .environmentObject(AppSettings())
    }
}
