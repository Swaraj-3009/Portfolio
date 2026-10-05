/* Admin controls are attached to the public portfolio's existing sections. */
(() => {
  if (getUser()?.role !== "admin") return;

  const profileEndpoint = "/admin/myProfile/updateMyProfile";
  const profileKeys = ["name", "email", "phone", "address", "githubURL", "linkedinURL", "profileImage", "aboutMe"];
  const profileGroups = {
    identity: [["name", "Name", "text", true], ["profileImage", "Profile image URL", "url"]],
    about: [["aboutMe", "About me", "area"]],
    contact: [["email", "Email", "email"], ["phone", "Phone", "tel"], ["address", "Location"], ["githubURL", "GitHub URL", "url"], ["linkedinURL", "LinkedIn URL", "url"]]
  };
  const collections = {
    skills: { label: "skill", list: "/admin/skill", add: "/admin/skill/addSkill", update: "/admin/skill/updateSkill", remove: "/admin/skill/DeleteSkill", fields: [["skillName", "Skill name", "text", true], ["isCompleted", "Currently learning", "check"]] },
    projects: { label: "project", list: "/admin/myProject", add: "/admin/myProject/addMyProject", update: "/admin/myProject/updateMyProject", remove: "/admin/myProject/deleteMyProject", fields: [["projectName", "Project name", "text", true], ["projectDescription", "Description", "area"], ["technologiesUsed", "Technologies", "text"], ["githubUrl", "GitHub URL", "url"], ["liveUrl", "Live URL", "url"], ["isCompleted", "Completed", "check"]] },
    education: { label: "education", list: "/admin/myEducation", add: "/admin/myEducation/AddEducation", update: "/admin/myEducation/updateEducation", remove: "/admin/myEducation/deleteEducation", fields: [["degree", "Degree or qualification", "text", true], ["institution", "Institution", "text", true], ["yearOfPassing", "Year of passing", "text"], ["grade", "Grade", "text"], ["description", "Description", "area"]] }
  };
  const profileValues = { name: "", email: "", phone: "", address: "", githubURL: "", linkedinURL: "", profileImage: "", aboutMe: "" };
  const collectionValues = { skills: [], projects: [], education: [] };
  const html = (tag, className, text) => { const element = document.createElement(tag); if (className) element.className = className; if (text !== undefined) element.textContent = text; return element; };

  function preparePortfolioEditing() {
    document.querySelector("#status")?.classList.add("admin-status");
    const heroActions = document.querySelector(".hero-text .btns");
    if (heroActions) heroActions.appendChild(actionButton("Edit intro", "profile-edit", "identity"));
    addSectionAction("about", "Edit about", "profile-edit", "about");
    addSectionAction("contact", "Edit contact", "profile-edit", "contact");
    addSectionAction("skills", "+ Add skill", "record-add", "skills", true);
    addSectionAction("projects", "+ Add project", "record-add", "projects", true);
    addSectionAction("education", "+ Add education", "record-add", "education", true);
    const dashboardLink = document.querySelector("#dash-link");
    if (dashboardLink) dashboardLink.textContent = "Editing portfolio";
    const nav = document.querySelector("#nav-links");
    if (nav) nav.appendChild(actionButton("Log out", "admin-logout", ""));

    Object.keys(collections).forEach((key) => {
      const section = document.querySelector(`#${key}`);
      if (!section) return;
      const editor = html("div", "portfolio-editor");
      editor.id = `${key}-editor`;
      editor.hidden = true;
      section.appendChild(editor);
    });
    ["about", "contact"].forEach((key) => {
      const editor = html("div", "portfolio-editor");
      editor.id = `${key}-editor`;
      editor.hidden = true;
      document.querySelector(`#${key}`)?.appendChild(editor);
    });
    const identityEditor = html("div", "portfolio-editor");
    identityEditor.id = "identity-editor";
    identityEditor.hidden = true;
    document.querySelector(".hero-text")?.appendChild(identityEditor);

    document.addEventListener("click", handleClick);
    document.addEventListener("click", (event) => {
      if (event.target.closest("[data-admin-logout]")) logout();
    });
    document.addEventListener("submit", handleSubmit);
    window.PortfolioEditor = { renderSection };
    loadRecords();
    loadProfile();
  }

  function actionButton(label, type, value, primary = false) {
    const button = html("button", `btn${primary ? " primary" : ""}`, label);
    button.type = "button";
    const property = type.replace(/-([a-z])/g, (_, character) => character.toUpperCase());
    button.dataset[property] = value;
    return button;
  }

  function addSectionAction(id, label, type, value, primary = false) {
    const section = document.querySelector(`#${id}`);
    const heading = section?.querySelector("h2");
    if (!section || !heading) return;
    const titleRow = html("div", "portfolio-section-heading");
    heading.parentNode.insertBefore(titleRow, heading);
    titleRow.appendChild(heading);
    const actions = html("div", "portfolio-section-actions");
    actions.appendChild(actionButton(label, type, value, primary));
    titleRow.appendChild(actions);
  }

  function renderSection(key, items) {
    if (!Object.hasOwn(collectionValues, key)) return;
    collectionValues[key] = items;
    const section = document.querySelector(`#${key}`);
    const cards = key === "skills" ? [...section.querySelectorAll("#skills-grid li")] : key === "projects" ? [...section.querySelectorAll("#projects-list .proj")] : [...section.querySelectorAll("#education-list .edu")];
    items.forEach((item, index) => {
      const card = cards[index];
      if (!card || !item.id || card.querySelector(".portfolio-admin-actions")) return;
      const actions = html("div", "portfolio-admin-actions");
      const edit = actionButton("Edit", "edit", key);
      const remove = actionButton("Delete", "delete", key);
      edit.dataset.id = item.id;
      remove.dataset.id = item.id;
      actions.append(edit, remove);
      card.appendChild(actions);
    });
  }

  async function loadRecords() {
    await Promise.all(Object.entries(collections).map(async ([key, config]) => {
      try {
        const response = await fetch(`${API_BASE_URL}${config.list}`, { cache: "no-store", credentials: "include", headers: { Accept: "text/html" } });
        if (response.status !== 404 && !response.ok) throw new Error(`Unable to load ${config.label} records.`);
        const items = response.status === 404 ? [] : parseRecords(await response.text());
        renderSection(key, items);
      } catch { /* The public portfolio already displays a section-specific load message. */ }
    }));
  }

  function parseRecords(markup) {
    const parsed = new DOMParser().parseFromString(markup, "text/html");
    return [...parsed.querySelectorAll("p")].map((paragraph) => {
      const record = {};
      let key = "";
      paragraph.childNodes.forEach((node) => {
        if (node.nodeType === Node.ELEMENT_NODE && node.tagName === "STRONG") {
          key = node.textContent.replace(/:$/, "").toLowerCase().replace(/[^a-z0-9]/g, "");
          record[key] = "";
        } else if (key && node.nodeType === Node.TEXT_NODE) {
          record[key] += node.nodeValue.replace(/^\s*\|\s*/, "").replace(/\s*\|\s*$/, "").trim();
        }
      });
      return record;
    }).filter((record) => record.id);
  }

  async function loadProfile() {
    try {
      const profile = await fetchProfile();
      Object.assign(profileValues, {
        name: profile.name, email: profile.email, phone: profile.phone, address: profile.address,
        githubURL: profile.github, linkedinURL: profile.linkedin, profileImage: profile.image, aboutMe: profile.about
      });
    } catch { /* The public page remains viewable while the backend is unavailable. */ }
  }

  function handleClick(event) {
    const button = event.target.closest("button");
    if (!button) return;
    if (button.hasAttribute("data-profile-edit")) return openProfileEditor(button.dataset.profileEdit);
    if (button.hasAttribute("data-record-add")) return openRecordEditor(button.dataset.recordAdd, null);
    if (button.hasAttribute("data-edit")) return openRecordEditor(button.dataset.edit, button.dataset.id);
    if (button.hasAttribute("data-delete")) return deleteRecord(button.dataset.delete, button.dataset.id, button);
    if (button.hasAttribute("data-editor-cancel")) {
      const editor = button.closest(".portfolio-editor");
      editor.hidden = true;
      editor.replaceChildren();
    }
  }

  function openProfileEditor(group) {
    const editor = document.querySelector(`#${group}-editor`);
    const fields = profileGroups[group];
    renderForm(editor, `Edit ${group === "identity" ? "intro" : group === "about" ? "about me" : "contact details"}`, fields, (name) => profileValues[name], { profileGroup: group });
  }

  function openRecordEditor(key, id) {
    const entry = id ? collectionValues[key].find((record) => String(record.id) === String(id)) : null;
    if (id && !entry) return setStatus("That entry could not be loaded. Refresh the portfolio and try again.", true);
    const editor = document.querySelector(`#${key}-editor`);
    const config = collections[key];
    const form = renderForm(editor, `${entry ? "Edit" : "Add"} ${config.label}`, config.fields,
      (name) => entry ? getRecordValue(key, entry, name) : (name === "isCompleted" ? false : ""),
      { recordForm: key, mode: entry ? "edit" : "add", id: entry?.id || "" });
    editor.scrollIntoView({ behavior: "smooth", block: "nearest" });
    form.querySelector("input,textarea")?.focus({ preventScroll: true });
  }

  function renderForm(editor, title, fields, valueFor, metadata) {
    const form = html("form", "stack-form portfolio-editor-form");
    Object.entries(metadata).forEach(([key, value]) => { if (value !== "") form.dataset[key] = value; });
    const top = html("div", "portfolio-editor-title");
    top.append(html("h3", "", title));
    const close = actionButton("×", "editor-cancel", "");
    close.classList.add("portfolio-editor-close");
    close.setAttribute("aria-label", "Close editor");
    top.appendChild(close);
    form.appendChild(top);
    fields.forEach(([name, label, type = "text", required = false]) => {
      const fieldLabel = html("label", "", type === "check" ? "" : label);
      let control;
      if (type === "area") {
        control = html("textarea");
        control.rows = 4;
      } else {
        control = html("input");
        control.type = type === "check" ? "checkbox" : type;
      }
      control.name = name;
      if (type === "check") {
        control.value = "true";
        control.checked = Boolean(valueFor(name));
        fieldLabel.classList.add("check-row");
        fieldLabel.append(control, document.createTextNode(` ${label}`));
      } else {
        control.value = valueFor(name) || "";
        if (required) control.required = true;
        fieldLabel.appendChild(control);
      }
      form.appendChild(fieldLabel);
    });
    const actions = html("div", "btns");
    const save = html("button", "btn primary", "Save changes");
    save.type = "submit";
    const cancel = actionButton("Cancel", "editor-cancel", "");
    actions.append(save, cancel);
    form.appendChild(actions);
    editor.replaceChildren(form);
    editor.hidden = false;
    return form;
  }

  function getRecordValue(key, record, name) {
    const fields = {
      skills: { skillName: ["name", "skillname"], isCompleted: ["status", "iscompleted"] },
      projects: { projectName: ["project", "projectname"], projectDescription: ["description"], technologiesUsed: ["technologies", "technologiesused"], githubUrl: ["github", "githuburl"], liveUrl: ["liveurl", "live"], isCompleted: ["status", "iscompleted"] },
      education: { degree: ["degree"], institution: ["institution"], yearOfPassing: ["yearofpassing"], grade: ["grade"], description: ["description"] }
    }[key][name];
    const value = fields.map((field) => record[field]).find((candidate) => candidate !== undefined) || "";
    return name === "isCompleted" ? /completed|true|yes/i.test(value) : value;
  }

  async function handleSubmit(event) {
    const form = event.target.closest(".portfolio-editor-form");
    if (!form) return;
    event.preventDefault();
    const button = event.submitter;
    if (form.dataset.profileGroup) return saveProfile(form, button);
    return saveRecord(form, button);
  }

  async function saveProfile(form, button) {
    const values = Object.fromEntries(profileKeys.map((key) => [key, profileValues[key]]));
    Object.assign(values, Object.fromEntries(new FormData(form).entries()));
    try {
      await withBusyButton(button, "Saving…", async () => {
        await submit(profileEndpoint, values, "Profile updated");
        await loadProfile();
        closeEditor(form);
        window.refreshPortfolio?.();
        setStatus("Portfolio section updated.");
      });
    } catch (error) { setStatus(error.message, true); }
  }

  async function saveRecord(form, button) {
    const key = form.dataset.recordForm;
    const config = collections[key];
    const editMode = form.dataset.mode === "edit";
    const payload = Object.fromEntries(new FormData(form).entries());
    config.fields.filter((field) => field[2] === "check").forEach(([name]) => { payload[name] = String(form.elements[name].checked); });
    if (editMode) payload.id = form.dataset.id;
    try {
      await withBusyButton(button, editMode ? "Saving…" : "Adding…", async () => {
        await submit(editMode ? config.update : config.add, payload, `${config.label} saved`);
        closeEditor(form);
        await loadRecords();
        window.refreshPortfolio?.();
        setStatus(`${config.label[0].toUpperCase()}${config.label.slice(1)} ${editMode ? "updated" : "added"}.`);
      });
    } catch (error) { setStatus(error.message, true); }
  }

  async function deleteRecord(key, id, button) {
    if (!id || !confirm(`Delete this ${collections[key].label}? This cannot be undone.`)) return;
    try {
      await withBusyButton(button, "Deleting…", async () => {
        await submit(collections[key].remove, { id }, `${collections[key].label} deleted`);
        await loadRecords();
        window.refreshPortfolio?.();
        setStatus(`${collections[key].label[0].toUpperCase()}${collections[key].label.slice(1)} deleted.`);
      });
    } catch (error) { setStatus(error.message, true); }
  }

  function closeEditor(form) {
    const editor = form.closest(".portfolio-editor");
    editor.hidden = true;
    editor.replaceChildren();
  }

  preparePortfolioEditing();
})();
