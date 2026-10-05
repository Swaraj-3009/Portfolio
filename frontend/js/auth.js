/* login.html, register.html and admin-login.html all use this file */
const page = document.body.dataset.page;
const form = $("#auth-form"), submitBtn = $("#submit");
const CFG = {
  login:         { path: "/user/login",  ok: "User logged in successfully", role: "user",  next: "user-dashboard.html" },
  "admin-login": { path: "/admin/login", ok: "Admin Logged in",             role: "admin", next: "admin-dashboard.html" }
};

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  const v = Object.fromEntries(new FormData(form).entries());
  v.username = v.username.trim();
  setStatus("Contacting the server...");
  submitBtn.disabled = true;
  try {
    if (page === "register") {
      v.email = v.email.trim(); v.role = "user";
      const { res, text } = await request("/user/register", v);
      if (res.status !== 201) throw new Error(plain(text) || `Registration failed (${res.status}).`);
      localStorage.setItem("email:" + v.username, v.email);   // remembered for the profile page
      setStatus("Account created. Redirecting to login...");
      setTimeout(() => (location.href = "login.html"), 900);
    } else {
      const c = CFG[page];
      const { res, text } = await request(c.path, { username: v.username, password: v.password });
      if (!res.ok || !(res.redirected || text.includes(c.ok))) throw new Error(plain(text) || "Invalid username or password.");
      setUser({ name: v.username, role: c.role });
      location.href = c.next;
    }
  } catch (err) {
    setStatus(err.message, true);
  } finally {
    submitBtn.disabled = false;
  }
});
