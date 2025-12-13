async function loadNavbar() {
    const navbar = document.getElementById("navbar");
    if (!navbar) return;

    const token = localStorage.getItem("token");
    if (!token) {
        navbar.innerHTML = await fetch("/components/navbar-default.html").then(r => r.text());
        attachLogoutHandler();
        return;
    }

    let role = [];
    try {
        const res = await fetch("http://127.0.0.1:8080/auth/me", {
            headers: {
                "Authorization": "Bearer " + token
            }
        });

        if (res.ok) {
            const user = await res.json();
            role = user.role || [];
        }
    } catch (err) {
        console.error("Error fetching user info:", err);
    }

    let file = "";
    if (role.includes("ROLE_ADMIN")) file = "navbar-admin.html";
    else if (role.includes("ROLE_PARTNER")) file = "navbar-partner.html";
    else if (role.includes("ROLE_SAGSBEHANDLER")) file = "navbar-sagsbehandler.html";
    else if (role.includes("ROLE_JURIST")) file = "navbar-jurist.html";

    navbar.innerHTML = await fetch(`/components/navbar/${file}`).then(r => r.text());

    // ⬅ IMPORTANT: run AFTER navbar is injected
    attachLogoutHandler();
}

function attachLogoutHandler() {
    const logoutButton = document.querySelector('#logout');
    if (!logoutButton) return;

    logoutButton.addEventListener('click', () => {
        localStorage.removeItem("token");
        window.location.href = "/pages/login/login.html"; // optional redirect
    });
}

loadNavbar();
