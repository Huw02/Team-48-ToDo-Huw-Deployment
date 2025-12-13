import {postObjectAsJson, fetchAnyUrl} from "/components/modules/modulejson.js";

const apiBase = "http://localhost:8080/api/v1/user";

let usersListContainer, createUserPanel, updateUserPanel;
let selectedUserId = null;
let allUsers = []; // Store all users for filtering/sorting

document.addEventListener("DOMContentLoaded", () => {
    usersListContainer = document.querySelector(".usersListContainer");
    createUserPanel = document.getElementById("createUserPanel");
    updateUserPanel = document.getElementById("updateUserPanel");

    document.querySelector(".createUserBtn").addEventListener("click", showCreatePanel);

    document.getElementById("cancelUpdateBtn").addEventListener("click", () => {
        showCreatePanel();
        clearUpdateForm();
    });

    document.getElementById("createUserSubmitBtn").addEventListener("click", createUser);
    document.getElementById("updateUserSubmitBtn").addEventListener("click", updateUser);

    // Search functionality
    document.querySelector(".searchUser").addEventListener("input", (e) => {
        filterAndDisplayUsers(e.target.value);
    });
    
    // Filter by role
    document.querySelector(".filterRoles").addEventListener("change", (e) => {
        const searchValue = document.querySelector(".searchUser").value;
        filterAndDisplayUsers(searchValue, e.target.value);
    });
    
    // Sort by date
    document.querySelector(".filterCreated").addEventListener("change", (e) => {
        sortUsers(e.target.value);
    });

    loadUsers();
});

async function loadUsers(){
    const token = localStorage.getItem("token");
    if(!token){
        console.error("no token in local storage");
        return;
    }

    try {
        allUsers = await fetchAnyUrl(apiBase, true);
        displayUsers(allUsers);
    } catch(error){
        console.error("error loading user: ", error);
    }
}

function displayUsers(users) {
    usersListContainer.innerHTML = "";
    
    users.forEach((user) => {
        const userRow = createUserRow(user);
        usersListContainer.appendChild(userRow);
    });
    
    updateUsersCounter(users.length);
}

function filterAndDisplayUsers(searchTerm = "", roleFilter = "") {
    let filteredUsers = allUsers;
    
    // Filter by search term (name, username, or email)
    if (searchTerm) {
        const lowerSearch = searchTerm.toLowerCase();
        filteredUsers = filteredUsers.filter(user => 
            user.name.toLowerCase().includes(lowerSearch) ||
            user.username.toLowerCase().includes(lowerSearch) ||
            user.email.toLowerCase().includes(lowerSearch)
        );
    }
    
    // Filter by role
    if (roleFilter && roleFilter !== "Alle roller") {
        filteredUsers = filteredUsers.filter(user => 
            user.role.roleName === roleFilter
        );
    }
    
    displayUsers(filteredUsers);
}

function sortUsers(sortOption) {
    let sortedUsers = [...allUsers];
    
    switch(sortOption) {
        case "Nyeste først":
            sortedUsers.sort((a, b) => new Date(b.createdDate) - new Date(a.createdDate));
            break;
        case "Ældste først":
            sortedUsers.sort((a, b) => new Date(a.createdDate) - new Date(b.createdDate));
            break;
        default:
            // Default sorting (as returned from backend)
            break;
    }
    
    allUsers = sortedUsers;
    const searchValue = document.querySelector(".searchUser").value;
    const roleFilter = document.querySelector(".filterRoles").value;
    filterAndDisplayUsers(searchValue, roleFilter);
}

async function checkUsernameAvailability(username, excludeUserId = null) {
    // Client-side check using existing users
    const existingUser = allUsers.find(u => u.username === username);
    
    if (!existingUser) {
        return true; // Username is available
    }
    
    // If updating, check if username belongs to current user
    if (excludeUserId && existingUser.userId === excludeUserId) {
        return true; // Username belongs to current user
    }
    
    return false; // Username is taken
}

function createUserRow(user){
    const singleUser = document.createElement("div");
    singleUser.className= "singleUser";

    const nameCell = document.createElement("div");
    nameCell.className = "userNameCell";

    const userName = document.createElement("div");
    userName.className = "userName";
    userName.textContent = user.name;

    const userUserName = document.createElement("div");
    userUserName.className = "userUsername";
    userUserName.textContent = user.username;

    nameCell.appendChild(userName);
    nameCell.appendChild(userUserName);

    const userEmail = document.createElement("div");
    userEmail.className="userEmail";
    userEmail.textContent = user.email;

    const userRole = document.createElement("div");
    userRole.className = "userRole";
    const roleClass = user.role.roleName.toLowerCase().replace("_", "");
    userRole.classList.add(roleClass);
    userRole.textContent = user.role.roleName;

    const userCreated = document.createElement("div");
    userCreated.className = "userCreated";
    userCreated.textContent = formatDate(user.createdDate);

    const userActions = document.createElement("div");
    userActions.className="userActions";

    const deleteBtn = document.createElement("button");
    deleteBtn.className = "actionBtn deleteBtn";
    deleteBtn.addEventListener("click", (e) => {
        e.stopPropagation();
        deleteUser(user);
    });

    userActions.appendChild(deleteBtn);

    singleUser.appendChild(nameCell);
    singleUser.appendChild(userEmail);
    singleUser.appendChild(userRole);
    singleUser.appendChild(userCreated);
    singleUser.appendChild(userActions);

    singleUser.addEventListener("click", () => {
        showUpdatePanel(user);
        highlightSelectedUser(singleUser);
    });
    
    return singleUser;
}

function showCreatePanel(){
    createUserPanel.classList.remove("hidden");
    updateUserPanel.classList.add("hidden");
    clearCreateForm();
    clearUserSelection();
}

function showUpdatePanel(user){
    createUserPanel.classList.add("hidden");
    updateUserPanel.classList.remove("hidden");
    
    selectedUserId = user.userId;

    document.getElementById("update_username").value = user.username;
    document.getElementById("update_name").value = user.name;
    document.getElementById("update_email").value = user.email;
    document.getElementById("update_password").value = "";
    document.getElementById("update_role").value = user.role.id;
}

function highlightSelectedUser(selectedElement){
    const allUsers = document.querySelectorAll(".singleUser");
    allUsers.forEach(user => {
        user.classList.remove("selected");
    });
    selectedElement.classList.add("selected");
}

function clearUserSelection(){
    const allUsers = document.querySelectorAll(".singleUser");
    allUsers.forEach(user => {
        user.classList.remove("selected");
    });
    selectedUserId = null;
}

async function createUser(){
    const token = localStorage.getItem("token");
    if(!token){
        console.error("no token in localstorage");
        return;
    }

    const username = document.getElementById("create_username").value.trim();
    const name = document.getElementById("create_name").value.trim();
    const email = document.getElementById("create_email").value.trim();
    const password = document.getElementById("create_password").value;
    const roleId = document.getElementById("create_role").value;

    if (!username || !name || !email || !password || !roleId) {
        alert("Udfyld venligst alle felter");
        return;
    }

    // Check username availability
    const isAvailable = await checkUsernameAvailability(username);
    if (!isAvailable) {
        alert("Brugernavnet er allerede i brug. Vælg venligst et andet.");
        return;
    }

    const userData = {
        username: username,
        name: name,
        email: email,
        password: password,
        roleId: roleId
    };

    try{
        const response = await postObjectAsJson(apiBase, userData, "POST", true);
        if(response.status === 200){
            alert("Bruger oprettet!");
            clearCreateForm();
            loadUsers();
        } else {
            console.log("response var: " + response.status);
        }
    } catch(error){
        console.error("error creating user: ", error);
        alert("Fejl ved oprettelse af bruger");
    }
}

async function updateUser(){
    const token = localStorage.getItem("token");
    if(!token){
        console.log("no token found in local storage");
        return;
    }
    if(!selectedUserId){
        alert("ingen bruger valgt");
        return;
    }

    const username = document.getElementById("update_username").value.trim();
    const name = document.getElementById("update_name").value.trim();
    const email = document.getElementById("update_email").value.trim();
    const password = document.getElementById("update_password").value;
    const roleId = document.getElementById("update_role").value;

    if (!username || !name || !email || !roleId) {
        alert("Udfyld venligst alle felter");
        return;
    }

    // Check username availability (excluding current user)
    const isAvailable = await checkUsernameAvailability(username, selectedUserId);
    if (!isAvailable) {
        alert("Brugernavnet er allerede i brug. Vælg venligst et andet.");
        return;
    }

    const userData = {
        username: username,
        name: name,
        email: email,
        roleId: roleId
    };

    if(password){
        userData.password = password;
    }

    const apiUrl = apiBase + "/" + selectedUserId;

    try{
        const response = await postObjectAsJson(apiUrl, userData, "PUT", true);
        if(response.status === 200){
            alert("Bruger opdateret!");
            clearUpdateForm();
            loadUsers();
            showCreatePanel();
        } else {
            console.log("response var: " + response.status);
        }
    } catch(error){
        console.error("Error updating user: ", error);
        alert("Fejl ved opdatering af bruger");
    }
}

async function deleteUser(user){
    if(!confirm("Er du sikker på at du vil slette denne bruger?")){
        return;
    }

    const token = localStorage.getItem("token");
    if(!token){
        console.error("no jwt token in local storage");
        return;
    }

    const apiUrl = apiBase + "/" + user.userId;

    try{
        const response = await postObjectAsJson(apiUrl, user, "DELETE", true);
        if(response.status === 200){
            alert("Bruger er blevet slettet");
            loadUsers();
            if(selectedUserId === user.userId){
                showCreatePanel();
            }
        } else {
            console.log("response status var: ", response);
        }
    } catch(error){
        console.error("Error deleting user, error: ", error);
        alert("Fejl ved sletning af bruger");
    }
}

function clearCreateForm(){
    document.getElementById("create_username").value = "";
    document.getElementById("create_name").value = "";
    document.getElementById("create_email").value = "";
    document.getElementById("create_password").value = "";
    document.getElementById("create_role").value = "";
}

function clearUpdateForm() {
    document.getElementById("update_username").value = "";
    document.getElementById("update_name").value = "";
    document.getElementById("update_email").value = "";
    document.getElementById("update_password").value = "";
    document.getElementById("update_role").value = "";
    selectedUserId = null;
}

function updateUsersCounter(count) {
    const usersCounter = document.querySelector(".usersCounter");
    usersCounter.textContent = `Viser ${count} af ${allUsers.length} brugere`;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}/${month}/${year}`;
}