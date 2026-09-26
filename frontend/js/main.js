const $ = (s) => document.querySelector(s);
const el = (t, c, h) => { const e = document.createElement(t); if (c) e.className = c; if (h !== undefined) e.innerHTML = h; return e; };
const tags = (a) => a.map(t => `<span class="tag">${t}</span>`).join("");

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
