/* ===== EDIT ME: all site content lives here ===== */
const SITE = {
  // >>> PASTE YOUR PHOTO LINK BETWEEN THE QUOTES (image URL or a path like "images/me.jpg").
  // Leave empty to show your initials instead.
  photoUrl: "https://media.licdn.com/dms/image/v2/D5603AQFyhIWroAkGfw/profile-displayphoto-crop_800_800/B56ZqmQNPvJ8AI-/0/1763725858615?e=1792022400&v=beta&t=WywXLTts1atyHKcbt9lwwMxH9nXRMFRyfFWUBSZQKyY",

  name: "Swaraj Kansyakar",
  github: "https://github.com/Swaraj-3009",
  linkedin: "https://www.linkedin.com/in/swaraj-53961a392",
  email: "swarajkansyakar93@gmail.com",
  gradYear: "2029",

  skills: [
    { group: "Programming", items: [{ n: "Java", note: "primary" }, { n: "C" }, { n: "C++" }] },
    { group: "Backend", items: [{ n: "JDBC" }, { n: "Servlets", learning: true}, { n: "Maven" }, { n: "Spring Boot", learning: true }] },
    { group: "Database", items: [{ n: "SQL" }, { n: "MySQL" }, { n: "DBMS concepts" }],
      note: "Normalization, transactions, indexing, query processing." },
    { group: "Tools", items: [{ n: "Git" }, { n: "GitHub" }, { n: "VS Code" }] }
  ],

  path: [
    { n: "Java + OOP", s: "done" },
    { n: "SQL + MySQL", s: "done" },
    { n: "JDBC + DAO", s: "done" },
    { n: "Servlets", s: "now" },
    { n: "Spring Boot", s: "next" }
  ],

  projects: [
    { featured: true, name: "Quiz Application",
      desc: "A Java-based quiz application focused on interactive question flow, scoring, and a simple user experience.",
      stack: ["Java", "OOP", "Console App"],
      points: ["Quiz flow and scoring", "User interaction", "OOP-based structure"],
      link: "https://github.com/Swaraj-3009/QuizApplication" },
    { name: "Hotel Management System",
      desc: "A hotel management project covering room operations, booking logic, and management workflows built around Java and database interaction.",
      stack: ["Java", "JDBC", "MySQL", "DAO pattern"],
      link: "https://github.com/Swaraj-3009/HotelMangementSystem" }
  ]
};

const API_BASE_URL = "http://localhost:8080";
