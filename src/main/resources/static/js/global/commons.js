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

addEventListener("DOMContentLoaded", () => { applyAuthGreeting(); });