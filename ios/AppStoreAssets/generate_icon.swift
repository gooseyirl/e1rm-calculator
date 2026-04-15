import Cocoa

func createAppIcon(size: Int, outputPath: String) {
    let cgSize = CGSize(width: size, height: size)

    let image = NSImage(size: cgSize)
    image.lockFocus()

    guard let context = NSGraphicsContext.current?.cgContext else {
        print("Failed to get graphics context")
        return
    }

    // Draw gradient background (blue)
    let colorSpace = CGColorSpaceCreateDeviceRGB()
    let colors = [
        CGColor(red: 0.0, green: 0.48, blue: 1.0, alpha: 1.0),    // #007AFF top
        CGColor(red: 0.1, green: 0.55, blue: 0.95, alpha: 1.0)    // Lighter bottom
    ]

    if let gradient = CGGradient(colorsSpace: colorSpace, colors: colors as CFArray, locations: [0.0, 1.0]) {
        context.drawLinearGradient(gradient,
                                   start: CGPoint(x: 0, y: CGFloat(size)),
                                   end: CGPoint(x: 0, y: 0),
                                   options: [])
    }

    // Draw text
    let fontSize = CGFloat(size) / 3.0
    let smallFontSize = CGFloat(size) / 8.0

    let font = NSFont.boldSystemFont(ofSize: fontSize)
    let smallFont = NSFont.boldSystemFont(ofSize: smallFontSize)

    // Shadow color
    let shadowColor = NSColor(red: 0.0, green: 0.33, blue: 0.67, alpha: 1.0)

    // Main text "1RM"
    let mainText = "1RM"
    let mainAttrs: [NSAttributedString.Key: Any] = [
        .font: font,
        .foregroundColor: NSColor.white
    ]
    let shadowAttrs: [NSAttributedString.Key: Any] = [
        .font: font,
        .foregroundColor: shadowColor
    ]

    let mainSize = mainText.size(withAttributes: mainAttrs)
    let mainX = (CGFloat(size) - mainSize.width) / 2
    let mainY = (CGFloat(size) - mainSize.height) / 2 + CGFloat(size) / 15

    // Draw shadow
    let shadowOffset: CGFloat = max(2, CGFloat(size) / 150)
    mainText.draw(at: NSPoint(x: mainX + shadowOffset, y: mainY - shadowOffset), withAttributes: shadowAttrs)
    // Draw main text
    mainText.draw(at: NSPoint(x: mainX, y: mainY), withAttributes: mainAttrs)

    // Subtitle "CALC"
    let subText = "CALC"
    let subAttrs: [NSAttributedString.Key: Any] = [
        .font: smallFont,
        .foregroundColor: NSColor(red: 0.8, green: 0.87, blue: 1.0, alpha: 1.0)
    ]
    let subShadowAttrs: [NSAttributedString.Key: Any] = [
        .font: smallFont,
        .foregroundColor: shadowColor
    ]

    let subSize = subText.size(withAttributes: subAttrs)
    let subX = (CGFloat(size) - subSize.width) / 2
    let subY = mainY - mainSize.height - CGFloat(size) / 30

    subText.draw(at: NSPoint(x: subX + shadowOffset, y: subY - shadowOffset), withAttributes: subShadowAttrs)
    subText.draw(at: NSPoint(x: subX, y: subY), withAttributes: subAttrs)

    image.unlockFocus()

    // Save as PNG
    guard let tiffData = image.tiffRepresentation,
          let bitmap = NSBitmapImageRep(data: tiffData),
          let pngData = bitmap.representation(using: .png, properties: [:]) else {
        print("Failed to create PNG data")
        return
    }

    do {
        try pngData.write(to: URL(fileURLWithPath: outputPath))
    } catch {
        print("Failed to write file: \(error)")
    }
}

// Get output directory from command line or use default
let iconDir = CommandLine.arguments.count > 1 ? CommandLine.arguments[1] : "./AppStoreAssets/AppIcon"

// Create directory if needed
try? FileManager.default.createDirectory(atPath: iconDir, withIntermediateDirectories: true)

// Generate icons at required sizes
let sizes: [(Int, String)] = [
    (1024, "AppIcon-1024.png"),
    (180, "AppIcon-180.png"),
    (120, "AppIcon-120.png"),
    (167, "AppIcon-167.png"),
    (152, "AppIcon-152.png"),
    (76, "AppIcon-76.png")
]

for (size, filename) in sizes {
    let outputPath = "\(iconDir)/\(filename)"
    createAppIcon(size: size, outputPath: outputPath)
    print("  ✓ Generated \(filename) (\(size)x\(size))")
}

print("\n  App icons saved to \(iconDir)/")
