const DEFAULT_THUMBNAIL = "/img/thumbnail_default.png";
let currentCategoryId = null;

// 게시판 이름 가져오기
function getBoardName()
{
    const el = document.querySelector(".boardName");
    return (el?.textContent || "").trim();
}

// 게시글 목록 불러오기
function bindCategoryButtons()
{
    const buttons = document.querySelectorAll(".categoryMenu .categories .category");

    buttons.forEach((btn) =>
    {
        if (btn.hasAttribute("onclick")) return;

        btn.addEventListener("click", () =>
        {
            buttons.forEach((b) => b.classList.remove("now"));
            btn.classList.add("now");
            const categoryIdAttr = btn.getAttribute("data-category-id");
            currentCategoryId = categoryIdAttr ? Number(categoryIdAttr) : null;
            loadPosts(0);
        });
    });
}

// 페이지 로드
function loadPosts(page)
{
    const boardName = getBoardName();
    if (!boardName) { showToast("게시판 정보를 불러오지 못했습니다."); return; }

    let url = `/api/board/${encodeURIComponent(boardName)}/posts?page=${page}`;
    if (currentCategoryId != null) url += `&categoryId=${encodeURIComponent(currentCategoryId)}`;

    fetchJson(url, { method: "GET" },
    {
        defaultErrorMessage: "게시글 목록을 불러오는 중 오류가 발생했습니다.",
        toastOnSuccess: null,
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;

        renderPostList(data.content || [], boardName);

        const pagination = document.querySelector("[data-pagination]");
        if (pagination) { renderPagination(pagination, data.number, data.totalPages, loadPosts); }
    });
}

// 렌더링
function renderPostList(items, boardName)
{
    const list = document.getElementById("postList") || document.querySelector(".postList");
    if (!list) return;

    if (!items || items.length === 0)
    {
        list.innerHTML = `<p class="text1 text-center">게시글이 없습니다.</p>`;
        return;
    }

    list.innerHTML = items.map((p) =>
    {
        const postId = p.postId ?? p.id;
        const title = escapeHtml(p.title ?? "");
        const subTitle = escapeHtml(p.subTitle ?? "");
        const thumb = (p.thumbnailUrl && String(p.thumbnailUrl).trim()) ? p.thumbnailUrl : DEFAULT_THUMBNAIL;

        const href = `/board/${encodeURIComponent(boardName)}/post/${postId}`;
        const subTitleHtml = subTitle ? `<p class="text1 text-center">${subTitle}</p>` : "";

        return `
            <a class="card pd-md" href="${href}">
                <img class="thumbnail" src="${thumb}" alt="thumbnail"
                     onerror="this.onerror=null; this.src='${DEFAULT_THUMBNAIL}';">
                <p class="title2Text text-center bold">${title}</p>
                ${subTitleHtml}
            </a>
        `;
    }).join("");
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

// 페이지 로드 시
document.addEventListener("DOMContentLoaded", () =>
{
    const hasList = document.querySelector(".postList") || document.getElementById("postList");
    const hasPagination = document.querySelector("[data-pagination]");
    const hasCategoryMenu = document.querySelector(".categoryMenu .categories");

    if (hasList && hasPagination && hasCategoryMenu)
    {
        bindCategoryButtons();
        loadPosts(0);
    }
});