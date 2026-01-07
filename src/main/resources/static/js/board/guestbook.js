// 조회
let currentGuestbookPage = 0;

// 방명록 목록 조회
function loadGuestbooks(page)
{
    if (page == null || page < 0) page = currentGuestbookPage;
    currentGuestbookPage = page;

    const url = `/api/guestbook?page=${encodeURIComponent(page)}`;

    fetchJson(url, { method: "GET" },
    {
        defaultErrorMessage: "방명록 목록을 불러오는 중 오류가 발생했습니다.",
        toastOnSuccess: null,
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;

        renderGuestbookList(data.content || []);

        const pager = document.querySelector("[data-pagination]");
        if (pager) { renderPagination(pager, data.number, data.totalPages, loadGuestbooks); }
    });
}

// 렌더링
function renderGuestbookList(items)
{
    const list = document.querySelector(".guestbookList");
    if (!list) return;

    if (!items || items.length === 0)
    { list.innerHTML = `<p class="text1 text-center">방명록이 없습니다.</p>`; return; }

    list.innerHTML = items.map(g => renderGuestbookItem(g)).join("");
}

// 렌더링
function renderGuestbookItem(g)
{
    const id = g.guestbookId ?? g.id;

    const userIdRaw = g.userId ?? "";
    const userId = escapeHtml(userIdRaw);
    const userName = escapeHtml(g.userName ?? "");
    const writerText = `${userName}(${userId})`;

    const profileHref = userIdRaw ? `/user/${encodeURIComponent(userIdRaw)}` : "javascript:void(0)";

    const createdAt = formatDateTime(g.createdAt);
    const content = escapeHtml(g.content ?? "");

    const canAnswer = g.canAnswer === true;
    const canDeleteGuestbook = g.canDeleteGuestbook === true;

    const hasAnswer = (g.answerContent != null) && String(g.answerContent).trim().length > 0;
    const answerAt = hasAnswer ? formatDateTime(g.answerAt) : "";
    const answerContent = hasAnswer ? escapeHtml(g.answerContent) : "";
    const canDeleteAnswer = g.canDeleteAnswer === true;

    const answerBtn = (canAnswer && !hasAnswer) ? `<button class="btn" onclick="toggleForm(this)">답변</button>` : ``;
    const deleteBtn = canDeleteGuestbook ? `<button class="btn" onclick="deleteGuestbook(${id})">삭제</button>` : ``;
    const deleteAnswerBtn = (hasAnswer && canDeleteAnswer) ? `<button class="btn" onclick="deleteAnswer(${id})">삭제</button>` : ``;

    const answerFormHtml = (canAnswer && !hasAnswer) ? `
        <form class="guestbookForm" name="answerForm" style="display:none;">
            <div class="guestFormHeader flex justify-between">
                <p class="title3Text bold">방명록 답변하기</p>
                <button type="button" class="btn" onclick="subAnswer(this, ${id})">등록</button>
            </div>
            <textarea class="guestbookContent" name="answerForm"></textarea>
        </form>
    ` : ``;

    // 답변 표시 블록
    const answerViewHtml = hasAnswer ? `
        <div class="guestbook flex flex-column gap-sm pd-md">
            <div class="guestbookInfo">
                <a class="title3Text bold" href="/user/blank0914">공백</a>
                <div class="manualBtns flex gap-sm justify-end">
                    ${deleteAnswerBtn}
                </div>
                <p class="text1 black2">${answerAt}</p>
            </div>
            <p class="text1">${answerContent}</p>
        </div>
    ` : ``;

    return `
        <div class="guestbookItem flex flex-column gap-sm" data-guestbook-id="${id}">
            <div class="guestbook flex flex-column gap-sm pd-md">
                <div class="guestbookInfo">
                    <a class="title3Text bold" href="${profileHref}">${writerText}</a>
                    <div class="manualBtns flex gap-sm justify-end">
                        ${answerBtn}
                        ${deleteBtn}
                    </div>
                    <p class="text1 black2">${createdAt}</p>
                </div>
                <p class="text1">${content}</p>
            </div>

            ${answerFormHtml}
            ${answerViewHtml}
        </div>
    `;
}

// 등록
function subGuestbook()
{
    const textarea = document.getElementById("guestbookContent");
    if (!textarea) return;

    if (!checkEl(textarea, "내용")) return;
    if (!checkMaxLengthEl(textarea, "내용", 500)) return;

    const content = getValueEl(textarea);

    const secretCheckbox = document.getElementById("guestbookSecret");
    const secret = secretCheckbox ? secretCheckbox.checked : false;

    const payload = { content, secret };

    postJson("/api/guestbook", payload,
    {
        defaultErrorMessage: "방명록 등록 중 오류가 발생했습니다.",
        toastOnSuccess: "방명록이 등록됐습니다.",
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;

        textarea.value = "";

        if (typeof loadGuestbooks === "function") { loadGuestbooks(0); }
    });
}

// 답변 폼 토글
function toggleForm(btn)
{
    const item = btn.closest(".guestbookItem");
    const form = item?.querySelector("form.guestbookForm[name='answerForm']");
    if (!form) return;

    const willOpen = getComputedStyle(form).display === "none";
    form.style.display = willOpen ? "block" : "none";

    if (willOpen) { form.querySelector("textarea.guestbookContent")?.focus(); }
}

function deleteGuestbook(guestbookId)
{
    if (!confirm("방명록을 삭제하시겠습니까?")) return;

    fetchJson(`/api/guestbook/${guestbookId}`,
        { method: "DELETE" },
        {
            defaultErrorMessage: "방명록 삭제 중 오류가 발생했습니다.",
            toastOnSuccess: "방명록이 삭제되었습니다.",
            parseJson: false
        }
    )
    .then(() => { if (typeof loadGuestbooks === "function") { loadGuestbooks(0); } });
}

// 답변 등록
function subAnswer(btn, guestbookId)
{
    const realBtn = btn || window.event?.target;
    if (!realBtn) return;

    const form = realBtn.closest("form.guestbookForm[name='answerForm']");
    if (!form) return;

    const textarea = form.querySelector("textarea.guestbookContent");
    if (!textarea) return;

    if (!checkEl(textarea, "답변 내용")) return;
    if (!checkMaxLengthEl(textarea, "답변 내용", 500)) return;

    const content = getValueEl(textarea);

    const payload = { content };

    postJson(`/api/guestbook/${guestbookId}/answer`, payload,
    {
        defaultErrorMessage: "답변 등록 중 오류가 발생했습니다.",
        toastOnSuccess: "답변이 등록됐습니다.",
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;

        textarea.value = "";
        form.style.display = "none";

        if (typeof loadGuestbooks === "function") { loadGuestbooks(0);  }
    });
}

// 답변 삭제
function deleteAnswer(guestbookId)
{
    if (!confirm("답변을 삭제하시겠습니까?")) return;

    fetchJson(`/api/guestbook/${guestbookId}/answer`,
    { method: "DELETE" },
    {
        defaultErrorMessage: "답변 삭제 중 오류가 발생했습니다.",
        toastOnSuccess: "답변이 삭제됐습니다."
    })
    .then(() => { if (typeof loadGuestbooks === "function") { loadGuestbooks(0); } });
}

// 이벤트 리스너
document.addEventListener("DOMContentLoaded", () =>
{
    const hasList = document.querySelector(".guestbookList");
    const hasPager = document.querySelector("[data-pagination]");
    if (hasList && hasPager) { loadGuestbooks(0); }
});