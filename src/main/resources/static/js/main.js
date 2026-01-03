// 애니메이션
const inVp = (el, off = 80) => { const r = el.getBoundingClientRect(); return r.top < innerHeight - off && r.bottom > off; };
const bindRv = (sel = ".rv", { off = 80 } = {}) =>
{
    const els = [...document.querySelectorAll(sel)];

    const tick = () => { for (const el of els) el.classList.toggle("in", inVp(el, off)); };

    tick();
    addEventListener("scroll", tick, { passive: true });
    addEventListener("resize", tick);

    return () => { removeEventListener("scroll", tick); removeEventListener("resize", tick); };
};

// 순차적으로 등장
const seq = (sel, step = 120, base = 0) =>
{ document.querySelectorAll(sel).forEach((el, i) => { el.style.setProperty("--d", `${base + i * step}ms`); }); };

// 이벤트 리스너
addEventListener("DOMContentLoaded", () =>
{
    // 애니메이션 적용
    seq(".newNovelList .newNovel.rv", 120, 0); bindRv(".rv", { off: 80 });

    // 인증 알림
    const params = new URLSearchParams(window.location.search);

    if (params.get("joined") === "1")
    {
        showToast("회원가입이 완료됐습니다.");
        history.replaceState({}, "", window.location.pathname);
    }
    if (params.get("login") === "1")
    {
        fetch("/api/auth/me", { credentials: "include" })
        .then(res => res.json())
        .then(data =>
        { if (data && data.userName) { showToast(`${data.userName}님, 환영합니다.`); } })
        .finally(() => { history.replaceState({}, "", window.location.pathname); });
    }
    if (params.get("logout") === "1")
    {
        showToast("로그아웃 됐습니다.");
        history.replaceState({}, "", window.location.pathname);
    }
});