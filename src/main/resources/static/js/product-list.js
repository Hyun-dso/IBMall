document.addEventListener("DOMContentLoaded", function() {
    console.log("✅ product-list.js loaded");

    setupDeleteConfirm();
    setupAddFormValidation();
    setupUpdateButton();
    setupDetailLink();
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

// 수정 버튼: 현재는 alert만 (모달이나 별도 폼 연동 가능)
function setupUpdateButton() {
    const updateForms = document.querySelectorAll(".update-form");
    updateForms.forEach(form => {
        form.addEventListener("submit", event => {
            alert("수정 기능은 아직 구현되지 않았습니다. (백엔드 로직 필요)");
            // event.preventDefault();  // 수정 기능 막고 싶으면 주석 해제
        });
    });
}

// 상세 버튼: 링크 확인용 콘솔 출력 (막고 싶으면 preventDefault 추가)
function setupDetailLink() {
    const detailLinks = document.querySelectorAll(".detail-link");
    detailLinks.forEach(link => {
        link.addEventListener("click", event => {
            console.log("상세 페이지 이동: " + link.href);
            // 필요하면 event.preventDefault();
        });
    });
}
