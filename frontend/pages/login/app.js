const form = document.querySelector(".login_form");


form?.addEventListener("submit", async (event) => {
    event.preventDefault();

    document.querySelector(".wrong-password")?.remove();

    const creds = Object.fromEntries(new FormData(form));

    try {
        const res = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-type": "application/json"
            },
            body: JSON.stringify(creds)
        });

        if (!res.ok) {
            throw new Error(`Login failed: ${res.status}`);
        }

        const {username, token, role} = await res.json();
        if (!token) throw new Error("Intet token i respons");

        localStorage.setItem("token", token);
        /* localStorage.setItem("username", username);
        localStorage.setItem("role", role); */

        
        location.replace("http://127.0.0.1:5500/pages/dashboard/index.html") // <---- not optimal just for test
    } catch (err) {
        console.error(err);
    }
});

function getToken() {
    return localStorage.getItem("token");
}

function getUsername() {
    return localStorage.getItem("username");
}

function getRole() {
    return localStorage.getItem("role");
}
