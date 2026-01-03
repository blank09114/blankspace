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

// 애니메이션 적용
addEventListener("DOMContentLoaded", () => { seq(".newNovelList .newNovel.rv", 120, 0); bindRv(".rv", { off: 80 }); });