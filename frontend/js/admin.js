/* admin-dashboard.html: one tab per section, switched with the URL hash */
const admin = requireRole("admin");
const panel = $("#panel");

/* Endpoints and field names are the ones your servlets already use.
   Field = [name, label, type, required]  (type: text | url | area | check) */
const SECTIONS = {
  education: { single: "education", updLabel: "Update education", list: "/admin/myEducation", bool: false,
    add: "/admin/myEducation/AddEducation", upd: "/admin/myEducation/updateEducation", del: "/admin/myEducation/deleteEducation",
    fields: [["degree", "Degree", "text", 1], ["institution", "Institution", "text", 1], ["yearOfPassing", "Year of passing"], ["grade", "Grade"], ["description", "Description", "area"]] },
  skills: { single: "skill", updLabel: "Update skill", bool: true, list: "/admin/skill",
    add: "/admin/skill/addSkill", upd: "/admin/skill/updateSkill", del: "/admin/skill/DeleteSkill",
    fields: [["skillName", "Skill name", "text", 1], ["isCompleted", "Completed", "check"]] },
  projects: { single: "project", updLabel: "Update project", bool: true, list: "/admin/myProject",
    add: "/admin/myProject/addMyProject", upd: "/admin/myProject/updateMyProject", del: "/admin/myProject/deleteMyProject",
    fields: [["projectName", "Project name", "text", 1], ["projectDescription", "Description", "area"], ["technologiesUsed", "Technologies used"], ["githubUrl", "GitHub URL", "url"], ["liveUrl", "Live URL", "url"], ["isCompleted", "Completed", "check"]] }
};
const PROFILE_FIELDS = [["name", "Name"], ["email", "Email", "email"], ["phone", "Phone"], ["address", "Address"], ["githubURL", "GitHub URL", "url"], ["linkedinURL", "LinkedIn URL", "url"], ["profileImage", "Profile image URL", "url"], ["aboutMe", "About me", "area"]];

if (admin) { mountTopbar(admin); window.addEventListener("hashchange", show); show(); }

function show() {
  const tab = location.hash.slice(1) in SECTIONS || location.hash === "#profile" ? location.hash.slice(1) : "profile";
  document.querySelectorAll("#tabs a").forEach((a) => a.classList.toggle("on", a.getAttribute("href") === "#" + tab));
  setStatus("");
  tab === "profile" ? profileTab() : manageTab(tab);
}

const fieldHtml = ([n, l, t, req], optional) =>
  t === "check" ? `<label class="check-row"><input type="checkbox" name="${n}" value="true"> ${l}</label>`
  : t === "area" ? `<label>${l}<textarea name="${n}" rows="3"></textarea></label>`
  : `<label>${l}<input name="${n}" type="${t === "url" ? "url" : t === "email" ? "email" : "text"}" ${req && !optional ? "required" : ""}></label>`;

/* ---- My Profile: view, then edit ---- */
async function profileTab() {
  panel.innerHTML = `<section class="panel"><p class="muted">Loading...</p></section>`;
  let p;
  try { p = await fetchProfile(); }
  catch { panel.innerHTML = `<section class="panel"><h3>My Profile</h3><p class="muted">The profile could not be loaded. Check that the backend is running.</p></section>`; return; }

  const toForm = { name: p.name, email: p.email, phone: p.phone, address: p.address, githubURL: p.github, linkedinURL: p.linkedin, profileImage: p.image, aboutMe: p.about };
  const view = () => {
    panel.innerHTML = `<section class="panel"><div class="row"><h3>My Profile</h3><button class="btn" id="edit" type="button">Edit profile</button></div>${profileRows(p)}</section>`;
    $("#edit").onclick = edit;
  };
  const edit = () => {
    panel.innerHTML = `<section class="panel"><h3 style="margin-bottom:16px">Edit profile</h3><form id="f" class="stack-form">${PROFILE_FIELDS.map((f) => fieldHtml(f)).join("")}
      <div class="btns"><button class="btn primary" type="submit">Save profile</button><button class="btn" type="button" id="cancel">Cancel</button></div></form></section>`;
    const f = $("#f");
    PROFILE_FIELDS.forEach(([n]) => (f.elements[n].value = toForm[n] || ""));
    $("#cancel").onclick = view;
    f.onsubmit = async (e) => {
      e.preventDefault();
      try {
        const msg = await submit("/admin/myProfile/updateMyProfile", Object.fromEntries(new FormData(f).entries()), "Profile updated");
        p = await fetchProfile(); Object.assign(toForm, { name: p.name, email: p.email, phone: p.phone, address: p.address, githubURL: p.github, linkedinURL: p.linkedin, profileImage: p.image, aboutMe: p.about });
        view(); setStatus(msg);
      } catch (err) { setStatus(err.message, true); }
    };
  };
  view();
}

/* ---- Education / Skills / Projects: add form + update-or-delete by ID ---- */
function manageTab(key) {
  const c = SECTIONS[key], upd = c.updFields ?? c.fields;
  panel.innerHTML = `
    <section class="panel"><h3 style="margin-bottom:16px">Add ${c.single}</h3>
      <form id="addf" class="stack-form">${c.fields.map((f) => fieldHtml(f)).join("")}<button class="btn primary" type="submit">Add ${c.single}</button></form></section>
    <section class="panel"><h3 style="margin-bottom:16px">Update or delete ${c.single}</h3>
      <form id="manf" class="stack-form">
        <label>${c.single[0].toUpperCase() + c.single.slice(1)} ID<input name="id" type="number" min="1" required></label>
        <label>Action<select name="action"><option value="upd">${c.updLabel}</option><option value="del">Delete ${c.single}</option></select></label>
        <div class="stack-form" data-opt>${upd.map((f) => fieldHtml(f, true)).join("")}</div>
        <button class="btn" type="submit">Apply change</button>
      </form>
      ${c.list ? `<div class="mini-list" id="list"></div>` : ""}
    </section>`;

  const manf = $("#manf");
  manf.elements.action.onchange = () => ($("[data-opt]").hidden = manf.elements.action.value === "del");
  const strip = (o) => Object.fromEntries(Object.entries(o).filter(([, v]) => v !== ""));

  $("#addf").onsubmit = async (e) => {
    e.preventDefault();
    const v = strip(Object.fromEntries(new FormData(e.target).entries()));
    if (c.bool && !v.isCompleted) v.isCompleted = "false";
    try { setStatus(await submit(c.add, v, `${c.single} added`)); e.target.reset(); c.list && loadItems(key); }
    catch (err) { setStatus(err.message, true); }
  };
  manf.onsubmit = async (e) => {
    e.preventDefault();
    const { action, ...v } = strip(Object.fromEntries(new FormData(manf).entries()));
    if (c.bool && action !== "del" && v.isCompleted === undefined) v.isCompleted = "false";
    try { setStatus(await submit(action === "del" ? c.del : c.upd, v, action === "del" ? `${c.single} deleted` : `${c.single} updated`)); manf.reset(); c.list && loadItems(key); }
    catch (err) { setStatus(err.message, true); }
  };
  c.list && loadItems(key);
}

async function loadItems(key) {
  const list = $("#list"); if (!list) return;
  try {
    const section = SECTIONS[key];
    const res = await fetch(`${API_BASE_URL}${section.list}`, { cache: "no-store", credentials: "include" });
    if (!res.ok) { list.innerHTML = `<p class="muted">No ${esc(section.single)} records found.</p>`; return; }
    const tmp = document.createElement("div"); tmp.innerHTML = await res.text();
    const items = [...tmp.querySelectorAll("p")].map((p) => p.textContent.trim()).filter(Boolean);
    list.innerHTML = items.length ? items.map((t) => `<div class="mini-skill">${esc(t)}</div>`).join("") : `<p class="muted">No ${esc(section.single)} records found.</p>`;
  } catch { list.innerHTML = `<p class="muted">Unable to load ${esc(SECTIONS[key].single)} records.</p>`; }
}
