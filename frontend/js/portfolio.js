/* portfolio.html: public portfolio view (content from data.js + owner profile from the backend) */
const el = (t, c, h) => { const e = document.createElement(t); if (c) e.className = c; if (h !== undefined) e.innerHTML = h; return e; };
const textEl = (tag, className, text) => {
  const node = document.createElement(tag);
  if (className) node.className = className;
  node.textContent = text;
  return node;
};

const parseRecords = (markup) => {
  const doc = new DOMParser().parseFromString(markup, "text/html");
  return [...doc.querySelectorAll("p")].map((paragraph) => {
    const record = {};
    let key = "";
    paragraph.childNodes.forEach((node) => {
      if (node.nodeType === Node.ELEMENT_NODE && node.tagName === "STRONG") {
        key = node.textContent.replace(/:$/, "").trim().toLowerCase().replace(/[^a-z0-9]/g, "");
        record[key] = "";
      } else if (key && node.nodeType === Node.TEXT_NODE) {
        record[key] += node.textContent.replace(/^\s*\|\s*/, "").replace(/\s*\|\s*$/, "").trim();
      }
    });
    return record;
  }).filter((record) => Object.values(record).some(Boolean));
};

const notifyPortfolioEditor = (section, records) => {
  window.PortfolioEditor?.renderSection(section, records);
};
const adminRecordActions = (section, record) => {
  if (getUser()?.role !== "admin" || !record.id) return null;
  const actions = el("div", "portfolio-admin-actions");
  actions.innerHTML = `<button class="btn" type="button" data-edit="${section}" data-id="${esc(record.id)}">Edit</button><button class="btn danger" type="button" data-delete="${section}" data-id="${esc(record.id)}">Delete</button>`;
  return actions;
};

const fetchRecords = async (path) => {
  const response = await fetch(`${API_BASE_URL}${path}`, { cache: "no-store", headers: { Accept: "text/html" } });
  if (response.status === 404) return [];
  if (!response.ok) throw new Error(`Unable to load ${path}.`);
  return parseRecords(await response.text());
};

const showSectionMessage = (selector, message) => {
  const section = $(selector);
  section.replaceChildren(textEl("p", "muted", message));
};

const loadSkills = async () => {
  try {
    const skills = await fetchRecords("/admin/skill");
    const grid = $("#skills-grid");
    grid.replaceChildren();
    notifyPortfolioEditor("skills", skills);
    if (!skills.length) return showSectionMessage("#skills-grid", "No skills added yet.");

    const group = el("div", "sg");
    group.appendChild(textEl("h3", "", "Skills"));
    const list = document.createElement("ul");
    skills.forEach((skill) => {
      const item = textEl("li", "", skill.name || "");
      if (skill.status) item.appendChild(textEl("small", "", ` ${skill.status}`));
      const actions = adminRecordActions("skills", skill);
      if (actions) item.appendChild(actions);
      list.appendChild(item);
    });
    group.appendChild(list);
    grid.appendChild(group);
  } catch {
    notifyPortfolioEditor("skills", []);
    showSectionMessage("#skills-grid", "Skills are temporarily unavailable.");
  }
};

const loadProjects = async () => {
  try {
    const projects = await fetchRecords("/admin/myProject");
    const list = $("#projects-list");
    list.replaceChildren();
    notifyPortfolioEditor("projects", projects);
    if (!projects.length) return showSectionMessage("#projects-list", "No projects added yet.");

    projects.forEach((project) => {
      const article = el("article", "proj");
      article.appendChild(textEl("h3", "", project.project || "Project"));
      if (project.description) article.appendChild(textEl("p", "", project.description));
      if (project.technologies) {
        const tagList = el("div", "tags");
        project.technologies.split(/[,;|]/).map((technology) => technology.trim()).filter(Boolean)
          .forEach((technology) => tagList.appendChild(textEl("span", "tag", technology)));
        article.appendChild(tagList);
      }
      [["GitHub", project.github], ["Live project", project.liveurl]].forEach(([label, value]) => {
        const href = safeUrl(value);
        if (!href) return;
        const link = textEl("a", "link", label);
        link.href = href;
        link.target = "_blank";
        link.rel = "noopener";
        article.appendChild(link);
      });
      const actions = adminRecordActions("projects", project);
      if (actions) article.appendChild(actions);
      list.appendChild(article);
    });
  } catch {
    notifyPortfolioEditor("projects", []);
    showSectionMessage("#projects-list", "Projects are temporarily unavailable.");
  }
};

const loadEducation = async () => {
  try {
    const educations = await fetchRecords("/admin/myEducation");
    const container = $("#education-list");
    container.replaceChildren();
    notifyPortfolioEditor("education", educations);
    if (!educations.length) return showSectionMessage("#education-list", "No education added yet.");

    educations.forEach((education) => {
      const item = el("article", "edu");
      item.appendChild(textEl("h3", "", education.degree || "Education"));
      const details = [education.institution, education.yearofpassing, education.grade].filter(Boolean);
      if (details.length) item.appendChild(textEl("p", "", details.join(" · ")));
      if (education.description) item.appendChild(textEl("p", "muted", education.description));
      const actions = adminRecordActions("education", education);
      if (actions) item.appendChild(actions);
      container.appendChild(item);
    });
  } catch {
    notifyPortfolioEditor("education", []);
    showSectionMessage("#education-list", "Education is temporarily unavailable.");
  }
};

// dashboard link goes to the right place for whoever is signed in
const who = getUser();
$("#dash-link").href = who ? (who.role === "admin" ? "#top" : "user-dashboard.html") : "index.html";
$("#dash-link").textContent = who ? "Dashboard" : "Home";

// links
$("#gh").href = $("#gh2").href = SITE.github;
$("#li").href = $("#li2").href = SITE.linkedin;
$("#gh2").textContent = SITE.github.replace("https://", "");
$("#li2").textContent = SITE.linkedin.replace("https://www.", "");
$("#mail").href = "mailto:" + SITE.email; $("#mail").textContent = SITE.email;
$("#yr").textContent = new Date().getFullYear();

const updateProfileLink = (selector, value) => {
  const link = $(selector);
  link.href = "#";
  link.textContent = "";
  if (!value) return;
  try {
    const url = new URL(value);
    if (url.protocol !== "https:" && url.protocol !== "http:") return;
    link.href = url.href;
    link.textContent = url.href.replace(/^https?:\/\/(www\.)?/, "").replace(/\/$/, "");
  } catch (error) {
    return;
  }
};

const applyProfile = (profile) => {
  $("#profile-name").textContent = profile.name;
  $("#footer-name").textContent = profile.name;
  const aboutEl = $("#profile-about");
  if (aboutEl) aboutEl.textContent = profile.about || "";
  $("#mail").href = profile.email ? `mailto:${profile.email}` : "#";
  $("#mail").textContent = profile.email;
  updateProfileLink("#gh", profile.github);
  updateProfileLink("#gh2", profile.github);
  updateProfileLink("#li", profile.linkedin);
  updateProfileLink("#li2", profile.linkedin);

  $("#phone-row").hidden = !profile.phone;
  $("#phone").href = profile.phone ? `tel:${profile.phone.replace(/[^+\d]/g, "")}` : "#";
  $("#phone").textContent = profile.phone;
  $("#address-row").hidden = !profile.address;
  $("#address").textContent = profile.address;
  ph.replaceChildren();
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
  } else {
    ph.classList.add("empty");
    ph.textContent = profile.name.split(/\s+/).map((part) => part[0] || "").join("").slice(0, 2).toUpperCase();
  }
};

const loadProfile = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/both/MyProfile`, { cache: "no-store", headers: { Accept: "text/html" } });
    if (response.ok) applyProfile(parseProfile(await response.text()));
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

loadSkills();
loadProjects();
loadEducation();

window.refreshPortfolio = () => { loadProfile(); loadSkills(); loadProjects(); loadEducation(); };

// mobile menu
const btn = document.querySelector(".menu-btn"), nav = $("#nav-links");
const closeMenu = () => { nav.classList.remove("open"); btn.setAttribute("aria-expanded", "false"); };
btn.onclick = () => { const open = nav.classList.toggle("open"); btn.setAttribute("aria-expanded", String(open)); };
nav.addEventListener("click", (e) => { if (e.target.closest("a")) closeMenu(); });
document.addEventListener("keydown", (e) => { if (e.key === "Escape") closeMenu(); });

// active nav link
const links = [...nav.querySelectorAll("a")];
const io = new IntersectionObserver(es => es.forEach(e => {
  if (e.isIntersecting) links.forEach(l => l.classList.toggle("active", l.getAttribute("href") === "#" + e.target.id));
}), { rootMargin: "-40% 0px -55% 0px" });
document.querySelectorAll("main section[id]").forEach(s => io.observe(s));
