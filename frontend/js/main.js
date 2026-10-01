const $ = (s) => document.querySelector(s);
const el = (t, c, h) => { const e = document.createElement(t); if (c) e.className = c; if (h !== undefined) e.innerHTML = h; return e; };
const tags = (a) => a.map(t => `<span class="tag">${t}</span>`).join("");

const authModal = $("#auth-modal");
const authMessage = $("#auth-message");
const authSlot = $("#auth-slot");
const loginForm = $("#login-form");
const closeAuthBtn = $("#close-auth");

const storageKey = "servletAuthUser";

const getStoredUser = () => {
  try {
    const value = JSON.parse(localStorage.getItem(storageKey));
    return value || null;
  } catch (error) {
    return null;
  }
};

const saveUser = (user) => localStorage.setItem(storageKey, JSON.stringify(user));
const clearUser = () => localStorage.removeItem(storageKey);

const openAuthModal = () => {
  authModal.classList.remove("hidden");
  authModal.setAttribute("aria-hidden", "false");
  $("#login-username").focus();
};

const closeAuthModal = () => {
  authModal.classList.add("hidden");
  authModal.setAttribute("aria-hidden", "true");
  authMessage.textContent = "";
  loginForm.reset();
};

const renderAuth = () => {
  const user = getStoredUser();
  authSlot.innerHTML = user
    ? `<div class="auth-user"><span>Hi, ${user.name.split(" ")[0]}</span><button id="logout-btn" type="button" class="btn auth-logout">Logout</button></div>`
    : `<button id="login-btn" type="button" class="btn primary">Login</button>`;

  const loginBtn = $("#login-btn");
  const logoutBtn = $("#logout-btn");

  if (loginBtn) loginBtn.addEventListener("click", openAuthModal);
  if (logoutBtn) logoutBtn.addEventListener("click", () => { clearUser(); renderAuth(); });
};

const authenticateUser = async (username, password, loginType) => {
  const endpoint = loginType === "admin" ? "/admin/login" : "/user/login";
  authMessage.classList.remove("error");
  authMessage.textContent = "Contacting the server...";

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
      body: new URLSearchParams({ username, password })
    });
    const responseText = (await response.text()).trim();

    if (!response.ok) {
      throw new Error(responseText || `Sign-in failed (${response.status}). Check your credentials.`);
    }

    saveUser({ name: username, role: loginType });
    authMessage.textContent = responseText || "Signed in successfully.";
    setTimeout(() => {
      closeAuthModal();
      renderAuth();
    }, 600);
  } catch (error) {
    authMessage.textContent = error instanceof TypeError
      ? "Could not reach the backend. Check that it is running and allows frontend requests."
      : error.message;
    authMessage.classList.add("error");
  }
};

loginForm.addEventListener("submit", (event) => {
  event.preventDefault();
  authenticateUser(
    $("#login-username").value.trim(),
    $("#login-password").value,
    $("#login-type").value
  );
});

closeAuthBtn.addEventListener("click", closeAuthModal);
authModal.addEventListener("click", (event) => {
  if (event.target === authModal) closeAuthModal();
});

// links
$("#gh").href = $("#gh2").href = SITE.github;
$("#li").href = $("#li2").href = SITE.linkedin;
$("#gh2").textContent = SITE.github.replace("https://", "");
$("#li2").textContent = SITE.linkedin.replace("https://www.", "");
$("#mail").href = "mailto:" + SITE.email; $("#mail").textContent = SITE.email;
$("#grad").textContent = SITE.gradYear; $("#yr").textContent = new Date().getFullYear();

// photo
const ph = $("#photo");
if (SITE.photoUrl) { const i = new Image(); i.src = SITE.photoUrl; i.alt = "Photo of " + SITE.name; ph.appendChild(i); }
else { ph.classList.add("empty"); ph.textContent = "SK"; }

// learning path
const label = { done: "Done", now: "Learning now", next: "Next" };
$("#path").innerHTML = SITE.path.map(p => `<li class="${p.s}"><b>${p.n}</b><small>${label[p.s]}</small></li>`).join("");

// skills
$("#skills-grid").innerHTML = SITE.skills.map(g => `
  <div class="sg"><h3>${g.group}</h3>
  <ul>${g.items.map(i => `<li>${i.n}${i.learning ? '<em class="learning">Learning</em>' : ""}${i.note ? `<small> ${i.note}</small>` : ""}</li>`).join("")}</ul>
  ${g.note ? `<p class="muted sm">${g.note}</p>` : ""}</div>`).join("");

// projects
const list = $("#projects-list");
const mid = el("div", "mid");
SITE.projects.forEach(p => {
  if (p.featured) {
    list.appendChild(el("article", "proj feat", `
      <div><h3>${p.name}</h3><p>${p.desc}</p>
      <div class="tags">${tags(p.stack)}</div>
      <a class="link" href="${p.link}" target="_blank" rel="noopener">View on GitHub</a></div>
      <ul class="pts">${p.points.map(x => `<li>${x}</li>`).join("")}</ul>`));
  } else if (p.small) {
    list.appendChild(mid); list.appendChild(el("article", "proj small", `
      <h3>${p.name}</h3><p>${p.desc}</p><div class="tags">${tags(p.stack)}</div>
      <a class="link" href="${p.link}" target="_blank" rel="noopener">GitHub</a>`));
  } else {
    mid.appendChild(el("article", "proj", `<h3>${p.name}</h3><p>${p.desc}</p>
      <div class="tags">${tags(p.stack)}</div><a class="link" href="${p.link}" target="_blank" rel="noopener">GitHub</a>`));
  }
});
if (!mid.parentNode) list.appendChild(mid);

renderAuth();

// mobile menu
const btn = document.querySelector(".menu-btn"), nav = $("#nav-links");
btn.onclick = () => { const o = nav.classList.toggle("open"); btn.setAttribute("aria-expanded", o); };
nav.addEventListener("click", e => { if (e.target.tagName === "A") { nav.classList.remove("open"); btn.setAttribute("aria-expanded", false); } });

// active nav link
const links = [...nav.querySelectorAll("a")];
const io = new IntersectionObserver(es => es.forEach(e => {
  if (e.isIntersecting) links.forEach(l => l.classList.toggle("active", l.getAttribute("href") === "#" + e.target.id));
}), { rootMargin: "-40% 0px -55% 0px" });
document.querySelectorAll("main section[id]").forEach(s => io.observe(s));
