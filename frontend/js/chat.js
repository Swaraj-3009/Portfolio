const chatUser = requireRole("user", "admin");
if (chatUser) initChat();

function initChat() {
  mountTopbar(chatUser);
  $("#role").textContent = chatUser.role === "admin" ? "Administrator" : "Signed-in user";

  const storageKey = `portfolioChats:${chatUser.name}`;
  const list = $("#conversation-list");
  const messages = $("#messages");
  const sendForm = $("#send-form");
  const messageInput = $("#message");
  let conversations = readConversations();
  let active = null;
  let refreshTimer;

  renderConversations();
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
    const conversation = {
      conversationId: Number(values.get("conversationId")),
      peer: values.get("peer").trim()
    };
    if (!conversation.conversationId || !conversation.peer || conversation.peer === chatUser.name) {
      setStatus("Enter a valid conversation ID and the other participant's username.", true);
      return;
    }
    try {
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
    active = conversation;
    renderConversations();
    renderThreadHeading();
    sendForm.hidden = false;
    messageInput.disabled = false;
    $(".chat-send-button", sendForm).disabled = false;
    messages.innerHTML = `<div class="chat-loading">Loading conversation...</div>`;
    try {
      await loadMessages();
      messageInput.focus();
      window.clearInterval(refreshTimer);
      refreshTimer = window.setInterval(() => {
        if (active && !document.hidden) loadMessages(true).catch(() => {});
      }, 7000);
    } catch (error) {
      messages.replaceChildren(emptyChatState(error.message));
      throw error;
    }
  }

  async function loadMessages(quiet = false) {
    if (!active) return;
    const rows = await chatRequest(`/chat/showChat?conversationId=${encodeURIComponent(active.conversationId)}`);
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
    const message = messageInput.value.trim();
    if (!message) return;
    const button = $(".chat-send-button", sendForm);
    try {
      await withBusyButton(button, "Sending...", async () => {
        await chatRequest("/chat/sendChat", {
          conversationId: active.conversationId,
          message,
          reciever: active.peer
        });
        messageInput.value = "";
        await loadMessages();
        messageInput.focus();
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
    if (own) {
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
    const replacement = window.prompt("Edit your message", row.message);
    if (replacement === null || !replacement.trim() || replacement.trim() === row.message) return;
    try {
      await chatRequest("/chat/editChat", {
        conversationId: active.conversationId,
        messageId: row.messageId,
        message: replacement.trim()
      });
      await loadMessages();
      setStatus("Message updated.");
    } catch (error) {
      setStatus(error.message, true);
    }
  }

  async function deleteMessage(row) {
    if (!window.confirm("Delete this message? This cannot be undone.")) return;
    try {
      await chatRequest("/chat/deleteChat", {
        conversationId: active.conversationId,
        messageId: row.messageId
      });
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
      button.innerHTML = `<span class="chat-peer-avatar small">${esc(conversation.peer.slice(0, 1).toUpperCase())}</span><span class="chat-conversation-copy"><strong>${esc(conversation.peer)}</strong><small>Conversation #${conversation.conversationId}</small></span><span class="chat-conversation-arrow" aria-hidden="true">&gt;</span>`;
      list.appendChild(button);
    });
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
