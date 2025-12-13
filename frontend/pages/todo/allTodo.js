import { postObjectAsJson, fetchAnyUrl } from "/components/modules/modulejson.js";


const tableBody = document.querySelector("#todoTableBody");
const allTasksTab = document.querySelector("#allTasksTab");
const myTasksTab = document.querySelector("#myTasksTab");
const searchInput = document.querySelector(".search-input");

const API_BASE_URL = "/api/v1";

let allTodos = [];
let myTodos = [];
let currentTodos = [];
let casses = [];
let sortDirection = {};

const createBtn = document.querySelector(".create-btn");
const createTodoModal = document.querySelector("#createTodoModal");
const createTodoForm = document.querySelector("#createTodoForm");
const cancelCreateTodoBtn = document.querySelector("#cancelCreateTodo");

const nameInput = document.querySelector("#todoName");
const descInput = document.querySelector("#todoDescription");
const caseSelect = document.querySelector("#todoCase");
const startDateInput = document.querySelector("#todoStartDate");
const endDateInput = document.querySelector("#todoEndDate");
const prioritySelect = document.querySelector("#todoPriority");

const STATUS_LABELS = {
    NOT_STARTED: "Ikke startet",
    IN_PROGRESS: "I gang",
    DONE: "Færdig"
};

const PRIORITY_LABEL = {
    HIGH: "Høj",
    MEDIUM: "Mellem",
    LOW: "Lav"
}

document.addEventListener("DOMContentLoaded", async () => {

    switchTab(allTasksTab);
    loadTodos(`${API_BASE_URL}/todos`, "all");
    loadCases(`${API_BASE_URL}/cases`);
});

// Tabs
allTasksTab.addEventListener("click", () => {
    switchTab(allTasksTab);
    loadTodos(`${API_BASE_URL}/todos`, "all");
});

myTasksTab.addEventListener("click", () => {
    switchTab(myTasksTab);
    loadTodos(`${API_BASE_URL}/todos/assigned`, "mine");
});

document.querySelectorAll("th[data-sort]").forEach(th => {
    th.style.cursor = "pointer";
    th.addEventListener("click", () => {
        const column = th.dataset.sort;
        sortTodos(column);
    });
});


function getToken() {
    return localStorage.getItem("token");
}


function switchTab(activeTab) {
    document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
    activeTab.classList.add("active");
}

function renderTodos(todos) {
    tableBody.innerHTML = "";

    if (!todos || todos.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='8'>Ingen opgaver fundet.</td></tr>";
        return;
    }

    todos.forEach(todo => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>#${todo.id ?? "N/A"}</td>
            <td>${todo.name}</td>
            <td>${todo.created?.split("T")[0] ?? ""}</td>
            <td>${todo.description ?? ""}</td>
            <td>${todo.startDate ?? ""}</td>
            <td>${todo.endDate ?? ""}</td>
            <td>${STATUS_LABELS[todo.status] ?? ""}</td>
            <td>${PRIORITY_LABEL[todo.priority] ?? ""}</td>
        `;

        const deleteCell = document.createElement("td");
        deleteCell.classList.add("todo-delete-cell");
        deleteCell.innerHTML = `<button class="delete-btn">Fjern</button>`;

        deleteCell.querySelector(".delete-btn").addEventListener("click", async (e) => {
            e.stopPropagation();

            const confirmDelete = confirm(`Vil du slette opgaven "${todo.name}"?`);
            if (!confirmDelete) return;

            await removeTodo(todo.id);
        });

        row.appendChild(deleteCell);

        row.addEventListener("click", () => {
            window.location.href = `/pages/todo/todo.html?id=${todo.id}`;
        });

        tableBody.appendChild(row);
    });
}

async function loadTodos(url, mode) {
    tableBody.innerHTML = "<tr><td colspan='7'>Henter data...</td></tr>";


        const todos = await fetchAnyUrl(url, true)
        console.log("TODOS:", mode, todos);

        if (mode === "all") {
            allTodos = todos;
        } else if (mode === "mine") {
            myTodos = todos;
        }

        currentTodos = todos;

        renderTodos(currentTodos);

    
}

async function removeTodo(todoId) {
    try {
        const res = await postObjectAsJson(`${API_BASE_URL}/todos/${todoId}`, {}, "DELETE", true, false);

        if (res.status === 401) {
            window.location.href = "/pages/login/login.html";
            return;
        }

        if (!res.ok) {
            console.error("Kunne ikke slette todo:", res.status, res.statusText);
            alert("Kunne ikke slette opgaven.");
            return;
        }

        currentTodos = currentTodos.filter(t => t.id !== todoId);
        renderTodos(currentTodos);

    } catch (err) {
        console.error("Fejl ved sletning af todo:", err);
        alert("Der opstod en fejl ved sletning.");
    }
}

async function loadCases(url) {
        return casses = await fetchAnyUrl(url, true);
}

function fillCaseDropdown() {
    caseSelect.innerHTML = `<option value="">Vælg sag</option>`;

    casses.forEach(c => {
        const opt = document.createElement("option");
        opt.value = c.id;
        opt.textContent = c.name;
        caseSelect.appendChild(opt);
    });
}

function sortTodos(column) {
    if (!currentTodos) return;

    sortDirection[column] = sortDirection[column] === "asc" ? "desc" : "asc";
    const direction = sortDirection[column];

    const sorted = [...currentTodos].sort((a, b) => {
        let valA = a[column];
        let valB = b[column];

        if (valA == null) valA = "";
        if (valB == null) valB = "";

        if (column.toLowerCase().includes("date") || column === "created") {
            return direction === "asc"
                ? new Date(valA) - new Date(valB)
                : new Date(valB) - new Date(valA);
        }

        if (typeof valA === "number" && typeof valB === "number") {
            return direction === "asc" ? valA - valB : valB - valA;
        }

        return direction === "asc"
            ? valA.toString().localeCompare(valB.toString())
            : valB.toString().localeCompare(valA.toString());
    });

    currentTodos = sorted;
    renderTodos(currentTodos);
}

createBtn.addEventListener("click", async () => {
    fillCaseDropdown();
    createTodoModal.classList.remove("hidden");
});

cancelCreateTodoBtn.addEventListener("click", () => {
    createTodoModal.classList.add("hidden");
    createTodoForm.reset();
});

createTodoModal.addEventListener("click", (e) => {
    if (e.target === createTodoModal) {
        createTodoModal.classList.add("hidden");
        createTodoForm.reset();
    }
});

createTodoForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const name = nameInput.value.trim();
    const description = descInput.value.trim();
    const caseId = caseSelect.value;
    const startDate = startDateInput.value || null;
    const endDate = endDateInput.value || null;
    const priority = prioritySelect.value;

    if (!name || !priority) {
        alert("Navn og prioritet er obligatoriske.");
        return;
    }

    const payload = {
        name,
        description,
        caseId,
        startDate,
        endDate,
        priority
    };

    try {
        const response = await postObjectAsJson(`${API_BASE_URL}/todos`, payload, "POST", true, false)
        console.log(response);

        if (response.status !== 201) {
            console.error("Fejl ved oprettelse:", response.status);
            alert("Kunne ikke oprette opgave.");
            return;
        }

        const newTodo = await response.json();
        console.log("NY TODO:", newTodo);

        createTodoModal.classList.add("hidden");
        createTodoForm.reset();

        switchTab(allTasksTab);
        await loadTodos(`${API_BASE_URL}/todos`, "all");

    } catch (err) {
        console.error("Fejl ved POST /todos:", err);
        alert("Der opstod en fejl ved oprettelse af opgave.");
    }
});

function checkAuth() {
    if (!getToken()) {
        window.location.href = "/pages/login/login.html";
    }
}
window.addEventListener('load', checkAuth);
window.addEventListener('pageshow', checkAuth);

searchInput.addEventListener("keydown", async (e) => {
    if (e.key !== "Enter") return;

    e.preventDefault();

    const query = searchInput.value.trim();

    // Hvis søgning er tom → vis standard-listen igen afhængigt af valgt tab
    if (query === "") {
        if (allTasksTab.classList.contains("active")) {
            await loadTodos(`${API_BASE_URL}/todos`, "all");
        } else if (myTasksTab.classList.contains("active")) {
            await loadTodos(`${API_BASE_URL}/todos/assigned`, "mine");
        }
        return;
    }

    // Ellers → hent todos baseret på søgekriteriet (navn/username/mail)
    await loadTodos(
        `${API_BASE_URL}/todos/search-by-assignee?q=${encodeURIComponent(query)}`,
        "search"
    );
});

searchInput.addEventListener("input", async () => {
    const value = searchInput.value.trim();

    if (value === "") {
        if (allTasksTab.classList.contains("active")) {
            await loadTodos(`${API_BASE_URL}/todos`, "all");
        } else if (myTasksTab.classList.contains("active")) {
            await loadTodos(`${API_BASE_URL}/todos/assigned`, "mine");
        }
    }
});