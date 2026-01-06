let currentCommentPage = 0;

// 댓글 대상 지정
function getCommentTarget()
{
    const root = document.querySelector(".commentMenu");
    const type = root?.dataset.targetType;
    const id   = root?.dataset.targetId;

    if (!type || !id) { showToast("댓글 대상 정보를 찾을 수 없습니다."); return null; }
    return { type, id };
}
function getCreateCommentUrl()
{
    const t = getCommentTarget();
    if (!t) return null;

    if (t.type === "post") return `/api/post/${t.id}/comment`;
    if (t.type === "episode") return `/api/episode/${t.id}/comment`;

    showToast("지원하지 않는 댓글 대상입니다.");
    return null;
}
function getCreateRecommentUrl(commentId)
{
    const t = getCommentTarget();
    if (!t) return null;

    if (t.type === "post") return `/api/post/${t.id}/comment/${commentId}/recomment`;
    if (t.type === "episode") return `/api/episode/${t.id}/comment/${commentId}/recomment`;

    showToast("지원하지 않는 댓글 대상입니다.");
    return null;
}

// 관리자 여부 확인
function isAdminUser()
{
    const root = document.querySelector(".commentMenu");
    return root?.dataset.isAdmin === "true";
}

// 로그인 여부 확인
function isAuthUser()
{ return document.querySelector(".commentMenu")?.dataset.isAuth === "true"; }

// 댓글 조회
function getListCommentUrl(page)
{
    const t = getCommentTarget();
    if (!t) return null;

    if (t.type === "post") return `/api/post/${t.id}/comment?page=${page}`;
    if (t.type === "episode") return `/api/episode/${t.id}/comment?page=${page}`;
    return null;
}
function loadComments(page) {
    const url = getListCommentUrl(page);
    if (!url) return;

    fetchJson(url, { method: "GET" },
    {
        defaultErrorMessage: "댓글을 불러오는 중 오류가 발생했습니다.",
        parseJson: true
    }).then(data =>
    {
        if (!data) return;

        renderCommentThreads(data.content || []);

        const pager = document.querySelector("[data-pagination]");
        if (pager) { renderPagination(pager, data.number, data.totalPages, loadComments); }

        setCommentCount(data.totalElements);
    });
}

// 렌더링
function renderCommentThreads(threads) {
    const list = document.querySelector(".commentList");
    if (!list) return;

    if (threads.length === 0) {
        list.innerHTML = `<p class="text1 text-center">댓글이 없습니다.</p>`;
        return;
    }

    list.innerHTML = threads.map(t => {
        const parent = renderComment(t.comment);
        const children = (t.recomments || [])
            .map(r => renderRecomment(r))
            .join("");

        return parent + children;
    }).join("");

    // 대댓글 폼 기본 숨김
    document.querySelectorAll('form[name="recommentForm"]').forEach(f => f.style.display = "none");
}
function renderComment(c) {
    const writer = escapeHtml(c.writerName);
    const content = escapeHtml(c.content);
    const time = formatDateTime(c.createdAt);
    const writerHref = `/user/${encodeURIComponent(c.writerId)}`;

    const mine = (c.isMine === true) || (c.mine === true);
    const deleteBtn = mine || isAdminUser()
      ? `<button class="btn" onclick="deleteComment(${c.commentId})">삭제</button>` : "";

    return `
    <div class="comment pd-sm flex flex-column gap-sm" data-comment-id="${c.commentId}">
        <p class="title3Text">
            <a class="bold" href="${writerHref}">${writer}</a>
            <span class="black2">· ${time}</span>
        </p>
        <div class="manualBtns flex gap-sm">
            ${isAuthUser() && !c.deleted? `<button class="btn" onclick="toggleRecommentForm(this)">답글</button>`: ``}
            ${deleteBtn}
        </div>
        <p class="commentContent text1 pd-xs">${content}</p>

        ${isAuthUser() && !c.deleted ? `
        <form class="commentForm flex flex-column gap-xs" name="recommentForm" data-mention-user-id="${escapeHtml(c.writerId)}">
            <p class="title3Text bold">대댓글 작성하기</p>
            <textarea class="commentInput" name="recommentInput"
                placeholder="500자 이내, 등록 후 수정이 불가합니다."></textarea>
            <button type="button" class="btn" onclick="subRecomment(this, ${c.commentId})">등록</button>
        </form>
        ` : ``}
    </div>`;
}
function renderRecomment(r)
{
    const author = escapeHtml(r.authorName);
    const content = escapeHtml(r.content);
    const time = formatDateTime(r.createdAt);
    const authorHref = `/user/${encodeURIComponent(r.authorId)}`;

    const mention = r.mentionUserName
    ? `<span class="navy1">@${escapeHtml(r.mentionUserName)}</span> `: "";

    const mine = (r.isMine === true) || (r.mine === true);
    const deleteBtn = mine || isAdminUser()
      ? `<button class="btn" onclick="deleteRecomment(${r.recommentId})">삭제</button>` : "";

    return `
    <div class="recomment flex gap-xs" data-recomment-id="${r.recommentId}" data-comment-id="${r.commentId}">
        <div class="recommentLine"><div class="recommentRing"></div></div>
        <div class="comment pd-sm flex flex-column gap-sm">
            <p class="title3Text">
                <a class="bold" href="${authorHref}">${author}</a>
                <span class="black2">· ${time}</span>
            </p>
            <div class="manualBtns flex gap-sm">
                ${isAuthUser() ? `<button class="btn" onclick="toggleRecommentForm(this)">언급</button>` : ``}
                ${deleteBtn}
            </div>
            <p class="commentContent text1 pd-xs">${mention}${content}</p>

            ${isAuthUser() ? `
            <form class="commentForm flex flex-column gap-xs" name="recommentForm" data-mention-user-id="${escapeHtml(r.authorId)}">
                <p class="title3Text bold">대댓글 작성하기</p>
                <textarea class="commentInput" name="recommentInput"
                    placeholder="500자 이내, 등록 후 수정이 불가합니다."></textarea>
                <button type="button" class="btn"
                    onclick="subRecomment(this, ${r.commentId})">등록</button>
            </form>
            ` : ``}
        </div>
    </div>`;
}

// 댓글 숫자 카운팅
function setCommentCount(n)
{
    const el = document.querySelector(".commentMenu .navy1");
    if (el) el.textContent = String(n).padStart(2, "0");
}

// 댓글 등록
function subComment()
{
    if (!check("commentInput", "내용")) return;
    if (!checkMaxLength("commentInput", "댓글", 500)) return;

    const url = getCreateCommentUrl();
    if (!url) return;

    const payload = { content: getValue("commentInput") };

    postJson(url, payload,
    {
        defaultErrorMessage: "댓글 등록 중 오류가 발생했습니다.",
        toastOnSuccess: "댓글을 등록했습니다.",
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;
        loadComments(-1);
    });
}

// 대댓글 등록 폼 토글
function toggleRecommentForm(btn)
{
    const comment = btn.closest(".comment");
    const form = comment?.querySelector('form[name="recommentForm"]');
    if (!form) return;

    const isOpen = getComputedStyle(form).display !== "none";

    document.querySelectorAll('form[name="recommentForm"]').forEach(f => f.style.display = "none");

    if (isOpen) { form.style.display = "none"; return; }

    form.style.display = "flex";
    form.querySelector("textarea")?.focus();
}

// 대댓글 등록
function subRecomment(btn, commentId)
{
    const form = btn.closest('form[name="recommentForm"]');
    const input = form?.querySelector('textarea[name="recommentInput"]');
    if (!input) return;

    if (!checkEl(input, "내용")) return;
    if (!checkMaxLengthEl(input, "댓글", 500)) return;

    const content = getValueEl(input);
    const mentionUserId = form?.dataset.mentionUserId || null;

    const url = getCreateRecommentUrl(commentId);
    if (!url) return;

    const payload = { content, mentionUserId };

    postJson(url, payload,
    {
        defaultErrorMessage: "대댓글 등록 중 오류가 발생했습니다.",
        toastOnSuccess: "대댓글을 등록했습니다.",
        parseJson: true
    })
    .then((data) =>
    {
        if (!data) return;

        loadComments(-1);

        input.value = "";
        form.style.display = "none";
    });
}

// 댓글 삭제
function deleteComment(commentId)
{
    if (!confirm("댓글을 삭제하시겠습니까?")) return;

    const t = getCommentTarget();
    if (!t) return;

    const force = isAdminUser() ? "?force=true" : "";
    const url =
        t.type === "post"
        ? `/api/post/${t.id}/comment/${commentId}${force}`
        : `/api/episode/${t.id}/comment/${commentId}${force}`;

    fetchJson(url, { method: "DELETE" },
    {
        defaultErrorMessage: "댓글 삭제 중 오류가 발생했습니다.",
        toastOnSuccess: "댓글을 삭제했습니다."
    }).then(() => { loadComments(-1); });
}

// 대댓글 삭제
function deleteRecomment(recommentId)
{
    if (!confirm("대댓글을 삭제하시겠습니까?")) return;

    const t = getCommentTarget();
    if (!t) return;

    const commentEl = document.querySelector(`[data-recomment-id="${recommentId}"]`);
    const commentId = commentEl?.dataset.commentId;
    if (!commentId) { showToast("댓글 정보를 찾을 수 없습니다."); return; }

    const url = t.type === "post"
        ? `/api/post/${t.id}/comment/${commentId}/recomment/${recommentId}`
        : `/api/episode/${t.id}/comment/${commentId}/recomment/${recommentId}`;

    fetchJson(url, { method: "DELETE" },
    {
        defaultErrorMessage: "대댓글 삭제 중 오류가 발생했습니다.",
        toastOnSuccess: "대댓글을 삭제했습니다."
    }).then(() => { loadComments(-1); });
}

// 이벤트 리스너
document.addEventListener("DOMContentLoaded", () =>
{
    const root = document.querySelector(".commentMenu");
    if (!root) return;

    const type = root.dataset.targetType;
    const id   = root.dataset.targetId;
    if (!type || !id) return;

    loadComments(-1);
});