const $ = (s) => document.querySelector(s);
const el = (t, c, h) => { const e = document.createElement(t); if (c) e.className = c; if (h !== undefined) e.innerHTML = h; return e; };
const tags = (a) => a.map(t => `<span class="tag">${t}</span>`).join("");

const authModal = $("#auth-modal");
const authMessage = $("#auth-message");
const authSlot = $("#auth-slot");
const loginForm = $("#login-form");
const closeAuthBtn = $("#close-auth");
const emailField = $("#registration-email-field");
const loginRoleField = $("#login-role-field");
const loginRoleSelect = $("#login-role");
const showLoginBtn = $("#show-login");
const showRegisterBtn = $("#show-register");
let authMode = "login";

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

const updateLoginCopy = () => {
  const role = loginRoleSelect.value;
  $("#auth-title").textContent = role === "admin" ? "Admin login" : "User login";
  $("#auth-hint").textContent = `Sign in with your ${role} username and password.`;
};

const setAuthMode = (mode) => {
  if (mode === "register" && loginRoleSelect.value !== "user") mode = "login";
  authMode = mode;
  const registering = mode === "register";
  showRegisterBtn.hidden = loginRoleSelect.value !== "user";
  if (registering) {
    $("#auth-title").textContent = "Create your account";
    $("#auth-hint").textContent = "Create a user account using the registration servlet.";
  } else {
    updateLoginCopy();
  }
  $("#auth-submit").textContent = registering ? "Register" : "Sign in";
  $("#login-password").autocomplete = registering ? "new-password" : "current-password";
  emailField.hidden = !registering;
  loginRoleField.hidden = registering;
  $("#register-email").required = registering;
  $("#guest-login").hidden = registering;
  showLoginBtn.classList.toggle("active", !registering);
  showLoginBtn.setAttribute("aria-pressed", String(!registering));
  showRegisterBtn.classList.toggle("active", registering);
  showRegisterBtn.setAttribute("aria-pressed", String(registering));
  authMessage.textContent = "";
  authMessage.classList.remove("error");
};

const openAuthModal = () => {
  setAuthMode("login");
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
  if (logoutBtn) logoutBtn.addEventListener("click", () => { clearUser(); renderAuth(); updateDashboardVisibility(); });
  updateDashboardVisibility();
};

const updateDashboardVisibility = () => {
  const user = getStoredUser();
  const dashboard = $("#dashboard");
  const guestDashboard = $("#guest-dashboard");
  const adminDashboard = $("#admin-dashboard");
  const userDashboard = $("#user-dashboard");

  if (!user) {
    dashboard.hidden = true;
    guestDashboard.hidden = true;
    adminDashboard.hidden = true;
    userDashboard.hidden = true;
    return;
  }

  dashboard.hidden = false;
  guestDashboard.hidden = user.role !== "guest";
  adminDashboard.hidden = user.role !== "admin";
  userDashboard.hidden = user.role !== "user";
  $("#dashboard-role").textContent = user.role === "admin" ? "Administrator" : user.role === "user" ? "Signed-in user" : "Guest view";
  $("#dashboard-title").textContent = user.role === "admin"
    ? "Portfolio administration"
    : `Welcome, ${user.name.split(" ")[0]}`;
  $("#guest-public-summary").innerHTML = `
    <a class="mini-skill" href="#about">About the developer</a>
    <a class="mini-skill" href="#skills">Skills and learning path</a>
    <a class="mini-skill" href="#projects">Projects and education</a>
    <a class="mini-skill" href="#contact">Contact details</a>`;

  if (user.role === "admin") {
    loadAdminProfileToForm();
    loadAdminSkills();
  }

  if (user.role === "user") {
    loadPublicAdminProfile();
  }
};

$("#guest-sign-in").addEventListener("click", openAuthModal);

const submitAuthRequest = async (endpoint, values, successText, user, isRegistration = false) => {
  authMessage.classList.remove("error");
  authMessage.textContent = "Contacting the server...";
  const submitButton = $("#auth-submit");
  submitButton.disabled = true;
  loginForm.setAttribute("aria-busy", "true");

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
      body: new URLSearchParams(values)
    });
    const responseText = (await response.text()).trim();

    const registrationSucceeded = isRegistration && response.status === 201;
    const loginSucceeded = !isRegistration && (response.redirected || responseText.includes(successText));
    if (!response.ok || (!registrationSucceeded && !loginSucceeded)) {
      throw new Error(response.status >= 500
        ? "The server could not complete this request. Check your credentials and backend logs."
        : responseText || `Request failed (${response.status}).`);
    }

    if (isRegistration) {
      setAuthMode("login");
      $("#login-password").value = "";
      authMessage.textContent = "Account created. Sign in with your new credentials.";
      return;
    }

    if (response.redirected) {
      window.location.assign(response.url);
      return;
    }

    saveUser(user);
    authMessage.textContent = "Signed in successfully.";
    setTimeout(() => {
      closeAuthModal();
      renderAuth();
    }, 600);
  } catch (error) {
    authMessage.textContent = error instanceof TypeError
      ? "Could not reach the backend. Check that it is running and allows frontend requests."
      : error.message;
    authMessage.classList.add("error");
  } finally {
    submitButton.disabled = false;
    loginForm.removeAttribute("aria-busy");
  }
};

const authenticateUser = (username, password, role) => {
  const isAdmin = role === "admin";
  return submitAuthRequest(
    isAdmin ? "/admin/login" : "/user/login",
    { username, password },
    isAdmin ? "Admin Logged in" : "User logged in successfully",
    { name: username, role }
  );
};

const registerUser = (username, password, email) => submitAuthRequest(
  "/user/register",
  { username, password, email, role: "user" },
  "User Registered",
  { name: username, role: "user" },
  true
);

loginForm.addEventListener("submit", (event) => {
  event.preventDefault();
  const username = $("#login-username").value.trim();
  const password = $("#login-password").value;

  if (authMode === "register") {
    registerUser(username, password, $("#register-email").value.trim());
  } else {
    authenticateUser(username, password, loginRoleSelect.value);
  }
});

showLoginBtn.addEventListener("click", () => setAuthMode("login"));
showRegisterBtn.addEventListener("click", () => setAuthMode("register"));
loginRoleSelect.addEventListener("change", () => {
  showRegisterBtn.hidden = loginRoleSelect.value !== "user";
  if (authMode === "register" && loginRoleSelect.value !== "user") setAuthMode("login");
  else if (authMode === "login") updateLoginCopy();
});
$("#guest-login").addEventListener("click", () => {
  saveUser({ name: "Guest", role: "guest" });
  closeAuthModal();
  renderAuth();
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

const parseProfileHtml = (markup) => {
  const document = new DOMParser().parseFromString(markup, "text/html");
  const profile = { name: document.querySelector("h2")?.textContent.trim() || "" };

  document.querySelectorAll("p").forEach((paragraph) => {
    const label = paragraph.querySelector("strong")?.textContent.replace(/:$/, "").trim().toLowerCase();
    if (!label) return;
    profile[label] = paragraph.textContent.slice(paragraph.querySelector("strong").textContent.length).trim();
  });

  profile.image = document.querySelector("img")?.getAttribute("src")?.trim() || "";
  return profile;
};

const updateProfileLink = (selector, value) => {
  if (!value) return;
  try {
    const url = new URL(value);
    if (url.protocol !== "https:" && url.protocol !== "http:") return;
    const link = $(selector);
    link.href = url.href;
    link.textContent = url.href.replace(/^https?:\/\/(www\.)?/, "").replace(/\/$/, "");
  } catch (error) {
    return;
  }
};

const applyProfile = (profile) => {
  if (profile.name) {
    $("#profile-name").textContent = profile.name;
    $("#footer-name").textContent = profile.name;
  }
  if (profile.about) {
    $("#profile-about").textContent = profile.about;
    $("#about-extra").hidden = true;
  }
  if (profile.email) {
    $("#mail").href = `mailto:${profile.email}`;
    $("#mail").textContent = profile.email;
  }
  updateProfileLink("#gh", profile.github);
  updateProfileLink("#gh2", profile.github);
  updateProfileLink("#li", profile.linkedin);
  updateProfileLink("#li2", profile.linkedin);

  if (profile.phone) {
    $("#phone").href = `tel:${profile.phone.replace(/[^+\d]/g, "")}`;
    $("#phone").textContent = profile.phone;
    $("#phone-row").hidden = false;
  }
  if (profile.address) {
    $("#address").textContent = profile.address;
    $("#address-row").hidden = false;
  }
  if (profile.image) {
    try {
      const imageUrl = new URL(profile.image, API_BASE_URL);
      if (imageUrl.protocol === "https:" || imageUrl.protocol === "http:") {
        const image = new Image();
        image.src = imageUrl.href;
        image.alt = `Photo of ${profile.name || SITE.name}`;
        ph.replaceChildren(image);
        ph.classList.remove("empty");
      }
    } catch (error) {
      return;
    }
  }
};

const loadProfile = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/both/MyProfile`, { headers: { Accept: "text/html" } });
    if (response.ok) applyProfile(parseProfileHtml(await response.text()));
  } catch {}
};

// photo
const ph = $("#photo");
if (SITE.photoUrl) { const i = new Image(); i.src = SITE.photoUrl; i.alt = "Photo of " + SITE.name; ph.appendChild(i); }
else { ph.classList.add("empty"); ph.textContent = "SK"; }
loadProfile();

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

const parseProfileFields = (markup) => {
  const document = new DOMParser().parseFromString(markup, "text/html");
  const fields = {
    name: document.querySelector("h2")?.textContent.trim() || "",
    email: "",
    phone: "",
    address: "",
    githubURL: "",
    linkedinURL: "",
    aboutMe: "",
    profileImage: ""
  };

  document.querySelectorAll("p").forEach((p) => {
    const strong = p.querySelector("strong");
    if (!strong) return;
    const key = strong.textContent.replace(":", "").trim().toLowerCase();
    const value = p.textContent.slice(strong.textContent.length).trim();
    if (key === "email") fields.email = value;
    if (key === "phone") fields.phone = value;
    if (key === "address") fields.address = value;
    if (key === "github") fields.githubURL = value;
    if (key === "linkedin") fields.linkedinURL = value;
    if (key === "about") fields.aboutMe = value;
  });

  const img = document.querySelector("img");
  if (img) fields.profileImage = img.getAttribute("src") || "";
  return fields;
};

const loadAdminProfileToForm = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/both/MyProfile`, { headers: { Accept: "text/html" } });
    if (!response.ok) return;
    const fields = parseProfileFields(await response.text());
    const form = $("#admin-profile-form");
    if (!form) return;
    form.elements.name.value = fields.name || "";
    form.elements.email.value = fields.email || "";
    form.elements.phone.value = fields.phone || "";
    form.elements.address.value = fields.address || "";
    form.elements.githubURL.value = fields.githubURL || "";
    form.elements.linkedinURL.value = fields.linkedinURL || "";
    form.elements.profileImage.value = fields.profileImage || "";
    form.elements.aboutMe.value = fields.aboutMe || "";
  } catch (error) {
    return;
  }
};

const loadPublicAdminProfile = async () => {
  const box = $("#public-admin-profile");
  if (!box) return;
  try {
    const response = await fetch(`${API_BASE_URL}/both/MyProfile`, { headers: { Accept: "text/html" } });
    if (!response.ok) {
      box.innerHTML = "<p>Profile not available.</p>";
      return;
    }
    const profile = parseProfileFields(await response.text());
    box.innerHTML = `
      <div class="mini-profile">
        <h4>${profile.name || "Admin"}</h4>
        <p>${profile.email || "No email added"}</p>
        <p>${profile.phone || "No phone added"}</p>
        <p>${profile.address || "No address added"}</p>
      </div>`;
  } catch (error) {
    box.innerHTML = "<p>Unable to load admin profile.</p>";
  }
};

const loadAdminSkills = async () => {
  const list = $("#admin-skills-list");
  if (!list) return;
  try {
    const response = await fetch(`${API_BASE_URL}/admin/skill`, { credentials: "include" });
    if (!response.ok) {
      list.innerHTML = "<p>No skills found.</p>";
      return;
    }
    const html = await response.text();
    const temp = document.createElement("div");
    temp.innerHTML = html;
    const items = [...temp.querySelectorAll("p")].map(p => p.textContent.trim());
    list.innerHTML = items.length
      ? items.map(item => `<div class="mini-skill">${item}</div>`).join("")
      : "<p>No skills found.</p>";
  } catch (error) {
    list.innerHTML = "<p>Unable to load skills.</p>";
  }
};

const submitForm = async (url, formData, successMessage) => {
  const response = await fetch(`${API_BASE_URL}${url}`, {
    method: "POST",
    credentials: "include",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body: new URLSearchParams(formData)
  });
  const text = (await response.text()).trim();
  if (!response.ok) {
    throw new Error(text || `Request failed (${response.status})`);
  }
  return text || successMessage;
};

$("#admin-profile-form")?.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  const values = Object.fromEntries(form.entries());
  try {
    const result = await submitForm("/admin/myProfile/updateMyProfile", values, "Profile updated");
    alert(result);
    loadAdminProfileToForm();
  } catch (error) {
    alert(error.message);
  }
});

$("#add-skill-form")?.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  const values = Object.fromEntries(form.entries());
  const payload = {
    skillName: values.skillName,
    isCompleted: values.isCompleted === "true" ? "true" : "false"
  };
  try {
    const result = await submitForm("/admin/skill/addSkill", payload, "Skill added");
    alert(result);
    event.currentTarget.reset();
    loadAdminSkills();
  } catch (error) {
    alert(error.message);
  }
});

const bindAdminForm = (formId, routes, successText) => {
  $(formId)?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const values = Object.fromEntries(new FormData(form).entries());
    const action = values.action;
    delete values.action;
    Object.keys(values).forEach((key) => {
      if (values[key] === "") delete values[key];
    });

    if (formId === "#add-project-form" && values.isCompleted === undefined) {
      values.isCompleted = "false";
    }

    try {
      const result = await submitForm(routes[action || "add"], values, successText);
      alert(result);
      form.reset();
      loadAdminSkills();
    } catch (error) {
      alert(error.message);
    }
  });
};

bindAdminForm("#manage-skill-form", {
  update: "/admin/skill/updateSkill",
  delete: "/admin/skill/DeleteSkill"
}, "Skill updated");
bindAdminForm("#add-education-form", {
  add: "/admin/myEducation/AddEducation"
}, "Education added");
bindAdminForm("#manage-education-form", {
  update: "/admin/myEducation/updateEducation",
  delete: "/admin/myEducation/deleteEducation"
}, "Education updated");
bindAdminForm("#add-project-form", {
  add: "/admin/myProject/addMyProject"
}, "Project added");
bindAdminForm("#manage-project-form", {
  update: "/admin/myProject/updateMyProject",
  delete: "/admin/myProject/deleteMyProject"
}, "Project updated");

$("#update-username-form")?.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  try {
    const result = await submitForm("/user/updateUsername", Object.fromEntries(form.entries()), "Username updated");
    alert(result);
    const user = getStoredUser();
    if (user) {
      user.name = form.get("username");
      saveUser(user);
      renderAuth();
    }
    event.currentTarget.reset();
  } catch (error) {
    alert(error.message);
  }
});

$("#update-email-form")?.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  try {
    const result = await submitForm("/user/updateUserEmail", Object.fromEntries(form.entries()), "Email updated");
    alert(result);
    event.currentTarget.reset();
  } catch (error) {
    alert(error.message);
  }
});

$("#update-password-form")?.addEventListener("submit", async (event) => {
  event.preventDefault();
  const form = new FormData(event.currentTarget);
  try {
    const result = await submitForm("/user/updateUserPassword", Object.fromEntries(form.entries()), "Password updated");
    alert(result);
    event.currentTarget.reset();
  } catch (error) {
    alert(error.message);
  }
});

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
