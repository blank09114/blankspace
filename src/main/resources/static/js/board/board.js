// 게시판 이름 가져오기
function getBoardName()
{
    const el = document.querySelector(".boardName");
    return (el?.textContent || "").trim();
}

// 카테고리 편집 폼 토글
function toggleForms()
{
    const forms = document.querySelector(".forms");
    const isMobile = window.innerWidth < 768;

    if (forms.style.display !== "none" && forms.style.display !== "") { forms.style.display = "none"; return; }
    forms.style.display = isMobile ? "flex" : "grid";
}

// 카테고리 추가
function addCate()
{
    const input = document.querySelector("form[name='categoryForm'] input[name='categoryInput']");
    const name = (input?.value || "").trim();
    const boardName = getBoardName();

    if (!name) { showToast("추가할 카테고리를 입력하세요."); input?.focus(); return; }
    if (!boardName) { showToast("게시판 정보를 불러오지 못했습니다."); return; }

    const payload = { name };

    postJson(`/api/board/${encodeURIComponent(boardName)}/category`, payload,
    {
        defaultErrorMessage: "카테고리 추가 중 오류가 발생했습니다.",
        toastOnSuccess: "카테고리를 추가했습니다.",
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;
        input.value = "";
        location.reload();
    });
}

// 카테고리 삭제
function deleteCate()
{
    const form = event.target.closest("form");
    const select = form.querySelector("select");

    if (select.selectedIndex === 0) { showToast("카테고리를 선택하세요."); return; }

    const categoryId = select.value;
    const boardName = getBoardName();

    if (!boardName) { showToast("게시판 정보를 불러오지 못했습니다."); return; }

    fetchJson(`/api/board/${encodeURIComponent(boardName)}/category/${categoryId}`,
    { method: "DELETE" },
    {
        defaultErrorMessage: "카테고리 삭제 중 오류가 발생했습니다.",
        toastOnSuccess: "카테고리를 삭제했습니다.",
        parseJson: false
    })
    .then((res) => { if (!res) return; location.reload(); });
}

// 카테고리 이름 변경
function editCate()
{
    const form = event.target.closest("form");
    const select = form.querySelector("select");
    const input = form.querySelector("input");

    if (select.selectedIndex === 0) { showToast("카테고리를 선택하세요."); return; }

    const categoryId = select.value;
    const after = input.value.trim();
    const boardName = getBoardName();

    if (!after)
    {
        showToast("변경할 이름을 입력하세요.");
        input.focus();
        return;
    }
    if (!boardName) { showToast("게시판 정보를 불러오지 못했습니다."); return; }

    fetchJson(`/api/board/${encodeURIComponent(boardName)}/category/${categoryId}`,
    { method: "PUT", body: JSON.stringify({ name: after }) },
    {
        defaultErrorMessage: "카테고리 변경 중 오류가 발생했습니다.",
        toastOnSuccess: "카테고리를 변경했습니다.",
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;
        input.value = "";
        location.reload();
    });
}

// 작성/수정
function subPost()
{
    const form = document.getElementById("postForm");
    if (!form) { console.error("form#postForm not found"); return; }

    const categoryEl = form.querySelector('[name="categoryId"]');
    const titleEl = form.querySelector('[name="title"]');
    const contentEl = form.querySelector('[name="content"]');

    if (!checkEl(categoryEl, "카테고리")) return;
    if (!checkEl(titleEl, "제목")) return;

    if (window.RichEditor && typeof window.RichEditor.sync === "function")
    { window.RichEditor.sync(); } else if (window.tinymce) { tinymce.triggerSave(); }

    if (!checkEl(contentEl, "내용")) return;
    HTMLFormElement.prototype.submit.call(form);
}