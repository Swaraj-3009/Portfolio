/* user-dashboard.html: users and guests */
const user = requireRole("user", "guest");
if (user) init();

function init() {
  mountTopbar(user);
  $("#hello").textContent = `Welcome, ${user.name.split(" ")[0]}`;
  $("#role").textContent = user.role === "guest" ? "Guest view" : "Signed-in user";
  user.role === "guest" ? renderGuest() : renderAccount();
  renderDeveloper();
}

function renderGuest() {
  $("#account").innerHTML = `<h3>My Profile</h3><p class="muted">You are browsing as a guest, so there is no account to edit. Log in or register to create a profile you can edit.</p>
    <div class="btns guest-actions"><a class="btn primary" href="login.html">Sign in</a><a class="btn" href="register.html">Create account</a></div>`;
}

/* My Profile: view mode, with an Edit button that switches to a form */
function renderAccount() {
  const box = $("#account"); let edit = false;
  const emailKey = () => "email:" + user.name;
  const draw = () => {
    const email = localStorage.getItem(emailKey()) || "";
    if (!edit) {
      box.innerHTML = `<div class="row"><h3>My Profile</h3><button class="btn" id="edit" type="button">Edit profile</button></div>
        <dl class="kv"><dt>Username</dt><dd>${esc(user.name)}</dd><dt>Email</dt><dd>${esc(email) || "—"}</dd><dt>Password</dt><dd>••••••••</dd></dl>`;
      $("#edit").onclick = () => { edit = true; setStatus(""); draw(); };
      return;
    }
    box.innerHTML = `<h3 class="section-heading">Edit profile</h3>
      <form id="f" class="stack-form">
        <label>Username<input name="username" value="${esc(user.name)}" required></label>
        <label>Email<input name="email" type="email" value="${esc(email)}"></label>
        <label>New password (optional)<input name="newPassword" type="password" autocomplete="new-password"></label>
        <label>Current password (needed to save)<input name="password" type="password" autocomplete="current-password" required></label>
        <div class="btns"><button class="btn primary" type="submit">Save changes</button><button class="btn" type="button" id="cancel">Cancel</button></div>
      </form>`;
    $("#cancel").onclick = () => { edit = false; draw(); };
    $("#f").onsubmit = async (e) => {
      e.preventDefault();
      const v = Object.fromEntries(new FormData(e.target).entries()), done = [];
      try {
        await withBusyButton(e.submitter, "Saving changes…", async () => {
          e.target.setAttribute("aria-busy", "true");
          try {
            if (v.username.trim() !== user.name) {
              await submit("/user/updateUsername", { username: v.username.trim(), password: v.password }, "ok");
              if (email) localStorage.setItem("email:" + v.username.trim(), email);
              user.name = v.username.trim(); setUser(user); mountTopbar(user); done.push("username");
            }
            if (v.email && v.email !== email) {
              await submit("/user/updateUserEmail", { email: v.email, password: v.password }, "ok");
              localStorage.setItem(emailKey(), v.email); done.push("email");
            }
            if (v.newPassword) {
              await submit("/user/updateUserPassword", { currentPassword: v.password, newPassword: v.newPassword }, "ok");
              done.push("password");
            }
            edit = false; draw();
            setStatus(done.length ? `Updated ${done.join(", ")}.` : "No changes to save.");
          } finally {
            e.target.removeAttribute("aria-busy");
          }
        });
      } catch (err) {
        setStatus(done.length ? `Saved ${done.join(", ")}, but: ${err.message}` : err.message, true);
      }
    };
  };
  draw();
}

async function renderDeveloper() {
  const box = $("#dev");
  try {
    const p = await fetchProfile();
    const img = p.image ? safeUrl(new URL(p.image, API_BASE_URL).href) : "";
    box.innerHTML = `<div class="row"><h3>Developer profile</h3><a class="btn" href="portfolio.html">View full portfolio</a></div>${img ? `<img class="avatar" src="${esc(img)}" alt="Photo of ${esc(p.name)}">` : ""}${profileRows(p)}`;
  } catch {
    box.innerHTML = `<div class="row"><h3>Developer profile</h3><a class="btn" href="portfolio.html">View full portfolio</a></div><p class="muted">The profile could not be loaded. Check that the backend is running.</p>`;
  }
}
