function initAdminUsers() {
  if (!requireRole("admin")) return;
  const root = $("#admin-users");
  if (!root) return;
  let users = [];
  root.addEventListener("click", handleAction);
  loadUsers();

  async function loadUsers() {
    root.innerHTML = `<p class="muted">Loading user accounts...</p>`;
    try {
      const response = await fetch(`${API_BASE_URL}/admin/users`, {
        credentials: "include",
        cache: "no-store",
        headers: { Accept: "application/json" }
      });
      const text = (await response.text()).trim();
      if (response.redirected && /\/(login|admin-login)\.html$/i.test(new URL(response.url).pathname)) {
        throw new Error("Your sign-in session has expired. Sign in again.");
      }
      if (!response.ok) throw new Error(plain(text) || `Could not load accounts (${response.status}).`);
      users = JSON.parse(text);
      if (!Array.isArray(users)) throw new Error("The server returned an invalid account list.");
      renderUsers();
    } catch (error) {
      root.replaceChildren(userMessage(error.message, true));
    }
  }

  function renderUsers() {
    if (!users.length) {
      root.replaceChildren(userMessage("There are no user accounts."));
      return;
    }
    const list = document.createElement("ul");
    list.className = "relationship-list admin-user-list";
    users.forEach((user) => {
      const row = document.createElement("li");
      const details = document.createElement("span");
      details.className = "admin-user-details";
      const username = document.createElement("strong");
      username.textContent = user.username;
      const metadata = document.createElement("small");
      metadata.textContent = `${user.email} · ${user.role || "user"}`;
      details.append(username, metadata);
      const remove = document.createElement("button");
      remove.type = "button";
      remove.className = "btn danger";
      remove.dataset.deleteUsername = user.username;
      remove.textContent = "Remove account";
      row.append(details, remove);
      list.appendChild(row);
    });
    root.replaceChildren(list);
  }

  async function handleAction(event) {
    const button = event.target.closest("[data-delete-username]");
    if (!button || !root.contains(button)) return;
    const username = button.dataset.deleteUsername;
    if (!window.confirm(`Permanently remove the account “${username}”? Its relationship membership, conversations, and messages will also be deleted.`)) return;

    try {
      await withBusyButton(button, "Removing...", async () => {
        const response = await fetch(`${API_BASE_URL}/admin/users/delete`, {
          method: "POST",
          credentials: "include",
          cache: "no-store",
          headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
          body: new URLSearchParams({ username })
        });
        const text = (await response.text()).trim();
        if (response.redirected && /\/(login|admin-login)\.html$/i.test(new URL(response.url).pathname)) {
          throw new Error("Your sign-in session has expired. Sign in again.");
        }
        if (!response.ok) throw new Error(plain(text) || `Could not remove account (${response.status}).`);
      });
      setStatus(`Removed the account ${username} and its chat and relationship data.`);
      users = users.filter((user) => user.username !== username);
      renderUsers();
    } catch (error) {
      setStatus(error.message, true);
    }
  }

  function userMessage(message, error = false) {
    const paragraph = document.createElement("p");
    paragraph.className = error ? "error-text" : "muted";
    paragraph.textContent = message;
    return paragraph;
  }
}
