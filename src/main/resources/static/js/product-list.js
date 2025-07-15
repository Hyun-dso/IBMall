document.addEventListener("DOMContentLoaded", function() {
    console.log("✅ product-list.js loaded");

	setupDeleteConfirm();
	setupAddFormValidation();
});

// 삭제 버튼: confirm 창 띄우기
function setupDeleteConfirm() {
    const deleteForms = document.querySelectorAll(".delete-form");
    deleteForms.forEach(form => {
        form.addEventListener("submit", event => {
            const confirmed = confirm("정말 삭제하시겠습니까?");
            if (!confirmed) {
                event.preventDefault();
            }
        });
    });
}

// 추가 폼 유효성 검사
function setupAddFormValidation() {
    const addForm = document.querySelector(".add-form");
    if (!addForm) return;

    addForm.addEventListener("submit", event => {
        const name = addForm.querySelector("input[name='name']").value.trim();
        const price = parseFloat(addForm.querySelector("input[name='price']").value);
        const stock = parseInt(addForm.querySelector("input[name='stock']").value);

        if (!name) {
            alert("상품명을 입력해주세요.");
            event.preventDefault();
            return;
        }
        if (isNaN(price) || price < 0) {
            alert("가격은 0 이상 숫자여야 합니다.");
            event.preventDefault();
            return;
        }
        if (isNaN(stock) || stock < 0) {
            alert("재고는 0 이상 숫자여야 합니다.");
            event.preventDefault();
        }
    });
}