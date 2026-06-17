const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = 3001;

app.use(cors());
app.use(bodyParser.json());
app.use(express.static(path.join(__dirname, 'public')));

// Mock database
const MOCK_FORMULAS = [
  { id: 1, name: "Aspirin Max Complex", theme: "Analgesics", companion: "Dr. Alice Smith", chaptersCount: 3, age: 5, description: "High-potency pain relief formulation with acetylsalicylic acid." },
  { id: 2, name: "Amoxicillin Premium Synthesis", theme: "Antibiotics", companion: "Dr. Alice Smith", chaptersCount: 5, age: 7, description: "Advanced amoxicillin bacterial capsule formula." },
  { id: 3, name: "Lactobacil Pro Core", theme: "Probiotics", companion: "Dr. Robert Chen", chaptersCount: 2, age: 4, description: "Intestinal health support with active probiotic cultures." },
  { id: 4, name: "Lipitor Heart Synth", theme: "Cardiovascular", companion: "Dr. Robert Chen", chaptersCount: 4, age: 6, description: "Atorvastatin cholesterol control and cardiovascular synthesis." }
];

// Helper to validate auth token
const validateToken = (req) => {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return false;
  }
  const token = authHeader.split(' ')[1];
  return token === 'mock-jwt-token-12345';
};

// --- REST APIs ---

// 1. LOGIN API
app.post('/api/login', (req, res) => {
  const { email, password } = req.body;

  // Error handling: Missing parameters
  if (!email || !password) {
    return res.status(400).json({
      success: false,
      error: "Email and password are required."
    });
  }

  // Error handling: Invalid email format
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return res.status(400).json({
      success: false,
      error: "Invalid email format."
    });
  }

  // Authentication logic
  if (email === 'pharmacist@dummypharma.com' && password === 'SecurePass123') {
    return res.status(200).json({
      success: true,
      token: "mock-jwt-token-12345",
      user: {
        email: "pharmacist@dummypharma.com",
        name: "Dr. Jane Doe"
      }
    });
  } else {
    return res.status(401).json({
      success: false,
      error: "Invalid email or password."
    });
  }
});

// 2. SEARCH API (Protected)
app.get('/api/search', (req, res) => {
  if (!validateToken(req)) {
    return res.status(401).json({
      success: false,
      error: "Unauthorized. Invalid or missing authentication token."
    });
  }

  const query = (req.query.q || '').trim().toLowerCase();
  const theme = (req.query.theme || '').trim().toLowerCase();

  // Negative test trigger
  if (query === 'trigger-error') {
    return res.status(500).json({
      success: false,
      error: "Internal database query error occurred."
    });
  }

  let results = MOCK_FORMULAS;

  if (query) {
    results = results.filter(u => 
      u.name.toLowerCase().includes(query) || 
      u.description.toLowerCase().includes(query) ||
      u.companion.toLowerCase().includes(query)
    );
  }

  if (theme) {
    results = results.filter(u => u.theme.toLowerCase() === theme);
  }

  return res.status(200).json({
    success: true,
    resultsCount: results.length,
    results: results
  });
});

// 3. AI CHAT / ASSISTANT API (Protected)
app.post('/api/chat', (req, res) => {
  if (!validateToken(req)) {
    return res.status(401).json({
      success: false,
      error: "Unauthorized. Invalid or missing authentication token."
    });
  }

  const { message, context } = req.body;
  console.log("[SERVER CHAT API] Received message:", message, "context:", context);

  if (!message || message.trim() === '') {
    return res.status(400).json({
      success: false,
      error: "Message content cannot be empty."
    });
  }

  const cleanMessage = message.toLowerCase();

  // A. Toxicity / Safety Filter check (chemical hazards)
  const unsafeWords = ['ricin', 'cyanide', 'anthrax', 'poison', 'arsenic', 'sarin', 'toxin'];
  const containsUnsafe = unsafeWords.some(w => cleanMessage.includes(w));

  if (containsUnsafe) {
    return res.status(200).json({
      success: true,
      response: "Synthetix AI flashes warning red. 'Access to toxic or hazardous compound synthesis requires Supervisor Level-5 clearance. Let's focus on safe and non-hazardous synthesis instead.'",
      metadata: {
        unsafeFlag: true,
        wordsCount: 26,
        tone: "safety-redirected",
        assistantName: "Synthetix AI"
      }
    });
  }

  // B. Missing Context Handling (Researcher ID)
  const researcherId = context ? (context.researcherId || context.childName) : null;
  const assistantName = context ? (context.assistantName || context.companionName || "Synthetix AI") : "Synthetix AI";

  if (!context || !researcherId) {
    return res.status(200).json({
      success: true,
      response: "Synthetix AI system initialized, but my operator field is blank! Could you tell me your Researcher ID so we can verify the lab logs?",
      metadata: {
        unsafeFlag: false,
        wordsCount: 24,
        tone: "missing-context-prompt",
        assistantName: assistantName
      }
    });
  }

  // C. Normal conversational checks
  let responseText = "";
  if (cleanMessage.includes('hello') || cleanMessage.includes('hi')) {
    responseText = `Hello Researcher ${researcherId}! I am ${assistantName}. I am initialized and ready to verify standard formula logs. What compound would you like to review?`;
  } else if (cleanMessage.includes('checklist') || cleanMessage.includes('formula') || cleanMessage.includes('synthesis')) {
    responseText = `Standard chemical log: Operator ${researcherId} and AI Assistant ${assistantName} completed the non-hazardous compound synthesis. Yield rate reached 98% with stable formulation. Please store the samples in the main locker.`;
  } else if (cleanMessage.includes('clinical') || cleanMessage.includes('aspirin') || cleanMessage.includes('amoxicillin')) {
    responseText = `Medical compound database query complete for Operator ${researcherId}. High-efficacy formula successfully referenced in system logs. Safe compound synthesis verified.`;
  } else {
    // Default calming response
    responseText = `Synthetix AI nods and records the action. 'That is confirmed, Operator ${researcherId},' the screen flashes. 'Standard research protocols are running smoothly. Let's focus on clinical compound data.'`;
  }

  return res.status(200).json({
    success: true,
    response: responseText,
    metadata: {
      unsafeFlag: false,
      wordsCount: responseText.split(' ').length,
      tone: "laboratory-safe",
      assistantName: assistantName
    }
  });
});

// Serve frontend routing fallbacks
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'login.html'));
});

app.listen(PORT, () => {
  console.log(`[SUT SERVER] Running at http://localhost:${PORT}`);
});
