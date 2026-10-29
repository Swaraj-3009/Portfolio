const chatUser = requireRole("user", "admin");
if (chatUser) {
  const chatLayout = $(".chat-layout");
  chatLayout.style.display = "none";
  if (chatUser.role === "admin") {
    chatLayout.style.removeProperty("display");
    initChat();
  } else {
    fetch(`${API_BASE_URL}/relation/myStatus`, { credentials: "include", cache: "no-store" })
      .then(async (response) => {
        const role = (await response.text()).trim();
        if (!response.ok) throw new Error(plain(role) || `Request failed (${response.status}).`);
        if (role !== "friends" && role !== "family") {
          setStatus("Chat is available only to friends and family.", true);
          return;
        }
        chatLayout.style.removeProperty("display");
        initChat();
      })
      .catch((error) => setStatus(error.message, true));
  }
}

function initChat() {
  mountTopbar(chatUser);
  const adminMode = chatUser.role === "admin";
  $("#role").textContent = adminMode ? "Administrator" : "Signed-in user";
  $(".chat-presence").lastChild.textContent = adminMode ? " Refreshes every 15 sec" : " Refreshes every 7 sec";
  if (adminMode) {
    $(".chat-page-heading p").textContent = "Review conversations and moderate messages.";
    $(".chat-list-heading h3").textContent = "All conversations";
    $("#new-chat-form").hidden = true;
    $("#join-chat-form").hidden = true;
    $(".chat-local-note").textContent = "Admin access: all conversations are listed here.";
  }

  const storageKey = `portfolioChats:${chatUser.name}`;
  const list = $("#conversation-list");
  const messages = $("#messages");
  const sendForm = $("#send-form");
  const messageInput = $("#message");
  let conversations = adminMode ? [] : readConversations();
  let active = null;
  let refreshTimer;
  let adminConversationSignature = null;
  let messageLoadSequence = 0;
  let adminListLoadSequence = 0;

  if (adminMode) {
    $("#conversation-list").innerHTML = `<p class="chat-list-empty">Loading conversations...</p>`;
    loadAdminConversations();
    window.setInterval(() => {
      if (!document.hidden) loadAdminConversations();
    }, 15000);
  } else {
    renderConversations();
  }
  $("#new-chat-form").addEventListener("submit", startConversation);
  $("#join-chat-form").addEventListener("submit", joinConversation);
  list.addEventListener("click", (event) => {
    const button = event.target.closest("[data-conversation-id]");
    if (button) openConversation(conversations.find((item) => item.conversationId === Number(button.dataset.conversationId))).catch((error) => setStatus(error.message, true));
  });
  sendForm.addEventListener("submit", sendMessage);
  messageInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter" && !event.shiftKey) {
      event.preventDefault();
      sendForm.requestSubmit();
    }
  });

  async function startConversation(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const recipient = new FormData(form).get("recipient").trim();
    if (!recipient || recipient === chatUser.name) {
      setStatus("Choose a different username.", true);
      return;
    }
    const button = form.querySelector("button[type=submit]");
    try {
      await withBusyButton(button, "...", async () => {
        const result = await chatRequest("/chat/createChat", { reciever: recipient });
        const conversation = { conversationId: Number(result.conversationId), peer: recipient };
        rememberConversation(conversation);
        form.reset();
        renderConversations();
        await openConversation(conversation);
        setStatus(`Conversation with ${recipient} is ready.`);
      });
    } catch (error) {
      setStatus(error.message, true);
    }
  }

  async function joinConversation(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const values = new FormData(form);
    const conversationId = Number(values.get("conversationId"));
    if (!Number.isInteger(conversationId) || conversationId <= 0) {
      setStatus("Enter a valid conversation ID.", true);
      return;
    }
    try {
      const conversation = await chatRequest(`/chat/conversation?conversationId=${encodeURIComponent(conversationId)}`);
      if (!conversation || !conversation.peer) throw new Error("The server returned an invalid conversation.");
      await openConversation(conversation);
      rememberConversation(conversation);
      renderConversations();
      form.reset();
    } catch (error) {
      setStatus(error.message, true);
    }
  }

  async function openConversation(conversation) {
    if (!conversation) return;
    window.clearInterval(refreshTimer);
    active = conversation;
    renderConversations();
    renderThreadHeading();
    sendForm.hidden = adminMode;
    messageInput.disabled = adminMode;
    $(".chat-send-button", sendForm).disabled = adminMode;
    messages.innerHTML = `<div class="chat-loading">Loading conversation...</div>`;
    try {
      await loadMessages();
      if (active?.conversationId !== conversation.conversationId) return;
        if (!adminMode) messageInput.focus();
      refreshTimer = window.setInterval(() => {
        if (active && !document.hidden) loadMessages(true).catch(() => {});
      }, 7000);
    } catch (error) {
      if (active?.conversationId === conversation.conversationId) {
        messages.replaceChildren(emptyChatState(error.message));
      }
      throw error;
    }
  }

  async function loadMessages(quiet = false) {
    if (!active) return;
    const requestedConversationId = active.conversationId;
    const requestSequence = ++messageLoadSequence;
    const endpoint = adminMode ? "/admin/chat/messages" : "/chat/showChat";
    const rows = await chatRequest(`${endpoint}?conversationId=${encodeURIComponent(requestedConversationId)}`);
    if (!active || active.conversationId !== requestedConversationId || requestSequence !== messageLoadSequence) return;
    if (!Array.isArray(rows)) throw new Error("The server returned an invalid message list.");
    const wasAtBottom = messages.scrollHeight - messages.scrollTop - messages.clientHeight < 80;
    const oldSignature = messages.dataset.signature;
    const signature = rows.map((row) => `${row.messageId}:${row.message}`).join("|");
    if (quiet && oldSignature === signature) return;
    messages.replaceChildren(...rows.map(renderMessage));
    messages.dataset.signature = signature;
    if (wasAtBottom || !quiet) messages.scrollTop = messages.scrollHeight;
  }

  async function sendMessage(event) {
    event.preventDefault();
    if (!active) return;
    const conversation = active;
    const message = messageInput.value.trim();
    if (!message) return;
    const button = $(".chat-send-button", sendForm);
    try {
      await withBusyButton(button, "Sending...", async () => {
        await chatRequest("/chat/sendChat", {
          conversationId: conversation.conversationId,
          message,
          reciever: conversation.peer
        });
        if (active?.conversationId === conversation.conversationId) {
          if (messageInput.value.trim() === message) messageInput.value = "";
          await loadMessages();
          messageInput.focus();
        }
      });
    } catch (error) {
      setStatus(error.message, true);
    }
  }

  function renderMessage(row) {
    const own = row.sender === chatUser.name;
    const article = document.createElement("article");
    article.className = `chat-message${own ? " own" : ""}`;
    article.dataset.messageId = row.messageId;
    const meta = document.createElement("div");
    meta.className = "chat-message-meta";
    const sender = document.createElement("strong");
    sender.textContent = own ? "You" : row.sender;
    const time = document.createElement("time");
    time.dateTime = row.time || "";
    time.textContent = formatChatTime(row.time);
    meta.append(sender, time);
    const body = document.createElement("p");
    body.className = "chat-message-body";
    body.textContent = row.message;
    article.append(meta, body);
    if (own || adminMode) {
      const actions = document.createElement("div");
      actions.className = "chat-message-actions";
      const edit = document.createElement("button");
      edit.type = "button";
      edit.textContent = "Edit";
      edit.addEventListener("click", () => editMessage(row));
      const remove = document.createElement("button");
      remove.type = "button";
      remove.textContent = "Delete";
      remove.addEventListener("click", () => deleteMessage(row));
      actions.append(edit, remove);
      article.appendChild(actions);
    }
    return article;
  }

  async function editMessage(row) {
    if (!active) return;
    const conversation = active;
    const replacement = window.prompt("Edit your message", row.message);
    if (replacement === null || !replacement.trim() || replacement.trim() === row.message) return;
    try {
      await chatRequest(adminMode ? "/admin/chat/edit" : "/chat/editChat", {
        conversationId: conversation.conversationId,
        messageId: row.messageId,
        message: replacement.trim()
      });
      if (active?.conversationId !== conversation.conversationId) return;
      await loadMessages();
      setStatus("Message updated.");
    } catch (error) {
      setStatus(error.message, true);
    }
  }

  async function deleteMessage(row) {
    if (!active) return;
    const conversation = active;
    if (!window.confirm("Delete this message? This cannot be undone.")) return;
    try {
      await chatRequest(adminMode ? "/admin/chat/delete" : "/chat/deleteChat", {
        conversationId: conversation.conversationId,
        messageId: row.messageId
      });
      if (active?.conversationId !== conversation.conversationId) return;
      await loadMessages();
      setStatus("Message deleted.");
    } catch (error) {
      setStatus(error.message, true);
    }
  }

  function renderThreadHeading() {
    const initials = active.peer.slice(0, 1).toUpperCase();
    $("#thread-heading").innerHTML = `<div class="chat-peer-avatar">${esc(initials)}</div><div class="chat-peer-details"><h3>${esc(active.peer)}</h3><p class="muted">Conversation <span class="chat-id">#${active.conversationId}</span></p></div><button class="chat-copy-id" type="button" title="Copy conversation ID" aria-label="Copy conversation ID">Copy ID</button>`;
    $(".chat-copy-id").addEventListener("click", async () => {
      try {
        await navigator.clipboard.writeText(String(active.conversationId));
        setStatus("Conversation ID copied.");
      } catch {
        setStatus(`Conversation ID: ${active.conversationId}`);
      }
    });
  }

  function renderConversations() {
    $("#conversation-count").textContent = String(conversations.length).padStart(2, "0");
    list.replaceChildren();
    if (!conversations.length) {
      const empty = document.createElement("p");
      empty.className = "chat-list-empty";
      empty.textContent = "No recent conversations yet.";
      list.appendChild(empty);
      return;
    }
    conversations.forEach((conversation) => {
      const button = document.createElement("button");
      button.type = "button";
      button.className = `chat-conversation${active?.conversationId === conversation.conversationId ? " selected" : ""}`;
      button.dataset.conversationId = conversation.conversationId;
      const label = conversation.peer || `${conversation.user1} / ${conversation.user2}`;
      button.innerHTML = `<span class="chat-peer-avatar small">${esc(label.slice(0, 1).toUpperCase())}</span><span class="chat-conversation-copy"><strong>${esc(label)}</strong><small>Conversation #${conversation.conversationId}</small></span><span class="chat-conversation-arrow" aria-hidden="true">&gt;</span>`;
      list.appendChild(button);
    });
  }

  async function loadAdminConversations() {
    const requestSequence = ++adminListLoadSequence;
    try {
      const rows = await chatRequest("/admin/chat/conversations");
      if (requestSequence !== adminListLoadSequence) return;
      if (!Array.isArray(rows)) throw new Error("The server returned an invalid conversation list.");
      const nextConversations = rows.map((row) => ({
        ...row,
        peer: `${row.user1} / ${row.user2}`
      }));
      const signature = nextConversations.map((row) => `${row.conversationId}:${row.user1}:${row.user2}`).join("|");
      if (signature === adminConversationSignature) return;
      adminConversationSignature = signature;
      conversations = nextConversations;
      renderConversations();
    } catch (error) {
      if (requestSequence !== adminListLoadSequence) return;
      if (!conversations.length) {
        const empty = document.createElement("p");
        empty.className = "chat-list-empty";
        empty.textContent = error.message;
        list.replaceChildren(empty);
      }
      setStatus(error.message, true);
    }
  }

  function rememberConversation(conversation) {
    conversations = [conversation, ...conversations.filter((item) => item.conversationId !== conversation.conversationId)].slice(0, 30);
    localStorage.setItem(storageKey, JSON.stringify(conversations));
  }

  function readConversations() {
    try {
      const saved = JSON.parse(localStorage.getItem(storageKey)) || [];
      return Array.isArray(saved) ? saved.filter((item) => Number.isInteger(item.conversationId) && item.conversationId > 0 && typeof item.peer === "string") : [];
    } catch {
      return [];
    }
  }

  function emptyChatState(text) {
    const state = document.createElement("div");
    state.className = "chat-empty-state";
    state.textContent = text;
    return state;
  }
}

async function chatRequest(path, values) {
  let response;
  try {
    const isGet = values === undefined;
    const url = new URL(API_BASE_URL + path);
    const options = { credentials: "include", cache: "no-store", headers: { Accept: "application/json" } };
    if (!isGet) {
      options.method = "POST";
      options.headers["Content-Type"] = "application/x-www-form-urlencoded;charset=UTF-8";
      options.body = new URLSearchParams(values);
    }
    response = await fetch(url, options);
  } catch {
    throw new Error("Could not reach the backend. Check that it is running and allows frontend requests.");
  }
  const text = (await response.text()).trim();
  if (response.redirected && /\/(login|admin-login)\.html$/i.test(new URL(response.url).pathname)) {
    throw new Error("Your sign-in session has expired. Sign in again, then retry.");
  }
  if (!response.ok) throw new Error(plain(text) || `Request failed (${response.status}).`);
  if (response.status === 204 || !text) return null;
  try {
    return JSON.parse(text);
  } catch {
    throw new Error("The server returned an unreadable response.");
  }
}

function formatChatTime(value) {
  if (!value) return "";
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? value : new Intl.DateTimeFormat(undefined, { dateStyle: "medium", timeStyle: "short" }).format(date);
}
