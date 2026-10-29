/* Relationship lists and counts use the existing servlet endpoints. */
function initRelationships(user) {
  const root = $("#relationships");
  if (!root) return;

  let role = user.role;
  const admin = user.role === "admin";
  const adminSections = [
    { key: "requestedFriend", title: "Friend requests", endpoint: "/relation/requestedFriend" },
    { key: "followers", title: "Followers", endpoint: "/relation/followers" },
    { key: "friends", title: "Friends", endpoint: "/relation/friends" },
    { key: "family", title: "Family", endpoint: "/relation/family" }
  ];
  let sections = [];

  root.innerHTML = `<section aria-labelledby="relationships-heading">
    <div class="row"><h3 id="relationships-heading">Community</h3></div>
    <div class="relationship-counts" id="relationship-counts"><p class="muted">Loading counts...</p></div>
    <div id="relationship-lists"></div>
  </section>`;
  const lists = $("#relationship-lists", root);
  root.addEventListener("click", handleRelationshipAction);
  loadRelationships();

  async function fetchText(path, values = {}) {
    const url = new URL(API_BASE_URL + path);
    Object.entries(values).forEach(([key, value]) => url.searchParams.set(key, value));
    let response;
    try {
      response = await fetch(url, { cache: "no-store", credentials: "include", headers: { Accept: "text/plain" } });
    } catch {
      throw new Error("Could not reach the backend. Check that it is running and allows frontend requests.");
    }
    const text = (await response.text()).trim();
    if (response.redirected && /\/(login|admin-login)\.html$/i.test(new URL(response.url).pathname)) {
      throw new Error("Your sign-in session has expired. Sign in again, then retry.");
    }
    if (!response.ok) throw new Error(plain(text) || `Request failed (${response.status}).`);
    return text;
  }

  function parseNames(text) {
    return text ? text.split(/\r?\n/).map((name) => name.trim()).filter(Boolean) : [];
  }

  async function loadRelationships() {
    if (!admin) {
      try {
        role = await fetchText("/relation/myStatus");
      } catch {
        role = user.role === "user" ? "follower" : user.role;
      }
      if (role === "followers") role = "follower";
    }
    const userSections = role === "family"
      ? [adminSections[3], adminSections[2], adminSections[1]]
      : role === "friends"
        ? [adminSections[2], adminSections[3], adminSections[1]]
        : role === "follower"
          ? [adminSections[1]] : [];
    sections = admin ? adminSections : userSections;
    const requests = sections.map(async (item) => {
      try {
        return [item.key, { names: parseNames(await fetchText(item.endpoint)) }];
      } catch (error) {
        return [item.key, { error: error.message }];
      }
    });
    const results = await Promise.all(requests);
    const listsByKey = Object.fromEntries(results);
    if (!admin) {
      Object.values(listsByKey).forEach((result) => {
        if (!result.error) {
          result.names = result.names.filter((name) => name.toLowerCase() !== user.name.toLowerCase());
        }
      });
    }
    lists.replaceChildren();
    if (!admin && role === "requestedFriends") {
      const pending = document.createElement("section");
      pending.className = "relationship-section";
      pending.appendChild(emptyMessage("Your friend request is pending admin approval."));
      const cancel = document.createElement("button");
      cancel.type = "button";
      cancel.className = "btn danger";
      cancel.dataset.relationshipAction = "cancelFriendRequest";
      cancel.textContent = "Cancel request";
      pending.appendChild(cancel);
      lists.appendChild(pending);
    }
    sections.forEach(({ key, title }) => {
      const section = document.createElement("section");
      section.className = "relationship-section";
      section.dataset.list = key;
      section.innerHTML = `<h3>${esc(title)} <span class="muted" data-list-count></span></h3><p class="relationship-empty">Loading...</p>`;
      lists.appendChild(section);
    });
    renderCounts(listsByKey);
    sections.forEach((item) => renderList(item, listsByKey[item.key]));
    if (!admin && (role === "friends" || role === "family")) renderMembershipAction(role);
    if (!admin && role === "follower") renderFriendRequestButton();
  }

  function renderCounts(listsByKey) {
    const target = $("#relationship-counts", root);
    const listCount = (key) => {
      const result = listsByKey[key];
      return result && !result.error ? result.names.length : null;
    };
    const metrics = admin
      ? [ ["Pending requests", listCount("requestedFriend")], ["Followers", listCount("followers")], ["Friends", listCount("friends")], ["Family", listCount("family")] ]
      : role === "family"
        ? [["Family", listCount("family")], ["Friends", listCount("friends")], ["Followers", listCount("followers")]]
        : role === "friends"
          ? [["Friends", listCount("friends")], ["Family", listCount("family")], ["Followers", listCount("followers")]]
          : role === "follower"
            ? [["Community", listCount("followers")]]
            : [];
    target.replaceChildren(...metrics.map(([label, value]) => {
      const metric = document.createElement("div");
      metric.className = "relationship-count";
      const count = value;
      metric.innerHTML = `<strong>${Number.isFinite(count) ? count : "—"}</strong><span>${esc(label)}</span>`;
      return metric;
    }));
  }

  function renderList(item, result) {
    const target = lists.querySelector(`[data-list="${item.key}"]`);
    if (!target) return;
    target.replaceChildren();
    const heading = document.createElement("h3");
    heading.textContent = item.title;
    if (!result || result.error) {
      target.append(heading, emptyMessage(result?.error || "Could not load this list."));
      return;
    }
    const count = document.createElement("span");
    count.className = "muted";
    count.textContent = ` (${result.names.length})`;
    heading.appendChild(count);
    target.appendChild(heading);
    if (!result.names.length) {
      target.appendChild(emptyMessage(item.key === "requestedFriend" ? "There are no pending friend requests." : `No ${item.title.toLowerCase()} to show.`));
      return;
    }
    const list = document.createElement("ul");
    list.className = "relationship-list";
    result.names.forEach((name) => {
      const row = document.createElement("li");
      const username = document.createElement("span");
      username.textContent = name;
      row.appendChild(username);
      const actions = admin && item.key === "requestedFriend"
        ? [["acceptFriend", "Accept"], ["cancelFriendRequest", "Cancel request"]]
        : admin && item.key === "friends"
          ? [["addFamily", "Promote to family"], ["demoteFriend", "Demote to follower"]]
          : admin && item.key === "family"
            ? [["demoteFamily", "Demote to friend"]]
            : [];
      actions.forEach(([action, label]) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = `btn${action === "acceptFriend" || action === "addFamily" ? " primary" : action === "cancelFriendRequest" || action === "demoteFamily" || action === "demoteFriend" ? " danger" : ""}`;
        button.dataset.relationshipAction = action;
        button.dataset.username = name;
        button.textContent = label;
        row.appendChild(button);
      });
      if (actions.length > 1) {
        const rowActions = document.createElement("div");
        rowActions.className = "btns";
        rowActions.append(...row.querySelectorAll("button"));
        row.appendChild(rowActions);
      }
      list.appendChild(row);
    });
    target.appendChild(list);
  }

  function renderMembershipAction(currentRole) {
    const actions = document.createElement("div");
    actions.className = "btns relationship-request";
    const button = document.createElement("button");
    button.type = "button";
    button.className = currentRole === "family" ? "btn danger" : "btn";
    button.dataset.relationshipAction = currentRole === "family" ? "demoteFamily" : "demoteFriend";
    button.textContent = currentRole === "family" ? "Leave family" : "Remove friend status";
    actions.appendChild(button);
    lists.appendChild(actions);
  }

  function emptyMessage(text) {
    const message = document.createElement("p");
    message.className = "relationship-empty";
    message.textContent = text;
    return message;
  }

  function renderFriendRequestButton() {
    const actions = document.createElement("div");
    actions.className = "btns relationship-request";
    const button = document.createElement("button");
    button.type = "button";
    button.className = "btn primary";
    button.dataset.requestFriend = "true";
    button.textContent = "Send friend request";
    actions.appendChild(button);
    lists.appendChild(actions);
  }

  async function handleRelationshipAction(event) {
    const requestButton = event.target.closest("[data-request-friend]");
    if (requestButton && root.contains(requestButton)) {
      try {
        await withBusyButton(requestButton, "Sending...", async () => {
          await submit("/relation/requestFriend", {}, "Friend Request Sent");
        });
        setStatus("Friend request sent to the admin.");
        await loadRelationships();
      } catch (error) {
        setStatus(error.message, true);
      }
      return;
    }
    const button = event.target.closest("[data-relationship-action]");
    if (!button || !root.contains(button)) return;
    const action = button.dataset.relationshipAction;
    const username = button.dataset.username;
    try {
      const labels = { acceptFriend: "Accepting...", addFamily: "Promoting...", demoteFamily: "Demoting...", demoteFriend: "Demoting...", cancelFriendRequest: "Cancelling..." };
      await withBusyButton(button, labels[action] || "Saving...", async () => {
        if (action === "acceptFriend" || action === "addFamily" || (admin && action === "demoteFriend")) {
          await submit(`/relation/${action}`, { username }, "Relationship updated");
        } else {
          await submit(`/relation/${action}`, username ? { username } : {}, "Relationship updated");
        }
      });
      const messages = {
        acceptFriend: `${username} is now a friend.`,
        addFamily: `${username} is now family.`,
        demoteFamily: username ? `${username} is now a friend.` : "You left the family group.",
        demoteFriend: username ? `${username} is now a follower.` : "You removed your friend status and are now a follower.",
        cancelFriendRequest: username ? `${username}'s request was cancelled.` : "Your friend request was cancelled.",
      };
      setStatus(messages[action] || "Relationship updated.");
      await loadRelationships();
    } catch (error) {
      setStatus(error.message, true);
    }
  }
}
