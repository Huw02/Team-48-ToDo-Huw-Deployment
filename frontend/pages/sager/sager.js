import { postObjectAsJson, fetchAnyUrl } from "/components/modules/modulejson.js";

const apiBase = "/api/v1/cases";

/* develop url */
/* deployURL */


let caseData;
let clientData;
let juristData;



async function main() {
    const tableBody = document.querySelector(".tableBody");

    const createButton = document.getElementById("createButton");
    
    if (createButton) {
        createButton.addEventListener('click', showCreateCase);
    } else {
        console.warn("Warning: Could not find element with ID 'createButton'.");
    }
    

    if (!tableBody) {
        console.error("Error: Could not find element with class '.tableBody'. Check HTML structure.");
        return; 
    }

    caseData = await fetchAnyUrl(apiBase, true);
    
    if (caseData && (Array.isArray(caseData) || (caseData.content && Array.isArray(caseData.content)))) {
        console.log("Fetched data:", caseData);
        buildTable(tableBody); 
    } else {
        console.log("No data or invalid data returned");
    }

    clientData = await fetchAnyUrl("/api/v1/client/", true);
    
    if (clientData && (Array.isArray(clientData) || (clientData.content && Array.isArray(clientData.content)))) {
        console.log("Fetched clients:", clientData);
    } else {
        console.log("No data or invalid data returned");
    }

    juristData = await fetchAnyUrl("/api/v1/user/jurists", true);

    if (juristData && (Array.isArray(juristData) || (juristData.content && Array.isArray(juristData.content)))) {
        console.log("Fetched jurists:", juristData);
    } else {
        console.log("No jurists or invalid data returned");
    }
    
}


function buildTable(tableBody) {
    
    tableBody.innerHTML = '';
    
    const dataToIterate = Array.isArray(caseData) ? caseData : caseData.content;
    
    if (!dataToIterate || dataToIterate.length === 0) {
        console.log("No cases to display.");
        return;
    }

    dataToIterate.forEach((caseItem) => {
        let newRow = document.createElement('tr');
        
        // --- 1. Sagsnummer (Nummer) ---
        let tdNummer = document.createElement('td');
        tdNummer.innerHTML = caseItem.idPrefix || 'N/A'; 
        newRow.appendChild(tdNummer);

        // --- 2. Sagsnavn (Navn) ---
        let tdNavn = document.createElement('td');
        tdNavn.innerHTML = caseItem.name || 'Untitled Case'; 
        newRow.appendChild(tdNavn); 

        // --- 3. Dato (Dato) ---
        let tdDato = document.createElement('td');
        
        // **START: DATOFORMATERING TIL DANSK**
        if (caseItem.created) {
            try {
                const date = new Date(caseItem.created);
                
                // Indstillinger for dato: "8. december 2025"
                const dateOptions = { 
                    year: 'numeric', 
                    month: 'long', 
                    day: 'numeric' // Giver tallet efterfulgt af punktum (f.eks. "8.")
                };
                
                // Indstillinger for tid: "12:59"
                const timeOptions = { 
                    hour: '2-digit', 
                    minute: '2-digit',
                    hour12: false // Sikrer 24-timers format
                };
                
                // Formater dato og tid ved hjælp af 'da-DK' (Dansk)
                const formattedDate = date.toLocaleDateString('da-DK', dateOptions); 
                const formattedTime = date.toLocaleTimeString('da-DK', timeOptions); 
                
                // Sæt den fulde streng sammen: "8. december 2025, 12:59"
                tdDato.innerHTML = `${formattedDate}, ${formattedTime}`; 
                
            } catch (e) {
                // Fejlhåndtering
                tdDato.innerHTML = 'Ugyldigt datoformat';
            }
        } else {
            // Hvis 'created' feltet mangler
            tdDato.innerHTML = 'Ukendt dato'; 
        }
        // **SLUT: DATOFORMATERING**
        
        newRow.appendChild(tdDato);

        // --- 4. Handlinger (Actions) ---
        let tdActions = document.createElement('td');
        tdActions.classList.add('actions');
        tdActions.innerHTML = `
            <button class="edit-btn">Rediger</button>
            <button class="delete-btn">Slet</button>
        `;

        tdActions.querySelector('.edit-btn').addEventListener('click', () => {
            showUpdateCase(caseItem.id);
        })
        
        tdActions.querySelector('.delete-btn').addEventListener('click', () => {
            deleteCase(caseItem.id);
        })

        newRow.appendChild(tdActions);

        tableBody.appendChild(newRow);
    });
}



function checkAuth() {
    if (!getToken()) {
        window.location.href = "/pages/login/login.html";
    }
}

function getToken() {
    return localStorage.getItem("token");
}

function showCreateCase() {

    const clientsToIterate = Array.isArray(clientData) ? clientData : (clientData?.content || []);
    const clientOptionsHtml = clientsToIterate.map(client => {
        return `<option value="${client.id}">${client.name}</option>`;
    }).join(''); 

    const clientSelectHtml = `
        <select id="clientId" name="clientId">
            <option value="" disabled selected>Vælg en klient...</option>
            ${clientOptionsHtml}
        </select>
    `;

    const juristsToIterate = Array.isArray(juristData) ? juristData : (juristData?.content || []);
    
    const juristCheckboxesHtml = juristsToIterate.map(jurist => {
        return `
            <div class="checkbox-row" style="display: flex; gap: 10px; margin-bottom: 5px;">
                <input type="checkbox" id="jurist_${jurist.userId}" name="juristIds" value="${jurist.userId}">
                <label for="jurist_${jurist.userId}">${jurist.username || jurist.name}</label>
            </div>
        `;
    }).join('');

    

    let overlayHtml = `
        <div class="editFrame">
            <form id="createCaseForm">

                <label for="name">Sagsnavn</label>
                <input type="text" id="name" name="name">

                <label for="clientId">Klient</label>
                <div id="clientInputPlaceholder">
                    <input type="text" id="clientId" name="clientId"> 
                </div>

                <label for="responsibleUsername">Sagsansvarlig</label>
                <input type="text" id="responsibleUsername" name="responsibleUsername" placeholder="Brugernavn">

                <label for="idPrefix">ID</label>
                <input type="text" id="idPrefix" name="idPrefix" placeholder="f.eks. 9573">

                <label>Medarbejdere</label>
                <div class="employeeBox" style="max-height: 150px; overflow-y: auto; padding: 10px;">
                    ${juristCheckboxesHtml} 
                </div>

                <button type="submit" class="inverted">Gem</button>
            </form>
        </div>
    `;

    // To cleanly replace the <input>, we'll use string manipulation
    // The most reliable way is to find the specific input line and replace it, 
    // or wrap the input and replace the wrapper content (as shown below).
    // For this demonstration, I'll use a direct replacement of the target line within the template.
    // NOTE: We need to recreate the overlay element after replacing the HTML string.
    
    // 3. Replace the input with the generated select element
    overlayHtml = overlayHtml.replace(
        '<input type="text" id="clientId" name="clientId">',
        clientSelectHtml
    );

    const overlay = document.createElement("div");
    overlay.classList.add("editFrameBackground");
    overlay.innerHTML = overlayHtml;

    document.body.appendChild(overlay);

    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
            removeOverlay();
        }
    });

    document.addEventListener('keydown', handleEscapeKey);

    


    document.getElementById("createCaseForm").addEventListener("submit", (e) => {
        e.preventDefault();
        submitCaseCreation();
    });

    

}

async function showUpdateCase(id) {
    let caseAssignees = await fetchAnyUrl(`${apiBase}/${id}/assignees`, true);
    
    console.log(caseAssignees)
    


    
    

    const caseAssigneesHtml = caseAssignees.map(jurist => {
    
        return `
            <div class="checkbox-row" style="display: flex; align-items: center; gap: 10px; margin-bottom: 5px;">
                <input type="checkbox" id="jurist_${jurist.userId}" name="juristIds" value="${jurist.userId}" checked> 
                <label for="jurist_${jurist.userId}" style="margin: 0; color: black;">${jurist.name}</label>
            </div>
        `;
    }).join('');
const assignedArray = Array.isArray(caseAssignees) ? caseAssignees : (caseAssignees?.content || []);
const assignedJuristIds = new Set(assignedArray.map(a => a.userId));
    // 2. Normalize All Jurists (using 'userId')
const juristsToIterate = Array.isArray(juristData) ? juristData : (juristData?.content || []);

// 3. Use filter() to create the new array containing ONLY unassigned jurists
const unassignedJuristData = juristsToIterate.filter((jurist) => {
    // Check if the current jurist's userId exists in the Set of assigned IDs.
    // If it DOES exist, the function returns false, and the jurist is excluded (removed).
    // If it DOES NOT exist, the function returns true, and the jurist is kept (unassigned).
    return !assignedJuristIds.has(jurist.userId);
});

    const juristDataHtml = unassignedJuristData.map(jurist => {
    
        return `
            <div class="checkbox-row" style="display: flex; align-items: center; gap: 10px; margin-bottom: 5px;">
                <input type="checkbox" id="jurist_${jurist.userId}" name="juristIds" value="${jurist.userId}"> 
                <label for="jurist_${jurist.userId}" style="margin: 0; color: black;">${jurist.name}</label>
            </div>
        `;
    }).join('');

    

    // --- 3. Render HTML ---
    const overlayHtml = `
        <div class="editFrame">
            <form id="updateCaseForm">
                <input type="hidden" id="id" name="id">
                <label for="name">Sagsnavn</label>
                <input type="text" id="name" name="name">
                <label for="responsibleUsername">Sagsansvarlig</label>
                <input type="text" id="responsibleUsername" name="responsibleUsername" placeholder="Brugernavn">
                <label for="idPrefix">ID</label>
                <input type="text" id="idPrefix" name="idPrefix" placeholder="f.eks. 9573">
                <label>Medarbejdere</label>
                <div class="employeeBox" style="max-height: 150px; overflow-y: auto; border: 1px solid #ccc; padding: 10px; background: #f9f9f9;">
                    ${caseAssigneesHtml}
                    ${juristDataHtml}
                </div>
                <button type="submit" class="inverted">Gem</button>
            </form>
        </div>
    `;

    const overlay = document.createElement("div");
    overlay.classList.add("editFrameBackground");
    overlay.innerHTML = overlayHtml;

    document.body.appendChild(overlay);

    // --- 4. Populate Data & Set Listeners ---
    const dataToIterate = Array.isArray(caseData) ? caseData : caseData.content;
    const existingCase = dataToIterate.find(c => c.id === id);

    if (existingCase) {
        document.getElementById("id").value = existingCase.id;
        document.getElementById("name").value = existingCase.name || "";
        document.getElementById("idPrefix").value = existingCase.idPrefix || "";
        document.getElementById("responsibleUsername").value = existingCase.responsibleUser.username || ""; 
    }
    
    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
            removeOverlay();
        }
    });

    document.addEventListener('keydown', handleEscapeKey);

    document.getElementById("updateCaseForm").addEventListener("submit", (e) => {
        e.preventDefault();
        submitCaseUpdate();
    });
}


async function deleteCase(id) {

    if (!confirm("Er du sikker på, at du vil slette denne sag?")) {
        return;
    }

    try {
        const res = await postObjectAsJson(
            apiBase, 
            { "id": id }, 
            "DELETE",
            true,
            false
        );
        
        if (res && res.ok) {
            alert("Sagen blev slettet!"); 
            location.reload();
        } else {
            let status = res ? res.status : "No response";
            alert(`Der skete en fejl ved sletning. Status: ${status}`); 
        }
    } catch (error) {
        alert("Der skete en fejl ved sletning. Netværksfejl."); 
    }
}

async function submitCaseUpdate() {

    const checkedBoxes = document.querySelectorAll('input[name="juristIds"]:checked');
    
    // 2. --- EXTRACT, PARSE, AND FILTER IDs ---
    const selectedJuristIds = Array.from(checkedBoxes)
        // Map: Get the value (which is the jurist.userId) from each checked box.
        .map(box => parseInt(box.value, 10))
        // Filter: Ensure no NaN (Not a Number) values are included.
        .filter(id => !isNaN(id));

    const updatedCase = {
        id: parseInt(document.getElementById("id").value, 10),
        name: document.getElementById("name").value,
        idPrefix: parseInt(document.getElementById("idPrefix").value, 10),
        responsibleUsername: document.getElementById("responsibleUsername").value,
        assigneeIds: selectedJuristIds
    };

    console.log(updatedCase);

    try {
        const res = await postObjectAsJson(
            apiBase, 
            updatedCase, 
            "PUT",
            true,
            false
        );
        
        if (res && res.ok) {
            alert("Sagen blev opdateret!");
            location.reload();
        } else {
            let status = res ? res.status : "No response";
            alert(`Der skete en fejl ved opdatering. Status: ${status}`);
        }
    } catch (error) {
        alert("Der skete en fejl ved opdatering. Netværksfejl.");
    }
}

async function submitCaseCreation() {
    // 1. Collect all checked checkboxes with name="juristIds"
    const checkedBoxes = document.querySelectorAll('input[name="juristIds"]:checked');
    
    // 2. Map the elements to their values (IDs) and convert to Integers
    const selectedJuristIds = Array.from(checkedBoxes)
        .map(box => parseInt(box.value, 10))
        // This line is crucial for preventing NaN
        .filter(id => !isNaN(id));


    const newCase = {
        name: document.getElementById("name").value,
        clientId: document.getElementById("clientId").value,
        idPrefix: parseInt(document.getElementById("idPrefix").value, 10),
        responsibleUsername: document.getElementById("responsibleUsername").value,
        assigneeIds: selectedJuristIds 
    };

    console.log(newCase);

    try {
        const res = await postObjectAsJson(
            apiBase, 
            newCase, 
            "POST",
            true,
            false
        );
        
        if (res && res.ok) {
            alert("Sagen blev opdateret!");
            location.reload();
        } else {
            let status = res ? res.status : "No response";
            alert(`Der skete en fejl ved opdatering. Status: ${status}`);
        }
    } catch (error) {
        alert("Der skete en fejl ved opdatering. Netværksfejl.");
    }
}


function removeOverlay() {
    const overlay = document.querySelector(".editFrameBackground");
    if (overlay) {
        overlay.remove();
    }
    document.removeEventListener('keydown', handleEscapeKey);
}

function handleEscapeKey(e) {
    if (e.key === 'Escape') {
        removeOverlay();
    }
}


window.addEventListener('DOMContentLoaded', main);
window.addEventListener('load', checkAuth);
window.addEventListener('pageshow', checkAuth);

