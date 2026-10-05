/* Shared helpers for every page. Loaded after data.js (API_BASE_URL lives there). */
const $ = (s, r = document) => r.querySelector(s);
const esc = (s) => String(s ?? "").replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
const plain = (html) => new DOMParser().parseFromString(html, "text/html").body.textContent.trim();

/* ---- session (UI only; real auth is the servlet session cookie) ---- */
const USER_KEY = "servletAuthUser";
const getUser = () => { try { return JSON.parse(localStorage.getItem(USER_KEY)) || null; } catch { return null; } };
const setUser = (u) => localStorage.setItem(USER_KEY, JSON.stringify(u));
const logout = () => { localStorage.removeItem(USER_KEY); location.href = "index.html"; };

/* Page guard: returns the user, or redirects and returns null */
function requireRole(...roles) {
  const u = getUser();
  if (!u) { location.replace(roles.includes("admin") ? "admin-login.html" : "login.html"); return null; }
  if (!roles.includes(u.role)) { location.replace(u.role === "admin" ? "admin-dashboard.html" : "user-dashboard.html"); return null; }
  return u;
}
function mountTopbar(u) {
  $("#bar-right").innerHTML = `<a class="btn" href="portfolio.html">Portfolio</a><span class="muted sm">${esc(u.name)}</span><button class="btn" type="button" id="logout">Logout</button>`;
  $("#logout").onclick = logout;
}
function setStatus(text, isError) {
  const s = $("#status"); if (!s) return;
  s.textContent = text || ""; s.classList.toggle("error", !!isError);
}

/* ---- backend calls (same endpoints and form encoding as before) ---- */
async function request(path, values) {
  if (location.protocol === "file:") throw new Error("Open this page from http://localhost:5500, not as a file. The backend is at http://localhost:8080/swaraj.");
  let res;
  try {
    res = await fetch(API_BASE_URL + path, {
      method: "POST", credentials: "include",
      headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
      body: new URLSearchParams(values)
    });
  } catch { throw new Error("Could not reach the backend. Check that it is running and allows frontend requests."); }
  return { res, text: (await res.text()).trim() };
}
async function submit(path, values, fallback) {
  const { res, text } = await request(path, values);
  if (!res.ok) throw new Error(res.status >= 500 ? "The server could not complete this request. Check the backend logs." : plain(text) || `Request failed (${res.status}).`);
  return plain(text) || fallback;
}

/* ---- owner profile (GET /both/MyProfile returns HTML) ---- */
function parseProfile(markup) {
  const d = new DOMParser().parseFromString(markup, "text/html");
  const p = { name: d.querySelector("h2")?.textContent.trim() || "", email: "", phone: "", address: "", github: "", linkedin: "", about: "", image: d.querySelector("img")?.getAttribute("src")?.trim() || "" };
  d.querySelectorAll("p").forEach((para) => {
    const strong = para.querySelector("strong"); if (!strong) return;
    const key = strong.textContent.replace(":", "").trim().toLowerCase();
    if (key in p) p[key] = para.textContent.slice(strong.textContent.length).trim();
  });
  return p;
}
async function fetchProfile() {
  const res = await fetch(`${API_BASE_URL}/both/MyProfile`, { cache: "no-store", headers: { Accept: "text/html" } });
  if (!res.ok) throw new Error("Profile not available.");
  return parseProfile(await res.text());
}
const safeUrl = (v) => { try { const u = new URL(v); return /^https?:$/.test(u.protocol) ? u.href : ""; } catch { return ""; } };
const linkOrDash = (v) => { const u = safeUrl(v); return u ? `<a class="link" href="${esc(u)}" target="_blank" rel="noopener">${esc(u.replace(/^https?:\/\/(www\.)?/, ""))}</a>` : esc(v) || "—"; };
function profileRows(p) {
  return `<dl class="kv"><dt>Name</dt><dd>${esc(p.name) || "—"}</dd><dt>Email</dt><dd>${esc(p.email) || "—"}</dd><dt>Phone</dt><dd>${esc(p.phone) || "—"}</dd><dt>Address</dt><dd>${esc(p.address) || "—"}</dd><dt>GitHub</dt><dd>${linkOrDash(p.github)}</dd><dt>LinkedIn</dt><dd>${linkOrDash(p.linkedin)}</dd><dt>About</dt><dd>${esc(p.about) || "—"}</dd></dl>`;
}
