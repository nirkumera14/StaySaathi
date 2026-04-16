const adminKeyInput = document.getElementById("adminKeyInput");
const loadButton = document.getElementById("loadAdminData");
const adminMessage = document.getElementById("adminMessage");
const adminMetrics = document.getElementById("adminMetrics");
const inquiryRows = document.getElementById("adminInquiryRows");

let adminKey = "";

function withHeaders() {
    return {
        "X-Admin-Key": adminKey,
        "Content-Type": "application/json"
    };
}

function renderMetrics(metrics) {
    const cards = [
        ["Listings", metrics.totalListings],
        ["Inquiries", metrics.totalInquiries],
        ["New", metrics.newInquiries],
        ["Contacted", metrics.contactedInquiries],
        ["Closed", metrics.closedInquiries]
    ];

    adminMetrics.innerHTML = cards
        .map(([label, value]) => `<article class="metric-card"><span>${label}</span><strong>${value}</strong></article>`)
        .join("");
}

function statusSelect(id, status) {
    const options = ["NEW", "CONTACTED", "CLOSED"]
        .map((item) => `<option value="${item}" ${item === status ? "selected" : ""}>${item}</option>`)
        .join("");
    return `<select data-status-id="${id}">${options}</select>`;
}

function renderInquiries(rows) {
    inquiryRows.innerHTML = rows
        .map((row) => `
            <tr>
                <td>#${row.id}</td>
                <td>${row.listingTitle}</td>
                <td>${row.name}</td>
                <td>${row.phone}</td>
                <td>${row.budget ? `?${row.budget.toLocaleString("en-IN")}` : "-"}</td>
                <td>${row.expectedStayMonths ? `${row.expectedStayMonths} mo` : "-"}</td>
                <td>${statusSelect(row.id, row.status)}</td>
                <td>${new Date(row.createdAt).toLocaleString()}</td>
            </tr>
        `)
        .join("");

    inquiryRows.querySelectorAll("select[data-status-id]").forEach((select) => {
        select.addEventListener("change", async (event) => {
            const inquiryId = event.target.dataset.statusId;
            const next = event.target.value;
            await updateStatus(inquiryId, next);
        });
    });
}

async function updateStatus(id, status) {
    const response = await fetch(`/api/admin/inquiries/${id}/status?status=${status}`, {
        method: "PATCH",
        headers: withHeaders()
    });
    if (!response.ok) {
        adminMessage.textContent = "Could not update status.";
        return;
    }
    adminMessage.textContent = `Inquiry #${id} updated to ${status}.`;
    await loadDashboard();
}

async function loadDashboard() {
    try {
        adminMessage.textContent = "Loading...";
        const [metricsRes, inquiriesRes] = await Promise.all([
            fetch("/api/admin/metrics", { headers: withHeaders() }),
            fetch("/api/admin/inquiries", { headers: withHeaders() })
        ]);

        if (!metricsRes.ok || !inquiriesRes.ok) {
            throw new Error("Invalid key or API error");
        }

        const metrics = await metricsRes.json();
        const inquiries = await inquiriesRes.json();

        renderMetrics(metrics);
        renderInquiries(inquiries);
        adminMessage.textContent = `Loaded ${inquiries.length} inquiry records.`;
    } catch (error) {
        adminMessage.textContent = error.message;
        adminMetrics.innerHTML = "";
        inquiryRows.innerHTML = "";
    }
}

loadButton?.addEventListener("click", async () => {
    adminKey = adminKeyInput.value.trim();
    if (!adminKey) {
        adminMessage.textContent = "Admin key is required.";
        return;
    }
    await loadDashboard();
});