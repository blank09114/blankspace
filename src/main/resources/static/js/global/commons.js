// 드로어 열기
function openDrawer()
{
    const drawer = document.querySelector('.drawer');
    drawer.style.right = '0';
    document.body.style.overflow = 'hidden';
}

// 드로어 닫기
function closeDrawer()
{
    const drawer = document.querySelector('.drawer');
    drawer.style.right = '-100%';
    document.body.style.overflow = ''; 
}

// 스크롤 이동
function toScroll(direction)
{
    if (direction === 'up') { window.scrollTo({ top: 0, behavior: 'smooth' }); }
    else if (direction === 'down') { window.scrollTo({ top: document.body.scrollHeight, behavior: 'smooth' }); }
}

// 토스트 전역 변수
let toastTimer = null;
let progressTimer = null;
let remainingTime = 5000;
let startTime = null;
let isPaused = false;

// 토스트 호출
function showToast(text)
{
    const toast = document.querySelector(".toast");
    const toastText = document.getElementById("toastText");
    const progressBar = document.querySelector(".progressBar");

    // 기존 토스트 종료
    closeToast();

    // 토스트 표시
    toastText.textContent = text;
    toast.style.transform = "translateX(0)";

    // 초기화
    remainingTime = 5000;
    progressBar.style.width = "100%";

    // 시작 시각
    startTime = Date.now();
    isPaused = false;

    // 프로그레스 바 감소
    progressTimer = setInterval(() =>
    {
        if (isPaused) return;

        const elapsed = Date.now() - startTime;
        const left = remainingTime - elapsed;

        const percent = Math.max((left / 5000) * 100, 0);
        progressBar.style.width = percent + "%";

        if (left <= 0) { closeToast(); }
    }, 50);

    // 자동 종료 타이머
    toastTimer = setTimeout(() => { closeToast(); }, remainingTime);

    // 호버 시 타이머 정지
    toast.onmouseenter = () =>
    {
        if (isPaused) return;

        isPaused = true;

        const elapsed = Date.now() - startTime;
        remainingTime = Math.max(remainingTime - elapsed, 0);

        clearTimeout(toastTimer);
        toastTimer = null;
    };

    // 호버 해제 시 타이머 재개
    toast.onmouseleave = () =>
    {
        if (!isPaused) return;

        isPaused = false;
        startTime = Date.now();

        clearTimeout(toastTimer);
        toastTimer = setTimeout(() => { closeToast(); }, remainingTime);
    };
}

// 토스트 닫기
function closeToast()
{
    const toast = document.querySelector(".toast");
    const progressBar = document.querySelector(".progressBar");

    toast.style.transform = "translateX(100%)";

    clearTimeout(toastTimer);
    clearInterval(progressTimer);

    toastTimer = null;
    progressTimer = null;

    remainingTime = 5000;
    startTime = null;
    isPaused = false;

    if (progressBar) { progressBar.style.width = "0%"; }
}

// 닉네임 불러오기
function applyAuthGreeting()
{
    fetch("/api/auth/me", { credentials: "include" }).then(res =>
    {
        if (!res.ok) return null;
        return res.json();
    })
    .then(data =>
    {
        if (!data || !data.userName) return;

        const targets = document.querySelectorAll("#authGreetingName");
        targets.forEach(el => { el.textContent = data.userName; });
    })
    .catch(() => { });
}

// 로그아웃
function logout()
{
    fetch("/api/auth/logout", {
        method: "POST",
        credentials: "include"
    })
    .then(() => { location.href = "/?logout=1"; })
    .catch(() => { location.href = "/?logout=1"; });
}

// API 응답 실패
async function handleApiError(res, defaultMessage = "요청 처리 중 문제가 발생했습니다.")
{
    if (res.status === 429) {  showToast("잠시 후 다시 시도해주세요."); return; } // 레이트 리밋
    try
    {
        const data = await res.json();
        if (data && data.message) { showToast(data.message); return; }
    }
    catch (_) { }

    showToast(defaultMessage);
}

// JSON API 래퍼
async function fetchJson(url, options = {}, {
    defaultErrorMessage = "요청 처리 중 문제가 발생했습니다.",
    toastOnSuccess = null, parseJson = true
} = {})
{
    const opts = { credentials: "include", ...options };
    if (opts.body && typeof opts.body === "string")
    { opts.headers = { "Content-Type": "application/json", ...(opts.headers || {}) }; }

    try
    {
        const res = await fetch(url, opts);

        if (!res.ok) { await handleApiError(res, defaultErrorMessage); return null; }

        if (toastOnSuccess) showToast(toastOnSuccess);

        if (!parseJson) return { ok: true };

        const ct = (res.headers.get("content-type") || "").toLowerCase();
        if (!ct.includes("application/json")) return { ok: true };

        try { return await res.json(); } catch (_) { return { ok: true }; }
    }
    catch (_) { showToast("네트워크 오류가 발생했습니다."); return null; }
}

// POST JSON 편의 함수
function postJson(url, bodyObj, opts = {})
{ return fetchJson(url, { method: "POST", body: JSON.stringify(bodyObj) }, opts); }

// 날짜 포맷
function formatDate(dateStr)
{
    if (!dateStr) return "";

    const d = new Date(dateStr);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");

    return `${yyyy}.${mm}.${dd}.`;
}

function formatDateTime(dateStr)
{
    if (!dateStr) return "";

    const d = new Date(dateStr);
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    const hh = String(d.getHours()).padStart(2, "0");
    const mi = String(d.getMinutes()).padStart(2, "0");

    return `${yyyy}.${mm}.${dd}. ${hh}:${mi}`;
}

// XSS 방지
function escapeHtml(str)
{
    return String(str)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

// 공통 페이지 유틸
function renderPagination(container, currentPage, totalPages, onPageClick)
{
    if (!container) return;

    container.innerHTML = "";

    const createBtn = (label, page, opts = {}) =>
    {
        const { isNow = false, disabled = false } = opts;

        const a = document.createElement("a");
        a.className = "page" + (isNow ? " now" : "") + (disabled ? " disabled" : "");
        a.textContent = label;
        a.href = "javascript:void(0)";

        if (!disabled) { a.onclick = () => onPageClick(page); }
        else
        {
            a.onclick = e => e.preventDefault();
            a.setAttribute("aria-disabled", "true");
            a.setAttribute("tabindex", "-1");
        }
        return a;
    };

    // 이전
    const prevDisabled = (totalPages <= 1) || (currentPage <= 0);
    container.appendChild(createBtn("<", Math.max(0, currentPage - 1), { disabled: prevDisabled }));

    // 번호
    if (totalPages <= 1)
    { container.appendChild(createBtn("1", 0, { isNow: true, disabled: true })); }
    else
    {
        const maxVisible = 5;

        let start = currentPage - 2;
        let end   = currentPage + 2;

        // 1차 보정
        if (start < 0) { end += -start; start = 0; }

        if (end > totalPages - 1) { start -= (end - (totalPages - 1)); end = totalPages - 1; }

        // 2차 보정 (음수 방지)
        start = Math.max(0, start);

        for (let i = start; i <= end; i++)
        { container.appendChild(createBtn(String(i + 1), i, { isNow: i === currentPage })); }
    }

    // 다음
    const nextDisabled = (totalPages <= 1) || (currentPage >= totalPages - 1);
    container.appendChild(createBtn(">", Math.min(totalPages - 1, currentPage + 1), { disabled: nextDisabled }));
}

// 콘솔 이스터에그
function consoleEasterEgg()
{
    console.group("%c🐛 Debugging Log", "color: #34495E;");
    console.log("이 글을 발견한 개발자는 오늘도 알 수 없는 빌드 에러를 만나고,");
    console.log("개발한 기능은 예상치 못한 방향으로 동작할 것이며,");
    console.log("디버깅을 하고 나면 잘 작동하던 다른 기능이 오작동하게 되고,");
    console.log("해당 기능을 디버깅하면 최초 수정한 기능이 다시 오작동할 것입니다.");
    console.log("%c(제가 자주 그럽니다.)", "color: #C2C8D0; font-style: italic;");
    console.groupEnd();
}

addEventListener("DOMContentLoaded", () =>
{
    applyAuthGreeting();
    consoleEasterEgg();
});