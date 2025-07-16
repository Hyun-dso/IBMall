document.addEventListener("DOMContentLoaded", function() {
    console.log("✅ product-list.js loaded");

    setupDeleteConfirm();
    setupAddFormValidation();
    setupUploadButtons();
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

// ✅ 이미지 업로드 버튼 설정 (list.html, detail.html 공용)
function setupUploadButtons() {
    const listUploadBtn = document.querySelector("#listUploadBtn");
    const detailUploadBtn = document.querySelector("#detailUploadBtn");

    if (listUploadBtn) {
        listUploadBtn.addEventListener("click", () => uploadImages("fileInput", "previewArea", "thumbnailUrl"));
    }
    if (detailUploadBtn) {
        detailUploadBtn.addEventListener("click", () => uploadImages("fileInput", "previewArea", "thumbnailUrl"));
    }
}

// ✅ 공용 이미지 업로드 함수
function uploadImages(fileInputId, previewAreaId, thumbnailInputId) {
    const fileInput = document.getElementById(fileInputId);
    const files = fileInput.files;

    if (files.length === 0) {
        alert('업로드할 파일을 선택해주세요!');
        return;
    }

    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
        formData.append('files', files[i]);
    }

    fetch('/image/upload', {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.status === 'success') {
            const previewArea = document.getElementById(previewAreaId);
            previewArea.innerHTML = '';

            data.imageUrls.forEach((url, index) => {
                const img = document.createElement('img');
                img.src = url;
                img.className = 'preview-img';
                previewArea.appendChild(img);

                if (index === 0) {
                    document.getElementById(thumbnailInputId).value = url;
                }
            });

            alert('이미지 업로드 성공!');
        } else {
            alert('이미지 업로드 실패: ' + data.message);
        }
    })
    .catch(error => {
        console.error('업로드 실패:', error);
        alert('업로드 중 오류 발생!');
    });
}
