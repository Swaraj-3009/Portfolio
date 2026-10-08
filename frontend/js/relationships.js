/* Relationship lists and counts use the existing servlet endpoints. */
function initRelationships(user) {
  const root = $("#relationships");
  if (!root) return;

  let role = user.role === "user" ? "follower" : user.role;
  const admin = role === "admin";
  const adminSections = [
        { key: "requestedFriend", title: "Friend requests", endpoint: "/relation/requestedFriend", action: "acceptFriend", actionLabel: "Accept" },
        { key: "followers", title: "Followers", endpoint: "/relation/followers" },
        { key: "friends", title: "Friends", endpoint: "/relation/friends", action: "addFamily", actionLabel: "Promote to family" },
        { key: "family", title: "Family", endpoint: "/relation/family" }
      ];
  const userSections = [
    { key: "family", title: "Family", endpoint: "/relation/family" },
    { key: "friends", title: "Friends", endpoint: "/relation/friends" },
    { key: "followers", title: "Followers", endpoint: "/relation/followers" }
  ];
  let sections = admin ? adminSections : userSections;

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
    const queriedSections = admin ? adminSections : userSections;
    const requests = queriedSections.map(async (item) => {
      try {
        return [item.key, { names: parseNames(await fetchText(item.endpoint)) }];
      } catch (error) {
        return [item.key, { error: error.message }];
      }
    });
    const countsRequest = fetchText("/relation").then((text) => text.split(/\r?\n/).map((value) => value.trim()).filter(Boolean).map(Number));
    const [results, counts] = await Promise.all([Promise.all(requests), countsRequest.then((value) => value, () => null)]);
    const listsByKey = Object.fromEntries(results);
    if (!admin) {
      sections = userSections.filter((item) => !listsByKey[item.key]?.error);
      role = listsByKey.family && !listsByKey.family.error ? "family"
        : listsByKey.friends && !listsByKey.friends.error ? "friends" : "follower";
      if (!sections.length) sections = [userSections[2]];
    }
    lists.replaceChildren();
    sections.forEach(({ key, title }) => {
      const section = document.createElement("section");
      section.className = "relationship-section";
      section.dataset.list = key;
      section.innerHTML = `<h3>${esc(title)} <span class="muted" data-list-count></span></h3><p class="relationship-empty">Loading...</p>`;
      lists.appendChild(section);
    });
    renderCounts(counts, listsByKey);
    sections.forEach((item) => renderList(item, listsByKey[item.key]));
    if (!admin && role === "follower") renderFriendRequestButton();
  }

  function renderCounts(counts, listsByKey) {
    const target = $("#relationship-counts", root);
    const values = counts && counts.length >= 2 ? { followers: counts[0], friends: counts[1], family: counts[2] } : null;
    const metrics = admin
      ? [ ["Pending requests", listsByKey.requestedFriend], ["Followers", values?.followers], ["Friends", values?.friends], ["Family", values?.family] ]
      : role === "family"
        ? [["Family", values?.family], ["Friends", values?.friends], ["Followers", values?.followers]]
        : [["Friends", values?.friends], ["Followers", values?.followers]];
    target.replaceChildren(...metrics.map(([label, value]) => {
      const metric = document.createElement("div");
      metric.className = "relationship-count";
      const count = typeof value === "number" ? value : value?.names?.length;
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
      if (admin && item.action) {
        const button = document.createElement("button");
        button.type = "button";
        button.className = `btn${item.action === "acceptFriend" ? " primary" : ""}`;
        button.dataset.relationshipAction = item.action;
        button.dataset.username = name;
        button.textContent = item.actionLabel;
        row.appendChild(button);
      }
      list.appendChild(row);
    });
    target.appendChild(list);
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
    const alreadyRequested = localStorage.getItem(`friendRequestSent:${user.name}`) === "true";
    button.disabled = alreadyRequested;
    button.textContent = alreadyRequested ? "Request sent" : "Send friend request";
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
        localStorage.setItem(`friendRequestSent:${user.name}`, "true");
        requestButton.textContent = "Request sent";
        requestButton.disabled = true;
        setStatus("Friend request sent to the admin.");
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
      await withBusyButton(button, action === "acceptFriend" ? "Accepting..." : "Promoting...", async () => {
        await fetchText(`/relation/${action}`, { username });
      });
      setStatus(action === "acceptFriend" ? `${username} is now a friend.` : `${username} is now family.`);
      await loadRelationships();
    } catch (error) {
      setStatus(error.message, true);
    }
  }
}
