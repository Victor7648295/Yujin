console.log("SCRIPT LOADED");

document.addEventListener("DOMContentLoaded", () => {

    const overlay = document.getElementById("modalOverlay");
    const callModal = document.getElementById("callModal");
    const logoutModal = document.getElementById("logoutModal");

    const callBtn = document.getElementById("callBtn");
    const logoutLink = document.querySelector(".logout-link");

    const closeCallModalBtn = document.getElementById("closeCallModalBtn");
    const cancelLogoutBtn = document.getElementById("cancelLogoutBtn");
    const confirmLogoutBtn = document.getElementById("confirmLogoutBtn");

    // ===== Открытие звонка =====
    callBtn?.addEventListener("click", () => {
        overlay.style.display = "block";
        callModal.style.display = "flex";
    });

    // ===== Кнопки "Показать телефон" в карточках объявлений =====
    const phoneNumberEl = document.getElementById("modalPhoneNumber");
    const sellerNameEl = document.getElementById("modalUserName");
    document.querySelectorAll(".show-phone-btn").forEach((btn) => {
        btn.addEventListener("click", () => {
            const phone = btn.dataset.phone || "";
            const seller = btn.dataset.seller || "Продавец";
            if (phoneNumberEl) phoneNumberEl.textContent = phone;
            if (sellerNameEl) sellerNameEl.textContent = seller;
            if (overlay) overlay.style.display = "block";
            if (callModal) callModal.style.display = "flex";
        });
    });

    // ===== Открытие выхода =====
    logoutLink?.addEventListener("click", () => {
        overlay.style.display = "block";
        logoutModal.style.display = "flex";
    });

    // ===== Закрытие =====
    function closeModal() {
        overlay.style.display = "none";
        callModal.style.display = "none";
        logoutModal.style.display = "none";
    }

    overlay?.addEventListener("click", closeModal);
    closeCallModalBtn?.addEventListener("click", closeModal);
    cancelLogoutBtn?.addEventListener("click", closeModal);

    // ===== Подтверждение выхода =====
    confirmLogoutBtn?.addEventListener("click", () => {
        window.location.href = "/logout";
    });

});







