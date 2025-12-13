import {postObjectAsJson, fetchAnyUrl} from "/components/modules/modulejson.js";

const apiBase = "http://localhost:8080/api/v1";

let firstLog, firstLogElement, logsListContainer;
let allLogs = []; // Store all logs for filtering/sorting
let allUsers = []; // Store unique users for filter dropdown

document.addEventListener("DOMContentLoaded", () => {
    logsListContainer = document.querySelector(".logsListContainer");
    
    // Search functionality
    document.querySelector(".searchArea").addEventListener("input", (e) => {
        filterAndDisplayLogs(e.target.value);
    });
    
    // Filter by user
    document.querySelector(".filterUsers").addEventListener("change", (e) => {
        const searchValue = document.querySelector(".searchArea").value;
        filterAndDisplayLogs(searchValue, e.target.value);
    });
    
    // Filter by method
    document.querySelector(".filterMethods").addEventListener("change", (e) => {
        const searchValue = document.querySelector(".searchArea").value;
        const userFilter = document.querySelector(".filterUsers").value;
        filterAndDisplayLogs(searchValue, userFilter, e.target.value);
    });
    
    // Filter by date
    document.querySelector(".filterDay").addEventListener("change", (e) => {
        sortLogs(e.target.value);
    });
    
    getLogs();
});

async function getLogs(){
    const token = localStorage.getItem("token");
    if(!token){
        console.error("No JWT Token found!");
        return;
    }
    const apiUrl = apiBase + "/admin/getalllogs";

    firstLog = null;
    firstLogElement = null;

    try{
        allLogs = await fetchAnyUrl(apiUrl, true);
        
        // Populate filter dropdowns
        populateFilterDropdowns();
        
        // Display all logs
        displayLogs(allLogs);
        
    } catch(error){
        console.log(error);
    }
}

function populateFilterDropdowns() {
    // Get unique users
    const users = new Set();
    const methods = new Set();
    
    allLogs.forEach(log => {
        users.add(log.actor);
        methods.add(log.action);
    });
    
    // Populate users dropdown
    const filterUsers = document.querySelector(".filterUsers");
    filterUsers.innerHTML = '<option>Alle Brugere</option>';
    [...users].sort().forEach(user => {
        const option = document.createElement("option");
        option.value = user;
        option.textContent = user;
        filterUsers.appendChild(option);
    });
    
    // Populate methods dropdown
    const filterMethods = document.querySelector(".filterMethods");
    filterMethods.innerHTML = '<option>Alle Metoder</option>';
    [...methods].sort().forEach(method => {
        const option = document.createElement("option");
        option.value = method;
        option.textContent = method;
        filterMethods.appendChild(option);
    });
}

function displayLogs(logs) {
    logsListContainer.innerHTML = "";
    
    let isFirst = true;
    
    logs.forEach((log, index) => {
        const singleLog = document.createElement("div");
        singleLog.className = "singleLog";

        // Store reference to first log
        if (isFirst) {
            firstLog = log;
            firstLogElement = singleLog;
            isFirst = false;
        }

        const timeAndId = document.createElement("div");
        timeAndId.className = "timeAndId";

        const logTimeStamp = document.createElement("div");
        logTimeStamp.textContent = formatTimestamp(log.timestamp);
        logTimeStamp.className = "logTimestamp";

        const logId = document.createElement("div");
        logId.textContent = "ID: " + log.id;
        logId.className = "logId";

        timeAndId.appendChild(logTimeStamp);
        timeAndId.appendChild(logId);

        const logUser = document.createElement("div");
        logUser.textContent = log.actor;
        logUser.className = "logUser";

        const logAction = document.createElement("div");
        logAction.textContent = log.details;
        logAction.className = "logAction";

        singleLog.appendChild(timeAndId);
        singleLog.appendChild(logUser);
        singleLog.appendChild(logAction);

        singleLog.addEventListener("click", () => {
            showLogDetails(log);
            highlightSelectedLog(singleLog);
        });

        logsListContainer.appendChild(singleLog);
    });

    // Show first log details by default
    if (firstLog && firstLogElement) {
        showLogDetails(firstLog);
        highlightSelectedLog(firstLogElement);
    }

    calculateSizeOfLogs(logs.length);
}

//har vibe codet de her sorterings og filter metoder
function filterAndDisplayLogs(searchTerm = "", userFilter = "", methodFilter = "") {
    let filteredLogs = allLogs;
    
    // Filter by search term (searches in actor, action, details)
    if (searchTerm) {
        const lowerSearch = searchTerm.toLowerCase();
        filteredLogs = filteredLogs.filter(log => 
            log.actor.toLowerCase().includes(lowerSearch) ||
            log.action.toLowerCase().includes(lowerSearch) ||
            log.details.toLowerCase().includes(lowerSearch) ||
            log.id.toString().includes(lowerSearch)
        );
    }
    
    // Filter by user
    if (userFilter && userFilter !== "Alle Brugere") {
        filteredLogs = filteredLogs.filter(log => 
            log.actor === userFilter
        );
    }
    
    // Filter by method
    if (methodFilter && methodFilter !== "Alle Metoder") {
        filteredLogs = filteredLogs.filter(log => 
            log.action === methodFilter
        );
    }
    
    displayLogs(filteredLogs);
}


function sortLogs(sortOption) {
    let sortedLogs = [...allLogs];
    
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);
    
    const lastWeekStart = new Date(today);
    lastWeekStart.setDate(lastWeekStart.getDate() - 7);
    
    const lastMonthStart = new Date(today);
    lastMonthStart.setDate(lastMonthStart.getDate() - 30);
    
    switch(sortOption) {
        case "I dag":
            sortedLogs = sortedLogs.filter(log => {
                const logDate = new Date(log.timestamp);
                return logDate >= today;
            });
            break;
        case "I går":
            sortedLogs = sortedLogs.filter(log => {
                const logDate = new Date(log.timestamp);
                return logDate >= yesterday && logDate < today;
            });
            break;
        case "Sidste uge":
            sortedLogs = sortedLogs.filter(log => {
                const logDate = new Date(log.timestamp);
                // All logs from the last 7 days (including today)
                return logDate >= lastWeekStart;
            });
            break;
        case "Sidste måned":
            sortedLogs = sortedLogs.filter(log => {
                const logDate = new Date(log.timestamp);
                // All logs from the last 30 days (including today)
                return logDate >= lastMonthStart;
            });
            break;
        case "Alle":
            // Show all logs
            break;
        default:
            // Default - show all
            break;
    }
    
    // Always sort by newest first
    sortedLogs.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
    
    const searchValue = document.querySelector(".searchArea").value;
    const userFilter = document.querySelector(".filterUsers").value;
    const methodFilter = document.querySelector(".filterMethods").value;
    
    // Apply other filters on top of date filter
    let finalLogs = sortedLogs;
    
    // Filter by search term
    if (searchValue) {
        const lowerSearch = searchValue.toLowerCase();
        finalLogs = finalLogs.filter(log => 
            log.actor.toLowerCase().includes(lowerSearch) ||
            log.action.toLowerCase().includes(lowerSearch) ||
            log.details.toLowerCase().includes(lowerSearch) ||
            log.id.toString().includes(lowerSearch)
        );
    }
    
    // Filter by user
    if (userFilter && userFilter !== "Alle Brugere") {
        finalLogs = finalLogs.filter(log => 
            log.actor === userFilter
        );
    }
    
    // Filter by method
    if (methodFilter && methodFilter !== "Alle Metoder") {
        finalLogs = finalLogs.filter(log => 
            log.action === methodFilter
        );
    }
    
    displayLogs(finalLogs);
}

async function showLogDetails(log){
    try {
        const urlFindUser = apiBase + "/user/singleUser/" + log.actor;
        const user = await fetchAnyUrl(urlFindUser, true);

        const detailsLogId = document.querySelector(".detailsLogId");
        detailsLogId.textContent = log.id;

        const detailsTime = document.querySelector(".detailsTime");
        detailsTime.textContent = formatDetailedTimestamp(log.timestamp);

        const detailsUserName = document.querySelector(".detailsUserName");
        detailsUserName.textContent = user.name;

        const detailsUserMail = document.querySelector(".detailsUserMail");
        detailsUserMail.textContent = user.email;

        const detailsUserId = document.querySelector(".detailsUserId");
        detailsUserId.textContent = "Bruger ID: " + user.userId;

        const detailsUserRole = document.querySelector(".detailsUserRole");
        detailsUserRole.textContent = "Role: " + user.role.roleName;

        const detailsMethod = document.querySelector(".detailsMethod");
        detailsMethod.textContent = log.action;

        const detailsDescription = document.querySelector(".detailsDescription");
        detailsDescription.textContent = log.details;

    } catch(error){
        console.log(error);
    }
}

function calculateSizeOfLogs(filteredCount){
    const logsCounter = document.querySelector(".logsCounter");
    logsCounter.textContent = `Viser ${filteredCount} af ${allLogs.length} logs`; 
}

function highlightSelectedLog(selectedElement) {
    const allLogElements = document.querySelectorAll('.singleLog');
    allLogElements.forEach(log => {
        log.classList.remove('selected');
    });
    selectedElement.classList.add('selected');
}

function formatDetailedTimestamp(timestamp) {
    const date = new Date(timestamp);
    const months = [
        'januar', 'februar', 'marts', 'april', 'maj', 'juni',
        'juli', 'august', 'september', 'oktober', 'november', 'december'
    ];
    
    const day = date.getDate();
    const month = months[date.getMonth()];
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${day}. ${month} ${year}, kl. ${hours}:${minutes}:${seconds}`;
}

function formatTimestamp(timestamp) {
    const date = new Date(timestamp);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;
}