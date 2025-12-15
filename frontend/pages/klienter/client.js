const logoutBtn = document.querySelector(".logout");
const table = document.querySelector(".client_table");
const modal = document.querySelector(".modal_overlay");
const modalNameInput = document.querySelector(".modal_name_input");
const updateBtn = document.querySelector(".update_client_btn");
const modalUserInput = document.querySelector(".modal_user_input");
const currentUserLabel = document.querySelector(".current_user_label");
const createBtn = document.querySelector("#createButton");
const createOverlay = document.querySelector(".create_modal_overlay");
const createName = document.querySelector(".create_name_input");
const createUser = document.querySelector(".create_user_input");
const createIdPrefix = document.querySelector(".create_idprefix_input");
const createSubmit = document.querySelector(".create_submit_btn");
const APIBASEURL = "/api/v1/client/";

let currentButton = null;
let currentOldName = null;

document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
        modal.classList.add("hidden");
    }
});

document.addEventListener("keydown", (event) => {
    if (event.key === "Escape") {
        createOverlay.classList.add("hidden");
    }
});

modalNameInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        event.preventDefault();
        updateBtn.click();
    }
});


modalUserInput.addEventListener("keydown", (event) => {
    if (event.key === "Enter") {
        event.preventDefault();
        updateBtn.click();
    }
});

async function fetchPost() {
    try {
        const token = getToken();

        const response = await fetch(APIBASEURL, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error("Failed to fetch clients");
        }

        const data = await response.json();
        console.log("DATA:", data);

        /* const table = document.querySelector(".client_table"); */

        data.forEach(client => {
            const row = document.createElement("tr");

            if (getRole() === "JURIST") return;

            row.innerHTML = `
                <td>${client.idPrefix}</td>
                <td>${client.name}</td>
                <td>${client.users}</td>
                <td>
                    <button class="edit_btn" 
                        data-name="${client.name}" 
                        data-user="${client.users}"
                        data-clientid="${client.idPrefix}">
                        Rediger
                    </button>
                    <button class="delete_btn" data-id="${client.id}">Slet</button>
                </td>
            `;
            table.appendChild(row);
        });

    } catch (err) {
        document.querySelector("body").innerHTML = ` 
        <div class="unauthorized">
        You are not authorized to see this window
        <br>
        Ask the administrator to give you access
        <br>
        🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻🧑🏽‍💻
        </div>
        `
        console.error("ERROR:", err);
    }
}

fetchPost();

const searchInput = document.querySelector(".search_input");

searchInput?.addEventListener("input", () => {
    const filter = searchInput.value.toLowerCase().trim();
    const rows = table.querySelectorAll("tr");

    rows.forEach((row, index) => {
        if (index === 0) return; 
        const cells = Array.from(row.querySelectorAll("td"));
        const rowText = cells.map(c => c.textContent.toLowerCase()).join(" ");

        if (rowText.includes(filter)) {
            row.style.display = "";
        } else {
            row.style.display = "none";
        }
    });
});

table.addEventListener("click", async (event) => {
    const btn = event.target.closest(".delete_btn");
    if (!btn) return;

    const id = btn.dataset.id;
    if (!id) return;

    try {
        const token = getToken();

        const response = await fetch(APIBASEURL + `delete/${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": "Bearer " + token,
            }
        });

        if (!response.ok) {
            throw new Error("Delete failed");
        }

        btn.closest("tr")?.remove();

    } catch (err) {
        console.error(err);
    }
});

table.addEventListener("click", (event) => {
    const editButton = event.target.closest(".edit_btn");
    if (!editButton) return;

    currentButton = editButton;
    currentOldName = editButton.dataset.name;

    modalNameInput.value = currentOldName;
    modalUserInput.value = editButton.dataset.user || "";


    modal.classList.remove("hidden");
});

modal.addEventListener("click", (event) => {
    if (!event.target.closest(".client_modal")) {
        modal.classList.add("hidden");
    }
});

updateBtn.addEventListener("click", async () => {
    const newName = modalNameInput.value.trim();
    const newUser = modalUserInput.value.trim();
    const clientIdPrefix = Number(currentButton.dataset.clientid);

    if (!newName) return;

    try {
        const token = getToken();
        const headers = {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        };

        if (newName !== currentOldName) {
            await fetch(APIBASEURL + "update/name", {
                method: "PATCH",
                headers,
                body: JSON.stringify({
                    oldName: currentOldName,
                    newName: newName
                })
            });

            const row = currentButton.closest("tr");
            row.children[1].textContent = newName;
            currentButton.dataset.name = newName;
        }

        if (newUser) {
            await fetch(APIBASEURL + "update/users", {
                method: "PUT",
                headers,
                body: JSON.stringify({
                    clientIdPrefix,
                    user: [newUser]
                })
            });

            const row = currentButton.closest("tr");
            row.children[2].textContent = newUser;
            currentButton.dataset.user = newUser;
        }

        modal.classList.add("hidden");

    } catch (err) {
        console.error(err);
    }
});

createBtn.addEventListener("click", () => {
    createName.value = "";
    createUser.value = "";
    createIdPrefix.value = "";
    createOverlay.classList.remove("hidden");
});

createOverlay.addEventListener("click", (e) => {
    if (!e.target.closest(".create_client_modal")) {
        createOverlay.classList.add("hidden");
    }
});

createSubmit.addEventListener("click", async () => {
    const name = createName.value.trim();
    const user = createUser.value.trim();
    const idPrefix = Number(createIdPrefix.value);

    if (!name || !user || !idPrefix) return;

    try {
        const token = getToken();

        const response = await fetch(APIBASEURL + "add", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                clientName: name,
                users: [user],
                idPrefix: idPrefix
            })
        });

        if (!response.ok) {
            console.log(response.message);
            throw new Error ("Test");
        }

        createOverlay.classList.add("hidden");
        location.reload();

    } catch (err) {
        console.error(err);
    }
});

logoutBtn?.addEventListener("click", () => {
    logout();
    location.replace("http://127.0.0.1:5500/pages/loginFrontend/index.html");
})

function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("username");
}

function getToken() {
    return localStorage.getItem("token");
}

function getUsername() {
    return localStorage.getItem("username");
}

function getRole() {
    return localStorage.getItem("role");
}

