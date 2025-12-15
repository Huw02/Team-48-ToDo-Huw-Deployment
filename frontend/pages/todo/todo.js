import { postObjectAsJson, fetchAnyUrl } from "/components/modules/modulejson.js";

const API_BASE_URL = "/api/v1";

const titleEl = document.querySelector("#todoTitle");
const numberEl = document.querySelector("#todoNumber");
const createdEl = document.querySelector("#todoCreated");
const statusEl = document.querySelector("#todoStatus");
const startEl = document.querySelector("#todoStartDate");
const endEl = document.querySelector("#todoEndDate");
const descEl = document.querySelector("#todoDescriptionText");
const employeesListEl = document.querySelector("#todoEmployees");
const selfAssignBtn = document.querySelector(".employees-self-btn");
const editTodoBtn = document.querySelector("#editTodoBtn");
const editTodoModal = document.querySelector("#editTodoModal");
const editTodoForm = document.querySelector("#editTodoForm");

const addEmployeeBtn = document.querySelector(".employees-add-btn");
const addEmployeeModal = document.querySelector("#addEmployeeModal");
const addEmployeeForm = document.querySelector("#addEmployeeForm");
const employeeCheckboxList = document.querySelector("#employeeCheckboxList");
const cancelAddEmployeeBtn = document.querySelector("#cancelAddEmployee");
const editName = document.querySelector("#editTodoName");
const editDescription = document.querySelector("#editTodoDescription");
const editStart = document.querySelector("#editTodoStartDate");
const editEnd = document.querySelector("#editTodoEndDate");
const editPriority = document.querySelector("#editTodoPriority");
const editStatus = document.querySelector("#editTodoStatus");
const editArchived = document.querySelector("#editArchived");
const cancelEditTodoBtn = document.querySelector("#cancelEditTodo");

let currentTodo = null;
let caseAssignees = [];

const STATUS_LABELS = {
    NOT_STARTED: "Ikke startet",
    IN_PROGRESS: "I gang",
    DONE: "Færdig"
};

function getToken() {
    return localStorage.getItem("token");
}


document.addEventListener("DOMContentLoaded", async () => {
    const token = getToken();
    if (!token) {
        window.location.href = "/pages/login/index.html";
        return;
    }

    const params = new URLSearchParams(window.location.search);
    const todoId = params.get("id");

    if (!todoId) {
        titleEl.textContent = "Opgave ikke fundet";
        return;
    }

    await loadTodo(todoId);

    await loadCaseAssignees(todoId);
});

async function loadTodo(todoId) {
    
        let todo = await fetchAnyUrl(`${API_BASE_URL}/todos/${todoId}`, true)

        

        console.log("TODO DETAIL:", todo);
        currentTodo = todo;

        titleEl.textContent = todo.name ?? "Uden navn";
        numberEl.textContent = `#${todo.id ?? ""}`;

        createdEl.textContent = todo.created
            ? todo.created.split("T")[0]
            : "-";

        statusEl.textContent = STATUS_LABELS[todo.status];

        startEl.textContent = todo.startDate ?? "-";
        endEl.textContent = todo.endDate ?? "-";

        descEl.textContent = todo.description || "Ingen beskrivelse.";

        renderEmployees(todo.toDoAssignees || []);

    
}

function renderEmployees(assignees) {
    employeesListEl.innerHTML = "";

    const list = assignees || [];

    if (list.length === 0) {
        const li = document.createElement("li");
        li.textContent = "Ingen medarbejdere tilknyttet";
        employeesListEl.appendChild(li);
        return;
    }

    list.forEach(user => {
        const li = document.createElement("li");
        li.classList.add("employee-item");
        li.innerHTML = `
            <span class="employee-dot"></span>
            ${user.name ?? user.username ?? "Ukendt bruger"}
        `;
        employeesListEl.appendChild(li);
    });
}

async function loadCaseAssignees(todoId) {
        caseAssignees = await fetchAnyUrl(`${API_BASE_URL}/todos/${todoId}/case-assignees`, true);
        console.log("CASE ASSIGNEES:", caseAssignees);

    
}

addEmployeeBtn?.addEventListener("click", () => {
    if (!currentTodo || !caseAssignees) return;

    const existingIds = new Set(
        (currentTodo.toDoAssignees || []).map(u => u.userId)
    );

    employeeCheckboxList.innerHTML = "";

    caseAssignees.forEach(user => {
        const id = user.userId;

        const label = document.createElement("label");
        label.classList.add("employee-checkbox-item");

        const checkbox = document.createElement("input");
        checkbox.type = "checkbox";
        checkbox.value = id;

        if (existingIds.has(id)) {
            checkbox.checked = true;
        }

        const span = document.createElement("span");
        span.textContent = user.name ?? user.username ?? `Bruger #${id}`;

        label.appendChild(checkbox);
        label.appendChild(span);
        employeeCheckboxList.appendChild(label);
    });

    addEmployeeModal.classList.remove("hidden");
});

cancelAddEmployeeBtn?.addEventListener("click", () => {
    addEmployeeModal.classList.add("hidden");
});

addEmployeeModal?.addEventListener("click", (e) => {
    if (e.target === addEmployeeModal) {
        addEmployeeModal.classList.add("hidden");
    }
});

addEmployeeForm?.addEventListener("submit", async (e) => {
    e.preventDefault();
    if (!currentTodo) return;

    const selectedIds = Array.from(
        employeeCheckboxList.querySelectorAll("input[type='checkbox']:checked")
    ).map(cb => Number(cb.value));

    

        const res = await postObjectAsJson(`${API_BASE_URL}/todos/${currentTodo.id}/assignees`, { userIds: selectedIds }, "PATCH", true, false)

        if (res.status === 401) {
            window.location.href = "/pages/login/index.html";
            return;
        }

        if (!res.ok) {
            console.error("Kunne ikke opdatere medarbejdere:", res.status);
            alert("Kunne ikke opdatere medarbejdere.");
            return;
        }

        const updatedTodo = await res.json();
        currentTodo = updatedTodo;

        renderEmployees(updatedTodo.toDoAssignees || []);

        addEmployeeModal.classList.add("hidden");

    
});

editTodoBtn?.addEventListener("click", () => {
    if (!currentTodo) return;

    editName.value = currentTodo.name || "";
    editDescription.value = currentTodo.description || "";
    editStart.value = currentTodo.startDate || "";
    editEnd.value = currentTodo.endDate || "";
    editPriority.value = currentTodo.priority || "LOW";
    editStatus.value = currentTodo.status || "NOT_STARTED";
    editArchived.checked = currentTodo.archived === true;

    editTodoModal.classList.remove("hidden");
});

cancelEditTodoBtn?.addEventListener("click", () => {
    editTodoModal.classList.add("hidden");
});

editTodoModal?.addEventListener("click", e => {
    if (e.target === editTodoModal) {
        editTodoModal.classList.add("hidden");
    }
});

editTodoForm?.addEventListener("submit", async (e) => {
    e.preventDefault();

    if (!currentTodo) return;

    const payload = {
        name: editName.value,
        description: editDescription.value,
        startDate: editStart.value || null,
        endDate: editEnd.value || null,
        toDoAssignees: currentTodo.toDoAssignees,
        priority: editPriority.value,
        status: editStatus.value,
        archived: editArchived.checked
    };

        const res = await postObjectAsJson(`${API_BASE_URL}/todos/${currentTodo.id}`, payload, "PUT", true, false)

        if (!res.ok) {
            alert("Kunne ikke opdatere opgaven.");
            return;
        } 

        const updated = await res.json();
        currentTodo = updated;

        titleEl.textContent = updated.name;
        statusEl.textContent = updated.status;
        startEl.textContent = updated.startDate || "-";
        endEl.textContent = updated.endDate || "-";
        descEl.textContent = updated.description || "Ingen beskrivelse.";

        editTodoModal.classList.add("hidden");
        
        location.reload();
     
});