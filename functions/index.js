const { onRequest } = require("firebase-functions/v2/https");

exports.getCropDecision = onRequest((req, res) => {
  try {
    const {
      crop,
      soilMoisture,
      temperature,
      humidity,
      rainExpected,
      growthStage
    } = req.body;

    if (!crop || soilMoisture == null) {
      return res.status(400).json({
        error: "Missing required data"
      });
    }

    let decision = "No Action";
    let confidence = "MEDIUM";
    let reasons = [];
    let action = {};

    // 🌾 WHEAT RULES
    if (crop === "wheat") {
      if (soilMoisture < 35 && !rainExpected) {
        decision = "Irrigate";
        confidence = "HIGH";
        reasons.push("Soil moisture below 35%");
        reasons.push("No rain expected");

        if (growthStage === "vegetative") {
          action.waterLitersPerAcre = 2500;
        } else {
          action.waterLitersPerAcre = 2000;
        }

        action.timing = "Evening";
      }
    }

    return res.json({
      decision,
      confidence,
      reasons,
      action,
      timestamp: new Date().toISOString()
    });

  } catch (err) {
    console.error(err);
    res.status(500).json({ error: "Decision engine failure" });
  }
});
