// Simple GET request
async function fetchAnyUrl(url, useToken = false) {
    const headers = {};

    if (useToken) {
        const token = localStorage.getItem("token")?.trim();
        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }
    }

    try {
        const response = await fetch(realURL() + url, { headers });
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error("Handled error xx:", error);
        return null;
    }
}


// POST/PUT/DELETE request with optional token
async function postObjectAsJson(url, object, HttpVerbum, useToken = false, basicAuth = false,) {
    let headers = {
        "Content-Type": "application/json"
    };
    if(basicAuth) {
        const credentials = btoa(`${object.username}:${object.password}`);

        headers = {
            'Authorization': `Basic ${credentials}`,
            "Content-Type": "application/json"
        };
    }

    if (useToken) {
        const token = localStorage.getItem("token").trim();
        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }
    }

    try {
        const response = await fetch(realURL() + url, {
            method: HttpVerbum,
            headers,
            body: JSON.stringify(object)
        });
        return response; // caller can check response.status
    } catch (error) {
        console.error("Handled error xx:", error);
        return null;
    }
}

function realURL() {
    return "http://localhost:8080";
    return "91.98.198.233";
}

export { postObjectAsJson, fetchAnyUrl };
